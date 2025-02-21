package com.zjht.unified.service.impl;


import com.wukong.core.mp.base.BaseServiceImpl;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Service;
import com.zjht.unified.mapper.StaticDefMapper;
import com.zjht.unified.wrapper.StaticDefWrapper;
import com.zjht.unified.entity.StaticDef;
import com.zjht.unified.service.IStaticDefService;

/**
 * Service业务层处理
 *
 * @author wangy
 */
@Service
@ConditionalOnMissingBean(name="staticDefServiceImplExt")
public class StaticDefServiceImpl<M,T> extends BaseServiceImpl<StaticDefMapper,StaticDef> implements IStaticDefService {
    @Override
    public boolean save(StaticDef entity) {
      	StaticDefWrapper.build().initStaticDef(entity);
      	return super.save(entity);
    }
  
    @Override
    public boolean updateById(StaticDef entity) {
      	StaticDefWrapper.build().validateStaticDef(entity);
        return super.updateById(entity);
    }
}
