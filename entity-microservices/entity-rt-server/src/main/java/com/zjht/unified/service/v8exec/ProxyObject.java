package com.zjht.unified.service.v8exec;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.caoccao.javet.enums.V8ValueSymbolType;
import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interfaces.IJavetUniFunction;
import com.caoccao.javet.interop.V8Runtime;
import com.caoccao.javet.interop.callback.IJavetDirectCallable;
import com.caoccao.javet.interop.callback.JavetCallbackContext;
import com.caoccao.javet.interop.callback.JavetCallbackType;
import com.caoccao.javet.interop.converters.JavetObjectConverter;
import com.caoccao.javet.interop.converters.JavetProxyConverter;
import com.caoccao.javet.interop.proxy.IJavetDirectProxyHandler;
import com.caoccao.javet.interop.proxy.IJavetProxyHandler;
import com.caoccao.javet.values.V8Value;
import com.caoccao.javet.values.primitive.*;
import com.caoccao.javet.values.reference.IV8ValueObject;
import com.caoccao.javet.values.reference.V8ValueObject;
import com.caoccao.javet.values.reference.V8ValueProxy;
import com.caoccao.javet.values.reference.V8ValueSymbol;
import com.caoccao.javet.values.reference.builtin.V8ValueBuiltInSymbol;
import com.wukong.core.util.ThreadLocalUtil;
import com.zjht.unified.common.core.constants.Constants;
import com.zjht.unified.common.core.util.SpringUtils;
import com.zjht.unified.domain.composite.ClazzDefCompositeDO;
import com.zjht.unified.domain.runtime.UnifiedObject;
import com.zjht.unified.service.ctx.RtRedisObjectStorageService;
import com.zjht.unified.service.ctx.TaskContext;
import groovy.util.logging.Slf4j;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Data
public class ProxyObject implements IJavetDirectProxyHandler<Exception> {

    private static final Logger log = LoggerFactory.getLogger(ProxyObject.class);


    public ProxyObject(TaskContext taskContext, String guid, String clazzGUID) {
        this.taskContext = taskContext;
        this.guid = guid;
        this.clazzGUID = clazzGUID;
    }

    private TaskContext taskContext;

    private String guid;

    private String clazzGUID;

