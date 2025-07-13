package com.doubleo.didagent.service;

import com.doubleo.didagent.agent.HospitalAgent;
import com.doubleo.didagent.domain.domain.ConnectionStatus;
import com.doubleo.didagent.domain.domain.MemberConnection;
import com.doubleo.didagent.domain.repository.HospitalInvitationRepository;
import com.doubleo.didagent.domain.repository.MemberConnectionRepository;
import com.doubleo.didagent.dto.request.hospital.HospitalDidCreateRequest;
import com.doubleo.didagent.dto.request.hospital.HospitalVcIssueRequest;
import com.doubleo.didagent.global.exception.CommonException;
import com.doubleo.didagent.global.exception.errorcode.AcapyErrorCode;
import com.doubleo.didagent.grpc.client.HospitalTenantClient;
import com.doubleo.didagent.grpc.client.PassClient;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Slf4j
@Service
@RequiredArgsConstructor
public class AcapyWebhookService {

    private final PassClient passClient;
    private final HospitalAgent hospitalAgent;
    private final HospitalTenantClient hospitalTenantClient;
    private final MemberConnectionRepository memberConnectionRepository;
    private final HospitalInvitationRepository hospitalInvitationRepository;
    private final ObjectMapper objectMapper;

    public Mono<Void> processConnectionWebhook(Map<String, Object> payload) {
        String invitationId = (String) payload.get("invitation_msg_id");
        String connectionId = (String) payload.get("connection_id");
        String state = (String) payload.get("state");
        String walletId = (String) payload.get("x-wallet-id");
        String theirDid = (String) payload.get("their_did");

        log.info(
                "Processing connection webhook - connectionId: {}, state: {}, walletId: {}",
                connectionId,
                state,
                walletId);

        if (state.equals("active")) {
            log.debug(
                    "State is active. Initiating member connection and processing further steps.");
            return initMemberConnection(payload)
                    .then(createAndPostDid(connectionId))
                    .flatMap(did -> offerVc(connectionId, did, theirDid))
                    .then(deleteHospitalInvitation(invitationId));
        }

        return Mono.empty();
    }

    public Mono<Void> processOutOfBandWebhook(Map<String, Object> payload) {
        return Mono.fromRunnable(
                () -> {
                    String oobId = (String) payload.get("oob_id");
                    String connectionId = (String) payload.get("connection_id");
                    String state = (String) payload.get("state");

                    log.info(
                            "Processing out of band webhook - oobId: {}, connectionId: {}, state: {}",
                            oobId,
                            connectionId,
                            state);
                });
    }

    public Mono<Void> processCredentialWebhook(Map<String, Object> payload) {
        String credExId = (String) payload.get("cred_ex_id");
        String connectionId = (String) payload.get("connection_id");
        String state = (String) payload.get("state");
        String threadId = (String) payload.get("thread_id");

        log.info(
                "Processing credential webhook - credExId: {}, connectionId: {}, state: {}, threadId: {}",
                credExId,
                connectionId,
                state,
                threadId);

        if ("credential-issued".equals(state)) {
            return processCredentialIssued(credExId, connectionId, payload);
        }

        return Mono.empty();
    }

    private Mono<Void> deleteHospitalInvitation(String invitationId) {
        return Mono.fromRunnable(
                        () ->
                                hospitalInvitationRepository.deleteHospitalInvitationByInvitationId(
                                        invitationId))
                .subscribeOn(Schedulers.boundedElastic())
                .then();
    }

    private Mono<Void> initMemberConnection(Map<String, Object> payload) {
        return Mono.fromCallable(
                        () -> {
                            String invitationId = (String) payload.get("invitation_msg_id");
                            return hospitalInvitationRepository
                                    .findByInvitationId(invitationId)
                                    .orElseThrow(
                                            () ->
                                                    new CommonException(
                                                            AcapyErrorCode.INVITATION_NOT_FOUND));
                        })
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(
                        invitation -> {
                            String connectionId = (String) payload.get("connection_id");
                            log.debug("connectionId: {}", connectionId);

                            MemberConnection connection =
                                    MemberConnection.createMemberConnection(
                                            connectionId,
                                            invitation.getTenantId(),
                                            invitation.getPassId(),
                                            invitation.getMemberId());

                            return Mono.fromCallable(
                                            () -> memberConnectionRepository.save(connection))
                                    .subscribeOn(Schedulers.boundedElastic())
                                    .doOnNext(
                                            saved ->
                                                    log.info(
                                                            "Created connection for connectionId: {}",
                                                            connectionId))
                                    .then();
                        });
    }

