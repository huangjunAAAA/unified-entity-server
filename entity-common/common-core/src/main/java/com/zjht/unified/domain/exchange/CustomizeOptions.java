package com.zjht.unified.domain.exchange;

import lombok.Data;

import java.util.List;

@Data
public class CustomizeOptions {
    private List<SimpleField> bindings;
    private String reactiveScript;
}
