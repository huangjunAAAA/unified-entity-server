package com.zjht.unified.jsengine.v8;

import com.caoccao.javet.annotations.V8Function;
import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interception.logging.JavetStandardConsoleInterceptor;
import com.caoccao.javet.interop.converters.JavetProxyConverter;
import com.caoccao.javet.interop.engine.IJavetEngine;
import com.caoccao.javet.interop.engine.IJavetEnginePool;
import com.caoccao.javet.interop.V8Runtime;
import com.caoccao.javet.interop.engine.JavetEnginePool;
import com.caoccao.javet.interop.proxy.IJavetDirectProxyHandler;
import com.caoccao.javet.values.V8Value;
import com.caoccao.javet.values.primitive.V8ValueBoolean;
import com.caoccao.javet.values.primitive.V8ValueDouble;
import com.caoccao.javet.values.primitive.V8ValueInteger;
import com.caoccao.javet.values.primitive.V8ValueString;
import com.caoccao.javet.values.reference.V8ValueObject;
import com.caoccao.javet.values.reference.V8ValueProxy;
import com.zjht.unified.domain.composite.ClazzDefCompositeDO;
import com.zjht.unified.domain.composite.FieldDefCompositeDO;
import com.zjht.unified.domain.simple.MethodDefDO;
import com.zjht.unified.service.ctx.TaskContext;
import javassist.CtField;
import javassist.Modifier;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class V8EngineService {

    private static final IJavetEnginePool<V8Runtime> javetEnginePool = new JavetEnginePool<>();


    public static void release() {
        try {
            javetEnginePool.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void executeScript(String script) {
        try (IJavetEngine<V8Runtime> javetEngine = javetEnginePool.getEngine()) {
            V8Runtime v8Runtime = javetEngine.getV8Runtime();
            JavetStandardConsoleInterceptor consoleInterceptor = new JavetStandardConsoleInterceptor(v8Runtime);
            consoleInterceptor.register(v8Runtime.getGlobalObject());


            v8Runtime.getExecutor(script).executeVoid();
            consoleInterceptor.unregister(v8Runtime.getGlobalObject());
            v8Runtime.lowMemoryNotification();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void executeScriptContext(String script,TaskContext taskContext) {
        try (IJavetEngine<V8Runtime> javetEngine = javetEnginePool.getEngine()) {
            V8Runtime v8Runtime = javetEngine.getV8Runtime();
            v8Runtime.setConverter(new JavetProxyConverter());


            JavetStandardConsoleInterceptor consoleInterceptor = new JavetStandardConsoleInterceptor(v8Runtime);
            consoleInterceptor.register(v8Runtime.getGlobalObject());

            ClassUtils ClassUtils = new ClassUtils(v8Runtime, taskContext);
            try (V8ValueObject v8ValueObject = v8Runtime.createV8ValueObject()) {
                v8Runtime.getGlobalObject().set("ClassUtils", v8ValueObject);
                v8ValueObject.bind(ClassUtils);
            }

            v8Runtime.getExecutor(script).executeVoid();
            consoleInterceptor.unregister(v8Runtime.getGlobalObject());
            v8Runtime.lowMemoryNotification();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


//    static class ClassUtils {
//        @V8Function(name = "new")
//        public static String newInstance(String className,TaskContext context) {
//            return " new "+className+" Object ";
//        }
//    }

    public  static class ClassUtils {

        private V8Runtime v8Runtime;
        private TaskContext taskContext;



        public  ClassUtils(V8Runtime v8Runtime, TaskContext taskContext) {
            this.v8Runtime = v8Runtime;
            this.taskContext = taskContext;
        }

        @V8Function(name = "new")
        public V8Value newInstance(String className) throws Exception {
//            v8Runtime.createV8ValueB()
            System.out.println("ClassUtils.new called");
            ClazzDefCompositeDO classDef = taskContext.getClazzMap().get(className);
            if (classDef == null) {
                throw new RuntimeException("Class not found: " + className);
            }
//            ProxyObject proxyObject = new ProxyObject(v8Runtime);
//            proxyObject.setName("po2");


//            for (FieldDefCompositeDO field : classDef.getClazzIdFieldDefList()) {
//                CtField ctField = new CtField(pool.get(field.getType()), field.getName(), ctClass);
//                ctField.setModifiers(Modifier.PUBLIC);
//                ctClass.addField(ctField);
//            }

//            Class<?> clazz = DynamicClassGenerator.generateClass(classDef);
//            Object instance = clazz.getDeclaredConstructor().newInstance(v8Runtime);
//            Object instance = clazz.getDeclaredConstructor().newInstance();


//            clazz.getDeclaredField("v8Runtime").set(instance, v8Runtime);

            ProxyObject proxyObject = new ProxyObject(v8Runtime);
            proxyObject.setName("name");
//            v8Runtime.createV8ValueProxy(proxyObject);

            // 创建 V8ValueObject
            V8ValueObject v8Object = v8Runtime.createV8ValueObject();

// 获取 clazzMap 中的 ClazzDefCompositeDO

            // 绑定 FieldDefCompositeDO 中的属性名
//            for (FieldDefCompositeDO field : classDef.getClazzIdFieldDefList()) {
//            }
            // 绑定代理对象到 V8 对象
            v8Object.bind(proxyObject);
            return v8Object;

        }
    }


    static class ProxyObject2 {
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    static class ProxyObject implements IJavetDirectProxyHandler<Exception> {
        private final V8Runtime v8Runtime;
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
        private Map<String, Object> properties;


        private String a1;

        public ProxyObject(V8Runtime v8Runtime) {
            this.v8Runtime = v8Runtime;
            this.properties = new HashMap<>();
        }

        @Override
        public V8Value proxyGet(V8Value target, V8Value property, V8Value receiver) throws JavetException {
            System.out.println("proxyGet");
            if (property instanceof V8ValueString) {
                String key = ((V8ValueString) property).getValue();
                Object value = properties.get(key);
                if (value == null) {
                    return v8Runtime.createV8ValueString("hello");
                }
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
            return v8Runtime.createV8ValueUndefined();

        }

        @Override
        public V8ValueBoolean proxySet(V8Value target, V8Value property, V8Value value, V8Value receiver) throws JavetException {

            System.out.println("proxySet");

            if (property instanceof V8ValueString) {
                String key = ((V8ValueString) property).getValue();
                System.out.println("key = " + key);
                // 根据 V8Value 类型转换为 Java 类型
                if (value instanceof V8ValueString) {
                    properties.put(key, ((V8ValueString) value).getValue());
                } else if (value instanceof V8ValueInteger) {
                    properties.put(key, ((V8ValueInteger) value).getValue());
                } else if (value instanceof V8ValueBoolean) {
                    properties.put(key, ((V8ValueBoolean) value).getValue());
                } else if (value instanceof V8ValueDouble) {
                    properties.put(key, ((V8ValueDouble) value).getValue());
                } else {
                    properties.put(key, null);
                }
                return v8Runtime.createV8ValueBoolean(true);
            }
            return v8Runtime.createV8ValueBoolean(false);
        }

        @Override
        public V8Runtime getV8Runtime() {
            System.out.println("getV8Runtime");
            return v8Runtime;
        }

        @Override
        public V8ValueBoolean proxyDeleteProperty(V8Value target, V8Value property) throws JavetException {
            if (property instanceof V8ValueString) {
                String key = ((V8ValueString) property).getValue();
                return v8Runtime.createV8ValueBoolean(properties.remove(key) != null);
            }
            return v8Runtime.createV8ValueBoolean(false);
        }




    }




    public static void main(String[] args) {
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



        ArrayList<MethodDefDO> methods = new ArrayList<>();
        methods.add(new MethodDefDO());

        ArrayList<FieldDefCompositeDO> fileds = new ArrayList<>();
        FieldDefCompositeDO fieldDefCompositeDO = new FieldDefCompositeDO();
        fieldDefCompositeDO.setName("a1");
        fieldDefCompositeDO.setType("java.lang.String");
        fileds.add(fieldDefCompositeDO);

        clazzDefCompositeDO.setClazzIdFieldDefList(fileds);
        clazzDefCompositeDO.setClazzIdMethodDefList(methods);
        clazzMap.put("ClassA",clazzDefCompositeDO);

        taskContext.setClazzMap(clazzMap);

        executeScriptContext(
                "console.log(1+1);" +
                " var a=ClassUtils.new('ClassA'); " +
                        "console.log(a.name)"
                ,taskContext);

//        executeScriptContext("console.log(1+1); var a=ClassUtils.new('ClassA');console.log(a); var b = 234; console.log(b)",taskContext);




    }

}
