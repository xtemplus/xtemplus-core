package io.github.xtemplus.page;

import cn.hutool.core.convert.Convert;
import io.github.xtemplus.utils.ServletUtil;

/**
 * 表格数据处理
 *
 * @author template
 */
public class TableSupport {
    /**
     * 当前记录起始索引
     */
    public static final String PAGE_NUM = "pageNum";

    /**
     * 每页显示记录数
     */
    public static final String PAGE_SIZE = "pageSize";

    /**
     * 排序字段
     */
    public static final String ORDER_BY = "orderBy";


    public static PageVo buildPageRequest() {
        PageVo pageVo = new PageVo();
        pageVo.setPageNum(Convert.toInt(ServletUtil.getParameterToInt(PAGE_NUM, 1)));
        pageVo.setPageSize(Convert.toInt(ServletUtil.getParameterToInt(PAGE_SIZE, 10)));
        pageVo.setOrderBy(ServletUtil.getParameter(ORDER_BY, null));
        return pageVo;
    }
}
