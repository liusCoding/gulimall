package com.liuscoding.common.exception;

import lombok.Getter;

@Getter
public class GuliException extends RuntimeException {

    private String msg;
    private int code;

    GuliException(String msg,int code){
        super(msg);
        this.code = code;
    }

    public GuliException(BizCodeEnume codeEnume){
        super(codeEnume.getMsg());
        this.code = codeEnume.getCode();
    }
}
