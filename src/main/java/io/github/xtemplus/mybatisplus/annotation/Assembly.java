package io.github.xtemplus.mybatisplus.annotation;

import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Assembly {
    AssemblyType type() default AssemblyType.EQ;
    String column() default ""; // 数据库字段名，默认用属性名
}
