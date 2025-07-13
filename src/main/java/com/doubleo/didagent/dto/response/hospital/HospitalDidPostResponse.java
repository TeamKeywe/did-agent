package com.doubleo.didagent.dto.response.hospital;

public record HospitalDidPostResponse(Result result) {
    public record Result(
            String did,
            String verkey,
            String posture,
            String key_type,
            String method,
            Metadata metadata) {}

    public record Metadata(boolean posted) {}
}
