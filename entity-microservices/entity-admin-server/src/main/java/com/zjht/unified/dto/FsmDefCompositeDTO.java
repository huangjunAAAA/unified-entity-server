package com.zjht.unified.dto;

import com.zjht.unified.entity.FsmDef;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.List;
import lombok.Data;

@Data
@ApiModel(value = "DTO", description = "",parent = FsmDef.class)
public class FsmDefCompositeDTO extends FsmDef {
  private Long originalId;
  private List<FsmConditionCompositeDTO> fsmIdFsmConditionList;
}