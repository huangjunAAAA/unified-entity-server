package com.zjht.unified.service.impl;


import com.wukong.core.mp.base.BaseServiceImpl;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Service;
import com.zjht.unified.mapper.MethodParamMapper;
import com.zjht.unified.wrapper.MethodParamWrapper;
import com.zjht.unified.entity.MethodParam;
import com.zjht.unified.service.IMethodParamService;

/**
 * Service业务层处理
 *
 * @author wangy
 */
@Service
@ConditionalOnMissingBean(name="methodParamServiceImplExt")
public class MethodParamServiceImpl<M,T> extends BaseServiceImpl<MethodParamMapper,MethodParam> implements IMethodParamService {
    @Override
    public boolean save(MethodParam entity) {
      	MethodParamWrapper.build().initMethodParam(entity);
      	return super.save(entity);
    }
  
    @Override
    public boolean updateById(MethodParam entity) {
      	MethodParamWrapper.build().validateMethodParam(entity);
        return super.updateById(entity);
    }
}
