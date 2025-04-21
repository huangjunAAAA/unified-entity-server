package com.zjht.unified.service.v8exec;

import com.caoccao.javet.annotations.V8Function;
import com.caoccao.javet.interop.V8Runtime;
import com.caoccao.javet.interop.converters.JavetProxyConverter;
import com.caoccao.javet.values.V8Value;
import com.caoccao.javet.values.reference.V8ValueObject;
import com.zjht.unified.common.core.util.MobileUtil;
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

    public InstanceUtils(TaskContext taskContext) {
        this.taskContext = taskContext;
    }

    @V8Function(name = "get")
    public V8Value getInstance(String guid) throws Exception {
        ProxyObject proxyObject = v8RttiService.getObject(taskContext,guid);
        V8Runtime v8Runtime = V8EngineService.getRuntime(taskContext);
        return new JavetProxyConverter().toV8Value(v8Runtime, proxyObject);
    }

    @V8Function(name = "delByGuid")
    public V8Value delByGuid(String guid) throws Exception {
        // 获取对象
        ProxyObject proxyObject = v8RttiService.getObject(taskContext, guid);

        if (proxyObject == null) {
            logger.warn("Instance with guid {} not found!", guid);
            return null;
        }
        boolean isDeleted = v8RttiService.deleteObject(taskContext, guid);

        if (isDeleted) {
            logger.info("Successfully deleted object with guid {}", guid);
            return null;
        } else {
            logger.error("Failed to delete object with guid {}", guid);
            return null;
        }
    }

    @V8Function(name = "del")
    public V8Value del(V8Value proxyObjectValue) throws Exception {
        V8Value guid = null;
        if (proxyObjectValue instanceof V8ValueObject) {
            V8ValueObject v8valueObject = (V8ValueObject) proxyObjectValue;
            guid = v8valueObject.get("guid");
            System.out.println("guid = " + guid);
        }
        if (Objects.isNull(guid)) {
            return null;
        }
        boolean isDeleted = v8RttiService.deleteObject(taskContext, guid.toString());

        if (isDeleted) {
            logger.info("Successfully deleted object with guid {}", guid);
            return null;
        } else {
            logger.error("Failed to delete object with guid {}", guid);
            return null;
        }

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
        ProxyObject newProxyObject = new ProxyObject(taskContext, newGuid, originalProxyObject.getClazzGUID());

        // 拷贝原对象的 Redis 属性值到新对象
        Map<String, Object> originalAttrValues = redisObjectStorageService.getObjectAttrValueMap(taskContext, guid);
        for (Map.Entry<String, Object> entry : originalAttrValues.entrySet()) {
            String attrName = entry.getKey();
            Object attrValue = entry.getValue();
            redisObjectStorageService.setObjectAttrValue(taskContext, newGuid, attrName, attrValue, false);
        }
        redisObjectStorageService.setObject(taskContext,new UnifiedObject(newGuid, originalProxyObject.getClazzGUID(),false));

        // 返回新创建的 ProxyObject，转换成 V8Value 返回
        return new JavetProxyConverter().toV8Value(V8EngineService.getRuntime(taskContext), newProxyObject);
    }
}

