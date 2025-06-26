package com.zjht.unified.service.v8exec;

import com.caoccao.javet.annotations.V8Function;
import com.caoccao.javet.interop.V8Runtime;
import com.caoccao.javet.interop.converters.JavetProxyConverter;
import com.caoccao.javet.values.V8Value;
import com.zjht.unified.common.core.util.SpringUtils;
import com.zjht.unified.jsengine.v8.utils.V8BeanUtils;
import com.zjht.unified.service.DbMetaService;
import com.zjht.unified.service.ctx.TaskContext;
import com.zjht.unified.service.v8exec.model.ColumnMeta;
import com.zjht.unified.service.v8exec.model.IndexMeta;
import com.zjht.unified.service.v8exec.model.TableMeta;
import com.zjht.unified.service.v8exec.model.V8Table;
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

    @V8Function(name = "getTable")
    public V8Value getTable(String dbName, String tableName) {
        try {
            TableMeta meta = dbMetaService.getTableMeta(dbName, tableName,taskContext);

            List<V8Column> v8Columns = new ArrayList<>();
            for (ColumnMeta col : meta.getColumns()) {
                v8Columns.add(new V8Column(col.getName(), col.getType(), col.getComment()));
            }

            List<V8Index> v8Indices = new ArrayList<>();
            for (IndexMeta idx : meta.getIndices()) {
                v8Indices.add(new V8Index(idx.getName(), idx.getType(), idx.getColumns()));
            }

            V8Table table = new V8Table(meta.getDbName(), meta.getTableName(), v8Columns, v8Indices);
            V8Runtime v8Runtime = V8EngineService.getRuntime(taskContext, prjGuid, prjVer);
            return V8BeanUtils.toV8Value(v8Runtime, table);
        } catch (Exception e) {
            log.error("Error getting table [{}].[{}]: {}", dbName, tableName, e.getMessage(), e);
            try {
                V8Runtime v8Runtime = V8EngineService.getRuntime(taskContext, prjGuid, prjVer);
                return v8Runtime.createV8ValueNull();
            } catch (Exception ex) {
                return null;
            }
        }

    }
}
