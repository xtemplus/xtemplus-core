package io.github.xtemplus.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.MessageDigest;

/**
 * 文件MD5工具类
 * 用于计算文件的MD5值，防止代码被篡改
 *
 * @author template
 */
public class FileMD5Util {

    private static final Logger log = LoggerFactory.getLogger(FileMD5Util.class);

    /**
     * 计算文件的MD5值
     *
     * @param file 文件
     * @return MD5值（32位十六进制字符串）
     */
    public static String getFileMD5(File file) {
        if (file == null || !file.exists() || !file.isFile()) {
            throw new IllegalArgumentException("文件不存在或不是有效文件: " + (file != null ? file.getPath() : "null"));
        }

        try (FileInputStream fis = new FileInputStream(file)) {
            return calculateMD5(fis);
        } catch (Exception e) {
            log.error("计算文件MD5失败: {}", file.getPath(), e);
            throw new RuntimeException("计算文件MD5失败: " + e.getMessage(), e);
        }
    }

    /**
     * 计算输入流的MD5值
     *
     * @param inputStream 输入流
     * @return MD5值（32位十六进制字符串）
     */
    public static String calculateMD5(InputStream inputStream) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] buffer = new byte[8192];
            int length;
            while ((length = inputStream.read(buffer)) != -1) {
                md.update(buffer, 0, length);
            }
            byte[] digest = md.digest();
            return bytesToHex(digest);
        } catch (Exception e) {
            log.error("计算MD5失败", e);
            throw new RuntimeException("计算MD5失败: " + e.getMessage(), e);
        }
    }

    /**
     * 获取当前运行的JAR文件的MD5值
     * 用于防止代码被篡改
     *
     * @return JAR文件的MD5值，如果无法获取则返回null
     */
    public static String getJarFileMD5() {
        try {
            // 获取当前类的保护域和代码源
            String classPath = FileMD5Util.class.getProtectionDomain()
                    .getCodeSource()
                    .getLocation()
                    .getPath();

            // 处理URL编码（Windows路径可能有特殊字符）
            if (classPath.startsWith("/") && System.getProperty("os.name").toLowerCase().contains("win")) {
                classPath = classPath.substring(1);
            }

            // 如果是jar文件
            if (classPath.endsWith(".jar")) {
                File jarFile = new File(classPath);
                if (jarFile.exists()) {
                    return getFileMD5(jarFile);
                }
            }

            log.warn("无法获取JAR文件路径，当前类路径: {}", classPath);
            return null;
        } catch (Exception e) {
            log.error("获取JAR文件MD5失败", e);
            return null;
        }
    }

    /**
     * 将字节数组转换为十六进制字符串
     *
     * @param bytes 字节数组
     * @return 十六进制字符串
     */
    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}

