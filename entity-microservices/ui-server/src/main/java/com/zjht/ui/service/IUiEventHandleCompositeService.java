package com.zjht.ui.service;

import com.wukong.core.mp.base.BaseService;
import com.zjht.ui.entity.UiEventHandle;
import com.zjht.ui.dto.*;
import com.zjht.ui.vo.*;
import java.util.List;

/**
 * Service接口
 *
 * @author wangy
 */
public interface IUiEventHandleCompositeService {
    Long submit(UiEventHandleCompositeDTO entity);
    void removeById(Long id);
    UiEventHandleCompositeDTO selectById(Long id);
    UiEventHandleCompositeDTO deepCopyById(Long id);  
     /**
     * 该接口会修改entity的主键
     * @param entity
     * @return
     */
    UiEventHandleCompositeDTO deepCopy(UiEventHandleCompositeDTO entity); 
    void batchRemove(List<Long> entityIdLst);
    void batchSubmit(List<UiEventHandleCompositeDTO> entityList);
    List<UiEventHandleCompositeDTO> selectList(UiEventHandleCompositeDTO param);
    UiEventHandleCompositeDTO selectOne(UiEventHandleCompositeDTO param);
}