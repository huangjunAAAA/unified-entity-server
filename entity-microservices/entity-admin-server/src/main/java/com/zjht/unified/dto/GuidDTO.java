package com.zjht.unified.dto;

import lombok.Data;

@Data
public class GuidDTO<T> {
    private String guid;
    private Long prjId;
    private T data;
}
