package com.zjht.ui.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zjht.ui.service.IUiPrjService;

import com.zjht.ui.service.*;


import com.zjht.unified.common.core.util.ListExtractionUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Service;
import com.zjht.ui.entity.UiPrj;
import com.zjht.ui.service.IUiPrjCompositeService;
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
@ConditionalOnMissingBean(name = "uiPrjCompositeServiceImplExt")
@Transactional(propagation = Propagation.REQUIRED)
public class UiPrjCompositeServiceImpl implements IUiPrjCompositeService {

    @Autowired
    private IUiPrjService uiPrjService;
    @Autowired
    private IUiPageCompositeService uiPageCompositeService;
  	@Autowired
    private IUiPageService uiPageService;
  
    @Autowired
    private IGitStoreCompositeService  gitStoreCompositeService;
    public Long submit(UiPrjCompositeDTO entity) {
        if(entity==null)
            return null;
        UiPrjCompositeValidate.preValidate(entity);
        if(entity.getId()==null){
            uiPrjService.save(entity);
        }else{
            uiPrjService.updateById(entity);
            UiPrj newestEntity= uiPrjService.getById(entity.getId());
            BeanUtils.copyProperties(newestEntity,entity);
        }
        UiPrjCompositeDTO oldEntity = selectById(entity.getId());
        {
            ListExtractionUtils<UiPageCompositeDTO, Long> uiPageUtils = new ListExtractionUtils<>();
            List<UiPageCompositeDTO> newList = uiPageUtils.extractNew(entity.getRprjIdUiPageList(), oldEntity ==
            null ? null : oldEntity.getRprjIdUiPageList(), UiPageCompositeDTO::getId);
            List<UiPageCompositeDTO> updateList = uiPageUtils.extractUpdate(entity.getRprjIdUiPageList(), oldEntity ==
            null ? null : oldEntity.getRprjIdUiPageList(), UiPageCompositeDTO::getId);
            List<Long> delList = uiPageUtils.extractDel(entity.getRprjIdUiPageList(), oldEntity ==
            null ? null : oldEntity.getRprjIdUiPageList(), UiPageCompositeDTO::getId);
            if (CollectionUtils.isNotEmpty(delList)) {
                uiPageCompositeService.batchRemove(delList);
            }
            if (CollectionUtils.isNotEmpty(newList)) {
                newList.stream().forEach(t->{t.setRprjId(entity.getId());});
                uiPageCompositeService.batchSubmit(newList);
            }
            if (CollectionUtils.isNotEmpty(updateList)) {
                updateList.stream().forEach(t->{t.setRprjId(entity.getId());});
                uiPageCompositeService.batchSubmit(updateList);
            }
        }
        boolean updateRequired=false;
        if(entity.getGitIdGitStoreComposite()!=null){
            if(entity.getGitId()!=null)
                entity.getGitIdGitStoreComposite().setId(entity.getGitId());
            gitStoreCompositeService.submit(entity.getGitIdGitStoreComposite());
           entity.setGitId(entity.getGitIdGitStoreComposite().getId());
           updateRequired=true;
        }
        if(UiPrjCompositeValidate.validateOnFlush(entity)||updateRequired)
          uiPrjService.updateById(entity);
        return entity.getId();
    }

    public void removeById(Long id) {
        UiPrjCompositeDTO oldEntity = selectById(id);
        if(oldEntity!=null){
            if(CollectionUtils.isNotEmpty(oldEntity.getRprjIdUiPageList())){
                List<Long> uiPageIdList = oldEntity.getRprjIdUiPageList().stream().map(t -> t.getId()).collect(Collectors.toList());
                uiPageCompositeService.batchRemove(uiPageIdList);
            }
            if(oldEntity.getGitIdGitStoreComposite()!=null)
                gitStoreCompositeService.removeById(oldEntity.getGitIdGitStoreComposite().getId());
        }
      uiPrjService.removeById(id);
    }

    @Override
    public void batchRemove(List<Long> entityIdLst) {
        if(CollectionUtils.isNotEmpty(entityIdLst)){
            entityIdLst.stream().filter(t->t!=null).forEach(this::removeById);
        }
    }

    @Override
    public void batchSubmit(List<UiPrjCompositeDTO> entityList) {
        if(CollectionUtils.isNotEmpty(entityList)){
            entityList.stream().filter(t->t!=null).forEach(this::submit);
        }
    }

    public UiPrjCompositeDTO selectById(Long id) {
        UiPrj uiPrj = uiPrjService.getById(id);
        if(uiPrj==null)
          return null;
        UiPrjCompositeDTO uiPrjDTO=new UiPrjCompositeDTO();
        BeanUtils.copyProperties(uiPrj,uiPrjDTO);
        UiPageCompositeDTO uiPageParam = new UiPageCompositeDTO();
        uiPageParam.setRprjId(id);
        uiPrjDTO.setRprjIdUiPageList(uiPageCompositeService.selectList(uiPageParam));
        uiPrjDTO.setGitIdGitStoreComposite(gitStoreCompositeService.selectById(uiPrj.getGitId()));
        return uiPrjDTO;
    }

    @Override
    public List<UiPrjCompositeDTO> selectList(UiPrjCompositeDTO param) {
        List<UiPrj> uiPrjList = uiPrjService.list(new QueryWrapper<>(param));
        return uiPrjList.stream().map(t->selectById(t.getId())).collect(Collectors.toList());
    }

    @Override
    public UiPrjCompositeDTO selectOne(UiPrjCompositeDTO param) {
        UiPrj uiPrj = uiPrjService.getOne(new QueryWrapper<>(param));
        if(uiPrj!=null){
            return selectById(uiPrj.getId());
        }
        return null;
    }
  
  	public UiPrjCompositeDTO deepCopyById(Long id){
    		if(id==null)
          return null;
      	UiPrjCompositeDTO entity=selectById(id);
        if(entity==null)
            return null;
      	return deepCopy(entity);
  	}
    public UiPrjCompositeDTO deepCopy(UiPrjCompositeDTO entity){      	
        UiPrjCompositeValidate.preCopy(entity);
        entity.setOriginalId(entity.getId());
      	entity.setId(null);
        uiPrjService.save(entity);
        {
            List<UiPageCompositeDTO> uiPageList=entity.getRprjIdUiPageList().stream().map(t->uiPageCompositeService.deepCopyById(t.getId())).collect(Collectors.toList());
            uiPageList.stream().forEach(t->{
              t.setRprjId(entity.getId());
              uiPageService.updateById(t);
            });
            entity.setRprjIdUiPageList(uiPageList);
        }
      if(entity.getGitIdGitStoreComposite()!=null)	{
        entity.setGitIdGitStoreComposite(gitStoreCompositeService.deepCopy(entity.getGitIdGitStoreComposite()));
        entity.setGitId(entity.getGitIdGitStoreComposite().getId());      
      }
        uiPrjService.updateById(entity);
        if(UiPrjCompositeValidate.validateOnCopy(entity))
          uiPrjService.updateById(entity);
        return entity;
    }
}