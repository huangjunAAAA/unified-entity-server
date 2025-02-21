package com.zjht.unified.service.impl;


import com.wukong.core.mp.base.BaseServiceImpl;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Service;
import com.zjht.unified.mapper.UePrjMapper;
import com.zjht.unified.wrapper.UePrjWrapper;
import com.zjht.unified.entity.UePrj;
import com.zjht.unified.service.IUePrjService;

/**
 * Service业务层处理
 *
 * @author wangy
 */
@Service
@ConditionalOnMissingBean(name="uePrjServiceImplExt")
public class UePrjServiceImpl<M,T> extends BaseServiceImpl<UePrjMapper,UePrj> implements IUePrjService {
    @Override
    public boolean save(UePrj entity) {
      	UePrjWrapper.build().initUePrj(entity);
      	return super.save(entity);
    }
  
    @Override
    public boolean updateById(UePrj entity) {
      	UePrjWrapper.build().validateUePrj(entity);
        return super.updateById(entity);
    }
}
