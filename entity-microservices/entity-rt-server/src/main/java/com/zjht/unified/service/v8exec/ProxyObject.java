package com.zjht.unified.service.v8exec;

import com.caoccao.javet.enums.V8ValueSymbolType;
import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interfaces.IJavetUniFunction;
import com.caoccao.javet.interop.V8Runtime;
import com.caoccao.javet.interop.callback.IJavetDirectCallable;
import com.caoccao.javet.interop.callback.JavetCallbackContext;
import com.caoccao.javet.interop.callback.JavetCallbackType;
import com.caoccao.javet.interop.converters.JavetProxyConverter;
import com.caoccao.javet.interop.proxy.IJavetDirectProxyHandler;
import com.caoccao.javet.interop.proxy.IJavetProxyHandler;
import com.caoccao.javet.values.V8Value;
import com.caoccao.javet.values.primitive.*;
import com.caoccao.javet.values.reference.V8ValueProxy;
import com.caoccao.javet.values.reference.V8ValueSymbol;
import com.caoccao.javet.values.reference.builtin.V8ValueBuiltInSymbol;
import com.wukong.core.weblog.utils.StringUtil;
import com.zjht.unified.common.core.constants.AttrConstants;
import com.zjht.unified.common.core.constants.Constants;
import com.zjht.unified.common.core.constants.FieldConstants;
import com.zjht.unified.common.core.util.SpringUtils;
import com.zjht.unified.domain.composite.ClazzDefCompositeDO;
import com.zjht.unified.domain.composite.FieldDefCompositeDO;
import com.zjht.unified.domain.runtime.UnifiedObject;
import com.zjht.unified.domain.simple.MethodDefDO;
import com.zjht.unified.jsengine.v8.utils.V8BeanUtils;
import com.zjht.unified.service.ctx.EntityDepService;
import com.zjht.unified.service.ctx.PrjUniqueInfo;
import com.zjht.unified.service.ctx.RtRedisObjectStorageService;
import com.zjht.unified.service.ctx.TaskContext;
import com.zjht.unified.service.v8exec.model.ClsDf;
import groovy.util.logging.Slf4j;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Data
public class ProxyObject implements IJavetDirectProxyHandler<Exception>   {

    private static final Logger log = LoggerFactory.getLogger(ProxyObject.class);

    public ProxyObject(TaskContext taskContext, String guid, String clazzGUID, String prjGuid, String prjVer) {
        this.taskContext = taskContext;
        this.guid = guid;
        this.clazzGUID = clazzGUID;
        this.prjVer = prjVer;
        this.prjGuid = prjGuid;
        EntityDepService entityDepService = SpringUtils.getBean(EntityDepService.class);
        ClazzDefCompositeDO clazzDef = entityDepService.getClsDefByGuid(taskContext,clazzGUID);
        methodSet = clazzDef.getClazzIdMethodDefList().stream().map(MethodDefDO::getName).collect(Collectors.toSet());
        fieldDefMap = clazzDef.getClazzIdFieldDefList().stream().collect(Collectors.toMap(FieldDefCompositeDO::getName, Function.identity()));
        if(clazzDef.getParentGuid()!=null){
            List<ClazzDefCompositeDO> parents = entityDepService.getClassDefWithParents(taskContext, clazzDef.getParentGuid());
            for (Iterator<ClazzDefCompositeDO> iterator = parents.iterator(); iterator.hasNext(); ) {
                ClazzDefCompositeDO p =  iterator.next();
                Set<String> extraMethods = p.getClazzIdMethodDefList().stream().map(MethodDefDO::getName).collect(Collectors.toSet());
                methodSet.addAll(extraMethods);
                Map<String, FieldDefCompositeDO> extraFields = p.getClazzIdFieldDefList().stream().collect(Collectors.toMap(FieldDefCompositeDO::getName, Function.identity()));
                fieldDefMap.putAll(extraFields);
            }
        }
    }

    public ProxyObject(TaskContext taskContext, String guid, String clazzGUID) {
        this.taskContext = taskContext;
        this.guid = guid;
        this.clazzGUID = clazzGUID;
        EntityDepService entityDepService = SpringUtils.getBean(EntityDepService.class);
        PrjUniqueInfo prjInfo  = entityDepService.getPrjInfoByGuid(taskContext, clazzGUID);
        this.prjGuid=prjInfo.getPrjGuid();
        this.prjVer=prjInfo.getPrjVer();
        RtRedisObjectStorageService rtRedisObjectStorageService=SpringUtils.getBean(RtRedisObjectStorageService.class);
        ClazzDefCompositeDO clazzDef = rtRedisObjectStorageService.getClsDef(taskContext, this.prjVer,clazzGUID);
        methodSet = clazzDef.getClazzIdMethodDefList().stream().map(MethodDefDO::getName).collect(Collectors.toSet());
        fieldDefMap = clazzDef.getClazzIdFieldDefList().stream().collect(Collectors.toMap(FieldDefCompositeDO::getName, Function.identity()));
        if(clazzDef.getParentGuid()!=null){
            List<ClazzDefCompositeDO> parents = entityDepService.getClassDefWithParents(taskContext, clazzDef.getParentGuid());
            for (Iterator<ClazzDefCompositeDO> iterator = parents.iterator(); iterator.hasNext(); ) {
                ClazzDefCompositeDO p =  iterator.next();
                Set<String> extraMethods = p.getClazzIdMethodDefList().stream().map(MethodDefDO::getName).collect(Collectors.toSet());
                methodSet.addAll(extraMethods);
                Map<String, FieldDefCompositeDO> extraFields = p.getClazzIdFieldDefList().stream().collect(Collectors.toMap(FieldDefCompositeDO::getName, Function.identity()));
                fieldDefMap.putAll(extraFields);
            }
        }
    }


