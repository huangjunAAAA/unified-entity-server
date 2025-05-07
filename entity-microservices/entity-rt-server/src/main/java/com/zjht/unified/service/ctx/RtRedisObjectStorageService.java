package com.zjht.unified.service.ctx;


import com.google.common.collect.Maps;
import com.wukong.bigdata.storage.gather.client.GatherClient;
import com.wukong.core.weblog.utils.JsonUtil;
import com.zjht.unified.common.core.constants.KafkaNames;
import com.zjht.unified.common.core.domain.store.EntityStoreMessageDO;
import com.zjht.unified.common.core.util.SpringUtils;
import com.zjht.unified.config.RedisKeyName;
import com.zjht.unified.domain.composite.ClazzDefCompositeDO;
import com.zjht.unified.domain.simple.InitialInstanceDO;
import com.zjht.unified.domain.simple.InstanceFieldDO;
import com.zjht.unified.domain.runtime.UnifiedObject;
import com.zjht.unified.utils.StoreUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;


@Service
@Slf4j
public class RtRedisObjectStorageService {


    @Autowired
    private EntityDepService entityDepService;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private GatherClient gather;


    public void setObject(TaskContext ctx, UnifiedObject uo){
        String key=RedisKeyName.getObjectRtKey(ctx.getVer(),uo.getGuid(),uo.getPrjGuid(),uo.getPrjVer());
        redisTemplate.opsForValue().setIfAbsent(key,uo);
    }

    public UnifiedObject getObject(TaskContext ctx, String guid,String prjGuid,String prjVer){
        String key=RedisKeyName.getObjectRtKey(ctx.getVer(),guid,prjGuid,prjVer);
        return (UnifiedObject) redisTemplate.opsForValue().get(key);
    }

    public void setObjectAttrValue(TaskContext ctx, String guid, String attrName, Object val, boolean dispatch){
        log.info("set attr:"+guid+"."+attrName+"=>["+val+"]");
        String key = RedisKeyName.getObjectKey(guid, ctx.getVer(),ctx.getPrjInfo().getPrjGuid(),ctx.getPrjInfo().getPrjVer());
        redisTemplate.opsForHash().put(key,attrName,val);

        if (dispatch) {
            HashMap<String, Object> kvMap = Maps.newHashMap();
            kvMap.put(attrName,val);
            kvMap.put("guid", guid);
            EntityDepService entityDepService=SpringUtils.getBean(EntityDepService.class);
            UnifiedObject object = entityDepService.getObject(ctx, guid);
            RtRedisObjectStorageService rtRedisObjectStorageService= SpringUtils.getBean(RtRedisObjectStorageService.class);
            ClazzDefCompositeDO classDef = rtRedisObjectStorageService.getClsDef(ctx, object.getPrjVer(),  object.getClazzGUID());
            EntityStoreMessageDO storeMessageDO = StoreUtil.getStoreMessageDO(classDef, ctx, kvMap, false);
            log.info("send update message to topic :{} message:{}",KafkaNames.UNIFIED_ENTITY_FIELD_STORE,storeMessageDO);

            gather.addRecordAsString(KafkaNames.UNIFIED_ENTITY_FIELD_STORE,false,KafkaNames.ENTITY_DATA,"update",storeMessageDO,System.currentTimeMillis());
        }
    }

    public void delObjectAttr(TaskContext ctx,String guid,String attrName,String prjGuid,String prjVer){
        log.info("delete attr:"+guid+"."+attrName);
        String key = RedisKeyName.getObjectKey(guid, ctx.getVer(),prjGuid,prjVer);
        redisTemplate.opsForHash().delete(key,attrName);
    }

    public Object getObjectAttrValue(TaskContext ctx, String guid, String attrName,String prjGuid,String prjVer){
        String key = RedisKeyName.getObjectKey(guid, ctx.getVer(),prjGuid,prjVer);
        return redisTemplate.opsForHash().get(key,attrName);
    }

    public Map<String, Object> getObjectAttrValueMap(TaskContext ctx, String guid,String prjGuid,String prjVer){
        String key = RedisKeyName.getObjectKey(guid, ctx.getVer(),prjGuid,prjVer);
        Map<Object, Object> kvMap = redisTemplate.opsForHash().entries(key);
        Map<String, Object> resultMap = new HashMap<>();
        kvMap.forEach((k, v) -> resultMap.put(String.valueOf(k), v));
        resultMap.put("guid", guid);
        return resultMap;
    }

    public Object getAttrDef(TaskContext ctx, String prjVer,String clsGuid, String attrName){
        String key = RedisKeyName.getObjectAttrDefKey(prjVer,clsGuid, ctx.getVer(), attrName);
        return redisTemplate.opsForValue().get(key);
    }

    public Object getAttrDefByGuid(TaskContext ctx, String prjVer,String fieldGuid){
        String key = RedisKeyName.getObjectAttrDefKey(prjVer,fieldGuid, ctx.getVer());
        return redisTemplate.opsForValue().get(key);
    }

