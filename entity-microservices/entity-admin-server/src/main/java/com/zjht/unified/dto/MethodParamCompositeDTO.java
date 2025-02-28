package com.zjht.unified.dto;

import com.zjht.unified.entity.MethodParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.List;
import lombok.Data;

@Data
@ApiModel(value = "DTO", description = "",parent = MethodParam.class)
public class MethodParamCompositeDTO extends MethodParam {
  private Long originalId;
}