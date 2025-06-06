package com.zjht.unified.service.v8exec;

import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interception.logging.JavetStandardConsoleInterceptor;
import com.caoccao.javet.interop.V8Runtime;
import com.caoccao.javet.interop.converters.JavetProxyConverter;
import com.caoccao.javet.interop.engine.IJavetEngine;
import com.caoccao.javet.interop.engine.IJavetEnginePool;
import com.caoccao.javet.interop.engine.JavetEnginePool;
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

import java.util.Map;


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
        return null;
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

    private static void registerUtils(V8Runtime v8Runtime,TaskContext taskContext,String prjGuid,String prjVer) throws Exception{
        ClassUtils classUtils = new ClassUtils(taskContext,prjGuid,prjVer);
        AutowireCapableBeanFactory autowireCapableBeanFactory=SpringUtils.getApplicationContext().getAutowireCapableBeanFactory();
        autowireCapableBeanFactory.autowireBean(classUtils);
        try (V8ValueObject v8ValueObject = v8Runtime.createV8ValueObject()) {
            v8Runtime.getGlobalObject().set("ClassUtils", v8ValueObject);
            v8ValueObject.bind(classUtils);
        }

        InstanceUtils instanceUtils=new InstanceUtils(taskContext,prjGuid,prjVer);
        autowireCapableBeanFactory.autowireBean(instanceUtils);
        try (V8ValueObject v8ValueObject = v8Runtime.createV8ValueObject()) {
            v8Runtime.getGlobalObject().set("InstanceUtils", v8ValueObject);
            v8ValueObject.bind(instanceUtils);
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
