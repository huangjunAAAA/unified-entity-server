package com.zjht.unified.service.v8exec;

import com.wukong.bigdata.storage.gather.client.GatherClient;
import com.zjht.unified.common.core.constants.KafkaNames;
import com.zjht.unified.common.core.domain.store.EntityStoreMessageDO;
import com.zjht.unified.domain.composite.ClazzDefCompositeDO;
import com.zjht.unified.domain.composite.FieldDefCompositeDO;
import com.zjht.unified.domain.runtime.UnifiedObject;
import com.zjht.unified.service.ctx.RtRedisObjectStorageService;
import com.zjht.unified.service.ctx.TaskContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class V8RttiService {

    @Autowired
    private RtRedisObjectStorageService redisObjectStorageService;

    @Autowired
    private GatherClient gather;


    public void test() {
        gather.addRecordAsString(KafkaNames.DRIVER_RAWDATA_TO_STORE,false,"key","table","你好",System.currentTimeMillis());
    }


    public ProxyObject createNewObject(ClazzDefCompositeDO classDef, TaskContext taskContext, Boolean isPersist){
        String guid = UUID.randomUUID().toString();
        ProxyObject proxyObject = new ProxyObject(taskContext,guid,classDef.getGuid());

        //加载默认值
        List<FieldDefCompositeDO> clazzIdFieldDefList = classDef.getClazzIdFieldDefList();
        for (FieldDefCompositeDO fieldDefCompositeDO : clazzIdFieldDefList) {
            redisObjectStorageService.setObjectAttrValue(taskContext, guid, fieldDefCompositeDO.getName(), fieldDefCompositeDO.getInitValue(), false);
        }

        if (isPersist) {
//            gather.addRecordAsString();
            EntityStoreMessageDO entityStoreMessageDO = new EntityStoreMessageDO();
            gather.addRecordAsString(KafkaNames.DRIVER_RAWDATA_TO_STORE,false,"key","table",entityStoreMessageDO,System.currentTimeMillis());

        }

        redisObjectStorageService.setObject(taskContext,new UnifiedObject(guid,classDef.getGuid(),isPersist));
        return proxyObject;
    }

    public ProxyObject getObject(TaskContext ctx, String guid){
        UnifiedObject uo = redisObjectStorageService.getObject(ctx, guid);
        return createFromUnifiedObject(ctx,uo);
    }

    public ProxyObject createFromUnifiedObject(TaskContext ctx,UnifiedObject uo){
        if(uo!=null){
            ProxyObject proxyObject = new ProxyObject(ctx,uo.getGuid(),uo.getClazzGUID());
            return proxyObject;
        }
        return null;
    }
}
