package com.zjht.unified.service;

import com.caoccao.javet.interop.V8Runtime;
import com.caoccao.javet.values.V8Value;
import com.zjht.unified.domain.composite.MethodDefCompositeDO;
import com.zjht.unified.domain.runtime.UnifiedObject;
import com.zjht.unified.dto.MethodInvokeParam;
import com.zjht.unified.jsengine.v8.utils.V8BeanUtils;
import com.zjht.unified.service.ctx.RtRedisObjectStorageService;
import com.zjht.unified.service.ctx.TaskContext;
import com.zjht.unified.service.v8exec.V8EngineService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class ClsMethodService {

    @Autowired
    private RtRedisObjectStorageService objectStorageService;

    @Autowired
    private RtContextService rtContextService;

    @Autowired
    private IScriptEngine scriptEngine;

    public Object execMethod(MethodInvokeParam param) {
        TaskContext tcxt = rtContextService.getRunningContext(param.getVer());
        UnifiedObject me = objectStorageService.getObject(tcxt, param.getObjGuid(), param.getPrjGuid(), param.getPrjVer());
        if (me == null)
            return null;
        MethodDefCompositeDO mf = (MethodDefCompositeDO) objectStorageService.getAttrDef(tcxt, param.getPrjVer(), param.getClazzGuid(), param.getMethodName());
        Map<String,  Object> params=  new HashMap<>();
        for (int i = 0; i < mf.getMethodIdMethodParamList().size() && i < param.getParams().length; i++)
            try {
                params.put(mf.getMethodIdMethodParamList().get(i).getName(), param.getParams()[i]);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        params.put("me",me);
        return scriptEngine.exec(mf.getBody(),  params, tcxt, param.getPrjGuid(), param.getPrjVer());
    }
}
