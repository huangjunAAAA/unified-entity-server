package com.zjht.unified.service;

import com.alibaba.fastjson.JSON;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import com.zjht.unified.common.core.constants.Constants;
import com.zjht.unified.common.core.constants.CoreClazzDef;
import com.zjht.unified.common.core.util.SpringUtils;
import com.zjht.unified.config.RedisKeyName;
import com.zjht.unified.domain.composite.FsmDefCompositeDO;
import com.zjht.unified.domain.runtime.UnifiedObject;
import com.zjht.unified.domain.simple.FsmDefDO;
import com.zjht.unified.domain.simple.SentinelDefDO;
import com.zjht.unified.feign.RemoteXXL;
import com.zjht.unified.feign.model.ReturnMap;

import com.zjht.unified.feign.model.ReturnT;
import com.zjht.unified.feign.model.XxlJobGroup;
import com.zjht.unified.feign.model.XxlJobInfo;
import com.zjht.unified.service.ctx.RtRedisObjectStorageService;
import com.zjht.unified.service.ctx.TaskContext;
import com.zjht.unified.utils.JsonUtilUnderline;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@Slf4j
public class TimerService {

    @Autowired
    private RemoteXXL remoteXXL;

    @Autowired
    private FsmService fsmService;

    @Autowired
    private RtContextService rtContextService;

    private static final String SENTINEL_EXEC_METHOD ="sentinel-exec";

    private static final String FSM_EXEC_METHOD ="fsm-exec";

    @Autowired
    private IScriptEngine scriptEngine;

    private AtomicInteger jgIdRef =new AtomicInteger(-1);

    public void createSentinel(TaskContext ctx, SentinelDefDO sentinel){
        Integer jgid = getJgId(ctx);
        ReturnMap<XxlJobInfo> jobList = remoteXXL.listJobInfo(0, Integer.MAX_VALUE, jgid, SENTINEL_EXEC_METHOD, ctx.getVer());
        if(CollectionUtils.isEmpty(jobList.getData())) {
            String param = JsonUtilUnderline.toJson(new UnifiedObject(sentinel.getGuid(),CoreClazzDef.CLAZZ_SENTINEL,true,
                    ctx.getPrjInfo().getPrjGuid(),ctx.getPrjInfo().getPrjVer(),ctx.getVer()));
            XxlJobInfo jInfo = createJobInfo(jgid, sentinel.getCron(), SENTINEL_EXEC_METHOD, 3, ctx.getVer(), param);
            executeJob(jInfo);
        }
    }

    public void removeAlljobs(TaskContext ctx){
        Integer jgid = getJgId(ctx);
        ReturnMap<XxlJobInfo> jobList = remoteXXL.listJobInfo(0, Integer.MAX_VALUE, jgid, null, ctx.getVer());
        if(jobList.getData()!=null){
            jobList.getData().forEach(jInfo->{
                remoteXXL.removeJob(jInfo.getId());
            });
        }
    }

    public void createFSM(TaskContext ctx, FsmDefDO fsmDef){
        Integer jgid = getJgId(ctx);
        ReturnMap<XxlJobInfo> jobList = remoteXXL.listJobInfo(0, Integer.MAX_VALUE, jgid, FSM_EXEC_METHOD, ctx.getVer());
        if(CollectionUtils.isEmpty(jobList.getData())) {
            String param = JsonUtilUnderline.toJson(new UnifiedObject(fsmDef.getGuid(),CoreClazzDef.CLAZZ_FSM,true,
                    ctx.getPrjInfo().getPrjGuid(),ctx.getPrjInfo().getPrjVer(),ctx.getVer()));
            XxlJobInfo jInfo = createJobInfo(jgid, fsmDef.getCron(), FSM_EXEC_METHOD, 3, ctx.getVer(), param);
            executeJob(jInfo);
        }
    }

    private Integer getJgId(TaskContext ctx){
        return initExec("entity-rt-server");
    }


    @PostConstruct
    public void initXXL(){
        initExec("entity-rt-server");
    }

