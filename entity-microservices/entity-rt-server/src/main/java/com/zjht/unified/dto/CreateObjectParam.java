package com.zjht.unified.dto;

import lombok.Data;

import java.util.List;

@Data
public class CreateObjectParam {
    private String ver;
    private String clsName;
    private String clsGuid;
    private String prjVer;
    private boolean persist;
    private List<Object> args;
}
