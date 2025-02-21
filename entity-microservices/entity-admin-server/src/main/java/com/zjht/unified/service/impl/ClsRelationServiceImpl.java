package com.zjht.unified.service.impl;


import com.wukong.core.mp.base.BaseServiceImpl;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Service;
import com.zjht.unified.mapper.ClsRelationMapper;
import com.zjht.unified.wrapper.ClsRelationWrapper;
import com.zjht.unified.entity.ClsRelation;
import com.zjht.unified.service.IClsRelationService;

/**
 * Service业务层处理
 *
 * @author wangy
 */
@Service
@ConditionalOnMissingBean(name="clsRelationServiceImplExt")
public class ClsRelationServiceImpl<M,T> extends BaseServiceImpl<ClsRelationMapper,ClsRelation> implements IClsRelationService {
    @Override
    public boolean save(ClsRelation entity) {
      	ClsRelationWrapper.build().initClsRelation(entity);
      	return super.save(entity);
    }
  
    @Override
    public boolean updateById(ClsRelation entity) {
      	ClsRelationWrapper.build().validateClsRelation(entity);
        return super.updateById(entity);
    }
}
