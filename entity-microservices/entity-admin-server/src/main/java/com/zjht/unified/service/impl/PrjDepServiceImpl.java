package com.zjht.unified.service.impl;


import com.wukong.core.mp.base.BaseServiceImpl;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Service;
import com.zjht.unified.mapper.PrjDepMapper;
import com.zjht.unified.wrapper.PrjDepWrapper;
import com.zjht.unified.entity.PrjDep;
import com.zjht.unified.service.IPrjDepService;

/**
 * Service业务层处理
 *
 * @author wangy
 */
@Service
@ConditionalOnMissingBean(name="prjDepServiceImplExt")
public class PrjDepServiceImpl<M,T> extends BaseServiceImpl<PrjDepMapper,PrjDep> implements IPrjDepService {
    @Override
    public boolean save(PrjDep entity) {
      	PrjDepWrapper.build().initPrjDep(entity);
      	return super.save(entity);
    }
  
    @Override
    public boolean updateById(PrjDep entity) {
      	PrjDepWrapper.build().validatePrjDep(entity);
        return super.updateById(entity);
    }
}
