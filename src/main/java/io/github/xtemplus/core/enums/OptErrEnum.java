package io.github.xtemplus.core.enums;

import io.github.xtemplus.core.common.BaseErrEnum;

/**
 * 数据操作错误枚举 (520-539)
 */
public enum OptErrEnum implements BaseErrEnum {
    // 增删改查操作 (520-529)
    CREATE_FAILED(520, "新增失败"),
    UPDATE_FAILED(521, "更新失败"),
    DELETE_FAILED(522, "删除失败"),
    QUERY_FAILED(523, "查询失败"),
    IMPORT_FAILED(524, "导入失败"),
    EXPORT_FAILED(525, "导出失败"),
    SAVE_FAILED(526, "保存失败"),
    IMPORT_EMPTY(527, "导入数据为空"),
    // 状态操作 (530-539)
    ENABLE_FAILED(530, "启用失败"),
    DISABLE_FAILED(531, "停用失败"),
    APPROVE_FAILED(532, "审核失败"),
    REJECT_FAILED(533, "驳回失败"),
    SUBMIT_FAILED(534, "提交失败"),
    CANCEL_FAILED(535, "取消失败"),

    // 批量操作 (540-549)
    BATCH_OPERATE_FAILED(540, "批量操作失败"),
    BATCH_CREATE_FAILED(541, "批量新增失败"),
    BATCH_UPDATE_FAILED(542, "批量更新失败"),
    BATCH_DELETE_FAILED(543, "批量删除失败"),
    BATCH_IMPORT_FAILED(544, "批量导入失败"),

    //请求出错
    IP_NOT_ALLOWED(424, "IP地址不允许访问"),
    REQUEST_FORBIDDEN(425, "请求被禁止"),
    SIGNATURE_ERROR(426, "签名验证失败"),
    REQUEST_ILLEGAL(427, "非法请求"),
    REQUEST_FREQUENT(428, "请求过于频繁"),
    ;

    private final Integer errCode;
    private final String errMsg;

    OptErrEnum(Integer errCode, String errMsg) {
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