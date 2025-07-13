package com.doubleo.didagent.service;

import com.doubleo.didagent.agent.MediatorAgent;
import com.doubleo.didagent.domain.repository.HospitalInvitationRepository;
import com.doubleo.didagent.dto.request.mediator.MediatorInvitationCreateRequest;
import com.doubleo.didagent.dto.request.poll.HospitalInvitationInfoRequest;
import com.doubleo.didagent.dto.response.poll.InvitationInfoResponse;
import com.doubleo.didagent.global.exception.CommonException;
import com.doubleo.didagent.global.exception.errorcode.AcapyErrorCode;
import com.doubleo.didagent.grpc.client.HospitalTenantClient;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class MemberPollService {

    private final MediatorAgent mediatorAgent;
    private final HospitalInvitationRepository hospitalInvitationRepository;
    private final HospitalTenantClient hospitalTenantClient;

    @Transactional(readOnly = true)
    public InvitationInfoResponse getMediatorInvitation() {
        return new InvitationInfoResponse(
                mediatorAgent
                        .createMediatorInvitation(MediatorInvitationCreateRequest.generate())
                        .block(Duration.ofMillis(10000))
                        .invitationUrl());
    }

    @Transactional(readOnly = true)
    public InvitationInfoResponse getHospitalInvitation(HospitalInvitationInfoRequest request) {
        log.info("Get hospital invitation for {}", request);
        String tenantId = hospitalTenantClient.getTenantIdByHospitalId(request.hospitalId());
        return new InvitationInfoResponse(getHospitalInvitationUrl(request.passId(), tenantId));
    }

    private String getHospitalInvitationUrl(Long passId, String tenantId) {
        return hospitalInvitationRepository
                .findHospitalInvitationByPassIdAndTenantId(String.valueOf(passId), tenantId)
                .orElseThrow(() -> new CommonException(AcapyErrorCode.INVITATION_NOT_FOUND))
                .getInvitationUrl();
    }
}
