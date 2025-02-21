package com.zjht.unified.vo;

import com.zjht.unified.entity.FsmCondition;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import lombok.Data;

@Data
@ApiModel(value = "VO", description = "",parent = FsmCondition.class)
public class FsmConditionVo extends FsmCondition {
	
	
}
