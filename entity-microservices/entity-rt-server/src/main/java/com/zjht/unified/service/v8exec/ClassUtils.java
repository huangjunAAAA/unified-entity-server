package com.zjht.unified.service.v8exec;

import com.caoccao.javet.annotations.V8Function;
import com.caoccao.javet.interop.V8Runtime;
import com.caoccao.javet.interop.converters.JavetProxyConverter;
import com.caoccao.javet.values.V8Value;
import com.caoccao.javet.values.primitive.V8ValueString;
import com.caoccao.javet.values.reference.V8ValueFunction;
import com.caoccao.javet.values.reference.V8ValueObject;
import com.caoccao.javet.exceptions.JavetException;
import com.wukong.core.util.ThreadLocalUtil;
import com.zjht.unified.common.core.constants.CoreClazzDef;
import com.zjht.unified.common.core.util.SpringUtils;
import com.zjht.unified.domain.composite.ClazzDefCompositeDO;
import com.zjht.unified.domain.composite.FieldDefCompositeDO;
import com.zjht.unified.domain.composite.MethodDefCompositeDO;
import com.zjht.unified.domain.simple.MethodParamDO;
import com.zjht.unified.service.ctx.RtRedisObjectStorageService;
import com.zjht.unified.service.ctx.TaskContext;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class ClassUtils {


    private TaskContext taskContext;

    @Autowired
    private V8RttiService v8RttiService;


    public ClassUtils(TaskContext taskContext) {
        this.taskContext = taskContext;
    }

    @V8Function(name = "new")
    public V8Value newInstance(String className, V8Value... args) throws Exception {
        System.out.println("args = " + Arrays.toString(args));

        String cguid = CoreClazzDef.getCoreClassGuid(className);
        if (cguid != null) {
            return V8EngineService.getRuntime(taskContext).createV8ValueNull();
        } else {
            ClazzDefCompositeDO classDef = taskContext.getClazzMap().get(className);
            if (classDef == null) {
                log.error("ClassName not found in ClazzMap:  {}", className);
                return null;
            }
            ProxyObject proxyObject = v8RttiService.createNewObject(classDef, taskContext, false);
            V8Runtime v8Runtime = V8EngineService.getRuntime(taskContext);
            V8Value v8Value = new JavetProxyConverter().toV8Value(v8Runtime, proxyObject);
            bindMethodsToV8Object(v8Value, classDef, v8Runtime);
            parseConstructMethod(args, classDef, v8Value);
            return v8Value;
        }
    }

    private static void parseConstructMethod(V8Value[] args, ClazzDefCompositeDO classDef, V8Value v8Value) throws JavetException {
        if (args.length > 0) {
            MethodDefCompositeDO matchingConstructor = classDef.getClazzIdMethodDefList().stream()
                    .filter(m -> m.getType() == 1) //
                    .filter(methodDef -> methodDef.getMethodIdMethodParamList().size() == args.length) // 匹配参数数量
                    .findFirst()
                    .orElse(null);

            log.info(" found match constructor : {}", matchingConstructor);
            if (Objects.nonNull(matchingConstructor)) {
                if (v8Value instanceof V8ValueObject) {
                    V8ValueObject v8ValueObject = (V8ValueObject) v8Value;
                    v8ValueObject.invoke(matchingConstructor.getName(), args);
                }
            }
        }
    }


    @V8Function(name = "newPersist")
    public V8Value newPersistInstance(String className, V8Value... args) throws Exception {
        ClazzDefCompositeDO classDef = taskContext.getClazzMap().get(className);
        if (classDef == null) {
            log.error("ClassName not found in ClazzMap:  {}", className);
            return null;
        }
        ProxyObject proxyObject = v8RttiService.createNewObject(classDef, taskContext, true);
        V8Runtime v8Runtime = V8EngineService.getRuntime(taskContext);
        V8Value v8Value = new JavetProxyConverter().toV8Value(v8Runtime, proxyObject);
        bindMethodsToV8Object(v8Value, classDef, v8Runtime);
        parseConstructMethod(args, classDef, v8Value);
        return v8Value;
    }


    @V8Function(name = "getClass")
    public V8Value getClassObject(V8ValueObject obj) {
        try {
            return obj.get("cls");
        } catch (JavetException e) {
            log.error(e.getMessage(), e);
        }
        return obj.getV8Runtime().createV8ValueNull();
    }

    @V8Function(name = "getClassByName")
    public V8Value getClassObjectByName(V8ValueObject clsName) {
        ClazzDefCompositeDO clsObj = taskContext.getClazzMap().get(clsName.toString());
        if (clsObj != null) {
            return convertClassObject(clsObj, clsName.getV8Runtime());
        }
        return clsName.getV8Runtime().createV8ValueNull();
    }

    @V8Function(name = "getClassByGuid")
    public V8Value getClassObject(String guid) {
        ClazzDefCompositeDO clsObj = CoreClazzDef.getCoreClassObject(guid);
        if (clsObj == null) {
            clsObj = taskContext.getClazzGUIDMap().get(guid);
        }
        V8Runtime v8Runtime = V8EngineService.getRuntime(taskContext);
        if (clsObj == null) {
            return v8Runtime.createV8ValueNull();
        } else {
            return convertClassObject(clsObj, v8Runtime);
        }
    }

    private static V8Value convertClassObject(ClazzDefCompositeDO cls, V8Runtime v8Runtime) {
        return null;
    }

    /***
     * 绑定js对象方法
     * @param v8Object
     * @param classDef
     * @param v8Runtime
     * @throws JavetException
     */
    public static void bindMethodsToV8Object(V8Value v8Object, ClazzDefCompositeDO classDef, V8Runtime v8Runtime) throws JavetException {
        if (v8Object instanceof V8ValueObject) {
            V8ValueObject value = (V8ValueObject) v8Object;
            for (MethodDefCompositeDO methodDef : classDef.getClazzIdMethodDefList()) {
                String methodName = methodDef.getName();
                String methodBody = methodDef.getBody();
                List<MethodParamDO> params = methodDef.getMethodIdMethodParamList();
                StringBuilder paramList = new StringBuilder();

                if (Objects.nonNull(params)) {
                    // **按 sort 排序参数**
                    params.sort(Comparator.comparingInt(MethodParamDO::getSort));

                    // 构造 JS 方法的参数列表
                    for (int i = 0; i < params.size(); i++) {
                        if (i > 0) {
                            paramList.append(", ");
                        }
                        paramList.append(params.get(i).getName());
                    }
                }

                // 生成JS方法定义
                String jsFunction = String.format("(function() { return function %s(%s) { %s }; })()", methodName, paramList.toString(), methodBody);
                V8ValueFunction functionValue = v8Runtime.getExecutor(jsFunction).execute();

                // 绑定到对象上
                value.set(methodName, functionValue);
                log.info("set method: {}  method body: {} on obj:{}", methodName, jsFunction,((V8ValueObject) v8Object).get("guid"));
            }
        }
    }

}


