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
import com.caoccao.javet.values.reference.*;
import com.caoccao.javet.values.reference.builtin.V8ValueBuiltInSymbol;
import com.wukong.core.util.ThreadLocalUtil;
import com.wukong.core.weblog.utils.StringUtil;
import com.zjht.unified.common.core.constants.Constants;
import com.zjht.unified.common.core.util.SpringUtils;
import com.zjht.unified.domain.composite.ClazzDefCompositeDO;
import com.zjht.unified.domain.composite.FieldDefCompositeDO;
import com.zjht.unified.domain.composite.MethodDefCompositeDO;
import com.zjht.unified.domain.runtime.UnifiedObject;
import com.zjht.unified.domain.simple.FieldDefDO;
import com.zjht.unified.domain.simple.MethodDefDO;
import com.zjht.unified.jsengine.v8.utils.V8BeanUtils;
import com.zjht.unified.service.ctx.RtRedisObjectStorageService;
import com.zjht.unified.service.ctx.TaskContext;
import com.zjht.unified.service.v8exec.model.ClsDf;
import groovy.util.logging.Slf4j;
import jdk.net.SocketFlow;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.time.temporal.ChronoField;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Data
public class ProxyObject implements IJavetDirectProxyHandler<Exception>   {

    private static final Logger log = LoggerFactory.getLogger(ProxyObject.class);


    public ProxyObject(TaskContext taskContext, String guid, String clazzGUID) {
        this.taskContext = taskContext;
        this.guid = guid;
        this.clazzGUID = clazzGUID;
        methodSet = taskContext.getMethods().values().stream().map(MethodDefDO::getName).collect(Collectors.toSet());
        fieldDefMap = taskContext.getClazzGUIDMap().get(clazzGUID).getClazzIdFieldDefList().stream().collect(Collectors.toMap(FieldDefCompositeDO::getName, Function.identity()));
    }


    private Map<String,Object> snapshot;

    private TaskContext taskContext;

    private String guid;

    private String clazzGUID;

    private Map<String,AttrWrapper> fieldObjectMap = new HashMap<>();

    private Set<String> methodSet = new HashSet<>();

    private Map<String, FieldDefCompositeDO> fieldDefMap = new HashMap<>();

    @Override
    public V8Value symbolToPrimitive(V8Value... v8Values) throws JavetException, Exception {
        System.out.println("==========symbolToPrimitive called=============================");
        return IJavetDirectProxyHandler.super.symbolToPrimitive(v8Values);
    }

