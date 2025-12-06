package io.github.xtemplus.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 全局日志工具类
 */
public class Log {
    private static final Logger logger = LoggerFactory.getLogger(Log.class);

    private Log() {
        // 私有构造器防止实例化
    }

    // ========== 普通日志方法 ==========
    
    public static void trace(String msg) {
        logger.trace(msg);
    }

    public static void trace(String format, Object... args) {
        logger.trace(format, args);
    }

    public static void debug(String msg) {
        logger.debug(msg);
    }

    public static void debug(String format, Object... args) {
        logger.debug(format, args);
    }

    public static void info(String msg) {
        logger.info(msg);
    }

    public static void info(String format, Object... args) {
        logger.info(format, args);
    }

    public static void warn(String msg) {
        logger.warn(msg);
    }

    public static void warn(String format, Object... args) {
        logger.warn(format, args);
    }

    public static void error(String msg) {
        logger.error(msg);
    }

    public static void error(String format, Object... args) {
        logger.error(format, args);
    }

    // ========== 带异常的日志方法 ==========
    
    public static void trace(String msg, Throwable t) {
        logger.trace(msg, t);
    }

    public static void debug(String msg, Throwable t) {
        logger.debug(msg, t);
    }

    public static void info(String msg, Throwable t) {
        logger.info(msg, t);
    }

    public static void warn(String msg, Throwable t) {
        logger.warn(msg, t);
    }

    public static void error(String msg, Throwable t) {
        logger.error(msg, t);
    }

    // ========== 条件日志方法 ==========
    
    public static void infoIf(boolean condition, String msg) {
        if (condition) {
            logger.info(msg);
        }
    }

    public static void errorIf(boolean condition, String msg) {
        if (condition) {
            logger.error(msg);
        }
    }
}