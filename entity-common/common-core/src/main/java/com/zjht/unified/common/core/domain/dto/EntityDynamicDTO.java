package com.zjht.unified.common.core.domain.dto;

import lombok.Data;

import java.util.Map;

@Data
public class EntityDynamicDTO {
    private Long id;
    private Map<String,Object> arguments;
    private String sessionId;
}
