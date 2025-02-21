package com.zjht.unified.service;

import com.wukong.core.mp.base.BaseService;
import com.zjht.unified.entity.ClazzDef;
import com.zjht.unified.dto.*;
import com.zjht.unified.vo.*;
import java.util.List;

/**
 * Service接口
 *
 * @author wangy
 */
public interface IClazzDefCompositeService {
    Long submit(ClazzDefCompositeDTO entity);
    void removeById(Long id);
    ClazzDefCompositeDTO selectById(Long id);
    ClazzDefCompositeDTO deepCopyById(Long id);  
     /**
     * 该接口会修改entity的主键
     * @param entity
     * @return
     */
    ClazzDefCompositeDTO deepCopy(ClazzDefCompositeDTO entity); 
    void batchRemove(List<Long> entityIdLst);
    void batchSubmit(List<ClazzDefCompositeDTO> entityList);
    List<ClazzDefCompositeDTO> selectList(ClazzDefCompositeDTO param);
    ClazzDefCompositeDTO selectOne(ClazzDefCompositeDTO param);
}