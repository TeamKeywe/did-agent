package com.doubleo.didagent.agent.client;

import com.doubleo.didagent.infra.config.hospital.HospitalProperties;
import com.doubleo.didagent.infra.config.mediator.MediatorProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@RequiredArgsConstructor
public class AcapyClientConfig {
    private final MediatorProperties mediatorProperties;
    private final HospitalProperties hospitalProperties;

    @Bean("mediatorClient")
    public AcapyClient mediatorClient(WebClient.Builder builder) {
        WebClient wc = builder.baseUrl(mediatorProperties.adminUrl()).build();
        return new AcapyClient(wc);
    }

    @Bean("hospitalClient")
    public AcapyClient hospitalClient(WebClient.Builder builder) {
        WebClient wc = builder.baseUrl(hospitalProperties.adminUrl()).build();
        return new AcapyClient(wc);
    }
}
