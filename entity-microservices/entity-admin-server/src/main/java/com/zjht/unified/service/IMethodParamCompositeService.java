package com.zjht.unified.service;

import com.wukong.core.mp.base.BaseService;
import com.zjht.unified.entity.MethodParam;
import com.zjht.unified.dto.*;
import com.zjht.unified.vo.*;
import java.util.List;

/**
 * Service接口
 *
 * @author wangy
 */
public interface IMethodParamCompositeService {
    Long submit(MethodParamCompositeDTO entity);
    void removeById(Long id);
    MethodParamCompositeDTO selectById(Long id);
    MethodParamCompositeDTO deepCopyById(Long id);  
     /**
     * 该接口会修改entity的主键
     * @param entity
     * @return
     */
    MethodParamCompositeDTO deepCopy(MethodParamCompositeDTO entity); 
    void batchRemove(List<Long> entityIdLst);
    void batchSubmit(List<MethodParamCompositeDTO> entityList);
    List<MethodParamCompositeDTO> selectList(MethodParamCompositeDTO param);
    MethodParamCompositeDTO selectOne(MethodParamCompositeDTO param);
}