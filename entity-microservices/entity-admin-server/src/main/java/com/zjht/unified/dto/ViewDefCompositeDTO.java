package com.zjht.unified.dto;

import com.zjht.unified.entity.ViewDef;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.List;
import lombok.Data;

@Data
@ApiModel(value = "DTO", description = "",parent = ViewDef.class)
public class ViewDefCompositeDTO extends ViewDef {
  private Long originalId;
}