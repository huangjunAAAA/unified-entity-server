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
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 *  控制器
 *
 * @author wangy
 */
@Api(value = "类方法定义维护",tags = {"类方法定义维护"})
@RestController
@RequestMapping("/rt/task")
public class TaskController {

    @Autowired
    private TaskService taskService;

    @Autowired
    private RtContextService rtContextService;

    @Autowired
    private IScriptEngine scriptEngine;

    @ApiOperation(value = "启动统一实体项目")
    @PostMapping("/run-project")
    public R<String> run(@RequestBody PrjSpecDO prjSpec){
        if(StringUtils.isNotBlank(prjSpec.getCtxVer())){
            taskService.stopTask(prjSpec.getCtxVer());
        }else {
            taskService.stopTask(prjSpec.getUePrj().getId());
        }
        taskService.startTask(prjSpec,prjSpec.getCtxVer());
        return R.ok();
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

    @ApiOperation(value = "在指定运行环境增加依赖项目")
    @PostMapping("/exec-script")
    public R<Object> execScript(@RequestBody Increment<String> script){
        TaskContext ctx = rtContextService.getRunningContext(script.getVer());
        if(ctx==null){
            return R.fail("task not found:"+script.getVer());
        }
        Object ret = scriptEngine.exec(script.getData(), ctx,ctx.getPrjInfo().getPrjGuid(),ctx.getPrjInfo().getPrjVer());
        return R.ok(ret);
    }
}
