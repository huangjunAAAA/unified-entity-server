package com.zjht.unified.datasource.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "swagger的说明",description = "swagger的说明")
public class OpenApiCollectionSpec {
    @ApiModelProperty(value = "swagger的endpoint")
    private String swaggerUrl;
    @ApiModelProperty(value = "该swagger包含的path的访问前缀")
    private String baseUrl;
}
