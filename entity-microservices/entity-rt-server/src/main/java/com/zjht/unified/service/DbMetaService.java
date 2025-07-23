package com.zjht.unified.service;

import com.zjht.unified.common.core.constants.Constants;
import com.zjht.unified.common.core.domain.R;
import com.zjht.unified.common.core.util.StringUtils;
import com.zjht.unified.feign.RemoteStore;
import com.zjht.unified.service.ctx.TaskContext;
import com.zjht.unified.service.v8exec.model.ColumnMeta;
import com.zjht.unified.service.v8exec.model.IndexMeta;
import com.zjht.unified.service.v8exec.model.TableMeta;
import com.zjht.unified.service.v8exec.model.DataViewInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
            dbName = Constants.STORE_DBNAME_PREFIX + taskContext.getVer();
        }
        log.info("获取表结构：{}.{}", dbName, tableName);
        meta.setDbName(dbName);
        meta.setTableName(tableName);
        String ver = taskContext.getVer();
        Long prjId = taskContext.getPrjInfo().getPrjId();

        String columnSql = String.format(
                "SELECT COLUMN_NAME, COLUMN_TYPE, COLUMN_COMMENT " +
                        "FROM information_schema.columns " +
                        "WHERE table_schema = '%s' AND table_name = '%s' ORDER BY ORDINAL_POSITION", dbName, tableName
        );
        List<Map<String, Object>> columnList = query(columnSql);

        List<ColumnMeta> columns = new ArrayList<>();
        for (Map<String, Object> row : columnList) {
            ColumnMeta col = new ColumnMeta();
            col.setName((String) row.get("COLUMN_NAME"));
            col.setType((String) row.get("COLUMN_TYPE"));
            col.setComment((String) row.get("COLUMN_COMMENT"));
            columns.add(col);
        }
        meta.setColumns(columns);

        String indexSql = String.format(
                "SELECT INDEX_NAME, COLUMN_NAME, INDEX_TYPE " +
                        "FROM information_schema.statistics " +
                        "WHERE table_schema = '%s' AND table_name = '%s' " +
                        "ORDER BY INDEX_NAME, SEQ_IN_INDEX", dbName, tableName
        );
        List<Map<String, Object>> indexList = query(indexSql);

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

    public void executeSql(String sql,boolean initFlag,String dbName) {
        R<Object> result = remoteStore.execute( sql,initFlag,dbName);
        if (result.getCode() == Constants.SUCCESS) {
            log.info("SQL执行成功: {}", sql);
        } else {
            log.error("远程执行SQL失败: {} {}", sql,result.getMsg());

        }
    }

    public List<Map<String, Object>> query( String sql, Object... args) {
        if (args == null || args.length == 0) {
            return remoteStore.query("", "", sql);
        } else {
            return remoteStore.queryWithArgs( sql, Arrays.asList(args));
        }
    }

    public List<DataViewInfo> listViewsWithSql(String dbName) {
        String sql = "SELECT TABLE_NAME, VIEW_DEFINITION " +
                "FROM information_schema.views " +
                "WHERE table_schema = '" + dbName + "'";

        List<DataViewInfo> views = new ArrayList<>();

        List<Map<String, Object>> rows = remoteStore.query("","",sql);
        log.info("rows:{}",rows);
        for (Map<String, Object> row : rows) {
            String viewName = (String) row.get("TABLE_NAME");
            String viewDef = (String) row.get("VIEW_DEFINITION");
            views.add(new DataViewInfo(viewName, dbName, viewDef));
        }
        log.info("views:{}",views);

        return views;
    }

}
