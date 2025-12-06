package io.github.xtemplus.service;

import io.github.xtemplus.config.BaseProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 模板核心服务类
 *
 * @author template
 */
public class TemplateCoreService {

    private static final Logger log = LoggerFactory.getLogger(TemplateCoreService.class);

    private final BaseProperties properties;

    public TemplateCoreService(BaseProperties properties) {
        this.properties = properties;
        if (properties.isShowBanner()) {
            printBanner();
        }
    }

    /**
     * 打印启动Banner
     */
    private void printBanner() {
        String banner = "\n" +
                "::: X-TemPlus-Core Starter v1.0.0 :::\n" +
                "Application Name: " + properties.getAppName() + "\n";
        log.info(banner);
    }

    /**
     * 获取欢迎消息
     *
     * @return 欢迎消息
     */
    public String getWelcomeMessage() {
        return "Welcome to use Template Core Starter! Application: " + properties.getAppName();
    }

    /**
     * 执行核心功能
     *
     * @param message 消息
     * @return 处理后的消息
     */
    public String execute(String message) {
        log.info("TemplateCoreService executing: {}", message);
        return String.format("[TemplateCore] %s", message);
    }
}

