package com.zjht.unified.service;

import com.caoccao.javet.interop.converters.JavetProxyConverter;
import com.caoccao.javet.values.V8Value;
import com.zjht.unified.common.core.constants.Constants;
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

import java.lang.reflect.Modifier;
import java.util.*;
import java.util.stream.Collectors;

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
            Map<String, Object> realVal = getObject(tcxt, (UnifiedObject) val, true);
            return realVal;
        }
        return val;
    }

    public Map<String, Object> getObject(GetParam param) {
        TaskContext taskContext = rtContextService.getRunningContext(param.getVer());
        UnifiedObject obj=entityDepService.getObject(taskContext,param.getObjGuid());
        Map<String, Object> pureObj = getObject(taskContext, obj, true);
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
            classDef = entityDepService.getClsDefByGuid(taskContext, param.getClsGuid());
        }else{
            classDef = entityDepService.getClsByName(taskContext, param.getClsName());
        }

        if(classDef==null)
            return null;

        ProxyObject proxyObject = v8RttiService.createNewObject(classDef, taskContext, param.isPersist());
        try {
            V8Value target = new JavetProxyConverter().toV8Value(proxyObject.getV8Runtime(), proxyObject);
            if(classDef.getParentGuid()!=null) {
                List<ClazzDefCompositeDO> parents = entityDepService.getClassDefWithParents(taskContext, classDef.getParentGuid());
                for (Iterator<ClazzDefCompositeDO> iterator = parents.iterator(); iterator.hasNext(); ) {
                    ClazzDefCompositeDO parentCls = iterator.next();
                    ClassUtils.bindMethodsToV8Object(target, parentCls.getClazzIdMethodDefList(), proxyObject.getV8Runtime());
                }
            }
            ClassUtils.bindMethodsToV8Object(target, classDef.getClazzIdMethodDefList(), proxyObject.getV8Runtime());
            ClassUtils.parseConstructMethod(proxyObject.getV8Runtime(), param.getArgs(), classDef, proxyObject);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        UnifiedObject unified = new UnifiedObject(proxyObject.getGuid(), classDef.getGuid(), param.isPersist(), classDef.getPrjGuid(), classDef.getPrjVer(), taskContext.getVer());
        return getObject(taskContext, unified, true);
    }

    public Object getObjectValue(GetParam param) {
        TaskContext taskContext = rtContextService.getRunningContext(param.getVer());
        UnifiedObject obj=entityDepService.getObject(taskContext,param.getObjGuid());;
        RtRedisObjectStorageService rtRedisObjectStorageService = SpringUtils.getBean(RtRedisObjectStorageService.class);
        ClazzDefCompositeDO clazzDef = rtRedisObjectStorageService.getClsDef(taskContext, obj.getPrjVer(), obj.getClazzGUID());
        String field = clazzDef.getPvAttr();
        Object val = objectStorageService.getObjectAttrValue(taskContext, obj.getGuid(), field, obj.getPrjGuid(), obj.getPrjVer());
        if (val != null && val instanceof UnifiedObject) {
            Map<String, Object> realVal = getObject(taskContext, (UnifiedObject) val, true);
            return realVal;
        }
        return val;
    }

    private Map<String, Object> getObject(TaskContext taskContext, UnifiedObject obj, boolean includePrivate) {
        Map<String, Object> ret = new HashMap<>();
        if(obj.getClazzGUID().equals(CoreClazzDef.CLAZZ_TREE_NODE)){
            List<Map<String, Object>> nodes = getTreeNodeGuid(taskContext, obj.getGuid());
            ret.put("extra_deserialize","TNode");
            ret.put("extra_deserialize_data",nodes);

            ClazzDefCompositeDO cls = CoreClazzDef.getCoreClassObject(CoreClazzDef.CLAZZ_TREE_NODE);
            ret.put(FieldConstants.CLAZZ_GUID, CoreClazzDef.CLAZZ_TREE_NODE);
            ret.put(FieldConstants.PROJECT_GUID, taskContext.getPrjInfo().getPrjGuid());
            ret.put(FieldConstants.PROJECT_VER, taskContext.getPrjInfo().getPrjVer());
            ret.put(FieldConstants.CLASS, ClsDf.from(cls, taskContext));
        }else {
            List<ClazzDefCompositeDO> clsList = entityDepService.getClassDefWithParents(taskContext, obj.getClazzGUID());
            for (ClazzDefCompositeDO cls : clsList) {
                if(Objects.equals(cls.getInheritable(),Integer.parseInt(Constants.NO))){
                    continue;
                }
                for (FieldDefCompositeDO field : cls.getClazzIdFieldDefList()) {
                    if(Objects.equals(field.getModifier().toLowerCase(), "private") && !includePrivate)
                        continue;
                    Object val = objectStorageService.getObjectAttrValue(taskContext, obj.getGuid(), field.getName(), obj.getPrjGuid(), obj.getPrjVer());
                    if (val != null) {
                        if (val instanceof UnifiedObject) {
                            Map<String, Object> realVal = getObject(taskContext, (UnifiedObject) val, false);
                            ret.put(field.getName(), realVal);
                        } else {
                            ret.put(field.getName(), val);
                        }
                    }
                }
            }
            ret.put(FieldConstants.GUID, obj.getGuid());
            ret.put(FieldConstants.CLAZZ_GUID, obj.getClazzGUID());
            ret.put(FieldConstants.PROJECT_GUID, obj.getPrjGuid());
            ret.put(FieldConstants.PROJECT_VER, obj.getPrjVer());
            ClsDf currentCls = ClsDf.from(clsList.remove(0), taskContext);
            ret.put(FieldConstants.CLASS, currentCls);
            if(!clsList.isEmpty()) {
                List<ClsDf> parentCls = clsList.stream().map(cls -> ClsDf.from(cls, taskContext)).collect(Collectors.toList());
                ret.put(FieldConstants.PARENT_CLASS_LIST, parentCls);
            }

        }
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
        if(param.getIncludeInherited()&&param.getClazzGuid()!=null){
            ClazzDefCompositeDO baseCls = entityDepService.getClsDefByGuid(ctx, param.getClazzGuid());
            if(baseCls!=null&&baseCls.getParentGuid()!=null) {
                ClazzDefCompositeDO parentCls = entityDepService.getClsDefByGuid(ctx, baseCls.getParentGuid());
                while (parentCls != null) {
                    BaseQueryDTO<QueryObjectDTO> parentQueryDTO = new BaseQueryDTO<>();
                    QueryObjectDTO parentCondition = new QueryObjectDTO();
                    parentCondition.setClazzGuid(parentCls.getGuid());
                    parentCondition.setPrjId(param.getPrjId());
                    parentCondition.setVer(param.getVer());
                    parentQueryDTO.setCondition(parentCondition);
                    parentQueryDTO.setSize(Integer.MAX_VALUE);
                    parentQueryDTO.setPage(1);
                    List<Map<String, Object>> data2 = listObject(ctx, parentQueryDTO);
                    data1.addAll(data2);
                    parentCls = entityDepService.getClsDefByGuid(ctx, parentCls.getParentGuid());
                }
            }
        }

        if(!data1.isEmpty()) {
            ClazzDefCompositeDO cls = entityDepService.getClsDefByGuid(ctx, param.getClazzGuid());
            data1.forEach(data -> {
                preInstallObject(ctx, data, cls);
            });
        }
        return data1;
    }

    public List<Map<String, Object>> listObject(TaskContext ctx,BaseQueryDTO<QueryObjectDTO> param){
        ClazzDefCompositeDO def = null;
        if(param.getCondition().getClazzGuid()!=null){
            def=entityDepService.getClsDefByGuid(ctx, param.getCondition().getClazzGuid());
        } else if(param.getCondition().getClazzName()!=null)
            def=entityDepService.getClsByName(ctx, param.getCondition().getClazzName());
        if(def==null){
            log.error("class not found:"+(param.getCondition().getClazzName()==null?param.getCondition().getClazzGuid():param.getCondition().getClazzName()));
            return new ArrayList<>();
        }

        if(def.getParentGuid()!=null){
            ClazzDefCompositeDO tmp = def;
            while(tmp.getParentGuid()!=null){
                tmp = entityDepService.getClsDefByGuid(ctx, tmp.getParentGuid());
                def.getClazzIdFieldDefList().addAll(tmp.getClazzIdFieldDefList());
            }
        }

        QueryClass storeQuery = new QueryClass();
        BaseQueryDTO<QueryClass> query = new BaseQueryDTO<>();
        query.setCondition(storeQuery);
        storeQuery.setVer(param.getCondition().getVer());
        storeQuery.setPrjId(param.getCondition().getPrjId());
        storeQuery.setClassDef(def);
        query.setSize(param.getSize());
        query.setPage(param.getPage());
        storeQuery.setEquals(param.getCondition().getEquals());
        storeQuery.setLike(param.getCondition().getLike());
        storeQuery.setInCondition(param.getCondition().getInCondition());

        R<List<Map<String, Object>>> result = remoteStore.query(query);
        if(result.getData()!=null){
            ClazzDefCompositeDO cls=def;
            List<Map<String, Object>> mapData = result.getData();
            mapData.stream().forEach(resData->{
                resData.put(FieldConstants.CLAZZ_GUID, cls.getGuid());
                if(cls.getType().equals(Constants.CLASS_TYPE_SYSTEM)){
                    resData.put(FieldConstants.PROJECT_GUID, ctx.getPrjInfo().getPrjGuid());
                    resData.put(FieldConstants.PROJECT_VER, ctx.getPrjInfo().getPrjVer());
                }else {
                    resData.put(FieldConstants.PROJECT_GUID, cls.getPrjGuid());
                    resData.put(FieldConstants.PROJECT_VER, cls.getPrjVer());
                }
                resData.put(FieldConstants.CLASS, ClsDf.from(cls, ctx));
                preInstallObject(ctx, resData,cls);
            });
            return mapData;
        }else{
            return new ArrayList<>();
        }
    }

    public void preInstallObject(TaskContext ctx, Map<String, Object> mapData, ClazzDefCompositeDO cls) {
        for (Iterator<FieldDefCompositeDO> iterator = cls.getClazzIdFieldDefList().iterator(); iterator.hasNext(); ) {
            FieldDefCompositeDO f = iterator.next();
            if (Objects.equals(f.getNature(), Integer.parseInt(FieldConstants.FIELD_TYPE_TREENODE))
                    || Objects.equals(f.getNature(), Integer.parseInt(FieldConstants.FIELD_TYPE_ANY))
                    || Objects.equals(f.getNature(), Integer.parseInt(FieldConstants.FIELD_TYPE_REGULAR_CLASS))) {
                Object fdata = mapData.get(f.getName());
                if (fdata == null)
                    continue;
                UnifiedObject uo = entityDepService.getObject(ctx, (String) fdata);
                if (uo != null) {
                    Map<String, Object> tdata = getObject(ctx, uo, true);
                    mapData.put(f.getName(), tdata);
                }
            }
        }
    }

    public Map<String,Object> treeNodeToMap(TaskContext ctx, TNodeDO node){
        Map<String,Object> ret=new HashMap<>();
        ret.put("id", node.getId());
        ret.put("guid", node.getGuid());
        ret.put("parent", node.getParent());
        ret.put("nodeData", node.getNodeData());
        ret.put("type", node.getType());
        ret.put("prjId", node.getPrjId());
        ret.put("subtype", node.getSubtype());
        ret.put("root", node.getRoot());
        ret.put("nodeType", node.getNodeType());
        return ret;
    }

    public static TNodeDO mapToTreeNode(TaskContext ctx, Map<String, Object> mapData){
        TNodeDO node=new TNodeDO();
        node.setGuid(mapData.get(FieldConstants.GUID).toString());
        node.setNodeData(mapData.get("nodeData").toString());
        node.setId(Long.parseLong(mapData.get(FieldConstants.ID)+""));
        node.setType(mapData.get("type").toString());
        node.setSubtype(mapData.get("subtype").toString());
        node.setPrjId(ctx.getPrjInfo().getPrjId());
        node.setRoot(mapData.get("root").toString());
        node.setParent(mapData.get("parent").toString());
        node.setNodeType(mapData.get("nodeType").toString());
        return node;
    }


    public Integer deleteTree(TaskContext ctx, String guid){
        List<Map<String, Object>> treeGuids = getTreeNodeGuid(ctx, guid);
        for (Iterator<Map<String, Object>> iterator = treeGuids.iterator(); iterator.hasNext(); ) {
            Map<String, Object> tnode =  iterator.next();
            Object tGuid = tnode.get(FieldConstants.GUID);
            if(tGuid!=null)
                entityDepService.deleteObject(ctx, tGuid.toString());
        }
        return treeGuids.size();
    }

    private List<Map<String,Object>> getTreeNodeGuid(TaskContext ctx,String guid){
        GetParam param=new GetParam();
        param.setObjGuid(guid);
        param.setVer(param.getVer());
        Map<String, Object> eNode = getObject(param);
        if(eNode==null){
            return new ArrayList<>();
        }
        List<Map<String,Object>> ret=new ArrayList<>();
        ret.add(eNode);
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
            treeQuery.setEquals(equals);
            treeQuery.setPrjId(ctx.getPrjInfo().getPrjId()+"");
            treeQuery.setVer(ctx.getVer());
            query.setPage(1);
            query.setSize(Integer.MAX_VALUE);
            R<List<Map<String, Object>>> result = remoteStore.query(query);
            if(result.getData()!=null){
                ret.addAll(result.getData());
            }

        }else{
            List<String> tmpGuidList=new ArrayList<>();
            tmpGuidList.add(guid);
            while(!tmpGuidList.isEmpty()){
                String tGuid=tmpGuidList.remove(0);
                QueryClass treeQuery = new QueryClass();
                BaseQueryDTO<QueryClass> query = new BaseQueryDTO<>();
                query.setCondition(treeQuery);
                Map<String, Object> equals=new HashMap<>();
                equals.put("parent", tGuid);
                equals.put(FieldConstants.CLAZZ_GUID,CoreClazzDef.CLAZZ_TREE_NODE);
                equals.put(FieldConstants.PROJECT_GUID,ctx.getPrjInfo().getPrjGuid());
                treeQuery.setEquals(equals);
                treeQuery.setPrjId(ctx.getPrjInfo().getPrjId()+"");
                treeQuery.setVer(ctx.getVer());
                query.setPage(1);
                query.setSize(Integer.MAX_VALUE);
                R<List<Map<String, Object>>> result = remoteStore.query(query);
                if(result.getData()!=null){
                    ret.addAll(result.getData());
                }
            }
        }
        return ret;
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
        treeQuery.setPrjId(ctx.getPrjInfo().getPrjId()+"");
        query.setPage(1);
        query.setSize(Integer.MAX_VALUE);
        treeQuery.setVer(ctx.getVer());
        R<List<Map<String, Object>>> result = remoteStore.query(query);
        List<TNodeDO> ret=new ArrayList<>();
        if(result.getData()!=null){
            result.getData().stream().forEach(resData->{
                TNodeDO node=mapToTreeNode(ctx, resData);
                ret.add(node);
            });
        }
        return ret;
    }

}
