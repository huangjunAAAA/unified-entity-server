package com.zjht.unified.domain.composite;

import com.zjht.unified.domain.simple.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.List;
import lombok.Data;

@Data
@ApiModel(value = "DO", description = "",parent = FsmDefDO.class)
public class FsmDefCompositeDO extends FsmDefDO {
  private Long originalId;
  private List<FsmConditionDO> fsmIdFsmConditionList;
}