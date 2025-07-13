package com.doubleo.didagent.agent;

import com.doubleo.didagent.agent.client.AcapyClient;
import com.doubleo.didagent.dto.request.mediator.MediatorInvitationCreateRequest;
import com.doubleo.didagent.dto.response.mediator.MediatorInvitationCreateResponse;
import com.doubleo.didagent.infra.config.acapy.AcapyProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class MediatorAgent {

    private final AcapyClient mediatorClient;
    private final AcapyProperties acapyProperties;

    public Mono<MediatorInvitationCreateResponse> createMediatorInvitation(
            MediatorInvitationCreateRequest request) {
        return mediatorClient
                .getWebClient()
                .post()
                .uri(uriBuilder -> uriBuilder.path(acapyProperties.createInvitation()).build())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(MediatorInvitationCreateResponse.class)
                .doOnError(
                        error -> {
                            log.error(
                                    "Mediator MemberConnection fetch error: {}",
                                    error.getMessage());
                        });
    }
}
