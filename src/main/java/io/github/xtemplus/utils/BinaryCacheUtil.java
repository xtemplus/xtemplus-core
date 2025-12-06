package io.github.xtemplus.utils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * 二进制缓存工具类
 * 用于将对象序列化为二进制格式并加密存储
 *
 * @author template
 */
public class BinaryCacheUtil {

    /**
     * 将对象序列化为二进制字节数组
     *
     * @param obj 要序列化的对象
     * @return 序列化后的字节数组
     */
    public static byte[] serialize(Object obj) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeObject(obj);
            oos.flush();
            return baos.toByteArray();
        } catch (IOException e) {
            Log.error("序列化对象失败", e);
            throw new RuntimeException("序列化对象失败: " + e.getMessage(), e);
        }
    }

    /**
     * 从二进制字节数组反序列化对象
     *
     * @param data 序列化的字节数组
     * @return 反序列化后的对象
     */
    public static Object deserialize(byte[] data) {
        try (ByteArrayInputStream bais = new ByteArrayInputStream(data);
             ObjectInputStream ois = new ObjectInputStream(bais)) {
            return ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            Log.error("反序列化对象失败", e);
            throw new RuntimeException("反序列化对象失败: " + e.getMessage(), e);
        }
    }

    /**
     * 将对象序列化并加密保存到文件
     *
     * @param obj      要保存的对象
     * @param file     目标文件
     * @param password 加密密码（使用机器码）
     */
    public static void saveEncrypted(Object obj, File file, String password) {
        try {
            // 1. 序列化对象为字节数组
            byte[] serializedData = serialize(obj);

            // 2. Base64编码（避免直接转换字符串可能的数据丢失）
            String base64Data = Base64.getEncoder().encodeToString(serializedData);

            // 3. 使用AES加密Base64字符串
            String encryptedData = AESUtil.encrypt(base64Data, password);

            // 4. 将加密后的字符串转换为字节数组写入文件
            byte[] encryptedBytes = encryptedData.getBytes(StandardCharsets.UTF_8);

            // 确保父目录存在
            File parentDir = file.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();
            }

            // 5. 写入文件（二进制格式）
            try (FileOutputStream fos = new FileOutputStream(file)) {
                fos.write(encryptedBytes);
                fos.flush();
            }

            // 6. 设置文件为隐藏（Windows）
            setHiddenFile(file);

        } catch (Exception e) {
            Log.error("保存加密文件失败", e);
            throw new RuntimeException("保存加密文件失败: " + e.getMessage(), e);
        }
    }

    /**
     * 从加密文件读取并反序列化对象
     *
     * @param file     加密文件
     * @param password 解密密码（使用机器码）
     * @return 反序列化后的对象
     */
    public static Object loadEncrypted(File file, String password) {
        try {
            if (!file.exists()) {
                return null;
            }

            // 1. 读取文件字节数组
            byte[] encryptedBytes;
            try (FileInputStream fis = new FileInputStream(file);
                 ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                byte[] buffer = new byte[8192];
                int length;
                while ((length = fis.read(buffer)) != -1) {
                    baos.write(buffer, 0, length);
                }
                encryptedBytes = baos.toByteArray();
            }

            // 2. 转换为字符串
            String encryptedData = new String(encryptedBytes, StandardCharsets.UTF_8);

            // 3. AES解密
            String base64Data = AESUtil.decrypt(encryptedData, password);

            // 4. Base64解码
            byte[] serializedData = Base64.getDecoder().decode(base64Data);

            // 5. 反序列化
            return deserialize(serializedData);

        } catch (Exception e) {
            Log.warn("读取加密文件失败", e);
            return null;
        }
    }

    /**
     * 设置文件为隐藏（Windows系统）
     *
     * @param file 要隐藏的文件
     */
    private static void setHiddenFile(File file) {
        try {
            String os = System.getProperty("os.name").toLowerCase();
            if (os.contains("win")) {
                // Windows系统：使用Java NIO的Files类设置隐藏属性（更可靠）
                try {
                    java.nio.file.Files.setAttribute(
                            file.toPath(),
                            "dos:hidden",
                            true,
                            java.nio.file.LinkOption.NOFOLLOW_LINKS
                    );
                } catch (Exception e) {
                    // 如果NIO方法失败，尝试使用attrib命令
                    Process process = Runtime.getRuntime().exec(
                            "attrib +H \"" + file.getAbsolutePath() + "\"");
                    process.waitFor();
                }
            } else {
                // Linux/Mac系统，文件名以.开头即为隐藏文件
                // 这里不修改文件名，因为文件名已经确定
                // 如果需要，可以在创建文件时就使用隐藏文件名
            }
        } catch (Exception e) {
            Log.debug("设置文件隐藏属性失败（可能无权限）: {}", e.getMessage());
            // 忽略错误，不影响功能
        }
    }

    /**
     * 获取隐藏的缓存目录路径
     * 使用系统临时目录下的隐藏文件夹
     *
     * @return 缓存目录路径
     */
    public static String getHiddenCacheDir() {
        String os = System.getProperty("os.name").toLowerCase();
        String cacheDir;

        if (os.contains("win")) {
            // Windows系统：使用系统盘下的隐藏目录
            // 例如：C:\Windows\Temp\.syscache 或 C:\ProgramData\.syscache
            String systemDrive = System.getenv("SystemDrive");
            if (systemDrive == null) {
                systemDrive = "C:";
            }
            // 使用ProgramData目录（系统级，更隐蔽）
            String programData = System.getenv("ProgramData");
            if (programData != null) {
                cacheDir = programData + File.separator + ".syscache" + File.separator + "license";
            } else {
                cacheDir = systemDrive + File.separator + ".syscache" + File.separator + "license";
            }
        } else if (os.contains("mac")) {
            // Mac系统：使用~/Library/Caches下的隐藏目录
            cacheDir = System.getProperty("user.home") + File.separator +
                    ".Library" + File.separator + "Caches" + File.separator +
                    ".syscache" + File.separator + "license";
        } else {
            // Linux系统：使用/tmp下的隐藏目录
            cacheDir = "/tmp/.syscache/license";
        }

        return cacheDir;
    }

    /**
     * 创建隐藏目录
     *
     * @param dirPath 目录路径
     * @return 创建的目录File对象
     */
    public static File createHiddenDir(String dirPath) {
        File dir = new File(dirPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        // 尝试设置目录为隐藏（Windows）
        try {
            String os = System.getProperty("os.name").toLowerCase();
            if (os.contains("win")) {
                // 使用Java NIO的Files类设置隐藏属性（更可靠）
                try {
                    java.nio.file.Files.setAttribute(
                            dir.toPath(),
                            "dos:hidden",
                            true,
                            java.nio.file.LinkOption.NOFOLLOW_LINKS
                    );
                } catch (Exception e) {
                    // 如果NIO方法失败，尝试使用attrib命令
                    Process process = Runtime.getRuntime().exec(
                            "attrib +H \"" + dir.getAbsolutePath() + "\"");
                    process.waitFor();
                }
            }
        } catch (Exception e) {
            Log.debug("设置目录隐藏属性失败（可能无权限）: {}", e.getMessage());
            // 忽略错误，不影响功能
        }

        return dir;
    }
}

