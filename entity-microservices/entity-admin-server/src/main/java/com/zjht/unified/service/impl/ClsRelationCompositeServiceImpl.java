package com.zjht.unified.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zjht.unified.service.IClsRelationService;

import com.zjht.unified.service.*;


import com.zjht.unified.common.core.util.ListExtractionUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Service;
import com.zjht.unified.entity.ClsRelation;
import com.zjht.unified.service.IClsRelationCompositeService;
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
@ConditionalOnMissingBean(name = "clsRelationCompositeServiceImplExt")
@Transactional(propagation = Propagation.REQUIRED)
public class ClsRelationCompositeServiceImpl implements IClsRelationCompositeService {

    @Autowired
    private IClsRelationService clsRelationService;
  
    public Long submit(ClsRelationCompositeDTO entity) {
        if(entity==null)
            return null;
        ClsRelationCompositeValidate.preValidate(entity);
        if(entity.getId()==null){
            clsRelationService.save(entity);
        }else{
            clsRelationService.updateById(entity);
            ClsRelation newestEntity= clsRelationService.getById(entity.getId());
            BeanUtils.copyProperties(newestEntity,entity);
        }
        boolean updateRequired=false;
        if(ClsRelationCompositeValidate.validateOnFlush(entity)||updateRequired)
          clsRelationService.updateById(entity);
        return entity.getId();
    }

    public void removeById(Long id) {
        ClsRelationCompositeDTO oldEntity = selectById(id);
        if(oldEntity!=null){
        }
      clsRelationService.removeById(id);
    }

    @Override
    public void batchRemove(List<Long> entityIdLst) {
        if(CollectionUtils.isNotEmpty(entityIdLst)){
            entityIdLst.stream().filter(t->t!=null).forEach(this::removeById);
        }
    }

    @Override
    public void batchSubmit(List<ClsRelationCompositeDTO> entityList) {
        if(CollectionUtils.isNotEmpty(entityList)){
            entityList.stream().filter(t->t!=null).forEach(this::submit);
        }
    }

    public ClsRelationCompositeDTO selectById(Long id) {
        ClsRelation clsRelation = clsRelationService.getById(id);
        if(clsRelation==null)
          return null;
        ClsRelationCompositeDTO clsRelationDTO=new ClsRelationCompositeDTO();
        BeanUtils.copyProperties(clsRelation,clsRelationDTO);
        return clsRelationDTO;
    }

    @Override
    public List<ClsRelationCompositeDTO> selectList(ClsRelationCompositeDTO param) {
        List<ClsRelation> clsRelationList = clsRelationService.list(new QueryWrapper<>(param));
        return clsRelationList.stream().map(t->selectById(t.getId())).collect(Collectors.toList());
    }

    @Override
    public ClsRelationCompositeDTO selectOne(ClsRelationCompositeDTO param) {
        ClsRelation clsRelation = clsRelationService.getOne(new QueryWrapper<>(param));
        if(clsRelation!=null){
            return selectById(clsRelation.getId());
        }
        return null;
    }
  
  	public ClsRelationCompositeDTO deepCopyById(Long id){
    		if(id==null)
          return null;
      	ClsRelationCompositeDTO entity=selectById(id);
        if(entity==null)
            return null;
      	return deepCopy(entity);
  	}
    public ClsRelationCompositeDTO deepCopy(ClsRelationCompositeDTO entity){      	
        ClsRelationCompositeValidate.preCopy(entity);
        entity.setOriginalId(entity.getId());
      	entity.setId(null);
        clsRelationService.save(entity);
        if(ClsRelationCompositeValidate.validateOnCopy(entity))
          clsRelationService.updateById(entity);
        return entity;
    }
}