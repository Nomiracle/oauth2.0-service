package com.example.auth.message;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthCallback implements Serializable {


    private String code;


    private String auth_code;


    private String state;


    private String authorization_code;


    private String oauth_token;

    private String oauth_verifier;

}
