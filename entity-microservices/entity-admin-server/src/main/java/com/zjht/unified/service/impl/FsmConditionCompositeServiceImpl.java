package com.zjht.unified.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zjht.unified.service.IFsmConditionService;

import com.zjht.unified.service.*;


import com.zjht.unified.common.core.util.ListExtractionUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Service;
import com.zjht.unified.entity.FsmCondition;
import com.zjht.unified.service.IFsmConditionCompositeService;
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
@ConditionalOnMissingBean(name = "fsmConditionCompositeServiceImplExt")
@Transactional(propagation = Propagation.REQUIRED)
public class FsmConditionCompositeServiceImpl implements IFsmConditionCompositeService {

    @Autowired
    private IFsmConditionService fsmConditionService;
  
    public Long submit(FsmConditionCompositeDTO entity) {
        if(entity==null)
            return null;
        FsmConditionCompositeValidate.preValidate(entity);
        if(entity.getId()==null){
            fsmConditionService.save(entity);
        }else{
            fsmConditionService.updateById(entity);
            FsmCondition newestEntity= fsmConditionService.getById(entity.getId());
            BeanUtils.copyProperties(newestEntity,entity);
        }
        boolean updateRequired=false;
        if(FsmConditionCompositeValidate.validateOnFlush(entity)||updateRequired)
          fsmConditionService.updateById(entity);
        return entity.getId();
    }

    public void removeById(Long id) {
        FsmConditionCompositeDTO oldEntity = selectById(id);
        if(oldEntity!=null){
        }
      fsmConditionService.removeById(id);
    }

    @Override
    public void batchRemove(List<Long> entityIdLst) {
        if(CollectionUtils.isNotEmpty(entityIdLst)){
            entityIdLst.stream().filter(t->t!=null).forEach(this::removeById);
        }
    }

    @Override
    public void batchSubmit(List<FsmConditionCompositeDTO> entityList) {
        if(CollectionUtils.isNotEmpty(entityList)){
            entityList.stream().filter(t->t!=null).forEach(this::submit);
        }
    }

    public FsmConditionCompositeDTO selectById(Long id) {
        FsmCondition fsmCondition = fsmConditionService.getById(id);
        if(fsmCondition==null)
          return null;
        FsmConditionCompositeDTO fsmConditionDTO=new FsmConditionCompositeDTO();
        BeanUtils.copyProperties(fsmCondition,fsmConditionDTO);
        return fsmConditionDTO;
    }

    @Override
    public List<FsmConditionCompositeDTO> selectList(FsmConditionCompositeDTO param) {
        List<FsmCondition> fsmConditionList = fsmConditionService.list(new QueryWrapper<>(param));
        return fsmConditionList.stream().map(t->selectById(t.getId())).collect(Collectors.toList());
    }

    @Override
    public FsmConditionCompositeDTO selectOne(FsmConditionCompositeDTO param) {
        FsmCondition fsmCondition = fsmConditionService.getOne(new QueryWrapper<>(param));
        if(fsmCondition!=null){
            return selectById(fsmCondition.getId());
        }
        return null;
    }
  
  	public FsmConditionCompositeDTO deepCopyById(Long id){
    		if(id==null)
          return null;
      	FsmConditionCompositeDTO entity=selectById(id);
        if(entity==null)
            return null;
      	return deepCopy(entity);
  	}
    public FsmConditionCompositeDTO deepCopy(FsmConditionCompositeDTO entity){      	
        FsmConditionCompositeValidate.preCopy(entity);
        entity.setOriginalId(entity.getId());
      	entity.setId(null);
        fsmConditionService.save(entity);
        if(FsmConditionCompositeValidate.validateOnCopy(entity))
          fsmConditionService.updateById(entity);
        return entity;
    }
}