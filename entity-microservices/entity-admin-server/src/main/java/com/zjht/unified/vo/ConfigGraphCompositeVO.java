package com.zjht.unified.vo;

import com.zjht.unified.entity.ConfigGraph;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import lombok.Data;
import java.util.List;

@Data
@ApiModel(value = "VO", description = "",parent = ConfigGraph.class)
public class ConfigGraphCompositeVO extends ConfigGraphVo{
  private ClazzDefCompositeVO nodeIdClazzDefComposite;
  private FsmDefCompositeVO nodeIdFsmDefComposite;
  private SentinelDefCompositeVO nodeIdSentinelDefComposite;
  private ViewDefCompositeVO nodeIdViewDefComposite;
}