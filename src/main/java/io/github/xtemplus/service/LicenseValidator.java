package io.github.xtemplus.service;

import io.github.xtemplus.utils.AESUtil;
import io.github.xtemplus.utils.MachineCodeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * 许可证验证器
 * 用于验证启动时的许可证信息
 *
 * @author template
 */
public class LicenseValidator {

    private static final Logger log = LoggerFactory.getLogger(LicenseValidator.class);
    
    /**
     * 日期格式（年月日，6位数字，统一为0点0分0秒）
     */
    private static final String DATE_FORMAT = "yyMMdd";
    
    /**
     * 分隔符（用于分隔机器码和有效时间）
     */
    private static final String SEPARATOR = ",";

    /**
     * 验证许可证
     * 解密流程：
     * 1. 原始文本（license-key）是一个AES加密串
     * 2. 密钥1：当前机器的机器码中的数字全部转化成对应的小写字符，比如1对应#a，2对应#b，得到一个完整的含#的纯英文字符串
     * 3. 使用密钥1对原始文本进行AES解密，得到加密串2
     * 4. 密钥2：当前机器的机器码的第一个数字，比如是9则截取前9位数，如果出现不存在数字或者数字为0或者截取之后出现异常，则直接截取前9位数
     * 5. 使用密钥2对加密串2进行AES解密，得到：机器码,过期时间
     * 6. 比对机器码是否一致、时间是否过期
     *
     * @param encryptedLicenseKey 加密后的许可证密钥（从配置文件读取的原始文本）
     * @throws LicenseValidationException 验证失败时抛出异常
     */
    public static void validate(String encryptedLicenseKey) throws LicenseValidationException {
        if (encryptedLicenseKey == null || encryptedLicenseKey.trim().isEmpty()) {
            throw new LicenseValidationException("许可证密钥未配置，请在配置文件中设置 template.core.license-key");
        }

        try {
            // 获取当前机器的机器码
            String currentMachineCode = MachineCodeUtil.getMachineCode();
            
            // 1. 密钥1：当前机器的机器码中的数字全部转化成对应的小写字符（1->#a, 2->#b, ..., 9->#i, 0->#j）
            String key1 = convertDigitsToLettersWithHash(currentMachineCode);
            
            // 2. 使用密钥1对原始文本进行AES解密，得到加密串2
            String encryptedString2;
            try {
                encryptedString2 = AESUtil.decrypt(encryptedLicenseKey, key1);
            } catch (Exception e) {
                log.error("第一次AES解密失败", e);
                throw new LicenseValidationException("许可证密钥解密失败（第一次解密），可能是许可证格式不正确", e);
            }

            // 3. 密钥2：当前机器的机器码的第一个数字，如果不存在或为0或异常，则截取前9位数
            String key2 = getKeyFromFirstDigit(currentMachineCode);
            
            // 4. 使用密钥2对加密串2进行AES解密，得到：机器码,过期时间
            String decryptedLicense;
            try {
                decryptedLicense = AESUtil.decrypt(encryptedString2, key2);
            } catch (Exception e) {
                log.error("第二次AES解密失败", e);
                throw new LicenseValidationException("许可证密钥解密失败（第二次解密），可能是许可证格式不正确", e);
            }

            // 5. 解析解密后的许可证信息（格式：机器码,到期时间）
            String[] parts = decryptedLicense.split(SEPARATOR);
            if (parts.length != 2) {
                throw new LicenseValidationException("许可证格式不正确，应为：机器码,到期时间（格式：机器码,20251101）");
            }

            String licenseMachineCode = parts[0];
            String licenseExpireDate = parts[1];

            // 6. 验证机器码
            if (!currentMachineCode.equals(licenseMachineCode)) {
                log.error("机器码验证失败。当前机器码: {}, 许可证机器码: {}", currentMachineCode, licenseMachineCode);
                throw new LicenseValidationException("许可证验证失败：当前设备与许可证不匹配");
            }

            // 7. 验证有效时间（格式：yyMMdd，统一为0点0分0秒）
            SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
            // 设置年份解释规则：00-99 对应 2000-2099
            Calendar calendar = new GregorianCalendar(2000, Calendar.JANUARY, 1);
            sdf.set2DigitYearStart(calendar.getTime());
            Date expireDate;
            try {
                expireDate = sdf.parse(licenseExpireDate);
            } catch (ParseException e) {
                log.error("许可证有效期格式错误: {}, 应为格式: {}", licenseExpireDate, DATE_FORMAT, e);
                throw new LicenseValidationException("许可证有效期格式错误，应为: " + DATE_FORMAT + "（例如：250101）", e);
            }

            // 设置过期时间为当天的23:59:59，这样当天还能使用
            Date currentDate = new Date();
            // 计算过期日期的次日0点作为过期时间点
            long expireTime = expireDate.getTime() + 24 * 60 * 60 * 1000; // 加1天
            Date actualExpireDate = new Date(expireTime);
            
            if (currentDate.after(actualExpireDate) || currentDate.equals(actualExpireDate)) {
                log.error("许可证已过期。过期时间: {}, 当前时间: {}", licenseExpireDate, sdf.format(currentDate));
                throw new LicenseValidationException("许可证验证失败：许可证已过期，过期时间为 " + licenseExpireDate);
            }

            // 验证成功
            log.info("许可证验证成功，有效期至: {} 00:00:00", licenseExpireDate);
            
            // 更新校验信息（本地验证也记录）
            try {
                io.github.xtemplus.service.LicenseCacheManager.updateValidationInfo(currentMachineCode, true);
            } catch (Exception e) {
                // 忽略错误，不影响验证流程
                log.debug("更新校验信息失败", e);
            }
        } catch (LicenseValidationException e) {
            throw e;
        } catch (Exception e) {
            log.error("许可证验证过程中发生未知错误", e);
            throw new LicenseValidationException("许可证验证失败：发生未知错误", e);
        }
    }
    
