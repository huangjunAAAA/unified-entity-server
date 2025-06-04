package com.zjht.unified.dto;

import lombok.Data;

@Data
public class MethodInvokeParam {
    private String ver;
    private String objGuid;
    private String clazzGuid;
    private String prjGuid;
    private String prjVer;
    private String methodName;
    private Object[] params;
}
