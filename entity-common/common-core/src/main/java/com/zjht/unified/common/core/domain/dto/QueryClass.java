package com.zjht.unified.common.core.domain.dto;


import com.zjht.unified.domain.composite.ClazzDefCompositeDO;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class QueryClass {
    private String ver;
    private ClazzDefCompositeDO classDef;
    private String prjId;
    private Map<String, Object> equals;
    private Map<String, String> like;
    private Map<String, List< Object>> inCondition;
}
