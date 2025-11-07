package io.github.xtemplus.page;

/**
 * 分页数据
 *
 * @author template
 */
public class PageVo {
    /**
     * 当前记录起始索引
     */
    private Integer pageNum = 1;

    /**
     * 每页显示记录数
     */
    private Integer pageSize = 10;
    /**
     * 排序
     */
    private String orderBy;

    public Integer getPageNum() {
        return pageNum;
    }

    public void setPageNum(Integer pageNum) {
        this.pageNum = pageNum;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public String getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }

    @Override
    public String toString() {
        return "PageVo{" +
                "pageNum=" + pageNum +
                ", pageSize=" + pageSize +
                ", orderBy='" + orderBy + '\'' +
                '}';
    }
}
