package com.doubleo.didagent.infra.config.mediator;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "mediator")
public record MediatorProperties(String adminUrl) {}
