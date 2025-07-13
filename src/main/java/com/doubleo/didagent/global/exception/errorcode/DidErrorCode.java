package com.doubleo.didagent.global.exception.errorcode;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum DidErrorCode implements BaseErrorCode {
    MALFORMED_PEER_DID(HttpStatus.BAD_REQUEST, "잘못된 형식의 DID 입니다."),
    KEY_GENERATION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "keypair 생성에 실패했습니다."),
    ;

    private final HttpStatus httpStatus;
    private final String message;

    @Override
    public String errorClassName() {
        return this.name();
    }
}
