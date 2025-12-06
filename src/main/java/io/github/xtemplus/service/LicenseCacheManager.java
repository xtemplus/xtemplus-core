package io.github.xtemplus.service;

import io.github.xtemplus.utils.BinaryCacheUtil;
import io.github.xtemplus.utils.MachineCodeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.Serializable;

/**
 * License缓存管理器
 * 用于本地缓存License验证结果，避免频繁访问授权服务器
 *
 * @author template
 */
public class LicenseCacheManager {

    private static final Logger log = LoggerFactory.getLogger(LicenseCacheManager.class);

    // 使用隐藏目录，更隐蔽
    private static final String CACHE_DIR = BinaryCacheUtil.getHiddenCacheDir();
    // 使用类似系统文件的文件名，更隐蔽
    // Windows下这些文件名看起来像系统文件
    private static final String CACHE_FILE_NAME = "syscache.tmp";
    private static final String FAILURE_CACHE_FILE_NAME = "sysfail.tmp";
    private static final String VALIDATION_INFO_FILE_NAME = "sysinfo.tmp";

    /**
     * 获取缓存文件
     */
    private static File getCacheFile() {
        BinaryCacheUtil.createHiddenDir(CACHE_DIR);
        return new File(CACHE_DIR + File.separator + CACHE_FILE_NAME);
    }

    /**
     * 获取失败记录文件
     */
    private static File getFailureCacheFile() {
        BinaryCacheUtil.createHiddenDir(CACHE_DIR);
        return new File(CACHE_DIR + File.separator + FAILURE_CACHE_FILE_NAME);
    }

    /**
     * 获取校验信息文件
     */
    private static File getValidationInfoFile() {
        // 确保目录存在
        BinaryCacheUtil.createHiddenDir(CACHE_DIR);
        File infoFile = new File(CACHE_DIR + File.separator + VALIDATION_INFO_FILE_NAME);
        // 如果文件不存在，初始化它
        if (!infoFile.exists()) {
            initializeValidationInfo(infoFile);
        }
        return infoFile;
    }

    /**
     * 获取加密密钥（使用机器码）
     */
    private static String getEncryptionKey() {
        return MachineCodeUtil.getMachineCode();
    }

    /**
     * 缓存有效期（毫秒），默认24小时
     */
    private static final long CACHE_VALIDITY = 24 * 60 * 60 * 1000L;

    /**
     * 宽限期（毫秒），默认1天
     */
    private static final long GRACE_PERIOD = 24 * 60 * 60 * 1000L;

    /**
     * License缓存信息
     */
    public static class LicenseCache implements Serializable {
        private static final long serialVersionUID = 1L;
        private String machineCode;
        private boolean valid;
        private long timestamp;
        private String expireDate;

        public LicenseCache() {
        }

        public LicenseCache(String machineCode, boolean valid, long timestamp, String expireDate) {
            this.machineCode = machineCode;
            this.valid = valid;
            this.timestamp = timestamp;
            this.expireDate = expireDate;
        }

        public String getMachineCode() {
            return machineCode;
        }

        public void setMachineCode(String machineCode) {
            this.machineCode = machineCode;
        }

        public boolean isValid() {
            return valid;
        }

        public void setValid(boolean valid) {
            this.valid = valid;
        }

        public long getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(long timestamp) {
            this.timestamp = timestamp;
        }

        public String getExpireDate() {
            return expireDate;
        }

        public void setExpireDate(String expireDate) {
            this.expireDate = expireDate;
        }
    }

    /**
     * 失败记录信息
     */
    public static class FailureRecord implements Serializable {
        private static final long serialVersionUID = 1L;
        private String machineCode;
        private long firstFailureTime;
        private int failureCount;

        public FailureRecord() {
        }

        public FailureRecord(String machineCode, long firstFailureTime, int failureCount) {
            this.machineCode = machineCode;
            this.firstFailureTime = firstFailureTime;
            this.failureCount = failureCount;
        }

        public String getMachineCode() {
            return machineCode;
        }

