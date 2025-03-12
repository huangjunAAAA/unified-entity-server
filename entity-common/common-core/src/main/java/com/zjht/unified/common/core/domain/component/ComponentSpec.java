package com.zjht.unified.common.core.domain.component;

import lombok.Data;

import java.util.List;

@Data
public class ComponentSpec {
    private List<CustomizeOptions> interfaces;
    private String sample;
    private List<ExposeEntry> entries;
}
