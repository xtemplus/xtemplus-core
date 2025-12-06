package io.github.xtemplus.mybatisplus.handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;

import io.github.xtemplus.utils.Log;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * MyBatis-Plus 自动填充处理器
 * 
 * 功能说明：
 * - 在执行insert操作时，自动填充createTime、updateTime
 * - 在执行update操作时，自动填充updateTime
 * 
 * 使用方式：
 * 1. 在实体类字段上添加@TableField(fill = FieldFill.INSERT)或@TableField(fill = FieldFill.INSERT_UPDATE)注解
 * 2. 或者直接继承BaseEntity基类
 * 
 * 注意事项：
 * - 只有在字段值为null时才会填充
 * - 如果手动设置了字段值，不会被覆盖
 * - 当前用户信息从UserContext获取，如果未登录则填充"system"
 * 
 * @author template
 */
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {

    /**
     * 插入时自动填充
     * 
     * @param metaObject 元对象
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        Date now = new Date();
        // 填充创建时间
        this.strictInsertFill(metaObject, "createTime", Date.class, now);
        // 填充更新时间
        this.strictInsertFill(metaObject, "updateTime", Date.class, now);
    }

    /**
     * 更新时自动填充
     * 
     * @param metaObject 元对象
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        Log.info("开始update填充...");
        Date now = new Date();
        // 填充更新时间
        this.strictUpdateFill(metaObject, "updateTime", Date.class, now);
    }

}

