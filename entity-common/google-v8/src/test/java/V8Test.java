import com.caoccao.javet.annotations.V8Function;
import com.caoccao.javet.interception.logging.JavetStandardConsoleInterceptor;
import com.caoccao.javet.interop.V8Runtime;
import com.caoccao.javet.interop.converters.JavetProxyConverter;
import com.caoccao.javet.interop.engine.IJavetEngine;
import com.caoccao.javet.interop.engine.IJavetEnginePool;
import com.caoccao.javet.interop.engine.JavetEnginePool;
import com.caoccao.javet.interop.proxy.JavetDirectProxyFunctionHandler;
import com.caoccao.javet.values.V8Value;
import com.caoccao.javet.values.reference.V8ValueFunction;
import com.caoccao.javet.values.reference.V8ValueObject;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Slf4j
@Service
public class V8Test {


    private static final IJavetEnginePool<V8Runtime> javetEnginePool = new JavetEnginePool<>();

    public static void test2() {
//        Object o = exec(" tst.m1('tst',1,2,'3'); ");
        Object o = exec(" var a = V8Test.new(); a.name = 123;  a.m2(1); ");
        System.out.println("o = " + o);

    }


    public static void main(String[] args) {
        test2();
    }

    @V8Function(name = "new")
    public V8Value newInstance( V8Value... args) throws Exception {
        V8Value v8Value = new JavetProxyConverter().toV8Value(getRuntime(), new TestProxyObject());
        if (v8Value instanceof V8ValueObject) {
            V8ValueObject value = (V8ValueObject) v8Value;
            V8ValueFunction m2Function = (V8ValueFunction) getRuntime()
                    .getExecutor("(function() { return function(x) { return this.name + x; }; })()")
                    .execute();

            value.set("m2", m2Function);

        }
        return v8Value;
    }


    public static Object exec(String script) {
        try {
            V8Runtime v8Runtime = getRuntime();
            V8Value tst = new JavetProxyConverter().toV8Value(v8Runtime, new TestProxyObject());
            v8Runtime.getGlobalObject().set("tst", tst);
            Object o = v8Runtime.getExecutor(script).executeObject();
            return o;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static V8Runtime getRuntime() {
        try (IJavetEngine<V8Runtime> javetEngine = javetEnginePool.getEngine()) {
            V8Runtime v8Runtime = javetEngine.getV8Runtime();
            JavetStandardConsoleInterceptor consoleInterceptor = new JavetStandardConsoleInterceptor(v8Runtime);
            consoleInterceptor.register(v8Runtime.getGlobalObject());

            try (V8ValueObject v8ValueObject = v8Runtime.createV8ValueObject()) {
                v8Runtime.getGlobalObject().set("V8Test", v8ValueObject);
                V8Test v8Test = new V8Test();
                v8ValueObject.bind(v8Test);
            }            return v8Runtime;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }


}
