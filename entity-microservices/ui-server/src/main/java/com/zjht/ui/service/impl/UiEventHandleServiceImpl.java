package com.zjht.ui.service.impl;


import com.wukong.core.mp.base.BaseServiceImpl;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Service;
import com.zjht.ui.mapper.UiEventHandleMapper;
import com.zjht.ui.wrapper.UiEventHandleWrapper;
import com.zjht.ui.entity.UiEventHandle;
import com.zjht.ui.service.IUiEventHandleService;

/**
 * Service业务层处理
 *
 * @author wangy
 */
@Service
@ConditionalOnMissingBean(name="uiEventHandleServiceImplExt")
public class UiEventHandleServiceImpl<M,T> extends BaseServiceImpl<UiEventHandleMapper,UiEventHandle> implements IUiEventHandleService {
    @Override
    public boolean save(UiEventHandle entity) {
      	UiEventHandleWrapper.build().initUiEventHandle(entity);
      	return super.save(entity);
    }
  
    @Override
    public boolean updateById(UiEventHandle entity) {
      	UiEventHandleWrapper.build().validateUiEventHandle(entity);
        return super.updateById(entity);
    }
}
