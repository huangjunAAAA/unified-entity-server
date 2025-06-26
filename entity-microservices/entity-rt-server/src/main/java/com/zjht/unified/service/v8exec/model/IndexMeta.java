package com.zjht.unified.service.v8exec.model;

import lombok.Data;

import java.util.List;

@Data
public class IndexMeta {
    private String name;
    private String type;
    private List<String> columns;
}
