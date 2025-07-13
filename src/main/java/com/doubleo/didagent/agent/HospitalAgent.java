package com.doubleo.didagent.agent;

import com.doubleo.didagent.agent.client.AcapyClient;
import com.doubleo.didagent.dto.request.hospital.HospitalDidCreateRequest;
import com.doubleo.didagent.dto.request.hospital.HospitalInvitationCreateRequest;
import com.doubleo.didagent.dto.request.hospital.HospitalVcIssueRequest;
import com.doubleo.didagent.dto.response.hospital.*;
import com.doubleo.didagent.infra.config.acapy.AcapyProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class HospitalAgent {

    private final AcapyClient hospitalClient;
    private final AcapyProperties acapyProperties;

    public Mono<HospitalInvitationCreateResponse> createHospitalInvitation(
            HospitalInvitationCreateRequest request, String token) {
        log.debug("Creating hospital invitation with path: {}", acapyProperties.createInvitation());
        return hospitalClient
                .getWebClient()
                .post()
                .uri(uriBuilder -> uriBuilder.path(acapyProperties.createInvitation()).build())
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(HospitalInvitationCreateResponse.class)
                .doOnError(
                        error -> {
                            log.error(
                                    "Hospital MemberConnection fetch error: {}",
                                    error.getMessage());
                        });
    }

    public Mono<HospitalDidCreateResponse> createHospitalDid(
            HospitalDidCreateRequest request, String token) {
        return hospitalClient
                .getWebClient()
                .post()
                .uri(uriBuilder -> uriBuilder.path(acapyProperties.createDid()).build())
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(HospitalDidCreateResponse.class)
                .doOnError(
                        error -> {
                            log.error("Hospital Did Creation error: {}", error.getMessage());
                        });
    }

    public Mono<HospitalDidPostResponse> postHospitalDid(String token, String did) {
        return hospitalClient
                .getWebClient()
                .post()
                .uri(
                        uriBuilder ->
                                uriBuilder
                                        .path(acapyProperties.postPublicDid())
                                        .queryParam("did", did)
                                        .build())
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .bodyToMono(HospitalDidPostResponse.class)
                .doOnError(
                        error -> {
                            log.error("Hospital DID Post error: {}", error.getMessage());
                        });
    }

    public Mono<HospitalVcIssueResponse> issueHospitalVc(
            HospitalVcIssueRequest request, String token) {
        log.debug("Issue VC endpoint: {}", acapyProperties.issueVc());
        Mono<HospitalVcIssueResponse> res =
                hospitalClient
                        .getWebClient()
                        .post()
                        .uri(uriBuilder -> uriBuilder.path(acapyProperties.issueVc()).build())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .bodyValue(request)
                        .retrieve()
                        .bodyToMono(HospitalVcIssueResponse.class)
                        .doOnError(
                                error -> {
                                    log.error("Hospital VC Issuance error: {}", error.getMessage());
                                })
                        .doOnNext(
                                response ->
                                        log.info("Hospital VC Issuance response: {}", response));
        return res;
    }
}
