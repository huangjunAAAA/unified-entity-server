package com.zjht.unified.service.impl;


import com.wukong.core.mp.base.BaseServiceImpl;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Service;
import com.zjht.unified.mapper.ConfigGraphMapper;
import com.zjht.unified.wrapper.ConfigGraphWrapper;
import com.zjht.unified.entity.ConfigGraph;
import com.zjht.unified.service.IConfigGraphService;

/**
 * Service业务层处理
 *
 * @author wangy
 */
@Service
@ConditionalOnMissingBean(name="configGraphServiceImplExt")
public class ConfigGraphServiceImpl<M,T> extends BaseServiceImpl<ConfigGraphMapper,ConfigGraph> implements IConfigGraphService {
    @Override
    public boolean save(ConfigGraph entity) {
      	ConfigGraphWrapper.build().initConfigGraph(entity);
      	return super.save(entity);
    }
  
    @Override
    public boolean updateById(ConfigGraph entity) {
      	ConfigGraphWrapper.build().validateConfigGraph(entity);
        return super.updateById(entity);
    }
}
