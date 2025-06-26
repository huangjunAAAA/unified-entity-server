package com.zjht.unified.service;

import com.zjht.unified.common.core.constants.Constants;
import com.zjht.unified.common.core.domain.R;
import com.zjht.unified.common.core.util.SpringUtils;
import com.zjht.unified.common.core.util.StringUtils;
import com.zjht.unified.feign.RemoteStore;
import com.zjht.unified.service.ctx.TaskContext;
import com.zjht.unified.service.v8exec.model.ColumnMeta;
import com.zjht.unified.service.v8exec.model.IndexMeta;
import com.zjht.unified.service.v8exec.model.TableMeta;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class DbMetaService {
    @Autowired
    private RemoteStore remoteStore;

    public TableMeta getTableMeta(String dbName, String tableName, TaskContext taskContext) {
        TableMeta meta = new TableMeta();
        if (StringUtils.isBlank(dbName)) {
            dbName = "uui_store_" + taskContext.getVer();
        }
        meta.setDbName(dbName);
        meta.setTableName(tableName);
        String ver = taskContext.getVer();
        Long prjId = taskContext.getPrjInfo().getPrjId();

        // ---------- 1. 查询字段信息 ----------
        String columnSql = String.format(
                "SELECT COLUMN_NAME, COLUMN_TYPE, COLUMN_COMMENT " +
                        "FROM information_schema.columns " +
                        "WHERE table_schema = '%s' AND table_name = '%s' ORDER BY ORDINAL_POSITION", dbName, tableName
        );
        List<Map<String, Object>> columnList = remoteStore.query(ver, String.valueOf(prjId), columnSql);

        List<ColumnMeta> columns = new ArrayList<>();
        for (Map<String, Object> row : columnList) {
            ColumnMeta col = new ColumnMeta();
            col.setName((String) row.get("COLUMN_NAME"));
            col.setType((String) row.get("COLUMN_TYPE"));
            col.setComment((String) row.get("COLUMN_COMMENT"));
            columns.add(col);
        }
        meta.setColumns(columns);

        // ---------- 2. 查询索引信息 ----------
        String indexSql = String.format(
                "SELECT INDEX_NAME, COLUMN_NAME, INDEX_TYPE " +
                        "FROM information_schema.statistics " +
                        "WHERE table_schema = '%s' AND table_name = '%s' " +
                        "ORDER BY INDEX_NAME, SEQ_IN_INDEX", dbName, tableName
        );
        List<Map<String, Object>> indexList = remoteStore.query(ver, String.valueOf(prjId), indexSql);

        Map<String, IndexMeta> indexMap = new LinkedHashMap<>();
        for (Map<String, Object> row : indexList) {
            String indexName = (String) row.get("INDEX_NAME");
            String colName = (String) row.get("COLUMN_NAME");
            String indexType = (String) row.get("INDEX_TYPE");

            indexMap.computeIfAbsent(indexName, k -> {
                IndexMeta idx = new IndexMeta();
                idx.setName(indexName);
                idx.setType(indexType);
                idx.setColumns(new ArrayList<>());
                return idx;
            }).getColumns().add(colName);
        }
        meta.setIndices(new ArrayList<>(indexMap.values()));

        return meta;
    }

    public static void executeSql(String sql) {
        RemoteStore remoteStore = SpringUtils.getBean(RemoteStore.class);
        R<Object> result = remoteStore.execute( sql);
        if (result.getCode() == Constants.SUCCESS) {
            log.info("SQL执行成功: {}", sql);
        } else {
            log.error("远程执行SQL失败: {} {}", sql,result.getMsg());

        }
    }

    public static List<Map<String, Object>> query( String sql, Object... args) {
        RemoteStore remoteStore = SpringUtils.getBean(RemoteStore.class);
        if (args == null || args.length == 0) {
            return remoteStore.query("", "", sql);
        } else {
            return remoteStore.queryWithArgs( sql, Arrays.asList(args));
        }
    }
}
