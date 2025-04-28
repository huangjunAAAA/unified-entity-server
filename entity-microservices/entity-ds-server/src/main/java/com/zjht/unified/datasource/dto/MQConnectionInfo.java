package com.zjht.unified.datasource.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "消息队列连接方式",description = "使用该结构连接消息队列")
public class MQConnectionInfo {

    @ApiModelProperty(value = "MQ 类型，目前支持 redis")
    private String mqType;
    @ApiModelProperty(value = "MQ 连接串")
    private String connectionString;
    @ApiModelProperty(value = "MQ topic，如果是redis则为通道名称")
    private String topic;
}
