package com.zjht.unified.datasource.service;

import com.zjht.unified.datasource.entity.DtpDataSource;
import com.zjht.unified.datasource.service.sysproxy.SystemProxy;
import com.zjht.unified.datasource.service.sysproxy.impl.Std20Stub;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class SystemBridgeService {

    private ConcurrentHashMap<Long, SystemProxy> proxies =new ConcurrentHashMap<>();
    @Autowired
    private IDtpDataSourceService dataSourceService;

    @Autowired
    private AutowireCapableBeanFactory wirer;

    public SystemProxy createProxy(Long datasourceId){
        SystemProxy bridge = proxies.get(datasourceId);
        if(bridge!=null)
            return bridge;
        DtpDataSource ds = dataSourceService.getById(datasourceId);
        if(ds==null){
            log.error("invalid datasource id:"+datasourceId);
            return null;
        }
        return createProxy(ds);
    }

    public synchronized SystemProxy createProxy(DtpDataSource ds){
        if(ds==null){
            log.error("null datasource received.");
            return null;
        }
        SystemProxy bridge = proxies.get(ds.getId());
        if(bridge!=null)
            return bridge;


        log.info("creating {} data proxy for datasource: {}",ds.getType(),ds.getId());
        bridge=createProxyIntenal(ds);

        if(bridge!=null){
            wirer.autowireBean(bridge);
            bridge.init(ds);
            proxies.put(ds.getId(),bridge);
        }
        return bridge;
    }

    public void destroyProxy(Long datasourceId){
        SystemProxy proxy = proxies.get(datasourceId);
        destroyProxy(proxy);
    }

    public synchronized void destroyProxy(SystemProxy proxy){
        if(proxy==null)
            return;
        Optional<Map.Entry<Long, SystemProxy>> entry = proxies.entrySet().stream().filter(t -> t.getValue() == proxy).findFirst();
        if(entry.isPresent()){
            proxies.remove(entry.get().getKey());
        }
    }

    private SystemProxy createProxyIntenal(DtpDataSource ds){
        if(ds.getType().equalsIgnoreCase("integrated")){
            return new Std20Stub();
        }
        return null;
    }

    public SystemProxy.RemoteSystemMeta queryProxy(DtpDataSource dataSource){
        SystemProxy stub = proxies.get(dataSource);
        if(stub==null)
            stub=createProxyIntenal(dataSource);
        if(stub!=null)
            return stub.getMeta();
        return null;
    }
}
