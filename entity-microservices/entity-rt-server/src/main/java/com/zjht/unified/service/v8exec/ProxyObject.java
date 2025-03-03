package com.zjht.unified.service.v8exec;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interop.V8Runtime;
import com.caoccao.javet.interop.proxy.IJavetDirectProxyHandler;
import com.caoccao.javet.values.V8Value;
import com.caoccao.javet.values.primitive.V8ValueBoolean;
import com.caoccao.javet.values.primitive.V8ValueDouble;
import com.caoccao.javet.values.primitive.V8ValueInteger;
import com.caoccao.javet.values.primitive.V8ValueString;
import com.caoccao.javet.values.reference.V8ValueObject;
import com.zjht.unified.common.core.util.SpringUtils;
import com.zjht.unified.service.ctx.RtRedisObjectStorageService;
import com.zjht.unified.service.ctx.TaskContext;
import groovy.util.logging.Slf4j;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.Objects;

@Slf4j
@Data
public class ProxyObject implements IJavetDirectProxyHandler<Exception> {

    private static final Logger log = LoggerFactory.getLogger(ProxyObject.class);
    private final V8Runtime v8Runtime;

    private RtRedisObjectStorageService rtRedisObjectStorageService ;


    private TaskContext taskContext;

    private String guid;

    public ProxyObject(V8Runtime v8Runtime) {
        this.v8Runtime = v8Runtime;
    }


    @Override
    public V8Value proxyGet(V8Value target, V8Value property, V8Value receiver) throws JavetException {
        System.out.println("proxyGet");

        if (property instanceof V8ValueString) {
            String key = ((V8ValueString) property).getValue();
            Object objectAttrValue = rtRedisObjectStorageService.getObjectAttrValue(taskContext, guid, key);
            if (Objects.nonNull(objectAttrValue)) {
                return convertToV8Value(objectAttrValue);
            }
//            try {
//                Field field = this.getClass().getDeclaredField(key);
//                field.setAccessible(true);
//                Object value = field.get(this);
//                return convertToV8Value(value);
//            } catch (NoSuchFieldException | IllegalAccessException e) {
//                return v8Runtime.createV8ValueUndefined();
//            }
        }
        return v8Runtime.createV8ValueUndefined();
    }

    @Override
    public V8ValueBoolean proxySet(V8Value target, V8Value property, V8Value value, V8Value receiver) throws JavetException {
        System.out.println("property = " + property);
        System.out.println("value = " + value);
        if (value instanceof V8ValueObject) {
            System.out.println(1111);
//            log.info("value tojson :{}", JSON.toJSONString(value));
        } else {
            System.out.println(2222);

        }
        if (property instanceof V8ValueString) {
            String key = ((V8ValueString) property).getValue();
            Object o = convertFromV8Value(value);
//            log.info("o is :{}",o);
            rtRedisObjectStorageService.setObjectAttrValue(taskContext, this.guid, key,o,false );
//            try {
//                Field field = this.getClass().getDeclaredField(key);
//                field.setAccessible(true);
//                field.set(this, convertFromV8Value(value));
//                return v8Runtime.createV8ValueBoolean(true);
//            } catch (NoSuchFieldException | IllegalAccessException e) {
//                return v8Runtime.createV8ValueBoolean(false);
//            }
        }
        return v8Runtime.createV8ValueBoolean(false);
    }

    private V8Value convertToV8Value(Object value) throws JavetException {
        if (value instanceof String) {
            return v8Runtime.createV8ValueString((String) value);
        } else if (value instanceof Integer) {
            return v8Runtime.createV8ValueInteger((Integer) value);
        } else if (value instanceof Boolean) {
            return v8Runtime.createV8ValueBoolean((Boolean) value);
        } else if (value instanceof Double) {
            return v8Runtime.createV8ValueDouble((Double) value);
        } else {
            return v8Runtime.createV8ValueNull();
        }
    }

    private Object convertFromV8Value(V8Value value) throws JavetException {
        if (value instanceof V8ValueString) {
            return ((V8ValueString) value).getValue();
        } else if (value instanceof V8ValueInteger) {
            return ((V8ValueInteger) value).getValue();
        } else if (value instanceof V8ValueBoolean) {
            return ((V8ValueBoolean) value).getValue();
        } else if (value instanceof V8ValueDouble) {
            return ((V8ValueDouble) value).getValue();
        }
        else if (value instanceof V8ValueObject) {
            try {
//                value.
//                return JSON.toJSONString((V8ValueObject )value, SerializerFeature.DisableCircularReferenceDetect);
                log.info("value is : {}",v8Runtime.getExecutor("JSON.stringify(value)").executeString());
                return null;
            } catch (Exception e) {
//                log.error("Failed to convert V8ValueObject to JSON: {}", e.getMessage(), e);
                return "{}"; // 返回空 JSON 字符串，避免 Redis 存储空值
            }
        }

        else {
            return null;
        }
    }

    @Override
    public V8Runtime getV8Runtime() {
        return v8Runtime;
    }

}