    private final Map<String,Object> modified=new HashMap<>();

    private TaskContext taskContext;

    private String guid;

    private String clazzGUID;

    private String prjVer;

    private String prjGuid;

    private Map<String,AttrWrapper> fieldObjectMap = new HashMap<>();

    private Set<String> methodSet = new HashSet<>();

    private Map<String, FieldDefCompositeDO> fieldDefMap = new HashMap<>();

    @Override
    public V8Value symbolToPrimitive(V8Value... v8Values) throws JavetException, Exception {
        log.info("==========symbolToPrimitive called=============================");
        return IJavetDirectProxyHandler.super.symbolToPrimitive(v8Values);
    }

    @Override
    public V8Value proxyGet(V8Value target, V8Value property, V8Value receiver) throws JavetException {
        log.info("proxyobj : {} proxyGet:{}" ,guid, property.toString());
        String key = null;

        if (methodSet.contains(property.toString())) {
            try {
                V8EngineService.setMe(getV8Runtime(), this);
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
        if (key.equals(FieldConstants.OBJECT_SAVE)){
            return getV8Runtime().createV8ValueFunction(
                    new JavetCallbackContext(
                            IJavetProxyHandler.FUNCTION_NAME_TO_V8_VALUE,
                            V8ValueSymbolType.BuiltIn,
                            JavetCallbackType.DirectCallNoThisAndResult,
                            (IJavetDirectCallable.NoThisAndNoResult<?>) this::save));
        }
        if (FieldConstants.GUID.equalsIgnoreCase(key))
            return convertToV8Value(guid,getV8Runtime(),taskContext);
        if (FieldConstants.PROJECT_GUID.equalsIgnoreCase(key))
            return convertToV8Value(prjGuid,getV8Runtime(),taskContext);
        if (FieldConstants.PROJECT_VER.equalsIgnoreCase(key))
            return convertToV8Value(prjVer,getV8Runtime(),taskContext);
        if(FieldConstants.CLASS.equalsIgnoreCase(key)){
            RtRedisObjectStorageService rtRedisObjectStorageService = SpringUtils.getBean(RtRedisObjectStorageService.class);
            ClazzDefCompositeDO clazzDef = rtRedisObjectStorageService.getClsDef(taskContext, prjVer, clazzGUID);
            return V8BeanUtils.toV8Value(getV8Runtime(), ClsDf.from(clazzDef,taskContext));
        }
        if (AttrConstants._OBJ_CURRENT_VALUE.equalsIgnoreCase(key)){
            RtRedisObjectStorageService rtRedisObjectStorageService = SpringUtils.getBean(RtRedisObjectStorageService.class);
            ClazzDefCompositeDO clazzDef = rtRedisObjectStorageService.getClsDef(taskContext, prjVer, clazzGUID);
            key = clazzDef.getPvAttr();
        }

        synchronized (modified){
            Object t = modified.get(key);
            if(t!=null)
                return convertToV8Value(t,getV8Runtime(),taskContext);
        }


        RtRedisObjectStorageService rtRedisObjectStorageService = SpringUtils.getBean(RtRedisObjectStorageService.class);
        Object objectAttrValue = rtRedisObjectStorageService.getObjectAttrValue(taskContext, guid, key,prjGuid,prjVer);
        if (Objects.nonNull(objectAttrValue)) {
            if (objectAttrValue instanceof UnifiedObject) {
                ProxyObject t = SpringUtils.getBean(V8RttiService.class).createFromUnifiedObject(taskContext, (UnifiedObject) objectAttrValue);
                return new JavetProxyConverter().toV8Value(getV8Runtime(), t);
            } else {
                AttrWrapper attrWrapper = fieldObjectMap.get(key);
                if (Objects.isNull(attrWrapper)) {
                    //todo init eval and archivestatus
                    FieldDefCompositeDO fieldDefCompositeDO = fieldDefMap.get(key);
                    attrWrapper = new AttrWrapper(null, null, fieldDefCompositeDO.getArchiveStatus(),
                            taskContext,guid, key,prjGuid,prjVer,objectAttrValue);
                    fieldObjectMap.put(key, attrWrapper);
                }
                return new JavetProxyConverter().toV8Value(getV8Runtime(), attrWrapper);
            }
        }

        return getV8Runtime().createV8ValueUndefined();
    }

    public void mergeFields(Map<String, Object> values){
        fieldDefMap.keySet().forEach(key -> {
            Object value = values.get(key);
            if(value!=null){
                FieldDefCompositeDO fieldDf = fieldDefMap.get(key);
                if(Objects.equals(FieldConstants.FIELD_TYPE_REGULAR_CLASS,fieldDf.getType())){
                    Map<String,Object> fieldValues = (Map<String, Object>) value;
                    Object objValue = fieldValues.get(FieldConstants.GUID);
                    modified.put(key, objValue);
                }else if(Objects.equals(FieldConstants.FIELD_TYPE_CLS_REL,fieldDf.getType())){
                    Map<String,Object> fieldValues = (Map<String, Object>) value;
                    Object objValue = fieldValues.get(FieldConstants.ID);
                    modified.put(key, objValue);
                }else {
                    modified.put(key, value);
                }
            }
        });
        save();
    }

    public void save(V8Value... v8Values) {
        synchronized (modified) {
            if (!modified.isEmpty()) {
                for (Iterator<Map.Entry<String, Object>> iterator = modified.entrySet().iterator(); iterator.hasNext(); ) {
                    Map.Entry<String, Object> kv = iterator.next();
                    Object o = kv.getValue();
                    String key = kv.getKey();
                    RtRedisObjectStorageService rtRedisObjectStorageService = SpringUtils.getBean(RtRedisObjectStorageService.class);
                    if (o != null) {
                        if(o instanceof AttrWrapper){
                            rtRedisObjectStorageService.setObjectAttrValue(taskContext, this.guid, key, ((AttrWrapper) o).getCurrentValue(), !iterator.hasNext());
                        }else{
                            rtRedisObjectStorageService.setObjectAttrValue(taskContext, this.guid, key, o, !iterator.hasNext());
                        }
                    }
                }
            }
            modified.clear();
        }
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
        Object actualValue = convertFromV8Value(value,taskContext);
        AttrWrapper attrWrapper = fieldObjectMap.get(key);
        if (Objects.nonNull(attrWrapper)) {
            int archiveStatus = attrWrapper.getArchiveStatus();
            System.out.println("archiveStatus = " + archiveStatus);
            if (archiveStatus==1) {
                return getV8Runtime().createV8ValueBoolean(false);
            }
            Object lastValue = attrWrapper.getCurrentValue();
            attrWrapper.setLastValue(lastValue);
            attrWrapper.setCurrentValue(actualValue);
            modified.put(key, attrWrapper);
        }else {
            synchronized (modified) {
                modified.put(key, actualValue);
            }
        }
        return getV8Runtime().createV8ValueBoolean(true);
    }

    public static  V8Value convertToV8Value(Object value, V8Runtime runtime,TaskContext taskContext) throws JavetException {
        if (value == null)
            return runtime.createV8ValueNull();
        if (value instanceof AttrWrapper) {
            new JavetProxyConverter().toV8Value(runtime, value);
        }else if(value instanceof ProxyObject){
            return new JavetProxyConverter().toV8Value(runtime, value);
        }else if(value instanceof V8Value){
            return (V8Value) value;
        }else if (value instanceof Integer) {
            return runtime.createV8ValueInteger((Integer) value);
        } else if (value instanceof Long) {
            return runtime.createV8ValueLong((Long) value);
        } else if (value instanceof Double) {
            return runtime.createV8ValueDouble((Double) value);
        } else if (value instanceof Boolean) {
            return runtime.createV8ValueBoolean((Boolean) value);
        } else if (value instanceof UnifiedObject) {
            ProxyObject proxyObj=new ProxyObject(taskContext, ((UnifiedObject) value).getGuid(), ((UnifiedObject) value).getClazzGUID(), ((UnifiedObject) value).getPrjGuid(), ((UnifiedObject) value).getPrjVer());
            return new JavetProxyConverter().toV8Value(runtime, proxyObj);
        }
        return runtime.createV8ValueString(value.toString());
    }

    public static  Object convertFromV8Value(V8Value value, TaskContext taskContext) throws JavetException {
        if (value == null ||value instanceof V8ValueNull) {
            return null;
        } else if (value instanceof V8ValueString) {
            return ((V8ValueString) value).getValue();
        } else if (value instanceof V8ValueInteger) {
            return ((V8ValueInteger) value).getValue();
        } else if (value instanceof V8ValueBoolean) {
            return ((V8ValueBoolean) value).getValue();
        } else if (value instanceof V8ValueDouble) {
            return ((V8ValueDouble) value).getValue();
        } else if (value instanceof V8ValueProxy) {
            V8Value tid = ((V8ValueProxy) value).get("guid");
            if (tid != null) {
                V8Value prjGuid = ((V8ValueProxy) value).get("prjGuid");
                V8Value prjVer = ((V8ValueProxy) value).get("prjVer");
                String guid = tid.toString();
                return SpringUtils.getBean(RtRedisObjectStorageService.class).getObject(taskContext, guid,prjGuid.toString(),prjVer.toString());
            }
        }
        log.info("convertFromV8Value:" + value.toString());
        return null;
    }

    @Override
    public V8Runtime getV8Runtime() {
        return V8EngineService.getRuntime(taskContext, prjGuid, prjVer);
    }

}
