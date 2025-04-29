package com.zjht.unified.datasource.job;


import com.xxl.job.core.handler.annotation.XxlJob;
import com.zjht.unified.datasource.dto.DataValUpdate;
import com.zjht.unified.datasource.dto.response.Feedback;
import com.zjht.unified.datasource.service.DSManager;
import com.zjht.unified.datasource.service.DataEventCenter;
import com.zjht.unified.datasource.util.DataValUtils;
import com.zjht.unified.datasource.util.UniqueTaskThreadPool;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
@Slf4j
public class BizEntityRefreshJob {

    @Autowired
    private DSManager dsManager;

    @Autowired
    private DataEventCenter dataEventCenter;

    private UniqueTaskThreadPool uniqueTaskThreadPool=new UniqueTaskThreadPool();

    @XxlJob("refresh_ds")
    public synchronized void refreshBizData(){
        Set<Long> dsLst = dataEventCenter.getActiveDataSourceList();
        dsLst.forEach(dsId->{
            uniqueTaskThreadPool.submit(new UniqueTaskThreadPool.TagRunnable() {
                @Override
                public String getTag() {
                    return dsId.toString();
                }

                @Override
                public void run() {
                    Map<String, List<Map<String, Object>>> entityData = dsManager.pullData(dsId);
                    for (Iterator<Map.Entry<String, List<Map<String, Object>>>> iterator = entityData.entrySet().iterator(); iterator.hasNext(); ) {
                        Map.Entry<String, List<Map<String, Object>>> itemList =  iterator.next();
                        String bType=itemList.getKey();
                        for (Iterator<Map<String, Object>> iter2 = itemList.getValue().iterator(); iter2.hasNext(); ) {
                            Map<String, Object> item =  iter2.next();
                            String qualifiedId=bType+"."+getObjectId(item);
                            Feedback<DataValUpdate> dv = DataValUtils.convertSimpleData(qualifiedId, ""+dsId, item);
                            dataEventCenter.fireUniversalMessage(dv,dsId);
                        }
                    }
                }
            });
        });
    }

    private Object getObjectId(Map<String, Object> item){
        return item.get("id");
    }
}
