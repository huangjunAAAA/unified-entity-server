package com.zjht.unified.service.v8exec;

import com.caoccao.javet.annotations.V8Function;
import com.caoccao.javet.interop.V8Runtime;
import com.caoccao.javet.interop.converters.JavetProxyConverter;
import com.caoccao.javet.values.V8Value;
import com.zjht.unified.common.core.util.SpringUtils;
import com.zjht.unified.domain.composite.ClazzDefCompositeDO;
import com.zjht.unified.domain.composite.FieldDefCompositeDO;
import com.zjht.unified.service.ctx.RtRedisObjectStorageService;
import com.zjht.unified.service.ctx.TaskContext;
import groovy.util.logging.Slf4j;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Data
public class ClassUtils {

    private static final Logger log = LoggerFactory.getLogger(ClassUtils.class);
    private V8Runtime v8Runtime;
    private TaskContext taskContext;

    private RtRedisObjectStorageService redisService;


    public ClassUtils(V8Runtime v8Runtime, TaskContext taskContext) {
        this.v8Runtime = v8Runtime;
        this.taskContext = taskContext;
    }

    @V8Function(name = "new")
    public V8Value newInstance(String className) throws Exception {
        ClazzDefCompositeDO classDef = taskContext.getClazzMap().get(className);
        if (classDef == null) {
            log.error("ClassName not found in ClazzMap:  {}",className);
        }

        ProxyObject proxyObject = new ProxyObject(v8Runtime);
        proxyObject.setRtRedisObjectStorageService(redisService);
        proxyObject.setTaskContext(taskContext);
        String guid = UUID.randomUUID().toString();
        proxyObject.setGuid(guid);

        //加载默认值
        if (Objects.nonNull(classDef)) {
            List<FieldDefCompositeDO> clazzIdFieldDefList = classDef.getClazzIdFieldDefList();
            for (FieldDefCompositeDO fieldDefCompositeDO : clazzIdFieldDefList) {
                redisService.setObjectAttrValue(taskContext,guid, fieldDefCompositeDO.getName(),fieldDefCompositeDO.getInitValue(), false);
            }
        }

        taskContext.getPobjMap().put(guid,proxyObject);
        return new JavetProxyConverter().toV8Value(v8Runtime, proxyObject);
    }
}


@Data
class InstanceUtils {
    private V8Runtime v8Runtime;

    private TaskContext taskContext;

    private RtRedisObjectStorageService redisService;


    public InstanceUtils(V8Runtime v8Runtime, TaskContext taskContext) {
        this.v8Runtime = v8Runtime;
        this.taskContext = taskContext;
    }

    @V8Function(name = "get")
    public V8Value getInstance(String guid) throws Exception {
//        ClazzDefCompositeDO classDef = taskContext.getClazzMap().get(className);
//        if (classDef == null) {
//            throw new RuntimeException("Class not found in ClazzMap:  " + className);
//        }
        ProxyObject proxyObject = taskContext.getPobjMap().get(guid);
        return new JavetProxyConverter().toV8Value(v8Runtime, proxyObject);
    }
}