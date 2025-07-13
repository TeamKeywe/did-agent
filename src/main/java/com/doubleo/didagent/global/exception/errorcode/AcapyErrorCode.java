package com.doubleo.didagent.global.exception.errorcode;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum AcapyErrorCode implements BaseErrorCode {
    INVITATION_NOT_FOUND(HttpStatus.NOT_FOUND, "invitation 을 찾을 수 없습니다."),
    MEMBER_CONNECTION_NOT_FOUND(HttpStatus.NOT_FOUND, "member connection 을 찾을 수 없습니다."),
    INVITATION_REQUEST_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "invitation 생성 요청에 실패했습니다"),
    DID_PROCESS_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "DID 생성 요청에 실패했습니다"),
    ;

    private final HttpStatus httpStatus;
    private final String message;

    @Override
    public String errorClassName() {
        return this.name();
    }
}
