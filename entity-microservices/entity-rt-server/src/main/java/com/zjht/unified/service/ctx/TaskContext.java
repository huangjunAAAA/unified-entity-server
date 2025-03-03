package com.zjht.unified.service.ctx;

import com.zjht.unified.domain.composite.ClazzDefCompositeDO;
import com.zjht.unified.domain.composite.FsmDefCompositeDO;
import com.zjht.unified.domain.composite.PrjSpecDO;
import com.zjht.unified.domain.simple.ClazzDefDO;
import com.zjht.unified.domain.simple.MethodDefDO;
import com.zjht.unified.domain.simple.SentinelDefDO;
import com.zjht.unified.service.v8exec.ProxyObject;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Data
public class TaskContext {
    private String ver;
    private PrjSpecDO prjSpec;
    private Map<String, MethodDefDO> methods=new HashMap<>();
    private Map<String,ClazzDefCompositeDO> clazzMap=new HashMap<>();
    private Map<String,ClazzDefCompositeDO> clazzGUIDMap=new HashMap<>();
    private ConcurrentHashMap<String, ClazzDefCompositeDO> rtti=new ConcurrentHashMap<>();
    private Map<String, SentinelDefDO> sentinelMap=new HashMap<>();
    private Map<String, FsmDefCompositeDO> fsmMap=new HashMap<>();
    private Map<String, ProxyObject> pobjMap = new HashMap<>();
}
