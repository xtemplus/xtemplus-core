package io.github.xtemplus.core.enums.error;

import io.github.xtemplus.core.common.BaseErrEnum;

/**
 * 参数校验错误枚举 (430-449)
 */
public enum ValidateErrEnum implements BaseErrEnum {
    // 基本参数校验 (430-439)
    PARAM_REQUIRED(430, "参数不能为空"),
    PARAM_FORMAT_ERROR(431, "参数格式错误"),
    PARAM_TYPE_ERROR(432, "参数类型错误"),
    PARAM_LENGTH_ERROR(433, "参数长度错误"),
    PARAM_RANGE_ERROR(434, "参数范围错误"),
    PARAM_INVALID(435, "参数无效"),
    PARAM_MISSING(436, "缺少必要参数"),
    PARAM_DUPLICATE(437, "参数重复"),

    // 格式校验 (440-449)
    EMAIL_FORMAT_ERROR(440, "邮箱格式错误"),
    PHONE_FORMAT_ERROR(441, "手机号格式错误"),
    ID_CARD_FORMAT_ERROR(442, "身份证格式错误"),
    URL_FORMAT_ERROR(443, "URL格式错误"),
    DATE_FORMAT_ERROR(444, "日期格式错误"),
    JSON_FORMAT_ERROR(445, "JSON格式错误"),
    XML_FORMAT_ERROR(446, "XML格式错误"),
    REGEX_MATCH_ERROR(447, "正则匹配失败"),
    ;

    private final Integer errCode;
    private final String errMsg;

    ValidateErrEnum(Integer errCode, String errMsg) {
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