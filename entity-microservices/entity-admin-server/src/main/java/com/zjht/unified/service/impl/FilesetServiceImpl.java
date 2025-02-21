package com.zjht.unified.service.impl;


import com.wukong.core.mp.base.BaseServiceImpl;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Service;
import com.zjht.unified.mapper.FilesetMapper;
import com.zjht.unified.wrapper.FilesetWrapper;
import com.zjht.unified.entity.Fileset;
import com.zjht.unified.service.IFilesetService;

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
