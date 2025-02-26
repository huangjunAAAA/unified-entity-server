package com.zjht.unified.service;


import com.zjht.unified.config.RedisKeyName;
import com.zjht.unified.domain.composite.PrjSpecDO;
import com.zjht.unified.service.ctx.TaskContext;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class RtContextService {



    @Resource
    private RedisTemplate<String, Object> redisTemplate;



    public TaskContext getRunningContext(String ver){
        Optional<TaskContext> find = getAllRunningContext().stream().filter(ctx -> ctx.getVer().equals(ver)).findFirst();
        if(find.isPresent())
            return find.get();
        return null;
    }


    public TaskContext startNewSession(PrjSpecDO task,String ver){
        if(task==null)
            return null;

        TaskContext running=new TaskContext();
        if(ver==null){
            ver=task.getUePrj().getGuid()+":"+System.currentTimeMillis();
        }
        running.setVer(ver);

        return running;
    }


    public TaskContext getRunningContext(Long prjId){
        String k = RedisKeyName.getEntityProjectKey(prjId);
        TaskContext sdpCtx = (TaskContext) redisTemplate.opsForValue().get(k);
        return sdpCtx;
    }

    public void saveRunningContext(TaskContext ctx){
        Object prjId=ctx.getPrjSpec().getUePrj().getId();
        String k = RedisKeyName.getEntityProjectKey(prjId);
        redisTemplate.opsForValue().set(k, ctx);
        redisTemplate.opsForHash().put(RedisKeyName.ALL_RUNNING_PROJECT,prjId.toString(),ctx.getVer());
    }

    public void removeRtContext(Long prjId){
        redisTemplate.opsForHash().delete(RedisKeyName.ALL_RUNNING_PROJECT,prjId.toString());
        String k = RedisKeyName.getEntityProjectKey(prjId);
        redisTemplate.delete(k);
    }

    public List<TaskContext> getAllRunningContext(){
        Set<Object> colpIds = redisTemplate.opsForHash().keys(RedisKeyName.ALL_RUNNING_PROJECT);
        List<TaskContext> cLst = colpIds.stream()
                .map(k -> getRunningContext(Long.parseLong(k.toString())))
                .collect(Collectors.toList());
        return cLst;
    }

}
