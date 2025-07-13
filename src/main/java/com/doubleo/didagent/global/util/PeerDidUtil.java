package com.doubleo.didagent.global.util;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import org.bitcoinj.core.Base58;
import org.json.JSONArray;
import org.json.JSONObject;

public class PeerDidUtil {

    public static String createPeerDid2(
            String signingKeyMb, // z6... (Ed25519)
            String agreementKeyMb, // z6L... (X25519)
            List<String> routingKeys,
            String serviceEndpoint) {

        String enc1 = "V" + signingKeyMb; // verification key
        String enc2 = "E" + agreementKeyMb; // key-agreement key
        List<String> routingKeysDidUrl = PeerDidUtil.convertRoutingKeys(routingKeys);
        JSONObject svc = new JSONObject();

        svc.put("type", "DIDComm");
        svc.put("priority", 0);
        svc.put("id", "#service-0");
        svc.put("recipientKeys", new JSONArray().put("#key-1"));
        if (routingKeys != null && !routingKeys.isEmpty()) {
            svc.put("routingKeys", new JSONArray(routingKeysDidUrl)); // routingKeys → r
        }
        svc.put("serviceEndpoint", serviceEndpoint); // serviceEndpoint → s

        String enc3 =
                "S"
                        + Base64.getUrlEncoder()
                                .withoutPadding()
                                .encodeToString(svc.toString().getBytes(StandardCharsets.UTF_8));

        return "did:peer:2." + enc1 + "." + enc2 + "." + enc3;
    }

    private static String rawVerkeyToDidKey(String verkeyBase58) {
        byte[] raw = Base58.decode(verkeyBase58); // 32-byte 공개키
        byte[] prefixed = new byte[raw.length + 2];
        prefixed[0] = (byte) 0xED; // multicodec: 0xED 0x01 = Ed25519
        prefixed[1] = 0x01;
        System.arraycopy(raw, 0, prefixed, 2, raw.length);

        String multibase = "z" + Base58.encode(prefixed);
        return "did:key:" + multibase;
    }

    private static List<String> convertRoutingKeys(List<String> rawKeys) {
        return rawKeys.stream().map(PeerDidUtil::rawVerkeyToDidKey).toList();
    }
}
