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

    @ApiOperation(value = "启动统一实体项目")
    @PostMapping("/run-project")
    public R<String> run(@RequestBody PrjSpecDO prjSpec){
        Long prjId = prjSpec.getUePrj().getId();
        if(StringUtils.isNotBlank(prjSpec.getCtxVer())){
            taskService.stopTask(prjId);
        }
        TaskContext tc = rtContextService.getRunningContext(prjId);
        if(tc!=null){
            taskService.startTask(prjSpec,prjSpec.getCtxVer());
            return R.ok(prjSpec.getCtxVer());
        }else{
            return R.ok(tc.getVer());
        }

    }

    @ApiOperation(value = "停止统一实体项目")
    @PostMapping("/stop-project")
    public R<String> stop(@RequestParam Long prjId){
        taskService.stopTask(prjId);
        return R.ok();
    }

    @ApiOperation(value = "获取一个正在执行的实体项目")
    @PostMapping("/project-runtime")
    public R<TaskContext> getContext(@RequestParam Long prjId){
        TaskContext tc = rtContextService.getRunningContext(prjId);
        if(tc==null)
            return R.fail("prject not running:"+prjId);
        return R.ok(tc);
    }

    @ApiOperation(value = "列出所有正在执行的统一实体项目")
    @PostMapping("/all-project-runtime")
    public R<List<TaskContext>> getAllContext(){
        List<TaskContext> tc = rtContextService.getAllRunningContext();
        return R.ok(tc);
    }

}
