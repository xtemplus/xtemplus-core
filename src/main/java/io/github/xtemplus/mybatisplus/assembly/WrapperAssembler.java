package io.github.xtemplus.mybatisplus.assembly;

import cn.hutool.core.util.ObjUtil;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import io.github.xtemplus.mybatisplus.annotation.Assembly;
import io.github.xtemplus.mybatisplus.query.LambdaQueryWrapperX;
import io.github.xtemplus.utils.Log;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;

/**
 * MyBatis-Plus 查询条件自动组装器
 * <p>
 * 功能：
 * 1. 自动将查询 DTO 对象转换为 MyBatis-Plus 的 QueryWrapper
 * 2. 通过反射读取对象字段，根据 @Assembly 注解自动组装查询条件
 * 3. 支持多种查询类型（EQ、LIKE、IN、BETWEEN、GT、LT、GE、LE）
 * 4. 自动处理驼峰命名到下划线的转换（使用 MyBatis-Plus 内置转换逻辑）
 * 5. 支持继承字段、过滤非数据库字段
 * </p>
 */
public class WrapperAssembler {
    private static final Map<Class<?>, Field[]> FIELD_CACHE = new ConcurrentHashMap<>();
    private static final Map<Field, Assembly> ASSEMBLY_CACHE = new ConcurrentHashMap<>();
    private static final Map<String, String> CAMEL_TO_UNDERSCORE_CACHE = new ConcurrentHashMap<>();

    /**
     * 驼峰命名转下划线命名
     * 使用 MyBatis-Plus 内置的 StringUtils.camelToUnderline() 方法，确保转换逻辑一致
     * 例如：dictType -> dict_type, userId -> user_id, userID -> user_i_d
     * 
     * 注意：MyBatis-Plus 的转换规则是遇到大写字母就加下划线，
     * 对于连续大写（如 userID），会转为 user_i_d，这是标准行为
     */
    private static String camelToUnderscore(String camelCase) {
        return CAMEL_TO_UNDERSCORE_CACHE.computeIfAbsent(camelCase, 
            key -> StringUtils.camelToUnderline(key));
    }

    /**
     * 获取类的所有字段（包括继承的字段）
     */
    private static Field[] getAllFields(Class<?> clazz) {
        List<Field> fieldList = new ArrayList<>();
        while (clazz != null && clazz != Object.class) {
            fieldList.addAll(Arrays.asList(clazz.getDeclaredFields()));
            clazz = clazz.getSuperclass();
        }
        return fieldList.toArray(new Field[0]);
    }

    /**
     * 判断字段是否应该被跳过
     */
    private static boolean shouldSkipField(Field field) {
        int modifiers = field.getModifiers();
        
        // 跳过 static、transient、final 字段
        if (Modifier.isStatic(modifiers) 
            || Modifier.isTransient(modifiers)
            || Modifier.isFinal(modifiers)) {
            return true;
        }
        
        // 跳过 @TableField(exist = false) 标记的非数据库字段
        TableField tableField = field.getAnnotation(TableField.class);
        if (ObjUtil.isNotNull(tableField) && !tableField.exist()) {
            return true;
        }
        
        return false;
    }

    /**
     * 判断值是否为空（包括 null、空集合、空数组、空字符串）
     */
    private static boolean isEmptyValue(Object value) {
        if (ObjUtil.isNull(value)) {
            return true;
        }
        
        // 检查空集合
        if (value instanceof Collection && ((Collection<?>) value).isEmpty()) {
            return true;
        }
        
        // 检查空数组
        if (value.getClass().isArray() && java.lang.reflect.Array.getLength(value) == 0) {
            return true;
        }
        
        // 检查空字符串（包括纯空格）
        if (value instanceof String && ((String) value).trim().isEmpty()) {
            return true;
        }
        
        return false;
    }

    /**
     * 公共组装逻辑
     */
    private static <T, W> void assembleFields(T queryDto, W wrapper, BiConsumer<AssemblyFieldContext<T, W>, Object> consumer) {
        // 使用 getAllFields 获取包括继承的所有字段
        Field[] fields = FIELD_CACHE.computeIfAbsent(queryDto.getClass(), WrapperAssembler::getAllFields);
        
        for (Field field : fields) {
            // 跳过不应该参与查询的字段
            if (shouldSkipField(field)) {
                continue;
            }
            
            Assembly assembly = ASSEMBLY_CACHE.computeIfAbsent(field, f -> f.getAnnotation(Assembly.class));
            field.setAccessible(true);
            
            try {
                Object value = field.get(queryDto);
                
                // 跳过空值（包括 null、空集合、空数组、空字符串）
                if (isEmptyValue(value)) {
                    continue;
                }
                
                consumer.accept(new AssemblyFieldContext<>(field, assembly, wrapper, queryDto), value);
                
            } catch (IllegalAccessException e) {
                throw new RuntimeException(
                    String.format("访问字段 %s.%s 失败", 
                        queryDto.getClass().getName(), 
                        field.getName()
                    ), e
                );
            }
        }
    }

