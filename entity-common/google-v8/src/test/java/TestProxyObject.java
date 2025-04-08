import com.caoccao.javet.annotations.V8Function;
import com.caoccao.javet.annotations.V8Property;
import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interfaces.IJavetBiFunction;
import com.caoccao.javet.interfaces.IJavetUniFunction;
import com.caoccao.javet.interop.V8Runtime;
import com.caoccao.javet.interop.binding.IClassProxyPlugin;
import com.caoccao.javet.interop.callback.IJavetDirectCallable;
import com.caoccao.javet.interop.callback.JavetCallbackContext;
import com.caoccao.javet.interop.callback.JavetCallbackType;
import com.caoccao.javet.interop.converters.JavetProxyConverter;
import com.caoccao.javet.interop.proxy.IJavetDirectProxyHandler;
import com.caoccao.javet.values.V8Value;
import com.caoccao.javet.values.primitive.V8ValueBoolean;
import com.caoccao.javet.values.reference.V8ValueArray;
import com.caoccao.javet.values.reference.V8ValueSymbol;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Slf4j
public class TestProxyObject implements IJavetDirectProxyHandler<Exception> {
    @Override
    public V8Runtime getV8Runtime() {
        return null;
    }

    @Override
    public V8Value proxyApply(V8Value target, V8Value thisObject, V8ValueArray arguments) throws JavetException, Exception {

        System.out.println("proxyApply:"+target+" "+thisObject+" "+arguments);
        return IJavetDirectProxyHandler.super.proxyApply(target, thisObject, arguments);
    }

    public V8Value exec(TestProxyObject that,String method,V8Value... arguments) throws JavetException, Exception {
        System.out.println("exec method:"+method);
        System.out.println("exec params:"+arguments[0]);
        return arguments[0].getV8Runtime().createV8ValueString("7788");
    }


    @Override
    public V8Value proxyGet(V8Value target, V8Value property, V8Value receiver) throws JavetException, Exception {
        System.out.println("proxyGet:" + property.toString());
        if(property.toString().equals("m1")){
            return property.getV8Runtime().createV8ValueFunction(new JavetCallbackContext(
                    "m1", this, JavetCallbackType.DirectCallNoThisAndResult,
                    (IJavetDirectCallable.NoThisAndResult<Exception>) (v8Values) -> exec(TestProxyObject.this,property.toString(),v8Values)));
        }
        return IJavetDirectProxyHandler.super.proxyGet(target, property, receiver);
    }

    @Override
    public V8Value symbolToPrimitive(V8Value... v8Values) throws JavetException, Exception {
        System.out.println("symbolToPrimitive:" + v8Values);
        return IJavetDirectProxyHandler.super.symbolToPrimitive(v8Values);
    }

    @Override
    public V8Value createTargetObject() {
        System.out.println("createTargetObject");
        return IJavetDirectProxyHandler.super.createTargetObject();
    }

    @Override
    public V8ValueBoolean proxyDeleteProperty(V8Value target, V8Value property) throws JavetException, Exception {
        System.out.println("proxyDeleteProperty");
        return IJavetDirectProxyHandler.super.proxyDeleteProperty(target, property);
    }

    @Override
    public V8Value proxyGetOwnPropertyDescriptor(V8Value target, V8Value property) throws JavetException, Exception {
        System.out.println("proxyGetOwnPropertyDescriptor");
        return IJavetDirectProxyHandler.super.proxyGetOwnPropertyDescriptor(target, property);
    }

    @Override
    public V8Value proxyGetPrototypeOf(V8Value target) throws JavetException, Exception {
        System.out.println("proxyGetPrototypeOf");
        return IJavetDirectProxyHandler.super.proxyGetPrototypeOf(target);
    }

    @Override
    public Map<String, IJavetUniFunction<String, ? extends V8Value, Exception>> proxyGetStringGetterMap() {
        System.out.println("proxyGetStringGetterMap");
        return IJavetDirectProxyHandler.super.proxyGetStringGetterMap();
    }

    @Override
    public Map<String, IJavetBiFunction<String, V8Value, Boolean, Exception>> proxyGetStringSetterMap() {
        System.out.println("proxyGetStringSetterMap");
        return IJavetDirectProxyHandler.super.proxyGetStringSetterMap();
    }

    @Override
    public Map<String, IJavetUniFunction<V8ValueSymbol, ? extends V8Value, Exception>> proxyGetSymbolGetterMap() {
        System.out.println("proxyGetSymbolGetterMap");
        return IJavetDirectProxyHandler.super.proxyGetSymbolGetterMap();
    }

    @Override
    public Map<String, IJavetBiFunction<V8ValueSymbol, V8Value, Boolean, Exception>> proxyGetSymbolSetterMap() {
        System.out.println("proxyGetSymbolSetterMap");
        return IJavetDirectProxyHandler.super.proxyGetSymbolSetterMap();
    }

    @Override
    public V8ValueBoolean proxyHas(V8Value target, V8Value property) throws JavetException, Exception {
        System.out.println("proxyHas");
        return IJavetDirectProxyHandler.super.proxyHas(target, property);
    }

    @Override
    public V8ValueArray proxyOwnKeys(V8Value target) throws JavetException, Exception {
        System.out.println("proxyOwnKeys");
        return IJavetDirectProxyHandler.super.proxyOwnKeys(target);
    }

    @Override
    public V8ValueBoolean proxySet(V8Value target, V8Value propertyKey, V8Value propertyValue, V8Value receiver) throws JavetException, Exception {
        System.out.println("proxySet");
        return IJavetDirectProxyHandler.super.proxySet(target, propertyKey, propertyValue, receiver);
    }

    @Override
    public void registerStringGetter(String propertyName, IJavetUniFunction<String, ? extends V8Value, Exception> getter) {
        System.out.println("registerStringGetter");
        IJavetDirectProxyHandler.super.registerStringGetter(propertyName, getter);
    }

    @Override
    public void registerStringGetterFunction(String propertyName, IJavetDirectCallable.NoThisAndResult<?> getter) {
        System.out.println("registerStringGetterFunction");
        IJavetDirectProxyHandler.super.registerStringGetterFunction(propertyName, getter);
    }

    @Override
    public void registerStringSetter(String propertyName, IJavetBiFunction<String, V8Value, Boolean, Exception> setter) {
        System.out.println("registerStringSetter");
        IJavetDirectProxyHandler.super.registerStringSetter(propertyName, setter);
    }

    @Override
    public void registerSymbolGetterFunction(String propertyName, IJavetDirectCallable.NoThisAndResult<?> getter) {
        System.out.println("registerSymbolGetterFunction");
        IJavetDirectProxyHandler.super.registerSymbolGetterFunction(propertyName, getter);
    }

}
