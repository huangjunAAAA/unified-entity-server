package com.zjht.unified.controller;


import com.zjht.unified.common.core.domain.PageDomain;
import com.zjht.unified.common.core.domain.R;
import com.zjht.unified.common.core.domain.TableDataInfo;
import com.zjht.unified.common.core.domain.dto.BaseQueryDTO;
import com.zjht.unified.domain.composite.PrjSpecDO;
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
@Api(value = "运行统一实体项目",tags = {"运行统一实体项目"})
@RestController
@RequestMapping("/prj")
public class PkgController {

    @Resource
    private RunService runService;

    @ApiOperation(value = "运行特定统一实体项目")
    @PostMapping("/run")
    public R<String> run(@RequestParam Long prjId){
        return runService.runProject(prjId);
    }


    @ApiOperation(value = "生产项目说明数据结构")
    @PostMapping("/genPrjSpec")
    public R<PrjSpecDO> genPrjSpec(@RequestParam Long prjId) {
        return R.ok(runService.genPrjSpec(prjId));
    }
}