    @Override
    public V8Value proxyGet(V8Value target, V8Value property, V8Value receiver) throws JavetException {
        log.info("proxyobj : {} proxyGet:{}" ,guid, property.toString());
        String key = null;

        if (methodSet.contains(property.toString())) {
            try {
                return IJavetDirectProxyHandler.super.proxyGet(target, property, receiver);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

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
                log.error(e.getMessage());
                log.error(e.getMessage());
                log.error(e.getMessage());


            }

        if (target instanceof V8ValueString) {
            for (ChronoField value : ChronoField.values()) {
                System.out.println("value.toString() = " + value.toString());
                System.out.println();
                System.out.println();

            }
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
        if("cls".equalsIgnoreCase(key)){
            ClazzDefCompositeDO cls = taskContext.getClazzGUIDMap().get(clazzGUID);
            return V8BeanUtils.toV8Value(getV8Runtime(), ClsDf.from(cls,taskContext));
        }
        if ("pv".equalsIgnoreCase(key)){
            ClazzDefCompositeDO cls = taskContext.getClazzGUIDMap().get(clazzGUID);
            key = cls.getPvAttr();
        }

        if(snapshot!=null){
            Object t = snapshot.get(key);
            if(t!=null)
                return convertToV8Value(t);
        }


        RtRedisObjectStorageService rtRedisObjectStorageService = SpringUtils.getBean(RtRedisObjectStorageService.class);
        Object objectAttrValue = rtRedisObjectStorageService.getObjectAttrValue(taskContext, guid, key);
        if (Objects.nonNull(objectAttrValue)) {
            if (objectAttrValue instanceof UnifiedObject) {
                UnifiedObject uo = rtRedisObjectStorageService.getObject(taskContext, ((UnifiedObject) objectAttrValue).getGuid());
                ProxyObject t = SpringUtils.getBean(V8RttiService.class).createFromUnifiedObject(taskContext, uo);
                return new JavetProxyConverter().toV8Value(getV8Runtime(), t);
            } else {
                AttrWrapper attrWrapper = fieldObjectMap.get(key);
                if (Objects.isNull(attrWrapper)) {
                    //todo init eval and archivestatus
                    FieldDefCompositeDO fieldDefCompositeDO = fieldDefMap.get(key);
                    attrWrapper = new AttrWrapper(null, null, fieldDefCompositeDO.getArchiveStatus(), fieldDefCompositeDO.getEval(), taskContext,guid, key);
                    fieldObjectMap.put(key, attrWrapper);
                }
                return new JavetProxyConverter().toV8Value(getV8Runtime(), attrWrapper);
            }
        }

        return getV8Runtime().createV8ValueUndefined();
    }

    @Override
    public V8ValueBoolean proxySet(V8Value target, V8Value property, V8Value value, V8Value receiver) throws JavetException {
        log.info("proxyobj :{} proxySet  property:{} value:{}", guid, property, value);
        String key = null;
        if (property instanceof V8ValueString) {
            key = ((V8ValueString) property).getValue();
        }
        if (key == null)
            return getV8Runtime().createV8ValueBoolean(false);

        if (methodSet.contains(property.toString())) {
            try {
                return IJavetDirectProxyHandler.super.proxySet(target, property, value, receiver);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        AttrWrapper attrWrapper = fieldObjectMap.get(key);
        if (Objects.nonNull(attrWrapper)) {
            int archiveStatus = attrWrapper.getArchiveStatus();
            System.out.println("archiveStatus = " + archiveStatus);
            if (archiveStatus==1) {
                return getV8Runtime().createV8ValueBoolean(true);
            }
        }


        Object o = convertFromV8Value(value);
        RtRedisObjectStorageService rtRedisObjectStorageService = SpringUtils.getBean(RtRedisObjectStorageService.class);
        if (o != null) {
            UnifiedObject object = rtRedisObjectStorageService.getObject(taskContext, guid);
            boolean dispatch = false;
            if (object.getPersistTag()) {
                ClazzDefCompositeDO clazzDefCompositeDO = this.getTaskContext().getClazzGUIDMap().get(clazzGUID);
                List<FieldDefCompositeDO> clazzIdFieldDefList = clazzDefCompositeDO.getClazzIdFieldDefList();
                String finalKey = key;
                dispatch = clazzIdFieldDefList.stream().anyMatch(fieldDefCompositeDO -> fieldDefCompositeDO.getName().equals(finalKey));
            }

            if (Objects.nonNull(attrWrapper)) {
                Object lastValue = attrWrapper.getCurrentValue();
                attrWrapper.setLastValue(lastValue);
                V8EngineService engineService = SpringUtils.getBean(V8EngineService.class);
                if (StringUtil.isNotBlank(attrWrapper.getEval())) {
                    log.info("attrWrapper.getEval() = {} " ,attrWrapper.getEval());
                    Boolean evalResult = (Boolean)engineService.exec(lastValue + " " + attrWrapper.getEval(), taskContext);
                    if (evalResult) {
                        attrWrapper.setLastEV(lastValue);
                    }
                }
            }

            rtRedisObjectStorageService.setObjectAttrValue(taskContext, this.guid, key, o, dispatch);


        } else {
            rtRedisObjectStorageService.delObjectAttr(taskContext, this.guid, key);
        }

        if(snapshot!=null){
            snapshot.remove(key);
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
