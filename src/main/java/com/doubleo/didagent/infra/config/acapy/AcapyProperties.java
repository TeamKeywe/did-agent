package com.doubleo.didagent.infra.config.acapy;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "acapy")
public record AcapyProperties(
        String createInvitation,
        String checkConnection,
        String createDid,
        String postPublicDid,
        String issueVc) {}
