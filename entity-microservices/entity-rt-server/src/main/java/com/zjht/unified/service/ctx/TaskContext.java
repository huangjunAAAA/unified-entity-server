package com.zjht.unified.service.ctx;

import com.zjht.unified.domain.composite.ClazzDefCompositeDO;
import com.zjht.unified.domain.composite.PrjSpecDO;
import com.zjht.unified.domain.simple.ClazzDefDO;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Data
public class TaskContext {
    private String ver;
    private PrjSpecDO prjSpec;
    private Map<String,String> methods=new HashMap<>();
    private Map<String,ClazzDefCompositeDO> clazzParent=new HashMap<>();
    private ConcurrentHashMap<String, ClazzDefCompositeDO> rtti=new ConcurrentHashMap<>();
}
