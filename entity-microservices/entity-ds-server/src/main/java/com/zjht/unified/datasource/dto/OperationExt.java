package com.zjht.unified.datasource.dto;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.v3.oas.models.media.ObjectSchema;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.parameters.RequestBody;
import io.swagger.v3.oas.models.responses.ApiResponses;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@ApiModel(value = "API 规格说明",description = "swagger的简易版本")
public class OperationExt {

    @ApiModelProperty(value = "路径")
    private String path;

    @ApiModelProperty(value = "发送方式")
    private String method;

    @ApiModelProperty(value = "get或post的参数集合")
    private List<Parameter> parameters=new ArrayList<>();

    @ApiModelProperty(value = "post方式的body说明")
    private RequestBody requestBody = null;

    @ApiModelProperty(value = "返回说明")
    private ApiResponses responses = null;

    @ApiModelProperty(value = "该API使用到的对象规格说明")
    private Set<ObjectSchema> objectSchemas=new HashSet<>();
}
