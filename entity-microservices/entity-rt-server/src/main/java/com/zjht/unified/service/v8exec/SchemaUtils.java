package com.zjht.unified.service.v8exec;

import com.caoccao.javet.annotations.V8Function;
import com.caoccao.javet.interop.V8Runtime;
import com.caoccao.javet.values.V8Value;
import com.zjht.unified.common.core.constants.Constants;
import com.zjht.unified.common.core.util.SpringUtils;
import com.zjht.unified.jsengine.v8.utils.V8BeanUtils;
import com.zjht.unified.service.DbMetaService;
import com.zjht.unified.service.ctx.TaskContext;
import com.zjht.unified.service.v8exec.model.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;

import java.util.*;

@Slf4j
public class SchemaUtils {

    @Autowired
    private DbMetaService dbMetaService;

    private final TaskContext taskContext;
    private final String prjGuid;
    private final String prjVer;

    public SchemaUtils(TaskContext taskContext, String prjGuid, String prjVer) {
        this.taskContext = taskContext;
        this.prjGuid = prjGuid;
        this.prjVer = prjVer;
    }

    public static class V8Column {
        public  String name;
        public  String type;
        public  String comment;

        public V8Column(String name, String type, String comment) {
            this.name = name;
            this.type = type;
            this.comment = comment;
        }
    }

    public static class V8Index {
        public  String name;
        public  String type;
        public  List<String> columns;

        public V8Index(String name, String type, List<String> columns) {
            this.name = name;
            this.type = type;
            this.columns = Collections.unmodifiableList(columns);
        }
    }

    public static SchemaUtils getSchemaUtils(TaskContext ctx) {
        SchemaUtils utils = new SchemaUtils(ctx, ctx.getPrjInfo().getPrjGuid(), ctx.getPrjInfo().getPrjVer());
        AutowireCapableBeanFactory factory = SpringUtils.getApplicationContext().getAutowireCapableBeanFactory();
        factory.autowireBean(utils);
        return utils;
    }

    public TableInfo getTableObj(String dbName, String tableName) {
        TableMeta meta = dbMetaService.getTableMeta(dbName, tableName, taskContext);
        List<V8Column> v8Columns = new ArrayList<>();
        for (ColumnMeta col : meta.getColumns()) {
            v8Columns.add(new V8Column(col.getName(), col.getType(), col.getComment()));
        }

        List<V8Index> v8Indices = new ArrayList<>();
        for (IndexMeta idx : meta.getIndices()) {
            v8Indices.add(new V8Index(idx.getName(), idx.getType(), idx.getColumns()));
        }

        log.info("表 [{}] 列数：{}，索引数：{}", meta.getTableName(), v8Columns.size(), v8Indices.size());
        return new TableInfo(meta.getDbName(), meta.getTableName(), v8Columns, v8Indices);
    }

    public List<DataViewInfo> listDVObj() {
        String dbName = Constants.STORE_DBNAME_PREFIX + taskContext.getVer();
        log.info("列出数据库 [{}] 中所有视图", dbName);
        List<DataViewInfo> views = dbMetaService.listViewsWithSql(dbName);
        log.info("共 {} 个视图", views.size());
        return views;
    }

    public DataViewInfo createDataViewObj(String viewSql, String viewName) {
        String dbName = Constants.STORE_DBNAME_PREFIX + taskContext.getVer();
        String ddl = "CREATE OR REPLACE VIEW `" + dbName + "`.`" + viewName + "` AS " + viewSql;
        log.info("创建视图 [{}]：{}", viewName, ddl);
        dbMetaService.executeSql(ddl, true, dbName);
        return new DataViewInfo(viewName, dbName, viewSql);
    }

    public boolean dropDVObj(String viewName) {
        String dbName = Constants.STORE_DBNAME_PREFIX + taskContext.getVer();
        String sql = String.format("DROP VIEW IF EXISTS `%s`.`%s`", dbName, viewName);
        log.info("删除视图 [{}] SQL：{}", viewName, sql);
        dbMetaService.executeSql(sql, false, "");
        return true;
    }

    // ==================== V8 暴露方法 ====================

    @V8Function(name = "getTable")
    public V8Value getTable( String tableName) {
        try {
            TableInfo table = getTableObj("", tableName);
            return V8BeanUtils.toV8Value(getRuntime(), table);
        } catch (Exception e) {
            log.error("获取表结构失败：[{}]，错误：{}", tableName, e.getMessage(), e);
            return safeV8Null();
        }
    }

    @V8Function(name = "listDV")
    public V8Value listDV() {
        try {
            List<DataViewInfo> views = listDVObj();
            return V8BeanUtils.toV8Value(getRuntime(), views);
        } catch (Exception e) {
            log.error("获取视图列表失败：{}", e.getMessage(), e);
            return safeV8Null();
        }
    }

    @V8Function(name = "createDataView")
    public V8Value createDataView(String viewSql, String viewName) {
        try {
            DataViewInfo view = createDataViewObj(viewSql, viewName);
            return V8BeanUtils.toV8Value(getRuntime(), view);
        } catch (Exception e) {
            log.error("创建视图失败 [{}]：{}", viewName, e.getMessage(), e);
            return safeV8Null();
        }
    }

    @V8Function(name = "dropDV")
    public V8Value dropDV(String viewName) {
        try {
            boolean success = dropDVObj(viewName);
            return getRuntime().createV8ValueBoolean(success);
        } catch (Exception e) {
            log.error("删除视图失败 [{}]：{}", viewName, e.getMessage(), e);
            return safeV8Null();
        }
    }

    private V8Runtime getRuntime() throws Exception {
        return V8EngineService.getRuntime(taskContext, prjGuid, prjVer);
    }

    private V8Value safeV8Null() {
        try {
            return getRuntime().createV8ValueNull();
        } catch (Exception e) {
            log.warn("无法创建 V8 null 对象：{}", e.getMessage());
            return null;
        }
    }







}
