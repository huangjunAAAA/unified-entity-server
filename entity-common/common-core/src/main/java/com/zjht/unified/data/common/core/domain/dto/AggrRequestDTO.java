package com.zjht.unified.data.common.core.domain.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@ApiModel(value = "聚合统计需求", description = "")
public class AggrRequestDTO extends TimeRangeQuery {
    @ApiModelProperty(value = "统计值列表")
    private List<String> valCols=new ArrayList<>();
    @ApiModelProperty(value = "维度列表")
    private List<String> groupCols=new ArrayList<>();
    @ApiModelProperty(value = "表名")
    private String tbl;
}
