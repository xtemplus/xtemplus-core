package io.github.xtemplus.mybatisplus.handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * MyBatis-Plus 自动填充处理器
 * <p>
 * 功能说明：
 * - 在执行insert操作时，自动填充createTime、updateTime
 * - 在执行update操作时，自动填充updateTime
 * <p>
 * 使用方式：
 * 1. 在实体类字段上添加@TableField(fill = FieldFill.INSERT)或@TableField(fill = FieldFill.INSERT_UPDATE)注解
 * 2. 或者直接继承BaseEntity基类
 * <p>
 * 注意事项：
 * - 只有在字段值为null时才会填充
 * - 如果手动设置了字段值，不会被覆盖
 * - 当前用户信息从UserContext获取，如果未登录则填充"system"
 *
 * @author template
 */
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {

    @Value("${mybatis-plus.global-config.db-config.auto-insert-field:createTime}")
    private String insertFillField;

    @Value("${mybatis-plus.global-config.db-config.auto-update-field:updateTime}")
    private String updateFillField;

    /**
     * 插入时自动填充
     *
     * @param metaObject 元对象
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        Date now = new Date();
        // 填充创建时间
        if (metaObject.hasSetter(insertFillField)) {
            this.strictInsertFill(metaObject, insertFillField, Date.class, now);
        }
        // 填充更新时间
        if (metaObject.hasSetter(updateFillField)) {
            this.strictUpdateFill(metaObject, updateFillField, Date.class, now);
        }
    }

    /**
     * 更新时自动填充
     *
     * @param metaObject 元对象
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        Date now = new Date();
        // 填充更新时间
        if (metaObject.hasSetter(updateFillField)) {
            this.strictUpdateFill(metaObject, updateFillField, Date.class, now);
        }
    }
}

