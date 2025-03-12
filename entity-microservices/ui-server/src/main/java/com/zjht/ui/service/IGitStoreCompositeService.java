package com.zjht.ui.service;

import com.wukong.core.mp.base.BaseService;
import com.zjht.ui.entity.GitStore;
import com.zjht.ui.dto.*;
import com.zjht.ui.vo.*;
import java.util.List;

/**
 * Service接口
 *
 * @author wangy
 */
public interface IGitStoreCompositeService {
    Long submit(GitStoreCompositeDTO entity);
    void removeById(Long id);
    GitStoreCompositeDTO selectById(Long id);
    GitStoreCompositeDTO deepCopyById(Long id);  
     /**
     * 该接口会修改entity的主键
     * @param entity
     * @return
     */
    GitStoreCompositeDTO deepCopy(GitStoreCompositeDTO entity); 
    void batchRemove(List<Long> entityIdLst);
    void batchSubmit(List<GitStoreCompositeDTO> entityList);
    List<GitStoreCompositeDTO> selectList(GitStoreCompositeDTO param);
    GitStoreCompositeDTO selectOne(GitStoreCompositeDTO param);
}