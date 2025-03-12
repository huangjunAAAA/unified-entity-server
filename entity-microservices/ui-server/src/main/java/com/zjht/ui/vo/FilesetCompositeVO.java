package com.zjht.ui.vo;

import com.zjht.ui.entity.Fileset;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import lombok.Data;
import java.util.List;

@Data
@ApiModel(value = "VO", description = "",parent = Fileset.class)
public class FilesetCompositeVO extends FilesetVo{
}