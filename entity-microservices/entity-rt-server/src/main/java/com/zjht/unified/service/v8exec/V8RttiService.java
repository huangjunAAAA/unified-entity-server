package com.zjht.unified.service.v8exec;

import com.wukong.bigdata.storage.gather.client.GatherClient;
import com.zjht.unified.common.core.constants.Constants;
import com.zjht.unified.common.core.constants.KafkaNames;
import com.zjht.unified.common.core.domain.store.EntityStoreMessageDO;
import com.zjht.unified.domain.composite.ClazzDefCompositeDO;
import com.zjht.unified.domain.composite.FieldDefCompositeDO;
import com.zjht.unified.domain.runtime.UnifiedObject;
import com.zjht.unified.service.ctx.EntityDepService;
import com.zjht.unified.service.ctx.PrjUniqueInfo;
import com.zjht.unified.service.ctx.RtRedisObjectStorageService;
import com.zjht.unified.service.ctx.TaskContext;
import com.zjht.unified.utils.StoreUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
public class V8RttiService {

    @Autowired
    private RtRedisObjectStorageService redisObjectStorageService;

    @Autowired
    private EntityDepService entityDepService;

    @Autowired
    private GatherClient gather;


    public void test() {
        gather.addRecordAsString(KafkaNames.DRIVER_RAWDATA_TO_STORE,false,"key","table","你好",System.currentTimeMillis());
    }


    public ProxyObject createNewObject(ClazzDefCompositeDO classDef, TaskContext taskContext, Boolean isPersist){
        String prjGuid=taskContext.getPrjInfo().getPrjGuid();
        String prjVer=taskContext.getPrjInfo().getPrjVer();
        if(!classDef.getType().equals(Constants.CLASS_TYPE_SYSTEM)) {
            PrjUniqueInfo prjInfo = entityDepService.getPrjInfoByGuid(taskContext, classDef.getGuid());
            prjGuid=prjInfo.getPrjGuid();
            prjVer=prjInfo.getPrjVer();
        }
        String guid = UUID.randomUUID().toString();
        ProxyObject proxyObject = new ProxyObject(taskContext,guid,classDef.getGuid(),prjGuid,prjVer);

        //加载默认值
        List<FieldDefCompositeDO> clazzIdFieldDefList = classDef.getClazzIdFieldDefList();
        for (FieldDefCompositeDO fieldDefCompositeDO : clazzIdFieldDefList) {
            redisObjectStorageService.setObjectAttrValue(taskContext, guid, fieldDefCompositeDO.getName(), fieldDefCompositeDO.getInitValue(), false);
        }

        if (isPersist) {
            Map<String, Object> objectAttrValueMap = redisObjectStorageService.getObjectAttrValueMap(taskContext, guid,  prjGuid,prjVer);
            objectAttrValueMap.put("clazz_guid",classDef.getGuid());
            EntityStoreMessageDO messageDO = StoreUtil.getStoreMessageDO(classDef, taskContext,objectAttrValueMap);
            log.info("send message to topic :{} message:{}",KafkaNames.UNIFIED_ENTITY_TO_STORE,messageDO);
            gather.addRecordAsString(KafkaNames.UNIFIED_ENTITY_TO_STORE,false,KafkaNames.ENTITY_DATA,Constants.CMD_STORE_ENTITY,messageDO,System.currentTimeMillis());
        }

        redisObjectStorageService.setObject(taskContext,new UnifiedObject(guid,classDef.getGuid(), isPersist,prjGuid,prjVer,taskContext.getVer()));
        return proxyObject;
    }

    public ProxyObject getObject(TaskContext ctx, String guid){
        UnifiedObject uo = entityDepService.getObject(ctx, guid);
        return createFromUnifiedObject(ctx,uo);
    }

    public ProxyObject createFromUnifiedObject(TaskContext ctx,UnifiedObject uo){
        if(uo!=null){
            ProxyObject proxyObject = new ProxyObject(ctx,uo.getGuid(),uo.getClazzGUID(),uo.getPrjGuid(),uo.getPrjVer());
            return proxyObject;

        }
        return null;
    }

    public boolean deleteObject(TaskContext ctx, String guid) {
        // 先获取对象，确保存在
        UnifiedObject unifiedObject = entityDepService.getObject(ctx, guid);

        if (unifiedObject == null) {
            log.warn("Object with guid {} not found, nothing to delete!", guid);
            return false;
        }

        // 删除对象记录
        boolean objectDeleted = redisObjectStorageService.deleteObject(ctx, guid,unifiedObject.getPrjGuid(),unifiedObject.getPrjVer());
        if (objectDeleted) {
            log.info("Successfully deleted object with guid {}", guid);
            return true;
        } else {
            log.error("Failed to delete object with guid {}", guid);
            return false;
        }
    }


}
