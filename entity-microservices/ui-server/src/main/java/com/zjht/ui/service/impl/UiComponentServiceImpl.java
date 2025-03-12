package com.zjht.ui.service.impl;


import com.wukong.core.mp.base.BaseServiceImpl;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Service;
import com.zjht.ui.mapper.UiComponentMapper;
import com.zjht.ui.wrapper.UiComponentWrapper;
import com.zjht.ui.entity.UiComponent;
import com.zjht.ui.service.IUiComponentService;

/**
 * Service业务层处理
 *
 * @author wangy
 */
@Service
@ConditionalOnMissingBean(name="uiComponentServiceImplExt")
public class UiComponentServiceImpl<M,T> extends BaseServiceImpl<UiComponentMapper,UiComponent> implements IUiComponentService {
    @Override
    public boolean save(UiComponent entity) {
      	UiComponentWrapper.build().initUiComponent(entity);
      	return super.save(entity);
    }
  
    @Override
    public boolean updateById(UiComponent entity) {
      	UiComponentWrapper.build().validateUiComponent(entity);
        return super.updateById(entity);
    }
}
