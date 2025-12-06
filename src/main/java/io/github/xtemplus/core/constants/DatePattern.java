package io.github.xtemplus.core.constants;

/**
 * 时间格式化常量接口
 */
public interface DatePattern {
    
    // ==================== 基础格式（使用格式内容命名）====================
    /**
     * 日期时间格式：yyyy-MM-dd HH:mm:ss
     */
    String DEFAULT_FORMAT = "yyyy-MM-dd HH:mm:ss";

    /**
     * 日期格式：yyyy-MM-dd
     */
    String yyyy_MM_dd = "yyyy-MM-dd";
    
    /**
     * 时间格式：HH:mm:ss
     */
    String HH_mm_ss = "HH:mm:ss";
    
    /**
     * 紧凑日期格式：yyyyMMdd
     */
    String yyyyMMdd = "yyyyMMdd";

    /**
     * 时间格式：yyyyMMddHHmm
     */
    String yyyyMMddHHmm = "yyyyMMddHHmm";
    
    /**
     * 紧凑日期时间格式：yyyyMMddHHmmss
     */
    String yyyyMMddHHmmss = "yyyyMMddHHmmss";
    
    /**
     * 带毫秒日期时间格式：yyyy-MM-dd HH:mm:ss.SSS
     */
    String yyyy_MM_dd_HH_mm_ss_SSS = "yyyy-MM-dd HH:mm:ss.SSS";
    
    // ==================== 业务格式（使用英文描述命名）====================
    
    /**
     * 年月格式：yyyy-MM
     */
    String YEAR_MONTH = "yyyy-MM";
    
    /**
     * 中文年月格式：yyyy年MM月
     */
    String YEAR_MONTH_ZH = "yyyy年MM月";
    
    /**
     * 中文日期格式：yyyy年MM月dd日
     */
    String DATE_ZH = "yyyy年MM月dd日";
    
    /**
     * 年月日时分格式（不含秒）：yyyy-MM-dd HH:mm
     */
    String DATETIME_NO_SECOND = "yyyy-MM-dd HH:mm";
    
    // ==================== 数据库格式 ====================
    
    /**
     * DB日期格式：%Y-%m-%d
     */
    String DB_DATE = "%Y-%m-%d";
    
    /**
     * DB时间格式：%H:%i:%s
     */
    String DB_TIME = "%H:%i:%s";
    
    /**
     * DB日期时间格式：%Y-%m-%d %H:%i:%s
     */
    String DB_DATETIME = "%Y-%m-%d %H:%i:%s";
    
    // ==================== 特殊业务格式 ====================
    
    /**
     * 文件命名时间格式：yyyyMMdd_HHmmss
     */
    String FILE_TIMESTAMP = "yyyyMMdd_HHmmss";
}