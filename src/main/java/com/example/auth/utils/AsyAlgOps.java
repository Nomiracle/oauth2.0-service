package com.example.auth.utils;

import java.security.KeyPair;

public interface AsyAlgOps {
    /**
     * 签名验证
     *
     * @param data      签名前原始数据
     * @param publicKey 公钥（base64格式）
     * @param sign      签名后的数据
     * @return boolean verify
     */
    boolean verify(byte[] data, String publicKey, byte[] sign);

    /**
     * 签名
     *
     * @param data       签名前原始数据
     * @param privateKey 私钥（base64格式）
     * @return byte[] signature
     */
    byte[] signature(byte[] data, String privateKey);

    /**
     * 加密
     *
     * @param data      原始数据
     * @param publicKey 公钥（base64格式）
     * @return byte[] encrypt
     */
    byte[] encrypt(byte[] data, String publicKey);

    /**
     * 解密
     *
     * @param data       加密数据
     * @param privateKey 公钥（base64格式）
     * @return byte[] decrypt
     */
    byte[] decrypt(byte[] data, String privateKey);

    /**
     * 生成密钥对
     * @return KeyPair
     */
     KeyPair pairGenerator() ;
}
