package com.zjht.unified.vo;

import com.zjht.unified.entity.ConfigGraph;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import lombok.Data;

@Data
@ApiModel(value = "VO", description = "",parent = ConfigGraph.class)
public class ConfigGraphVo extends ConfigGraph {
	
	
}
