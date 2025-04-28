package com.zjht.unified.datasource.dto;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "api 补充说明",description = "api 补充说明")
public class ApiSpec {
    @ApiModelProperty(value = "自定义名称，为空无效")
    private String name;
    @ApiModelProperty(value = "相对路径，用以锚定 api详细说明列表")
    private String path;
    @ApiModelProperty(value = "简易版 api详情")
    private OperationExt spec;
    @ApiModelProperty(value = "基础URL，为空无效")
    private String baseUrl;
    @ApiModelProperty(value = "参数模板")
    private String paramTemplate;
    @ApiModelProperty(hidden = true,value = "是否需要服务器中转 1 需要")
    private Integer relayStatus=1;
}
