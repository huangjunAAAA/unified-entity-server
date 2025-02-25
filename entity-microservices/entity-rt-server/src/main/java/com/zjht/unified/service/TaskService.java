package com.zjht.unified.service;


import com.zjht.unified.domain.composite.PrjSpecDO;
import com.zjht.unified.service.ctx.RtRedisObjectStorageService;
import com.zjht.unified.service.ctx.TaskContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TaskService {

    @Autowired
    private RtContextService rtContextService;

    @Autowired
    private RtRedisObjectStorageService rtRedisObjectStorageService;

    public void stopTask(Long prjId){
        rtContextService.removeRtContext(prjId);
    }

    public void startTask(PrjSpecDO spec){

        // 初始化定义
        TaskContext ctx = rtContextService.startNewSession(spec, System.currentTimeMillis() + "");
        rtRedisObjectStorageService.initSpecDefinition(ctx,spec);

        // 将初始化的实例放入redis
        rtRedisObjectStorageService.initializeInstances(ctx,spec);


        // 生成哨兵和状态机的定时任务，通过XXL开始执行

    }
}
