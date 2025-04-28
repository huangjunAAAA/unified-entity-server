package com.zjht.unified.datasource.service.impl;


import com.wukong.core.mp.base.BaseServiceImpl;
import com.zjht.unified.datasource.entity.DtpDataSource;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Service;
import com.zjht.unified.datasource.mapper.DtpDataSourceMapper;
import com.zjht.unified.datasource.wrapper.DtpDataSourceWrapper;

import com.zjht.unified.datasource.service.IDtpDataSourceService;

/**
 * Service业务层处理
 *
 * @author wangy
 */
@Service
@ConditionalOnMissingBean(name="dtpDataSourceServiceImplExt")
public class DtpDataSourceServiceImpl<M,T> extends BaseServiceImpl<DtpDataSourceMapper, DtpDataSource> implements IDtpDataSourceService {
    @Override
    public boolean save(DtpDataSource entity) {
      	DtpDataSourceWrapper.build().initDtpDataSource(entity);
      	return super.save(entity);
    }
  
    @Override
    public boolean updateById(DtpDataSource entity) {
      	DtpDataSourceWrapper.build().validateDtpDataSource(entity);
        return super.updateById(entity);
    }
}
