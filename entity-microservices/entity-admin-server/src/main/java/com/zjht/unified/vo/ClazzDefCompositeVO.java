package com.zjht.unified.vo;

import com.zjht.unified.entity.ClazzDef;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import lombok.Data;
import java.util.List;

@Data
@ApiModel(value = "VO", description = "",parent = ClazzDef.class)
public class ClazzDefCompositeVO extends ClazzDefVo{
  private List<FieldDefCompositeVO> clazzIdFieldDefList;
  private List<MethodDefCompositeVO> clazzIdMethodDefList;
}