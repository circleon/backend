package com.circleon.authentication;

public final class AuthConstants {

    public static final String SOURCE_MAIL = "audgkrnt@naver.com";
    public static final int EXPIRATION_TIME = 5;
    public static final int ATTEMPT_THRESHOLD = 5;
    public static final int VERIFICATION_CODE_LENGTH = 6;
    public static final long REFRESH_TOKEN_COMPROMISE_THRESHOLD = 1000L * 60 * 5;

    private AuthConstants() {

    }
}
