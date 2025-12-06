package io.github.xtemplus.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * RSA加密解密工具类
 *
 * @author template
 */
public class RSAUtil {

    private static final Logger log = LoggerFactory.getLogger(RSAUtil.class);

    /**
     * RSA算法
     */
    private static final String ALGORITHM = "RSA";

    /**
     * 密钥长度（2048位）
     */
    private static final int KEY_SIZE = 2048;

    /**
     * RSA加密
     *
     * @param data      待加密数据
     * @param publicKey 公钥（Base64编码）
     * @return 加密后的Base64字符串
     */
    public static String encrypt(String data, String publicKey) {
        try {
            byte[] keyBytes = Base64.getDecoder().decode(publicKey);
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
            PublicKey pubKey = keyFactory.generatePublic(keySpec);

            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, pubKey);

            byte[] encryptedBytes = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (Exception e) {
            log.error("RSA加密失败", e);
            throw new RuntimeException("RSA加密失败: " + e.getMessage(), e);
        }
    }

    /**
     * RSA解密
     *
     * @param encryptedData 加密数据（Base64编码）
     * @param privateKey    私钥（Base64编码）
     * @return 解密后的字符串
     */
    public static String decrypt(String encryptedData, String privateKey) {
        try {
            byte[] keyBytes = Base64.getDecoder().decode(privateKey);
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
            PrivateKey priKey = keyFactory.generatePrivate(keySpec);

            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, priKey);

            byte[] encryptedBytes = Base64.getDecoder().decode(encryptedData);
            byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
            return new String(decryptedBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error("RSA解密失败", e);
            throw new RuntimeException("RSA解密失败: " + e.getMessage(), e);
        }
    }

    /**
     * 生成RSA密钥对
     *
     * @return 密钥对（公钥和私钥都是Base64编码）
     */
    public static KeyPair generateKeyPair() {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(ALGORITHM);
            keyPairGenerator.initialize(KEY_SIZE);
            return keyPairGenerator.generateKeyPair();
        } catch (Exception e) {
            log.error("生成RSA密钥对失败", e);
            throw new RuntimeException("生成RSA密钥对失败: " + e.getMessage(), e);
        }
    }

    /**
     * 获取公钥的Base64编码
     *
     * @param keyPair 密钥对
     * @return 公钥的Base64编码
     */
    public static String getPublicKeyBase64(KeyPair keyPair) {
        return Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded());
    }

    /**
     * 获取私钥的Base64编码
     *
     * @param keyPair 密钥对
     * @return 私钥的Base64编码
     */
    public static String getPrivateKeyBase64(KeyPair keyPair) {
        return Base64.getEncoder().encodeToString(keyPair.getPrivate().getEncoded());
    }

    /**
     * 使用文件的MD5值作为密钥，对公钥进行AES加密存储
     * 这样可以防止代码被篡改
     *
     * @param publicKeyBase64 公钥（Base64编码）
     * @param fileMd5         文件的MD5值
     * @return 加密后的公钥
     */
    public static String encryptPublicKeyWithMd5(String publicKeyBase64, String fileMd5) {
        return AESUtil.encrypt(publicKeyBase64, fileMd5);
    }

    /**
     * 使用文件的MD5值作为密钥，解密公钥
     *
     * @param encryptedPublicKey 加密后的公钥
     * @param fileMd5            文件的MD5值
     * @return 解密后的公钥（Base64编码）
     */
    public static String decryptPublicKeyWithMd5(String encryptedPublicKey, String fileMd5) {
        return AESUtil.decrypt(encryptedPublicKey, fileMd5);
    }
}

