package com.zjht.ui.controller;


import com.zjht.unified.common.core.constants.Constants;
import com.zjht.unified.common.core.domain.R;
import com.zjht.unified.domain.composite.PrjSpecDO;
import com.zjht.unified.feign.RemoteAdmin;
import com.zjht.unified.feign.RemoteRT;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *  控制器
 *
 * @author wangy
 */
@Api(value = "类方法定义维护",tags = {"类方法定义维护"})
@RestController
@RequestMapping("/prj")
public class PkgController {

    @Autowired
    private RemoteAdmin remoteAdmin;

    @Autowired
    private RemoteRT remoteRT;


    @ApiOperation(value = "查询类方法定义列表")
    @PostMapping("/run")
    public R<String> run(@RequestParam Long prjId){
        R<PrjSpecDO> prjSpecDOR = remoteAdmin.genPrjSpec(prjId);
        if (prjSpecDOR.getCode() == Constants.SUCCESS) {
            PrjSpecDO prjSpecDO = prjSpecDOR.getData();
            return remoteRT.startProject(prjSpecDO);
        }
        return R.fail(prjSpecDOR.getMsg());

    }



}
