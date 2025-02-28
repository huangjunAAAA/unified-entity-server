package com.zjht.unified.vo;

import com.zjht.unified.entity.MethodDef;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import lombok.Data;
import java.util.List;

@Data
@ApiModel(value = "VO", description = "",parent = MethodDef.class)
public class MethodDefCompositeVO extends MethodDefVo{
  private List<MethodParamCompositeVO> methodIdMethodParamList;
}