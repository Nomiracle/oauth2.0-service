package com.example.auth.client;

import com.example.auth.config.ClientConfig;
import com.example.auth.exception.BusinessException;
import com.example.auth.message.AuthCallback;
import com.example.auth.message.AuthUserGender;
import com.example.auth.message.ServerMsg;
import com.example.auth.message.UserInfoResp;
import com.example.auth.utils.AsyAlgOps;
import com.example.auth.utils.PropertiesOps;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static com.example.auth.constant.ConfigConstants.*;

@Slf4j
@Service(AUTH_CLIENT_ALIPAY + AUTH__CONFIG_CLIENT_SUFFIX)
@RequiredArgsConstructor
public class AlipayAuth implements CommonClient{
    private final AlipayClient alipayClient;

    @Autowired
    @Qualifier(AUTH_CLIENT_ALIPAY + AUTH_CONFIG_SUFFIX)
    private ClientConfig clientConfig;
    @Autowired
    @Qualifier(BEAN_NAME_RSA_OPERATION)
    private AsyAlgOps asyAlgOps;
    private final DateTimeFormatter timeColonFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public String getAuthorize(String state) {
        try {
            return UriComponentsBuilder
                    .fromHttpUrl("https://openauth.alipay.com/oauth2/publicAppAuthorize.htm")
                    .queryParam("scope", "auth_user,auth_base")
                    .queryParam("app_id", clientConfig.getClient_id())
                    .queryParam("redirect_uri", clientConfig.getRedirect_uri())
                    .queryParam("state", state)
                    .toUriString();
        }catch (Exception e){
            log.error("生成url错误");
            return null;
        }
    }
    private String getAccessToken(String code) throws BusinessException {

        String remoteResponse;
        Map<String, String> form = new TreeMap<>();
        form.put("app_id", clientConfig.getClient_id());
        form.put("method","alipay.system.oauth.token");
        form.put("charset","GBK");
        form.put("sign_type","RSA2");
        //2014-01-01 08:08:08
        form.put("timestamp",timeColonFormatter.format(LocalDateTime.now()));

        form.put("version","1.0");
        form.put("grant_type","authorization_code");
        form.put("code",code);

        form.put("sign", calcSignStr(form));


        try {
            remoteResponse = alipayClient.accessToken(form);
        } catch (FeignException e) {
            e.printStackTrace();
            throw new BusinessException(ServerMsg.REMOTE_SERVER_UNAVAILABLE_EXCEPTION);
        }
        log.info("服务器响应：{}", remoteResponse);
        Map<String,String> result = new HashMap<>();
        PropertiesOps.recursionFindProperty(new HashSet<>(Collections.singletonList("access_token")),new JsonParser().parse(remoteResponse).getAsJsonObject(),result);
        return result.get("access_token");
    }



    @Override
    public String getUserInfo(AuthCallback authCallback) throws BusinessException {
        String token = getAccessToken(authCallback.getAuth_code());
        return getUserInfoByToken(token);
    }

    private String getUserInfoByToken(String token) throws BusinessException {
        String remoteResponse;
        Map<String, String> form = new TreeMap<>();
        form.put("app_id", clientConfig.getClient_id());
        form.put("method","alipay.user.info.share");
        form.put("charset","GBK");
        form.put("sign_type","RSA2");
        //2014-01-01 08:08:08

        form.put("timestamp",timeColonFormatter.format(LocalDateTime.now()));

        form.put("version","1.0");
        form.put("grant_type","authorization_code");
        form.put("auth_token",token);

        form.put("sign", calcSignStr(form));


        try {
            remoteResponse = alipayClient.accessToken(form);
        } catch (FeignException e) {
            e.printStackTrace();
            throw new BusinessException(ServerMsg.REMOTE_SERVER_UNAVAILABLE_EXCEPTION);
        }
        log.info("服务器响应：{}", remoteResponse);
        JsonObject jsonObject = new JsonParser().parse(remoteResponse).getAsJsonObject();

        return jsonObject.get("alipay_user_info_share_response").getAsJsonObject().toString();
    }

    private String calcSignStr(Map<String, String> form){
        StringJoiner stringJoiner = new StringJoiner("&");
        form.forEach((key,value)->{
            if(StringUtils.isEmpty(value)){
                return;
            }
            stringJoiner.add(key+"="+value);
        });
        log.info("生成待签名字符串:{}",stringJoiner);
        byte[] sign = asyAlgOps.signature(stringJoiner.toString().getBytes(),clientConfig.getClient_secret());
        return Base64.encodeBase64String(sign);
    }


    @Override
    public String getResponse(String rawUserInfo) {
        JsonObject object = new JsonParser().parse(rawUserInfo).getAsJsonObject();
        return UserInfoResp.builder()
                .rawUserInfo(object)
                .uuid(object.get("user_id").getAsString())
                .username(object.get("user_id").getAsString())
                .nickname(object.get("nick_name").getAsString())
                .city(object.get("city").getAsString())
                .province(object.get("province").getAsString())
                .gender(AuthUserGender.getAlipayRealGender(object.get("gender").getAsString()))
//                .token(authToken)
                .source(AUTH_CLIENT_ALIPAY)
                .build().toJson();
    }
}