    /**
     * 将机器码中的数字转换为对应的小写字母（带#前缀）
     * 1->#a, 2->#b, 3->#c, 4->#d, 5->#e, 6->#f, 7->#g, 8->#h, 9->#i, 0->#j
     *
     * @param machineCode 机器码
     * @return 转换后的含#的纯英文字符串
     */
    private static String convertDigitsToLettersWithHash(String machineCode) {
        if (machineCode == null) {
            return "";
        }
        StringBuilder result = new StringBuilder();
        for (char c : machineCode.toCharArray()) {
            if (Character.isDigit(c)) {
                // 将数字转换为字母（带#前缀）：1->#a, 2->#b, ..., 9->#i, 0->#j
                int digit = Character.getNumericValue(c);
                if (digit == 0) {
                    result.append("#j");
                } else {
                    result.append("#").append((char) ('a' + digit - 1));
                }
            } else {
                // 非数字字符保持不变
                result.append(c);
            }
        }
        return result.toString();
    }
    
    /**
     * 根据机器码的第一个数字获取密钥
     * 规则：找到第一个数字，如果是9则截取前9位数，如果不存在数字或数字为0或截取异常，则直接截取前9位数
     *
     * @param machineCode 机器码
     * @return 密钥（截取的机器码子串）
     */
    private static String getKeyFromFirstDigit(String machineCode) {
        if (machineCode == null || machineCode.isEmpty()) {
            return machineCode != null && machineCode.length() >= 9 ? machineCode.substring(0, 9) : machineCode;
        }
        
        // 查找第一个数字
        int firstDigitIndex = -1;
        int firstDigit = -1;
        for (int i = 0; i < machineCode.length(); i++) {
            char c = machineCode.charAt(i);
            if (Character.isDigit(c)) {
                firstDigitIndex = i;
                firstDigit = Character.getNumericValue(c);
                break;
            }
        }
        
        // 如果没有找到数字，或者数字为0，或者截取后长度不足，则截取前9位数
        if (firstDigitIndex == -1 || firstDigit == 0) {
            return machineCode.length() >= 9 ? machineCode.substring(0, 9) : machineCode;
        }
        
        // 根据第一个数字截取对应长度的字符串
        try {
            int length = firstDigit;
            if (machineCode.length() >= length) {
                return machineCode.substring(0, length);
            } else {
                // 如果长度不足，截取前9位数
                return machineCode.length() >= 9 ? machineCode.substring(0, 9) : machineCode;
            }
        } catch (Exception e) {
            // 出现异常，截取前9位数
            log.warn("截取密钥时出现异常，使用前9位数: {}", e.getMessage());
            return machineCode.length() >= 9 ? machineCode.substring(0, 9) : machineCode;
        }
    }

    /**
     * 生成许可证密钥
     * 加密流程（与解密流程相反）：
     * 1. 准备原始内容：机器码,过期时间
     * 2. 第一次加密：密钥（当前机器的机器码的第一个数字，比如是9则截取前9位数，如果出现不存在数字或者数字为0或者截取之后出现异常，则直接截取前9位数）
     * 3. 第二次加密：密钥（当前机器的机器码中的数字全部转化成对应的小写字符，比如1对应#a，2对应#b，得到一个完整的含#的纯英文字符串）
     *
     * @param expireDate 过期日期（格式：yyMMdd，例如：250101）
     * @return 双重加密后的许可证密钥（将配置到 license-key 字段）
     */
    public static String generateLicense(String expireDate) {
        // 验证日期格式
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
            // 设置年份解释规则：00-99 对应 2000-2099
            Calendar calendar = new GregorianCalendar(2000, Calendar.JANUARY, 1);
            sdf.set2DigitYearStart(calendar.getTime());
            sdf.parse(expireDate);
        } catch (ParseException e) {
            throw new IllegalArgumentException("过期日期格式错误，应为: " + DATE_FORMAT + "（例如：250101）");
        }
        
        // 获取当前机器的机器码
        String machineCode = MachineCodeUtil.getMachineCode();
        
        // 1. 准备原始内容：机器码,过期时间
        String licenseContent = machineCode + SEPARATOR + expireDate;
        
        // 2. 第一次加密：密钥（当前机器的机器码的第一个数字，比如是9则截取前9位数，如果出现不存在数字或者数字为0或者截取之后出现异常，则直接截取前9位数）
        String key2 = getKeyFromFirstDigit(machineCode);
        String encryptedString2 = AESUtil.encrypt(licenseContent, key2);
        
        // 3. 第二次加密：密钥（当前机器的机器码中的数字全部转化成对应的小写字符，比如1对应#a，2对应#b，得到一个完整的含#的纯英文字符串）
        String key1 = convertDigitsToLettersWithHash(machineCode);
        String finalLicenseKey = AESUtil.encrypt(encryptedString2, key1);
        
        return finalLicenseKey;
    }
    

    /**
     * 许可证验证异常
     */
    public static class LicenseValidationException extends Exception {
        public LicenseValidationException(String message) {
            super(message);
        }

        public LicenseValidationException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}

