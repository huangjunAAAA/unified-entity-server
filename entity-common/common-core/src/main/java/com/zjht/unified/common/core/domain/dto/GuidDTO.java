package com.zjht.unified.common.core.domain.dto;

import lombok.Data;

@Data
public class GuidDTO<T> {
    private String guid;
    private Long prjId;
    private T data;
}
