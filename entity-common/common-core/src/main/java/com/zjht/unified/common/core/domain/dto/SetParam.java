package com.zjht.unified.common.core.domain.dto;

import lombok.Data;

import java.util.Map;

@Data
public class SetParam extends GetParam{
    private Map<String,Object> value;
}
