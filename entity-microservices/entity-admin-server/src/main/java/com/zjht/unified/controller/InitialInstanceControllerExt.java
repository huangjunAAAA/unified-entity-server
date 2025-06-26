package com.zjht.unified.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.zjht.unified.common.core.constants.FieldConstants;
import com.zjht.unified.common.core.domain.R;
import com.zjht.unified.common.core.domain.TableDataInfo;
import com.zjht.unified.common.core.domain.dto.BaseQueryDTO;
import com.zjht.unified.common.core.domain.dto.GuidDTO;
import com.zjht.unified.common.core.util.UUID;
import com.zjht.unified.dto.*;
import com.zjht.unified.entity.ClazzDef;
import com.zjht.unified.entity.InitialInstance;
import com.zjht.unified.service.IClazzDefCompositeService;
import com.zjht.unified.service.IClazzDefService;
import com.zjht.unified.service.IInitialInstanceService;
import com.zjht.unified.utils.JsonUtilUnderline;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Api(value = "初始实例维护",tags = {"初始实例维护"})
@RestController
@RequestMapping("/initialInstance")
public class InitialInstanceControllerExt {


    @Autowired
    private IInitialInstanceService initialInstanceService;
    @Autowired
    private IClazzDefCompositeService clazzDefCompositeService;
    @Autowired
    private IClazzDefService clazzDefService;

    /**
     * 查询初始实例列表, 对象形式
     */
    @ApiOperation(value = "查询初始实例列表")
    @PostMapping("/list-ext-name")
    public TableDataInfo listName(@RequestBody BaseQueryDTO<ClassNameDTO<Object>> initialInstance)
    {
        String guid = initialInstance.getCondition().getClassGuid();
        if(guid==null&&initialInstance.getCondition().getClassName()!=null){
            ClazzDef def = clazzDefService.getOne(new LambdaQueryWrapper<ClazzDef>()
                    .eq(ClazzDef::getName, initialInstance.getCondition().getClassName())
                    .eq(ClazzDef::getPrjId, initialInstance.getCondition().getPrjId()));
            if(def!=null)
                guid=def.getGuid();
        }
        if(guid==null){
            return new TableDataInfo<>(0, "类不存在");
        }
        List<InitialInstance> records = initialInstanceService.list(new LambdaQueryWrapper<InitialInstance>()
                .eq(InitialInstance::getClassGuid, guid)
                .eq(InitialInstance::getPrjId, initialInstance.getCondition().getPrjId()));
        List<Map<String, Object>> rlst = records.stream().map(ss -> {
            Map<String, Object> validAttrs = JsonUtilUnderline.readValue(ss.getAttrValue(), new TypeReference<Map<String, Object>>() {
            });
            validAttrs.put(FieldConstants.ID, ss.getId());
            validAttrs.put(FieldConstants.GUID, ss.getGuid());
            validAttrs.put(FieldConstants.CLAZZ_GUID, ss.getClassGuid());
            return validAttrs;
        }).collect(Collectors.toList());
        int count = initialInstanceService.count(new LambdaQueryWrapper<InitialInstance>()
                .eq(InitialInstance::getClassGuid, guid)
                .eq(InitialInstance::getPrjId, initialInstance.getCondition().getPrjId()));
        TableDataInfo r = new TableDataInfo<>(rlst, count);
        return r;
    }

