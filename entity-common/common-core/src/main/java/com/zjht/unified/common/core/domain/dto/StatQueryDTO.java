package com.zjht.unified.common.core.domain.dto;

import com.zjht.unified.common.core.domain.store.MetricExpr;
import com.zjht.unified.common.core.domain.store.ValueExpr;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(value = "统计计算条件", description = "")
public class StatQueryDTO<T> extends TimeRangeQuery<T> {
    @ApiModelProperty(value = "统计维度列表")
    private List<MetricExpr> metrics;
    @ApiModelProperty(value = "统计值表达式列表")
    private List<ValueExpr> valueExprList;
}
