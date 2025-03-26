package com.zjht.unified.service.ctx;

import com.zjht.unified.domain.composite.FsmDefCompositeDO;
import com.zjht.unified.domain.simple.SentinelDefDO;

import java.util.HashMap;
import java.util.Map;

public class UnifiedEntityStatics implements StaticMgmt{

    public static final String STATIC_TYPE_SENTINEL="sentinel";
    public static final String STATIC_TYPE_FSM="fsm";

    private Map<String, SentinelDefDO> sentinelMap=new HashMap<>();
    private Map<String, FsmDefCompositeDO> fsmMap=new HashMap<>();

    @Override
    public <T> T getObject(String type, String guid) {
        if(STATIC_TYPE_SENTINEL.equals(type))
            return (T)sentinelMap.get(guid);
        if (STATIC_TYPE_FSM.equals(type))
            return (T)fsmMap.get(guid);
        return null;
    }

    @Override
    public void setObject(String type, String guid, Object obj) {
        if (STATIC_TYPE_SENTINEL.equals(type))
            sentinelMap.put(guid, (SentinelDefDO)obj);
        if (STATIC_TYPE_FSM.equals(type))
            fsmMap.put(guid, (FsmDefCompositeDO)obj);
    }
}
