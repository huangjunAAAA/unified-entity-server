package com.zjht.unified.data.mq;


import com.wukong.bigdata.common.model.KafkaMessageRecord;
import com.wukong.core.weblog.utils.JsonUtil;


import com.zjht.unified.common.core.constants.KafkaNames;
import com.zjht.unified.common.core.domain.store.EntityStoreMessageDO;
import com.zjht.unified.common.core.domain.store.StoreMessageDO;
import com.zjht.unified.data.dispatch.DispatchMqService;
import com.zjht.unified.data.storage.persist.GeneralStoreService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.common.TopicPartition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.listener.ConsumerSeekAware;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Map;


@Component
@Slf4j
public class KafkaConsumerListener implements ConsumerSeekAware {

    @Autowired
    private GeneralStoreService storeService;

    @Autowired
    private DispatchMqService dispatchMqService;

    @KafkaListener(topics = {KafkaNames.GATHER_PREFIX+KafkaNames.DRIVER_RAWDATA_TO_STORE,KafkaNames.GATHER_PREFIX+KafkaNames.RT_DERIVEDDATA_TO_STORE}, concurrency = "1", properties = {"auto.offset.reset:latest"})
    public void onMessage(String message) {
        log.info("收到消息：{}",message);
        if(StringUtils.isEmpty(message))
            return;
        KafkaMessageRecord kMsg = JsonUtil.parse(message, KafkaMessageRecord.class);
        if(kMsg==null|| kMsg.getData()==null)
            return;


        EntityStoreMessageDO sMsg = JsonUtil.parse(kMsg.getData().toString(), EntityStoreMessageDO.class);
        List<Long> longs = storeService.saveObjectPoint(sMsg);
        System.out.println("save object  return ids = " + longs);
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
