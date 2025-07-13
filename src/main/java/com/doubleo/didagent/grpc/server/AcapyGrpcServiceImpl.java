package com.doubleo.didagent.grpc.server;

import com.doubleo.didagent.agent.HospitalAgent;
import com.doubleo.didagent.domain.domain.ConnectionStatus;
import com.doubleo.didagent.domain.domain.HospitalInvitation;
import com.doubleo.didagent.domain.domain.MemberConnection;
import com.doubleo.didagent.domain.repository.HospitalInvitationRepository;
import com.doubleo.didagent.domain.repository.MemberConnectionRepository;
import com.doubleo.didagent.dto.request.hospital.HospitalInvitationCreateRequest;
import com.doubleo.didagent.dto.response.hospital.HospitalInvitationCreateResponse;
import com.doubleo.didagent.global.exception.CommonException;
import com.doubleo.didagent.global.exception.errorcode.AcapyErrorCode;
import com.doubleo.didagent.grpc.client.HospitalTenantClient;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.transaction.annotation.Transactional;

@GrpcService
@RequiredArgsConstructor
@Transactional
public class AcapyGrpcServiceImpl extends AcapyServiceGrpc.AcapyServiceImplBase {

    private final HospitalAgent hospitalAgent;
    private final HospitalInvitationRepository hospitalInvitationRepository;
    private final MemberConnectionRepository memberConnectionRepository;
    private final HospitalTenantClient hospitalTenantClient;

    @Override
    public void issueVc(VcIssueRequest request, StreamObserver<VcIssueResponse> responseObserver) {

        HospitalInvitationCreateResponse invitation =
                hospitalAgent
                        .createHospitalInvitation(
                                HospitalInvitationCreateRequest.create(request.getTenantId()),
                                hospitalTenantClient
                                        .getTokenByTenantId(request.getTenantId())
                                        .getWalletToken())
                        .block();
        hospitalInvitationRepository.save(
                HospitalInvitation.createHospitalInvitation(
                        invitation.inviMsgId(),
                        invitation.invitationUrl(),
                        request.getTenantId(),
                        String.valueOf(request.getPassId()),
                        String.valueOf(request.getMemberId())));
        VcIssueResponse response =
                VcIssueResponse.newBuilder().setIsInvitationCreated(true).build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void verifyCredential(
            VerifyCredentialRequest request,
            StreamObserver<VerifyCredentialResponse> responseObserver) {
        MemberConnection connection =
                memberConnectionRepository
                        .findMemberConnectionByConnectionId(request.getConnectionId())
                        .orElseThrow(
                                () ->
                                        new CommonException(
                                                AcapyErrorCode.MEMBER_CONNECTION_NOT_FOUND));
        if (connection.getConnectionStatus() == ConnectionStatus.VC_ISSUED) {
            responseObserver.onNext(
                    VerifyCredentialResponse.newBuilder().setIsVerified(true).build());
        } else {
            responseObserver.onNext(
                    VerifyCredentialResponse.newBuilder().setIsVerified(false).build());
        }
        responseObserver.onCompleted();
    }
}
