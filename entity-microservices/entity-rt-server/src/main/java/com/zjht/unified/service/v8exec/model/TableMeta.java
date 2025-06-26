package com.zjht.unified.service.v8exec.model;

import lombok.Data;

import java.util.List;

@Data
public class TableMeta {
    private String dbName;
    private String tableName;
    private List<ColumnMeta> columns;
    private List<IndexMeta> indices;

}


