package com.zjht.unified.service;

import com.wukong.core.mp.base.BaseService;
import com.zjht.unified.entity.MethodDef;
import com.zjht.unified.dto.*;
import com.zjht.unified.vo.*;
import java.util.List;

/**
 * Service接口
 *
 * @author wangy
 */
public interface IMethodDefCompositeService {
    Long submit(MethodDefCompositeDTO entity);
    void removeById(Long id);
    MethodDefCompositeDTO selectById(Long id);
    MethodDefCompositeDTO deepCopyById(Long id);  
     /**
     * 该接口会修改entity的主键
     * @param entity
     * @return
     */
    MethodDefCompositeDTO deepCopy(MethodDefCompositeDTO entity); 
    void batchRemove(List<Long> entityIdLst);
    void batchSubmit(List<MethodDefCompositeDTO> entityList);
    List<MethodDefCompositeDTO> selectList(MethodDefCompositeDTO param);
    MethodDefCompositeDTO selectOne(MethodDefCompositeDTO param);
}