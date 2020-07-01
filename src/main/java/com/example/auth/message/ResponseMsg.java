package com.example.auth.message;


import com.example.auth.constant.ConfigConstants;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.apache.logging.log4j.ThreadContext;

@Getter
@Setter
@Accessors(chain = true)
public class ResponseMsg extends AbstractMsg {
    private String version, code, data,msg,requestId;

    public ResponseMsg() {
        this(ServerMsg.SUCCESS);
    }

    public ResponseMsg(ServerMsg serverMsg) {
        this(serverMsg.getCode(), serverMsg.getMsg());
    }

    public ResponseMsg(String code, String msg) {
        setRequestId(ThreadContext.get("logId"));
        this.version = ConfigConstants.SERVICE_VERSION;
        this.code = code;
        this.msg = msg;
    }


}
