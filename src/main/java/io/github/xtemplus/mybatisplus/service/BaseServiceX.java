package io.github.xtemplus.mybatisplus.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import io.github.xtemplus.page.PageVo;
import io.github.xtemplus.page.TableSupport;

/**
 * 扩展的基础Service接口
 *
 * @param <T> 实体类型
 */
public interface BaseServiceX<T> extends IService<T> {

    /**
     * 通用分页查询
     *
     * @param queryWrapper 查询条件
     * @return 分页结果
     */
    default IPage<T> selectPage(Wrapper<T> queryWrapper) {
        PageVo pageVo = TableSupport.buildPageRequest();
        Page<T> page = new Page<>(pageVo.getPageNum(), pageVo.getPageSize());
        return this.page(page, queryWrapper);
    }
}
