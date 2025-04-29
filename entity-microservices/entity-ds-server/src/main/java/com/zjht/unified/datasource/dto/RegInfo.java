package com.zjht.unified.datasource.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "websocket的数据要求",description = "websocket通过这个结构向服务器发送数据要求")
public class RegInfo {
    @ApiModelProperty(value = "register 或 deregister，分别表示注册和反注册")
    private String action;
    @ApiModelProperty(value = "数据源ID")
    private Long dsId;
}
