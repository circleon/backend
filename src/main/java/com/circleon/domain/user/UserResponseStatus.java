package com.circleon.domain.user;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum UserResponseStatus {

    EMAIL_DUPLICATE(HttpStatus.CONFLICT.value(), "021", "이메일 중복"),
    USER_INFO_VALIDATION_FAILED(HttpStatus.BAD_REQUEST.value(), "022", "유저 정보 데이터 검증 실패"),
    EMAIL_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "023", "이메일 불일치"),
    PASSWORD_MISMATCH(HttpStatus.NOT_FOUND.value(),"024", "비밀번호 불일치"),
    VERIFICATION_CODE_NOT_EXPIRED(HttpStatus.BAD_REQUEST.value(), "025", "인증 코드가 아직 만료되지 않았습니다."),
    TOO_MANY_ATTEMPTS(HttpStatus.TOO_MANY_REQUESTS.value(), "026", "너무 많은 인증 시도가 발생했습니다."),
    VERIFICATION_CODE_NOT_REQUESTED(HttpStatus.BAD_REQUEST.value(), "027", "인증 코드 발급을 먼저 요청해주세요."),
    INVALID_VERIFICATION_CODE(HttpStatus.BAD_REQUEST.value(), "028", "유효하지 않은 인증 코드입니다."),
    VERIFICATION_CODE_EXPIRED(HttpStatus.BAD_REQUEST.value(), "029", "인증 코드가 만료되었습니다."),
    INVALID_UNIV_EMAIL_DOMAIN(HttpStatus.BAD_REQUEST.value(), "030", "허용된 대학 이메일 도메인이 아닙니다."),
    EMAIL_SERVICE_UNAVAILABLE(HttpStatus.SERVICE_UNAVAILABLE.value(), "031", "이메일 전송 서비스를 사용할 수 없습니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "032", "유저가 존재하지 않습니다.");

    private final int httpStatusCode;
    private final String code;
    private final String message;

    UserResponseStatus(int httpStatusCode, String code, String message) {
        this.httpStatusCode = httpStatusCode;
        this.code = code;
        this.message = message;
    }

}
