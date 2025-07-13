package com.doubleo.didagent.global.util;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.Security;
import java.util.Arrays;
import org.bitcoinj.core.Base58;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

public class Ed25519KeyGenerator {

    static {
        // BouncyCastle Provider 등록 (X25519 지원을 위해 필요)
        if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
            Security.addProvider(new BouncyCastleProvider());
        }
    }

    public static KeyMaterial generate() throws Exception {

        // Ed25519 키 쌍 생성
        KeyPairGenerator ed25519Kpg = KeyPairGenerator.getInstance("Ed25519");
        KeyPair ed25519Kp = ed25519Kpg.generateKeyPair();

        // Ed25519 Public Key 처리
        byte[] ed25519PubSpki = ed25519Kp.getPublic().getEncoded();
        byte[] ed25519RawPub =
                Arrays.copyOfRange(
                        ed25519PubSpki, ed25519PubSpki.length - 32, ed25519PubSpki.length);

        // Ed25519 Private Key 처리
        byte[] ed25519PrivPkcs8 = ed25519Kp.getPrivate().getEncoded();
        byte[] ed25519RawPriv =
                Arrays.copyOfRange(
                        ed25519PrivPkcs8, ed25519PrivPkcs8.length - 32, ed25519PrivPkcs8.length);

        // Ed25519 Public Key multicodec encoding (0xED 0x01)
        byte[] ed25519PubPrefixed = new byte[ed25519RawPub.length + 2];
        ed25519PubPrefixed[0] = (byte) 0xED;
        ed25519PubPrefixed[1] = 0x01;
        System.arraycopy(ed25519RawPub, 0, ed25519PubPrefixed, 2, ed25519RawPub.length);

        String ed25519PublicKeyBase58 = "z" + Base58.encode(ed25519PubPrefixed);
        String ed25519PrivateKeyBase58 = Base58.encode(ed25519RawPriv);

        // X25519 키 쌍 생성 (별도의 독립적인 키 쌍)
        KeyPairGenerator x25519Kpg = KeyPairGenerator.getInstance("X25519", "BC");
        KeyPair x25519Kp = x25519Kpg.generateKeyPair();

        // X25519 Public Key 처리
        byte[] x25519PubEncoded = x25519Kp.getPublic().getEncoded();
        byte[] x25519RawPub = extractX25519PublicKey(x25519PubEncoded);

        // X25519 Private Key 처리
        byte[] x25519PrivEncoded = x25519Kp.getPrivate().getEncoded();
        byte[] x25519RawPriv = extractX25519PrivateKey(x25519PrivEncoded);

        // X25519 Public Key multicodec encoding (0xEC 0x01)
        byte[] x25519PubPrefixed = new byte[x25519RawPub.length + 2];
        x25519PubPrefixed[0] = (byte) 0xEC;
        x25519PubPrefixed[1] = 0x01;
        System.arraycopy(x25519RawPub, 0, x25519PubPrefixed, 2, x25519RawPub.length);

        String x25519PublicKeyMb58 = "z" + Base58.encode(x25519PubPrefixed);

        // X25519 Private Key multicodec encoding (0x82 0x26)
        byte[] x25519PrivPrefixed = new byte[x25519RawPriv.length + 2];
        x25519PrivPrefixed[0] = (byte) 0x82;
        x25519PrivPrefixed[1] = 0x26;
        System.arraycopy(x25519RawPriv, 0, x25519PrivPrefixed, 2, x25519RawPriv.length);

        String x25519PrivateKeyMb58 = "z" + Base58.encode(x25519PrivPrefixed);

        // 디버깅 출력
        System.out.println("=== Ed25519 Keys ===");
        System.out.println("Ed25519 Public (hex): " + bytesToHex(ed25519RawPub));
        System.out.println("Ed25519 Private (hex): " + bytesToHex(ed25519RawPriv));

        System.out.println("\n=== X25519 Keys ===");
        System.out.println("X25519 Public (hex): " + bytesToHex(x25519RawPub));
        System.out.println("X25519 Private (hex): " + bytesToHex(x25519RawPriv));

        System.out.println("\n=== Encoded Keys ===");
        System.out.println("Ed25519 Public MB58: " + ed25519PublicKeyBase58);
        System.out.println("Ed25519 Private B58: " + ed25519PrivateKeyBase58);
        System.out.println("X25519 Public MB58: " + x25519PublicKeyMb58);
        System.out.println("X25519 Private MB58: " + x25519PrivateKeyMb58);

        return new KeyMaterial(
                ed25519RawPub, // rawPub (Ed25519)
                ed25519RawPriv, // rawPriv (Ed25519)
                ed25519PublicKeyBase58, // signingKeyMb58 (Ed25519 public)
                ed25519PrivateKeyBase58, // signingPrivBase58 (Ed25519 private)
                x25519PublicKeyMb58, // agreementKeyMb58 (X25519 public)
                x25519PrivateKeyMb58 // x25519PrivateMb58 (X25519 private)
                );
    }

    /**
     * X25519 Public Key에서 원시 32바이트 추출 SPKI 형식: 30 2A 30 05 06 03 2B 65 6E 03 21 00 [32바이트 public
     * key]
     */
    private static byte[] extractX25519PublicKey(byte[] encoded) {
        // SPKI 형식에서 마지막 32바이트가 실제 public key
        if (encoded.length < 32) {
            throw new IllegalArgumentException("Invalid X25519 public key encoding");
        }
        return Arrays.copyOfRange(encoded, encoded.length - 32, encoded.length);
    }

    /** X25519 Private Key에서 원시 32바이트 추출 PKCS#8 형식에서 실제 private key 데이터 추출 */
    private static byte[] extractX25519PrivateKey(byte[] encoded) {
        // PKCS#8 형식 파싱
        // 30 2E 02 01 00 30 05 06 03 2B 65 6E 04 22 04 20 [32바이트 private key]

        if (encoded.length < 32) {
            throw new IllegalArgumentException("Invalid X25519 private key encoding");
        }

        // PKCS#8에서 private key는 OCTET STRING 내부에 있음
        // 일반적으로 마지막 32바이트가 실제 private key
        // 하지만 더 정확한 파싱을 위해 OCTET STRING을 찾음

        for (int i = 0; i < encoded.length - 34; i++) {
            if (encoded[i] == 0x04
                    && encoded[i + 1] == 0x22
                    && encoded[i + 2] == 0x04
                    && encoded[i + 3] == 0x20) {
                // 0x04 0x22 = OCTET STRING (34 bytes)
                // 0x04 0x20 = OCTET STRING (32 bytes) - actual private key
                return Arrays.copyOfRange(encoded, i + 4, i + 36);
            }
        }

        // fallback: 마지막 32바이트 사용
        return Arrays.copyOfRange(encoded, encoded.length - 32, encoded.length);
    }

    /** 바이트 배열을 hex 문자열로 변환 */
    private static String bytesToHex(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        for (byte b : bytes) {
            result.append(String.format("%02x", b));
        }
        return result.toString();
    }
}
