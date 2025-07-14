package com.zjht.unified.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zjht.unified.common.core.domain.R;
import com.zjht.unified.dto.IdNameGuidDTO;
import com.zjht.unified.entity.FieldDef;
import com.zjht.unified.entity.MethodDef;
import com.zjht.unified.entity.MethodParam;
import com.zjht.unified.service.IFieldDefService;
import com.zjht.unified.service.IMethodDefService;
import com.zjht.unified.service.IMethodParamService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Api(value = "删除类中的字段或方法",tags = {"类字段与方法维护"})
@RestController
@RequestMapping("/clazzDef")
public class FieldAndMethodDelController {

    @Autowired
    private IFieldDefService fieldDefService;

    @Autowired
    private IMethodDefService methodDefService;

    @Autowired
    private IMethodParamService methodParamService;

    /**
     * 删除类字段
     */
    @ApiOperation(value = "删除类字段")
    @PostMapping("/delete/field")
    public R<String> removeFieldByName(@RequestBody IdNameGuidDTO field) {
        FieldDef f = null;
        if (field.getGuid() != null) {
            f = fieldDefService.getOne(new LambdaQueryWrapper<FieldDef>()
                    .eq(FieldDef::getGuid, field.getGuid()));

        } else {
            f = fieldDefService.getOne(new LambdaQueryWrapper<FieldDef>()
                    .eq(FieldDef::getName, field.getName())
                    .eq(FieldDef::getClazzId, field.getId()));

        }
        if (f != null) {
            fieldDefService.removeById(f.getId());
            return R.ok(f.getGuid());
        }
        return R.fail("找不到字段:"+field.getName());
    }

    /**
     * 删除类方法
     */
    @ApiOperation(value = "删除类方法")
    @PostMapping("/delete/method")
    public R<String> removeMethodByName(@RequestBody IdNameGuidDTO method)
    {
        MethodDef m = null;
        if (method.getGuid() != null) {
            m = methodDefService.getOne(new LambdaQueryWrapper<MethodDef>()
                    .eq(MethodDef::getGuid, method.getGuid()));
        } else {
            m = methodDefService.getOne(new LambdaQueryWrapper<MethodDef>()
                    .eq(MethodDef::getName, method.getName())
                    .eq(MethodDef::getClazzId, method.getId()));
        }
        if(m!=null){
            methodDefService.removeById(m.getId());
            return R.ok(m.getGuid());
        }
        return R.fail();
    }

    /**
     * 删除方法的参数
     */
    @ApiOperation(value = "删除方法的参数")
    @PostMapping("/delete/param")
    public R<String> removeParamByName(@RequestBody IdNameGuidDTO paramName)
    {
        MethodParam p = null;
        if (paramName.getGuid() != null) {
            p = methodParamService.getOne(new LambdaQueryWrapper<MethodParam>()
                    .eq(MethodParam::getGuid, paramName.getGuid()));
        } else {
            p = methodParamService.getOne(new LambdaQueryWrapper<MethodParam>()
                    .eq(MethodParam::getName, paramName.getName())
                    .eq(MethodParam::getMethodId, paramName.getId()));
        }
        if (p != null) {
            methodParamService.removeById(p.getId());
            return R.ok(p.getGuid());
        }
        return R.fail();
    }
}
