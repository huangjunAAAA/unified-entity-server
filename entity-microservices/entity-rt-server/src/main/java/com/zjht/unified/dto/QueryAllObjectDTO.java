package com.zjht.unified.dto;

import lombok.Data;

@Data
public class QueryAllObjectDTO{
    private Boolean includeInherited;
    private String ver;
    private String clazzGuid;
    private String clazzName;
    private String prjId;
}
