package com.example.auth.config;

import com.example.auth.constant.ConfigConstants;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
public class ClientConfig {
    private String client_id;
    private String client_secret;
    private String redirect_uri;

    @Bean(ConfigConstants.AUTH_CLIENT_ALIYUN+ ConfigConstants.AUTH_CONFIG_SUFFIX)
    @ConfigurationProperties("oauth2.aliyun")
    public ClientConfig aliyun() {
        return new ClientConfig();
    }

    @Bean(ConfigConstants.AUTH_CLIENT_Dingding+ ConfigConstants.AUTH_CONFIG_SUFFIX)
    @ConfigurationProperties("oauth2.dingding")
    public ClientConfig dingding() {
        return new ClientConfig();
    }

    @Bean(ConfigConstants.AUTH_CLIENT_ALIPAY+ ConfigConstants.AUTH_CONFIG_SUFFIX)
    @ConfigurationProperties("oauth2.alipay")
    public ClientConfig aplipay() {
        return new ClientConfig();
    }
}
