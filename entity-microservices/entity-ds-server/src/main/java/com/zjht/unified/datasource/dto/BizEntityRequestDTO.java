package com.zjht.unified.datasource.dto;

import lombok.Data;

import java.util.Map;

@Data
public class BizEntityRequestDTO {
    private Long datasourceId;
    private String entityCode;
    private String entityId;
    private Map<String,Object> params;
}
