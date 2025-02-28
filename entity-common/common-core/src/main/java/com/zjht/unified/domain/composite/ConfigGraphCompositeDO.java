package com.zjht.unified.domain.composite;

import com.zjht.unified.domain.simple.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.List;
import lombok.Data;

@Data
@ApiModel(value = "DO", description = "",parent = ConfigGraphDO.class)
public class ConfigGraphCompositeDO extends ConfigGraphDO {
  private Long originalId;
  private ClazzDefCompositeDO nodeIdClazzDefComposite;
  private ConfigGraphCompositeDO parentIdConfigGraphComposite;
  private FsmDefCompositeDO nodeIdFsmDefComposite;
  private SentinelDefDO nodeIdSentinelDef;
  private ViewDefDO nodeIdViewDef;
}