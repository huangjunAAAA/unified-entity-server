package com.zjht.unified.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zjht.unified.service.IFieldDefService;

import com.zjht.unified.service.*;


import com.zjht.unified.common.core.util.ListExtractionUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Service;
import com.zjht.unified.entity.FieldDef;
import com.zjht.unified.service.IFieldDefCompositeService;
import com.zjht.unified.dto.*;
import com.zjht.unified.wrapper.*;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Wrapper;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service业务层处理
 *
 * @author wangy
 */
@Service
@ConditionalOnMissingBean(name = "fieldDefCompositeServiceImplExt")
@Transactional(propagation = Propagation.REQUIRED)
public class FieldDefCompositeServiceImpl implements IFieldDefCompositeService {

    @Autowired
    private IFieldDefService fieldDefService;
  
    public Long submit(FieldDefCompositeDTO entity) {
        if(entity==null)
            return null;
        FieldDefCompositeValidate.preValidate(entity);
        if(entity.getId()==null){
            fieldDefService.save(entity);
        }else{
            fieldDefService.updateById(entity);
            FieldDef newestEntity= fieldDefService.getById(entity.getId());
            BeanUtils.copyProperties(newestEntity,entity);
        }
        boolean updateRequired=false;
        if(FieldDefCompositeValidate.validateOnFlush(entity)||updateRequired)
          fieldDefService.updateById(entity);
        return entity.getId();
    }

    public void removeById(Long id) {
        FieldDefCompositeDTO oldEntity = selectById(id);
        if(oldEntity!=null){
        }
      fieldDefService.removeById(id);
    }

    @Override
    public void batchRemove(List<Long> entityIdLst) {
        if(CollectionUtils.isNotEmpty(entityIdLst)){
            entityIdLst.stream().filter(t->t!=null).forEach(this::removeById);
        }
    }

    @Override
    public void batchSubmit(List<FieldDefCompositeDTO> entityList) {
        if(CollectionUtils.isNotEmpty(entityList)){
            entityList.stream().filter(t->t!=null).forEach(this::submit);
        }
    }

    public FieldDefCompositeDTO selectById(Long id) {
        FieldDef fieldDef = fieldDefService.getById(id);
        if(fieldDef==null)
          return null;
        FieldDefCompositeDTO fieldDefDTO=new FieldDefCompositeDTO();
        BeanUtils.copyProperties(fieldDef,fieldDefDTO);
        return fieldDefDTO;
    }

    @Override
    public List<FieldDefCompositeDTO> selectList(FieldDefCompositeDTO param) {
        List<FieldDef> fieldDefList = fieldDefService.list(new QueryWrapper<>(param));
        return fieldDefList.stream().map(t->selectById(t.getId())).collect(Collectors.toList());
    }

    @Override
    public FieldDefCompositeDTO selectOne(FieldDefCompositeDTO param) {
        FieldDef fieldDef = fieldDefService.getOne(new QueryWrapper<>(param));
        if(fieldDef!=null){
            return selectById(fieldDef.getId());
        }
        return null;
    }
  
  	public FieldDefCompositeDTO deepCopyById(Long id){
    		if(id==null)
          return null;
      	FieldDefCompositeDTO entity=selectById(id);
        if(entity==null)
            return null;
      	return deepCopy(entity);
  	}
    public FieldDefCompositeDTO deepCopy(FieldDefCompositeDTO entity){      	
        FieldDefCompositeValidate.preCopy(entity);
        entity.setOriginalId(entity.getId());
      	entity.setId(null);
        fieldDefService.save(entity);
        if(FieldDefCompositeValidate.validateOnCopy(entity))
          fieldDefService.updateById(entity);
        return entity;
    }
}