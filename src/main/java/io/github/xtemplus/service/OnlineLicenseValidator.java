package io.github.xtemplus.service;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import io.github.xtemplus.utils.FileMD5Util;
import io.github.xtemplus.utils.MachineCodeUtil;
import io.github.xtemplus.utils.RSAUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * 在线License验证器
 * 通过访问授权服务器进行在线验证
 *
 * @author template
 */
public class OnlineLicenseValidator {

    private static final Logger log = LoggerFactory.getLogger(OnlineLicenseValidator.class);

    /**
     * 验证License
     *
     * @param encryptedAuthUrl    加密的授权服务器URL（使用RSA公钥加密）
     * @param encryptedPrivateKey 加密的RSA私钥（使用JAR文件MD5值进行AES加密）
     * @throws LicenseValidator.LicenseValidationException 验证失败时抛出异常
     */
    public static void validate(String encryptedAuthUrl, String encryptedPrivateKey) throws LicenseValidator.LicenseValidationException {
        try {
            // 1. 获取当前机器的机器码
            String machineCode = MachineCodeUtil.getMachineCode();

            // 2. 检查缓存
            LicenseCacheManager.LicenseCache cache = LicenseCacheManager.getCache(machineCode);
            if (cache != null && cache.isValid()) {
                log.info("使用缓存的License验证结果，有效期至: {}", cache.getExpireDate());
                return;
            }

            // 3. 检查失败记录
            LicenseCacheManager.FailureRecord failureRecord = LicenseCacheManager.getFailureRecord(machineCode);
            if (failureRecord != null) {
                // 如果失败次数 >= 2，强制退出
                if (failureRecord.getFailureCount() >= 2) {
                    log.error("License验证失败次数 >= 2，程序将退出");
                    System.exit(1);
                    return;
                }
                // 如果失败次数 == 1，检查是否在宽限期内
                if (failureRecord.getFailureCount() == 1) {
                    long currentTime = System.currentTimeMillis();
                    if (currentTime - failureRecord.getFirstFailureTime() <= 24 * 60 * 60 * 1000L) {
                        // 在宽限期内，允许继续运行
                        log.warn("License验证失败，但在宽限期内（1天），允许继续运行");
                        return;
                    } else {
                        // 超过宽限期，清除失败记录，重新验证
                        LicenseCacheManager.clearFailureRecord(machineCode);
                    }
                }
            }

            // 4. 获取JAR文件MD5值
            String jarFileMd5 = FileMD5Util.getJarFileMD5();
            if (jarFileMd5 == null) {
                log.warn("无法获取JAR文件MD5，跳过在线验证");
                throw new LicenseValidator.LicenseValidationException("无法获取JAR文件MD5，无法进行在线验证");
            }

            // 5. 解密私钥（私钥用文件的MD5值加密存储）
            String privateKey;
            try {
                // 使用JAR文件MD5值解密RSA私钥
                privateKey = RSAUtil.decryptPublicKeyWithMd5(encryptedPrivateKey, jarFileMd5);
            } catch (Exception e) {
                log.error("解密RSA私钥失败，可能代码被篡改", e);
                throw new LicenseValidator.LicenseValidationException("解密RSA私钥失败，可能代码被篡改", e);
            }

            // 6. 解密授权服务器URL（使用RSA私钥解密）
            String authUrl;
            try {
                // URL是用RSA公钥加密的，这里用私钥解密
                authUrl = RSAUtil.decrypt(encryptedAuthUrl, privateKey);
            } catch (Exception e) {
                log.error("解密授权服务器URL失败", e);
                throw new LicenseValidator.LicenseValidationException("解密授权服务器URL失败，可能代码被篡改", e);
            }

            // 7. 构造请求参数
            Map<String, Object> params = new HashMap<>();
            params.put("machineCode", machineCode);

            HttpResponse response;
            try {
                response = HttpRequest.post(authUrl)
                        .header("Content-Type", "application/json")
                        .body(JSON.toJSONString(params)) // 使用 FastJSON2 序列化
                        .timeout(10000) // 10秒超时
                        .execute();
            } catch (Exception e) {
                log.error("访问授权服务器失败", e);
                // 网络错误，记录失败但允许在宽限期内继续运行
                LicenseCacheManager.FailureRecord record = LicenseCacheManager.recordFailure(machineCode);
                if (record == null) {
                    // 超过宽限期，强制退出
                    log.error("License验证失败次数超过限制，程序将退出");
                    System.exit(1);
                    return;
                }
                throw new LicenseValidator.LicenseValidationException("访问授权服务器失败: " + e.getMessage(), e);
            }

            // 8. 解析响应
            if (response.getStatus() != 200) {
                log.error("授权服务器返回错误状态码: {}", response.getStatus());
                LicenseCacheManager.FailureRecord record = LicenseCacheManager.recordFailure(machineCode);
                if (record == null) {
                    log.error("License验证失败次数超过限制，程序将退出");
                    System.exit(1);
                    return;
                }
                throw new LicenseValidator.LicenseValidationException("授权服务器返回错误: " + response.getStatus());
            }

            String responseBody = response.body();
            JSONObject jsonObject;
            try {
                jsonObject = JSON.parseObject(responseBody); // 使用 FastJSON2 解析
            } catch (Exception e) {
                log.error("解析授权服务器响应失败", e);
                throw new LicenseValidator.LicenseValidationException("解析授权服务器响应失败", e);
            }

            // 9. 检查验证结果
            Boolean valid = jsonObject.getBoolean("valid");
            if (valid == null || !valid) {
                String errorMessage = jsonObject.getString("message");
                log.error("License验证失败: {}", errorMessage != null ? errorMessage : "未知错误");
                LicenseCacheManager.FailureRecord record = LicenseCacheManager.recordFailure(machineCode);
                if (record == null) {
                    log.error("License验证失败次数超过限制，程序将退出");
                    System.exit(1);
                    return;
                }
                throw new LicenseValidator.LicenseValidationException("License验证失败: " + (errorMessage != null ? errorMessage : "未知错误"));
            }

            // 10. 获取过期日期
            String expireDate = jsonObject.getString("expireDate");

            // 11. 验证成功，保存缓存并清除失败记录
            LicenseCacheManager.saveCache(machineCode, true, expireDate);
            LicenseCacheManager.clearFailureRecord(machineCode);
            log.info("License在线验证成功，有效期至: {}", expireDate);

        } catch (LicenseValidator.LicenseValidationException e) {
            throw e;
        } catch (Exception e) {
            log.error("在线License验证过程中发生未知错误", e);
            throw new LicenseValidator.LicenseValidationException("在线License验证失败: " + e.getMessage(), e);
        }
    }
}