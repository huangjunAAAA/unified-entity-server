package com.zjht.unified.controller;


import com.zjht.unified.common.core.domain.R;
import com.zjht.unified.domain.composite.PrjSpecDO;
import com.zjht.unified.service.TaskService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

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

    @ApiOperation(value = "查询类方法定义列表")
    @PostMapping("/run-project")
    public R<String> run(@RequestBody PrjSpecDO prjSpec){
        taskService.stopTask(prjSpec.getUePrj().getId());
        taskService.startTask(prjSpec);
        return R.ok();
    }
}
