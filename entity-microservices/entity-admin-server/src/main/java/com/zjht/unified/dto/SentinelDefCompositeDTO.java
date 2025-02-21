package com.zjht.unified.dto;

import com.zjht.unified.entity.SentinelDef;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.List;
import lombok.Data;

@Data
@ApiModel(value = "DTO", description = "",parent = SentinelDef.class)
public class SentinelDefCompositeDTO extends SentinelDef {
  private Long originalId;
}