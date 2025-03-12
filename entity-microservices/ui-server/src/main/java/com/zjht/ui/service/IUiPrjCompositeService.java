package com.zjht.ui.service;

import com.wukong.core.mp.base.BaseService;
import com.zjht.ui.entity.UiPrj;
import com.zjht.ui.dto.*;
import com.zjht.ui.vo.*;
import java.util.List;

/**
 * Service接口
 *
 * @author wangy
 */
public interface IUiPrjCompositeService {
    Long submit(UiPrjCompositeDTO entity);
    void removeById(Long id);
    UiPrjCompositeDTO selectById(Long id);
    UiPrjCompositeDTO deepCopyById(Long id);  
     /**
     * 该接口会修改entity的主键
     * @param entity
     * @return
     */
    UiPrjCompositeDTO deepCopy(UiPrjCompositeDTO entity); 
    void batchRemove(List<Long> entityIdLst);
    void batchSubmit(List<UiPrjCompositeDTO> entityList);
    List<UiPrjCompositeDTO> selectList(UiPrjCompositeDTO param);
    UiPrjCompositeDTO selectOne(UiPrjCompositeDTO param);
}