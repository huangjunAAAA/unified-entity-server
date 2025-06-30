package com.zjht.unified.utils;

import com.google.common.collect.Lists;
import com.zjht.authcenter.permission.util.StringUtils;
import com.zjht.unified.common.core.constants.FieldConstants;
import com.zjht.unified.common.core.domain.ddl.TblCol;
import com.zjht.unified.common.core.domain.ddl.TblIndex;
import com.zjht.unified.common.core.domain.store.EntityStoreMessageDO;
import com.zjht.unified.common.core.util.JsonUtilExt;
import com.zjht.unified.domain.composite.ClazzDefCompositeDO;
import com.zjht.unified.domain.composite.FieldDefCompositeDO;
import com.zjht.unified.service.ctx.TaskContext;
import groovy.util.logging.Slf4j;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class StoreUtil {



    private static EntityStoreMessageDO getStoreMessageDO(ClazzDefCompositeDO classDef) {
        EntityStoreMessageDO messageDO = new EntityStoreMessageDO();
        if (StringUtils.isBlank(classDef.getTbl())) {
            messageDO.setTblName( StringUtils.toUnderScoreCase(classDef.getName()));
        } else {
            messageDO.setTblName( classDef.getTbl());
        }
        ArrayList<TblCol> cols = Lists.newArrayList();
        for (FieldDefCompositeDO fieldDef : classDef.getClazzIdFieldDefList()) {
            TblCol col = new TblCol();
            if(StringUtils.isNotBlank(fieldDef.getTblCol()))
                col.setNameEn(fieldDef.getTblCol());
            else{
                String fname = StringUtils.toUnderScoreCase(fieldDef.getName());
                col.setNameEn(fname);
            }
            col.setNameZh(fieldDef.getDisplayName());
            col.setType(fieldDef.getType());
            col.setJdbcType(null);
            col.setIsPK(Objects.equals(fieldDef.getTblCol(),"id")?1:0);
            col.setIsTempstamp(0);
            cols.add(col);
        }
        messageDO.setCols(cols);
        messageDO.setIndices(Lists.newArrayList());
        return messageDO;
    }

    public static EntityStoreMessageDO getNullMessageDO(ClazzDefCompositeDO classDef, TaskContext taskContext, List<String> nullFields,String guid) {
        EntityStoreMessageDO messageDO = getStoreMessageDO(classDef);
        List<TblCol> fCols = messageDO.getCols().stream().filter(col -> nullFields.contains(col.getNameEn())).collect(Collectors.toList());
        messageDO.setCols(fCols);
        Map<String, Object> newKvMap = new HashMap<>();
        newKvMap.put(FieldConstants.CLAZZ_GUID,classDef.getGuid());
        newKvMap.put(FieldConstants.GUID,guid);
        messageDO.setPrjId(taskContext.getPrjInfo().getPrjId());
        messageDO.setVer(taskContext.getVer());
        messageDO.setData(JsonUtilExt.toJson(Lists.newArrayList(newKvMap)));
        return messageDO;
    }

    public static EntityStoreMessageDO getDelMessageDO(TaskContext taskContext,ClazzDefCompositeDO classDef,String guid) {
        EntityStoreMessageDO messageDO = getStoreMessageDO(classDef);
        messageDO.setPrjId(taskContext.getPrjInfo().getPrjId());
        messageDO.setVer(taskContext.getVer());
        messageDO.setData(guid);
        return messageDO;
    }

    public static EntityStoreMessageDO getStoreMessageDO(ClazzDefCompositeDO classDef, TaskContext taskContext , Map<String, Object> kvMap) {
        EntityStoreMessageDO messageDO = getStoreMessageDO(classDef);
        Map<String, Object> newKvMap = new HashMap<>();
        for (FieldDefCompositeDO fieldDef : classDef.getClazzIdFieldDefList()) {
            if (kvMap.containsKey(fieldDef.getName())) {
                String colName;
                if(StringUtils.isBlank(fieldDef.getTblCol())) {
                    colName = StringUtils.toUnderScoreCase(fieldDef.getName());
                } else {
                    colName = fieldDef.getTblCol();
                }
                newKvMap.put(colName, kvMap.get(fieldDef.getName()));
            }
        }
        if (kvMap.containsKey(FieldConstants.GUID)) {
            newKvMap.put(FieldConstants.GUID,kvMap.get(FieldConstants.GUID));
        }
        newKvMap.put(FieldConstants.CLAZZ_GUID,classDef.getGuid());
        messageDO.setPrjId(taskContext.getPrjInfo().getPrjId());
        messageDO.setVer(taskContext.getVer());
        messageDO.setData(JsonUtilExt.toJson(Lists.newArrayList(newKvMap)));
        messageDO.setVer(taskContext.getVer());
        return messageDO;
    }
}
