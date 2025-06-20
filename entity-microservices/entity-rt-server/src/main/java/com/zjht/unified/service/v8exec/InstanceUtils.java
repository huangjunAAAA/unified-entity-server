package com.zjht.unified.service.v8exec;

import com.caoccao.javet.annotations.V8Function;
import com.caoccao.javet.interop.V8Runtime;
import com.caoccao.javet.interop.converters.JavetProxyConverter;
import com.caoccao.javet.values.V8Value;
import com.caoccao.javet.values.primitive.V8ValueString;
import com.caoccao.javet.values.reference.V8ValueObject;
import com.zjht.unified.domain.runtime.UnifiedObject;
import com.zjht.unified.service.ctx.RtRedisObjectStorageService;
import com.zjht.unified.service.ctx.TaskContext;
import groovy.util.logging.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;
import java.util.Objects;

@Slf4j
public class InstanceUtils {
    private static final Logger logger = LoggerFactory.getLogger(InstanceUtils.class);


    private TaskContext taskContext;
    @Autowired
    private V8RttiService v8RttiService;

    @Autowired
    private RtRedisObjectStorageService redisObjectStorageService;

    private String prjGuid;
    private String prjVer;

    public InstanceUtils(TaskContext taskContext, String prjGuid, String prjVer) {
        this.taskContext = taskContext;
        this.prjGuid = prjGuid;
        this.prjVer = prjVer;
    }

    @V8Function(name = "getEntity")
    public V8Value getInstance(String guid) throws Exception {
        ProxyObject proxyObject = v8RttiService.getObject(taskContext,guid);
        V8Runtime v8Runtime = V8EngineService.getRuntime(taskContext, prjGuid, prjVer);
        return new JavetProxyConverter().toV8Value(v8Runtime, proxyObject);
    }

    @V8Function(name = "deleteEntity")
    public void deleteEntity(V8Value obj) throws Exception {
        String guid = null;
        if(obj instanceof V8ValueString){
            guid = ((V8ValueString) obj).getValue();
        }else if(obj instanceof V8ValueObject){
            guid = ((V8ValueObject) obj).get("guid").toString();
        }else{
            guid=obj.toString();
        }
        // 获取对象
        ProxyObject proxyObject = v8RttiService.getObject(taskContext, guid);

        if (proxyObject == null) {
            logger.warn("Instance with guid {} not found!", guid);
        }
        boolean isDeleted = v8RttiService.deleteObject(taskContext, guid);

        if (isDeleted) {
            logger.info("Successfully deleted object with guid {}", guid);
        } else {
            logger.error("Failed to delete object with guid {}", guid);
        }
    }

    @V8Function(name = "listByClsGuid")
    public V8Value listByClsGuid(V8ValueString guid) throws Exception {
        return null;
    }

    // Shadow Copy: 浅拷贝功能
    @V8Function(name = "shadowCopy")
    public V8Value shadowCopy(String guid) throws Exception {
        // 获取原始对象
        ProxyObject originalProxyObject = v8RttiService.getObject(taskContext, guid);

        if (originalProxyObject == null) {
            logger.warn("Instance with guid {} not found, cannot perform shadowCopy!", guid);
            return null;
        }

        // 创建新的 guid
        String newGuid = java.util.UUID.randomUUID().toString();

        // 创建新对象，guid 需要重新生成
        ProxyObject newProxyObject = new ProxyObject(taskContext, newGuid, originalProxyObject.getClazzGUID(),originalProxyObject.getPrjGuid(),originalProxyObject.getPrjVer());

        // 拷贝原对象的 Redis 属性值到新对象
        Map<String, Object> originalAttrValues = redisObjectStorageService.getObjectAttrValueMap(taskContext, guid,originalProxyObject.getPrjGuid(),originalProxyObject.getPrjVer());
        for (Map.Entry<String, Object> entry : originalAttrValues.entrySet()) {
            String attrName = entry.getKey();
            Object attrValue = entry.getValue();
            redisObjectStorageService.setObjectAttrValue(taskContext, newGuid, attrName, attrValue, false);
        }
        redisObjectStorageService.setObject(taskContext,new UnifiedObject(newGuid, originalProxyObject.getClazzGUID(),false,prjGuid,prjVer,taskContext.getVer()));

        // 返回新创建的 ProxyObject，转换成 V8Value 返回
        return new JavetProxyConverter().toV8Value(V8EngineService.getRuntime(taskContext, prjGuid, prjVer), newProxyObject);
    }
}

