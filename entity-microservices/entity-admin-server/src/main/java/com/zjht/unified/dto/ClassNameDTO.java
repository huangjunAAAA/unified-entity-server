package com.zjht.unified.dto;

import lombok.Data;

@Data
public class ClassNameDTO<T> {
    private String className;
    private T data;
    private String classGuid;
    private Long prjId;
}
