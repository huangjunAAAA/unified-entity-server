package com.zjht.unified.service;


import com.zjht.unified.common.core.constants.Constants;
import com.zjht.unified.domain.composite.PrjSpecDO;
import com.zjht.unified.service.ctx.RtRedisObjectStorageService;
import com.zjht.unified.service.ctx.TaskContext;
import com.zjht.unified.service.ctx.UnifiedEntityStatics;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TaskService {

    @Autowired
    private RtContextService rtContextService;

    @Autowired
    private TimerService timerService;

    @Autowired
    private FsmService fsmService;

    @Autowired
    private RtRedisObjectStorageService rtRedisObjectStorageService;

    public void stopTask(Long prjId) {
        rtContextService.removeRtContext(prjId);
    }

    public void startTask(PrjSpecDO spec) {

        // 初始化定义
        TaskContext ctx = rtContextService.startNewSession(spec, System.currentTimeMillis() + "");
        rtRedisObjectStorageService.initSpecDefinition(ctx, spec);

        // 将初始化的实例放入redis
        rtRedisObjectStorageService.initializeInstances(ctx, spec);


        // 生成哨兵和状态机的定时任务，通过XXL开始执行
        if (CollectionUtils.isNotEmpty(spec.getSentinelDefList())) {
            spec.getSentinelDefList().forEach(ss -> {
                timerService.createSentinel(ctx, ss);
                ctx.getStaticMgmt().setObject(UnifiedEntityStatics.STATIC_TYPE_SENTINEL, ss.getGuid(), ss);
            });
        }

        if (CollectionUtils.isNotEmpty(spec.getFsmList())) {
            spec.getFsmList().forEach(sf -> {
                fsmService.initFsm(ctx,sf);
                if (sf.getDriver() == Constants.FSM_TIMER){
                    timerService.createFSM(ctx, sf);
                    ctx.getStaticMgmt().setObject(UnifiedEntityStatics.STATIC_TYPE_FSM, sf.getGuid(), sf);
                }
            });
        }
    }
}
