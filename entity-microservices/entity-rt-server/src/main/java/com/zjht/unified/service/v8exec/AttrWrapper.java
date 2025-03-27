package com.zjht.unified.service.v8exec;

import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interop.V8Runtime;
import com.caoccao.javet.interop.proxy.IJavetDirectProxyHandler;
import com.caoccao.javet.values.V8Value;
import com.caoccao.javet.values.primitive.V8ValueBoolean;
import com.zjht.unified.service.ctx.TaskContext;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AttrWrapper  implements IJavetDirectProxyHandler<Exception>  {
    private Object lastValue;
    private Object lastEV;
    private int archiveStatus;
    private String eval;
    private TaskContext taskContext;


    @Override
    public V8Value symbolToPrimitive(V8Value... v8Values) throws JavetException, Exception {
        System.out.println("AttrWrapper symbolToPrimitive = " + v8Values);
//        return IJavetDirectProxyHandler.super.symbolToPrimitive(v8Values);
        return convertToV8Value(lastValue);

    }

    private V8Value convertToV8Value(Object value) throws JavetException {
        if (value == null)
            return getV8Runtime().createV8ValueNull();

        if (value instanceof Integer) {
            return getV8Runtime().createV8ValueInteger((Integer) value);
        } else if (value instanceof Long) {
            return getV8Runtime().createV8ValueLong((Long) value);
        } else if (value instanceof Double) {
            return getV8Runtime().createV8ValueDouble((Double) value);
        } else if (value instanceof Boolean) {
            return getV8Runtime().createV8ValueBoolean((Boolean) value);
        }
        return getV8Runtime().createV8ValueString(value.toString());
    }

    @Override
    public V8Value proxyGet(V8Value target, V8Value property, V8Value receiver) throws JavetException, Exception {
        return IJavetDirectProxyHandler.super.proxyGet(target, property, receiver);
    }

    @Override
    public V8ValueBoolean proxySet(V8Value target, V8Value propertyKey, V8Value propertyValue, V8Value receiver) throws JavetException, Exception {
        return IJavetDirectProxyHandler.super.proxySet(target, propertyKey, propertyValue, receiver);
    }

    @Override
    public V8Runtime getV8Runtime() {
        return V8EngineService.getRuntime(taskContext);
    }

    @Override
    public String toString() {
        return String.valueOf(lastValue);
    }
}
