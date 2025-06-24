package com.zjht.unified.dto;


import com.zjht.unified.domain.composite.ClazzDefCompositeDO;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class QueryObjectDTO {
    private String ver;
    private String clazzGuid;
    private String clazzName;
    private String prjId;

    private Map<String, Object> equals;
    private Map<String, String> like;
    private Map<String, List< Object>> inCondition;
}
