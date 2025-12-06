package io.github.xtemplus.core.enums.error;

import io.github.xtemplus.core.common.BaseErrEnum;

/**
 * 外部服务错误枚举 (550-569)
 */
public enum ExternalErrEnum implements BaseErrEnum {
    // 外部调用 (550-559)
    EXTERNAL_API_ERROR(550, "外部API调用失败"),
    EXTERNAL_SERVICE_ERROR(551, "外部服务异常"),
    REQUEST_TIMEOUT(552, "请求超时"),
    NETWORK_ERROR(553, "网络连接异常"),
    THIRD_PARTY_ERROR(554, "第三方服务异常"),
    
    // 消息服务 (560-569)
    SMS_SEND_FAILED(560, "短信发送失败"),
    EMAIL_SEND_FAILED(561, "邮件发送失败"),
    SMS_SERVICE_ERROR(562, "短信服务异常"),
    EMAIL_SERVICE_ERROR(563, "邮件服务异常"),
    NOTIFICATION_FAILED(564, "通知发送失败"),
    MQ_SEND_FAILED(565, "消息发送失败"),
    ;
    
    private final Integer errCode;
    private final String errMsg;
    
    ExternalErrEnum(Integer errCode, String errMsg) {
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