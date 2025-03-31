package com.zjht.unified.service.v8exec;

import com.caoccao.javet.interception.logging.JavetStandardConsoleInterceptor;
import com.caoccao.javet.interop.V8Runtime;
import com.caoccao.javet.interop.converters.JavetProxyConverter;
import com.caoccao.javet.interop.engine.IJavetEngine;
import com.caoccao.javet.interop.engine.IJavetEnginePool;
import com.caoccao.javet.interop.engine.JavetEnginePool;
import com.caoccao.javet.values.reference.V8ValueObject;
import com.wukong.core.util.ThreadLocalUtil;
import com.zjht.unified.common.core.util.SpringUtils;
import com.zjht.unified.common.core.util.UUID;
import com.zjht.unified.domain.composite.ClazzDefCompositeDO;
import com.zjht.unified.domain.composite.FieldDefCompositeDO;
import com.zjht.unified.domain.composite.MethodDefCompositeDO;
import com.zjht.unified.domain.composite.PrjSpecDO;
import com.zjht.unified.domain.simple.MethodDefDO;
import com.zjht.unified.domain.simple.UiPrjDO;
import com.zjht.unified.service.IScriptEngine;
import com.zjht.unified.service.ctx.RtRedisObjectStorageService;
import com.zjht.unified.service.ctx.TaskContext;
import groovy.util.logging.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.stereotype.Service;


import java.util.ArrayList;
import java.util.HashMap;


@Slf4j
@Service
public class V8EngineService implements IScriptEngine {

    private static final Logger log = LoggerFactory.getLogger(V8EngineService.class);

    private static final IJavetEnginePool<V8Runtime> javetEnginePool = new JavetEnginePool<>();

    public void test2() {
        Object o = exec(" var a=1", new TaskContext());
        System.out.println("o = " + o);

    }

    public void testM(TaskContext taskContext) {
        exec(
//                "var a =ClassUtils.new(\"ClassA\");\n" +
//                        "var b  = ClassUtils.new(\"ClassA\")\n;" +
//                        "a.f1 = b ;\n" +
//                        "b.f2 = 3;\n" +
//                        "console.log(\"111111111111111111111111\");\n"+
//                        "console.log(\"\"+a.f1.f2);"

//                "var a = ClassUtils.newPersist(\"ClassA\");"+
//                "a.name = \"张三new\";"+
                "var a = ClassUtils.new(\"Device\",1,2);"+
                        "console.log(a.power_on());"
                , taskContext);
    }


    public void test() {

        TaskContext taskContext = new TaskContext();

        HashMap<String, MethodDefDO> objectObjectHashMap = new HashMap<>();
        HashMap<String, ClazzDefCompositeDO> clazzMap = new HashMap<>();
        HashMap<String, ClazzDefCompositeDO> clazzgGUIDMap = new HashMap<>();

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
        clazzDefCompositeDO.setTbl("test_tbl");
        String clsGuid = UUID.fastUUID().toString();
        clazzDefCompositeDO.setGuid(clsGuid);



        ArrayList<MethodDefCompositeDO> methods = new ArrayList<>();
        methods.add(new MethodDefCompositeDO());

        ArrayList<FieldDefCompositeDO> fileds = new ArrayList<>();
        FieldDefCompositeDO fieldDefCompositeDO = new FieldDefCompositeDO();
        fieldDefCompositeDO.setName("name");
        fieldDefCompositeDO.setTblCol("name_col");
        fieldDefCompositeDO.setDisplayName("姓名");
        fieldDefCompositeDO.setType("String");
        fieldDefCompositeDO.setInitValue("张三");
        fileds.add(fieldDefCompositeDO);


        FieldDefCompositeDO fieldDefCompositeDO2 = new FieldDefCompositeDO();
        fieldDefCompositeDO2.setName("age");
        fieldDefCompositeDO2.setTblCol("age_col");
        fieldDefCompositeDO2.setDisplayName("年龄");
        fieldDefCompositeDO2.setType("int");
        fieldDefCompositeDO2.setInitValue("18");
        fileds.add(fieldDefCompositeDO2);


        clazzDefCompositeDO.setClazzIdFieldDefList(fileds);
        clazzDefCompositeDO.setClazzIdMethodDefList(methods);
        clazzMap.put("ClassA",clazzDefCompositeDO);
        clazzgGUIDMap.put(clsGuid,clazzDefCompositeDO);

        taskContext.setClazzMap(clazzMap);
        taskContext.setClazzGUIDMap(clazzgGUIDMap);
//        taskContext.set

        UiPrjDO uiPrjDO = new UiPrjDO();

        uiPrjDO.setId(66L);

        PrjSpecDO prjSpecDO = new PrjSpecDO();
        prjSpecDO.setUiPrj(uiPrjDO);

        taskContext.setPrjId("1");
        taskContext.getPrjContextProvider().setPrjectContext(prjSpecDO);

        exec(
//                "var a =ClassUtils.new(\"ClassA\");\n" +
//                        "var b  = ClassUtils.new(\"ClassA\")\n;" +
//                        "a.f1 = b ;\n" +
//                        "b.f2 = 3;\n" +
//                        "console.log(\"111111111111111111111111\");\n"+
//                        "console.log(\"\"+a.f1.f2);"

//                "var a = ClassUtils.newPersist(\"ClassA\");"+
//                "a.name = \"张三new\";"+
                        "var a = ClassUtils.newPersist(\"ClassA\");"+
//                                "function method1() { console.log(this.name);}"+
                                "a.method1();"
                , taskContext);

    }



