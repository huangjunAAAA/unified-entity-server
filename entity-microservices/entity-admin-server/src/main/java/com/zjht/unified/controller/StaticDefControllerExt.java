package com.zjht.unified.controller;

import com.alibaba.nacos.common.utils.CollectionUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zjht.unified.common.core.constants.FieldConstants;
import com.zjht.unified.common.core.domain.R;
import com.zjht.unified.common.core.util.UUID;
import com.zjht.unified.common.core.domain.dto.GuidDTO;
import com.zjht.unified.entity.StaticDef;
import com.zjht.unified.service.IStaticDefService;
import com.zjht.unified.utils.JsonUtilUnderline;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Api(value = "静态变量维护",tags = {"静态变量维护"})
@RestController
@RequestMapping("/staticDef")
public class StaticDefControllerExt {


    @Autowired
    private IStaticDefService staticDefService;



    @ApiOperation(value = "查询所有静态实例")
    @PostMapping("/list-all")
    public R<Object> listAll(@RequestParam("prjId") Long prjId)
    {
        List<StaticDef> ss = staticDefService.list(new LambdaQueryWrapper<StaticDef>().eq(StaticDef::getPrjId, prjId));
        Map<String, Object> r=new HashMap<>();
        if(CollectionUtils.isNotEmpty(ss)){
            ss.forEach(sss->{
                if(sss.getFieldType().equals(Integer.parseInt(FieldConstants.FIELD_TYPE_REGULAR_CLASS))){
                    r.put(sss.getFieldName(), JsonUtilUnderline.readValue(sss.getFieldValue(),Object.class));
                }else{
                    r.put(sss.getFieldName(), sss.getFieldValue());
                }
            });
        }
        return R.ok(r);
    }


    @ApiOperation(value = "新增静态实例")
    @PostMapping("/set")
    public R<Object> set(@RequestBody GuidDTO<Map<String, Object>> params)
    {
        Long prjId=params.getPrjId();
        List<StaticDef> ss = staticDefService.list(new LambdaQueryWrapper<StaticDef>().eq(StaticDef::getPrjId, prjId));
        Map<String, StaticDef> ssMap = ss.stream().collect(Collectors.toMap(StaticDef::getFieldName, Function.identity()));
        for (Iterator<Map.Entry<String, Object>> iterator = params.getData().entrySet().iterator(); iterator.hasNext(); ) {
            Map.Entry<String, Object> ssItem = iterator.next();
            StaticDef ssDef=ssMap.get(ssItem.getKey());
            if(ssDef==null){
                ssDef=new StaticDef();
                ssDef.setGuid(UUID.fastUUID().toString());
                ssDef.setPrjId(prjId);
            }
            ssDef.setFieldName(ssItem.getKey());
            if(ssItem.getValue() instanceof Map){
                ssDef.setFieldType(Integer.parseInt(FieldConstants.FIELD_TYPE_REGULAR_CLASS));
                ssDef.setFieldValue(JsonUtilUnderline.toJson(ssItem.getValue()));
            }else {
                ssDef.setFieldType(Integer.parseInt(FieldConstants.FIELD_TYPE_PRIMITIVE));
                ssDef.setFieldValue(ssItem.getValue().toString());
            }
            staticDefService.saveOrUpdate(ssDef);
        }

        return listAll(prjId);
    }

    /**
     * 删除初始实例
     */
    @ApiOperation(value = "删除初始实例")
    @PostMapping("/delete-name/")
    public R<Integer> delete(@RequestBody GuidDTO<String> fieldName)
    {
        staticDefService.remove(new LambdaQueryWrapper<StaticDef>()
                .eq(StaticDef::getFieldName, fieldName.getData())
                .eq(StaticDef::getPrjId, fieldName.getPrjId()));
        return R.ok();
    }
}
