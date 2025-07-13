package com.doubleo.didagent.dto.request.mediator;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public record MediatorInvitationCreateRequest(
        @JsonProperty("alias") String alias,
        @JsonProperty("handshake_protocols") List<String> handshakeProtocols,
        @JsonProperty("goal_code") String goalCode,
        @JsonProperty("my_label") String myLabel,
        @JsonProperty("accept") List<String> accept,
        @JsonProperty("use_did_method") String useDidMethod,
        @JsonProperty("multi_use") boolean multiUse) {
    public static MediatorInvitationCreateRequest generate() {
        List<String> handshakeProtocols = new ArrayList<>();
        List<String> accept = new ArrayList<>();

        handshakeProtocols.add("https://didcomm.org/didexchange/1.0");
        accept.add("didcomm/aip2;env=rfc19");

        return new MediatorInvitationCreateRequest(
                "mediator:invitation:" + LocalDateTime.now(),
                handshakeProtocols,
                "vc-issue",
                "keywe_mediator",
                accept,
                "did:peer:2",
                true);
    }
}
