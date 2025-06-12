package com.zjht.unified.service;

import com.zjht.unified.common.core.constants.FieldConstants;
import com.zjht.unified.common.core.util.SpringUtils;
import com.zjht.unified.domain.composite.ClazzDefCompositeDO;
import com.zjht.unified.domain.composite.FieldDefCompositeDO;
import com.zjht.unified.domain.composite.MethodDefCompositeDO;
import com.zjht.unified.domain.runtime.UnifiedObject;
import com.zjht.unified.dto.CreateObjectParam;
import com.zjht.unified.dto.GetParam;
import com.zjht.unified.dto.MethodInvokeParam;
import com.zjht.unified.service.ctx.EntityDepService;
import com.zjht.unified.service.ctx.RtRedisObjectStorageService;
import com.zjht.unified.service.ctx.TaskContext;
import com.zjht.unified.service.v8exec.ClassUtils;
import com.zjht.unified.service.v8exec.ProxyObject;
import com.zjht.unified.service.v8exec.V8RttiService;
import com.zjht.unified.service.v8exec.model.ClsDf;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class FrontObjectService {

    @Autowired
    private RtRedisObjectStorageService objectStorageService;

    @Autowired
    private RtContextService rtContextService;

    @Autowired
    private IScriptEngine scriptEngine;

    @Autowired
    private EntityDepService entityDepService;

    @Autowired
    private V8RttiService v8RttiService;

    public Object execMethod(MethodInvokeParam param) {
        TaskContext tcxt = rtContextService.getRunningContext(param.getVer());
        UnifiedObject me = objectStorageService.getObject(tcxt, param.getObjGuid(), param.getPrjGuid(), param.getPrjVer());
        if (me == null)
            return null;
        MethodDefCompositeDO mf = (MethodDefCompositeDO) objectStorageService.getAttrDef(tcxt, param.getPrjVer(), param.getClazzGuid(), param.getMethodName());
        Map<String, Object> params = new HashMap<>();
        for (int i = 0; i < mf.getMethodIdMethodParamList().size() && i < param.getParams().length; i++)
            try {
                params.put(mf.getMethodIdMethodParamList().get(i).getName(), param.getParams()[i]);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        params.put("me", me);
        return scriptEngine.exec(mf.getBody(), params, tcxt, param.getPrjGuid(), param.getPrjVer());
    }

    public Map<String, Object> getObject(GetParam param) {
        TaskContext taskContext = rtContextService.getRunningContext(param.getVer());
        UnifiedObject obj=null;
        if(param.getPrjGuid()!=null){
            obj = objectStorageService.getObject(taskContext, param.getObjGuid(), param.getPrjGuid(), param.getPrjVer());
        }else{
            obj = entityDepService.getObject(taskContext,param.getObjGuid());
        }
        Map<String, Object> pureObj = getObject(taskContext, obj);
        return pureObj;
    }

    public Map<String, Object> createObject(CreateObjectParam param) {
        TaskContext taskContext = rtContextService.getRunningContext(param.getVer());
        ClazzDefCompositeDO classDef=null;
        if(param.getPrjVer()!=null&&param.getClsGuid()!=null){
            classDef = objectStorageService.getClsDef(taskContext, param.getPrjVer(), param.getClsGuid());
        }else{
            if(param.getClsGuid()!=null)
                classDef = entityDepService.getClsDefByGuid(taskContext,param.getClsGuid());
            else if (param.getClsName() != null)
                classDef = entityDepService.getClsByName(taskContext, param.getClsName());
        }
        if(classDef==null)
            return null;


        ProxyObject proxyObject = v8RttiService.createNewObject(classDef, taskContext, param.isPersist());
        ClassUtils.parseConstructMethod(proxyObject.getV8Runtime(), param.getArgs(), classDef, proxyObject);
        UnifiedObject unified = new UnifiedObject(proxyObject.getGuid(), classDef.getGuid(), param.isPersist(), classDef.getPrjGuid(), classDef.getPrjVer(), taskContext.getVer());
        return getObject(taskContext, unified);
    }

    public Object getObjectValue(GetParam param) {
        TaskContext taskContext = rtContextService.getRunningContext(param.getVer());
        UnifiedObject obj=null;
        if(param.getPrjGuid()!=null){
            obj = objectStorageService.getObject(taskContext, param.getObjGuid(), param.getPrjGuid(), param.getPrjVer());
        }else{
            obj = entityDepService.getObject(taskContext,param.getObjGuid());
        }
        RtRedisObjectStorageService rtRedisObjectStorageService = SpringUtils.getBean(RtRedisObjectStorageService.class);
        ClazzDefCompositeDO clazzDef = rtRedisObjectStorageService.getClsDef(taskContext, param.getPrjVer(), obj.getClazzGUID());
        String field = clazzDef.getPvAttr();
        Object val = objectStorageService.getObjectAttrValue(taskContext, obj.getGuid(), field, obj.getPrjGuid(), obj.getPrjVer());
        if (val != null && val instanceof UnifiedObject) {
            Map<String, Object> realVal = getObject(taskContext, (UnifiedObject) val);
            return realVal;
        }
        return val;
    }

    private Map<String, Object> getObject(TaskContext taskContext, UnifiedObject obj) {
        Map<String, Object> ret = new HashMap<>();
        ClazzDefCompositeDO cls = entityDepService.getClsDefByGuid(taskContext, obj.getClazzGUID());
        for (FieldDefCompositeDO field : cls.getClazzIdFieldDefList()) {
            Object val = objectStorageService.getObjectAttrValue(taskContext, obj.getGuid(), field.getName(), obj.getPrjGuid(), obj.getPrjVer());
            if (val != null) {
                if (val instanceof UnifiedObject) {
                    Map<String, Object> realVal = getObject(taskContext, (UnifiedObject) val);
                    ret.put(field.getName(), realVal);
                } else {
                    ret.put(field.getName(), val);
                }
            }
        }
        ret.put(FieldConstants.GUID, obj.getGuid());
        ret.put(FieldConstants.CLAZZ_GUID, obj.getClazzGUID());
        ret.put(FieldConstants.PROJECT_GUID, obj.getPrjGuid());
        ret.put(FieldConstants.PROJECT_VER, obj.getPrjVer());
        ret.put(FieldConstants.CLASS, ClsDf.from(cls, taskContext));
        return ret;
    }

}
