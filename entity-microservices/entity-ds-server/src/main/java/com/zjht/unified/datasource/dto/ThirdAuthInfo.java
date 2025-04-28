package com.zjht.unified.datasource.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "鉴权方式",description = "鉴权方式")
public class ThirdAuthInfo {
    @ApiModelProperty(value = "用户名或apiKey")
    private String apiKey;
    @ApiModelProperty(value = "密码或apiSecret")
    private String apiSecret;
    @ApiModelProperty(value = "鉴权方式 bear/jwt/plain")
    private String method;
}
