package com.zjht.ui.service.impl;


import com.wukong.core.mp.base.BaseServiceImpl;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Service;
import com.zjht.ui.mapper.UiPrjMapper;
import com.zjht.ui.wrapper.UiPrjWrapper;
import com.zjht.ui.entity.UiPrj;
import com.zjht.ui.service.IUiPrjService;

/**
 * Service业务层处理
 *
 * @author wangy
 */
@Service
@ConditionalOnMissingBean(name="uiPrjServiceImplExt")
public class UiPrjServiceImpl<M,T> extends BaseServiceImpl<UiPrjMapper,UiPrj> implements IUiPrjService {
    @Override
    public boolean save(UiPrj entity) {
      	UiPrjWrapper.build().initUiPrj(entity);
      	return super.save(entity);
    }
  
    @Override
    public boolean updateById(UiPrj entity) {
      	UiPrjWrapper.build().validateUiPrj(entity);
        return super.updateById(entity);
    }
}
