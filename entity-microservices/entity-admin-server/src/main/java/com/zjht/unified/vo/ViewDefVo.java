package com.zjht.unified.vo;

import com.zjht.unified.entity.ViewDef;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import lombok.Data;

@Data
@ApiModel(value = "VO", description = "",parent = ViewDef.class)
public class ViewDefVo extends ViewDef {
	
	
}
