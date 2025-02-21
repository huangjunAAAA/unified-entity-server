package com.zjht.unified.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zjht.unified.service.IMethodDefService;

import com.zjht.unified.service.*;


import com.zjht.unified.common.core.util.ListExtractionUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Service;
import com.zjht.unified.entity.MethodDef;
import com.zjht.unified.service.IMethodDefCompositeService;
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
@ConditionalOnMissingBean(name = "methodDefCompositeServiceImplExt")
@Transactional(propagation = Propagation.REQUIRED)
public class MethodDefCompositeServiceImpl implements IMethodDefCompositeService {

    @Autowired
    private IMethodDefService methodDefService;
  
    public Long submit(MethodDefCompositeDTO entity) {
        if(entity==null)
            return null;
        MethodDefCompositeValidate.preValidate(entity);
        if(entity.getId()==null){
            methodDefService.save(entity);
        }else{
            methodDefService.updateById(entity);
            MethodDef newestEntity= methodDefService.getById(entity.getId());
            BeanUtils.copyProperties(newestEntity,entity);
        }
        boolean updateRequired=false;
        if(MethodDefCompositeValidate.validateOnFlush(entity)||updateRequired)
          methodDefService.updateById(entity);
        return entity.getId();
    }

    public void removeById(Long id) {
        MethodDefCompositeDTO oldEntity = selectById(id);
        if(oldEntity!=null){
        }
      methodDefService.removeById(id);
    }

    @Override
    public void batchRemove(List<Long> entityIdLst) {
        if(CollectionUtils.isNotEmpty(entityIdLst)){
            entityIdLst.stream().filter(t->t!=null).forEach(this::removeById);
        }
    }

    @Override
    public void batchSubmit(List<MethodDefCompositeDTO> entityList) {
        if(CollectionUtils.isNotEmpty(entityList)){
            entityList.stream().filter(t->t!=null).forEach(this::submit);
        }
    }

    public MethodDefCompositeDTO selectById(Long id) {
        MethodDef methodDef = methodDefService.getById(id);
        if(methodDef==null)
          return null;
        MethodDefCompositeDTO methodDefDTO=new MethodDefCompositeDTO();
        BeanUtils.copyProperties(methodDef,methodDefDTO);
        return methodDefDTO;
    }

    @Override
    public List<MethodDefCompositeDTO> selectList(MethodDefCompositeDTO param) {
        List<MethodDef> methodDefList = methodDefService.list(new QueryWrapper<>(param));
        return methodDefList.stream().map(t->selectById(t.getId())).collect(Collectors.toList());
    }

    @Override
    public MethodDefCompositeDTO selectOne(MethodDefCompositeDTO param) {
        MethodDef methodDef = methodDefService.getOne(new QueryWrapper<>(param));
        if(methodDef!=null){
            return selectById(methodDef.getId());
        }
        return null;
    }
  
  	public MethodDefCompositeDTO deepCopyById(Long id){
    		if(id==null)
          return null;
      	MethodDefCompositeDTO entity=selectById(id);
        if(entity==null)
            return null;
      	return deepCopy(entity);
  	}
    public MethodDefCompositeDTO deepCopy(MethodDefCompositeDTO entity){      	
        MethodDefCompositeValidate.preCopy(entity);
        entity.setOriginalId(entity.getId());
      	entity.setId(null);
        methodDefService.save(entity);
        if(MethodDefCompositeValidate.validateOnCopy(entity))
          methodDefService.updateById(entity);
        return entity;
    }
}