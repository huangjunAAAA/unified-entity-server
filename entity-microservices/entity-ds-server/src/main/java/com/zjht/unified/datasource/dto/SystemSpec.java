package com.zjht.unified.datasource.dto;




import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.v3.oas.models.media.Schema;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@ApiModel(value = "数据源详细说明",description = "通过数据源获得详细说明")
public class SystemSpec {
    @ApiModelProperty(value = "系统源ID")
    private Long sourceId;
    @ApiModelProperty(value = "鉴权方式")
    private ThirdAuthInfo authInfo=new ThirdAuthInfo();
    @ApiModelProperty(value = "系统类型/名称 cpd 或 zhy等")
    private String sysType;
    @ApiModelProperty(value = "消息队列连接方式")
    private MQConnectionInfo mqConnection;
    @ApiModelProperty(value = "系统 api说明，swagger方式")
    private List<OpenApiCollectionSpec> apiSpecs =new ArrayList<>();
    @ApiModelProperty(value = "系统动态数据规格说明", dataType = "io.swagger.v3.oas.models.media.Schema")
    private Map<String,Schema> liveFeedInfoList=new HashMap<>();
    @ApiModelProperty(value = "系统可用的业务实体列表")
    private List<BizEntitySpec> bizObjects=new ArrayList<>();

}
