package com.example.auth.message;

import lombok.Getter;

@Getter
public enum ServerMsg {

    SUCCESS("0000", "成功"),
    UNKNOWN_EXCEPTION("9999", "系统异常，请联系管理员"),
    UNSUPPORTED_OPERATION("0001","不支持的方法"),
    UNSUPPORTED_AUTH("0002","不支持的认证方式"),
    REMOTE_SERVER_UNAVAILABLE_EXCEPTION("0003","服务器不可用"),
    DINGDING_SIGNATURE_FAILED("0004","【钉钉】签名失败"),
    INVALID_PARAMS("0005","参数错误"),
    ;

    private final String code;
    private final String msg;

    ServerMsg(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

}
