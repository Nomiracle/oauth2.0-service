package com.example.auth.client;

import com.example.auth.config.ClientConfig;
import com.example.auth.exception.BusinessException;
import com.example.auth.message.AuthCallback;
import com.example.auth.message.AuthUserGender;
import com.example.auth.message.ServerMsg;
import com.example.auth.message.UserInfoResp;
import com.example.auth.utils.URLEncodeUtils;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static com.example.auth.constant.ConfigConstants.*;


@Slf4j
@Service(AUTH_CLIENT_Dingding +AUTH__CONFIG_CLIENT_SUFFIX)
@RequiredArgsConstructor
public class DingdingAuth implements CommonClient{
    private final DingdingClient dingdingClient;

    @Autowired
    @Qualifier(AUTH_CLIENT_Dingding+ AUTH_CONFIG_SUFFIX)
    private ClientConfig clientConfig;


    @Override
    public String getAuthorize(String state) {

        return UriComponentsBuilder
                .fromHttpUrl("https://oapi.dingtalk.com/connect/oauth2/sns_authorize")
                .queryParam("response_type", "code")
                .queryParam("appid", clientConfig.getClient_id())
                .queryParam("redirect_uri", clientConfig.getRedirect_uri())
                .queryParam("state", state)
                .queryParam("scope","snsapi_login")
                .toUriString();
    }

    @Override
    public  String getUserInfo(AuthCallback authCallback) throws BusinessException {
        String tmp_auth_code = authCallback.getCode();
        return this.getUserInfoByTmpCode( tmp_auth_code, clientConfig.getClient_id(), clientConfig.getClient_secret());
    }
    @Override
    public String getResponse(String rawUserInfo) {
        JsonObject object = new JsonParser().parse (rawUserInfo).getAsJsonObject();
        return UserInfoResp.builder()
                .rawUserInfo(object)
                .uuid(object.get("openid").getAsString())
                .username(object.get("dingId").getAsString())
                .nickname(object.get("nick").getAsString())
                .gender(AuthUserGender.UNKNOWN)
                .company(object.get("unionid").getAsString())
                .source(AUTH_CLIENT_Dingding)
                .build().toJson();
    }

    private String getUserInfoByTmpCode(String tmp_auth_code, String appid, String appSecret) throws BusinessException {
        String remoteResponse;
        Map<String, String> form = new HashMap<>();
        String timestamp = System.currentTimeMillis()+"";
        // 根据timestamp, appSecret计算签名值
        String signature;
        String urlEncodeSignature;
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(appSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] signatureBytes = mac.doFinal(timestamp.getBytes(StandardCharsets.UTF_8));
            signature = new String(Base64.encodeBase64(signatureBytes));
            urlEncodeSignature = URLEncodeUtils.urlEncode(signature);
        }catch (Exception e){
            throw new BusinessException(ServerMsg.DINGDING_SIGNATURE_FAILED);
        }


        form .put("tmp_auth_code",tmp_auth_code);
        JsonObject respObj;
        try {
            remoteResponse = dingdingClient.userInfo(urlEncodeSignature,appid,timestamp, form);
            respObj = new JsonParser().parse (remoteResponse).getAsJsonObject();
            if(respObj.get("errcode").getAsInt() != 0){
                throw new BusinessException(ServerMsg.REMOTE_SERVER_UNAVAILABLE_EXCEPTION);
            }
        } catch (FeignException e) {
            e.printStackTrace();
            throw new BusinessException(ServerMsg.REMOTE_SERVER_UNAVAILABLE_EXCEPTION);
        }
        log.info("服务器响应：{}", remoteResponse);
        return respObj.get("user_info").toString();
    }

}
