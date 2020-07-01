package com.example.auth.exception;


import com.example.auth.message.ServerMsg;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;

@Setter
@Getter
public class BusinessException extends Exception {
    private String code;
    private String msg;

    public BusinessException(ServerMsg serverMsg) {
        this(serverMsg.getCode(), serverMsg.getMsg());

    }

    public BusinessException(String code, String msg) {
        this(msg);
        this.code = code;
        this.msg = msg;
    }

    public BusinessException(String message) {
        super(message);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).toString();
    }

}
