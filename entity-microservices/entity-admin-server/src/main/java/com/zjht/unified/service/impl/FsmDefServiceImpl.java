package com.zjht.unified.service.impl;


import com.wukong.core.mp.base.BaseServiceImpl;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Service;
import com.zjht.unified.mapper.FsmDefMapper;
import com.zjht.unified.wrapper.FsmDefWrapper;
import com.zjht.unified.entity.FsmDef;
import com.zjht.unified.service.IFsmDefService;

/**
 * Service业务层处理
 *
 * @author wangy
 */
@Service
@ConditionalOnMissingBean(name="fsmDefServiceImplExt")
public class FsmDefServiceImpl<M,T> extends BaseServiceImpl<FsmDefMapper,FsmDef> implements IFsmDefService {
    @Override
    public boolean save(FsmDef entity) {
      	FsmDefWrapper.build().initFsmDef(entity);
      	return super.save(entity);
    }
  
    @Override
    public boolean updateById(FsmDef entity) {
      	FsmDefWrapper.build().validateFsmDef(entity);
        return super.updateById(entity);
    }
}
