package com.zjht.unified.data.storage.vo;

import com.zjht.unified.data.entity.RawData;
import io.swagger.annotations.ApiModel;

import lombok.Data;

@Data
@ApiModel(value = "VO", description = "",parent = RawData.class)
public class RawDataVo extends RawData {
	
	
}
