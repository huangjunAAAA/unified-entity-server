package com.zjht.ui.service.impl;


import com.wukong.core.mp.base.BaseServiceImpl;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Service;
import com.zjht.ui.mapper.UiLayoutMapper;
import com.zjht.ui.wrapper.UiLayoutWrapper;
import com.zjht.ui.entity.UiLayout;
import com.zjht.ui.service.IUiLayoutService;

/**
 * Service业务层处理
 *
 * @author wangy
 */
@Service
@ConditionalOnMissingBean(name="uiLayoutServiceImplExt")
public class UiLayoutServiceImpl<M,T> extends BaseServiceImpl<UiLayoutMapper,UiLayout> implements IUiLayoutService {
    @Override
    public boolean save(UiLayout entity) {
      	UiLayoutWrapper.build().initUiLayout(entity);
      	return super.save(entity);
    }
  
    @Override
    public boolean updateById(UiLayout entity) {
      	UiLayoutWrapper.build().validateUiLayout(entity);
        return super.updateById(entity);
    }
}
