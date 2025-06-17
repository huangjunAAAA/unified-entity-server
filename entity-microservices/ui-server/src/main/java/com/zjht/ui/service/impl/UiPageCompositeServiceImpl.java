package com.zjht.ui.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zjht.ui.service.IUiPageService;

import com.zjht.ui.service.*;


import com.zjht.unified.common.core.util.ListExtractionUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Service;
import com.zjht.ui.entity.UiPage;
import com.zjht.ui.service.IUiPageCompositeService;
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
@ConditionalOnMissingBean(name = "uiPageCompositeServiceImplExt")
@Transactional(propagation = Propagation.REQUIRED)
public class UiPageCompositeServiceImpl implements IUiPageCompositeService {

    @Autowired
    private IUiPageService uiPageService;
    @Autowired
    private IUiComponentCompositeService uiComponentCompositeService;
  	@Autowired
    private IUiComponentService uiComponentService;
  
    public Long submit(UiPageCompositeDTO entity) {
        if(entity==null)
            return null;
        UiPageCompositeValidate.preValidate(entity);
        if(entity.getId()==null){
            uiPageService.save(entity);
        }else{
            uiPageService.updateById(entity);
            UiPage newestEntity= uiPageService.getById(entity.getId());
            BeanUtils.copyProperties(newestEntity,entity);
        }
        UiPageCompositeDTO oldEntity = selectById(entity.getId());
        {
            ListExtractionUtils<UiComponentCompositeDTO, Long> uiComponentUtils = new ListExtractionUtils<>();
            List<UiComponentCompositeDTO> newList = uiComponentUtils.extractNew(entity.getPageIdUiComponentList(), oldEntity ==
            null ? null : oldEntity.getPageIdUiComponentList(), UiComponentCompositeDTO::getId);
            List<UiComponentCompositeDTO> updateList = uiComponentUtils.extractUpdate(entity.getPageIdUiComponentList(), oldEntity ==
            null ? null : oldEntity.getPageIdUiComponentList(), UiComponentCompositeDTO::getId);
            List<Long> delList = uiComponentUtils.extractDel(entity.getPageIdUiComponentList(), oldEntity ==
            null ? null : oldEntity.getPageIdUiComponentList(), UiComponentCompositeDTO::getId);
            if (CollectionUtils.isNotEmpty(delList)) {
                uiComponentCompositeService.batchRemove(delList);
            }
            if (CollectionUtils.isNotEmpty(newList)) {
                newList.stream().forEach(t->{t.setPageId(entity.getId());});
                uiComponentCompositeService.batchSubmit(newList);
            }
            if (CollectionUtils.isNotEmpty(updateList)) {
                updateList.stream().forEach(t->{t.setPageId(entity.getId());});
                uiComponentCompositeService.batchSubmit(updateList);
            }
        }
        boolean updateRequired=false;
        if(UiPageCompositeValidate.validateOnFlush(entity)||updateRequired)
          uiPageService.updateById(entity);
        return entity.getId();
    }

    public void removeById(Long id) {
        UiPageCompositeDTO oldEntity = selectById(id);
        if(oldEntity!=null){
            if(CollectionUtils.isNotEmpty(oldEntity.getPageIdUiComponentList())){
                List<Long> uiComponentIdList = oldEntity.getPageIdUiComponentList().stream().map(t -> t.getId()).collect(Collectors.toList());
                uiComponentCompositeService.batchRemove(uiComponentIdList);
            }
        }
      uiPageService.removeById(id);
    }

    @Override
    public void batchRemove(List<Long> entityIdLst) {
        if(CollectionUtils.isNotEmpty(entityIdLst)){
            entityIdLst.stream().filter(t->t!=null).forEach(this::removeById);
        }
    }

    @Override
    public void batchSubmit(List<UiPageCompositeDTO> entityList) {
        if(CollectionUtils.isNotEmpty(entityList)){
            entityList.stream().filter(t->t!=null).forEach(this::submit);
        }
    }

    public UiPageCompositeDTO selectById(Long id) {
        UiPage uiPage = uiPageService.getById(id);
        if(uiPage==null)
          return null;
        UiPageCompositeDTO uiPageDTO=new UiPageCompositeDTO();
        BeanUtils.copyProperties(uiPage,uiPageDTO);
        UiComponentCompositeDTO uiComponentParam = new UiComponentCompositeDTO();
        uiComponentParam.setPageId(id);
        uiPageDTO.setPageIdUiComponentList(uiComponentCompositeService.selectList(uiComponentParam));
        return uiPageDTO;
    }

    @Override
    public List<UiPageCompositeDTO> selectList(UiPageCompositeDTO param) {
        List<UiPage> uiPageList = uiPageService.list(new QueryWrapper<>(param));
        return uiPageList.stream().map(t->selectById(t.getId())).collect(Collectors.toList());
    }

    @Override
    public UiPageCompositeDTO selectOne(UiPageCompositeDTO param) {
        UiPage uiPage = uiPageService.getOne(new QueryWrapper<>(param));
        if(uiPage!=null){
            return selectById(uiPage.getId());
        }
        return null;
    }
  
  	public UiPageCompositeDTO deepCopyById(Long id){
    		if(id==null)
          return null;
      	UiPageCompositeDTO entity=selectById(id);
        if(entity==null)
            return null;
      	return deepCopy(entity);
  	}
    public UiPageCompositeDTO deepCopy(UiPageCompositeDTO entity){      	
        UiPageCompositeValidate.preCopy(entity);
        entity.setOriginalId(entity.getId());
      	entity.setId(null);
        uiPageService.save(entity);
        {
            List<UiComponentCompositeDTO> uiComponentList=entity.getPageIdUiComponentList().stream().map(t->uiComponentCompositeService.deepCopyById(t.getId())).collect(Collectors.toList());
            uiComponentList.stream().forEach(t->{
              t.setPageId(entity.getId());
              uiComponentService.updateById(t);
            });
            entity.setPageIdUiComponentList(uiComponentList);
        }
        if(UiPageCompositeValidate.validateOnCopy(entity))
          uiPageService.updateById(entity);
        return entity;
    }
}