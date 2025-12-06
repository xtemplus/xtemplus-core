package io.github.xtemplus.core.enums;

import io.github.xtemplus.core.common.BaseErrEnum;

/**
 * 系统错误枚举 (500-519)
 */
public enum SysErrEnum implements BaseErrEnum {

    // 系统内部错误 (500-509)
    SYSTEM_ERROR(500, "系统内部错误"),
    SERVICE_UNAVAILABLE(501, "服务暂时不可用"),
    DATABASE_ERROR(502, "数据库操作异常"),
    DATABASE_CONNECTION_ERROR(503, "数据库连接失败"),
    CACHE_ERROR(504, "缓存服务异常"),
    CONFIG_ERROR(505, "系统配置错误"),
    INITIALIZATION_ERROR(506, "系统初始化失败"),
    
    // 运行时错误 (510-519)
    RUNTIME_ERROR(510, "运行时错误"),
    NULL_POINTER(511, "空指针异常"),
    ARRAY_INDEX_OUT_OF_BOUNDS(512, "数组越界异常"),
    CLASS_CAST_ERROR(513, "类型转换异常"),
    ARITHMETIC_ERROR(514, "算术运算异常"),
    ILLEGAL_ARGUMENT(515, "非法参数异常"),
    ILLEGAL_STATE(516, "非法状态异常"),
    ;
    
    private final Integer errCode;
    private final String errMsg;

    SysErrEnum(Integer errCode, String errMsg) {
        this.errCode = errCode;
        this.errMsg = errMsg;
    }
    
    @Override
    public Integer errCode() {
        return this.errCode;
    }
    
    @Override
    public String errMsg() {
        return this.errMsg;
    }
}