    /**
     * 组装 QueryWrapper（使用字符串列名）
     * 
     * @param queryDto 查询 DTO 对象
     * @param <T> 实体类型
     * @return QueryWrapper 对象
     */
    public static <T> QueryWrapper<T> assemble(T queryDto) {
        QueryWrapper<T> wrapper = new QueryWrapper<>();
        assembleFields(queryDto, wrapper, (ctx, value) -> {
            // 获取列名：注解指定 > 字段名转下划线
            String column;
            if (ObjUtil.isNull(ctx.assembly)) {
                // 没有注解时，将驼峰字段名转为下划线列名
                column = camelToUnderscore(ctx.field.getName());
            } else {
                // 有注解时，优先使用注解的column，否则转换字段名
                column = ctx.assembly.column().isEmpty() 
                        ? camelToUnderscore(ctx.field.getName()) 
                        : ctx.assembly.column();
            }
            
            // 没有注解时默认使用EQ类型
            if (ObjUtil.isNull(ctx.assembly)) {
                wrapper.eq(column, value);
                return;
            }
            
            // 根据注解类型组装查询条件
            switch (ctx.assembly.type()) {
                case LIKE:
                    if (!(value instanceof String)) {
                        throw new IllegalArgumentException(
                            String.format("字段 %s 的 LIKE 类型仅支持 String，当前类型：%s", 
                                ctx.field.getName(), value.getClass().getName())
                        );
                    }
                    wrapper.like(column, value.toString());
                    break;
                    
                case IN:
                    if (!(value instanceof Collection)) {
                        throw new IllegalArgumentException(
                            String.format("字段 %s 的 IN 类型仅支持 Collection，当前类型：%s", 
                                ctx.field.getName(), value.getClass().getName())
                        );
                    }
                    wrapper.in(column, (Collection<?>) value);
                    break;
                    
                case BETWEEN:
                    if (!(value instanceof List) || ((List<?>) value).size() != 2) {
                        throw new IllegalArgumentException(
                            String.format("字段 %s 的 BETWEEN 类型需为长度为2的List", ctx.field.getName())
                        );
                    }
                    List<?> betweenList = (List<?>) value;
                    Object start = betweenList.get(0);
                    Object end = betweenList.get(1);
                    
                    // 验证起始值和结束值不为 null
                    if (start == null || end == null) {
                        throw new IllegalArgumentException(
                            String.format("字段 %s 的 BETWEEN 起始值和结束值不能为 null", ctx.field.getName())
                        );
                    }
                    
                    // 如果是可比较类型，验证起始值不大于结束值
                    if (start instanceof Comparable && end instanceof Comparable) {
                        try {
                            @SuppressWarnings("unchecked")
                            int compare = ((Comparable<Object>) start).compareTo(end);
                            if (compare > 0) {
                                Log.warn("字段 {} 的 BETWEEN 起始值({})大于结束值({})，将自动交换",
                                    ctx.field.getName(), start, end);
                                // 自动交换，提升用户体验
                                Object temp = start;
                                start = end;
                                end = temp;
                            }
                        } catch (ClassCastException e) {
                            // 类型不兼容，忽略比较
                            Log.debug("字段 {} 的 BETWEEN 值类型不兼容，跳过大小比较", ctx.field.getName());
                        }
                    }
                    
                    wrapper.between(column, start, end);
                    break;
                    
                case EQ:
                    wrapper.eq(column, value);
                    break;
                    
                case GT:
                    wrapper.gt(column, value);
                    break;
                    
                case LT:
                    wrapper.lt(column, value);
                    break;
                    
                case GE:
                    wrapper.ge(column, value);
                    break;
                    
                case LE:
                    wrapper.le(column, value);
                    break;
                    
                default:
                    wrapper.eq(column, value);
                    break;
            }
        });
        return wrapper;
    }

