package com.example.auth.client;

import com.example.auth.client.config.FeignFormConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

import static com.example.auth.constant.ConfigConstants.AUTH_CLIENT_ALIPAY;


@FeignClient(name = AUTH_CLIENT_ALIPAY, url = "https://openapi.alipay.com", configuration = FeignFormConfig.class)
public interface AlipayClient {
    @PostMapping(value = "/gateway.do")
    String accessToken( @RequestParam Map<String, ?> form);
}
