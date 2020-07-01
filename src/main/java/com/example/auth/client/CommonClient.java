package com.example.auth.client;


import com.example.auth.exception.BusinessException;
import com.example.auth.message.AuthCallback;

import static com.example.auth.message.ServerMsg.UNSUPPORTED_OPERATION;

public interface CommonClient {

//    String accessToken(Map<String, String> headers, Map<String, ?> form);
//
//    String userInfo(Map<String, String> headers, Map<String, ?> form);

//    String getAccessToken(String code, String redirectUrl,String client_id,String client_secret) throws BusinessException;
    String getAuthorize(String state);
//    String getUserInfoByToken(String accessToken) throws BusinessException;
    String getUserInfo(AuthCallback authCallback) throws BusinessException;
    String getResponse(String rawUserInfo);

    default String revoke() throws BusinessException {
        throw new BusinessException(UNSUPPORTED_OPERATION);
    }

    default String refresh() throws BusinessException {
        throw new BusinessException(UNSUPPORTED_OPERATION);
    }

    default String getName() {
        return this.getClass().getSimpleName();
    }

}
