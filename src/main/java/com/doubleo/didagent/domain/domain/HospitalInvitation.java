package com.doubleo.didagent.domain.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;
import org.springframework.data.redis.core.index.Indexed;

@Getter
@RedisHash("hospital-invitation")
public class HospitalInvitation {

    @Id private final String invitationId;
    private final String invitationUrl;
    private final String memberId;
    @Indexed private final String passId;
    @Indexed private final String tenantId;

    @TimeToLive private final long ttl;

    @Builder(access = AccessLevel.PRIVATE)
    private HospitalInvitation(
            String invitationId,
            String invitationUrl,
            String tenantId,
            String passId,
            String memberId,
            long ttl) {
        this.invitationId = invitationId;
        this.invitationUrl = invitationUrl;
        this.tenantId = tenantId;
        this.passId = passId;
        this.memberId = memberId;
        this.ttl = ttl;
    }

    public static HospitalInvitation createHospitalInvitation(
            String invitationId,
            String invitationUrl,
            String tenantId,
            String passId,
            String memberId) {
        return builder()
                .invitationId(invitationId)
                .invitationUrl(invitationUrl)
                .tenantId(tenantId)
                .passId(passId)
                .memberId(memberId)
                .ttl(1000L * 3600 * 24 * 3)
                .build();
    }
}