        public void setMachineCode(String machineCode) {
            this.machineCode = machineCode;
        }

        public long getFirstFailureTime() {
            return firstFailureTime;
        }

        public void setFirstFailureTime(long firstFailureTime) {
            this.firstFailureTime = firstFailureTime;
        }

        public int getFailureCount() {
            return failureCount;
        }

        public void setFailureCount(int failureCount) {
            this.failureCount = failureCount;
        }
    }

    /**
     * License校验信息
     * 存储首次验证时间、总验证次数等统计信息
     */
    public static class ValidationInfo implements Serializable {
        private static final long serialVersionUID = 1L;
        private String machineCode;
        private long firstValidationTime;  // 首次验证时间
        private long lastValidationTime;   // 最后验证时间
        private int totalValidationCount;  // 总验证次数
        private int successValidationCount; // 成功验证次数
        private int failureValidationCount; // 失败验证次数
        private String version;            // 版本信息

        public ValidationInfo() {
        }

        public ValidationInfo(String machineCode, long firstValidationTime) {
            this.machineCode = machineCode;
            this.firstValidationTime = firstValidationTime;
            this.lastValidationTime = firstValidationTime;
            this.totalValidationCount = 0;
            this.successValidationCount = 0;
            this.failureValidationCount = 0;
            this.version = "1.0";
        }

        public String getMachineCode() {
            return machineCode;
        }

        public void setMachineCode(String machineCode) {
            this.machineCode = machineCode;
        }

        public long getFirstValidationTime() {
            return firstValidationTime;
        }

        public void setFirstValidationTime(long firstValidationTime) {
            this.firstValidationTime = firstValidationTime;
        }

        public long getLastValidationTime() {
            return lastValidationTime;
        }

        public void setLastValidationTime(long lastValidationTime) {
            this.lastValidationTime = lastValidationTime;
        }

        public int getTotalValidationCount() {
            return totalValidationCount;
        }

        public void setTotalValidationCount(int totalValidationCount) {
            this.totalValidationCount = totalValidationCount;
        }

        public int getSuccessValidationCount() {
            return successValidationCount;
        }

        public void setSuccessValidationCount(int successValidationCount) {
            this.successValidationCount = successValidationCount;
        }

        public int getFailureValidationCount() {
            return failureValidationCount;
        }

