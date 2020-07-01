package com.example.auth.client;

import com.example.auth.constant.ConfigConstants;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@FeignClient(name = ConfigConstants.AUTH_CLIENT_Dingding, url = "https://oapi.dingtalk.com")
public interface DingdingClient  {


    @PostMapping(value = "/sns/getuserinfo_bycode?signature={signature}&accessKey={accessKey}&timestamp={timestamp}")
    String userInfo(@PathVariable("signature") String signature, @PathVariable("accessKey") String accessKey
            , @PathVariable("timestamp") String timestamp, @RequestBody Map<String, ?> form);


}
