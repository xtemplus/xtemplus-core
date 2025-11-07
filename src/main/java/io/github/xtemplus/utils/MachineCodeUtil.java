package io.github.xtemplus.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.NetworkInterface;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Enumeration;

/**
 * 机器码工具类
 * 用于获取设备唯一标识（机器码）
 *
 * @author template
 */
public class MachineCodeUtil {

    private static final Logger log = LoggerFactory.getLogger(MachineCodeUtil.class);

    /**
     * 获取机器码
     * 通过MAC地址、CPU信息、系统信息等生成唯一标识
     *
     * @return 机器码（MD5加密后的32位字符串）
     */
    public static String getMachineCode() {
        try {
            StringBuilder machineId = new StringBuilder();
            
            // 1. 获取MAC地址
            String macAddress = getMacAddress();
            if (macAddress != null && !macAddress.isEmpty()) {
                machineId.append(macAddress);
            }
            
            // 2. 获取CPU序列号
            String cpuSerial = getCpuSerial();
            if (cpuSerial != null && !cpuSerial.isEmpty()) {
                machineId.append(cpuSerial);
            }
            
            // 3. 获取主板序列号（Windows）
            String motherboardSerial = getMotherboardSerial();
            if (motherboardSerial != null && !motherboardSerial.isEmpty()) {
                machineId.append(motherboardSerial);
            }
            
            // 4. 获取系统信息
            String osName = System.getProperty("os.name");
            String osVersion = System.getProperty("os.version");
            String userName = System.getProperty("user.name");
            machineId.append(osName).append(osVersion).append(userName);
            
            // 如果获取到的信息为空，使用JVM信息作为备选
            if (machineId.length() == 0) {
                machineId.append(System.getProperty("java.vm.name"));
                machineId.append(System.getProperty("java.vm.version"));
            }
            log.info("机器码1111: {}", machineId.toString());
            // 生成MD5摘要
            return md5(machineId.toString());
        } catch (Exception e) {
            log.error("获取机器码失败", e);
            // 如果获取失败，使用JVM信息生成一个相对稳定的标识
            return md5(System.getProperty("java.vm.name") + System.getProperty("user.dir"));
        }
    }

    /**
     * 获取MAC地址
     */
    private static String getMacAddress() {
        try {
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = networkInterfaces.nextElement();
                if (networkInterface.isLoopback() || networkInterface.isVirtual() || !networkInterface.isUp()) {
                    continue;
                }
                byte[] macBytes = networkInterface.getHardwareAddress();
                if (macBytes != null && macBytes.length > 0) {
                    StringBuilder mac = new StringBuilder();
                    for (int i = 0; i < macBytes.length; i++) {
                        if (i > 0) {
                            mac.append("-");
                        }
                        mac.append(String.format("%02X", macBytes[i]));
                    }
                    return mac.toString();
                }
            }
        } catch (Exception e) {
            log.warn("获取MAC地址失败", e);
        }
        return null;
    }

    /**
     * 获取CPU序列号（Windows）
     */
    private static String getCpuSerial() {
        String os = System.getProperty("os.name").toLowerCase();
        try {
            Process process;
            if (os.contains("win")) {
                // Windows系统
                process = Runtime.getRuntime().exec("wmic cpu get ProcessorId");
            } else if (os.contains("mac")) {
                // Mac系统
                process = Runtime.getRuntime().exec("sysctl -n machdep.cpu.brand_string");
            } else {
                // Linux系统
                process = Runtime.getRuntime().exec("cat /proc/cpuinfo | grep 'Serial' | head -1");
            }
            
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8));
            StringBuilder result = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty() && !line.equalsIgnoreCase("ProcessorId")) {
                    result.append(line);
                }
            }
            reader.close();
            process.waitFor();
            return result.toString();
        } catch (Exception e) {
            log.warn("获取CPU序列号失败", e);
            return null;
        }
    }

    /**
     * 获取主板序列号（Windows）
     */
    private static String getMotherboardSerial() {
        String os = System.getProperty("os.name").toLowerCase();
        try {
            if (os.contains("win")) {
                Process process = Runtime.getRuntime().exec("wmic baseboard get serialnumber");
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8));
                StringBuilder result = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    line = line.trim();
                    if (!line.isEmpty() && !line.equalsIgnoreCase("SerialNumber")) {
                        result.append(line);
                    }
                }
                reader.close();
                process.waitFor();
                return result.toString();
            }
        } catch (Exception e) {
            log.warn("获取主板序列号失败", e);
        }
        return null;
    }

    /**
     * MD5加密
     */
    private static String md5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] bytes = md.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            log.error("MD5加密失败", e);
            return input;
        }
    }
}