        public void setFailureValidationCount(int failureValidationCount) {
            this.failureValidationCount = failureValidationCount;
        }

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }
    }

    /**
     * 初始化校验信息文件
     *
     * @param infoFile 信息文件
     */
    private static void initializeValidationInfo(File infoFile) {
        try {
            String machineCode = MachineCodeUtil.getMachineCode();
            long currentTime = System.currentTimeMillis();
            ValidationInfo info = new ValidationInfo(machineCode, currentTime);
            BinaryCacheUtil.saveEncrypted(info, infoFile, getEncryptionKey());
            log.info("初始化License校验信息文件成功");
        } catch (Exception e) {
            log.error("初始化License校验信息文件失败", e);
        }
    }

    /**
     * 获取校验信息
     *
     * @param machineCode 机器码
     * @return 校验信息，如果不存在则返回null
     */
    public static ValidationInfo getValidationInfo(String machineCode) {
        try {
            File infoFile = getValidationInfoFile();
            if (!infoFile.exists()) {
                return null;
            }

            Object obj = BinaryCacheUtil.loadEncrypted(infoFile, getEncryptionKey());
            if (obj == null || !(obj instanceof ValidationInfo)) {
                log.warn("校验信息文件格式不正确");
                return null;
            }

            ValidationInfo info = (ValidationInfo) obj;

            // 检查机器码是否匹配
            if (!machineCode.equals(info.getMachineCode())) {
                log.info("机器码不匹配，重新初始化校验信息");
                initializeValidationInfo(infoFile);
                return getValidationInfo(machineCode);
            }

            return info;
        } catch (Exception e) {
            log.warn("读取校验信息失败", e);
            return null;
        }
    }

    /**
     * 更新校验信息
     *
     * @param machineCode 机器码
     * @param success     是否验证成功
     */
    public static void updateValidationInfo(String machineCode, boolean success) {
        try {
            File infoFile = getValidationInfoFile();
            ValidationInfo info = getValidationInfo(machineCode);

            if (info == null) {
                // 如果信息不存在，初始化
                initializeValidationInfo(infoFile);
                info = getValidationInfo(machineCode);
                if (info == null) {
                    log.warn("无法获取校验信息，跳过更新");
                    return;
                }
            }

            // 更新信息
            info.setLastValidationTime(System.currentTimeMillis());
            info.setTotalValidationCount(info.getTotalValidationCount() + 1);
            if (success) {
                info.setSuccessValidationCount(info.getSuccessValidationCount() + 1);
            } else {
                info.setFailureValidationCount(info.getFailureValidationCount() + 1);
            }

            // 保存更新后的信息
            BinaryCacheUtil.saveEncrypted(info, infoFile, getEncryptionKey());
            log.debug("更新校验信息成功，总验证次数: {}, 成功: {}, 失败: {}", 
                     info.getTotalValidationCount(), 
                     info.getSuccessValidationCount(), 
                     info.getFailureValidationCount());
        } catch (Exception e) {
            log.error("更新校验信息失败", e);
        }
    }

    /**
     * 获取缓存的License验证结果
     *
     * @param machineCode 机器码
     * @return 缓存的License信息，如果缓存不存在或已过期则返回null
     */
    public static LicenseCache getCache(String machineCode) {
        try {
            File cacheFile = getCacheFile();
            if (!cacheFile.exists()) {
                return null;
            }

            // 从加密的二进制文件读取
            Object obj = BinaryCacheUtil.loadEncrypted(cacheFile, getEncryptionKey());
            if (obj == null || !(obj instanceof LicenseCache)) {
                log.warn("缓存文件格式不正确");
                return null;
            }

            LicenseCache cache = (LicenseCache) obj;

            // 检查机器码是否匹配
            if (!machineCode.equals(cache.getMachineCode())) {
                log.info("机器码不匹配，清除缓存");
                clearCache();
                return null;
            }

            // 检查缓存是否过期
            long currentTime = System.currentTimeMillis();
            if (currentTime - cache.getTimestamp() > CACHE_VALIDITY) {
                log.info("License缓存已过期");
                return null;
            }

            return cache;
        } catch (Exception e) {
            log.warn("读取License缓存失败", e);
            return null;
        }
    }

    /**
     * 保存License验证结果到缓存
     *
     * @param machineCode 机器码
     * @param valid       是否有效
     * @param expireDate  过期日期
     */
    public static void saveCache(String machineCode, boolean valid, String expireDate) {
        try {
            File cacheFile = getCacheFile();
            LicenseCache cache = new LicenseCache(machineCode, valid, System.currentTimeMillis(), expireDate);
            
            // 保存为加密的二进制文件
            BinaryCacheUtil.saveEncrypted(cache, cacheFile, getEncryptionKey());
            log.info("License验证结果已缓存到隐藏文件");
            
            // 更新校验信息
            updateValidationInfo(machineCode, valid);
        } catch (Exception e) {
            log.error("保存License缓存失败", e);
        }
    }

    /**
     * 清除缓存
     */
    public static void clearCache() {
        try {
            File cacheFile = getCacheFile();
            if (cacheFile.exists()) {
                cacheFile.delete();
            }
        } catch (Exception e) {
            log.warn("清除缓存失败", e);
        }
    }

    /**
     * 记录验证失败
     *
     * @param machineCode 机器码
     * @return 失败记录，如果失败次数 >= 2 则返回null（表示应该强制退出）
     */
    public static FailureRecord recordFailure(String machineCode) {
        try {
            File failureFile = getFailureCacheFile();
            FailureRecord record;

            if (failureFile.exists()) {
                Object obj = BinaryCacheUtil.loadEncrypted(failureFile, getEncryptionKey());
                if (obj != null && obj instanceof FailureRecord) {
                    record = (FailureRecord) obj;
                    // 检查机器码是否匹配
                    if (!machineCode.equals(record.getMachineCode())) {
                        // 机器码不匹配，重置记录（新机器码的第一次失败）
                        record = new FailureRecord(machineCode, System.currentTimeMillis(), 1);
                    } else {
                        // 机器码匹配，增加失败次数
                        // 如果之前没有失败过（理论上不应该，因为文件存在），设置第一次失败时间
                        if (record.getFailureCount() == 0) {
                            record.setFirstFailureTime(System.currentTimeMillis());
                        }
                        // 增加失败次数
                        record.setFailureCount(record.getFailureCount() + 1);
                    }
                } else {
                    // 文件格式不正确，创建新记录
                    record = new FailureRecord(machineCode, System.currentTimeMillis(), 1);
                }
            } else {
                // 第一次失败（文件不存在）
                record = new FailureRecord(machineCode, System.currentTimeMillis(), 1);
            }

            // 保存失败记录为加密的二进制文件
            BinaryCacheUtil.saveEncrypted(record, failureFile, getEncryptionKey());

            // 更新校验信息（记录失败）
            updateValidationInfo(machineCode, false);

            // 如果失败次数 >= 2，返回null表示应该强制退出
            if (record.getFailureCount() >= 2) {
                log.error("License验证失败次数 >= 2，强制退出");
                return null; // 表示应该强制退出
            }

            return record;
        } catch (Exception e) {
            log.error("记录验证失败失败", e);
            // 出错时返回一个默认记录，允许继续运行
            return new FailureRecord(machineCode, System.currentTimeMillis(), 1);
        }
    }

    /**
     * 清除失败记录（验证成功时调用）
     *
     * @param machineCode 机器码
     */
    public static void clearFailureRecord(String machineCode) {
        try {
            File failureFile = getFailureCacheFile();
            if (failureFile.exists()) {
                Object obj = BinaryCacheUtil.loadEncrypted(failureFile, getEncryptionKey());
                if (obj != null && obj instanceof FailureRecord) {
                    FailureRecord record = (FailureRecord) obj;
                    // 只清除匹配的机器码记录
                    if (machineCode.equals(record.getMachineCode())) {
                        failureFile.delete();
                        log.info("已清除失败记录");
                    }
                }
            }
        } catch (Exception e) {
            log.warn("清除失败记录失败", e);
        }
    }

    /**
     * 检查是否在宽限期内
     *
     * @param machineCode 机器码
     * @return true表示在宽限期内，false表示不在宽限期内或没有失败记录
     */
    public static boolean isInGracePeriod(String machineCode) {
        try {
            File failureFile = getFailureCacheFile();
            if (!failureFile.exists()) {
                return false;
            }

            Object obj = BinaryCacheUtil.loadEncrypted(failureFile, getEncryptionKey());
            if (obj == null || !(obj instanceof FailureRecord)) {
                return false;
            }

            FailureRecord record = (FailureRecord) obj;
            if (!machineCode.equals(record.getMachineCode())) {
                return false;
            }

            long currentTime = System.currentTimeMillis();
            return (currentTime - record.getFirstFailureTime()) <= GRACE_PERIOD;
        } catch (Exception e) {
            log.warn("检查宽限期失败", e);
            return false;
        }
    }

    /**
     * 获取失败记录
     *
     * @param machineCode 机器码
     * @return 失败记录，如果不存在则返回null
     */
    public static FailureRecord getFailureRecord(String machineCode) {
        try {
            File failureFile = getFailureCacheFile();
            if (!failureFile.exists()) {
                return null;
            }

            Object obj = BinaryCacheUtil.loadEncrypted(failureFile, getEncryptionKey());
            if (obj == null || !(obj instanceof FailureRecord)) {
                return null;
            }

            FailureRecord record = (FailureRecord) obj;
            if (!machineCode.equals(record.getMachineCode())) {
                return null;
            }

            return record;
        } catch (Exception e) {
            log.warn("获取失败记录失败", e);
            return null;
        }
    }
}

