package com.doubleo.didagent.agent.client;

import lombok.Getter;
import org.springframework.web.reactive.function.client.WebClient;

@Getter
public class AcapyClient {
    private final WebClient webClient;

    public AcapyClient(WebClient webClient) {
        this.webClient = webClient;
    }
}
