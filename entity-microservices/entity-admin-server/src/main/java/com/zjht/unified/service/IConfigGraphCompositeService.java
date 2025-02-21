package com.zjht.unified.service;

import com.wukong.core.mp.base.BaseService;
import com.zjht.unified.entity.ConfigGraph;
import com.zjht.unified.dto.*;
import com.zjht.unified.vo.*;
import java.util.List;

/**
 * Service接口
 *
 * @author wangy
 */
public interface IConfigGraphCompositeService {
    Long submit(ConfigGraphCompositeDTO entity);
    void removeById(Long id);
    ConfigGraphCompositeDTO selectById(Long id);
    ConfigGraphCompositeDTO deepCopyById(Long id);  
     /**
     * 该接口会修改entity的主键
     * @param entity
     * @return
     */
    ConfigGraphCompositeDTO deepCopy(ConfigGraphCompositeDTO entity); 
    void batchRemove(List<Long> entityIdLst);
    void batchSubmit(List<ConfigGraphCompositeDTO> entityList);
    List<ConfigGraphCompositeDTO> selectList(ConfigGraphCompositeDTO param);
    ConfigGraphCompositeDTO selectOne(ConfigGraphCompositeDTO param);
}