package com.zjht.unified.vo;

import com.zjht.unified.entity.FieldDef;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import lombok.Data;

@Data
@ApiModel(value = "VO", description = "",parent = FieldDef.class)
public class FieldDefVo extends FieldDef {
	
	
}
