package com.zjht.unified.dto;


import com.zjht.unified.domain.composite.ClazzDefCompositeDO;
import lombok.Data;

@Data
public class QueryObjectDTO {
    private String ver;
    private String clazzGuid;
    private String clazzName;
    private String prjId;
}
