package com.zjht.unified.service;

import com.wukong.core.mp.base.BaseService;
import com.zjht.unified.entity.FieldDef;
import com.zjht.unified.dto.*;
import com.zjht.unified.vo.*;
import java.util.List;

/**
 * Service接口
 *
 * @author wangy
 */
public interface IFieldDefCompositeService {
    Long submit(FieldDefCompositeDTO entity);
    void removeById(Long id);
    FieldDefCompositeDTO selectById(Long id);
    FieldDefCompositeDTO deepCopyById(Long id);  
     /**
     * 该接口会修改entity的主键
     * @param entity
     * @return
     */
    FieldDefCompositeDTO deepCopy(FieldDefCompositeDTO entity); 
    void batchRemove(List<Long> entityIdLst);
    void batchSubmit(List<FieldDefCompositeDTO> entityList);
    List<FieldDefCompositeDTO> selectList(FieldDefCompositeDTO param);
    FieldDefCompositeDTO selectOne(FieldDefCompositeDTO param);
}