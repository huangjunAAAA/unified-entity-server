package com.zjht.unified.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zjht.unified.service.ISentinelDefService;

import com.zjht.unified.service.*;


import com.zjht.unified.common.core.util.ListExtractionUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Service;
import com.zjht.unified.entity.SentinelDef;
import com.zjht.unified.service.ISentinelDefCompositeService;
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
@ConditionalOnMissingBean(name = "sentinelDefCompositeServiceImplExt")
@Transactional(propagation = Propagation.REQUIRED)
public class SentinelDefCompositeServiceImpl implements ISentinelDefCompositeService {

    @Autowired
    private ISentinelDefService sentinelDefService;
  
    public Long submit(SentinelDefCompositeDTO entity) {
        if(entity==null)
            return null;
        SentinelDefCompositeValidate.preValidate(entity);
        if(entity.getId()==null){
            sentinelDefService.save(entity);
        }else{
            sentinelDefService.updateById(entity);
            SentinelDef newestEntity= sentinelDefService.getById(entity.getId());
            BeanUtils.copyProperties(newestEntity,entity);
        }
        boolean updateRequired=false;
        if(SentinelDefCompositeValidate.validateOnFlush(entity)||updateRequired)
          sentinelDefService.updateById(entity);
        return entity.getId();
    }

    public void removeById(Long id) {
        SentinelDefCompositeDTO oldEntity = selectById(id);
        if(oldEntity!=null){
        }
      sentinelDefService.removeById(id);
    }

    @Override
    public void batchRemove(List<Long> entityIdLst) {
        if(CollectionUtils.isNotEmpty(entityIdLst)){
            entityIdLst.stream().filter(t->t!=null).forEach(this::removeById);
        }
    }

    @Override
    public void batchSubmit(List<SentinelDefCompositeDTO> entityList) {
        if(CollectionUtils.isNotEmpty(entityList)){
            entityList.stream().filter(t->t!=null).forEach(this::submit);
        }
    }

    public SentinelDefCompositeDTO selectById(Long id) {
        SentinelDef sentinelDef = sentinelDefService.getById(id);
        if(sentinelDef==null)
          return null;
        SentinelDefCompositeDTO sentinelDefDTO=new SentinelDefCompositeDTO();
        BeanUtils.copyProperties(sentinelDef,sentinelDefDTO);
        return sentinelDefDTO;
    }

    @Override
    public List<SentinelDefCompositeDTO> selectList(SentinelDefCompositeDTO param) {
        List<SentinelDef> sentinelDefList = sentinelDefService.list(new QueryWrapper<>(param));
        return sentinelDefList.stream().map(t->selectById(t.getId())).collect(Collectors.toList());
    }

    @Override
    public SentinelDefCompositeDTO selectOne(SentinelDefCompositeDTO param) {
        SentinelDef sentinelDef = sentinelDefService.getOne(new QueryWrapper<>(param));
        if(sentinelDef!=null){
            return selectById(sentinelDef.getId());
        }
        return null;
    }
  
  	public SentinelDefCompositeDTO deepCopyById(Long id){
    		if(id==null)
          return null;
      	SentinelDefCompositeDTO entity=selectById(id);
        if(entity==null)
            return null;
      	return deepCopy(entity);
  	}
    public SentinelDefCompositeDTO deepCopy(SentinelDefCompositeDTO entity){      	
        SentinelDefCompositeValidate.preCopy(entity);
        entity.setOriginalId(entity.getId());
      	entity.setId(null);
        sentinelDefService.save(entity);
        if(SentinelDefCompositeValidate.validateOnCopy(entity))
          sentinelDefService.updateById(entity);
        return entity;
    }
}