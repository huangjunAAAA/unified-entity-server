package com.zjht.unified.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wukong.core.weblog.utils.StringUtil;
import com.zjht.unified.common.core.util.IdUtils;
import com.zjht.unified.service.IClazzDefService;

import com.zjht.unified.service.*;


import com.zjht.unified.common.core.util.ListExtractionUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Service;
import com.zjht.unified.entity.ClazzDef;
import com.zjht.unified.service.IClazzDefCompositeService;
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
@ConditionalOnMissingBean(name = "clazzDefCompositeServiceImplExt")
@Transactional(propagation = Propagation.REQUIRED)
public class ClazzDefCompositeServiceImpl implements IClazzDefCompositeService {

    @Autowired
    private IClazzDefService clazzDefService;
    @Autowired
    private IFieldDefCompositeService fieldDefCompositeService;
  	@Autowired
    private IFieldDefService fieldDefService;
    @Autowired
    private IMethodDefCompositeService methodDefCompositeService;
  	@Autowired
    private IMethodDefService methodDefService;
  
    public Long submit(ClazzDefCompositeDTO entity) {
        if(entity==null)
            return null;
        ClazzDefCompositeValidate.preValidate(entity);
        if(entity.getId()==null){
            if (StringUtil.isBlank(entity.getGuid())) {
                entity.setGuid(IdUtils.fastUUID());
            }
            clazzDefService.save(entity);
        }else{
            clazzDefService.updateById(entity);
            ClazzDef newestEntity= clazzDefService.getById(entity.getId());
            BeanUtils.copyProperties(newestEntity,entity);
        }
        ClazzDefCompositeDTO oldEntity = selectById(entity.getId());
        {
            ListExtractionUtils<FieldDefCompositeDTO, Long> fieldDefUtils = new ListExtractionUtils<>();
            List<FieldDefCompositeDTO> newList = fieldDefUtils.extractNew(entity.getClazzIdFieldDefList(), oldEntity ==
            null ? null : oldEntity.getClazzIdFieldDefList(), FieldDefCompositeDTO::getId);
            List<FieldDefCompositeDTO> updateList = fieldDefUtils.extractUpdate(entity.getClazzIdFieldDefList(), oldEntity ==
            null ? null : oldEntity.getClazzIdFieldDefList(), FieldDefCompositeDTO::getId);
            List<Long> delList = fieldDefUtils.extractDel(entity.getClazzIdFieldDefList(), oldEntity ==
            null ? null : oldEntity.getClazzIdFieldDefList(), FieldDefCompositeDTO::getId);
            if (CollectionUtils.isNotEmpty(delList)) {
                fieldDefCompositeService.batchRemove(delList);
            }
            if (CollectionUtils.isNotEmpty(newList)) {
                newList.stream().forEach(t->{t.setClazzId(entity.getId());});
                fieldDefCompositeService.batchSubmit(newList);
            }
            if (CollectionUtils.isNotEmpty(updateList)) {
                updateList.stream().forEach(t->{t.setClazzId(entity.getId());});
                fieldDefCompositeService.batchSubmit(updateList);
            }
        }
        {
            ListExtractionUtils<MethodDefCompositeDTO, Long> methodDefUtils = new ListExtractionUtils<>();
            List<MethodDefCompositeDTO> newList = methodDefUtils.extractNew(entity.getClazzIdMethodDefList(), oldEntity ==
            null ? null : oldEntity.getClazzIdMethodDefList(), MethodDefCompositeDTO::getId);
            List<MethodDefCompositeDTO> updateList = methodDefUtils.extractUpdate(entity.getClazzIdMethodDefList(), oldEntity ==
            null ? null : oldEntity.getClazzIdMethodDefList(), MethodDefCompositeDTO::getId);
            List<Long> delList = methodDefUtils.extractDel(entity.getClazzIdMethodDefList(), oldEntity ==
            null ? null : oldEntity.getClazzIdMethodDefList(), MethodDefCompositeDTO::getId);
            if (CollectionUtils.isNotEmpty(delList)) {
                methodDefCompositeService.batchRemove(delList);
            }
            if (CollectionUtils.isNotEmpty(newList)) {
                newList.stream().forEach(t->{t.setClazzId(entity.getId());});
                methodDefCompositeService.batchSubmit(newList);
            }
            if (CollectionUtils.isNotEmpty(updateList)) {
                updateList.stream().forEach(t->{t.setClazzId(entity.getId());});
                methodDefCompositeService.batchSubmit(updateList);
            }
        }
        boolean updateRequired=false;
        if(ClazzDefCompositeValidate.validateOnFlush(entity)||updateRequired)
          clazzDefService.updateById(entity);
        return entity.getId();
    }

