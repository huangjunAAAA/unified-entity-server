package com.zjht.ui.service;

import com.wukong.core.mp.base.BaseService;
import com.zjht.ui.entity.UiLayout;
import com.zjht.ui.dto.*;
import com.zjht.ui.vo.*;
import java.util.List;

/**
 * Service接口
 *
 * @author wangy
 */
public interface IUiLayoutCompositeService {
    Long submit(UiLayoutCompositeDTO entity);
    void removeById(Long id);
    UiLayoutCompositeDTO selectById(Long id);
    UiLayoutCompositeDTO deepCopyById(Long id);  
     /**
     * 该接口会修改entity的主键
     * @param entity
     * @return
     */
    UiLayoutCompositeDTO deepCopy(UiLayoutCompositeDTO entity); 
    void batchRemove(List<Long> entityIdLst);
    void batchSubmit(List<UiLayoutCompositeDTO> entityList);
    List<UiLayoutCompositeDTO> selectList(UiLayoutCompositeDTO param);
    UiLayoutCompositeDTO selectOne(UiLayoutCompositeDTO param);
}