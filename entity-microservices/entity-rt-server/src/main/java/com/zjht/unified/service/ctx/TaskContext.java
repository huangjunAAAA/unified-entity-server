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
    private PrjUniqueInfo prjInfo=new PrjUniqueInfo();
    private Map<String, TaskContext> deps=new HashMap<>();
    public void appendTaskContext(TaskContext ctx){
        String key=ctx.getPrjInfo().getPrjGuid()+":"+ctx.getPrjInfo().getPrjVer();
        this.deps.put(key,ctx);
    }

    public TaskContext getTaskContext(String prjGuid,String prjVer){
        if(this.prjInfo.getPrjGuid().equals(prjGuid)&&this.prjInfo.getPrjVer().equals(prjVer))
            return this;
        String key=prjGuid+":"+prjVer;
        return this.deps.get(key);
    }
}