    /**
     * 组装 QueryWrapper（使用字符串列名）
     * 
     * @param queryDto 查询 DTO 对象
     * @param entityClass 实体类（保留参数以保持向后兼容，实际未使用）
     * @param <T> 实体类型
     * @return QueryWrapper 对象
     * @deprecated 建议使用 {@link #assemble(Object)}，entityClass 参数未使用
     */
    @Deprecated
    public static <T> QueryWrapper<T> assemble(T queryDto, Class<T> entityClass) {
        return assemble(queryDto);
    }

    /**
     * 组装 LambdaQueryWrapper（使用类型安全的 Lambda 表达式）
     * 
     * @param queryDto 查询 DTO 对象
     * @param entityClass 实体类（保留参数以保持向后兼容）
     * @param propertyFuncMap 属性名到 Lambda 函数的映射
     * @param <T> 实体类型
     * @return LambdaQueryWrapperX 对象
     */
    public static <T> LambdaQueryWrapperX<T> assembleLambda(T queryDto, Class<T> entityClass, Map<String, SFunction<T, ?>> propertyFuncMap) {
        LambdaQueryWrapperX<T> wrapper = new LambdaQueryWrapperX<>();
        assembleFields(queryDto, wrapper, (ctx, value) -> {
            String property = ctx.field.getName();
            SFunction<T, ?> func = propertyFuncMap.get(property);
            
            // 如果字段不在 Map 中，记录警告并跳过
            if (ObjUtil.isNull(func)) {
                Log.warn("字段 {} 在 propertyFuncMap 中不存在，已跳过该字段的查询条件组装", property);
                return;
            }
            
            // 没有注解时默认使用EQ类型
            if (ObjUtil.isNull(ctx.assembly)) {
                wrapper.eq(func, value);
                return;
            }
            
            // 根据注解类型组装查询条件
            switch (ctx.assembly.type()) {
                case LIKE:
                    if (!(value instanceof String)) {
                        throw new IllegalArgumentException(
                            String.format("字段 %s 的 LIKE 类型仅支持 String，当前类型：%s", 
                                property, value.getClass().getName())
                        );
                    }
                    wrapper.like(func, value.toString());
                    break;
                    
                case IN:
                    if (!(value instanceof Collection)) {
                        throw new IllegalArgumentException(
                            String.format("字段 %s 的 IN 类型仅支持 Collection，当前类型：%s", 
                                property, value.getClass().getName())
                        );
                    }
                    wrapper.in(func, (Collection<?>) value);
                    break;
                    
                case BETWEEN:
                    if (!(value instanceof List) || ((List<?>) value).size() != 2) {
                        throw new IllegalArgumentException(
                            String.format("字段 %s 的 BETWEEN 类型需为长度为2的List", property)
                        );
                    }
                    List<?> betweenList = (List<?>) value;
                    Object start = betweenList.get(0);
                    Object end = betweenList.get(1);
                    
                    // 验证起始值和结束值不为 null
                    if (start == null || end == null) {
                        throw new IllegalArgumentException(
                            String.format("字段 %s 的 BETWEEN 起始值和结束值不能为 null", property)
                        );
                    }
                    
                    // 如果是可比较类型，验证起始值不大于结束值
                    if (start instanceof Comparable && end instanceof Comparable) {
                        try {
                            @SuppressWarnings("unchecked")
                            int compare = ((Comparable<Object>) start).compareTo(end);
                            if (compare > 0) {
                                Log.warn("字段 {} 的 BETWEEN 起始值({})大于结束值({})，将自动交换",
                                    property, start, end);
                                // 自动交换
                                Object temp = start;
                                start = end;
                                end = temp;
                            }
                        } catch (ClassCastException e) {
                            Log.debug("字段 {} 的 BETWEEN 值类型不兼容，跳过大小比较", property);
                        }
                    }
                    
                    wrapper.between(func, start, end);
                    break;
                    
                case EQ:
                    wrapper.eq(func, value);
                    break;
                    
                case GT:
                    wrapper.gt(func, value);
                    break;
                    
                case LT:
                    wrapper.lt(func, value);
                    break;
                    
                case GE:
                    wrapper.ge(func, value);
                    break;
                    
                case LE:
                    wrapper.le(func, value);
                    break;
            }
        });
        return wrapper;
    }

    /**
     * 组装上下文（内部类）
     */
    private static class AssemblyFieldContext<T, W> {
        Field field;
        Assembly assembly;
        W wrapper;
        T queryDto;

        public AssemblyFieldContext(Field field, Assembly assembly, W wrapper, T queryDto) {
            this.field = field;
            this.assembly = assembly;
            this.wrapper = wrapper;
            this.queryDto = queryDto;
        }
    }
}
