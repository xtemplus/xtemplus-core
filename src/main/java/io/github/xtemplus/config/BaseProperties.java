package io.github.xtemplus.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 模板核心配置属性
 *
 * @author template
 */
@ConfigurationProperties(prefix = "template.core")
public class BaseProperties {

    /**
     * 是否启用模板核心功能，默认为true
     */
    private boolean enabled = true;

    /**
     * 应用名称
     */
    private String appName = "template-application";

    /**
     * 是否打印启动信息
     */
    private boolean showBanner = true;

    /**
     * 许可证密钥（加密后的机器码,到期时间，格式：机器码,20251102080000）
     */
    private String licenseKey;

    /**
     * 是否启用许可证验证，默认为true
     */
    private boolean licenseEnabled = true;

    /**
     * 是否启用在线License验证，默认为false
     */
    private boolean onlineLicenseEnabled = false;

    /**
     * 加密的授权服务器URL（使用RSA公钥加密）
     */
    private String encryptedAuthUrl;

    /**
     * 加密的RSA私钥（使用JAR文件MD5值进行AES加密）
     */
    private String encryptedPrivateKey;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public boolean isShowBanner() {
        return showBanner;
    }

    public void setShowBanner(boolean showBanner) {
        this.showBanner = showBanner;
    }

    public String getLicenseKey() {
        return licenseKey;
    }

    public void setLicenseKey(String licenseKey) {
        this.licenseKey = licenseKey;
    }

    public boolean isLicenseEnabled() {
        return licenseEnabled;
    }

    public void setLicenseEnabled(boolean licenseEnabled) {
        this.licenseEnabled = licenseEnabled;
    }

    public boolean isOnlineLicenseEnabled() {
        return onlineLicenseEnabled;
    }

    public void setOnlineLicenseEnabled(boolean onlineLicenseEnabled) {
        this.onlineLicenseEnabled = onlineLicenseEnabled;
    }

    public String getEncryptedAuthUrl() {
        return encryptedAuthUrl;
    }

    public void setEncryptedAuthUrl(String encryptedAuthUrl) {
        this.encryptedAuthUrl = encryptedAuthUrl;
    }

    public String getEncryptedPrivateKey() {
        return encryptedPrivateKey;
    }

    public void setEncryptedPrivateKey(String encryptedPrivateKey) {
        this.encryptedPrivateKey = encryptedPrivateKey;
    }
}

