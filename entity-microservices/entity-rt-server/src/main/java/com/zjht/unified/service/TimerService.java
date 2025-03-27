package com.zjht.unified.service;

import com.wukong.core.weblog.utils.JsonUtil;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import com.zjht.unified.domain.composite.FsmDefCompositeDO;
import com.zjht.unified.domain.simple.FsmDefDO;
import com.zjht.unified.domain.simple.SentinelDefDO;
import com.zjht.unified.feign.RemoteXXL;
import com.zjht.unified.feign.model.ReturnMap;

import com.zjht.unified.feign.model.ReturnT;
import com.zjht.unified.feign.model.XxlJobGroup;
import com.zjht.unified.feign.model.XxlJobInfo;
import com.zjht.unified.service.ctx.TaskContext;
import com.zjht.unified.service.ctx.UnifiedEntityStatics;
import com.zjht.unified.utils.JsonUtilUnderline;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
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
            String param = JsonUtilUnderline.toJson(new DVal(ctx.getVer(), sentinel.getGuid()));
            XxlJobInfo jInfo = createJobInfo(jgid, sentinel.getCron(), SENTINEL_EXEC_METHOD, 3, ctx.getVer(), param);
            executeJob(jInfo);
        }
    }

    public void removeAlljobs(TaskContext ctx){
        Integer jgid = getJgId(ctx);
        ReturnMap<XxlJobInfo> jobList = remoteXXL.listJobInfo(0, Integer.MAX_VALUE, jgid, null, ctx.getVer());
    }

    public void createFSM(TaskContext ctx, FsmDefDO fsmDef){
        Integer jgid = getJgId(ctx);
        ReturnMap<XxlJobInfo> jobList = remoteXXL.listJobInfo(0, Integer.MAX_VALUE, jgid, FSM_EXEC_METHOD, ctx.getVer());
        if(CollectionUtils.isEmpty(jobList.getData())) {
            String param = JsonUtilUnderline.toJson(new DVal(ctx.getVer(), fsmDef.getGuid()));
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

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DVal{
        private String ver;
        private String guid;
    }

    @XxlJob(SENTINEL_EXEC_METHOD)
    public com.xxl.job.core.biz.model.ReturnT execSentinelScript(String param){
        if (StringUtils.isEmpty(param))
            param = XxlJobHelper.getJobParam();
        DVal id = JsonUtil.parse(param, DVal.class);
        TaskContext ctx = rtContextService.getRunningContext(id.getVer());
        SentinelDefDO ss = ctx.getStaticMgmt().getObject(UnifiedEntityStatics.STATIC_TYPE_SENTINEL,id.getGuid());
        scriptEngine.exec(ss.getBody(),ctx);
        return com.xxl.job.core.biz.model.ReturnT.SUCCESS;
    }

    @XxlJob(FSM_EXEC_METHOD)
    public com.xxl.job.core.biz.model.ReturnT evalFSM(String param){
        if (StringUtils.isEmpty(param))
            param = XxlJobHelper.getJobParam();
        DVal id = JsonUtil.parse(param, DVal.class);
        TaskContext ctx = rtContextService.getRunningContext(id.getVer());
        FsmDefCompositeDO ss = ctx.getStaticMgmt().getObject(UnifiedEntityStatics.STATIC_TYPE_FSM,id.getGuid());
        fsmService.evalFsm(ctx,ss);
        return com.xxl.job.core.biz.model.ReturnT.SUCCESS;
    }
}
