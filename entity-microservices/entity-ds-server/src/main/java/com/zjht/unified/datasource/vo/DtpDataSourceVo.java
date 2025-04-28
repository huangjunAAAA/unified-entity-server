package com.zjht.unified.datasource.vo;


import com.zjht.unified.datasource.entity.DtpDataSource;
import io.swagger.annotations.ApiModel;

import lombok.Data;

@Data
@ApiModel(value = "VO", description = "",parent = DtpDataSource.class)
public class DtpDataSourceVo extends DtpDataSource {
	
	
}
