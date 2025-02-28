package com.zjht.unified.domain.composite;

import com.zjht.unified.domain.simple.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.List;
import lombok.Data;

@Data
@ApiModel(value = "DO", description = "",parent = MethodDefDO.class)
public class MethodDefCompositeDO extends MethodDefDO {
  private Long originalId;
  private List<MethodParamDO> methodIdMethodParamList;
}