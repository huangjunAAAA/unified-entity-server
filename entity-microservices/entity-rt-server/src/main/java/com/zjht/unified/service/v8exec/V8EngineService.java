package com.zjht.unified.service.v8exec;

import com.caoccao.javet.annotations.V8Function;
import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interception.logging.JavetStandardConsoleInterceptor;
import com.caoccao.javet.interop.V8Runtime;
import com.caoccao.javet.interop.converters.JavetProxyConverter;
import com.caoccao.javet.interop.engine.IJavetEngine;
import com.caoccao.javet.interop.engine.IJavetEnginePool;
import com.caoccao.javet.interop.engine.JavetEnginePool;
import com.caoccao.javet.values.reference.V8ValueFunction;
import com.caoccao.javet.values.reference.V8ValueObject;
import com.wukong.core.util.ThreadLocalUtil;
import com.zjht.unified.common.core.util.SpringUtils;
import com.zjht.unified.domain.runtime.UnifiedObject;
import com.zjht.unified.jsengine.v8.utils.V8BeanUtils;
import com.zjht.unified.service.IScriptEngine;
import com.zjht.unified.service.ctx.TaskContext;
import groovy.util.logging.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.stereotype.Service;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.function.Supplier;


@Slf4j
@Service
public class V8EngineService implements IScriptEngine {

    private static final Logger log = LoggerFactory.getLogger(V8EngineService.class);

    private static final IJavetEnginePool<V8Runtime> javetEnginePool = new JavetEnginePool<>();

    @Override
    public Object exec(String script, Map<String, Object> params, TaskContext ctx, String prjGuid, String prjVer) {
        V8Runtime v8Runtime = getRuntime(ctx, prjGuid, prjVer);
        try {
            unSetMe(v8Runtime);
            if(params!=null){
                for (Map.Entry<String, Object> entry : params.entrySet()) {
                    v8Runtime.getGlobalObject().set(entry.getKey(), V8BeanUtils.toV8Value(v8Runtime,entry.getValue()));
                }
            }
            Object o = v8Runtime.getExecutor(script).executeObject();
            return o;
        }catch (Exception e){
            log.error(e.getMessage(),e);
            return e.getMessage();
        }finally {
            try {
                if(params!=null){
                    for (Map.Entry<String, Object> entry : params.entrySet()) {
                        v8Runtime.getGlobalObject().delete(entry.getKey());
                    }
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
    }

    public Object exec(String script) {
        try {
            V8Runtime v8Runtime = getRuntime();
            Object o = v8Runtime.getExecutor(script).executeObject();
            clearThreadRuntime();
            return o;
        }catch (Exception e){
            log.error(e.getMessage(),e);
        }
        return null;
    }

    public static V8Runtime getRuntime(TaskContext taskContext, String prjGuid, String prjVer){
        V8Runtime rt = ThreadLocalUtil.get("V8Runtime");
        if(rt==null) {
            try (IJavetEngine<V8Runtime> javetEngine = javetEnginePool.getEngine()) {
                V8Runtime v8Runtime = javetEngine.getV8Runtime();
                JavetStandardConsoleInterceptor consoleInterceptor = new JavetStandardConsoleInterceptor(v8Runtime);
                consoleInterceptor.register(v8Runtime.getGlobalObject());
                registerUtils(v8Runtime,taskContext,prjGuid,prjVer);
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

    public static V8Runtime getRuntime(){
        V8Runtime rt = ThreadLocalUtil.get("V8Runtime");
        if(rt==null) {
            try (IJavetEngine<V8Runtime> javetEngine = javetEnginePool.getEngine()) {
                V8Runtime v8Runtime = javetEngine.getV8Runtime();
                JavetStandardConsoleInterceptor consoleInterceptor = new JavetStandardConsoleInterceptor(v8Runtime);
                consoleInterceptor.register(v8Runtime.getGlobalObject());
//                registerUtils(v8Runtime,taskContext);
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

    /**
     * 注册所有需要暴露给 V8 JS 环境的工具类实例
     */
    private static void registerUtils(V8Runtime v8Runtime, TaskContext ctx, String prjGuid, String prjVer) {
        AutowireCapableBeanFactory factory = SpringUtils.getApplicationContext().getAutowireCapableBeanFactory();
        register(v8Runtime, factory, () -> new ClassUtils(ctx, prjGuid, prjVer));
        register(v8Runtime, factory, () -> new InstanceUtils(ctx, prjGuid, prjVer));
        register(v8Runtime, factory, () -> new LockUtils(ctx, prjGuid, prjVer));
        register(v8Runtime, factory, () -> new MemUtils(ctx, prjGuid, prjVer));
        register(v8Runtime, factory, () -> new RecordUtils(ctx, prjGuid, prjVer));
    }

    /**
     * 注册单个工具类到 V8 JS 环境
     * @param v8Runtime JS 引擎运行时
     * @param factory Spring 自动注入工厂
     * @param supplier 工具类实例的构造函数
     * @param <T> 工具类的类型
     */
    private static <T> void register(V8Runtime v8Runtime, AutowireCapableBeanFactory factory, Supplier<T> supplier) {
        T instance = supplier.get();
        factory.autowireBean(instance);
        String bindName = instance.getClass().getSimpleName();
        bind(v8Runtime, instance, bindName);
    }


    /**
     * 将Util类对象绑定到 V8 全局作用域，并暴露所有 @V8Function 方法
     *
     * @param v8Runtime V8 引擎上下文
     * @param target Java 对象
     * @param globalAlias 全局变量名
     */
    public static void bind(V8Runtime v8Runtime, Object target, String globalAlias) {
        try (V8ValueObject v8ValueObject = v8Runtime.createV8ValueObject()) {
            // 绑定对象
            v8Runtime.getGlobalObject().set(globalAlias, v8ValueObject);
            v8ValueObject.bind(target);

            // 扫描 @V8Function 方法，绑定到 global
            for (Method method : target.getClass().getDeclaredMethods()) {
                V8Function annotation = method.getAnnotation(V8Function.class);
                if (annotation != null) {
                    String jsFnName = annotation.name();
                    V8ValueFunction jsFn = v8ValueObject.get(jsFnName);
                    if (jsFn != null) {
                        v8Runtime.getGlobalObject().set(jsFnName, jsFn);
                        log.info("绑定 JS 全局函数 [{}] -> {}.{}", jsFnName, target.getClass().getSimpleName(), method.getName());
                    }
                }
            }

        } catch (JavetException e) {
            log.error("绑定 {} 到 V8 全局作用域失败：{}", target.getClass().getSimpleName(), e.getMessage(), e);
            throw new RuntimeException("V8 global method binding failed", e);
        }
    }

    public static void setMe(V8Runtime v8Runtime, ProxyObject me){
        try {
            v8Runtime.getGlobalObject().set("me", new JavetProxyConverter().toV8Value(v8Runtime, me));
        } catch (Exception e) {
            log.error(e.getMessage(),e);
        }
    }

    public static void unSetMe(V8Runtime v8Runtime){
        try {
            v8Runtime.getGlobalObject().delete("me");
        } catch (JavetException e) {
            log.error(e.getMessage(),e);
        }
    }
}
