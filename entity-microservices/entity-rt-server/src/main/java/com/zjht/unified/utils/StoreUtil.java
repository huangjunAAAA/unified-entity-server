package com.zjht.unified.utils;

import com.google.common.collect.Lists;
import com.zjht.unified.common.core.domain.ddl.TblCol;
import com.zjht.unified.common.core.domain.ddl.TblIndex;
import com.zjht.unified.common.core.domain.store.EntityStoreMessageDO;
import com.zjht.unified.common.core.util.JsonUtilExt;
import com.zjht.unified.domain.composite.ClazzDefCompositeDO;
import com.zjht.unified.domain.composite.FieldDefCompositeDO;
import com.zjht.unified.service.ctx.TaskContext;
import groovy.util.logging.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class StoreUtil {

    public static EntityStoreMessageDO getStoreMessageDO(ClazzDefCompositeDO classDef, TaskContext taskContext , Map<String, Object> kvMap) {
        EntityStoreMessageDO messageDO = new EntityStoreMessageDO();

        ArrayList<TblCol> cols = Lists.newArrayList();
        ArrayList<TblIndex> indices = Lists.newArrayList();
        messageDO.setTblName( classDef.getTbl());
        Map<String, Object> newKvMap = new HashMap<>();
        for (FieldDefCompositeDO fieldDef : classDef.getClazzIdFieldDefList()) {
            TblCol col = new TblCol();
            col.setNameEn(fieldDef.getTblCol());
            col.setNameZh(fieldDef.getDisplayName());
            col.setType(fieldDef.getType());
            col.setJdbcType(null);
            col.setIsPK(0);
            col.setIsTempstamp(0);
            cols.add(col);
            if (kvMap.containsKey(fieldDef.getName())) {
                newKvMap.put(fieldDef.getTblCol(), kvMap.get(fieldDef.getName()));
            }
        }
        messageDO.setCols(cols);
        messageDO.setIndices(indices);
        messageDO.setPrjId(taskContext.getPrjSpec().getUiPrj().getId());
        messageDO.setData(JsonUtilExt.toJson(Lists.newArrayList(newKvMap)));
        return messageDO;
    }
}
