package com.zjht.unified.datasource.dto;



import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "业务对象实体规格说明",description = "业务对象实体规格说明")
public class BizEntitySpec {
    @ApiModelProperty(value = "业务实体名称")
    private String name;
    @ApiModelProperty(value = "业务实体代码")
    private String objectType;
    @ApiModelProperty(value = "业务实体初始列表api")
    private ApiSpec listApi;
    @ApiModelProperty(value = "业务实体详情api")
    private ApiSpec detailApi;
    @ApiModelProperty(value = "业务实体历史数据api")
    private ApiSpec historyApi;
    @ApiModelProperty(value = "业务实体规格")
    private String schemaName;
    @ApiModelProperty(value = "是否启用")
    private Boolean enabled;
}