    public void setAttrDef(TaskContext ctx, String prjVer,String clsGuid, String fieldGuid, String attrName,Object def){
        String key = RedisKeyName.getObjectAttrDefKey(prjVer,clsGuid, ctx.getVer(), attrName);
        redisTemplate.opsForValue().set(key,def);
        String key2 = RedisKeyName.getObjectAttrDefKey(prjVer,fieldGuid, ctx.getVer());
        redisTemplate.opsForValue().set(key2,def);
    }

    public void setClsDef(TaskContext ctx, String prjVer,String clsGuid, ClazzDefCompositeDO clazzDef){
        String key=RedisKeyName.getClsDefKey(ctx.getVer(),prjVer,clsGuid);
        redisTemplate.opsForValue().set(key,clazzDef);
        addNameToGUID(ctx.getVer(), clsGuid, clazzDef.getName(), clazzDef.getPrjGuid(), clazzDef.getPrjVer());
    }

    public ClazzDefCompositeDO getClsDef(TaskContext ctx, String prjVer,String clsGuid){
        String key=RedisKeyName.getClsDefKey(ctx.getVer(),prjVer,clsGuid);
        return (ClazzDefCompositeDO) redisTemplate.opsForValue().get(key);
    }

    public ClazzDefCompositeDO getClsDefByName(TaskContext ctx, String prjVer,String name){
        String clsGuid=nameToGUID(ctx.getVer(), name, ctx.getPrjInfo().getPrjGuid(), ctx.getPrjInfo().getPrjVer());
        if (clsGuid==null)
            return null;
        String key=RedisKeyName.getClsDefKey(ctx.getVer(),prjVer,clsGuid);
        return (ClazzDefCompositeDO) redisTemplate.opsForValue().get(key);
    }

    public String nameToGUID(String ver, String name, String prjGuid,String prjVer){
        String key=RedisKeyName.nameGUIDKey(ver,name,prjGuid,prjVer);
        return (String)redisTemplate.opsForValue().get(key);
    }

    public void addNameToGUID(String ver, String guid, String name, String prjGuid,String prjVer){
        String key=RedisKeyName.nameGUIDKey(ver,name,prjGuid,prjVer);
        redisTemplate.opsForValue().setIfAbsent(key,guid);
    }




    public void initializeInstances(TaskContext ctx, String prjGuid, String prjVer, List<InitialInstanceDO> instanceList){
        if (Objects.nonNull(instanceList)) {
            instanceList.forEach(inst->{
                setObject(ctx,new UnifiedObject(inst.getGuid(),inst.getClassGuid(),true, prjGuid,prjVer,ctx.getVer()));
                if(StringUtils.isNotBlank(inst.getAttrValue())){
                    List<InstanceFieldDO> fdlst = JsonUtil.parseArray(inst.getAttrValue(), InstanceFieldDO.class);
                    fdlst.forEach(fd->{
                        setObjectAttrValue(ctx,inst.getGuid(),fd.getField(),fd.getCurrentValue(),false);
                    });
                }

                Map<String, Object> objectAttrValueMap = getObjectAttrValueMap(ctx, inst.getGuid(),prjGuid,prjVer);
                objectAttrValueMap.put("clazz_guid",inst.getClassGuid());
                RtRedisObjectStorageService rtRedisObjectStorageService= SpringUtils.getBean(RtRedisObjectStorageService.class);
                ClazzDefCompositeDO classDef = rtRedisObjectStorageService.getClsDef(ctx, prjVer,  inst.getClassGuid());
                EntityStoreMessageDO messageDO = StoreUtil.getStoreMessageDO(classDef, ctx,objectAttrValueMap,true);
                log.info("send message to topic :{} message:{}",KafkaNames.UNIFIED_ENTITY_TO_STORE,messageDO);
                gather.addRecordAsString(KafkaNames.UNIFIED_ENTITY_TO_STORE,false,KafkaNames.ENTITY_DATA,"save",messageDO,System.currentTimeMillis());
            });
        }

    }

    public boolean deleteObject(TaskContext ctx, String guid,String prjGuid,String prjVer) {
        // 获取对象
        UnifiedObject unifiedObject = getObject(ctx, guid, prjGuid,prjVer);

        if (unifiedObject == null) {
            log.warn("Object with guid {} not found, nothing to delete!", guid);
            return false;
        }

        // 删除对象属性
        Map<String, Object> objectAttrValueMap = getObjectAttrValueMap(ctx, guid, prjGuid,prjVer);
        if (objectAttrValueMap != null && !objectAttrValueMap.isEmpty()) {
            objectAttrValueMap.keySet().forEach(attrName -> {
                delObjectAttr(ctx, guid, attrName,prjGuid,prjVer);  // 删除每个属性
            });
        }

        // 删除对象记录
        String objectKey = RedisKeyName.getObjectRtKey(ctx.getVer(), guid, prjGuid,prjVer);
        boolean deleted = redisTemplate.delete(objectKey);
        if (deleted) {
            return true;
        } else {
            return false;
        }
    }

}
