package com.zjht.unified.common.core.domain.store;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "统计分组维度", description = "")
public class MetricExpr {
    @ApiModelProperty(value = "列名")
    private String name;
}
