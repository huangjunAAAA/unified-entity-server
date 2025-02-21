package com.zjht.unified.service;

import com.wukong.core.mp.base.BaseService;
import com.zjht.unified.entity.FsmCondition;
import com.zjht.unified.dto.*;
import com.zjht.unified.vo.*;
import java.util.List;

/**
 * Service接口
 *
 * @author wangy
 */
public interface IFsmConditionCompositeService {
    Long submit(FsmConditionCompositeDTO entity);
    void removeById(Long id);
    FsmConditionCompositeDTO selectById(Long id);
    FsmConditionCompositeDTO deepCopyById(Long id);  
     /**
     * 该接口会修改entity的主键
     * @param entity
     * @return
     */
    FsmConditionCompositeDTO deepCopy(FsmConditionCompositeDTO entity); 
    void batchRemove(List<Long> entityIdLst);
    void batchSubmit(List<FsmConditionCompositeDTO> entityList);
    List<FsmConditionCompositeDTO> selectList(FsmConditionCompositeDTO param);
    FsmConditionCompositeDTO selectOne(FsmConditionCompositeDTO param);
}