package com.zjht.unified.data.common.core.domain.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "BaseQueryDTO 基础查询条件对象", description = "")
public class BaseQueryDTO<T> {

    /**
     * 条件对象
     */
    @ApiModelProperty(value = "条件对象")
    private T condition;
    /**
     * 排序字段
     */
    @ApiModelProperty(value = "排序字段，默认是最后更新时间")
    private String orderBy="updateTime";
    /**
     * 排序规则
     */
    @ApiModelProperty(value = "排序规则，默认倒序")
    private String asc="desc";
    /**
     * 页码
     */
    @ApiModelProperty(value = "页码，1开始")
    private int page=1;
    /**
     * 每页大小
     */
    @ApiModelProperty(value = "每页大小，默认10")
    private int size=10;
}
