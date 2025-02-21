package com.zjht.unified.service.impl;


import com.wukong.core.mp.base.BaseServiceImpl;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Service;
import com.zjht.unified.mapper.FsmConditionMapper;
import com.zjht.unified.wrapper.FsmConditionWrapper;
import com.zjht.unified.entity.FsmCondition;
import com.zjht.unified.service.IFsmConditionService;

/**
 * Service业务层处理
 *
 * @author wangy
 */
@Service
@ConditionalOnMissingBean(name="fsmConditionServiceImplExt")
public class FsmConditionServiceImpl<M,T> extends BaseServiceImpl<FsmConditionMapper,FsmCondition> implements IFsmConditionService {
    @Override
    public boolean save(FsmCondition entity) {
      	FsmConditionWrapper.build().initFsmCondition(entity);
      	return super.save(entity);
    }
  
    @Override
    public boolean updateById(FsmCondition entity) {
      	FsmConditionWrapper.build().validateFsmCondition(entity);
        return super.updateById(entity);
    }
}
