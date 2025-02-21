package com.zjht.unified.service.impl;


import com.wukong.core.mp.base.BaseServiceImpl;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Service;
import com.zjht.unified.mapper.MethodDefMapper;
import com.zjht.unified.wrapper.MethodDefWrapper;
import com.zjht.unified.entity.MethodDef;
import com.zjht.unified.service.IMethodDefService;

/**
 * Service业务层处理
 *
 * @author wangy
 */
@Service
@ConditionalOnMissingBean(name="methodDefServiceImplExt")
public class MethodDefServiceImpl<M,T> extends BaseServiceImpl<MethodDefMapper,MethodDef> implements IMethodDefService {
    @Override
    public boolean save(MethodDef entity) {
      	MethodDefWrapper.build().initMethodDef(entity);
      	return super.save(entity);
    }
  
    @Override
    public boolean updateById(MethodDef entity) {
      	MethodDefWrapper.build().validateMethodDef(entity);
        return super.updateById(entity);
    }
}
