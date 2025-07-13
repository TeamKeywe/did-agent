package com.doubleo.didagent.infra.config.hospital;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "hospital")
public record HospitalProperties(String adminUrl) {}
