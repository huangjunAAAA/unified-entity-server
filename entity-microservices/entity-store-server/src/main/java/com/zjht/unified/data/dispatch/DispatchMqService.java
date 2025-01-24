package com.zjht.unified.data.dispatch;

import com.wukong.bigdata.storage.gather.client.GatherClient;
import com.zjht.unified.data.common.core.constants.KafkaNames;
import com.zjht.unified.data.common.core.domain.store.StoreMessageDO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DispatchMqService {

    @Autowired
    private GatherClient client;

    public void dispatch(StoreMessageDO message) {
        client.addRecordAsString(KafkaNames.STORE_RAWDATA_RELAY_TO_RT, false, KafkaNames.POINT_DATA, "http", message, System.currentTimeMillis());
        client.addRecordAsString(KafkaNames.STORE_RAWDATA_RELAY_TO_INTERNAL, false, KafkaNames.POINT_DATA, "http", message, System.currentTimeMillis());
    }
}
