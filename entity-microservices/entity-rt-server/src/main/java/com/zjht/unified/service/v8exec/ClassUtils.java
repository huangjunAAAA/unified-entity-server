package com.zjht.unified.service.v8exec;

import com.caoccao.javet.annotations.V8Function;
import com.caoccao.javet.interop.V8Runtime;
import com.caoccao.javet.interop.converters.JavetProxyConverter;
import com.caoccao.javet.values.V8Value;
import com.caoccao.javet.values.reference.V8ValueFunction;
import com.caoccao.javet.values.reference.V8ValueObject;
import com.caoccao.javet.exceptions.JavetException;
import com.wukong.core.weblog.utils.StringUtil;
import com.zjht.unified.common.core.constants.CoreClazzDef;
import com.zjht.unified.domain.composite.ClazzDefCompositeDO;
import com.zjht.unified.domain.composite.MethodDefCompositeDO;
import com.zjht.unified.domain.simple.MethodParamDO;
import com.zjht.unified.jsengine.v8.utils.V8BeanUtils;
import com.zjht.unified.service.ctx.EntityDepService;
import com.zjht.unified.service.ctx.TaskContext;
import com.zjht.unified.service.v8exec.model.ClsDf;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

@Slf4j
public class ClassUtils {


    private TaskContext taskContext;

    @Autowired
    private V8RttiService v8RttiService;

    @Autowired
    private EntityDepService entityDepService;

    private String prjGuid;
    private String prjVer;

    public ClassUtils(TaskContext taskContext, String prjGuid, String prjVer) {
        this.taskContext = taskContext;
        this.prjGuid = prjGuid;
        this.prjVer = prjVer;
    }

    @V8Function(name = "createEntity")
    public V8Value newInstance(String className, Boolean isPersist,V8Value... args) throws Exception {
        log.info("new args = " + Arrays.toString(args));
        ClazzDefCompositeDO classDef = null;
        String cguid = CoreClazzDef.getCoreClassGuid(className);
        if (cguid != null) {
            classDef = CoreClazzDef.getCoreClassObject(cguid);
        } else {
            classDef = entityDepService.getClsByName(taskContext, className);
        }
        if (classDef == null) {
            log.error("ClassName not found in newInstance:  {}", className);
            return null;
        }
        ProxyObject proxyObject = v8RttiService.createNewObject(classDef, taskContext, isPersist);
        V8Runtime v8Runtime = V8EngineService.getRuntime(taskContext, prjGuid, prjVer);
        V8Value target = new JavetProxyConverter().toV8Value(v8Runtime, proxyObject);
        bindMethodsToV8Object(target, classDef.getClazzIdMethodDefList(), v8Runtime);
        parseConstructMethod(args, classDef, target);
        return target;
    }

    @V8Function(name = "createEntityByGuid")
    public V8Value newInstanceByGuid(String classGuid, Boolean isPersist,V8Value... args) throws Exception {
        log.info("new args = " + Arrays.toString(args));
        ClazzDefCompositeDO classDef = CoreClazzDef.getCoreClassObject(classGuid);;
        if (classDef != null) {
            classDef = entityDepService.getClsDefByGuid(taskContext, classGuid);
        }
        if (classDef == null) {
            log.error("ClassName not found in newInstance:  {}", classGuid);
            return null;
        }
        ProxyObject proxyObject = v8RttiService.createNewObject(classDef, taskContext, isPersist);
        V8Runtime v8Runtime = V8EngineService.getRuntime(taskContext, prjGuid, prjVer);
        V8Value target = new JavetProxyConverter().toV8Value(v8Runtime, proxyObject);
        bindMethodsToV8Object(target, classDef.getClazzIdMethodDefList(), v8Runtime);
        parseConstructMethod(args, classDef, target);
        return target;
    }

    private static void parseConstructMethod(V8Value[] args, ClazzDefCompositeDO classDef, V8Value target) throws JavetException {
        if (args.length > 0) {
            MethodDefCompositeDO matchingConstructor = classDef.getClazzIdMethodDefList().stream()
                    .filter(m -> m.getType() == 1) //
                    .filter(methodDef -> methodDef.getMethodIdMethodParamList().size() == args.length) // 匹配参数数量
                    .findFirst()
                    .orElse(null);

            log.info(" found match constructor : {}", matchingConstructor);
            if (Objects.nonNull(matchingConstructor)) {
                if (target instanceof V8ValueObject) {
                    V8ValueObject v8ValueObject = (V8ValueObject) target;
                    v8ValueObject.invoke(matchingConstructor.getName(), args);
                }
            }
        }
    }

    public static void parseConstructMethod(V8Runtime v8Runtime,List<Object> args, ClazzDefCompositeDO classDef, ProxyObject target){
        try {
            List<V8Value> v8args = new ArrayList<>();
            for (Object arg : args) {
                V8Value v8Value = V8BeanUtils.toV8Value(v8Runtime, arg);
                v8args.add(v8Value);
            }
            V8Value v8Target = new JavetProxyConverter().toV8Value(v8Runtime, target);
            parseConstructMethod(v8args.toArray(new V8Value[0]), classDef, v8Target);
        }catch (Exception e){
            log.error(e.getMessage(),e);
        }
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
        ClazzDefCompositeDO clsObj =null;
        String cguid = CoreClazzDef.getCoreClassGuid(clsName.toString());
        if (cguid != null) {
            clsObj=CoreClazzDef.getCoreClassObject(cguid);
        }else{
            clsObj=entityDepService.getClsByName(taskContext,clsName.toString());
        }
        if (clsObj != null) {
            return convertClassObject(clsObj, clsName.getV8Runtime());
        }
        return clsName.getV8Runtime().createV8ValueNull();
    }

    @V8Function(name = "getClassByGuid")
    public V8Value getClassObject(String guid) {
        ClazzDefCompositeDO clsObj = CoreClazzDef.getCoreClassObject(guid);
        if (clsObj == null) {
            clsObj = entityDepService.getClsDefByGuid(taskContext,guid);
        }
        V8Runtime v8Runtime = V8EngineService.getRuntime(taskContext, prjGuid, prjVer);
        if (clsObj == null) {
            return v8Runtime.createV8ValueNull();
        } else {
            return convertClassObject(clsObj, v8Runtime);
        }
    }

    private V8Value convertClassObject(ClazzDefCompositeDO cls, V8Runtime v8Runtime) {
        ClsDf clsDf = ClsDf.from(cls, taskContext);
        try {
            return V8BeanUtils.toV8Value(v8Runtime, clsDf);
        } catch (JavetException e) {
            log.error(e.getMessage(),e);
            throw new RuntimeException(e);
        }
    }

    /**
     * 绑定方法到 V8 对象
     *
     * @param v8Object      V8 对象
     * @param methodDefList 方法定义列表
     * @param v8Runtime     V8 运行时
     * @throws JavetException Javet 异常
     */
    public static void bindMethodsToV8Object(V8Value v8Object, List<MethodDefCompositeDO> methodDefList, V8Runtime v8Runtime) throws JavetException {
        if (v8Object instanceof V8ValueObject) {
            V8ValueObject value = (V8ValueObject) v8Object;
            for (MethodDefCompositeDO methodDef : methodDefList) {
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
                System.out.println("methodName: " + methodName);
                if (StringUtil.isNotBlank(methodName) && StringUtil.isNotBlank(methodBody)) {
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

}


