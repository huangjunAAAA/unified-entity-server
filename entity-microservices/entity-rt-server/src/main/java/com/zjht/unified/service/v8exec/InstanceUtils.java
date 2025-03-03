package com.zjht.unified.service.v8exec;

import com.caoccao.javet.annotations.V8Function;
import com.caoccao.javet.interop.V8Runtime;
import com.caoccao.javet.interop.converters.JavetProxyConverter;
import com.caoccao.javet.values.V8Value;
import com.zjht.unified.service.ctx.RtRedisObjectStorageService;
import com.zjht.unified.service.ctx.TaskContext;
import groovy.util.logging.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public class InstanceUtils {

    private TaskContext taskContext;
    @Autowired
    private V8RttiService v8RttiService;

    public InstanceUtils(TaskContext taskContext) {
        this.taskContext = taskContext;
    }

    @V8Function(name = "get")
    public V8Value getInstance(String guid) throws Exception {
        ProxyObject proxyObject = v8RttiService.getObject(taskContext,guid);
        V8Runtime v8Runtime = V8EngineService.getRuntime(taskContext);
        return new JavetProxyConverter().toV8Value(v8Runtime, proxyObject);
    }
}
