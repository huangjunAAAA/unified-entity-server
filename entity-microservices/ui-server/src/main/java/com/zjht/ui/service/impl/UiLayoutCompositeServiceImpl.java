package com.zjht.ui.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zjht.ui.service.IUiLayoutService;


import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Service;
import com.zjht.ui.entity.UiLayout;
import com.zjht.ui.service.IUiLayoutCompositeService;
import com.zjht.ui.dto.*;
import com.zjht.ui.wrapper.*;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service业务层处理
 *
 * @author wangy
 */
@Service
@ConditionalOnMissingBean(name = "uiLayoutCompositeServiceImplExt")
@Transactional(propagation = Propagation.REQUIRED)
public class UiLayoutCompositeServiceImpl implements IUiLayoutCompositeService {

    @Autowired
    private IUiLayoutService uiLayoutService;
  
    public Long submit(UiLayoutCompositeDTO entity) {
        if(entity==null)
            return null;
        UiLayoutCompositeValidate.preValidate(entity);
        if(entity.getId()==null){
            uiLayoutService.save(entity);
        }else{
            uiLayoutService.updateById(entity);
            UiLayout newestEntity= uiLayoutService.getById(entity.getId());
            BeanUtils.copyProperties(newestEntity,entity);
        }
        boolean updateRequired=false;
        if(UiLayoutCompositeValidate.validateOnFlush(entity)||updateRequired)
          uiLayoutService.updateById(entity);
        return entity.getId();
    }

    public void removeById(Long id) {
        UiLayoutCompositeDTO oldEntity = selectById(id);
        if(oldEntity!=null){
        }
      uiLayoutService.removeById(id);
    }

    @Override
    public void batchRemove(List<Long> entityIdLst) {
        if(CollectionUtils.isNotEmpty(entityIdLst)){
            entityIdLst.stream().filter(t->t!=null).forEach(this::removeById);
        }
    }

    @Override
    public void batchSubmit(List<UiLayoutCompositeDTO> entityList) {
        if(CollectionUtils.isNotEmpty(entityList)){
            entityList.stream().filter(t->t!=null).forEach(this::submit);
        }
    }

    public UiLayoutCompositeDTO selectById(Long id) {
        UiLayout uiLayout = uiLayoutService.getById(id);
        if(uiLayout==null)
          return null;
        UiLayoutCompositeDTO uiLayoutDTO=new UiLayoutCompositeDTO();
        BeanUtils.copyProperties(uiLayout,uiLayoutDTO);
        return uiLayoutDTO;
    }

    @Override
    public List<UiLayoutCompositeDTO> selectList(UiLayoutCompositeDTO param) {
        List<UiLayout> uiLayoutList = uiLayoutService.list(new QueryWrapper<>(param));
        return uiLayoutList.stream().map(t->selectById(t.getId())).collect(Collectors.toList());
    }

    @Override
    public UiLayoutCompositeDTO selectOne(UiLayoutCompositeDTO param) {
        UiLayout uiLayout = uiLayoutService.getOne(new QueryWrapper<>(param));
        if(uiLayout!=null){
            return selectById(uiLayout.getId());
        }
        return null;
    }
  
  	public UiLayoutCompositeDTO deepCopyById(Long id){
    		if(id==null)
          return null;
      	UiLayoutCompositeDTO entity=selectById(id);
        if(entity==null)
            return null;
      	return deepCopy(entity);
  	}
    public UiLayoutCompositeDTO deepCopy(UiLayoutCompositeDTO entity){      	
        UiLayoutCompositeValidate.preCopy(entity);
        entity.setOriginalId(entity.getId());
      	entity.setId(null);
        uiLayoutService.save(entity);
        if(UiLayoutCompositeValidate.validateOnCopy(entity))
          uiLayoutService.updateById(entity);
        return entity;
    }
}