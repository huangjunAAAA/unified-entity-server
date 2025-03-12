package com.zjht.ui.service;

import com.wukong.core.mp.base.BaseService;
import com.zjht.ui.entity.Fileset;
import com.zjht.ui.dto.*;
import com.zjht.ui.vo.*;
import java.util.List;

/**
 * Service接口
 *
 * @author wangy
 */
public interface IFilesetCompositeService {
    Long submit(FilesetCompositeDTO entity);
    void removeById(Long id);
    FilesetCompositeDTO selectById(Long id);
    FilesetCompositeDTO deepCopyById(Long id);  
     /**
     * 该接口会修改entity的主键
     * @param entity
     * @return
     */
    FilesetCompositeDTO deepCopy(FilesetCompositeDTO entity); 
    void batchRemove(List<Long> entityIdLst);
    void batchSubmit(List<FilesetCompositeDTO> entityList);
    List<FilesetCompositeDTO> selectList(FilesetCompositeDTO param);
    FilesetCompositeDTO selectOne(FilesetCompositeDTO param);
}