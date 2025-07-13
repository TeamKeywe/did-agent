package com.doubleo.didagent.dto.request.hospital;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Map;

public record HospitalVcIssueRequest(
        @JsonProperty("connection_id") String connectionId,
        @JsonProperty("credential_preview") CredentialPreview credentialPreview,
        Filter filter,
        @JsonProperty("auto_issue") boolean autoIssue,
        @JsonProperty("auto_remove") boolean autoRemove) {

    public record CredentialPreview(
            @JsonProperty("@type") String type, List<Attribute> attributes) {
        public record Attribute(String name, String value) {}
    }

    public record Filter(@JsonProperty("ld_proof") LdProofFilter ldProof) {}

    public record LdProofFilter(
            @JsonProperty("credential") Credential credential,
            @JsonProperty("options") Options options) {}

    public record Credential(
            @JsonProperty("@context") List<Object> context,
            @JsonProperty("type") List<String> type,
            @JsonProperty("issuer") String issuer,
            @JsonProperty("issuanceDate") String issuanceDate,
            @JsonProperty("credentialSubject") CredentialSubject credentialSubject) {}

    public record CredentialSubject(
            @JsonProperty("id") String id,
            @JsonProperty("hospital_tenant") String hospitalTenant,
            @JsonProperty("area_code") String areaCode) {}

    public record Options(
            @JsonProperty("proofType") String proofType,
            @JsonProperty("proofPurpose") String proofPurpose) {}

    // LD Proof 기반 병원 접근 크리덴셜용 팩토리 메서드
    public static HospitalVcIssueRequest createLdProofCredential(
            String connectionId,
            String issuer,
            String credentialSubjectId,
            String hospitalTenant,
            String areaCode) {

        List<Object> defaultContext =
                List.of(
                        "https://www.w3.org/2018/credentials/v1",
                        Map.of(
                                "hospital_tenant", "https://example.org/hospital_tenant",
                                "area_code", "https://example.org/area_code"));

        List<String> defaultType = List.of("VerifiableCredential", "HospitalAccessCredential");
        String defaultIssuanceDate = java.time.Instant.now().toString();
        String defaultProofType = "Ed25519Signature2018";
        String defaultProofPurpose = "assertionMethod";

        // Credential Preview 생성
        CredentialPreview preview =
                new CredentialPreview(
                        "https://didcomm.org/issue-credential/2.0/credential-preview",
                        List.of(
                                new CredentialPreview.Attribute("hospital_tenant", hospitalTenant),
                                new CredentialPreview.Attribute("area_code", areaCode)));

        // Credential Subject 생성
        CredentialSubject credentialSubject =
                new CredentialSubject(credentialSubjectId, hospitalTenant, areaCode);

        // LD Proof Filter 생성
        LdProofFilter ldProofFilter =
                new LdProofFilter(
                        new Credential(
                                defaultContext,
                                defaultType,
                                issuer,
                                defaultIssuanceDate,
                                credentialSubject),
                        new Options(defaultProofType, defaultProofPurpose));

        // Filter 생성 (LD Proof만 사용)
        Filter filter = new Filter(ldProofFilter);

        return new HospitalVcIssueRequest(
                connectionId,
                preview,
                filter,
                true, // auto_issue
                false // auto_remove
                );
    }

    // 간단한 버전 (기본값 사용)
    public static HospitalVcIssueRequest createWithDid(
            String connectionId, String issuer, String credentialSubjectId) {

        return createLdProofCredential(
                connectionId,
                issuer,
                credentialSubjectId,
                "default_hospital", // 기본 병원 텐넌트
                "default_area" // 기본 지역 코드
                );
    }
}
