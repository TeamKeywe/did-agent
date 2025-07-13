package com.doubleo.didagent.domain.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;
import org.springframework.data.redis.core.index.Indexed;

@Getter
@RedisHash("connection")
public class MemberConnection {

    @Id private final String connectionId;
    private final String tenantId;
    @Indexed private final String passId;
    private final String memberId;
    private final ConnectionStatus connectionStatus;

    @TimeToLive private final long ttl;

    @Builder(access = AccessLevel.PRIVATE)
    private MemberConnection(
            String tenantId,
            String passId,
            String memberId,
            String connectionId,
            ConnectionStatus connectionStatus,
            long ttl) {
        this.connectionId = connectionId;
        this.tenantId = tenantId;
        this.passId = passId;
        this.memberId = memberId;
        this.connectionStatus = connectionStatus;
        this.ttl = ttl;
    }

    public static MemberConnection createMemberConnection(
            String connectionId, String tenantId, String passId, String memberId) {
        return builder()
                .connectionId(connectionId)
                .tenantId(tenantId)
                .passId(passId)
                .memberId(memberId)
                .connectionStatus(ConnectionStatus.ACTIVE)
                .ttl(1000L * 3600 * 24 * 3)
                .build();
    }

    public MemberConnection updateConnectionStatus(ConnectionStatus newStatus) {
        return builder()
                .connectionId(this.connectionId)
                .tenantId(this.tenantId)
                .passId(this.passId)
                .memberId(this.memberId)
                .connectionStatus(newStatus)
                .ttl(this.ttl)
                .build();
    }
}
