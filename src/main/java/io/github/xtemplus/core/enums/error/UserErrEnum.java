package io.github.xtemplus.core.enums.error;

import io.github.xtemplus.core.common.BaseErrEnum;

/**
 * 用户授权错误枚举 (400-429)
 */
public enum UserErrEnum implements BaseErrEnum {
    // 认证相关 (400-409)
    NOT_LOGIN(401, "用户登录已过期"),
    LOGIN_FAILED(402, "用户名或密码错误"),
    CAPTCHA_ERROR(403, "验证码输入有误"),
    CAPTCHA_EXPIRE(404, "验证码已过期"),
    ACCOUNT_LOCKED(405, "账号已被锁定"),
    ACCOUNT_DISABLED(406, "账号已被禁用"),
    ACCOUNT_DELETED(407, "账号已被删除"),
    LOGIN_ATTEMPTS_EXCEED(408, "登录尝试次数过多"),
    TOKEN_INVALID(409, "令牌无效"),
    TOKEN_EXPIRED(410, "令牌已过期"),
    
    // 用户操作 (411-419)
    USER_NOT_EXIST(411, "用户不存在"),
    USER_EXIST(412, "用户已存在"),
    PHONE_EXIST(413, "手机号已存在"),
    EMAIL_EXIST(414, "邮箱已存在"),
    OLD_PASSWORD_ERROR(415, "原密码错误"),
    PASSWORD_SAME(416, "新密码不能与原密码相同"),
    PASSWORD_WEAK(417, "密码强度不足"),
    USER_UPDATE_FAILED(418, "用户信息更新失败"),
    USER_DELETE_FAILED(419, "用户删除失败"),
    
    // 授权相关 (420-429)
    NO_PERMISSION(420, "没有操作权限"),
    ROLE_DISABLED(421, "角色已停用"),
    MENU_DISABLED(422, "菜单已停用"),
    API_DISABLED(423, "接口已停用"),
    ;
    
    private final Integer errCode;
    private final String errMsg;

    UserErrEnum(Integer errCode, String errMsg) {
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