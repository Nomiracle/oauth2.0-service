package com.example.auth.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

import static com.example.auth.constant.ConfigConstants.AUTH_CLIENT_WEIBO;

@FeignClient(name = AUTH_CLIENT_WEIBO, url = "https://api.weibo.com")
public interface WeiBoClient {

    @PostMapping(value = "/oauth2/access_token")
    String accessToken(@RequestParam Map<String, ?> form);

    @PostMapping(value = "/oauth2/get_token_info")
    String get_token_info(@RequestParam Map<String, ?> form);

    @GetMapping(value = "/2/users/show.json")
    String userInfo(@RequestParam Map<String, ?> form);

}
