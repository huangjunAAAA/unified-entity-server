package com.zjht.unified.service.impl;


import com.wukong.core.mp.base.BaseServiceImpl;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Service;
import com.zjht.unified.mapper.FieldDefMapper;
import com.zjht.unified.wrapper.FieldDefWrapper;
import com.zjht.unified.entity.FieldDef;
import com.zjht.unified.service.IFieldDefService;

/**
 * Service业务层处理
 *
 * @author wangy
 */
@Service
@ConditionalOnMissingBean(name="fieldDefServiceImplExt")
public class FieldDefServiceImpl<M,T> extends BaseServiceImpl<FieldDefMapper,FieldDef> implements IFieldDefService {
    @Override
    public boolean save(FieldDef entity) {
      	FieldDefWrapper.build().initFieldDef(entity);
      	return super.save(entity);
    }
  
    @Override
    public boolean updateById(FieldDef entity) {
      	FieldDefWrapper.build().validateFieldDef(entity);
        return super.updateById(entity);
    }
}
