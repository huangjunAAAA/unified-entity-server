package com.zjht.unified.common.core.domain.store;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "统计值列与极值方式", description = "")
public class ValueExpr {
    @ApiModelProperty(value = "列名")
    private String propertyName;
    @ApiModelProperty(value = "极值方式 max/min/avg/sum")
    private String extremum;
    @ApiModelProperty(value = "别名")
    private String alias;
}
