package com.zjht.ui.controller;


import com.zjht.unified.common.core.domain.R;
import com.zjht.ui.service.DeployService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Api(value = "项目编译与发布",tags = {"项目编译与发布"})
@RestController
@RequestMapping("/run")
@Slf4j
public class DeployController {

    @Resource
    private DeployService deployService;

    @ApiOperation(value = "编译并运行")
    @GetMapping("/compile-run")
    public R<String> compileAndRun(@RequestParam Long prjId,@RequestParam(required = false) Boolean restart)
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

    @ApiOperation(value = "编译并下载")
    @GetMapping("/download")
    public void compileAndDownload(@RequestParam Long prjId, HttpServletResponse response) {
        try {
            // 设置响应头
            response.setContentType("application/zip");
            response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=files." + prjId + ".zip");

            // 调用服务层压缩并输出到响应流
            deployService.compressProject(prjId, response.getOutputStream());
        } catch (IOException e) {
            log.error("Error occurred during project download for ID {}: {}", prjId, e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

}
