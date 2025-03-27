package com.zjht.ui.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zjht.ui.service.IUiEventHandleService;

import com.zjht.ui.service.*;


import com.zjht.unified.common.core.util.ListExtractionUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Service;
import com.zjht.ui.entity.UiEventHandle;
import com.zjht.ui.service.IUiEventHandleCompositeService;
import com.zjht.ui.dto.*;
import com.zjht.ui.wrapper.*;
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
@ConditionalOnMissingBean(name = "uiEventHandleCompositeServiceImplExt")
@Transactional(propagation = Propagation.REQUIRED)
public class UiEventHandleCompositeServiceImpl implements IUiEventHandleCompositeService {

    @Autowired
    private IUiEventHandleService uiEventHandleService;
  
    public Long submit(UiEventHandleCompositeDTO entity) {
        if(entity==null)
            return null;
        UiEventHandleCompositeValidate.preValidate(entity);
        if(entity.getId()==null){
            uiEventHandleService.save(entity);
        }else{
            uiEventHandleService.updateById(entity);
            UiEventHandle newestEntity= uiEventHandleService.getById(entity.getId());
            BeanUtils.copyProperties(newestEntity,entity);
        }
        boolean updateRequired=false;
        if(UiEventHandleCompositeValidate.validateOnFlush(entity)||updateRequired)
          uiEventHandleService.updateById(entity);
        return entity.getId();
    }

    public void removeById(Long id) {
        UiEventHandleCompositeDTO oldEntity = selectById(id);
        if(oldEntity!=null){
        }
      uiEventHandleService.removeById(id);
    }

    @Override
    public void batchRemove(List<Long> entityIdLst) {
        if(CollectionUtils.isNotEmpty(entityIdLst)){
            entityIdLst.stream().filter(t->t!=null).forEach(this::removeById);
        }
    }

    @Override
    public void batchSubmit(List<UiEventHandleCompositeDTO> entityList) {
        if(CollectionUtils.isNotEmpty(entityList)){
            entityList.stream().filter(t->t!=null).forEach(this::submit);
        }
    }

    public UiEventHandleCompositeDTO selectById(Long id) {
        UiEventHandle uiEventHandle = uiEventHandleService.getById(id);
        if(uiEventHandle==null)
          return null;
        UiEventHandleCompositeDTO uiEventHandleDTO=new UiEventHandleCompositeDTO();
        BeanUtils.copyProperties(uiEventHandle,uiEventHandleDTO);
        return uiEventHandleDTO;
    }

    @Override
    public List<UiEventHandleCompositeDTO> selectList(UiEventHandleCompositeDTO param) {
        List<UiEventHandle> uiEventHandleList = uiEventHandleService.list(new QueryWrapper<>(param));
        return uiEventHandleList.stream().map(t->selectById(t.getId())).collect(Collectors.toList());
    }

    @Override
    public UiEventHandleCompositeDTO selectOne(UiEventHandleCompositeDTO param) {
        UiEventHandle uiEventHandle = uiEventHandleService.getOne(new QueryWrapper<>(param));
        if(uiEventHandle!=null){
            return selectById(uiEventHandle.getId());
        }
        return null;
    }
  
  	public UiEventHandleCompositeDTO deepCopyById(Long id){
    		if(id==null)
          return null;
      	UiEventHandleCompositeDTO entity=selectById(id);
        if(entity==null)
            return null;
      	return deepCopy(entity);
  	}
    public UiEventHandleCompositeDTO deepCopy(UiEventHandleCompositeDTO entity){      	
        UiEventHandleCompositeValidate.preCopy(entity);
        entity.setOriginalId(entity.getId());
      	entity.setId(null);
        uiEventHandleService.save(entity);
        if(UiEventHandleCompositeValidate.validateOnCopy(entity))
          uiEventHandleService.updateById(entity);
        return entity;
    }
}