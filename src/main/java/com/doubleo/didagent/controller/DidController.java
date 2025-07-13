package com.doubleo.didagent.controller;

import com.doubleo.didagent.dto.request.did.DidCreateRequest;
import com.doubleo.didagent.dto.response.did.DidCreateResponse;
import com.doubleo.didagent.service.DidService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dids")
@RequiredArgsConstructor
public class DidController {

    private final DidService didService;

    @PostMapping
    public DidCreateResponse peer2DidCreate(@Valid @RequestBody DidCreateRequest request) {
        return didService.createPeer2Did(request);
    }
}
