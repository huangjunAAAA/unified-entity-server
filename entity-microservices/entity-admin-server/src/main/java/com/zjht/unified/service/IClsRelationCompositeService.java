package com.zjht.unified.service;

import com.wukong.core.mp.base.BaseService;
import com.zjht.unified.entity.ClsRelation;
import com.zjht.unified.dto.*;
import com.zjht.unified.vo.*;
import java.util.List;

/**
 * Service接口
 *
 * @author wangy
 */
public interface IClsRelationCompositeService {
    Long submit(ClsRelationCompositeDTO entity);
    void removeById(Long id);
    ClsRelationCompositeDTO selectById(Long id);
    ClsRelationCompositeDTO deepCopyById(Long id);  
     /**
     * 该接口会修改entity的主键
     * @param entity
     * @return
     */
    ClsRelationCompositeDTO deepCopy(ClsRelationCompositeDTO entity); 
    void batchRemove(List<Long> entityIdLst);
    void batchSubmit(List<ClsRelationCompositeDTO> entityList);
    List<ClsRelationCompositeDTO> selectList(ClsRelationCompositeDTO param);
    ClsRelationCompositeDTO selectOne(ClsRelationCompositeDTO param);
}