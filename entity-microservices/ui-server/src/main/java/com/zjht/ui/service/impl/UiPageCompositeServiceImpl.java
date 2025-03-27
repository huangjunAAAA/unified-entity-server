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
    private IFilesetCompositeService filesetCompositeService;
  	@Autowired
    private IFilesetService filesetService;
    @Autowired
    private IUiComponentCompositeService uiComponentCompositeService;
  	@Autowired
    private IUiComponentService uiComponentService;
  
    @Autowired
    private IUiLayoutCompositeService  uiLayoutCompositeService;
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
            ListExtractionUtils<FilesetCompositeDTO, Long> filesetUtils = new ListExtractionUtils<>();
            List<FilesetCompositeDTO> newList = filesetUtils.extractNew(entity.getBelongtoIdFilesetList(), oldEntity ==
            null ? null : oldEntity.getBelongtoIdFilesetList(), FilesetCompositeDTO::getId);
            List<FilesetCompositeDTO> updateList = filesetUtils.extractUpdate(entity.getBelongtoIdFilesetList(), oldEntity ==
            null ? null : oldEntity.getBelongtoIdFilesetList(), FilesetCompositeDTO::getId);
            List<Long> delList = filesetUtils.extractDel(entity.getBelongtoIdFilesetList(), oldEntity ==
            null ? null : oldEntity.getBelongtoIdFilesetList(), FilesetCompositeDTO::getId);
            if (CollectionUtils.isNotEmpty(delList)) {
                filesetCompositeService.batchRemove(delList);
            }
            if (CollectionUtils.isNotEmpty(newList)) {
                newList.stream().forEach(t->{t.setBelongtoId(entity.getId());});
                newList.stream().forEach(t->{t.setBelongtoType(UiPageCompositeDTO.BELONGTOID_BELONGTOTYPE_FILESET_FK);});
                filesetCompositeService.batchSubmit(newList);
            }
            if (CollectionUtils.isNotEmpty(updateList)) {
                updateList.stream().forEach(t->{t.setBelongtoId(entity.getId());});
                updateList.stream().forEach(t->{t.setBelongtoType(UiPageCompositeDTO.BELONGTOID_BELONGTOTYPE_FILESET_FK);});
                filesetCompositeService.batchSubmit(updateList);
            }
        }
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
        if(entity.getLayoutIdUiLayoutComposite()!=null){
            if(entity.getLayoutId()!=null)
                entity.getLayoutIdUiLayoutComposite().setId(entity.getLayoutId());
            uiLayoutCompositeService.submit(entity.getLayoutIdUiLayoutComposite());
           entity.setLayoutId(entity.getLayoutIdUiLayoutComposite().getId());
           updateRequired=true;
        }
        if(UiPageCompositeValidate.validateOnFlush(entity)||updateRequired)
          uiPageService.updateById(entity);
        return entity.getId();
    }

    public void removeById(Long id) {
        UiPageCompositeDTO oldEntity = selectById(id);
        if(oldEntity!=null){
            if(CollectionUtils.isNotEmpty(oldEntity.getBelongtoIdFilesetList())){
                List<Long> filesetIdList = oldEntity.getBelongtoIdFilesetList().stream().map(t -> t.getId()).collect(Collectors.toList());
                filesetCompositeService.batchRemove(filesetIdList);
            }
            if(CollectionUtils.isNotEmpty(oldEntity.getPageIdUiComponentList())){
                List<Long> uiComponentIdList = oldEntity.getPageIdUiComponentList().stream().map(t -> t.getId()).collect(Collectors.toList());
                uiComponentCompositeService.batchRemove(uiComponentIdList);
            }
            if(oldEntity.getLayoutIdUiLayoutComposite()!=null)
                uiLayoutCompositeService.removeById(oldEntity.getLayoutIdUiLayoutComposite().getId());
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
        FilesetCompositeDTO filesetParam = new FilesetCompositeDTO();
        filesetParam.setBelongtoId(id);
        filesetParam.setBelongtoType(UiPageCompositeDTO.BELONGTOID_BELONGTOTYPE_FILESET_FK);
        uiPageDTO.setBelongtoIdFilesetList(filesetCompositeService.selectList(filesetParam));
        UiComponentCompositeDTO uiComponentParam = new UiComponentCompositeDTO();
        uiComponentParam.setPageId(id);
        uiPageDTO.setPageIdUiComponentList(uiComponentCompositeService.selectList(uiComponentParam));
        uiPageDTO.setLayoutIdUiLayoutComposite(uiLayoutCompositeService.selectById(uiPage.getLayoutId()));
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
            List<FilesetCompositeDTO> filesetList=entity.getBelongtoIdFilesetList().stream().map(t->filesetCompositeService.deepCopyById(t.getId())).collect(Collectors.toList());
            filesetList.stream().forEach(t->{
              t.setBelongtoId(entity.getId());
              filesetService.updateById(t);
              t.setBelongtoType(UiPageCompositeDTO.BELONGTOID_BELONGTOTYPE_FILESET_FK);
            });
            entity.setBelongtoIdFilesetList(filesetList);
        }
        {
            List<UiComponentCompositeDTO> uiComponentList=entity.getPageIdUiComponentList().stream().map(t->uiComponentCompositeService.deepCopyById(t.getId())).collect(Collectors.toList());
            uiComponentList.stream().forEach(t->{
              t.setPageId(entity.getId());
              uiComponentService.updateById(t);
            });
            entity.setPageIdUiComponentList(uiComponentList);
        }
      if(entity.getLayoutIdUiLayoutComposite()!=null)	{
        entity.setLayoutIdUiLayoutComposite(uiLayoutCompositeService.deepCopy(entity.getLayoutIdUiLayoutComposite()));
        entity.setLayoutId(entity.getLayoutIdUiLayoutComposite().getId());      
      }
        uiPageService.updateById(entity);
        if(UiPageCompositeValidate.validateOnCopy(entity))
          uiPageService.updateById(entity);
        return entity;
    }
}