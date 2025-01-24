package com.zjht.unified.data.storage.service.impl;


import com.wukong.core.mp.base.BaseServiceImpl;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Service;
import com.zjht.unified.data.storage.mapper.RawDataMapper;
import com.zjht.unified.data.storage.wrapper.RawDataWrapper;
import com.zjht.unified.data.entity.RawData;
import com.zjht.unified.data.storage.service.IRawDataService;

/**
 * Service业务层处理
 *
 * @author wangy
 */
@Service
@ConditionalOnMissingBean(name="rawDataServiceImplExt")
public class RawDataServiceImpl<M,T> extends BaseServiceImpl<RawDataMapper,RawData> implements IRawDataService {
    @Override
    public boolean save(RawData entity) {
      	RawDataWrapper.build().initRawData(entity);
      	return super.save(entity);
    }
  
    @Override
    public boolean updateById(RawData entity) {
      	RawDataWrapper.build().validateRawData(entity);
        return super.updateById(entity);
    }
}