    /**
     * 新增初始实例
     */
    @ApiOperation(value = "新增初始实例")
    @PostMapping("create")
    public R<Object> create(@RequestBody ClassNameDTO<InitialInstance> initialInstance)
    {
        ClazzDefCompositeDTO condition=new ClazzDefCompositeDTO();
        condition.setGuid(initialInstance.getClassGuid());
        condition.setName(initialInstance.getClassName());
        ClazzDefCompositeDTO clazzDefCompositeDTO = clazzDefCompositeService.selectOne(condition);
        if(clazzDefCompositeDTO==null){
            R r = R.fail("类不存在");
            return r;
        }
        InitialInstance data = initialInstance.getData();
        Map<String, Object> attrValue = JsonUtilUnderline.readValue(data.getAttrValue(), new TypeReference<Map<String, Object>>() {});

        Map<String, FieldDefCompositeDTO> fieldMap = clazzDefCompositeDTO.getClazzIdFieldDefList().stream().collect(Collectors.toMap(FieldDefCompositeDTO::getName, Function.identity()));
        Map<String,Object> validAttrs=new HashMap<>();
        for (Iterator<String> iterator = fieldMap.keySet().iterator(); iterator.hasNext(); ) {
            String key =  iterator.next();
            Object value = attrValue.get(key);
            if(value!=null){
                validAttrs.put(key,value);
            }
        }
        data.setAttrValue(JsonUtilUnderline.toJson(validAttrs));
        data.setClassGuid(clazzDefCompositeDTO.getGuid());
        data.setClassId(clazzDefCompositeDTO.getId());
        data.setPrjId(clazzDefCompositeDTO.getPrjId());
        if(data.getGuid()==null){
            data.setGuid(UUID.fastUUID().toString());
        }
        initialInstanceService.save(data);
        validAttrs.put(FieldConstants.ID,data.getId());
        validAttrs.put(FieldConstants.GUID,data.getGuid());
        validAttrs.put(FieldConstants.CLAZZ_GUID,data.getClassGuid());
        return R.ok(validAttrs);
    }

    /**
     * 修改初始实例
     */
    @ApiOperation(value = "修改初始实例")
    @PostMapping("update")
    public R<Object> update(@RequestBody ClassNameDTO<InitialInstance> initialInstance)
    {
        ClazzDefCompositeDTO condition=new ClazzDefCompositeDTO();
        condition.setGuid(initialInstance.getClassGuid());
        condition.setName(initialInstance.getClassName());
        ClazzDefCompositeDTO clazzDefCompositeDTO = clazzDefCompositeService.selectOne(condition);
        if(clazzDefCompositeDTO==null){
            R r = R.fail("类不存在");
            return r;
        }
        InitialInstance data = initialInstance.getData();
        Map<String, Object> attrValue = JsonUtilUnderline.readValue(data.getAttrValue(), new TypeReference<Map<String, Object>>() {});

        Map<String, FieldDefCompositeDTO> fieldMap = clazzDefCompositeDTO.getClazzIdFieldDefList().stream().collect(Collectors.toMap(FieldDefCompositeDTO::getName, Function.identity()));

        InitialInstance oldEntity = initialInstanceService.getById(data.getId());
        Map<String,Object> validAttrs=JsonUtilUnderline.readValue(oldEntity.getAttrValue(), new TypeReference<Map<String, Object>>() {});
        for (Iterator<String> iterator = fieldMap.keySet().iterator(); iterator.hasNext(); ) {
            String key =  iterator.next();
            Object value = attrValue.get(key);
            if(value!=null){
                validAttrs.put(key,value);
            }
        }
        data.setAttrValue(JsonUtilUnderline.toJson(validAttrs));
        data.setClassGuid(clazzDefCompositeDTO.getGuid());
        data.setClassId(clazzDefCompositeDTO.getId());
        data.setPrjId(clazzDefCompositeDTO.getPrjId());
        if(data.getGuid()==null){
            data.setGuid(UUID.fastUUID().toString());
        }
        initialInstanceService.updateById(data);
        validAttrs.put(FieldConstants.ID,data.getId());
        validAttrs.put(FieldConstants.GUID,data.getGuid());
        validAttrs.put(FieldConstants.CLAZZ_GUID,data.getClassGuid());
        return R.ok(validAttrs);
    }

    /**
     * 删除初始实例
     */
    @ApiOperation(value = "删除初始实例")
    @PostMapping("/delete-guid/")
    public R<Integer> delete(@RequestBody GuidDTO<Object> guid)
    {
        initialInstanceService.remove(new LambdaQueryWrapper<InitialInstance>()
                .eq(InitialInstance::getGuid, guid.getGuid())
                .eq(InitialInstance::getPrjId, guid.getPrjId()));
        return R.ok();
    }
}
