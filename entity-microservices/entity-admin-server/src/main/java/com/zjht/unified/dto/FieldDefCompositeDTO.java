package com.zjht.unified.dto;

import com.zjht.unified.entity.FieldDef;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.List;
import lombok.Data;

@Data
@ApiModel(value = "DTO", description = "",parent = FieldDef.class)
public class FieldDefCompositeDTO extends FieldDef {
  private Long originalId;
}