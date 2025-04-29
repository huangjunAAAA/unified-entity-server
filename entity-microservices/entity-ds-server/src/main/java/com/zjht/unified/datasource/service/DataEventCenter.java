package com.zjht.unified.datasource.service;


import com.zjht.unified.common.core.json.GsonUtil;
import com.zjht.unified.datasource.dto.DataValUpdate;
import com.zjht.unified.datasource.dto.RegInfo;
import com.zjht.unified.datasource.dto.WsConnContext;
import com.zjht.unified.datasource.dto.response.Feedback;
import com.zjht.unified.datasource.websocket.WebSocketUsers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
@Slf4j
public class DataEventCenter {

    @Autowired
    private WebSocketUsers webSocketUsers;
    private ConcurrentHashMap<String, WsConnContext> wsConnBinding = new ConcurrentHashMap<>();
    private ConcurrentHashMap<Long, AtomicInteger> proxyUsage = new ConcurrentHashMap<>();

    @Autowired
    private SystemBridgeService bridgeService;

    public void fireUniversalMessage(Feedback feedback, Long dsId) {
        if (dsId == null)
            return;
        wsConnBinding.entrySet().parallelStream()
                .filter(mapping -> dsId.equals(mapping.getValue().getDsId()))
                .forEach(mapping -> {
                    webSocketUsers.setMessageToUser(mapping.getKey(), feedback);
                });
    }

    public Set<Long> getActiveDataSourceList(){
        return wsConnBinding.entrySet().stream()
                .map(mapping -> mapping.getValue().getDsId()).collect(Collectors.toSet());
    }

    public void onMessage(RegInfo rInfo, String wsConn) {
        if (rInfo != null && rInfo.getDsId() != null) {
            wsConnBinding.put(wsConn, new WsConnContext(wsConn, rInfo.getDsId()));
            initDatasourceBridge(rInfo.getDsId());
        }
    }

    public void unbind(String wsConn) {
        WsConnContext wsCtx = wsConnBinding.remove(wsConn);
        if (wsCtx != null) {
            Long datasourceId = wsCtx.getDsId();
            AtomicInteger count = proxyUsage.get(datasourceId);
            if (count == null || count.decrementAndGet() <= 0) {
                log.info("destroy proxy [" + datasourceId + "], ref count:" + count);
                destroyDatasourceBridge(datasourceId);
            }
        }
    }


    public synchronized void destroyDatasourceBridge(Long datasourceId) {
        bridgeService.destroyProxy(datasourceId);
    }

    public synchronized void initDatasourceBridge(Long datasourceId) {
        AtomicInteger count = proxyUsage.get(datasourceId);
        if (count != null) {
            int nc = count.incrementAndGet();
            log.debug("datasource:" + datasourceId + ", ref count:" + nc);
        } else {
            bridgeService.createProxy(datasourceId);
            count = new AtomicInteger(1);
            proxyUsage.put(datasourceId, count);
            log.debug("datasource:" + datasourceId + ", new ref count:1");
        }
    }
}
