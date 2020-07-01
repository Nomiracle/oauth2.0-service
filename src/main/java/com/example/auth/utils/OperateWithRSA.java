package com.example.auth.utils;



import com.example.auth.constant.ConfigConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

@Slf4j
@Component(ConfigConstants.BEAN_NAME_RSA_OPERATION)
public class OperateWithRSA implements AsyAlgOps {

    @Override
    public byte[] signature(byte[] data, String privateKey) {
        byte[] result = null;
        try {
            byte[] keyBytes = Base64.decodeBase64(privateKey);
            // 构造PKCS8EncodedKeySpec对象
            PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(keyBytes);
            // 指定加密算法
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            // 取得私钥对象
            PrivateKey priKey = keyFactory.generatePrivate(pkcs8EncodedKeySpec);

            Signature signature = Signature.getInstance("SHA256WithRSA");
            signature.initSign(priKey);
            signature.update(data);
            result = signature.sign();
        } catch (Exception e) {
            log.error("RSA.signature", e);
        }
        return result;
    }

    @Override
    public boolean verify(byte[] data, String publicKey, byte[] sign) {
        try {
            byte[] publicKeyBytes = Base64.decodeBase64(publicKey);

            //构造X509EncodedKeySpec对象
            X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(publicKeyBytes);
            //指定加密算法
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            //取公钥匙对象
            PublicKey pubKey = keyFactory.generatePublic(x509EncodedKeySpec);

            Signature signature = Signature.getInstance("SHA1withRSA");
            signature.initVerify(pubKey);
            signature.update(data);

            //验证签名是否正常
            return signature.verify(sign);
        } catch (Exception e) {
            log.error("RSA.verify", e);
            return false;
        }
    }

    @Override
    public byte[] encrypt(byte[] data, String publicKey) {
        try {
            byte[] publicKeyBytes = Base64.decodeBase64(publicKey);

            //构造X509EncodedKeySpec对象
            X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(publicKeyBytes);
            //指定加密算法
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            //取公钥匙对象
            PublicKey pubKey = keyFactory.generatePublic(x509EncodedKeySpec);

            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, pubKey);

            return cipher.doFinal(data);
        } catch (Exception e) {
            log.error("RSA.encrypt", e);
            return null;
        }
    }

    @Override
    public byte[] decrypt(byte[] data, String privateKey) {
        try {
            byte[] privateKeyBytes = Base64.decodeBase64(privateKey);

            // 构造PKCS8EncodedKeySpec对象
            PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
            //指定加密算法
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            //取公钥匙对象
            PrivateKey priKey = keyFactory.generatePrivate(pkcs8EncodedKeySpec);

            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, priKey);

            return cipher.doFinal(data);
        } catch (Exception e) {
            log.error("RSA.decrypt", e);
            return null;
        }
    }

    @Override
    public  KeyPair pairGenerator()  {
        Security.addProvider(new BouncyCastleProvider());
        KeyPair keyPair = null;
        try{
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(1024,new SecureRandom());
            keyPair = keyPairGenerator.genKeyPair();
        }catch (NoSuchAlgorithmException e) {
            log.error("生成rsa1024密钥对失败");
        }
        return keyPair;
    }
}
