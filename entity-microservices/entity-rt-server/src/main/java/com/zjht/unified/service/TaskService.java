package com.zjht.unified.service;


import com.zjht.unified.common.core.constants.Constants;
import com.zjht.unified.config.RedisKeyName;
import com.zjht.unified.domain.composite.ClazzDefCompositeDO;
import com.zjht.unified.domain.composite.FsmDefCompositeDO;
import com.zjht.unified.domain.composite.PrjSpecDO;
import com.zjht.unified.domain.simple.InitialInstanceDO;
import com.zjht.unified.domain.simple.SentinelDefDO;
import com.zjht.unified.domain.simple.StaticDefDO;
import com.zjht.unified.service.ctx.RtRedisObjectStorageService;
import com.zjht.unified.service.ctx.TaskContext;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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

    public void stopTask(String ver){
        TaskContext ctx = rtContextService.getRunningContext(ver);
        if(ctx!=null){
            rtContextService.removeRtContext(ctx.getPrjInfo().getPrjId());
        }
    }

    /**
     * 设置初始化实例
     * @param ctx
     * @param instanceList
     */
    public void setInstances(TaskContext ctx, List<InitialInstanceDO> instanceList, String prjGuid,String prjVer){
        rtRedisObjectStorageService.initializeInstances(ctx, prjGuid, prjVer, instanceList);
    }

    /**
     * 设置静态变量
     */
    public void setStatics(TaskContext ctx, List<StaticDefDO> statics, String prjGuid,String prjVer){
        if (CollectionUtils.isNotEmpty(statics)) {
            statics.forEach(sd -> {
                rtRedisObjectStorageService.setObjectAttrValue(ctx, RedisKeyName.getStaticKey(Constants.STATICS,ctx.getVer(),prjGuid,prjVer),sd.getFieldName(),sd.getFieldValue(),false);
            });
        }
    }

    /**
     * 设置哨兵
     * @param ctx
     * @param sentinelDefList
     */
    public void setSentinels(TaskContext ctx, List<SentinelDefDO> sentinelDefList, String prjGuid,String prjVer){
        if (CollectionUtils.isNotEmpty(sentinelDefList)) {
            sentinelDefList.forEach(ss -> {
                timerService.createSentinel(ctx, ss);
                String objKey = RedisKeyName.getStaticKey(Constants.STATIC_TYPE_SENTINEL, ctx.getVer(), prjGuid, prjVer);
                rtRedisObjectStorageService.setObjectAttrValue(ctx,objKey,ss.getGuid(),ss,false);
            });
        }
    }

    /**
     * 设置状态机
     * @param ctx
     * @param fsmList
     */
    public void setFsm(TaskContext ctx, List<FsmDefCompositeDO> fsmList, String prjGuid,String prjVer){
        if (CollectionUtils.isNotEmpty(fsmList)) {
            fsmList.forEach(fsm -> {
                fsmService.initFsm(ctx,fsm,prjGuid,prjVer);
                if (fsm.getDriver() == Constants.FSM_TIMER){
                    timerService.createFSM(ctx, fsm);
                }
                String objKey = RedisKeyName.getStaticKey(Constants.STATIC_TYPE_FSM, ctx.getVer(), prjGuid, prjVer);
                rtRedisObjectStorageService.setObjectAttrValue(ctx,objKey,fsm.getGuid(),fsm,false);
            });
        }
    }

    public void startTask(PrjSpecDO spec,String ver) {
        if(ver==null){
            ver=spec.getUePrj().getGuid()+":"+System.currentTimeMillis();
        }
        // 初始化定义
        TaskContext ctx = rtContextService.startNewSession(spec, ver);
        initSpecDefinition(ctx, spec);
        rtContextService.saveRunningContext(ctx);
    }

    //
    public void setClsDefs(TaskContext ctx, List<ClazzDefCompositeDO> clazzList, String prjGuid, String prjVer) {
        clazzList.forEach(cd->{
            rtRedisObjectStorageService.setClsDef(ctx,prjVer,cd.getGuid(),cd);
        });
        clazzList.forEach(cd->{
            cd.getClazzIdFieldDefList().forEach(fd->{
                if(fd.getNature()== Constants.FIELD_TYPE_SCRIPT){
                    rtRedisObjectStorageService.setAttrDef(ctx,prjVer,cd.getGuid(),fd.getGuid(),fd.getName(),fd);
                }
            });
            cd.getClazzIdMethodDefList().forEach(md->{
                rtRedisObjectStorageService.setAttrDef(ctx,prjVer,cd.getGuid(),md.getGuid(),md.getName(),md);
            });
        });
    }

    public void initSpecDefinition(TaskContext ctx, PrjSpecDO spec){

        String prjGuid = spec.getUePrj().getGuid();
        String prjVer = spec.getUePrj().getVersion();

        // 设置类定义
        setClsDefs(ctx,spec.getClazzList(), prjGuid,prjVer);

        // 设置静态变量
        setStatics(ctx, spec.getStaticDefList(), prjGuid,prjVer);

        // 将初始化的实例放入redis
        setInstances(ctx, spec.getInstanceList(), prjGuid,prjVer);

        // 生成哨兵和状态机的定时任务，通过XXL开始执行
        setSentinels(ctx, spec.getSentinelDefList(),prjGuid,prjVer);

        // 设置状态机
        setFsm(ctx, spec.getFsmList(),  prjGuid,prjVer);

//        String prjKey=prjGuid+":"+prjVer;

        if(spec.getDepPkgList()!=null){
            spec.getDepPkgList().forEach(pkg->{
                TaskContext subContext=rtContextService.startNewSession(pkg,ctx.getVer());
                ctx.appendTaskContext(subContext);
                initSpecDefinition(subContext,pkg);
            });
        }
    }
}
