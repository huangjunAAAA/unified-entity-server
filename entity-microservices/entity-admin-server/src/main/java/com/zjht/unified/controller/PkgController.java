package com.zjht.unified.controller;


import com.zjht.unified.common.core.constants.Constants;
import com.zjht.unified.common.core.domain.PageDomain;
import com.zjht.unified.common.core.domain.R;
import com.zjht.unified.common.core.domain.TableDataInfo;
import com.zjht.unified.common.core.domain.dto.BaseQueryDTO;
import com.zjht.unified.domain.composite.PrjSpecDO;
import com.zjht.unified.domain.simple.UiPrjDO;
import com.zjht.unified.entity.MethodDef;
import com.zjht.unified.entity.UePrj;
import com.zjht.unified.feign.RemoteUIAdmin;
import com.zjht.unified.service.IUePrjService;
import com.zjht.unified.service.scheduling.RunService;
import com.zjht.unified.vo.MethodDefVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Objects;

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

    @Resource
    private IUePrjService uePrjService;

    @Resource
    private RemoteUIAdmin uiAdmin;

    @ApiOperation(value = "运行特定统一实体项目")
    @PostMapping("/run")
    public R<String> run(@RequestParam Long prjId,@RequestParam(required = false) String version,@RequestParam(required = false) boolean force){
        if(force&&StringUtils.isBlank(version)){
            version="prj_"+prjId+"_"+System.currentTimeMillis();
        }
        return runService.runProject(prjId,version);
    }


    @ApiOperation(value = "生产项目说明数据结构")
    @PostMapping("/genPrjSpec")
    public R<PrjSpecDO> genPrjSpec(@RequestParam Long prjId) {
        return R.ok(runService.genPrjSpec(prjId));
    }

    @ApiOperation(value = "联系UE项目和UI项目")
    @PostMapping("/link-prj")
    public R linkPrjs(@RequestParam Long ueId,@RequestParam Long uiId){
        UePrj uePrj = uePrjService.getById(ueId);
        uePrj.setUiPrjId(uiId);


        R<UiPrjDO> r = uiAdmin.getPrjInfoById(uiId);
        if(r.getData()!=null){
            UiPrjDO uiPrj = r.getData();
            uiPrj.setExternalId(ueId+"");
            if(Objects.equals(uePrj.getTemplate()+"", Constants.YES)){
                uiPrj.setExternalType("ue");
            }else{
                uiPrj.setExternalType("ue_template");
            }
            uiAdmin.editPrjInfo(uiPrj);
            uePrjService.updateById(uePrj);
        }
        return R.ok();
    }
}
