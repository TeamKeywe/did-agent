package com.doubleo.didagent.domain.repository;

import com.doubleo.didagent.domain.domain.HospitalInvitation;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;

public interface HospitalInvitationRepository extends CrudRepository<HospitalInvitation, String> {

    Optional<HospitalInvitation> findByInvitationId(String invitationId);

    Optional<HospitalInvitation> findHospitalInvitationByPassIdAndTenantId(
            String passId, String tenantId);

    void deleteHospitalInvitationByInvitationId(String invitationId);
}
