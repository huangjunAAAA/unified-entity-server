package com.zjht.ui.controller;


import com.zjht.unified.common.core.domain.R;
import com.zjht.ui.service.DeployService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@Api(value = "项目编译与发布",tags = {"项目编译与发布"})
@RestController
@RequestMapping("/run")
public class DeployController {

    @Resource
    private DeployService deployService;

    @ApiOperation(value = "编译并运行")
    @GetMapping("/compile-run")
    public R<String> compileAndRun(@RequestParam Long prjId,@RequestParam Boolean restart)
    {
        return deployService.devRun(prjId,restart);
    }

    @ApiOperation(value = "编译")
    @GetMapping("/dry-run")
    public R<String> compileOnly(@RequestParam Long prjId)
    {
        return deployService.dryRun(prjId);
    }

    @ApiOperation(value = "编译并发布")
    @GetMapping("/deploy")
    public R<String> compileAndDeploy(@RequestParam Long prjId)
    {
        return deployService.deploy(prjId);
    }
}
