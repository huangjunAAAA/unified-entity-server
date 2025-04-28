package com.zjht.unified.datasource.dto;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;

@Data
@ApiModel(value = "推送数据更新",description = "后台数据更新时使用该结构更新数据")
public class DataValUpdate {
    @ApiModelProperty(value = "数据规格说明")
    private DataMeta meta;
    @ApiModelProperty(value = "实际数据")
    private String data;

    @Data
    public static class DataMeta{
        @ApiModelProperty(value = "通道标识")
        private String channel;
        @ApiModelProperty(value = "数据整体ID")
        private String key;
        @ApiModelProperty(value = "数据ID")
        private String dataId;
        @ApiModelProperty(value = "数据类型")
        private String dataType;
        @ApiModelProperty(value = "数据的时间戳")
        private Long ts;
        @ApiModelProperty(value = "前置指令")
        private String cmd;
    }


}
