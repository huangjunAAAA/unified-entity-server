package com.zjht.ui.service;

import com.wukong.core.mp.base.BaseService;
import com.zjht.ui.entity.UiPage;
import com.zjht.ui.dto.*;
import com.zjht.ui.vo.*;
import java.util.List;

/**
 * Service接口
 *
 * @author wangy
 */
public interface IUiPageCompositeService {
    Long submit(UiPageCompositeDTO entity);
    void removeById(Long id);
    UiPageCompositeDTO selectById(Long id);
    UiPageCompositeDTO deepCopyById(Long id);  
     /**
     * 该接口会修改entity的主键
     * @param entity
     * @return
     */
    UiPageCompositeDTO deepCopy(UiPageCompositeDTO entity); 
    void batchRemove(List<Long> entityIdLst);
    void batchSubmit(List<UiPageCompositeDTO> entityList);
    List<UiPageCompositeDTO> selectList(UiPageCompositeDTO param);
    UiPageCompositeDTO selectOne(UiPageCompositeDTO param);
}