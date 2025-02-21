package com.zjht.unified.vo;

import com.zjht.unified.entity.SentinelDef;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import lombok.Data;
import java.util.List;

@Data
@ApiModel(value = "VO", description = "",parent = SentinelDef.class)
public class SentinelDefCompositeVO extends SentinelDefVo{
}