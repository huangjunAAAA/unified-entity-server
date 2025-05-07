package com.zjht.unified.controller;


import com.zjht.unified.common.core.domain.R;
import com.zjht.unified.domain.composite.PrjSpecDO;
import com.zjht.unified.service.IScriptEngine;
import com.zjht.unified.service.RtContextService;
import com.zjht.unified.service.TaskService;
import com.zjht.unified.service.ctx.TaskContext;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

/**
 *  控制器
 *
 * @author wangy
 */
@Api(value = "ExecController",tags = {"ExecController"})
@RestController
public class ExecController {

    @Autowired
    private IScriptEngine scriptEngine;

    @Autowired
    private RtContextService rtContextService;

    @ApiOperation(value = "后端脚本执行")
    @PostMapping("/backend/exec")
    public R<Object> run(@RequestParam String version,@RequestParam String script){
        System.out.println("version = " + version);
        System.out.println("script = " + script);

        TaskContext runningContext = rtContextService.getRunningContext(version);
        System.out.println("runningContext = " + runningContext);
        if (Objects.nonNull(runningContext)) {
            Object exec = scriptEngine.exec(script, runningContext);
            return R.ok(exec);
        }

        return R.ok();
    }
}
