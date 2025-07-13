package com.doubleo.didagent.dto.request.hospital;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public record HospitalInvitationCreateRequest(
        @JsonProperty("alias") String alias,
        @JsonProperty("handshake_protocols") List<String> handshakeProtocols,
        @JsonProperty("goal_code") String goalCode,
        @JsonProperty("my_label") String myLabel,
        @JsonProperty("accept") List<String> accept,
        @JsonProperty("use_did_method") String useDidMethod,
        @JsonProperty("multi_use") boolean multiUse) {
    public static HospitalInvitationCreateRequest create(String tenantId) {
        List<String> handshakeProtocols = new ArrayList<>();
        List<String> accept = new ArrayList<>();

        handshakeProtocols.add("https://didcomm.org/didexchange/1.0");
        accept.add("didcomm/aip2;env=rfc19");

        return new HospitalInvitationCreateRequest(
                tenantId + ":" + LocalDateTime.now(),
                handshakeProtocols,
                "vc-issue",
                tenantId,
                accept,
                "did:peer:2",
                true);
    }
}
