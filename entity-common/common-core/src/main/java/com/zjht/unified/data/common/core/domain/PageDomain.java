package com.zjht.unified.data.common.core.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * 分页数据
 * 
 * @author zjht
 */
@ApiModel(value = "分页")
@AllArgsConstructor
@NoArgsConstructor
public class PageDomain
{
    /** 当前记录起始索引 */
    @ApiModelProperty(value = "起始页")
    private Integer pageNum = 1;

    /** 每页显示记录数 */
    @ApiModelProperty(value = "结束页")
    private Integer pageSize = 20;


    public Integer getPageNum()
    {
        return pageNum;
    }

    public void setPageNum(Integer pageNum)
    {
        this.pageNum = pageNum;
    }

    public Integer getPageSize()
    {
        return pageSize;
    }

    public void setPageSize(Integer pageSize)
    {
        this.pageSize = pageSize;
    }

}
