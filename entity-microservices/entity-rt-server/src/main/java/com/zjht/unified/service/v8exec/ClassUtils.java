package com.zjht.unified.service.v8exec;

import com.caoccao.javet.annotations.V8Function;
import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interop.V8Runtime;
import com.caoccao.javet.interop.converters.JavetProxyConverter;
import com.caoccao.javet.values.V8Value;
import com.caoccao.javet.values.reference.V8ValueObject;
import com.wukong.core.util.ThreadLocalUtil;
import com.zjht.unified.common.core.constants.CoreClazzDef;
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
        String cguid = CoreClazzDef.getCoreClassGuid(className);
        if(cguid!=null){

        }else {
            ClazzDefCompositeDO classDef = taskContext.getClazzMap().get(className);
            if (classDef == null) {
                log.error("ClassName not found in ClazzMap:  {}", className);
                return null;
            }

            ProxyObject proxyObject = v8RttiService.createNewObject(classDef, taskContext);
            V8Runtime v8Runtime = V8EngineService.getRuntime(taskContext);
            return new JavetProxyConverter().toV8Value(v8Runtime, proxyObject);
        }
    }

    public V8Value newCoreInstance(String guid){

    }

    public V8Value getClassObject(V8ValueObject obj){
        try {
            return obj.get("cls");
        } catch (JavetException e) {
            log.error(e.getMessage(),e);
        }
        return obj.getV8Runtime().createV8ValueNull();
    }

    public V8Value getClassObject(String guid){
        ClazzDefCompositeDO clsObj = CoreClazzDef.getCoreClassObject(guid);
        if(clsObj==null){
            clsObj=taskContext.getClazzGUIDMap().get(guid);
        }
        V8Runtime v8Runtime = V8EngineService.getRuntime(taskContext);
        if(clsObj==null){
            return v8Runtime.createV8ValueNull();
        }else{
            return convertClassObject(clsObj,v8Runtime);
        }
    }

    private static V8Value convertClassObject(ClazzDefCompositeDO cls,V8Runtime v8Runtime){
        return null;
    }
}


