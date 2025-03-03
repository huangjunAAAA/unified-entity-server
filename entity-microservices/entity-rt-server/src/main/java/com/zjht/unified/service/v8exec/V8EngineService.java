package com.zjht.unified.service.v8exec;

import com.caoccao.javet.interception.logging.JavetStandardConsoleInterceptor;
import com.caoccao.javet.interop.V8Runtime;
import com.caoccao.javet.interop.converters.JavetProxyConverter;
import com.caoccao.javet.interop.engine.IJavetEngine;
import com.caoccao.javet.interop.engine.IJavetEnginePool;
import com.caoccao.javet.interop.engine.JavetEnginePool;
import com.caoccao.javet.values.reference.V8ValueObject;
import com.zjht.unified.common.core.util.SpringUtils;
import com.zjht.unified.domain.composite.ClazzDefCompositeDO;
import com.zjht.unified.domain.composite.FieldDefCompositeDO;
import com.zjht.unified.domain.composite.MethodDefCompositeDO;
import com.zjht.unified.domain.simple.MethodDefDO;
import com.zjht.unified.service.IScriptEngine;
import com.zjht.unified.service.ctx.RtRedisObjectStorageService;
import com.zjht.unified.service.ctx.TaskContext;
import groovy.util.logging.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.ArrayList;
import java.util.HashMap;


@Slf4j
@Service
public class V8EngineService implements IScriptEngine {

    private static final Logger log = LoggerFactory.getLogger(V8EngineService.class);
    @Autowired
    private RtRedisObjectStorageService rtRedisObjectStorageService;


    private static final IJavetEnginePool<V8Runtime> javetEnginePool = new JavetEnginePool<>();

    public void executeSimpleScript(String script) {
        try (IJavetEngine<V8Runtime> javetEngine = javetEnginePool.getEngine()) {
            V8Runtime v8Runtime = javetEngine.getV8Runtime();
            JavetStandardConsoleInterceptor consoleInterceptor = new JavetStandardConsoleInterceptor(v8Runtime);
            consoleInterceptor.register(v8Runtime.getGlobalObject());
            v8Runtime.getExecutor(script).executeVoid();
            consoleInterceptor.unregister(v8Runtime.getGlobalObject());
            v8Runtime.lowMemoryNotification();
        } catch (Exception e) {
            log.error(e.getMessage(),e);
        }
    }

    public Boolean executeScriptContext(String script, TaskContext taskContext) {
        try (IJavetEngine<V8Runtime> javetEngine = javetEnginePool.getEngine()) {
            V8Runtime v8Runtime = javetEngine.getV8Runtime();
            v8Runtime.setConverter(new JavetProxyConverter());
            JavetStandardConsoleInterceptor consoleInterceptor = new JavetStandardConsoleInterceptor(v8Runtime);
            consoleInterceptor.register(v8Runtime.getGlobalObject());

            ClassUtils classUtils = new ClassUtils(v8Runtime, taskContext);
            classUtils.setRedisService(rtRedisObjectStorageService);
            try (V8ValueObject v8ValueObject = v8Runtime.createV8ValueObject()) {
                v8Runtime.getGlobalObject().set("ClassUtils", v8ValueObject);
                v8ValueObject.bind(classUtils);
            }

            v8Runtime.getExecutor(script).executeVoid();

            consoleInterceptor.unregister(v8Runtime.getGlobalObject());
            v8Runtime.lowMemoryNotification();
            return true;
        } catch (Exception e) {
            log.error(e.getMessage(),e);
            return false;
        }
    }

    public Object executeScriptReturn(String script, TaskContext taskContext) {
        try (IJavetEngine<V8Runtime> javetEngine = javetEnginePool.getEngine()) {
            V8Runtime v8Runtime = javetEngine.getV8Runtime();
            v8Runtime.setConverter(new JavetProxyConverter());
            JavetStandardConsoleInterceptor consoleInterceptor = new JavetStandardConsoleInterceptor(v8Runtime);
            consoleInterceptor.register(v8Runtime.getGlobalObject());

//            ClassUtils classUtils = new ClassUtils(v8Runtime, taskContext);
//            classUtils.setRedisService(rtRedisObjectStorageService);
//            try (V8ValueObject v8ValueObject = v8Runtime.createV8ValueObject()) {
//                v8Runtime.getGlobalObject().set("ClassUtils", v8ValueObject);
//                v8ValueObject.bind(classUtils);
//            }

            Object o = v8Runtime.getExecutor(script).executeObject();

            consoleInterceptor.unregister(v8Runtime.getGlobalObject());
            v8Runtime.lowMemoryNotification();
            return o;
        } catch (Exception e) {
            log.error(e.getMessage(),e);
            return null;
        }
    }

    public void test2() {
        Object o = executeScriptReturn(" 1+2", new TaskContext());
        System.out.println("o = " + o);

    }





    public void test() {
        /**
         * public class TaskContext {
         *     private String ver;
         *     private PrjSpecDO prjSpec;
         *     private Map<String, MethodDefDO> methods=new HashMap<>();
         *     private Map<String,ClazzDefCompositeDO> clazzMap=new HashMap<>();
         *     private Map<String,ClazzDefCompositeDO> clazzGUIDMap=new HashMap<>();
         *     private ConcurrentHashMap<String, ClazzDefCompositeDO> rtti=new ConcurrentHashMap<>();
         * }
         */

        TaskContext taskContext = new TaskContext();
        HashMap<String, MethodDefDO> objectObjectHashMap = new HashMap<>();
        HashMap<String, ClazzDefCompositeDO> clazzMap = new HashMap<>();

        /**
         * @Data
         * @ApiModel(value = "DO", description = "",parent = ClazzDefDO.class)
         * public class ClazzDefCompositeDO extends ClazzDefDO {
         *   private Long originalId;
         *   private List<FieldDefCompositeDO> clazzIdFieldDefList;
         *   private List<MethodDefDO> clazzIdMethodDefList;
         * }
         */
        ClazzDefCompositeDO clazzDefCompositeDO = new ClazzDefCompositeDO();
        clazzDefCompositeDO.setName("ClassA");



        ArrayList<MethodDefCompositeDO> methods = new ArrayList<>();
        methods.add(new MethodDefCompositeDO());

        ArrayList<FieldDefCompositeDO> fileds = new ArrayList<>();
        FieldDefCompositeDO fieldDefCompositeDO = new FieldDefCompositeDO();
        fieldDefCompositeDO.setName("a1");
        fieldDefCompositeDO.setType("java.lang.String");
        fieldDefCompositeDO.setInitValue("stra1");
        fileds.add(fieldDefCompositeDO);

        clazzDefCompositeDO.setClazzIdFieldDefList(fileds);
        clazzDefCompositeDO.setClazzIdMethodDefList(methods);
        clazzMap.put("ClassA",clazzDefCompositeDO);

        taskContext.setClazzMap(clazzMap);

        SpringUtils.getBean(V8EngineService.class).executeScriptContext(
//                "console.log(1+1);" +
//                        " var a=ClassUtils.new('ClassA'); "+
//                        "console.log(a.name); "
                "var a =ClassUtils.new(\"ClassA\");\n" +
                        "var b  = ClassUtils.new(\"ClassA\")\n;" +
                        "a.f1 = b ;\n" +
                        "b.f2 = 3;\n" +
                        "console.log(a.f1.f2) "
                , taskContext);

    }

    @Override
    public Object exec(String script, TaskContext ctx) {
        return executeScriptContext(script, ctx);
    }
}
