package com.zjht.unified.service;

import com.caoccao.javet.interop.converters.JavetProxyConverter;
import com.caoccao.javet.values.V8Value;
import com.zjht.unified.common.core.constants.CoreClazzDef;
import com.zjht.unified.common.core.constants.FieldConstants;
import com.zjht.unified.common.core.domain.R;
import com.zjht.unified.common.core.domain.dto.BaseQueryDTO;
import com.zjht.unified.common.core.domain.dto.QueryClass;
import com.zjht.unified.common.core.util.SpringUtils;
import com.zjht.unified.domain.composite.ClazzDefCompositeDO;
import com.zjht.unified.domain.composite.FieldDefCompositeDO;
import com.zjht.unified.domain.composite.MethodDefCompositeDO;
import com.zjht.unified.domain.runtime.UnifiedObject;
import com.zjht.unified.domain.simple.TNodeDO;
import com.zjht.unified.dto.CreateObjectParam;
import com.zjht.unified.common.core.domain.dto.GetParam;
import com.zjht.unified.dto.MethodInvokeParam;
import com.zjht.unified.common.core.domain.dto.SetParam;
import com.zjht.unified.dto.QueryAllObjectDTO;
import com.zjht.unified.dto.QueryObjectDTO;
import com.zjht.unified.feign.RemoteStore;
import com.zjht.unified.service.ctx.EntityDepService;
import com.zjht.unified.service.ctx.RtRedisObjectStorageService;
import com.zjht.unified.service.ctx.TaskContext;
import com.zjht.unified.service.v8exec.ClassUtils;
import com.zjht.unified.service.v8exec.ProxyObject;
import com.zjht.unified.service.v8exec.V8RttiService;
import com.zjht.unified.service.v8exec.model.ClsDf;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

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

    @Autowired
    private RemoteStore remoteStore;

    public Object execMethod(MethodInvokeParam param) {
        TaskContext tcxt = rtContextService.getRunningContext(param.getVer());
        UnifiedObject me = objectStorageService.getObject(tcxt, param.getObjGuid(), param.getPrjGuid(), param.getPrjVer());
        if (me == null)
            return "error:object not found:"+param.getObjGuid();
        MethodDefCompositeDO mf = (MethodDefCompositeDO) objectStorageService.getAttrDef(tcxt, param.getPrjVer(), param.getClazzGuid(), param.getMethodName());
        if (mf == null)
            return "error:method not found:"+param.getMethodName();
        Map<String, Object> params = new HashMap<>();
        if(mf.getMethodIdMethodParamList()!=null&&param.getParams()!=null) {
            for (int i = 0; i < mf.getMethodIdMethodParamList().size() && i < param.getParams().length; i++)
                try {
                    params.put(mf.getMethodIdMethodParamList().get(i).getName(), param.getParams()[i]);
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
        }
        params.put("me", me);
        Object val = scriptEngine.exec(mf.getBody(), params, tcxt, param.getPrjGuid(), param.getPrjVer());
        if (val != null && val instanceof UnifiedObject) {
            Map<String, Object> realVal = getObject(tcxt, (UnifiedObject) val);
            return realVal;
        }
        return val;
    }

    public Map<String, Object> getObject(GetParam param) {
        TaskContext taskContext = rtContextService.getRunningContext(param.getVer());
        UnifiedObject obj=entityDepService.getObject(taskContext,param.getObjGuid());
        Map<String, Object> pureObj = getObject(taskContext, obj);
        return pureObj;
    }

    public void delObject(GetParam param) {
        TaskContext taskContext = rtContextService.getRunningContext(param.getVer());
        UnifiedObject obj=entityDepService.getObject(taskContext,param.getObjGuid());
        if(obj!=null)
            objectStorageService.deleteObject(taskContext, obj.getGuid(), obj.getPrjGuid(), obj.getPrjVer());
    }

    public void setObject(SetParam  param){
        TaskContext taskContext = rtContextService.getRunningContext(param.getVer());
        ProxyObject proxyObject = v8RttiService.getObject(taskContext, param.getObjGuid());
        proxyObject.mergeFields(param.getValue());
    }

    public Map<String, Object> createObject(CreateObjectParam param) {
        TaskContext taskContext = rtContextService.getRunningContext(param.getVer());
        ClazzDefCompositeDO classDef=null;
        String prjVer= param.getPrjVer();
        if(prjVer==null){
            prjVer=taskContext.getVer();
        }
        if(StringUtils.isNotBlank(param.getClsGuid())){
            classDef = CoreClazzDef.getCoreClassObject(param.getClsGuid());;
            if (classDef == null) {
                classDef = entityDepService.getClsDefByGuid(taskContext, param.getClsGuid());
            }
        }else{
            String cguid = CoreClazzDef.getCoreClassGuid(param.getClsName());
            if (cguid != null) {
                classDef = CoreClazzDef.getCoreClassObject(cguid);
            } else {
                classDef = entityDepService.getClsByName(taskContext, param.getClsName());
            }
        }

        if(classDef==null)
            return null;


        ProxyObject proxyObject = v8RttiService.createNewObject(classDef, taskContext, param.isPersist());
        try {
            V8Value target = new JavetProxyConverter().toV8Value(proxyObject.getV8Runtime(), proxyObject);
            ClassUtils.bindMethodsToV8Object(target, classDef.getClazzIdMethodDefList(), proxyObject.getV8Runtime());
            ClassUtils.parseConstructMethod(proxyObject.getV8Runtime(), param.getArgs(), classDef, proxyObject);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        UnifiedObject unified = new UnifiedObject(proxyObject.getGuid(), classDef.getGuid(), param.isPersist(), classDef.getPrjGuid(), classDef.getPrjVer(), taskContext.getVer());
        return getObject(taskContext, unified);
    }

    public Object getObjectValue(GetParam param) {
        TaskContext taskContext = rtContextService.getRunningContext(param.getVer());
        UnifiedObject obj=entityDepService.getObject(taskContext,param.getObjGuid());;
        RtRedisObjectStorageService rtRedisObjectStorageService = SpringUtils.getBean(RtRedisObjectStorageService.class);
        ClazzDefCompositeDO clazzDef = rtRedisObjectStorageService.getClsDef(taskContext, obj.getPrjVer(), obj.getClazzGUID());
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

    public List<Map<String, Object>> listAllObject(TaskContext ctx, QueryAllObjectDTO param){
        BaseQueryDTO<QueryObjectDTO> baseQueryDTO=new BaseQueryDTO<>();
        QueryObjectDTO queryObjectDTO=new QueryObjectDTO();
        queryObjectDTO.setClazzGuid(param.getClazzGuid());
        queryObjectDTO.setPrjId(param.getPrjId());
        queryObjectDTO.setVer(param.getVer());
        queryObjectDTO.setClazzName(param.getClazzName());
        baseQueryDTO.setCondition(queryObjectDTO);
        baseQueryDTO.setSize(Integer.MAX_VALUE);
        baseQueryDTO.setPage(1);
        List<Map<String, Object>> data1 = listObject(ctx, baseQueryDTO);
        if(param.getIncludeInherited()){
            ClazzDefCompositeDO baseCls = entityDepService.getClsDefByGuid(ctx, param.getClazzGuid());
            ClazzDefCompositeDO parentCls=entityDepService.getClsDefByGuid(ctx, baseCls.getParentGuid());
            while(parentCls!=null){
                BaseQueryDTO<QueryObjectDTO> parentQueryDTO=new BaseQueryDTO<>();
                QueryObjectDTO parentCondition = new QueryObjectDTO();
                parentCondition.setClazzGuid(parentCls.getGuid());
                parentCondition.setPrjId(param.getPrjId());
                parentCondition.setVer(param.getVer());
                parentQueryDTO.setCondition(parentCondition);
                parentQueryDTO.setSize(Integer.MAX_VALUE);
                parentQueryDTO.setPage(1);
                List<Map<String, Object>> data2 = listObject(ctx, parentQueryDTO);
                data1.addAll(data2);
                parentCls=entityDepService.getClsDefByGuid(ctx, parentCls.getParentGuid());
            }
        }
        return data1;
    }

    public List<Map<String, Object>> listObject(TaskContext ctx,BaseQueryDTO<QueryObjectDTO> param){
        ClazzDefCompositeDO def = null;
        if(param.getCondition().getClazzGuid()!=null)
            def=entityDepService.getClsDefByGuid(ctx, param.getCondition().getClazzGuid());
        else if(param.getCondition().getClazzName()!=null)
            def=entityDepService.getClsByName(ctx, param.getCondition().getClazzName());
        if(def==null)
            return new ArrayList<>();
        QueryClass storeQuery = new QueryClass();
        BaseQueryDTO<QueryClass> query = new BaseQueryDTO<>();
        query.setCondition(storeQuery);
        R<List<Map<String, Object>>> result = remoteStore.query(query);
        if(result.getData()!=null){
            ClazzDefCompositeDO cls=def;
            result.getData().stream().forEach(resData->{
                resData.put(FieldConstants.CLAZZ_GUID, cls.getGuid());
                resData.put(FieldConstants.PROJECT_GUID, cls.getPrjGuid());
                resData.put(FieldConstants.PROJECT_VER, cls.getPrjVer());
                resData.put(FieldConstants.CLASS, ClsDf.from(cls, ctx));
            });
            return result.getData();
        }else{
            return new ArrayList<>();
        }
    }


    public Integer deleteTree(TaskContext ctx, String guid){
        GetParam param=new GetParam();
        param.setObjGuid(guid);
        param.setVer(param.getVer());
        Map<String, Object> eNode = getObject(param);
        if(eNode==null){
            return 0;
        }
        Set<String> treeGuids=new HashSet<>();
        treeGuids.add(guid);
        Object parentGuid = eNode.get("parent");
        if(Objects.equals("0",parentGuid)){
            // 根节点
            QueryClass treeQuery = new QueryClass();
            BaseQueryDTO<QueryClass> query = new BaseQueryDTO<>();
            query.setCondition(treeQuery);
            Map<String, Object> equals=new HashMap<>();
            equals.put("root", guid);
            equals.put(FieldConstants.CLAZZ_GUID,CoreClazzDef.CLAZZ_TREE_NODE);
            equals.put(FieldConstants.PROJECT_GUID,ctx.getPrjInfo().getPrjGuid());
            R<List<Map<String, Object>>> result = remoteStore.query(query);
            if(result.getData()!=null){
                result.getData().stream().forEach(resData->{
                    Object tmpGuid = resData.get(FieldConstants.GUID);
                    if(tmpGuid!=null)
                        treeGuids.add(tmpGuid.toString());
                });
            }
            for (Iterator<String> iterator = treeGuids.iterator(); iterator.hasNext(); ) {
                String tGuid =  iterator.next();
                entityDepService.deleteObject(ctx, tGuid);
            }
            return treeGuids.size();
        }else{
            int count=0;
            while(!treeGuids.isEmpty()){
                String tGuid=treeGuids.iterator().next();
                treeGuids.remove(tGuid);
                entityDepService.deleteObject(ctx, tGuid);
                count++;
                QueryClass treeQuery = new QueryClass();
                BaseQueryDTO<QueryClass> query = new BaseQueryDTO<>();
                query.setCondition(treeQuery);
                Map<String, Object> equals=new HashMap<>();
                equals.put("parent", guid);
                equals.put(FieldConstants.CLAZZ_GUID,CoreClazzDef.CLAZZ_TREE_NODE);
                equals.put(FieldConstants.PROJECT_GUID,ctx.getPrjInfo().getPrjGuid());
                R<List<Map<String, Object>>> result = remoteStore.query(query);
                if(result.getData()!=null){
                    result.getData().stream().forEach(resData->{
                        Object tmpGuid = resData.get(FieldConstants.GUID);
                        if(tmpGuid!=null)
                            treeGuids.add(tmpGuid.toString());
                    });
                }
            }
            return count;
        }
    }

    public List<TNodeDO> listTree(TaskContext ctx,String type,String subType){
        QueryClass treeQuery = new QueryClass();
        BaseQueryDTO<QueryClass> query = new BaseQueryDTO<>();
        query.setCondition(treeQuery);
        Map<String, Object> equals=new HashMap<>();
        equals.put("type", type);
        equals.put("subType", subType);
        equals.put(FieldConstants.CLAZZ_GUID,CoreClazzDef.CLAZZ_TREE_NODE);
        equals.put(FieldConstants.PROJECT_GUID,ctx.getPrjInfo().getPrjGuid());
        treeQuery.setEquals(equals);
        R<List<Map<String, Object>>> result = remoteStore.query(query);
        List<TNodeDO> ret=new ArrayList<>();
        if(result.getData()!=null){
            result.getData().stream().forEach(resData->{
                TNodeDO node=new TNodeDO();
                node.setGuid(resData.get(FieldConstants.GUID).toString());
                node.setNodeData(resData.get("nodeData").toString());
                node.setId(Long.parseLong(resData.get(FieldConstants.ID)+""));
                node.setType(resData.get("type").toString());
                node.setSubtype(resData.get("subtype").toString());
                node.setPrjId(ctx.getPrjInfo().getPrjId());
                node.setRoot(resData.get("root").toString());
                node.setParent(resData.get("parent").toString());
                node.setNodeType(resData.get("nodeType").toString());
                ret.add(node);
            });
        }
        return ret;
    }

}
