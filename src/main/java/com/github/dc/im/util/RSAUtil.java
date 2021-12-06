package com.github.dc.im.util;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.util.Base64Utils;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * <p>
 * rsa 加解密
 * </p>
 *
 * @author wangpeiyuan
 * @date 2021/12/6 8:19
 */
@UtilityClass
@Slf4j
public class RSAUtil {
    public static String RSA_ALGORITHM = "RSA";

    /**
     * 密钥长度，DSA算法的默认密钥长度是1024
     * 密钥长度必须是64的倍数，在512到65536位之间
     */
    private static final int KEY_SIZE = 1024;

    /**
     * 生成密钥对
     *
     * @return 密钥对对象
     */
    public static Pair<RSAPublicKey, RSAPrivateKey> createKeys() {
        try {
            //KeyPairGenerator用于生成公钥和私钥对。密钥对生成器是使用 getInstance 工厂方法
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(RSA_ALGORITHM);
            keyPairGenerator.initialize(KEY_SIZE);
            KeyPair keyPair = keyPairGenerator.generateKeyPair();
            RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
            RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
            return Pair.of(publicKey, privateKey);
        } catch (NoSuchAlgorithmException e) {
            log.warn("[RSA]生成密钥对失败", e);
        }

        return null;
    }

    /**
     * 获取私钥
     *
     * @param keyStore
     * @return
     */
    private static byte[] getPrivateKey(Pair<RSAPublicKey, RSAPrivateKey> keyStore) {
        return keyStore.getRight().getEncoded();
    }

    /**
     * 获取私钥
     *
     * @param keyStore
     * @return
     */
    public static String getPrivateKeyString(Pair<RSAPublicKey, RSAPrivateKey> keyStore) {
        return Base64Utils.encodeToString(keyStore.getRight().getEncoded());
    }

    /**
     * 获取公钥
     *
     * @param keyStore
     * @return
     */
    private static byte[] getPublicKey(Pair<RSAPublicKey, RSAPrivateKey> keyStore) {
        return keyStore.getLeft().getEncoded();
    }

    /**
     * 获取公钥
     *
     * @param keyStore
     * @return
     */
    public static String getPublicKeyString(Pair<RSAPublicKey, RSAPrivateKey> keyStore) {
        return Base64Utils.encodeToString(keyStore.getLeft().getEncoded());
    }

    /**
     * 私钥加密
     *
     * @param data 待加密数据
     * @param key  密钥
     * @return byte[] 加密数据
     */
    public static byte[] encryptByPrivateKey(byte[] data, byte[] key) {
        try {
            //取得私钥
            PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(key);
            KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);
            //生成私钥
            PrivateKey privateKey = keyFactory.generatePrivate(pkcs8KeySpec);
            //数据加密
            Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
            cipher.init(Cipher.ENCRYPT_MODE, privateKey);
            return cipher.doFinal(data);
        } catch (NoSuchAlgorithmException | InvalidKeyException | NoSuchPaddingException | BadPaddingException | InvalidKeySpecException | IllegalBlockSizeException e) {
            log.warn("[RSA]私钥加密失败", e);
        }

        return null;
    }

    /**
     * 公钥加密
     *
     * @param data 待加密数据
     * @param key  密钥
     * @return 加密数据
     */
    private static byte[] encryptByPublicKey(byte[] data, byte[] key) {
        try {
            //实例化密钥工厂
            KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);
            //初始化公钥,根据给定的编码密钥创建一个新的 X509EncodedKeySpec。
            X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(key);
            PublicKey publicKey = keyFactory.generatePublic(x509EncodedKeySpec);
            //数据加密
            Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            return cipher.doFinal(data);
        } catch (NoSuchAlgorithmException | InvalidKeyException | NoSuchPaddingException | BadPaddingException | InvalidKeySpecException | IllegalBlockSizeException e) {
            log.warn("[RSA]公钥加密失败", e);
        }

        return null;
    }

    /**
     * 私钥解密
     *
     * @param data 待解密数据
     * @param key  密钥
     * @return byte[] 解密数据
     */
    public static byte[] decryptByPrivateKey(byte[] data, byte[] key) {
        try {
            //取得私钥
            PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(key);
            KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);
            //生成私钥
            PrivateKey privateKey = keyFactory.generatePrivate(pkcs8KeySpec);
            //数据解密
            Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            return cipher.doFinal(data);
        } catch (NoSuchAlgorithmException | InvalidKeyException | NoSuchPaddingException | BadPaddingException | InvalidKeySpecException | IllegalBlockSizeException e) {
            log.warn("[RSA]私钥解密失败", e);
        }

        return null;
    }

    /**
     * 私钥解密
     *
     * @param data 待解密数据
     * @param key  密钥
     * @return byte[] 解密数据
     */
    public static String decryptByPrivateKey(String data, String key) {
        return new String(decryptByPrivateKey(Base64Utils.decodeFromString(data), Base64Utils.decodeFromString(key)), StandardCharsets.UTF_8);
    }

    /**
     * 公钥解密
     *
     * @param data 待解密数据
     * @param key  密钥
     * @return byte[] 解密数据
     */
    public static byte[] decryptByPublicKey(byte[] data, byte[] key) {
        try {
            //实例化密钥工厂
            KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);
            //初始化公钥
            //密钥材料转换
            X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(key);
            //产生公钥
            PublicKey pubKey = keyFactory.generatePublic(x509KeySpec);
            //数据解密
            Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
            cipher.init(Cipher.DECRYPT_MODE, pubKey);
            return cipher.doFinal(data);
        } catch (NoSuchAlgorithmException | InvalidKeyException | NoSuchPaddingException | BadPaddingException | InvalidKeySpecException | IllegalBlockSizeException e) {
            log.warn("[RSA]公钥解密失败", e);
        }

        return null;
    }
}
