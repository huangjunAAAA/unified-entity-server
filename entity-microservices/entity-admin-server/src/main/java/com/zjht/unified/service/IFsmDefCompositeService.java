package com.zjht.unified.service;

import com.wukong.core.mp.base.BaseService;
import com.zjht.unified.entity.FsmDef;
import com.zjht.unified.dto.*;
import com.zjht.unified.vo.*;
import java.util.List;

/**
 * Service接口
 *
 * @author wangy
 */
public interface IFsmDefCompositeService {
    Long submit(FsmDefCompositeDTO entity);
    void removeById(Long id);
    FsmDefCompositeDTO selectById(Long id);
    FsmDefCompositeDTO deepCopyById(Long id);  
     /**
     * 该接口会修改entity的主键
     * @param entity
     * @return
     */
    FsmDefCompositeDTO deepCopy(FsmDefCompositeDTO entity); 
    void batchRemove(List<Long> entityIdLst);
    void batchSubmit(List<FsmDefCompositeDTO> entityList);
    List<FsmDefCompositeDTO> selectList(FsmDefCompositeDTO param);
    FsmDefCompositeDTO selectOne(FsmDefCompositeDTO param);
}