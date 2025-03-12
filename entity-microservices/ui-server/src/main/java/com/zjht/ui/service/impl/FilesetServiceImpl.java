package com.zjht.ui.service.impl;


import com.wukong.core.mp.base.BaseServiceImpl;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Service;
import com.zjht.ui.mapper.FilesetMapper;
import com.zjht.ui.wrapper.FilesetWrapper;
import com.zjht.ui.entity.Fileset;
import com.zjht.ui.service.IFilesetService;

/**
 * Service业务层处理
 *
 * @author wangy
 */
@Service
@ConditionalOnMissingBean(name="filesetServiceImplExt")
public class FilesetServiceImpl<M,T> extends BaseServiceImpl<FilesetMapper,Fileset> implements IFilesetService {
    @Override
    public boolean save(Fileset entity) {
      	FilesetWrapper.build().initFileset(entity);
      	return super.save(entity);
    }
  
    @Override
    public boolean updateById(Fileset entity) {
      	FilesetWrapper.build().validateFileset(entity);
        return super.updateById(entity);
    }
}
