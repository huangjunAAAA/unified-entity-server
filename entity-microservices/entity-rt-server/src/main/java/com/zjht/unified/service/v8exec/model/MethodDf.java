package com.zjht.unified.service.v8exec.model;

import com.caoccao.javet.annotations.V8Function;
import com.caoccao.javet.interop.V8Runtime;
import com.caoccao.javet.values.V8Value;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.zjht.unified.domain.simple.MethodDefDO;
import com.zjht.unified.domain.simple.MethodParamDO;
import com.zjht.unified.jsengine.v8.utils.V8BeanUtils;
import com.zjht.unified.service.ctx.TaskContext;
import com.zjht.unified.service.v8exec.V8EngineService;
import lombok.extern.slf4j.Slf4j;

import java.util.Iterator;
import java.util.List;

@Slf4j
public class MethodDf extends MethodDefDO {
    private List<MethodParamDO> params;
    @V8Function(name = "getParam")
    public V8Value getParam(V8Value paramName){
        for (Iterator<MethodParamDO> iterator = params.iterator(); iterator.hasNext(); ) {
            MethodParamDO p =  iterator.next();
            if(p.getName().equals(paramName.toString())){
                try{
                    return V8BeanUtils.toV8Value(paramName.getV8Runtime(),p);
                }catch (Exception e){
                    log.error(e.getMessage(),e);
                    return paramName.getV8Runtime().createV8ValueUndefined();
                }
            }
        }
        return paramName.getV8Runtime().createV8ValueUndefined();

    }

}
