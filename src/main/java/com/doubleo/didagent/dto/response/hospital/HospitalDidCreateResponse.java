package com.doubleo.didagent.dto.response.hospital;

public record HospitalDidCreateResponse(Result result) {
    public record Result(
            String did,
            String verkey,
            String posture,
            String key_type,
            String method,
            Metadata metadata) {}

    public record Metadata() {}
}
