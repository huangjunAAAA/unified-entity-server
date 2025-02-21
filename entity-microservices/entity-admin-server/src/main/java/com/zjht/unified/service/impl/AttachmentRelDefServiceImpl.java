package com.zjht.unified.service.impl;


import com.wukong.core.mp.base.BaseServiceImpl;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Service;
import com.zjht.unified.mapper.AttachmentRelDefMapper;
import com.zjht.unified.wrapper.AttachmentRelDefWrapper;
import com.zjht.unified.entity.AttachmentRelDef;
import com.zjht.unified.service.IAttachmentRelDefService;

/**
 * Service业务层处理
 *
 * @author wangy
 */
@Service
@ConditionalOnMissingBean(name="attachmentRelDefServiceImplExt")
public class AttachmentRelDefServiceImpl<M,T> extends BaseServiceImpl<AttachmentRelDefMapper,AttachmentRelDef> implements IAttachmentRelDefService {
    @Override
    public boolean save(AttachmentRelDef entity) {
      	AttachmentRelDefWrapper.build().initAttachmentRelDef(entity);
      	return super.save(entity);
    }
  
    @Override
    public boolean updateById(AttachmentRelDef entity) {
      	AttachmentRelDefWrapper.build().validateAttachmentRelDef(entity);
        return super.updateById(entity);
    }
}
