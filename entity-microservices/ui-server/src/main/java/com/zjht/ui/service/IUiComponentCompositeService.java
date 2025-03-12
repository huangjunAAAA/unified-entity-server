package com.zjht.ui.service;

import com.wukong.core.mp.base.BaseService;
import com.zjht.ui.entity.UiComponent;
import com.zjht.ui.dto.*;
import com.zjht.ui.vo.*;
import java.util.List;

/**
 * Service接口
 *
 * @author wangy
 */
public interface IUiComponentCompositeService {
    Long submit(UiComponentCompositeDTO entity);
    void removeById(Long id);
    UiComponentCompositeDTO selectById(Long id);
    UiComponentCompositeDTO deepCopyById(Long id);  
     /**
     * 该接口会修改entity的主键
     * @param entity
     * @return
     */
    UiComponentCompositeDTO deepCopy(UiComponentCompositeDTO entity); 
    void batchRemove(List<Long> entityIdLst);
    void batchSubmit(List<UiComponentCompositeDTO> entityList);
    List<UiComponentCompositeDTO> selectList(UiComponentCompositeDTO param);
    UiComponentCompositeDTO selectOne(UiComponentCompositeDTO param);
}