package com.example.auth.client.config;

import feign.codec.Encoder;
import feign.form.spring.SpringFormEncoder;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.support.SpringEncoder;
import org.springframework.context.annotation.Bean;


public class FeignFormConfig {
    @Bean
    Encoder feignFormEncoder(ObjectFactory<HttpMessageConverters> converters) {
        return new SpringFormEncoder(new SpringEncoder(converters));
    }
    //https://cloud.spring.io/spring-cloud-netflix/multi/multi_spring-cloud-feign.html#spring-cloud-feign-hystrix
//    @Bean
//    @Scope("prototype")
//    public Feign.Builder feignBuilder() {
//        return Feign.builder();
//    }

}
