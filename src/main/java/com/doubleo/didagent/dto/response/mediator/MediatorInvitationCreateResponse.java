package com.doubleo.didagent.dto.response.mediator;

import com.doubleo.didagent.dto.response.hospital.HospitalInvitationCreateResponse;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record MediatorInvitationCreateResponse(
        String state,
        boolean trace,
        @JsonProperty("invi_msg_id") String inviMsgId,
        @JsonProperty("oob_id") String oobId,
        HospitalInvitationCreateResponse.Invitation invitation,
        @JsonProperty("invitation_url") String invitationUrl) {
    public record Invitation(
            @JsonProperty("@type") String type,
            @JsonProperty("@id") String id,
            String label,
            @JsonProperty("handshake_protocols") List<String> handshakeProtocols,
            List<String> accept,
            List<String> services) {}
}
