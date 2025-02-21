package com.zjht.unified.service.impl;


import com.wukong.core.mp.base.BaseServiceImpl;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Service;
import com.zjht.unified.mapper.ClazzDefMapper;
import com.zjht.unified.wrapper.ClazzDefWrapper;
import com.zjht.unified.entity.ClazzDef;
import com.zjht.unified.service.IClazzDefService;

/**
 * Service业务层处理
 *
 * @author wangy
 */
@Service
@ConditionalOnMissingBean(name="clazzDefServiceImplExt")
public class ClazzDefServiceImpl<M,T> extends BaseServiceImpl<ClazzDefMapper,ClazzDef> implements IClazzDefService {
    @Override
    public boolean save(ClazzDef entity) {
      	ClazzDefWrapper.build().initClazzDef(entity);
      	return super.save(entity);
    }
  
    @Override
    public boolean updateById(ClazzDef entity) {
      	ClazzDefWrapper.build().validateClazzDef(entity);
        return super.updateById(entity);
    }
}
