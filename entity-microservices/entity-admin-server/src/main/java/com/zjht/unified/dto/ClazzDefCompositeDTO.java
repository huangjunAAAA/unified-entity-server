package com.zjht.unified.dto;

import com.zjht.unified.entity.ClazzDef;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.List;
import lombok.Data;

@Data
@ApiModel(value = "DTO", description = "",parent = ClazzDef.class)
public class ClazzDefCompositeDTO extends ClazzDef {
  private Long originalId;
  private List<FieldDefCompositeDTO> clazzIdFieldDefList;
  private List<MethodDefCompositeDTO> clazzIdMethodDefList;
}