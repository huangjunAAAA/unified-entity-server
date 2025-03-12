package com.zjht.ui.service.impl;


import com.wukong.core.mp.base.BaseServiceImpl;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Service;
import com.zjht.ui.mapper.GitStoreMapper;
import com.zjht.ui.wrapper.GitStoreWrapper;
import com.zjht.ui.entity.GitStore;
import com.zjht.ui.service.IGitStoreService;

/**
 * Service业务层处理
 *
 * @author wangy
 */
@Service
@ConditionalOnMissingBean(name="gitStoreServiceImplExt")
public class GitStoreServiceImpl<M,T> extends BaseServiceImpl<GitStoreMapper,GitStore> implements IGitStoreService {
    @Override
    public boolean save(GitStore entity) {
      	GitStoreWrapper.build().initGitStore(entity);
      	return super.save(entity);
    }
  
    @Override
    public boolean updateById(GitStore entity) {
      	GitStoreWrapper.build().validateGitStore(entity);
        return super.updateById(entity);
    }
}
