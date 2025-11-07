package io.github.xtemplus.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;

/**
 * AES加密解密工具类
 *
 * @author template
 */
public class AESUtil {

    private static final Logger log = LoggerFactory.getLogger(AESUtil.class);
    
    /**
     * 算法名称
     */
    private static final String ALGORITHM = "AES";
    
    /**
     * 加密算法模式：AES/CBC/PKCS5Padding
     */
    private static final String TRANSFORMATION = "AES/CBC/PKCS5Padding";
    
    /**
     * 密钥长度（128位 = 16字节）
     */
    private static final int KEY_LENGTH = 16;

    /**
     * AES加密
     *
     * @param data 待加密数据
     * @param key  密钥（将自动转换为16字节）
     * @return Base64编码的加密字符串
     */
    public static String encrypt(String data, String key) {
        try {
            // 将密钥转换为16字节
            byte[] keyBytes = getKeyBytes(key);
            SecretKeySpec secretKey = new SecretKeySpec(keyBytes, ALGORITHM);
            
            // 创建Cipher实例
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            
            // 使用密钥的前16字节作为IV（初始向量）
            IvParameterSpec ivParameterSpec = new IvParameterSpec(keyBytes);
            
            // 初始化加密模式
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivParameterSpec);
            
            // 执行加密
            byte[] encryptedBytes = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
            
            // Base64编码
            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (Exception e) {
            log.error("AES加密失败", e);
            throw new RuntimeException("AES加密失败: " + e.getMessage(), e);
        }
    }

    /**
     * AES解密
     *
     * @param encryptedData Base64编码的加密字符串
     * @param key           密钥（将自动转换为16字节）
     * @return 解密后的原始字符串
     */
    public static String decrypt(String encryptedData, String key) {
        try {
            // 将密钥转换为16字节
            byte[] keyBytes = getKeyBytes(key);
            SecretKeySpec secretKey = new SecretKeySpec(keyBytes, ALGORITHM);
            
            // 创建Cipher实例
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            
            // 使用密钥的前16字节作为IV（初始向量）
            IvParameterSpec ivParameterSpec = new IvParameterSpec(keyBytes);
            
            // 初始化解密模式
            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivParameterSpec);
            
            // Base64解码
            byte[] encryptedBytes = Base64.getDecoder().decode(encryptedData);
            
            // 执行解密
            byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
            
            // 转换为字符串
            return new String(decryptedBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error("AES解密失败", e);
            throw new RuntimeException("AES解密失败: " + e.getMessage(), e);
        }
    }

    /**
     * 将密钥转换为16字节数组
     * 如果密钥长度不足16字节，使用MD5进行填充
     * 如果密钥长度超过16字节，截取前16字节
     *
     * @param key 原始密钥
     * @return 16字节的密钥数组
     */
    private static byte[] getKeyBytes(String key) {
        try {
            // 如果密钥长度正好是16字节，直接使用
            if (key != null && key.length() == KEY_LENGTH) {
                return key.getBytes(StandardCharsets.UTF_8);
            }
            
            // 如果密钥长度不是16字节，使用MD5生成16字节的密钥
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] keyBytes = md.digest(key.getBytes(StandardCharsets.UTF_8));
            
            return keyBytes;
        } catch (Exception e) {
            log.error("密钥转换失败", e);
            throw new RuntimeException("密钥转换失败: " + e.getMessage(), e);
        }
    }


}

