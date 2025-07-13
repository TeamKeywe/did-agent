package com.doubleo.didagent.controller;

import com.doubleo.didagent.service.AcapyWebhookService;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/webhooks")
@Slf4j
@RequiredArgsConstructor
public class AcapyWebhookController {

    private final AcapyWebhookService acapyWebhookService;

    @PostMapping("/topic/connections/")
    @ResponseStatus(HttpStatus.OK)
    public void connectionWebhookReceive(
            HttpServletRequest req,
            @RequestBody Map<String, Object> payload,
            @RequestHeader Map<String, String> headers) {

        log.info("=== Connection Webhook Received ===");
        logWebhookDetails(req, headers, payload);

        try {
            Mono<Void> mono = acapyWebhookService.processConnectionWebhook(payload);
            mono.subscribe(null, err -> log.error("processing failed", err));
        } catch (Exception e) {
            log.error("Error processing connection webhook: {}", e.getMessage(), e);
        }
    }

    @PostMapping("/topic/issue_credential_v2_0/")
    @ResponseStatus(HttpStatus.OK)
    public void credentialWebhookReceive(
            HttpServletRequest req,
            @RequestBody Map<String, Object> payload,
            @RequestHeader Map<String, String> headers) {

        log.info("=== Credential Webhook Received ===");
        logWebhookDetails(req, headers, payload);

        try {
            Mono<Void> mono = acapyWebhookService.processCredentialWebhook(payload);
            mono.subscribe(null, err -> log.error("processing failed", err));
        } catch (Exception e) {
            log.error("Error processing credential webhook: {}", e.getMessage(), e);
        }
    }

    @PostMapping("/topic/out_of_band/")
    @ResponseStatus(HttpStatus.OK)
    public void outOfBandWebhookReceive(
            HttpServletRequest req,
            @RequestBody Map<String, Object> payload,
            @RequestHeader Map<String, String> headers) {

        log.info("=== Out of Band Webhook Received ===");
        logWebhookDetails(req, headers, payload);

        try {
            Mono<Void> mono = acapyWebhookService.processOutOfBandWebhook(payload);
            mono.subscribe(null, err -> log.error("processing failed", err));
        } catch (Exception e) {
            log.error("Error processing out of band webhook: {}", e.getMessage(), e);
        }
    }

    private void logWebhookDetails(
            HttpServletRequest req, Map<String, String> headers, Map<String, Object> payload) {
        String path = req.getRequestURI();
        log.info("=== Webhook Path: {} ===", path);
        log.info("--- Headers ---");
        headers.forEach((k, v) -> log.info("{}: {}", k, v));
        log.info("--- Payload ---");
        log.info("{}", payload);
    }
}
