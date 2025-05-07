package com.zjht.unified.service;

import com.zjht.unified.config.RedisKeyName;
import com.zjht.unified.domain.composite.FsmDefCompositeDO;
import com.zjht.unified.domain.simple.FsmConditionDO;
import com.zjht.unified.service.ctx.TaskContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Iterator;
import java.util.Objects;

@Service
public class FsmService {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private IScriptEngine scriptEngine;

    public void evalFsm(TaskContext ctx, FsmDefCompositeDO fsm, String prjGuid, String prjVer){
        String cState=getCurrentState(ctx,fsm.getGuid(),prjGuid,prjVer);
        for (Iterator<FsmConditionDO> iterator = fsm.getFsmIdFsmConditionList().iterator(); iterator.hasNext(); ) {
            FsmConditionDO fc =  iterator.next();
            if(Objects.equals(fc.getCurrentState(),cState)){
                Object result = scriptEngine.exec(fc.getConditionExpr(), ctx,prjGuid,prjVer);
                if(isTrue(result)){
                    scriptEngine.exec(fc.getScript(),ctx,prjGuid,prjVer);
                    setCurrentState(ctx,fsm.getGuid(),fc.getNextState(),prjGuid,prjVer);
                    return;
                }
            }
        }
    }

    public void initFsm(TaskContext ctx, FsmDefCompositeDO fsm,String prjGuid,String prjVer){
        setCurrentState(ctx,fsm.getGuid(),fsm.getInitialState(),prjGuid,prjVer);
    }

    public String getCurrentState(TaskContext ctx, String guid,String prjGuid,String prjVer){
        String key="fsm-"+RedisKeyName.getObjectKey(guid,ctx.getVer(),prjGuid,prjVer);
        Object state = redisTemplate.opsForValue().get(key);
        return (String)state;
    }

    private void setCurrentState(TaskContext ctx, String guid,String val,String prjGuid,String prjVer){
        String key="fsm-"+RedisKeyName.getObjectKey(guid,ctx.getVer(),prjGuid,prjVer);
        redisTemplate.opsForValue().set(key,val);
    }

    private static boolean isTrue(Object result){
        if(result==null)
            return false;
        if(Objects.equals(result,Boolean.TRUE))
            return true;
        if(Objects.equals(result,"true"))
            return true;
        return false;
    }
}
