package com.zjht.unified.service.v8exec;

import com.caoccao.javet.annotations.V8Function;
import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interop.V8Runtime;
import com.caoccao.javet.interop.converters.JavetProxyConverter;
import com.caoccao.javet.values.reference.V8ValueArray;
import com.caoccao.javet.values.reference.V8ValueObject;
import com.zjht.unified.domain.composite.ClazzDefCompositeDO;
import com.zjht.unified.feign.RemoteStore;
import com.zjht.unified.jsengine.v8.utils.V8BeanUtils;
import com.zjht.unified.service.ctx.EntityDepService;
import com.zjht.unified.service.ctx.TaskContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

@Slf4j
public class RecordUtils {

    private TaskContext taskContext;

    private String prjGuid;

    private String prjVer;

    @Autowired
    private RemoteStore remoteStore;

    @Autowired
    private EntityDepService entityDepService;


    public RecordUtils(String prjGuid, String prjVer, TaskContext taskContext) {
        this.prjGuid = prjGuid;
        this.prjVer = prjVer;
        this.taskContext = taskContext;
    }

    /**
     * 通过clsName获得表明，再拼接whereClause获得完整的sql，使用RemoteStore通过sql查到数据后转为ProxyObject的元素的V8ValueArray结构返回
     * 通过taskContext获得ClazzDefCompositeDO对象，表名在ClazzDefCompositeDO.tbl字段上
     * @param whereClause
     * @param clsName
     * @return
     */
    @V8Function(name="query")
    public V8ValueArray query(String whereClause, String clsName) throws JavetException {
        // Step 1: Retrieve the ClazzDefCompositeDO object using clsName
        ClazzDefCompositeDO clazzDef = entityDepService.getClsByName(taskContext,clsName);
        if (clazzDef == null) {
            throw new IllegalArgumentException("Class definition not found for class name: " + clsName);
        }

        // Step 2: Construct the SQL query
        String tableName = clazzDef.getTbl();
        String sql = "SELECT * FROM " + tableName + " WHERE " + whereClause;

        // Step 3: Execute the query using RemoteStore
        List<Map<String, Object>> results = remoteStore.query(taskContext.getVer(), taskContext.getPrjInfo().getPrjId()+"", sql);

        // Step 4: Convert results to V8ValueArray
        V8Runtime v8Runtime = V8EngineService.getRuntime(taskContext, prjGuid, prjVer);
        V8ValueArray v8ValueArray = v8Runtime.createV8ValueArray();
        JavetProxyConverter proxyConverter = new JavetProxyConverter();

        for (Map<String, Object> result : results) {
            ProxyObject proxyObject = new ProxyObject(taskContext, result.get("guid").toString(), clazzDef.getGuid());
            proxyObject.setSnapshot(result);
            v8ValueArray.push(proxyConverter.toV8Value(v8Runtime, proxyObject));
        }

        return v8ValueArray;
    }


        /**
     * 使用RemoteStore直接查询sql获得数据，如果clsName则将返回数据转为ProxyObject的元素的V8ValueArray结构返回，否者转为V8ValueMap的元素的V8ValueArray结构返回
     * @param sql
     * @param clsName
     * @return
     */
    @V8Function(name="sql")
    public V8ValueArray sql(String sql, String clsName) throws JavetException {
        // Step 1: Execute the SQL query using RemoteStore
        List<Map<String, Object>> results = remoteStore.query(taskContext.getVer(), taskContext.getPrjInfo().getPrjId()+"", sql);

        // Step 2: Convert results to V8ValueArray
        V8Runtime v8Runtime = V8EngineService.getRuntime(taskContext, prjGuid, prjVer);
        V8ValueArray v8ValueArray = v8Runtime.createV8ValueArray();
        JavetProxyConverter proxyConverter = new JavetProxyConverter();

        if (clsName != null && !clsName.isEmpty()) {
            // If clsName is provided, convert results to ProxyObject and then to V8ValueArray
            ClazzDefCompositeDO clazzDef = entityDepService.getClsByName(taskContext,clsName);
            if (clazzDef == null) {
                throw new IllegalArgumentException("Class definition not found for class name: " + clsName);
            }

            for (Map<String, Object> result : results) {
                ProxyObject proxyObject = new ProxyObject(taskContext, result.get("guid").toString(), clazzDef.getGuid());
                proxyObject.setSnapshot(result);
                v8ValueArray.push(proxyConverter.toV8Value(v8Runtime, proxyObject));
            }
        } else {
            // If clsName is not provided, convert results to V8ValueMap and then to V8ValueArray
            for (Map<String, Object> result : results) {
                V8ValueObject v8ValueObject = v8Runtime.createV8ValueObject();
                for (Map.Entry<String, Object> entry : result.entrySet()) {
                    v8ValueObject.set(entry.getKey(), V8BeanUtils.toV8Value(v8Runtime,entry.getValue()));
                }
                v8ValueArray.push(v8ValueObject);
            }
        }

        return v8ValueArray;
    }
}

