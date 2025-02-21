package com.zjht.unified.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zjht.unified.service.IFsmDefService;

import com.zjht.unified.service.*;


import com.zjht.unified.common.core.util.ListExtractionUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Service;
import com.zjht.unified.entity.FsmDef;
import com.zjht.unified.service.IFsmDefCompositeService;
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
@ConditionalOnMissingBean(name = "fsmDefCompositeServiceImplExt")
@Transactional(propagation = Propagation.REQUIRED)
public class FsmDefCompositeServiceImpl implements IFsmDefCompositeService {

    @Autowired
    private IFsmDefService fsmDefService;
    @Autowired
    private IFsmConditionCompositeService fsmConditionCompositeService;
  	@Autowired
    private IFsmConditionService fsmConditionService;
  
    public Long submit(FsmDefCompositeDTO entity) {
        if(entity==null)
            return null;
        FsmDefCompositeValidate.preValidate(entity);
        if(entity.getId()==null){
            fsmDefService.save(entity);
        }else{
            fsmDefService.updateById(entity);
            FsmDef newestEntity= fsmDefService.getById(entity.getId());
            BeanUtils.copyProperties(newestEntity,entity);
        }
        FsmDefCompositeDTO oldEntity = selectById(entity.getId());
        {
            ListExtractionUtils<FsmConditionCompositeDTO, Long> fsmConditionUtils = new ListExtractionUtils<>();
            List<FsmConditionCompositeDTO> newList = fsmConditionUtils.extractNew(entity.getFsmIdFsmConditionList(), oldEntity ==
            null ? null : oldEntity.getFsmIdFsmConditionList(), FsmConditionCompositeDTO::getId);
            List<FsmConditionCompositeDTO> updateList = fsmConditionUtils.extractUpdate(entity.getFsmIdFsmConditionList(), oldEntity ==
            null ? null : oldEntity.getFsmIdFsmConditionList(), FsmConditionCompositeDTO::getId);
            List<Long> delList = fsmConditionUtils.extractDel(entity.getFsmIdFsmConditionList(), oldEntity ==
            null ? null : oldEntity.getFsmIdFsmConditionList(), FsmConditionCompositeDTO::getId);
            if (CollectionUtils.isNotEmpty(delList)) {
                fsmConditionCompositeService.batchRemove(delList);
            }
            if (CollectionUtils.isNotEmpty(newList)) {
                newList.stream().forEach(t->{t.setFsmId(entity.getId());});
                fsmConditionCompositeService.batchSubmit(newList);
            }
            if (CollectionUtils.isNotEmpty(updateList)) {
                updateList.stream().forEach(t->{t.setFsmId(entity.getId());});
                fsmConditionCompositeService.batchSubmit(updateList);
            }
        }
        boolean updateRequired=false;
        if(FsmDefCompositeValidate.validateOnFlush(entity)||updateRequired)
          fsmDefService.updateById(entity);
        return entity.getId();
    }

    public void removeById(Long id) {
        FsmDefCompositeDTO oldEntity = selectById(id);
        if(oldEntity!=null){
            if(CollectionUtils.isNotEmpty(oldEntity.getFsmIdFsmConditionList())){
                List<Long> fsmConditionIdList = oldEntity.getFsmIdFsmConditionList().stream().map(t -> t.getId()).collect(Collectors.toList());
                fsmConditionCompositeService.batchRemove(fsmConditionIdList);
            }
        }
      fsmDefService.removeById(id);
    }

    @Override
    public void batchRemove(List<Long> entityIdLst) {
        if(CollectionUtils.isNotEmpty(entityIdLst)){
            entityIdLst.stream().filter(t->t!=null).forEach(this::removeById);
        }
    }

    @Override
    public void batchSubmit(List<FsmDefCompositeDTO> entityList) {
        if(CollectionUtils.isNotEmpty(entityList)){
            entityList.stream().filter(t->t!=null).forEach(this::submit);
        }
    }

    public FsmDefCompositeDTO selectById(Long id) {
        FsmDef fsmDef = fsmDefService.getById(id);
        if(fsmDef==null)
          return null;
        FsmDefCompositeDTO fsmDefDTO=new FsmDefCompositeDTO();
        BeanUtils.copyProperties(fsmDef,fsmDefDTO);
        FsmConditionCompositeDTO fsmConditionParam = new FsmConditionCompositeDTO();
        fsmConditionParam.setFsmId(id);
        fsmDefDTO.setFsmIdFsmConditionList(fsmConditionCompositeService.selectList(fsmConditionParam));
        return fsmDefDTO;
    }

    @Override
    public List<FsmDefCompositeDTO> selectList(FsmDefCompositeDTO param) {
        List<FsmDef> fsmDefList = fsmDefService.list(new QueryWrapper<>(param));
        return fsmDefList.stream().map(t->selectById(t.getId())).collect(Collectors.toList());
    }

    @Override
    public FsmDefCompositeDTO selectOne(FsmDefCompositeDTO param) {
        FsmDef fsmDef = fsmDefService.getOne(new QueryWrapper<>(param));
        if(fsmDef!=null){
            return selectById(fsmDef.getId());
        }
        return null;
    }
  
  	public FsmDefCompositeDTO deepCopyById(Long id){
    		if(id==null)
          return null;
      	FsmDefCompositeDTO entity=selectById(id);
        if(entity==null)
            return null;
      	return deepCopy(entity);
  	}
    public FsmDefCompositeDTO deepCopy(FsmDefCompositeDTO entity){      	
        FsmDefCompositeValidate.preCopy(entity);
        entity.setOriginalId(entity.getId());
      	entity.setId(null);
        fsmDefService.save(entity);
        {
            List<FsmConditionCompositeDTO> fsmConditionList=entity.getFsmIdFsmConditionList().stream().map(t->fsmConditionCompositeService.deepCopyById(t.getId())).collect(Collectors.toList());
            fsmConditionList.stream().forEach(t->{
              t.setFsmId(entity.getId());
              fsmConditionService.updateById(t);
            });
            entity.setFsmIdFsmConditionList(fsmConditionList);
        }
        if(FsmDefCompositeValidate.validateOnCopy(entity))
          fsmDefService.updateById(entity);
        return entity;
    }
}