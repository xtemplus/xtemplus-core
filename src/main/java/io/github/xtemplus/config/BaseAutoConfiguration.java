package io.github.xtemplus.config;

import io.github.xtemplus.service.TemplateCoreService;
import io.github.xtemplus.utils.Log;
import io.github.xtemplus.utils.MachineCodeUtil;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 模板核心自动配置类
 *
 * @author template
 */
@Configuration
@EnableConfigurationProperties(BaseProperties.class)
@ConditionalOnProperty(prefix = "template.core", name = "enabled", havingValue = "true", matchIfMissing = true)
public class BaseAutoConfiguration {

    private final BaseProperties properties;

    public BaseAutoConfiguration(BaseProperties properties) {
        this.properties = properties;


        // 打印机器码
        String machineCode = MachineCodeUtil.getMachineCode();
        Log.info("========================================");
        Log.info("当前设备机器码: {}", machineCode);
        Log.info("========================================");

        // 执行许可证验证
      /*  if (properties.isLicenseEnabled()) {
            boolean validationSuccess = false;

            // 优先使用在线验证
            if (properties.isOnlineLicenseEnabled()) {
                try {
                    OnlineLicenseValidator.validate(properties.getEncryptedAuthUrl(), properties.getEncryptedPrivateKey());
                    Log.info("在线License验证成功");
                    validationSuccess = true;
                } catch (LicenseValidator.LicenseValidationException e) {
                    Log.warn("在线License验证失败: {}，将继续使用本地验证", e.getMessage());
                    // 在线验证失败，继续使用本地验证
                }

              *//*  try {
                    if (properties.getEncryptedAuthUrl() == null || properties.getEncryptedAuthUrl().trim().isEmpty()) {
                        Log.warn("在线License验证已启用，但未配置加密的授权服务器URL，跳过在线验证");
                    } else if (properties.getEncryptedPrivateKey() == null || properties.getEncryptedPrivateKey().trim().isEmpty()) {
                        Log.warn("在线License验证已启用，但未配置加密的RSA私钥，跳过在线验证");
                    } else {
                        try {
                            OnlineLicenseValidator.validate(properties.getEncryptedAuthUrl(), properties.getEncryptedPrivateKey());
                            Log.info("在线License验证成功");
                            validationSuccess = true;
                        } catch (LicenseValidator.LicenseValidationException e) {
                            Log.warn("在线License验证失败: {}，将继续使用本地验证", e.getMessage());
                            // 在线验证失败，继续使用本地验证
                        }
                    }
                } *//* catch (Exception e) {
                    Log.error("在线License验证过程中发生异常: {}", e.getMessage(), e);
                    // 在线验证异常，继续使用本地验证
                }
            }

            // 如果在线验证未启用或失败，则使用本地验证
            if (!validationSuccess) {
                try {
                    LicenseValidator.validate(properties.getLicenseKey());
                    Log.info("本地License验证成功，Template Core Auto Configuration loaded, appName: {}", properties.getAppName());
                } catch (LicenseValidator.LicenseValidationException e) {
                    Log.error("许可证验证失败，应用启动被阻止: {}", e.getMessage());
                    Log.error("请使用以下机器码生成许可证: {}", machineCode);
                    throw new RuntimeException("许可证验证失败: " + e.getMessage(), e);
                }
            } else {
                Log.info("Template Core Auto Configuration loaded, appName: {}", properties.getAppName());
            }
        } else {
            Log.warn("许可证验证已禁用，Template Core Auto Configuration loaded, appName: {}", properties.getAppName());
        }*/
    }

    /**
     * 创建模板核心服务Bean
     *
     * @return TemplateCoreService实例
     */
    @Bean
    public TemplateCoreService templateCoreService() {
        return new TemplateCoreService(properties);
    }
}

