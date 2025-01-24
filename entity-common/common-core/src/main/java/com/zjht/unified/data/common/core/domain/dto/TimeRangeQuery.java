package com.zjht.unified.data.common.core.domain.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
@ApiModel(value = "基于时间的查询条件对象", description = "")
public class TimeRangeQuery<T> extends BaseQueryDTO<T> {

    @ApiModelProperty(value = "开始时间")
    private Date start;
    @ApiModelProperty(value = "结束时间")
    private Date end;
    @ApiModelProperty(value = "切片方式， 年，月，日，小时")
    private String slice;
}
