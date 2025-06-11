package com.zjht.unified.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zjht.unified.common.core.domain.R;
import com.zjht.unified.dto.IdAndNameDTO;
import com.zjht.unified.entity.FieldDef;
import com.zjht.unified.entity.MethodDef;
import com.zjht.unified.service.IFieldDefService;
import com.zjht.unified.service.IMethodDefService;
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

    /**
     * 删除类字段
     */
    @ApiOperation(value = "删除类字段")
    @PostMapping("/delete/field")
    public R<Long> removeFieldByName(@RequestBody IdAndNameDTO field)
    {
        FieldDef f = fieldDefService.getOne(new LambdaQueryWrapper<FieldDef>()
                .eq(FieldDef::getName, field.getName())
                .eq(FieldDef::getClazzId, field.getId()));
        if(f!=null){
            fieldDefService.removeById(f.getId());
            return R.ok(f.getId());
        }
        return R.fail();
    }

    /**
     * 删除类方法
     */
    @ApiOperation(value = "删除类方法")
    @PostMapping("/delete/method")
    public R<Long> removeMethodByName(@RequestBody  IdAndNameDTO method)
    {
        MethodDef m = methodDefService.getOne(new LambdaQueryWrapper<MethodDef>()
                .eq(MethodDef::getName, method.getName())
                .eq(MethodDef::getClazzId, method.getId()));
        if(m!=null){
            methodDefService.removeById(m.getId());
            return R.ok(m.getId());
        }
        return R.fail();
    }
}
