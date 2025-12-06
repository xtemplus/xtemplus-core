package io.github.xtemplus.utils;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ObjUtil;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Collections;
import java.util.Map;

/**
 * Servlet相关工具类，简化参数获取、类型转换、编码、Ajax判断等常用操作
 * 适用于Spring Boot Web项目
 *
 * @author template
 */
public class ServletUtil {
    /**
     * 获取当前请求对象
     */
    public static HttpServletRequest getRequest() {
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (ObjUtil.isNull(attrs)) throw new IllegalStateException("当前线程无请求上下文");
        return attrs.getRequest();
    }

    /**
     * 获取当前响应对象
     */
    public static HttpServletResponse getResponse() {
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (ObjUtil.isNull(attrs)) throw new IllegalStateException("当前线程无请求上下文");
        return attrs.getResponse();
    }

    /**
     * 获取当前Session对象
     */
    public static HttpSession getSession() {
        return getRequest().getSession();
    }

    /**
     * 获取String参数，支持默认值
     */
    public static String getParameter(String name, String defaultValue) {
        return Convert.toStr(getRequest().getParameter(name), defaultValue);
    }

    /**
     * 获取Integer参数，支持默认值
     */
    public static Integer getParameterToInt(String name, Integer defaultValue) {
        return Convert.toInt(getRequest().getParameter(name), defaultValue);
    }

    /**
     * 获取Boolean参数，支持默认值
     */
    public static Boolean getParameterToBool(String name, Boolean defaultValue) {
        return Convert.toBool(getRequest().getParameter(name), defaultValue);
    }

    /**
     * 获取所有请求参数（不可变Map）
     */
    public static Map<String, String[]> getParams() {
        return Collections.unmodifiableMap(getRequest().getParameterMap());
    }

}
