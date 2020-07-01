package com.example.auth.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class URLEncodeUtils {
    public static String urlEncode(String value) {
        if (value == null) {
            return "";
        }
        try {
            String encoded = URLEncoder.encode(value, "UTF-8");
            return encoded.replace("+", "%20").replace("*", "%2A")
                    .replace("~", "%7E").replace("/", "%2F");
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException("FailedToEncodeUri", e);
        }
    }


    public static String urlEncodeAlipay(String value) {
        if (value == null) {
            return "";
        }

        return value.replace(":", "%3A").replace("/", "%2F");
    }
}
