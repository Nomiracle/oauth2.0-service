package com.example.auth.client;

import com.example.auth.client.config.FeignFormConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

import static com.example.auth.constant.ConfigConstants.AUTH_CLIENT_ALIYUN;


@FeignClient(name = AUTH_CLIENT_ALIYUN, url = "https://oauth.aliyun.com", configuration = FeignFormConfig.class)
public interface AuthAliyunClient {

    @PostMapping(value = "/v1/token")
    String accessToken(@RequestHeader Map<String, String> headers, @RequestParam Map<String, ?> form);

    @PostMapping(value = "/v1/userinfo")
    String userInfo(@RequestHeader Map<String, String> headers, @RequestParam Map<String, ?> form);

    @PostMapping(value = "/v1/token")
    String refresh(@RequestHeader Map<String, String> headers, @RequestBody Map<String, ?> form);

}
