package com.zjht.unified.controller;


import com.zjht.unified.common.core.domain.R;
import com.zjht.unified.domain.composite.ClazzDefCompositeDO;
import com.zjht.unified.common.core.domain.dto.GetParam;
import com.zjht.unified.service.RtContextService;
import com.zjht.unified.service.ctx.EntityDepService;
import com.zjht.unified.service.ctx.TaskContext;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(value = "运行期类规格信息提供",tags = {"运行期信息"})
@RestController
@RequestMapping("/rt/meta")
public class MetaController {

    @Autowired
    private RtContextService rtContextService;

    @Autowired
    private EntityDepService entityDepService;

    @ApiOperation(value = "在指定运行环境获取类定义对象")
    @PostMapping("/get-class-def")
    public R<ClazzDefCompositeDO> getObjectClassDef(@RequestBody GetParam param){
        TaskContext ctx = rtContextService.getRunningContext(param.getVer());
        if(ctx==null){
            return R.fail("task not found:"+param.getVer());
        }
        ClazzDefCompositeDO ret = null;
        if(param.getObjGuid()!=null)
            ret=entityDepService.getClsDefByGuid(ctx, param.getObjGuid());
        else if(param.getObjName()!=null)
            ret=entityDepService.getClsByName(ctx, param.getObjName());
        return R.ok(ret);
    }
}
