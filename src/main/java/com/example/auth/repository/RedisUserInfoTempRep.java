package com.example.auth.repository;

import com.example.auth.constant.ConfigConstants;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;


@Repository
public class RedisUserInfoTempRep {


    BoundHashOperations<String,String,String> RedirectUrlOps, SourceOps, UserInfoOps;


    public RedisUserInfoTempRep(StringRedisTemplate redisTemplate){
        RedirectUrlOps = redisTemplate.boundHashOps(ConfigConstants.REDIS_CACHE_KEY_REDIRECT_URL);
        SourceOps = redisTemplate.boundHashOps(ConfigConstants.REDIS_CACHE_KEY_AUTH_SOURCE);
        UserInfoOps = redisTemplate.boundHashOps(ConfigConstants.REDIS_CACHE_KEY_USER_INTO);
    }

    public void saveRedirectUrl(String state,String redirectUrl){
        RedirectUrlOps.put(state,redirectUrl);
    }

    public String getRedirectUrl(String state){

        return RedirectUrlOps.get(state);
    }
    public void deleteRedirectUrl(String state){
        RedirectUrlOps.delete(state);
    }
    public void saveSource(String state,String source){
        SourceOps.put(state,source);
    }

    public String getSource(String state){

        return SourceOps.get(state);
    }
    public void deleteSource(String state){
        SourceOps.delete(state);
    }
    public void saveUserInfo(String state,String userInfo){
        UserInfoOps.put(state,userInfo);
    }

    public String getUserInfo(String state){

        return UserInfoOps.get(state);
    }
    public void deleteUserInfo(String state){
        UserInfoOps.delete(state);
    }
    public void deleteTempCache(String state){
        deleteRedirectUrl(state);
        deleteSource(state);
        deleteUserInfo(state);
    }
}