    @Override
    public Object exec(String script, TaskContext ctx) {
        try {
            V8Runtime v8Runtime = getRuntime(ctx);
            Object o = v8Runtime.getExecutor(script).executeObject();
            clearThreadRuntime();
            return o;
        }catch (Exception e){
            log.error(e.getMessage(),e);
        }
        return null;
    }

    public static V8Runtime getRuntime(TaskContext taskContext){
        V8Runtime rt = ThreadLocalUtil.get("V8Runtime");
        if(rt==null) {
            try (IJavetEngine<V8Runtime> javetEngine = javetEnginePool.getEngine()) {
                V8Runtime v8Runtime = javetEngine.getV8Runtime();
                JavetStandardConsoleInterceptor consoleInterceptor = new JavetStandardConsoleInterceptor(v8Runtime);
                consoleInterceptor.register(v8Runtime.getGlobalObject());
                registerUtils(v8Runtime,taskContext);
                ThreadLocalUtil.put("V8Runtime", v8Runtime);
                ThreadLocalUtil.put("console", consoleInterceptor);
                return v8Runtime;
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
            return null;
        }else{
            return rt;
        }
    }

    public static void clearThreadRuntime(){
        V8Runtime rt = ThreadLocalUtil.get("V8Runtime");
        if(rt==null)
            return;
        JavetStandardConsoleInterceptor console=ThreadLocalUtil.get("console");
        if(console==null)
            return;
        ThreadLocalUtil.clear();
        try{
            console.unregister(rt.getGlobalObject());
        }catch (Exception e){
            log.error(e.getMessage(),e);
        }
    }

    private static void registerUtils(V8Runtime v8Runtime,TaskContext taskContext) throws Exception{
        ClassUtils classUtils = new ClassUtils(taskContext);
        AutowireCapableBeanFactory autowireCapableBeanFactory=SpringUtils.getApplicationContext().getAutowireCapableBeanFactory();
        autowireCapableBeanFactory.autowireBean(classUtils);
        try (V8ValueObject v8ValueObject = v8Runtime.createV8ValueObject()) {
            v8Runtime.getGlobalObject().set("ClassUtils", v8ValueObject);
            v8ValueObject.bind(classUtils);
        }

        InstanceUtils instanceUtils=new InstanceUtils(taskContext);
        autowireCapableBeanFactory.autowireBean(instanceUtils);
        try (V8ValueObject v8ValueObject = v8Runtime.createV8ValueObject()) {
            v8Runtime.getGlobalObject().set("InstanceUtils", v8ValueObject);
            v8ValueObject.bind(instanceUtils);
        }
    }
}
