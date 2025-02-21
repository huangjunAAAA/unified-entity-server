package com.zjht.unified.vo;

import com.zjht.unified.entity.Fileset;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import lombok.Data;

@Data
@ApiModel(value = "VO", description = "",parent = Fileset.class)
public class FilesetVo extends Fileset {
	
	
}