    private Mono<String> createAndPostDid(String connectionId) {
        return getMemberConnectionReactive(connectionId)
                .filter(connection -> connection.getConnectionStatus() == ConnectionStatus.ACTIVE)
                .switchIfEmpty(Mono.error(new CommonException(AcapyErrorCode.DID_PROCESS_FAILED)))
                .flatMap(
                        connection ->
                                hospitalAgent
                                        .createHospitalDid(
                                                HospitalDidCreateRequest.didKey(),
                                                hospitalTenantClient
                                                        .getTokenByTenantId(
                                                                connection.getTenantId())
                                                        .getWalletToken())
                                        .map(response -> response.result().did())
                                        .doOnNext(
                                                hospitalDid ->
                                                        log.info(
                                                                "DID 생성 및 공개키 등록 완료: {}",
                                                                hospitalDid))
                                        .flatMap(
                                                hospitalDid ->
                                                        hospitalAgent
                                                                .postHospitalDid(
                                                                        hospitalTenantClient
                                                                                .getTokenByTenantId(
                                                                                        connection
                                                                                                .getTenantId())
                                                                                .getWalletToken(),
                                                                        hospitalDid)
                                                                .map(
                                                                        postResponse ->
                                                                                postResponse
                                                                                        .result()
                                                                                        .did())));
    }

    private Mono<Void> offerVc(String connectionId, String issuerDid, String theirDid) {
        return getMemberConnectionReactive(connectionId)
                .filter(connection -> connection.getConnectionStatus() == ConnectionStatus.ACTIVE)
                .switchIfEmpty(
                        Mono.defer(
                                () -> {
                                    log.warn("Connection {} is not active", connectionId);
                                    return Mono.empty();
                                }))
                .flatMap(
                        connection -> {
                            HospitalVcIssueRequest request =
                                    HospitalVcIssueRequest.createWithDid(
                                            connectionId, issuerDid, theirDid);

                            return Mono.fromCallable(
                                            () -> {
                                                try {
                                                    return objectMapper
                                                            .writerWithDefaultPrettyPrinter()
                                                            .writeValueAsString(request);
                                                } catch (JsonProcessingException e) {
                                                    throw new RuntimeException(
                                                            "Failed to serialize request", e);
                                                }
                                            })
                                    .doOnNext(
                                            requestJson ->
                                                    log.info(
                                                            "Sending VC offer request: {}",
                                                            requestJson))
                                    .then(
                                            hospitalAgent.issueHospitalVc(
                                                    request,
                                                    hospitalTenantClient
                                                            .getTokenByTenantId(
                                                                    connection.getTenantId())
                                                            .getWalletToken()))
                                    .doOnNext(
                                            response ->
                                                    log.info(
                                                            "VC offer sent successfully. Response: {}",
                                                            response))
                                    .doOnError(
                                            error -> {
                                                log.error("Failed to issue VC: ", error);
                                                if (error instanceof WebClientResponseException) {
                                                    WebClientResponseException wcre =
                                                            (WebClientResponseException) error;
                                                    log.error(
                                                            "Response body: {}",
                                                            wcre.getResponseBodyAsString());
                                                }
                                            })
                                    .onErrorMap(
                                            error ->
                                                    new RuntimeException(
                                                            "Failed to offer VC", error))
                                    .then();
                        });
    }

    private Mono<Void> processCredentialIssued(
            String credExId, String connectionId, Map<String, Object> payload) {
        return Mono.fromCallable(
                        () -> {
                            Map<String, Object> byFormat =
                                    (Map<String, Object>) payload.get("by_format");
                            if (byFormat != null) {
                                Map<String, Object> credIssue =
                                        (Map<String, Object>) byFormat.get("cred_issue");
                                if (credIssue != null) {
                                    Map<String, Object> ldProof =
                                            (Map<String, Object>) credIssue.get("ld_proof");
                                    if (ldProof != null) {
                                        log.info(
                                                "Credential issued successfully - type: {}, issuer: {}",
                                                ldProof.get("type"),
                                                ldProof.get("issuer"));
                                    }
                                }
                            }
                            return null;
                        })
                .then(getMemberConnectionReactive(connectionId))
                .flatMap(
                        connection -> {
                            connection.updateConnectionStatus(ConnectionStatus.VC_ISSUED);
                            return Mono.fromCallable(
                                            () -> memberConnectionRepository.save(connection))
                                    .subscribeOn(Schedulers.boundedElastic());
                        })
                .doOnSuccess(
                        saved -> {
                            log.info("VC 발급 완료");
                            passClient.updateConnectionStatus(
                                    saved.getTenantId(),
                                    Long.parseLong(saved.getPassId()),
                                    connectionId);
                            log.info(
                                    "Credential issued and processed - credExId: {}, connectionId: {}",
                                    credExId,
                                    connectionId);
                        })
                .doOnError(
                        error ->
                                log.error(
                                        "Error processing credential issued for credExId: {}",
                                        credExId,
                                        error))
                .then();
    }

    private Mono<MemberConnection> getMemberConnectionReactive(String connectionId) {
        return Mono.fromCallable(
                        () ->
                                memberConnectionRepository
                                        .findMemberConnectionByConnectionId(connectionId)
                                        .orElseThrow(
                                                () ->
                                                        new CommonException(
                                                                AcapyErrorCode
                                                                        .MEMBER_CONNECTION_NOT_FOUND)))
                .subscribeOn(Schedulers.boundedElastic());
    }
}
