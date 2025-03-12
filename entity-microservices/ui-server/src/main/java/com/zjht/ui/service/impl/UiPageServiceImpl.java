package com.zjht.ui.service.impl;


import com.wukong.core.mp.base.BaseServiceImpl;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Service;
import com.zjht.ui.mapper.UiPageMapper;
import com.zjht.ui.wrapper.UiPageWrapper;
import com.zjht.ui.entity.UiPage;
import com.zjht.ui.service.IUiPageService;

/**
 * Service业务层处理
 *
 * @author wangy
 */
@Service
@ConditionalOnMissingBean(name="uiPageServiceImplExt")
public class UiPageServiceImpl<M,T> extends BaseServiceImpl<UiPageMapper,UiPage> implements IUiPageService {
    @Override
    public boolean save(UiPage entity) {
      	UiPageWrapper.build().initUiPage(entity);
      	return super.save(entity);
    }
  
    @Override
    public boolean updateById(UiPage entity) {
      	UiPageWrapper.build().validateUiPage(entity);
        return super.updateById(entity);
    }
}
