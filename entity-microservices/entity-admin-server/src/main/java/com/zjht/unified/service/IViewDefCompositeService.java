package com.zjht.unified.service;

import com.wukong.core.mp.base.BaseService;
import com.zjht.unified.entity.ViewDef;
import com.zjht.unified.dto.*;
import com.zjht.unified.vo.*;
import java.util.List;

/**
 * Service接口
 *
 * @author wangy
 */
public interface IViewDefCompositeService {
    Long submit(ViewDefCompositeDTO entity);
    void removeById(Long id);
    ViewDefCompositeDTO selectById(Long id);
    ViewDefCompositeDTO deepCopyById(Long id);  
     /**
     * 该接口会修改entity的主键
     * @param entity
     * @return
     */
    ViewDefCompositeDTO deepCopy(ViewDefCompositeDTO entity); 
    void batchRemove(List<Long> entityIdLst);
    void batchSubmit(List<ViewDefCompositeDTO> entityList);
    List<ViewDefCompositeDTO> selectList(ViewDefCompositeDTO param);
    ViewDefCompositeDTO selectOne(ViewDefCompositeDTO param);
}