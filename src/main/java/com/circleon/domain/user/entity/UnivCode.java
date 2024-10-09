package com.circleon.domain.user.entity;

public enum UnivCode {
    AJOU(1, "ajou.ac.kr");

    private final int code;
    private final String email;

    UnivCode(int code, String email){
        this.code = code;
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public int getCode(){
        return code;
    }
}
