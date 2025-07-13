package com.doubleo.didagent.dto.response.hospital;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Instant;
import java.util.Map;

public record HospitalVcIssueResponse(
        String state,
        @JsonProperty("created_at") Instant createdAt,
        @JsonProperty("updated_at") Instant updatedAt,
        @JsonProperty("cred_ex_id") String credExId,
        @JsonProperty("connection_id") String connectionId,
        @JsonProperty("thread_id") String threadId,
        String initiator,
        String role,
        @JsonProperty("cred_proposal") Map<String, Object> credProposal,
        @JsonProperty("cred_offer") Map<String, Object> credOffer,
        @JsonProperty("by_format") Map<String, Object> byFormat,
        @JsonProperty("auto_offer") Boolean autoOffer,
        @JsonProperty("auto_issue") Boolean autoIssue,
        @JsonProperty("auto_remove") Boolean autoRemove) {}
