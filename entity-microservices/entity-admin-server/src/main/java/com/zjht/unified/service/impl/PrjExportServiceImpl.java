package com.zjht.unified.service.impl;


import com.wukong.core.mp.base.BaseServiceImpl;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Service;
import com.zjht.unified.mapper.PrjExportMapper;
import com.zjht.unified.wrapper.PrjExportWrapper;
import com.zjht.unified.entity.PrjExport;
import com.zjht.unified.service.IPrjExportService;

/**
 * Service业务层处理
 *
 * @author wangy
 */
@Service
@ConditionalOnMissingBean(name="prjExportServiceImplExt")
public class PrjExportServiceImpl<M,T> extends BaseServiceImpl<PrjExportMapper,PrjExport> implements IPrjExportService {
    @Override
    public boolean save(PrjExport entity) {
      	PrjExportWrapper.build().initPrjExport(entity);
      	return super.save(entity);
    }
  
    @Override
    public boolean updateById(PrjExport entity) {
      	PrjExportWrapper.build().validatePrjExport(entity);
        return super.updateById(entity);
    }
}
