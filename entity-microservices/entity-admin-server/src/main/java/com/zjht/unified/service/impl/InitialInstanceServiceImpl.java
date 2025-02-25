package com.zjht.unified.service.impl;


import com.wukong.core.mp.base.BaseServiceImpl;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Service;
import com.zjht.unified.mapper.InitialInstanceMapper;
import com.zjht.unified.wrapper.InitialInstanceWrapper;
import com.zjht.unified.entity.InitialInstance;
import com.zjht.unified.service.IInitialInstanceService;

/**
 * Service业务层处理
 *
 * @author wangy
 */
@Service
@ConditionalOnMissingBean(name="initialInstanceServiceImplExt")
public class InitialInstanceServiceImpl<M,T> extends BaseServiceImpl<InitialInstanceMapper,InitialInstance> implements IInitialInstanceService {
    @Override
    public boolean save(InitialInstance entity) {
      	InitialInstanceWrapper.build().initInitialInstance(entity);
      	return super.save(entity);
    }
  
    @Override
    public boolean updateById(InitialInstance entity) {
      	InitialInstanceWrapper.build().validateInitialInstance(entity);
        return super.updateById(entity);
    }
}
