package com.zjht.unified.vo;

import com.zjht.unified.entity.FsmCondition;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import lombok.Data;
import java.util.List;

@Data
@ApiModel(value = "VO", description = "",parent = FsmCondition.class)
public class FsmConditionCompositeVO extends FsmConditionVo{
}