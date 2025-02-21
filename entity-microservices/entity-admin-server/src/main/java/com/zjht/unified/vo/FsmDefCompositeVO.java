package com.zjht.unified.vo;

import com.zjht.unified.entity.FsmDef;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import lombok.Data;
import java.util.List;

@Data
@ApiModel(value = "VO", description = "",parent = FsmDef.class)
public class FsmDefCompositeVO extends FsmDefVo{
  private List<FsmConditionCompositeVO> fsmIdFsmConditionList;
}