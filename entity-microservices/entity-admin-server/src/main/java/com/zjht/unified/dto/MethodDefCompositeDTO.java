package com.zjht.unified.dto;

import com.zjht.unified.entity.MethodDef;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.List;
import lombok.Data;

@Data
@ApiModel(value = "DTO", description = "",parent = MethodDef.class)
public class MethodDefCompositeDTO extends MethodDef {
  private Long originalId;
}