    public void removeById(Long id) {
        ClazzDefCompositeDTO oldEntity = selectById(id);
        if(oldEntity!=null){
            if(CollectionUtils.isNotEmpty(oldEntity.getClazzIdFieldDefList())){
                List<Long> fieldDefIdList = oldEntity.getClazzIdFieldDefList().stream().map(t -> t.getId()).collect(Collectors.toList());
                fieldDefCompositeService.batchRemove(fieldDefIdList);
            }
            if(CollectionUtils.isNotEmpty(oldEntity.getClazzIdMethodDefList())){
                List<Long> methodDefIdList = oldEntity.getClazzIdMethodDefList().stream().map(t -> t.getId()).collect(Collectors.toList());
                methodDefCompositeService.batchRemove(methodDefIdList);
            }
        }
      clazzDefService.removeById(id);
    }

    @Override
    public void batchRemove(List<Long> entityIdLst) {
        if(CollectionUtils.isNotEmpty(entityIdLst)){
            entityIdLst.stream().filter(t->t!=null).forEach(this::removeById);
        }
    }

    @Override
    public void batchSubmit(List<ClazzDefCompositeDTO> entityList) {
        if(CollectionUtils.isNotEmpty(entityList)){
            entityList.stream().filter(t->t!=null).forEach(this::submit);
        }
    }

    public ClazzDefCompositeDTO selectById(Long id) {
        ClazzDef clazzDef = clazzDefService.getById(id);
        if(clazzDef==null)
          return null;
        ClazzDefCompositeDTO clazzDefDTO=new ClazzDefCompositeDTO();
        BeanUtils.copyProperties(clazzDef,clazzDefDTO);
        FieldDefCompositeDTO fieldDefParam = new FieldDefCompositeDTO();
        fieldDefParam.setClazzId(id);
        clazzDefDTO.setClazzIdFieldDefList(fieldDefCompositeService.selectList(fieldDefParam));
        MethodDefCompositeDTO methodDefParam = new MethodDefCompositeDTO();
        methodDefParam.setClazzId(id);
        clazzDefDTO.setClazzIdMethodDefList(methodDefCompositeService.selectList(methodDefParam));
        return clazzDefDTO;
    }

    @Override
    public List<ClazzDefCompositeDTO> selectList(ClazzDefCompositeDTO param) {
        List<ClazzDef> clazzDefList = clazzDefService.list(new QueryWrapper<>(param));
        return clazzDefList.stream().map(t->selectById(t.getId())).collect(Collectors.toList());
    }

    @Override
    public ClazzDefCompositeDTO selectOne(ClazzDefCompositeDTO param) {
        ClazzDef clazzDef = clazzDefService.getOne(new QueryWrapper<>(param));
        if(clazzDef!=null){
            return selectById(clazzDef.getId());
        }
        return null;
    }
  
  	public ClazzDefCompositeDTO deepCopyById(Long id){
    		if(id==null)
          return null;
      	ClazzDefCompositeDTO entity=selectById(id);
        if(entity==null)
            return null;
      	return deepCopy(entity);
  	}
    public ClazzDefCompositeDTO deepCopy(ClazzDefCompositeDTO entity){      	
        ClazzDefCompositeValidate.preCopy(entity);
        entity.setOriginalId(entity.getId());
      	entity.setId(null);
        clazzDefService.save(entity);
        {
            List<FieldDefCompositeDTO> fieldDefList=entity.getClazzIdFieldDefList().stream().map(t->fieldDefCompositeService.deepCopyById(t.getId())).collect(Collectors.toList());
            fieldDefList.stream().forEach(t->{
              t.setClazzId(entity.getId());
              fieldDefService.updateById(t);
            });
            entity.setClazzIdFieldDefList(fieldDefList);
        }
        {
            List<MethodDefCompositeDTO> methodDefList=entity.getClazzIdMethodDefList().stream().map(t->methodDefCompositeService.deepCopyById(t.getId())).collect(Collectors.toList());
            methodDefList.stream().forEach(t->{
              t.setClazzId(entity.getId());
              methodDefService.updateById(t);
            });
            entity.setClazzIdMethodDefList(methodDefList);
        }
        if(ClazzDefCompositeValidate.validateOnCopy(entity))
          clazzDefService.updateById(entity);
        return entity;
    }
}