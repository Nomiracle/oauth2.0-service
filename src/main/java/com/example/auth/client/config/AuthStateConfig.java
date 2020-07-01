package com.example.auth.client.config;

import com.example.auth.utils.UuidUtils;

public class AuthStateConfig {

    public static String createState() {
        return UuidUtils.getUUID();
    }
}
