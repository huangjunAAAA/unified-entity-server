package com.zjht.unified.dto;

import lombok.Data;

@Data
public class SchemaActionParam {
    /**
     * 运行环境版本号（task ver）
     */
    private String ver;

    /**
     * 表名（针对 getTable / 表结构操作）
     */
    private String tableName;

    /**
     * 视图名（针对创建 / 删除视图）
     */
    private String viewName;

    /**
     * 视图 SQL（针对创建视图）
     */
    private String viewSql;

    /**
     * 执行的方法名
     */
    private String methodName;

    /**
     * 待执行方法的参数值列表
     */
    private Object[] params;



}
