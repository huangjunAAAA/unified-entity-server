package com.zjht.unified.controller;


import com.zjht.unified.common.core.domain.R;
import com.zjht.unified.domain.composite.ClazzDefCompositeDO;
import com.zjht.unified.domain.composite.FsmDefCompositeDO;
import com.zjht.unified.domain.composite.PrjSpecDO;
import com.zjht.unified.domain.simple.InitialInstanceDO;
import com.zjht.unified.domain.simple.SentinelDefDO;
import com.zjht.unified.domain.simple.StaticDefDO;
import com.zjht.unified.dto.Increment;
import com.zjht.unified.service.IScriptEngine;
import com.zjht.unified.service.RtContextService;
import com.zjht.unified.service.TaskService;
import com.zjht.unified.service.ctx.TaskContext;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

/**
 *  控制器
 *
 * @author wangy
 */
@Api(value = "ExecController",tags = {"ExecController"})
@RestController
@Slf4j
public class ExecController {

    @Autowired
    private IScriptEngine scriptEngine;

    @Autowired
    private TaskService taskService;

    @Autowired
    private RtContextService rtContextService;

    @ApiOperation(value = "后端脚本执行")
    @PostMapping("/backend/exec")
    public R<Object> run(@RequestBody Increment<String> script){
        log.info("version = " + script.getVer());
        log.info("script = " + script);

        TaskContext runningContext = rtContextService.getRunningContext(script.getVer());
        log.info("runningContext = " + runningContext);
        if (Objects.nonNull(runningContext)) {
            Object exec = scriptEngine.exec(script.getData(), runningContext, runningContext.getPrjInfo().getPrjGuid(), runningContext.getPrjInfo().getPrjVer());
            return R.ok(exec);
        }else{
            return R.fail("task not found:"+script.getVer());
        }
    }

    @ApiOperation(value = "在指定运行环境新增类定义")
    @PostMapping("/add-cls-def")
    public R<String> addClassDef(@RequestBody Increment<List<ClazzDefCompositeDO>> clazzDefList){
        TaskContext ctx = rtContextService.getRunningContext(clazzDefList.getVer());
        if(ctx==null){
            return R.fail("task not found:"+clazzDefList.getVer());
        }
        String prjGuid=ctx.getPrjInfo().getPrjGuid();
        String prjVer=ctx.getPrjInfo().getPrjVer();
        taskService.setClsDefs(ctx, clazzDefList.getData(), prjGuid, prjVer);
        return R.ok();
    }

    @ApiOperation(value = "在指定运行环境新增状态机")
    @PostMapping("/add-fsm")
    public R<String> addFsmDef(@RequestBody Increment<List<FsmDefCompositeDO>> fsmLst){
        TaskContext ctx = rtContextService.getRunningContext(fsmLst.getVer());
        if(ctx==null){
            return R.fail("task not found:"+fsmLst.getVer());
        }
        String prjGuid=ctx.getPrjInfo().getPrjGuid();
        String prjVer=ctx.getPrjInfo().getPrjVer();
        taskService.setFsm(ctx, fsmLst.getData(), prjGuid, prjVer);
        return R.ok();
    }

    @ApiOperation(value = "在指定运行环境新增哨兵")
    @PostMapping("/add-sentinel-def")
    public R<String> addSentinelDef(@RequestBody Increment<List<SentinelDefDO>> sentinels){
        TaskContext ctx = rtContextService.getRunningContext(sentinels.getVer());
        if(ctx==null){
            return R.fail("task not found:"+sentinels.getVer());
        }
        String prjGuid=ctx.getPrjInfo().getPrjGuid();
        String prjVer=ctx.getPrjInfo().getPrjVer();
        taskService.setSentinels(ctx, sentinels.getData(), prjGuid, prjVer);
        return R.ok();
    }

    @ApiOperation(value = "在指定运行环境设置静态变量")
    @PostMapping("/add-statics")
    public R<String> addStatics(@RequestBody Increment<List<StaticDefDO>> statics){
        TaskContext ctx = rtContextService.getRunningContext(statics.getVer());
        if(ctx==null){
            return R.fail("task not found:"+statics.getVer());
        }
        String prjGuid=ctx.getPrjInfo().getPrjGuid();
        String prjVer=ctx.getPrjInfo().getPrjVer();
        taskService.setStatics(ctx, statics.getData(), prjGuid, prjVer);

        return R.ok();
    }

    @ApiOperation(value = "在指定运行环境新增类实例")
    @PostMapping("/add-instance")
    public R<String> addInstance(@RequestBody Increment<List<InitialInstanceDO>> instances){
        TaskContext ctx = rtContextService.getRunningContext(instances.getVer());
        if(ctx==null){
            return R.fail("task not found:"+instances.getVer());
        }
        String prjGuid=ctx.getPrjInfo().getPrjGuid();
        String prjVer=ctx.getPrjInfo().getPrjVer();
        taskService.setInstances(ctx, instances.getData(), prjGuid, prjVer);

        return R.ok();
    }

    @ApiOperation(value = "在指定运行环境增加依赖项目")
    @PostMapping("/add-dep")
    public R<String> addDep(@RequestBody Increment<PrjSpecDO> spec){
        TaskContext ctx = rtContextService.getRunningContext(spec.getVer());
        if(ctx==null){
            return R.fail("task not found:"+spec.getVer());
        }
        taskService.initSpecDefinition(ctx, spec.getData());
        return R.ok();
    }
}
