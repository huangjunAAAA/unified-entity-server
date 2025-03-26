package com.zjht.unified.service.ctx;

import com.zjht.unified.domain.composite.ClazzDefCompositeDO;
import com.zjht.unified.domain.composite.FsmDefCompositeDO;
import com.zjht.unified.domain.composite.PrjSpecDO;
import com.zjht.unified.domain.simple.MethodDefDO;
import com.zjht.unified.domain.simple.SentinelDefDO;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class TaskContext {
    private String ver;
    private PrjSpecDO prjSpec;
    private Map<String, MethodDefDO> methods=new HashMap<>();
    private Map<String,ClazzDefCompositeDO> clazzMap=new HashMap<>();
    private Map<String,ClazzDefCompositeDO> clazzGUIDMap=new HashMap<>();
    public StaticMgmt staticMgmt=new UnifiedEntityStatics();
}
