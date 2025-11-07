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
        StringBuilder banner = new StringBuilder();
        banner.append("\n");
        banner.append("   _______                     __                ____            \n");
        banner.append("  / ____(_)___ ___  ____ _____/ /___ __________ / __ \\_________  \n");
        banner.append(" / /   / / __ `__ \\/ __ `/ __  / __ `/ ___/ __ \\/ /_/ / ___/ _ \\ \n");
        banner.append("/ /___/ / / / / / / /_/ / /_/ / /_/ / /  / /_/ / ____/ /  /  __/ \n");
        banner.append("\\____/_/_/ /_/ /_/\\__,_/\\__,_/\\__,_/_/   \\____/_/   /_/   \\___/  \n");
        banner.append("\n");
        banner.append("::: Template Core Starter v1.0.0 :::\n");
        banner.append("Application Name: ").append(properties.getAppName()).append("\n");
        log.info(banner.toString());
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

