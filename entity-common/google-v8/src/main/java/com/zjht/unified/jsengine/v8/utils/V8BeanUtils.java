package com.zjht.unified.jsengine.v8.utils;

import com.caoccao.javet.annotations.V8Function;
import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interop.V8Runtime;
import com.caoccao.javet.interop.callback.JavetCallbackContext;
import com.caoccao.javet.values.V8Value;
import com.caoccao.javet.values.reference.V8ValueArray;
import com.caoccao.javet.values.reference.V8ValueObject;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;

@Slf4j
public class V8BeanUtils {
    // 类型转换注册器（支持扩展）
    private static final Map<Class<?>, BiFunction<Object, V8Runtime,V8Value>> TYPE_CONVERTERS =
            new ConcurrentHashMap<>();

    // 基础类型缓存
    private static final Set<Class<?>> PRIMITIVE_TYPES = new HashSet<>(Arrays.asList(String.class, Integer.class, Long.class,
            Boolean.class, Double.class, Float.class,
            Character.class, Byte.class, Short.class));
    ;

    static {
        // 注册默认类型转换器
        TYPE_CONVERTERS.put(Date.class, (obj,v8Runtime) -> {
            try {
                return v8Runtime.createV8ValueZonedDateTime(((Date)obj).getTime());
            } catch (JavetException e) {
                throw new RuntimeException("Date转换失败", e);
            }
        });
        TYPE_CONVERTERS.put(Calendar.class, (obj,v8Runtime) ->
                TYPE_CONVERTERS.get(Date.class).apply(((Calendar) obj).getTime(),v8Runtime));
    }

    /**
     * 将Java对象转换为V8对象
     */
    public static V8Value toV8Value(V8Runtime runtime, Object obj)
            throws JavetException {
        return convertValue(runtime, obj, new IdentityHashMap<>());
    }

    /**
     * 递归转换入口
     */
    private static V8Value convertValue(
            V8Runtime runtime,
            Object value,
            Map<Object, V8Value> visited) throws JavetException {

        if (value == null) return runtime.createV8ValueNull();

        // 循环引用检测
        if (visited.containsKey(value)) {
            return visited.get(value);
        }

        // 优先使用注册的类型转换器
        BiFunction<Object, V8Runtime,V8Value> converter = TYPE_CONVERTERS.get(value.getClass());
        if (converter != null) {
            return converter.apply(value,runtime);
        }

        // 分类型处理
        if (value instanceof Map) {
            return convertMap(runtime, (Map<?, ?>) value, visited);
        } else if (value instanceof Collection) {
            return convertCollection(runtime, (Collection<?>) value, visited);
        } else if (value.getClass().isArray()) {
            return convertArray(runtime, value, visited);
        } else if (isPrimitiveType(value.getClass())) {
            return runtime.toV8Value(value);
        } else {
            return convertPojo(runtime, value, visited);
        }
    }

    /**
     * 处理POJO对象（含循环引用检测）
     */
    private static V8ValueObject convertPojo(
            V8Runtime runtime,
            Object pojo,
            Map<Object, V8Value> visited) throws JavetException {

        V8ValueObject v8Obj = runtime.createV8ValueObject();
        visited.put(pojo, v8Obj);

        for (Field field : getAllFields(pojo.getClass())) {
            if (Modifier.isStatic(field.getModifiers())) continue;

            try {
                field.setAccessible(true);
                Object fieldValue = field.get(pojo);
                V8Value v8Value = convertValue(runtime, fieldValue, visited);
                v8Obj.set(field.getName(), v8Value);
            } catch (IllegalAccessException e) {
                log.error(e.getMessage(), e);
            }
        }

        for(Method method:getAllMethods(pojo.getClass())){
            V8Function fn = method.getAnnotation(V8Function.class);
            if(fn!=null){
                JavetCallbackContext javetCallbackContext = new JavetCallbackContext(fn.name(), pojo, method);
                v8Obj.bindFunction(javetCallbackContext);
            }
        }
        return v8Obj;
    }

    /**
     * 处理集合类型（List/Set）
     */
    private static V8ValueArray convertCollection(
            V8Runtime runtime,
            Collection<?> collection,
            Map<Object, V8Value> visited) throws JavetException {

        V8ValueArray v8Array = runtime.createV8ValueArray();
        visited.put(collection, v8Array);

        for (Object item : collection) {
            v8Array.push(convertValue(runtime, item, visited));
        }
        return v8Array;
    }

    /**
     * 处理多维数组
     */
    private static V8ValueArray convertArray(
            V8Runtime runtime,
            Object array,
            Map<Object, V8Value> visited) throws JavetException {

        V8ValueArray v8Array = runtime.createV8ValueArray();
        visited.put(array, v8Array);

        int length = Array.getLength(array);
        for (int i = 0; i < length; i++) {
            Object element = Array.get(array, i);
            v8Array.push(convertValue(runtime, element, visited));
        }
        return v8Array;
    }

    /**
     * 处理Map类型
     */
    private static V8ValueObject convertMap(
            V8Runtime runtime,
            Map<?, ?> map,
            Map<Object, V8Value> visited) throws JavetException {

        V8ValueObject v8Obj = runtime.createV8ValueObject();
        visited.put(map, v8Obj);

        for (Map.Entry<?, ?> entry : map.entrySet()) {
            String key = entry.getKey().toString();
            V8Value value = convertValue(runtime, entry.getValue(), visited);
            v8Obj.set(key, value);
        }
        return v8Obj;
    }

    /**
     * 注册自定义转换器
     */
    public static void registerConverter(
            Class<?> clazz,
            BiFunction<Object, V8Runtime,V8Value> converter) {

        TYPE_CONVERTERS.put(clazz, converter);
    }

    // 辅助方法：获取类及其父类的所有字段
    private static List<Field> getAllFields(Class<?> clazz) {
        List<Field> fields = new ArrayList<>();
        while (clazz != null) {
            Collections.addAll(fields, clazz.getDeclaredFields());
            clazz = clazz.getSuperclass();
        }
        return fields;
    }

    // 辅助方法：获取类及其父类的所有字段
    private static List<Method> getAllMethods(Class<?> clazz) {
        List<Method> methods = new ArrayList<>();
        while (clazz != null) {
            Collections.addAll(methods, clazz.getMethods());
            clazz = clazz.getSuperclass();
        }
        return methods;
    }

    // 判断基础类型
    private static boolean isPrimitiveType(Class<?> clazz) {
        return clazz.isPrimitive() || PRIMITIVE_TYPES.contains(clazz);
    }
}