    @Override
    public V8Value proxyGet(V8Value target, V8Value property, V8Value receiver) throws JavetException {
        log.info("proxyGet:" + property.toString());
        String key = null;

        if (property instanceof V8ValueSymbol)
            try {
                final V8ValueSymbol propertySymbol = (V8ValueSymbol) property;
                final String description = propertySymbol.getDescription();
                Optional<IJavetUniFunction<V8ValueSymbol, ? extends V8Value, Exception>> optionalGetter =
                        Optional.ofNullable(proxyGetSymbolGetterMap()).map(m -> m.get(description));
                if (optionalGetter.isPresent()) {
                    return optionalGetter.get().apply(propertySymbol);
                } else if (V8ValueBuiltInSymbol.SYMBOL_PROPERTY_TO_PRIMITIVE.equals(description)) {
                    return getV8Runtime().createV8ValueFunction(
                            new JavetCallbackContext(
                                    V8ValueBuiltInSymbol.SYMBOL_PROPERTY_TO_PRIMITIVE,
                                    V8ValueSymbolType.BuiltIn,
                                    JavetCallbackType.DirectCallNoThisAndResult,
                                    (IJavetDirectCallable.NoThisAndResult<?>) this::symbolToPrimitive));
                } else if (V8ValueBuiltInSymbol.SYMBOL_PROPERTY_ITERATOR.equals(description)) {
                    return  getV8Runtime().createV8ValueFunction(
                            new JavetCallbackContext(
                                    V8ValueBuiltInSymbol.SYMBOL_PROPERTY_ITERATOR,
                                    V8ValueSymbolType.BuiltIn,
                                    JavetCallbackType.DirectCallNoThisAndResult,
                                    (IJavetDirectCallable.NoThisAndResult<?>) this::symbolIterator));
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }

        if (property instanceof V8ValueString)
            try {
                final String propertyString = ((V8ValueString) property).getValue();
                Optional<IJavetUniFunction<String, ? extends V8Value, Exception>> optionalGetter =
                        Optional.ofNullable(proxyGetStringGetterMap()).map(m -> m.get(propertyString));
                if (optionalGetter.isPresent()) {
                    return optionalGetter.get().apply(propertyString);
                } else if (IJavetProxyHandler.FUNCTION_NAME_TO_JSON.equals(propertyString)) {
                    return getV8Runtime().createV8ValueFunction(
                            new JavetCallbackContext(
                                    IJavetProxyHandler.FUNCTION_NAME_TO_JSON,
                                    V8ValueSymbolType.BuiltIn,
                                    JavetCallbackType.DirectCallNoThisAndResult,
                                    (IJavetDirectCallable.NoThisAndResult<?>) this::toJSON));
                } else if (IJavetProxyHandler.FUNCTION_NAME_TO_V8_VALUE.equals(propertyString)) {
                    return getV8Runtime().createV8ValueFunction(
                            new JavetCallbackContext(
                                    IJavetProxyHandler.FUNCTION_NAME_TO_V8_VALUE,
                                    V8ValueSymbolType.BuiltIn,
                                    JavetCallbackType.DirectCallNoThisAndResult,
                                    (IJavetDirectCallable.NoThisAndResult<?>) this::symbolToPrimitive));
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }

        if (property instanceof V8ValueString) {
            key = ((V8ValueString) property).getValue();
        }

        if (property instanceof V8ValueSymbol) {
            key = ((V8ValueSymbol) property).getDescription();
        }

        if (key == null)
            return getV8Runtime().createV8ValueUndefined();
        if ("guid".equalsIgnoreCase(key))
            return convertToV8Value(guid);

        RtRedisObjectStorageService rtRedisObjectStorageService = SpringUtils.getBean(RtRedisObjectStorageService.class);
        Object objectAttrValue = rtRedisObjectStorageService.getObjectAttrValue(taskContext, guid, key);
        if (Objects.nonNull(objectAttrValue)) {
            if (objectAttrValue instanceof UnifiedObject) {
                UnifiedObject uo = rtRedisObjectStorageService.getObject(taskContext, ((UnifiedObject) objectAttrValue).getGuid());
                ProxyObject t = SpringUtils.getBean(V8RttiService.class).createFromUnifiedObject(taskContext, uo);
                return new JavetProxyConverter().toV8Value(getV8Runtime(), t);
            } else
                return convertToV8Value(objectAttrValue);
        }

        return getV8Runtime().createV8ValueUndefined();
    }

    @Override
    public V8ValueBoolean proxySet(V8Value target, V8Value property, V8Value value, V8Value receiver) throws JavetException {
        log.info("property = " + property);
        log.info("value = " + value);
        String key = null;
        if (property instanceof V8ValueString) {
            key = ((V8ValueString) property).getValue();
        }
        if (key == null)
            return getV8Runtime().createV8ValueBoolean(false);

        Object o = convertFromV8Value(value);
        RtRedisObjectStorageService rtRedisObjectStorageService = SpringUtils.getBean(RtRedisObjectStorageService.class);
        if (o != null) {
            rtRedisObjectStorageService.setObjectAttrValue(taskContext, this.guid, key, o, false);
        } else {
            rtRedisObjectStorageService.delObjectAttr(taskContext, this.guid, key);
        }

        return getV8Runtime().createV8ValueBoolean(true);
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

    private Object convertFromV8Value(V8Value value) throws JavetException {
        if (value instanceof V8ValueString) {
            return ((V8ValueString) value).getValue();
        } else if (value instanceof V8ValueInteger) {
            return ((V8ValueInteger) value).getValue();
        } else if (value instanceof V8ValueBoolean) {
            return ((V8ValueBoolean) value).getValue();
        } else if (value instanceof V8ValueDouble) {
            return ((V8ValueDouble) value).getValue();
        } else if (value instanceof V8ValueNull) {
            return null;
        } else if (value instanceof V8ValueProxy) {
            V8Value tid = ((V8ValueProxy) value).get("guid");
            if (tid != null) {
                String guid = tid.toString();
                return SpringUtils.getBean(RtRedisObjectStorageService.class).getObject(taskContext, guid);
            }
        }
        log.info("convertFromV8Value:" + value.toString());
        return null;
    }

    @Override
    public V8Runtime getV8Runtime() {
        return V8EngineService.getRuntime(taskContext);
    }

}
