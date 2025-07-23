package com.zjht.unified.service.v8exec.model;

import com.caoccao.javet.annotations.V8Function;
import com.zjht.unified.common.core.util.SpringUtils;
import com.zjht.unified.service.DbMetaService;
import com.zjht.unified.service.v8exec.SchemaUtils;

import java.util.*;

public class TableInfo {


    public String name;
    public String dbname;
    public List<SchemaUtils.V8Column> columns;
    public List<SchemaUtils.V8Index> indices;
    public List<String> methodNames;

    public TableInfo(String dbname, String name, List<SchemaUtils.V8Column> columns, List<SchemaUtils.V8Index> indices) {
        this.dbname = dbname;
        this.name = name;
        this.columns = columns;
        this.indices = indices;
    }


    @V8Function(name = "alterColumnType")
    public void alterColumnType(String colName, String newType) {
        String sql = String.format("ALTER TABLE `%s`.`%s` MODIFY COLUMN `%s` %s",
                dbname, name, colName, newType);
        DbMetaService dbMetaService = SpringUtils.getBean(DbMetaService.class);
        dbMetaService.executeSql(sql,false,"");
    }

    @V8Function(name = "alterColumnTypeByIdx")
    public void alterColumnTypeByIdx(int idx, String newType) {
        if (idx < 0 || idx >= columns.size()) throw new IllegalArgumentException("Invalid column index");
        alterColumnType(columns.get(idx).name, newType);
    }

    @V8Function(name = "alterColumnName")
    public void alterColumnName(String oldName, String newName) {
        Optional<SchemaUtils.V8Column> colOpt = columns.stream().filter(c -> c.name.equals(oldName)).findFirst();
        if (!colOpt.isPresent()) throw new IllegalArgumentException("Column not found: " + oldName);
        String type = colOpt.get().type;
        String sql = String.format("ALTER TABLE `%s`.`%s` CHANGE COLUMN `%s` `%s` %s",
                dbname, name, oldName, newName, type);
        DbMetaService dbMetaService = SpringUtils.getBean(DbMetaService.class);
        dbMetaService.executeSql(sql,false,"");
    }

    @V8Function(name = "alterColumnNameByIdx")
    public void alterColumnNameByIdx(int idx, String newName) {
        if (idx < 0 || idx >= columns.size()) throw new IllegalArgumentException("Invalid column index");
        alterColumnName(columns.get(idx).name, newName);
    }

    @V8Function(name = "dropIdx")
    public void dropIdx(String idxName) {
        String sql = String.format("ALTER TABLE `%s`.`%s` DROP INDEX `%s`",
                dbname, name, idxName);
        DbMetaService dbMetaService = SpringUtils.getBean(DbMetaService.class);
        dbMetaService.executeSql(sql,false,"");
    }

    @V8Function(name = "createIdx")
    public void createIdx(String idxName, String type, List<String> columnNames) {
        String cols = String.join("`, `", columnNames);
        String sql = String.format("CREATE %s INDEX `%s` ON `%s`.`%s` (`%s`)",
                type.toUpperCase(), idxName, dbname, name, cols);
        DbMetaService dbMetaService = SpringUtils.getBean(DbMetaService.class);

        dbMetaService.executeSql(sql,false,"");
    }

    @V8Function(name = "filter")
    public List<Map<String, Object>> filter(Map<String, Object> condition, int offset, int limit) {
        List<Object> params = new ArrayList<>();
        String sql = "SELECT * FROM `" +
                dbname + "`.`" + name + "` " +
                buildWhereSql(condition, params) +
                " LIMIT ?, ?";
        params.add(offset);
        params.add(limit);
        DbMetaService dbMetaService = SpringUtils.getBean(DbMetaService.class);
        return dbMetaService.query(sql, params.toArray());
    }

    @V8Function(name = "filterLen")
    public int filterLen(Map<String, Object> condition) {
        List<Object> params = new ArrayList<>();
        String sql = "SELECT COUNT(*) AS cnt FROM `" +
                dbname + "`.`" + name + "` " +
                buildWhereSql(condition, params);
        DbMetaService dbMetaService = SpringUtils.getBean(DbMetaService.class);
        List<Map<String, Object>> result = dbMetaService.query(sql, params.toArray());
        if (result.isEmpty()) return 0;
        return ((Number) result.get(0).get("cnt")).intValue();
    }

    @V8Function(name = "length")
    public int length() {
        String sql = String.format("SELECT COUNT(*) AS cnt FROM `%s`.`%s`", dbname, name);
        DbMetaService dbMetaService = SpringUtils.getBean(DbMetaService.class);
        List<Map<String, Object>> result = dbMetaService.query( sql);
        if (result.isEmpty()) return 0;
        return ((Number) result.get(0).get("cnt")).intValue();
    }

    // 构造 WHERE 语句和参数
    private String buildWhereSql(Map<String, Object> condition, List<Object> params) {
        if (condition == null || condition.isEmpty()) {
            return "";
        }
        StringBuilder where = new StringBuilder("WHERE ");
        boolean first = true;
        for (Map.Entry<String, Object> entry : condition.entrySet()) {
            if (!first) {
                where.append(" AND ");
            }
            where.append("`").append(entry.getKey()).append("`");
            Object val = entry.getValue();
            if (val == null) {
                where.append(" IS NULL");
            } else {
                where.append(" = ?");
                params.add(val);
            }
            first = false;
        }
        return where.toString();
    }

}