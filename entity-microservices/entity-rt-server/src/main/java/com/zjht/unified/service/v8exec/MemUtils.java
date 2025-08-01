package com.zjht.unified.service.v8exec;

import com.caoccao.javet.annotations.V8Function;
import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interop.V8Runtime;
import com.caoccao.javet.values.V8Value;
import com.zjht.unified.jsengine.v8.utils.V8BeanUtils;
import com.zjht.unified.service.ctx.TaskContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

@Slf4j
public class MemUtils {

    private String prjGuid;

    private String prjVer;

    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    public MemUtils(TaskContext taskContext, String prjGuid, String prjVer) {
        this.taskContext = taskContext;
        this.prjGuid = prjGuid;
        this.prjVer = prjVer;
    }

    private TaskContext taskContext;

    @V8Function(name="set")
    public void set(String name,Object obj){
        String key=taskContext.getVer()+":"+name;
        redisTemplate.opsForValue().set(key,obj);
    }

    @V8Function(name="get")
    public V8Value get(String name) throws JavetException {
        String key=taskContext.getVer()+":"+name;
        Object v = redisTemplate.opsForValue().get(key);
        V8Runtime v8Runtime = V8EngineService.getRuntime(taskContext, taskContext.getPrjInfo().getPrjGuid(), taskContext.getPrjInfo().getPrjVer());
        if(v==null){
            return v8Runtime.createV8ValueNull();
        }else{
            return V8BeanUtils.toV8Value(v8Runtime,v);
        }
    }
}
