package com.zjht.unified.datasource.dto;

import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.LinkedHashMap;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "api 补充说明",description = "api 补充说明")
public class ApiInvokeParam {
    @ApiModelProperty(value = "调用的URL")
    private String path;
    @ApiModelProperty(value = "发送的json body参数")
    private String requestBody;
    @ApiModelProperty(value = "post/get方式发的参数")
    private Map<String,Object> params=new LinkedHashMap<>();
    @ApiModelProperty(value = "自定义headers")
    private Map<String,String> headers =new LinkedHashMap<>();
    @ApiModelProperty(value = "发送方式 post/get")
    private String method;
    @ApiModelProperty(value = "数据源ID")
    private Long dsId;
    @ApiModelProperty(value = "基础Url，为空表示使用默认")
    private String baseUrl;

    @ApiModelProperty(hidden = true)
    public boolean useCache=true;

    public void putParameter(String k, Object v){
        params.put(k,v);
    }

    public void putHeader(String k, String v){
        headers.put(k,v);
    }
}
