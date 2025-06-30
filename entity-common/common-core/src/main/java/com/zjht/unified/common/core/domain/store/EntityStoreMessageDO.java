package com.zjht.unified.common.core.domain.store;

import com.zjht.unified.common.core.domain.ddl.TblCol;
import com.zjht.unified.common.core.domain.ddl.TblIndex;
import com.zjht.unified.common.core.util.JsonUtilExt;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class EntityStoreMessageDO {
    private Object data;

    private String tblName;

    private List<TblCol> cols;

    private List<TblIndex> indices;

    private Long prjId;

    private String ver;

    public static List<Map<String,Object>> getDataAsObjectList(EntityStoreMessageDO sMsg){
        return JsonUtilExt.parseMapList(getStringCastData(sMsg));
    }

    public static String getStringCastData(EntityStoreMessageDO sMsg){
        Object v = getActualData(sMsg);
        if(v==null)
            return null;
        return v.toString();
    }

    public static Double getDoubleCastData(EntityStoreMessageDO sMsg){
        Object v = getActualData(sMsg);
        if(v==null)
            return null;
        if(v instanceof Number){
            return ((Number)v).doubleValue();
        }
        return Double.parseDouble(v.toString());
    }

    public static Object getActualData(EntityStoreMessageDO sMsg){
//        if(sMsg.processedData!=null)
//            return sMsg.processedData;
        return sMsg.data;
    }
}

