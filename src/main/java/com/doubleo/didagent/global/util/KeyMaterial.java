package com.doubleo.didagent.global.util;

public record KeyMaterial(
        byte[] rawEd25519Public, // 32-byte 원본 Ed25519 공개키
        byte[] rawEd25519Private, // 32-byte 원본 Ed25519 비밀키
        String signingKeyMb58, // 멀티코덱+멀티베이스(“z…”) Ed25519 공개키
        String signingPrivBase58, // Base58 인코딩된 Ed25519 비밀키(원본 32 바이트)
        String agreementKeyMb58, // 멀티코덱+멀티베이스(“z…”) X25519 공개키
        //        EdECPublicKey signingPublic, // JCA Ed25519 PublicKey (서명용)
        //        EdECPrivateKey signingPrivate, // JCA Ed25519 PrivateKey (서명용)
        //        XDHPublicKey agreementPublic, // JCA X25519 PublicKey (암호화·키합의용)
        //        XDHPrivateKey agreementPrivate, // JCA X25519 PrivateKey (암호화·키합의용)
        String x25519PrivateMb58) {}
