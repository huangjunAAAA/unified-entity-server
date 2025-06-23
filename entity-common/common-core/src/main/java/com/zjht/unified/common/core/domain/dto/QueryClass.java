package com.zjht.unified.common.core.domain.dto;


import com.zjht.unified.domain.composite.ClazzDefCompositeDO;
import lombok.Data;

@Data
public class QueryClass {
    private String ver;
    private ClazzDefCompositeDO classDef;
    private String prjId;
}
