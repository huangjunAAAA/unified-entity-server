package com.zjht.unified.vo;

import com.zjht.unified.entity.ClsRelation;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import lombok.Data;
import java.util.List;

@Data
@ApiModel(value = "VO", description = "",parent = ClsRelation.class)
public class ClsRelationCompositeVO extends ClsRelationVo{
}