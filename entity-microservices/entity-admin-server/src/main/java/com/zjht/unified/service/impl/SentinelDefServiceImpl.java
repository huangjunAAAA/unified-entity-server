package com.zjht.unified.service.impl;


import com.wukong.core.mp.base.BaseServiceImpl;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Service;
import com.zjht.unified.mapper.SentinelDefMapper;
import com.zjht.unified.wrapper.SentinelDefWrapper;
import com.zjht.unified.entity.SentinelDef;
import com.zjht.unified.service.ISentinelDefService;

/**
 * Service业务层处理
 *
 * @author wangy
 */
@Service
@ConditionalOnMissingBean(name="sentinelDefServiceImplExt")
public class SentinelDefServiceImpl<M,T> extends BaseServiceImpl<SentinelDefMapper,SentinelDef> implements ISentinelDefService {
    @Override
    public boolean save(SentinelDef entity) {
      	SentinelDefWrapper.build().initSentinelDef(entity);
      	return super.save(entity);
    }
  
    @Override
    public boolean updateById(SentinelDef entity) {
      	SentinelDefWrapper.build().validateSentinelDef(entity);
        return super.updateById(entity);
    }
}
