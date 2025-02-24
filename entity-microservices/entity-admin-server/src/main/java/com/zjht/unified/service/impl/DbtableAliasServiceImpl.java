package com.zjht.unified.service.impl;


import com.wukong.core.mp.base.BaseServiceImpl;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Service;
import com.zjht.unified.mapper.DbtableAliasMapper;
import com.zjht.unified.wrapper.DbtableAliasWrapper;
import com.zjht.unified.entity.DbtableAlias;
import com.zjht.unified.service.IDbtableAliasService;

/**
 * Service业务层处理
 *
 * @author wangy
 */
@Service
@ConditionalOnMissingBean(name="dbtableAliasServiceImplExt")
public class DbtableAliasServiceImpl<M,T> extends BaseServiceImpl<DbtableAliasMapper,DbtableAlias> implements IDbtableAliasService {
    @Override
    public boolean save(DbtableAlias entity) {
      	DbtableAliasWrapper.build().initDbtableAlias(entity);
      	return super.save(entity);
    }
  
    @Override
    public boolean updateById(DbtableAlias entity) {
      	DbtableAliasWrapper.build().validateDbtableAlias(entity);
        return super.updateById(entity);
    }
}