    private synchronized Integer initExec(String execName){
        int j= jgIdRef.get();
        if(j!=-1)
            return j;
        ReturnMap<XxlJobGroup> jgLst = remoteXXL.listJobGroup(null, Integer.MAX_VALUE, execName, null);
        if(CollectionUtils.isEmpty(jgLst.getData())){
            XxlJobGroup ng=new XxlJobGroup();
            ng.setAppname(execName);
            ng.setAddressType(0);
            ng.setTitle(execName+"驱动执行器");
            ReturnT aResp = remoteXXL.addJobGroup(ng);
            log.info("create xxl job group:"+execName+", resp:"+aResp);
            ReturnMap<XxlJobGroup> mt = remoteXXL.listJobGroup(null, null, execName, null);
            XxlJobGroup dg = mt.getData().get(0);
            return dg.getId();
        }else{
            return jgLst.getData().get(0).getId();
        }
    }

    private XxlJobInfo createJobInfo(Integer jgId, String cron, String execName, Integer retry,String author,String params){
        XxlJobInfo jobInfo=new XxlJobInfo();
        jobInfo.setAuthor(author);
        jobInfo.setScheduleType("CRON");
        jobInfo.setScheduleConf(cron);
        jobInfo.setGlueType("BEAN");
        jobInfo.setExecutorHandler(execName);
        jobInfo.setJobGroup(jgId);
        jobInfo.setExecutorRouteStrategy("FIRST");
        jobInfo.setMisfireStrategy("DO_NOTHING");
        jobInfo.setExecutorBlockStrategy("SERIAL_EXECUTION");
        jobInfo.setExecutorTimeout(0);
        if(retry!=null)
            jobInfo.setExecutorFailRetryCount(retry);
        else
            jobInfo.setExecutorFailRetryCount(3);
        jobInfo.setGlueRemark("GLUE代码初始化");
        jobInfo.setExecutorParam(params);
        jobInfo.setJobDesc(execName);
        return jobInfo;
    }

    private ReturnT executeJob(XxlJobInfo jobInfo){
        ReturnT<String> result = remoteXXL.addJob(jobInfo);
        if(result.getCode()==ReturnT.SUCCESS_CODE) {
            ReturnT sRes = remoteXXL.startJob(Integer.parseInt(result.getContent()));
            log.info("create xxl job success:"+sRes.getContent());
            return sRes;
        }else{
            log.error("create xxl job failed:"+result.getMsg());
            return result;
        }
    }


    @XxlJob(SENTINEL_EXEC_METHOD)
    public com.xxl.job.core.biz.model.ReturnT execSentinelScript(String param){
        if (StringUtils.isEmpty(param))
            param = XxlJobHelper.getJobParam();
        UnifiedObject id = JsonUtilUnderline.parse(param, UnifiedObject.class);
        TaskContext ctx = rtContextService.getRunningContext(id.getVer());
        log.info("sentinel-exec job get ctx:{}",JSON.toJSONString(ctx));
        RtRedisObjectStorageService rtRedisObjectStorageService = SpringUtils.getBean(RtRedisObjectStorageService.class);
        String objKey = RedisKeyName.getStaticKey(Constants.STATIC_TYPE_SENTINEL, id.getVer(), id.getPrjGuid(), id.getPrjVer());
        SentinelDefDO ss = (SentinelDefDO) rtRedisObjectStorageService.getObjectAttrValue(ctx,objKey,id.getGuid(),id.getPrjGuid(),id.getPrjVer());
        log.info("sentinel-exec job get SentinelDefDO:{}",JSON.toJSONString(ss));
        scriptEngine.exec(ss.getBody(), null, ctx,id.getPrjGuid(),id.getPrjVer());
        return com.xxl.job.core.biz.model.ReturnT.SUCCESS;
    }

    @XxlJob(FSM_EXEC_METHOD)
    public com.xxl.job.core.biz.model.ReturnT evalFSM(String param){
        if (StringUtils.isEmpty(param))
            param = XxlJobHelper.getJobParam();
        UnifiedObject id = JsonUtilUnderline.parse(param, UnifiedObject.class);
        TaskContext ctx = rtContextService.getRunningContext(id.getVer());
        RtRedisObjectStorageService rtRedisObjectStorageService = SpringUtils.getBean(RtRedisObjectStorageService.class);
        String objKey = RedisKeyName.getStaticKey(Constants.STATIC_TYPE_FSM, id.getVer(), id.getPrjGuid(), id.getPrjVer());
        FsmDefCompositeDO ss = (FsmDefCompositeDO) rtRedisObjectStorageService.getObjectAttrValue(ctx,objKey,id.getGuid(),id.getPrjGuid(),id.getPrjVer());
        fsmService.evalFsm(ctx,ss,id.getPrjGuid(),id.getPrjVer());
        return com.xxl.job.core.biz.model.ReturnT.SUCCESS;
    }
}
