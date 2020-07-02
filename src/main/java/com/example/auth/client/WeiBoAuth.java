package com.example.auth.client;

import com.example.auth.config.ClientConfig;
import com.example.auth.exception.BusinessException;
import com.example.auth.message.*;
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
@Service(AUTH_CLIENT_WEIBO +AUTH__CONFIG_CLIENT_SUFFIX)
@RequiredArgsConstructor
public class WeiBoAuth implements CommonClient{
    private final WeiBoClient weiBoClient;

    @Autowired
    @Qualifier(AUTH_CLIENT_WEIBO+ AUTH_CONFIG_SUFFIX)
    private ClientConfig clientConfig;
    @Override
    public String getAuthorize(String state) {
        return UriComponentsBuilder
                .fromHttpUrl("https://api.weibo.com/oauth2/authorize")
                .queryParam("client_id", clientConfig.getClient_id())
                .queryParam("redirect_uri", clientConfig.getRedirect_uri())
                .queryParam("state", state)
                .toUriString();
    }

    @Override
    public String getUserInfo(AuthCallback authCallback) throws BusinessException {
        //get access token
        Map<String, String> form = new HashMap<>();
        form.put("code", authCallback.getCode());
        form.put("client_id", clientConfig.getClient_id());
        form.put("client_secret", clientConfig.getClient_secret());
        form.put("redirect_uri", clientConfig.getRedirect_uri());
        form.put("grant_type", "authorization_code");
        String remoteResponse;
        try {
            remoteResponse = weiBoClient.accessToken(form);
        } catch (FeignException e) {
            e.printStackTrace();
            throw new BusinessException(ServerMsg.REMOTE_SERVER_UNAVAILABLE_EXCEPTION);
        }
        log.info("服务器响应：{}", remoteResponse);

        JsonObject accessTokenObject = new JsonParser().parse(remoteResponse).getAsJsonObject();


        AuthToken authToken = AuthToken.builder()
                .accessToken(accessTokenObject.get("access_token").getAsString())
                .expireIn(accessTokenObject.get("expires_in").getAsInt())
                .uid(accessTokenObject.get("uid").getAsString())
                .build();

        //get user info

        form = new HashMap<>();
        form.put("access_token", authToken.getAccessToken());
        form.put("uid",authToken.getUid());
        try {
            remoteResponse = weiBoClient.userInfo(form);
        } catch (FeignException e) {
            e.printStackTrace();
            throw new BusinessException(ServerMsg.REMOTE_SERVER_UNAVAILABLE_EXCEPTION);
        }
        log.info("服务器响应：{}", remoteResponse);
        //https://open.weibo.com/wiki/2/users/show remoteResponse 字段意义
        JsonObject object = new JsonParser().parse(remoteResponse).getAsJsonObject();
        return UserInfoResp.builder()
                .rawUserInfo(object)
                .uuid(object.get("idstr").getAsString())
                .username(object.get("name").getAsString())
                .nickname(object.get("screen_name").getAsString())
                .gender(AuthUserGender.getRealGender(object.get("gender").getAsString()))
                .avatar(object.get("avatar_large").getAsString())
                .location(object.get("location").getAsString())
                .email(object.get("email").getAsString())
                .token(authToken)
                .source(AUTH_CLIENT_WEIBO)
                .build().toJson();
    }

    @Override
    public String getResponse(String rawUserInfo) {
        return rawUserInfo;
    }
}
