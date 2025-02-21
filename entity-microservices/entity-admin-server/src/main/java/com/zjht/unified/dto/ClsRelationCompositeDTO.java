package com.zjht.unified.dto;

import com.zjht.unified.entity.ClsRelation;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.List;
import lombok.Data;

@Data
@ApiModel(value = "DTO", description = "",parent = ClsRelation.class)
public class ClsRelationCompositeDTO extends ClsRelation {
  private Long originalId;
}