package com.zjht.ui.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zjht.ui.service.IUiComponentService;

import com.zjht.ui.service.*;


import com.zjht.unified.common.core.util.ListExtractionUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Service;
import com.zjht.ui.entity.UiComponent;
import com.zjht.ui.service.IUiComponentCompositeService;
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
@ConditionalOnMissingBean(name = "uiComponentCompositeServiceImplExt")
@Transactional(propagation = Propagation.REQUIRED)
public class UiComponentCompositeServiceImpl implements IUiComponentCompositeService {

    @Autowired
    private IUiComponentService uiComponentService;
    @Autowired
    private IFilesetCompositeService filesetCompositeService;
  	@Autowired
    private IFilesetService filesetService;
    @Autowired
    private IUiEventHandleCompositeService uiEventHandleCompositeService;
  	@Autowired
    private IUiEventHandleService uiEventHandleService;
  
    public Long submit(UiComponentCompositeDTO entity) {
        if(entity==null)
            return null;
        UiComponentCompositeValidate.preValidate(entity);
        if(entity.getId()==null){
            uiComponentService.save(entity);
        }else{
            uiComponentService.updateById(entity);
            UiComponent newestEntity= uiComponentService.getById(entity.getId());
            BeanUtils.copyProperties(newestEntity,entity);
        }
        UiComponentCompositeDTO oldEntity = selectById(entity.getId());
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
                newList.stream().forEach(t->{t.setBelongtoType(UiComponentCompositeDTO.BELONGTOID_BELONGTOTYPE_FILESET_FK);});
                filesetCompositeService.batchSubmit(newList);
            }
            if (CollectionUtils.isNotEmpty(updateList)) {
                updateList.stream().forEach(t->{t.setBelongtoId(entity.getId());});
                updateList.stream().forEach(t->{t.setBelongtoType(UiComponentCompositeDTO.BELONGTOID_BELONGTOTYPE_FILESET_FK);});
                filesetCompositeService.batchSubmit(updateList);
            }
        }
        {
            ListExtractionUtils<UiEventHandleCompositeDTO, Long> uiEventHandleUtils = new ListExtractionUtils<>();
            List<UiEventHandleCompositeDTO> newList = uiEventHandleUtils.extractNew(entity.getComponentIdUiEventHandleList(), oldEntity ==
            null ? null : oldEntity.getComponentIdUiEventHandleList(), UiEventHandleCompositeDTO::getId);
            List<UiEventHandleCompositeDTO> updateList = uiEventHandleUtils.extractUpdate(entity.getComponentIdUiEventHandleList(), oldEntity ==
            null ? null : oldEntity.getComponentIdUiEventHandleList(), UiEventHandleCompositeDTO::getId);
            List<Long> delList = uiEventHandleUtils.extractDel(entity.getComponentIdUiEventHandleList(), oldEntity ==
            null ? null : oldEntity.getComponentIdUiEventHandleList(), UiEventHandleCompositeDTO::getId);
            if (CollectionUtils.isNotEmpty(delList)) {
                uiEventHandleCompositeService.batchRemove(delList);
            }
            if (CollectionUtils.isNotEmpty(newList)) {
                newList.stream().forEach(t->{t.setComponentId(entity.getId());});
                uiEventHandleCompositeService.batchSubmit(newList);
            }
            if (CollectionUtils.isNotEmpty(updateList)) {
                updateList.stream().forEach(t->{t.setComponentId(entity.getId());});
                uiEventHandleCompositeService.batchSubmit(updateList);
            }
        }
        boolean updateRequired=false;
        if(UiComponentCompositeValidate.validateOnFlush(entity)||updateRequired)
          uiComponentService.updateById(entity);
        return entity.getId();
    }

    public void removeById(Long id) {
        UiComponentCompositeDTO oldEntity = selectById(id);
        if(oldEntity!=null){
            if(CollectionUtils.isNotEmpty(oldEntity.getBelongtoIdFilesetList())){
                List<Long> filesetIdList = oldEntity.getBelongtoIdFilesetList().stream().map(t -> t.getId()).collect(Collectors.toList());
                filesetCompositeService.batchRemove(filesetIdList);
            }
            if(CollectionUtils.isNotEmpty(oldEntity.getComponentIdUiEventHandleList())){
                List<Long> uiEventHandleIdList = oldEntity.getComponentIdUiEventHandleList().stream().map(t -> t.getId()).collect(Collectors.toList());
                uiEventHandleCompositeService.batchRemove(uiEventHandleIdList);
            }
        }
      uiComponentService.removeById(id);
    }

    @Override
    public void batchRemove(List<Long> entityIdLst) {
        if(CollectionUtils.isNotEmpty(entityIdLst)){
            entityIdLst.stream().filter(t->t!=null).forEach(this::removeById);
        }
    }

    @Override
    public void batchSubmit(List<UiComponentCompositeDTO> entityList) {
        if(CollectionUtils.isNotEmpty(entityList)){
            entityList.stream().filter(t->t!=null).forEach(this::submit);
        }
    }

    public UiComponentCompositeDTO selectById(Long id) {
        UiComponent uiComponent = uiComponentService.getById(id);
        if(uiComponent==null)
          return null;
        UiComponentCompositeDTO uiComponentDTO=new UiComponentCompositeDTO();
        BeanUtils.copyProperties(uiComponent,uiComponentDTO);
        FilesetCompositeDTO filesetParam = new FilesetCompositeDTO();
        filesetParam.setBelongtoId(id);
        filesetParam.setBelongtoType(UiComponentCompositeDTO.BELONGTOID_BELONGTOTYPE_FILESET_FK);
        uiComponentDTO.setBelongtoIdFilesetList(filesetCompositeService.selectList(filesetParam));
        UiEventHandleCompositeDTO uiEventHandleParam = new UiEventHandleCompositeDTO();
        uiEventHandleParam.setComponentId(id);
        uiComponentDTO.setComponentIdUiEventHandleList(uiEventHandleCompositeService.selectList(uiEventHandleParam));
        return uiComponentDTO;
    }

    @Override
    public List<UiComponentCompositeDTO> selectList(UiComponentCompositeDTO param) {
        List<UiComponent> uiComponentList = uiComponentService.list(new QueryWrapper<>(param));
        return uiComponentList.stream().map(t->selectById(t.getId())).collect(Collectors.toList());
    }

    @Override
    public UiComponentCompositeDTO selectOne(UiComponentCompositeDTO param) {
        UiComponent uiComponent = uiComponentService.getOne(new QueryWrapper<>(param));
        if(uiComponent!=null){
            return selectById(uiComponent.getId());
        }
        return null;
    }
  
  	public UiComponentCompositeDTO deepCopyById(Long id){
    		if(id==null)
          return null;
      	UiComponentCompositeDTO entity=selectById(id);
        if(entity==null)
            return null;
      	return deepCopy(entity);
  	}
    public UiComponentCompositeDTO deepCopy(UiComponentCompositeDTO entity){      	
        UiComponentCompositeValidate.preCopy(entity);
        entity.setOriginalId(entity.getId());
      	entity.setId(null);
        uiComponentService.save(entity);
        {
            List<FilesetCompositeDTO> filesetList=entity.getBelongtoIdFilesetList().stream().map(t->filesetCompositeService.deepCopyById(t.getId())).collect(Collectors.toList());
            filesetList.stream().forEach(t->{
              t.setBelongtoId(entity.getId());
              filesetService.updateById(t);
              t.setBelongtoType(UiComponentCompositeDTO.BELONGTOID_BELONGTOTYPE_FILESET_FK);
            });
            entity.setBelongtoIdFilesetList(filesetList);
        }
        {
            List<UiEventHandleCompositeDTO> uiEventHandleList=entity.getComponentIdUiEventHandleList().stream().map(t->uiEventHandleCompositeService.deepCopyById(t.getId())).collect(Collectors.toList());
            uiEventHandleList.stream().forEach(t->{
              t.setComponentId(entity.getId());
              uiEventHandleService.updateById(t);
            });
            entity.setComponentIdUiEventHandleList(uiEventHandleList);
        }
        if(UiComponentCompositeValidate.validateOnCopy(entity))
          uiComponentService.updateById(entity);
        return entity;
    }
}