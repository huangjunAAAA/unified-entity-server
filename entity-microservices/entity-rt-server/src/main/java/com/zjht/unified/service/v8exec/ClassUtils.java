package com.zjht.unified.service.v8exec;

import com.caoccao.javet.annotations.V8Function;
import com.caoccao.javet.interop.V8Runtime;
import com.caoccao.javet.interop.converters.JavetProxyConverter;
import com.caoccao.javet.values.V8Value;
import com.wukong.core.util.ThreadLocalUtil;
import com.zjht.unified.common.core.util.SpringUtils;
import com.zjht.unified.domain.composite.ClazzDefCompositeDO;
import com.zjht.unified.domain.composite.FieldDefCompositeDO;
import com.zjht.unified.service.ctx.RtRedisObjectStorageService;
import com.zjht.unified.service.ctx.TaskContext;
import groovy.util.logging.Slf4j;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Slf4j
public class ClassUtils {

    private static final Logger log = LoggerFactory.getLogger(ClassUtils.class);
    private TaskContext taskContext;

    @Autowired
    private V8RttiService v8RttiService;


    public ClassUtils(TaskContext taskContext) {
        this.taskContext = taskContext;
    }

    @V8Function(name = "new")
    public V8Value newInstance(String className) throws Exception {
        ClazzDefCompositeDO classDef = taskContext.getClazzMap().get(className);
        if (classDef == null) {
            log.error("ClassName not found in ClazzMap:  {}",className);
            return null;
        }

        ProxyObject proxyObject = v8RttiService.createNewObject(classDef, taskContext);
        V8Runtime v8Runtime = V8EngineService.getRuntime(taskContext);
        return new JavetProxyConverter().toV8Value(v8Runtime, proxyObject);
    }
}


