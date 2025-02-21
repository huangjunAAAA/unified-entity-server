package com.zjht.unified.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zjht.unified.service.IViewDefService;

import com.zjht.unified.service.*;


import com.zjht.unified.common.core.util.ListExtractionUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Service;
import com.zjht.unified.entity.ViewDef;
import com.zjht.unified.service.IViewDefCompositeService;
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
@ConditionalOnMissingBean(name = "viewDefCompositeServiceImplExt")
@Transactional(propagation = Propagation.REQUIRED)
public class ViewDefCompositeServiceImpl implements IViewDefCompositeService {

    @Autowired
    private IViewDefService viewDefService;
  
    public Long submit(ViewDefCompositeDTO entity) {
        if(entity==null)
            return null;
        ViewDefCompositeValidate.preValidate(entity);
        if(entity.getId()==null){
            viewDefService.save(entity);
        }else{
            viewDefService.updateById(entity);
            ViewDef newestEntity= viewDefService.getById(entity.getId());
            BeanUtils.copyProperties(newestEntity,entity);
        }
        boolean updateRequired=false;
        if(ViewDefCompositeValidate.validateOnFlush(entity)||updateRequired)
          viewDefService.updateById(entity);
        return entity.getId();
    }

    public void removeById(Long id) {
        ViewDefCompositeDTO oldEntity = selectById(id);
        if(oldEntity!=null){
        }
      viewDefService.removeById(id);
    }

    @Override
    public void batchRemove(List<Long> entityIdLst) {
        if(CollectionUtils.isNotEmpty(entityIdLst)){
            entityIdLst.stream().filter(t->t!=null).forEach(this::removeById);
        }
    }

    @Override
    public void batchSubmit(List<ViewDefCompositeDTO> entityList) {
        if(CollectionUtils.isNotEmpty(entityList)){
            entityList.stream().filter(t->t!=null).forEach(this::submit);
        }
    }

    public ViewDefCompositeDTO selectById(Long id) {
        ViewDef viewDef = viewDefService.getById(id);
        if(viewDef==null)
          return null;
        ViewDefCompositeDTO viewDefDTO=new ViewDefCompositeDTO();
        BeanUtils.copyProperties(viewDef,viewDefDTO);
        return viewDefDTO;
    }

    @Override
    public List<ViewDefCompositeDTO> selectList(ViewDefCompositeDTO param) {
        List<ViewDef> viewDefList = viewDefService.list(new QueryWrapper<>(param));
        return viewDefList.stream().map(t->selectById(t.getId())).collect(Collectors.toList());
    }

    @Override
    public ViewDefCompositeDTO selectOne(ViewDefCompositeDTO param) {
        ViewDef viewDef = viewDefService.getOne(new QueryWrapper<>(param));
        if(viewDef!=null){
            return selectById(viewDef.getId());
        }
        return null;
    }
  
  	public ViewDefCompositeDTO deepCopyById(Long id){
    		if(id==null)
          return null;
      	ViewDefCompositeDTO entity=selectById(id);
        if(entity==null)
            return null;
      	return deepCopy(entity);
  	}
    public ViewDefCompositeDTO deepCopy(ViewDefCompositeDTO entity){      	
        ViewDefCompositeValidate.preCopy(entity);
        entity.setOriginalId(entity.getId());
      	entity.setId(null);
        viewDefService.save(entity);
        if(ViewDefCompositeValidate.validateOnCopy(entity))
          viewDefService.updateById(entity);
        return entity;
    }
}