package com.zjht.unified.domain.composite;

import com.zjht.unified.domain.simple.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.List;
import lombok.Data;

@Data
@ApiModel(value = "DO", description = "",parent = ClazzDefDO.class)
public class ClazzDefCompositeDO extends ClazzDefDO {
  private Long originalId;
  private List<FieldDefCompositeDO> clazzIdFieldDefList;
  private List<MethodDefDO> clazzIdMethodDefList;
}