package io.github.xtemplus.config;

import io.github.xtemplus.service.LicenseValidator;
import io.github.xtemplus.service.TemplateCoreService;
import io.github.xtemplus.utils.MachineCodeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger log = LoggerFactory.getLogger(BaseAutoConfiguration.class);

    private final BaseProperties properties;

    public BaseAutoConfiguration(BaseProperties properties) {
        this.properties = properties;
        
        // 打印机器码
        String machineCode = MachineCodeUtil.getMachineCode();
        log.info("========================================");
        log.info("当前设备机器码: {}", machineCode);
        log.info("========================================");
        
        // 执行许可证验证
        if (properties.isLicenseEnabled()) {
            try {
                LicenseValidator.validate(properties.getLicenseKey());
                log.info("许可证验证成功，Template Core Auto Configuration loaded, appName: {}", properties.getAppName());
            } catch (LicenseValidator.LicenseValidationException e) {
                log.error("许可证验证失败，应用启动被阻止: {}", e.getMessage());
                log.error("请使用以下机器码生成许可证: {}", machineCode);
                throw new RuntimeException("许可证验证失败: " + e.getMessage(), e);
            }
        } else {
            log.warn("许可证验证已禁用，Template Core Auto Configuration loaded, appName: {}", properties.getAppName());
        }
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

