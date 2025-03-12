package com.zjht.unified.common.core.domain.component;

import lombok.Data;

import java.util.List;

@Data
public class CustomizeOptions {
    private List<SimpleField> bindings;
    private String reactiveScript;
}
