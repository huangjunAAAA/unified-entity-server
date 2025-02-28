package com.zjht.unified.vo;

import com.zjht.unified.entity.MethodParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import lombok.Data;

@Data
@ApiModel(value = "VO", description = "",parent = MethodParam.class)
public class MethodParamVo extends MethodParam {
	
	
}
