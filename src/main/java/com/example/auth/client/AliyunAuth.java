package com.example.auth.client;

import com.example.auth.config.ClientConfig;
import com.example.auth.exception.BusinessException;
import com.example.auth.message.AuthCallback;
import com.example.auth.message.AuthUserGender;
import com.example.auth.message.ServerMsg;
import com.example.auth.message.UserInfoResp;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.Map;

import static com.example.auth.constant.ConfigConstants.*;

@Slf4j
@Service(AUTH_CLIENT_ALIYUN + AUTH__CONFIG_CLIENT_SUFFIX)
@RequiredArgsConstructor
public class AliyunAuth implements CommonClient {
    private final AuthAliyunClient aliyunClient;

    @Autowired
    @Qualifier(AUTH_CLIENT_ALIYUN + AUTH_CONFIG_SUFFIX)
    private ClientConfig clientConfig;


    @Override
    public String getAuthorize(String state) {

        return UriComponentsBuilder
                .fromHttpUrl("https://signin.aliyun.com/oauth2/v1/auth")
                .queryParam("response_type", "code")
                .queryParam("client_id", clientConfig.getClient_id())
                .queryParam("redirect_uri", clientConfig.getRedirect_uri())
                .queryParam("state", state)
                .toUriString();
    }

    @Override
    public String getUserInfo(AuthCallback authCallback) throws BusinessException {
        String code = authCallback.getCode();
        String token = this.getAccessToken(code);
        return this.getUserInfoByToken(token);
    }

    @Override
    public String getResponse(String rawUserInfo) {
        JsonObject object = new JsonParser().parse(rawUserInfo).getAsJsonObject();
        return UserInfoResp.builder()
                .rawUserInfo(object)
                .uuid(object.get("sub").getAsString())
                .username(object.get("login_name").getAsString())
                .nickname(object.get("name").getAsString())
                .gender(AuthUserGender.UNKNOWN)
//                .token(authToken)
                .source(AUTH_CLIENT_ALIYUN)
                .build().toJson();
    }


    private String getAccessToken(String code) throws BusinessException {

        String remoteResponse;
        Map<String, String> form = new HashMap<>();
        form.put("code", code);
        form.put("client_id", clientConfig.getClient_id());
        form.put("client_secret", clientConfig.getClient_secret());
        form.put("redirect_uri", clientConfig.getRedirect_uri());
        form.put("grant_type", "authorization_code");


        Map<String, String> headers = new HashMap<>();
        try {
            remoteResponse = aliyunClient.accessToken(headers, form);
        } catch (FeignException e) {
            e.printStackTrace();
            throw new BusinessException(ServerMsg.REMOTE_SERVER_UNAVAILABLE_EXCEPTION);
        }
        log.info("服务器响应：{}", remoteResponse);
        return new JsonParser().parse(remoteResponse).getAsJsonObject().get("access_token").getAsString();
    }

    private String getUserInfoByToken(String accessToken) throws BusinessException {

        String remoteResponse;
        Map<String, String> form = new HashMap<>();
        form.put("access_token", accessToken);
        Map<String, String> headers = new HashMap<>();
        try {
            remoteResponse = aliyunClient.userInfo(headers, form);
        } catch (FeignException e) {
            e.printStackTrace();
            throw new BusinessException(ServerMsg.REMOTE_SERVER_UNAVAILABLE_EXCEPTION);
        }
        log.info("服务器响应：{}", remoteResponse);
        return remoteResponse;
    }
}
