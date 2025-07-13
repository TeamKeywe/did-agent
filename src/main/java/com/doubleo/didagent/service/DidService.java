package com.doubleo.didagent.service;

import com.doubleo.didagent.dto.request.did.DidCreateRequest;
import com.doubleo.didagent.dto.response.did.DidCreateResponse;
import com.doubleo.didagent.global.exception.CommonException;
import com.doubleo.didagent.global.exception.errorcode.DidErrorCode;
import com.doubleo.didagent.global.util.Ed25519KeyGenerator;
import com.doubleo.didagent.global.util.KeyMaterial;
import com.doubleo.didagent.global.util.PeerDidUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class DidService {

    public DidCreateResponse createPeer2Did(DidCreateRequest request) {
        KeyMaterial key = getKeyMaterial();
        String peer2Did =
                PeerDidUtil.createPeerDid2(
                        key.signingKeyMb58(), // Ed25519 서명 키 (V)
                        key.agreementKeyMb58(), // X25519 암호화 키 (E)
                        request.routingKeys(),
                        request.serviceEndpoint());
        log.info("Created PeerDid2: {}", peer2Did);
        return new DidCreateResponse(
                peer2Did,
                key.signingKeyMb58(),
                key.signingPrivBase58(),
                key.agreementKeyMb58(),
                key.x25519PrivateMb58());
    }

    private KeyMaterial getKeyMaterial() throws CommonException {
        try {
            System.out.println(Ed25519KeyGenerator.generate());
            return Ed25519KeyGenerator.generate();
        } catch (Exception e) {
            throw new CommonException(DidErrorCode.KEY_GENERATION_FAILED);
        }
    }
}
