package com.zjht.unified.service.v8exec;

import com.caoccao.javet.annotations.V8Property;
import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interop.V8Runtime;
import com.caoccao.javet.interop.proxy.IJavetDirectProxyHandler;
import com.caoccao.javet.values.V8Value;
import com.caoccao.javet.values.primitive.V8ValueBoolean;
import com.caoccao.javet.values.reference.V8ValueObject;
import com.zjht.unified.common.core.util.SpringUtils;
import com.zjht.unified.service.ctx.RtRedisObjectStorageService;
import com.zjht.unified.service.ctx.TaskContext;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
public class AttrWrapper implements IJavetDirectProxyHandler<Exception> {

    private Object lastValue;
    private Object lastEV;
    private int archiveStatus;
    private String eval;

    private TaskContext taskContext;

    private String objGUID;

    private String propertyKey;


    @Override
    public V8Value symbolToPrimitive(V8Value... v8Values) throws JavetException, Exception {
        log.info("AttrWrapper symbolToPrimitive callback  and source attr is:{} ", propertyKey);
        Object objectAttrValue = getCurrentValue();
        return convertToV8Value(objectAttrValue);

    }

    public Object getCurrentValue() {
        RtRedisObjectStorageService rtRedisObjectStorageService = SpringUtils.getBean(RtRedisObjectStorageService.class);
        return rtRedisObjectStorageService.getObjectAttrValue(taskContext, objGUID, propertyKey);
    }


    private V8Value convertToV8Value(Object value) throws JavetException {
        if (value == null)
            return getV8Runtime().createV8ValueNull();

        if (value instanceof Integer) {
            return getV8Runtime().createV8ValueInteger((Integer) value);
        } else if (value instanceof Long) {
            return getV8Runtime().createV8ValueLong((Long) value);
        } else if (value instanceof Double) {
            return getV8Runtime().createV8ValueDouble((Double) value);
        } else if (value instanceof Boolean) {
            return getV8Runtime().createV8ValueBoolean((Boolean) value);
        }
        return getV8Runtime().createV8ValueString(value.toString());
    }

    @Override
    public V8Value proxyGet(V8Value target, V8Value property, V8Value receiver) throws JavetException, Exception {
        log.info("attrWrapper proxyGet = " + property);
//        if (property.toString().equals("archiveStatus")) {
//            return getV8Runtime().createV8ValueInteger(archiveStatus);
//        }
        try {
            Field field = this.getClass().getDeclaredField(property.toString());
            field.setAccessible(true);
            Object value = field.get(this);
            return convertToV8Value(value);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            return IJavetDirectProxyHandler.super.proxyGet(target, property, receiver);
        }
    }


    @Override
    public V8ValueBoolean proxySet(V8Value target, V8Value propertyKey, V8Value propertyValue, V8Value receiver) throws JavetException, Exception {
        return IJavetDirectProxyHandler.super.proxySet(target, propertyKey, propertyValue, receiver);
    }

    @Override
    public V8Runtime getV8Runtime() {
        return V8EngineService.getRuntime(taskContext);
    }
}
