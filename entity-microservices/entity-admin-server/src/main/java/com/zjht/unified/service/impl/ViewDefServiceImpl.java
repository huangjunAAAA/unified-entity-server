package com.zjht.unified.service.impl;


import com.wukong.core.mp.base.BaseServiceImpl;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Service;
import com.zjht.unified.mapper.ViewDefMapper;
import com.zjht.unified.wrapper.ViewDefWrapper;
import com.zjht.unified.entity.ViewDef;
import com.zjht.unified.service.IViewDefService;

/**
 * Service业务层处理
 *
 * @author wangy
 */
@Service
@ConditionalOnMissingBean(name="viewDefServiceImplExt")
public class ViewDefServiceImpl<M,T> extends BaseServiceImpl<ViewDefMapper,ViewDef> implements IViewDefService {
    @Override
    public boolean save(ViewDef entity) {
      	ViewDefWrapper.build().initViewDef(entity);
      	return super.save(entity);
    }
  
    @Override
    public boolean updateById(ViewDef entity) {
      	ViewDefWrapper.build().validateViewDef(entity);
        return super.updateById(entity);
    }
}
