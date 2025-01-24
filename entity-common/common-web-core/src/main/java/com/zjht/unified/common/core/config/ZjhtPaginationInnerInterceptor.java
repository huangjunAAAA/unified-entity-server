package com.zjht.unified.common.core.config;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;

/**
 * 如果查询当前页没有数据，则返回最后一页
 */
public class ZjhtPaginationInnerInterceptor extends PaginationInnerInterceptor {

    @Override
    protected void handlerOverflow(IPage<?> page) {
        long currentParam = page.getCurrent();
        if(currentParam <= 1) {
            return;
        }
        long pages = page.getTotal() / page.getSize();
        long n = page.getTotal() % page.getSize();
        if(n > 0) {
            pages++;
        }
        long current = currentParam - 1;
        if(pages < current)
            current = pages;
        page.setCurrent(current);
    }
}
