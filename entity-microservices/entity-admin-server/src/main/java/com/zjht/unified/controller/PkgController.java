package com.zjht.unified.controller;


import com.zjht.unified.common.core.domain.PageDomain;
import com.zjht.unified.common.core.domain.R;
import com.zjht.unified.common.core.domain.TableDataInfo;
import com.zjht.unified.common.core.domain.dto.BaseQueryDTO;
import com.zjht.unified.entity.MethodDef;
import com.zjht.unified.service.scheduling.RunService;
import com.zjht.unified.vo.MethodDefVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 *  控制器
 *
 * @author wangy
 */
@Api(value = "类方法定义维护",tags = {"类方法定义维护"})
@RestController
@RequestMapping("/prj")
public class PkgController {

    @Resource
    private RunService runService;

    @ApiOperation(value = "查询类方法定义列表")
    @PostMapping("/run")
    public R<String> run(@RequestParam Long prjId){
        return null;
    }
}
