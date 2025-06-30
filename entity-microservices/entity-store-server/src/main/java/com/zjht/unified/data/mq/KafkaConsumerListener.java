package com.zjht.unified.data.mq;


import com.wukong.bigdata.common.model.KafkaMessageRecord;
import com.wukong.core.weblog.utils.JsonUtil;


import com.zjht.unified.common.core.constants.Constants;
import com.zjht.unified.common.core.constants.KafkaNames;
import com.zjht.unified.common.core.domain.store.EntityStoreMessageDO;
import com.zjht.unified.data.dispatch.DispatchMqService;
import com.zjht.unified.data.storage.persist.GeneralStoreService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.common.TopicPartition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.listener.ConsumerSeekAware;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;


@Component
@Slf4j
public class KafkaConsumerListener implements ConsumerSeekAware {

    @Autowired
    private GeneralStoreService storeService;

    @Autowired
    private DispatchMqService dispatchMqService;

    @KafkaListener(topics = {KafkaNames.GATHER_PREFIX+KafkaNames.UNIFIED_ENTITY_TO_STORE,KafkaNames.GATHER_PREFIX+KafkaNames.UNIFIED_ENTITY_FIELD_STORE}, concurrency = "1", properties = {"auto.offset.reset:latest"})
    public void onMessage(String message,@Header("kafka_receivedTopic") String topic) {
        log.info("收到消息，来自 topic：{}，内容：{}", topic, message);
        if(StringUtils.isEmpty(message))
            return;
        KafkaMessageRecord kMsg = JsonUtil.parse(message, KafkaMessageRecord.class);
        if(kMsg==null|| kMsg.getData()==null)
            return;

        EntityStoreMessageDO sMsg = JsonUtil.parse(kMsg.getData().toString(), EntityStoreMessageDO.class);
        if (kMsg.getTable().equals(Constants.CMD_STORE_ENTITY)) {
            List<Long> longs = storeService.saveEntity(sMsg);
            log.info(" table : {} save object  return ids {} " , sMsg.getTblName(),longs);
        } else if (kMsg.getTable().equals(Constants.CMD_UPDATE_ENTITY)) {
            List<Integer> integers = storeService.updateEntity(sMsg);
            log.info("table : {} update object  return  {} " ,sMsg.getTblName(), integers);
        } else if (kMsg.getTable().equals(Constants.CMD_DELETE_ENTITY)) {
            storeService.deleteEntity(sMsg.getTblName(),(String)sMsg.getData(),null);
        } else if (kMsg.getTable().equals(Constants.CMD_ENTITY_DELETE_FIELD)) {
            storeService.removeEntityFieldByGuid(sMsg);
        }



//        if(sMsg.getProtocol().equals("http")){
//            List<Long> ids = storeService.saveObjectPoint(sMsg);
//            sMsg.getExtras().put("ids",ids);
//        }
//
//        if(sMsg.getProtocol().equals("modbus")){
//            Long id=storeService.saveSimplePoint(sMsg);
//            sMsg.getExtras().put("ids", Arrays.asList(id));
//        }

//        dispatchMqService.dispatch(sMsg);
    }

    @Override
    public void onPartitionsAssigned(Map<TopicPartition, Long> assignments, ConsumerSeekCallback callback) {
        assignments.forEach((t, o) -> callback.seekToEnd(t.topic(), t.partition()));
    }

}
