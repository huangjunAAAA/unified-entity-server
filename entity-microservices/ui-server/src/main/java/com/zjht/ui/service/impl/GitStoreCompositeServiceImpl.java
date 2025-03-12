package com.zjht.ui.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zjht.ui.service.IGitStoreService;


import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Service;
import com.zjht.ui.entity.GitStore;
import com.zjht.ui.service.IGitStoreCompositeService;
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
@ConditionalOnMissingBean(name = "gitStoreCompositeServiceImplExt")
@Transactional(propagation = Propagation.REQUIRED)
public class GitStoreCompositeServiceImpl implements IGitStoreCompositeService {

    @Autowired
    private IGitStoreService gitStoreService;
  
    public Long submit(GitStoreCompositeDTO entity) {
        if(entity==null)
            return null;
        GitStoreCompositeValidate.preValidate(entity);
        if(entity.getId()==null){
            gitStoreService.save(entity);
        }else{
            gitStoreService.updateById(entity);
            GitStore newestEntity= gitStoreService.getById(entity.getId());
            BeanUtils.copyProperties(newestEntity,entity);
        }
        boolean updateRequired=false;
        if(GitStoreCompositeValidate.validateOnFlush(entity)||updateRequired)
          gitStoreService.updateById(entity);
        return entity.getId();
    }

    public void removeById(Long id) {
        GitStoreCompositeDTO oldEntity = selectById(id);
        if(oldEntity!=null){
        }
      gitStoreService.removeById(id);
    }

    @Override
    public void batchRemove(List<Long> entityIdLst) {
        if(CollectionUtils.isNotEmpty(entityIdLst)){
            entityIdLst.stream().filter(t->t!=null).forEach(this::removeById);
        }
    }

    @Override
    public void batchSubmit(List<GitStoreCompositeDTO> entityList) {
        if(CollectionUtils.isNotEmpty(entityList)){
            entityList.stream().filter(t->t!=null).forEach(this::submit);
        }
    }

    public GitStoreCompositeDTO selectById(Long id) {
        GitStore gitStore = gitStoreService.getById(id);
        if(gitStore==null)
          return null;
        GitStoreCompositeDTO gitStoreDTO=new GitStoreCompositeDTO();
        BeanUtils.copyProperties(gitStore,gitStoreDTO);
        return gitStoreDTO;
    }

    @Override
    public List<GitStoreCompositeDTO> selectList(GitStoreCompositeDTO param) {
        List<GitStore> gitStoreList = gitStoreService.list(new QueryWrapper<>(param));
        return gitStoreList.stream().map(t->selectById(t.getId())).collect(Collectors.toList());
    }

    @Override
    public GitStoreCompositeDTO selectOne(GitStoreCompositeDTO param) {
        GitStore gitStore = gitStoreService.getOne(new QueryWrapper<>(param));
        if(gitStore!=null){
            return selectById(gitStore.getId());
        }
        return null;
    }
  
  	public GitStoreCompositeDTO deepCopyById(Long id){
    		if(id==null)
          return null;
      	GitStoreCompositeDTO entity=selectById(id);
        if(entity==null)
            return null;
      	return deepCopy(entity);
  	}
    public GitStoreCompositeDTO deepCopy(GitStoreCompositeDTO entity){      	
        GitStoreCompositeValidate.preCopy(entity);
        entity.setOriginalId(entity.getId());
      	entity.setId(null);
        gitStoreService.save(entity);
        if(GitStoreCompositeValidate.validateOnCopy(entity))
          gitStoreService.updateById(entity);
        return entity;
    }
}