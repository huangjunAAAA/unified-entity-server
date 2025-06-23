package com.zjht.unified.data.storage.persist;



import com.zjht.unified.common.core.domain.store.EntityStoreMessageDO;
import com.zjht.unified.domain.composite.ClazzDefCompositeDO;

import java.util.List;
import java.util.Map;

public interface IObjectEntityStore {

    List<Long> saveEntity(EntityStoreMessageDO val);

    List<Integer> updateEntity(EntityStoreMessageDO val);

    List<Map<String,Object>> queryEntity(ClazzDefCompositeDO clazzDef, Integer page, Integer size, String orderby, String asc);

    void deleteEntity(String table, String guid, Long id);
 }
