package com.zjht.unified.service.v8exec.model;

import lombok.Data;

@Data
public class DataViewInfo {
    private  String name;
    private  String dbName;
    private  String sql;

    public DataViewInfo(String name, String db, String sql) {
        this.name = name;
        this.dbName = db;
        this.sql = sql;
    }
}

