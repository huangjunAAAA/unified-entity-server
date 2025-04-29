package com.zjht.unified.datasource.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.zjht.unified.common.core.domain.R;
import com.zjht.unified.datasource.dto.SystemSpec;
import com.zjht.unified.datasource.entity.DtpDataSource;
import com.zjht.unified.datasource.service.sysproxy.SystemProxy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@Component
public class DSManager {

    @Autowired
    protected SwaggerApiService apiService;

    @Autowired
    protected IDtpDataSourceService  dtpDataSourceService;

    protected SystemBridgeService systemBridgeService;
    public Map<String,List<Map<String,Object>>> pullData(DtpDataSource dtpDataSource) {
        log.info("pull data from datasource id:" + dtpDataSource.getId());
        SystemSpec dataspec = apiService.convert(dtpDataSource);
        if(dataspec==null)
            log.error("SystemSpec is null, pull data failure.");
        ConcurrentHashMap<String,List<Map<String,Object>>> ret=new ConcurrentHashMap<>();
        // 遍历所有实体，根据实体的list api来获取完整的实体列表
        ArrayBlockingQueue<String> failedBizType=new ArrayBlockingQueue<>(dataspec.getBizObjects().size());
        dataspec.getBizObjects().parallelStream()
                .filter(spec->spec.getEnabled())
                .forEach(bizEntitySpec -> {
                    try {
                        SystemProxy proxy = systemBridgeService.createProxy(dataspec.getSourceId());
                        R hresult = proxy.getBizObjectList(bizEntitySpec.getObjectType(), null);
                        if (hresult.getCode() == R.SUCCESS) {
                            List<Map<String, Object>> rMapList = (List<Map<String, Object>>) hresult.getData();
                            ret.put(bizEntitySpec.getObjectType(), rMapList);
                        } else {
                            failedBizType.add(bizEntitySpec.getObjectType());
                        }
                    }catch (Throwable e){
                        log.error("failed to fetch biz object type:"+bizEntitySpec.getObjectType());
                        log.error(e.getMessage(),e);
                    }
                });
        if(!failedBizType.isEmpty()){
            return null;
        }
        return ret;
    }


    public Map<String,List<Map<String,Object>>> pullData(Long dsId) {
        DtpDataSource ds = dtpDataSourceService.getById(dsId);
        if(ds==null)
            return null;
        return pullData(ds);
    }
}
