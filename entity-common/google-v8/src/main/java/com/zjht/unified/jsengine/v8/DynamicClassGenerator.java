package com.zjht.unified.jsengine.v8;

import com.caoccao.javet.interop.V8Runtime;
import com.caoccao.javet.interop.proxy.IJavetDirectProxyHandler;
import com.caoccao.javet.values.V8Value;
import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.values.primitive.V8ValueBoolean;
import com.caoccao.javet.values.primitive.V8ValueInteger;
import com.caoccao.javet.values.primitive.V8ValueString;
import com.zjht.unified.domain.composite.ClazzDefCompositeDO;
import com.zjht.unified.domain.composite.FieldDefCompositeDO;
import com.zjht.unified.domain.simple.MethodDefDO;
import javassist.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.lang.reflect.Field;

public class DynamicClassGenerator {
    public static Class<?> generateClass(ClazzDefCompositeDO classDef) throws Exception {
        ClassPool pool = ClassPool.getDefault();
        CtClass ctClass = pool.makeClass(classDef.getName());



        ctClass.addInterface(pool.get(IJavetDirectProxyHandler.class.getName()));

        // 添加 V8Runtime 成员变量
        CtField v8RuntimeField = new CtField(pool.get(V8Runtime.class.getName()), "v8Runtime", ctClass);
        v8RuntimeField.setModifiers(Modifier.PRIVATE);
        ctClass.addField(v8RuntimeField);

        // 构造函数
//        CtConstructor constructor = new CtConstructor(new CtClass[]{pool.get(V8Runtime.class.getName())}, ctClass);
//        constructor.setBody("{ this.v8Runtime = $1; }");
//        ctClass.addConstructor(constructor);

        // **1. 添加动态属性**
        for (FieldDefCompositeDO field : classDef.getClazzIdFieldDefList()) {
            CtField ctField = new CtField(pool.get(field.getType()), field.getName(), ctClass);
            ctField.setModifiers(Modifier.PUBLIC);
            ctClass.addField(ctField);
        }

        // 修正 proxyGet 方法
        CtMethod proxyGetMethod = new CtMethod(
                pool.get(V8Value.class.getName()), // 返回类型改为 V8Value
                "proxyGet",
                new CtClass[]{
                        pool.get(V8Value.class.getName()), // target
                        pool.get(V8Value.class.getName()), // property
                        pool.get(V8Value.class.getName())  // receiver
                },
                ctClass
        );
        proxyGetMethod.setModifiers(Modifier.PUBLIC);
        proxyGetMethod.setBody(
                "{" +
                        "    if ($2 instanceof " + V8ValueString.class.getName() + ") {" +
                        "        String key = ((" + V8ValueString.class.getName() + ") $2).getValue();" +
                        "        try {" +
                        "            java.lang.reflect.Field field = this.getClass().getDeclaredField(key);" +
                        "            field.setAccessible(true);" +
                        "            Object value = field.get(this);" +
                        "            if (value instanceof String) {" +
                        "                return v8Runtime.createV8ValueString((String) value);" +
                        "            } else if (value instanceof Integer) {" +
                        "                return v8Runtime.createV8ValueInteger((int) value);" +
                        "            } else if (value instanceof Boolean) {" +
                        "                return v8Runtime.createV8ValueBoolean((boolean) value);" +
                        "            } else {" +
                        "                return v8Runtime.createV8ValueNull();" +
                        "            }" +
                        "        } catch (java.lang.NoSuchFieldException e) {" +
                        "            return v8Runtime.createV8ValueUndefined();" +
                        "        } catch (java.lang.IllegalAccessException e) {" +
                        "            return v8Runtime.createV8ValueUndefined();" +
                        "        }" +
                        "    }" +
                        "    return v8Runtime.createV8ValueUndefined();" +
                        "}"
        );
        ctClass.addMethod(proxyGetMethod);

        // 修正 proxySet 方法（示例，需根据接口调整参数和返回类型）
        CtMethod proxySetMethod = new CtMethod(
                pool.get(V8ValueBoolean.class.getName()), // 假设接口返回 V8ValueBoolean
                "proxySet",
                new CtClass[]{
                        pool.get(V8Value.class.getName()), // target
                        pool.get(V8Value.class.getName()), // property
                        pool.get(V8Value.class.getName()), // value
                        pool.get(V8Value.class.getName())  // receiver
                },
                ctClass
        );
        proxySetMethod.setBody(
                "{" +
                        "    if ($2 instanceof " + V8ValueString.class.getName() + ") {" +
                        "        String key = ((" + V8ValueString.class.getName() + ") $2).getValue();" +
                        "        try {" +
                        "            java.lang.reflect.Field field = this.getClass().getDeclaredField(key);" +
                        "            field.setAccessible(true);" +
                        "            if ($3 instanceof " + V8ValueString.class.getName() + ") {" +
                        "                field.set(this, ((" + V8ValueString.class.getName() + ") $3).getValue());" +
                        "                return v8Runtime.createV8ValueBoolean(true);" +
                        "            } else if ($3 instanceof " + V8ValueInteger.class.getName() + ") {" +
                        "                field.set(this, ((" + V8ValueInteger.class.getName() + ") $3).getValue());" +
                        "                return v8Runtime.createV8ValueBoolean(true);" +
                        "            } else if ($3 instanceof " + V8ValueBoolean.class.getName() + ") {" +
                        "                field.set(this, ((" + V8ValueBoolean.class.getName() + ") $3).getValue());" +
                        "                return v8Runtime.createV8ValueBoolean(true);" +
                        "            }" +
                        "        } catch (Exception e) {" +
                        "            return v8Runtime.createV8ValueBoolean(false);" +
                        "        }" +
                        "    }" +
                        "    return v8Runtime.createV8ValueBoolean(false);" +
                        "}"
        );
        ctClass.addMethod(proxySetMethod);

        return ctClass.toClass();
    }
}
