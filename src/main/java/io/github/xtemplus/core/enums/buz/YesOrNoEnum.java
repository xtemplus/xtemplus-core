package io.github.xtemplus.core.enums.buz;

/**
 * YesOrNoEnum class
 *
 * @author huaigu
 * @date 2022/10/13
 */
public enum YesOrNoEnum {
    /**
     * 否的枚举定义
     */
    NO(0, "否", "N", false),
    /**
     * 是的枚举定义
     */
    YES(1, "是", "Y", true);

    private final Integer type;
    private final String desc;
    private final String descEn;
    private final Boolean booleanType;

    YesOrNoEnum(Integer type, String desc, String descEn, Boolean booleanType) {
        this.type = type;
        this.desc = desc;
        this.descEn = descEn;
        this.booleanType = booleanType;
    }

    public static YesOrNoEnum check(boolean flag) {
        return flag ? YES : NO;
    }

    public static YesOrNoEnum check(Integer type) {
        return type == 1 ? YES : NO;
    }

    public Integer getType() {
        return type;
    }

    public String getDesc() {
        return desc;
    }

    public String getDescEn() {
        return descEn;
    }

    public Boolean getBooleanType() {
        return booleanType;
    }
}
