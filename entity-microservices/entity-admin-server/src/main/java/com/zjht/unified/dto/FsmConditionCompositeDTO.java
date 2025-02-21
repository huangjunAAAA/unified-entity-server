package com.zjht.unified.dto;

import com.zjht.unified.entity.FsmCondition;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.List;
import lombok.Data;

@Data
@ApiModel(value = "DTO", description = "",parent = FsmCondition.class)
public class FsmConditionCompositeDTO extends FsmCondition {
  private Long originalId;
}