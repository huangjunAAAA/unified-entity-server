package com.zjht.unified.service;

import com.wukong.core.mp.base.BaseService;
import com.zjht.unified.entity.SentinelDef;
import com.zjht.unified.dto.*;
import com.zjht.unified.vo.*;
import java.util.List;

/**
 * Service接口
 *
 * @author wangy
 */
public interface ISentinelDefCompositeService {
    Long submit(SentinelDefCompositeDTO entity);
    void removeById(Long id);
    SentinelDefCompositeDTO selectById(Long id);
    SentinelDefCompositeDTO deepCopyById(Long id);  
     /**
     * 该接口会修改entity的主键
     * @param entity
     * @return
     */
    SentinelDefCompositeDTO deepCopy(SentinelDefCompositeDTO entity); 
    void batchRemove(List<Long> entityIdLst);
    void batchSubmit(List<SentinelDefCompositeDTO> entityList);
    List<SentinelDefCompositeDTO> selectList(SentinelDefCompositeDTO param);
    SentinelDefCompositeDTO selectOne(SentinelDefCompositeDTO param);
}