package com.zjht.unified.dto;

import lombok.Data;

import java.util.Map;

@Data
public class SetParam extends GetParam{
    private Map<String,Object> value;
}
