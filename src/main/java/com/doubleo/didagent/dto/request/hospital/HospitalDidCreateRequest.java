package com.doubleo.didagent.dto.request.hospital;

public record HospitalDidCreateRequest(String method, Options options) {
    public record Options(String key_type) {}

    public static HospitalDidCreateRequest didKey() {
        Options options = new Options("ed25519");
        return new HospitalDidCreateRequest("key", options);
    }
}
