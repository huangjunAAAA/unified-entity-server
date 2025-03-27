package com.zjht.ui.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zjht.ui.service.IFilesetService;

import com.zjht.ui.service.*;


import com.zjht.unified.common.core.util.ListExtractionUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Service;
import com.zjht.ui.entity.Fileset;
import com.zjht.ui.service.IFilesetCompositeService;
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
@ConditionalOnMissingBean(name = "filesetCompositeServiceImplExt")
@Transactional(propagation = Propagation.REQUIRED)
public class FilesetCompositeServiceImpl implements IFilesetCompositeService {

    @Autowired
    private IFilesetService filesetService;
  
    public Long submit(FilesetCompositeDTO entity) {
        if(entity==null)
            return null;
        FilesetCompositeValidate.preValidate(entity);
        if(entity.getId()==null){
            filesetService.save(entity);
        }else{
            filesetService.updateById(entity);
            Fileset newestEntity= filesetService.getById(entity.getId());
            BeanUtils.copyProperties(newestEntity,entity);
        }
        boolean updateRequired=false;
        if(FilesetCompositeValidate.validateOnFlush(entity)||updateRequired)
          filesetService.updateById(entity);
        return entity.getId();
    }

    public void removeById(Long id) {
        FilesetCompositeDTO oldEntity = selectById(id);
        if(oldEntity!=null){
        }
      filesetService.removeById(id);
    }

    @Override
    public void batchRemove(List<Long> entityIdLst) {
        if(CollectionUtils.isNotEmpty(entityIdLst)){
            entityIdLst.stream().filter(t->t!=null).forEach(this::removeById);
        }
    }

    @Override
    public void batchSubmit(List<FilesetCompositeDTO> entityList) {
        if(CollectionUtils.isNotEmpty(entityList)){
            entityList.stream().filter(t->t!=null).forEach(this::submit);
        }
    }

    public FilesetCompositeDTO selectById(Long id) {
        Fileset fileset = filesetService.getById(id);
        if(fileset==null)
          return null;
        FilesetCompositeDTO filesetDTO=new FilesetCompositeDTO();
        BeanUtils.copyProperties(fileset,filesetDTO);
        return filesetDTO;
    }

    @Override
    public List<FilesetCompositeDTO> selectList(FilesetCompositeDTO param) {
        List<Fileset> filesetList = filesetService.list(new QueryWrapper<>(param));
        return filesetList.stream().map(t->selectById(t.getId())).collect(Collectors.toList());
    }

    @Override
    public FilesetCompositeDTO selectOne(FilesetCompositeDTO param) {
        Fileset fileset = filesetService.getOne(new QueryWrapper<>(param));
        if(fileset!=null){
            return selectById(fileset.getId());
        }
        return null;
    }
  
  	public FilesetCompositeDTO deepCopyById(Long id){
    		if(id==null)
          return null;
      	FilesetCompositeDTO entity=selectById(id);
        if(entity==null)
            return null;
      	return deepCopy(entity);
  	}
    public FilesetCompositeDTO deepCopy(FilesetCompositeDTO entity){      	
        FilesetCompositeValidate.preCopy(entity);
        entity.setOriginalId(entity.getId());
      	entity.setId(null);
        filesetService.save(entity);
        if(FilesetCompositeValidate.validateOnCopy(entity))
          filesetService.updateById(entity);
        return entity;
    }
}