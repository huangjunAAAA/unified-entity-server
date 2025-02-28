package com.zjht.unified.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zjht.unified.service.IMethodParamService;

import com.zjht.unified.service.*;


import com.zjht.unified.common.core.util.ListExtractionUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Service;
import com.zjht.unified.entity.MethodParam;
import com.zjht.unified.service.IMethodParamCompositeService;
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
@ConditionalOnMissingBean(name = "methodParamCompositeServiceImplExt")
@Transactional(propagation = Propagation.REQUIRED)
public class MethodParamCompositeServiceImpl implements IMethodParamCompositeService {

    @Autowired
    private IMethodParamService methodParamService;
  
    public Long submit(MethodParamCompositeDTO entity) {
        if(entity==null)
            return null;
        MethodParamCompositeValidate.preValidate(entity);
        if(entity.getId()==null){
            methodParamService.save(entity);
        }else{
            methodParamService.updateById(entity);
            MethodParam newestEntity= methodParamService.getById(entity.getId());
            BeanUtils.copyProperties(newestEntity,entity);
        }
        boolean updateRequired=false;
        if(MethodParamCompositeValidate.validateOnFlush(entity)||updateRequired)
          methodParamService.updateById(entity);
        return entity.getId();
    }

    public void removeById(Long id) {
        MethodParamCompositeDTO oldEntity = selectById(id);
        if(oldEntity!=null){
        }
      methodParamService.removeById(id);
    }

    @Override
    public void batchRemove(List<Long> entityIdLst) {
        if(CollectionUtils.isNotEmpty(entityIdLst)){
            entityIdLst.stream().filter(t->t!=null).forEach(this::removeById);
        }
    }

    @Override
    public void batchSubmit(List<MethodParamCompositeDTO> entityList) {
        if(CollectionUtils.isNotEmpty(entityList)){
            entityList.stream().filter(t->t!=null).forEach(this::submit);
        }
    }

    public MethodParamCompositeDTO selectById(Long id) {
        MethodParam methodParam = methodParamService.getById(id);
        if(methodParam==null)
          return null;
        MethodParamCompositeDTO methodParamDTO=new MethodParamCompositeDTO();
        BeanUtils.copyProperties(methodParam,methodParamDTO);
        return methodParamDTO;
    }

    @Override
    public List<MethodParamCompositeDTO> selectList(MethodParamCompositeDTO param) {
        List<MethodParam> methodParamList = methodParamService.list(new QueryWrapper<>(param));
        return methodParamList.stream().map(t->selectById(t.getId())).collect(Collectors.toList());
    }

    @Override
    public MethodParamCompositeDTO selectOne(MethodParamCompositeDTO param) {
        MethodParam methodParam = methodParamService.getOne(new QueryWrapper<>(param));
        if(methodParam!=null){
            return selectById(methodParam.getId());
        }
        return null;
    }
  
  	public MethodParamCompositeDTO deepCopyById(Long id){
    		if(id==null)
          return null;
      	MethodParamCompositeDTO entity=selectById(id);
        if(entity==null)
            return null;
      	return deepCopy(entity);
  	}
    public MethodParamCompositeDTO deepCopy(MethodParamCompositeDTO entity){      	
        MethodParamCompositeValidate.preCopy(entity);
        entity.setOriginalId(entity.getId());
      	entity.setId(null);
        methodParamService.save(entity);
        if(MethodParamCompositeValidate.validateOnCopy(entity))
          methodParamService.updateById(entity);
        return entity;
    }
}