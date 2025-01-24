package com.zjht.unified.common.core.domain.store;

import com.zjht.unified.common.core.util.JsonUtilExt;
import lombok.Data;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Data
public class StoreMessageDO {
    private Long persistId;
    private Object data;
    private Object processedData;
    private String protocol;
    private String dataType;
    private List<Date> etLst;
    private String sessionId;
    private Long planId;
    private Long colpId;
    private Long driverId;
    private Object previousVal;
    private Map<String,Object> extras=new HashMap<>();

    public static List<Map<String,Object>> getDataAsObjectList(StoreMessageDO sMsg){
        return JsonUtilExt.parseMapList(getStringCastData(sMsg));
    }

    public static String getStringCastData(StoreMessageDO sMsg){
        Object v = getActualData(sMsg);
        if(v==null)
            return null;
        return v.toString();
    }

    public static Double getDoubleCastData(StoreMessageDO sMsg){
        Object v = getActualData(sMsg);
        if(v==null)
            return null;
        if(v instanceof Number){
            return ((Number)v).doubleValue();
        }
        return Double.parseDouble(v.toString());
    }

    public static Object getActualData(StoreMessageDO sMsg){
        if(sMsg.processedData!=null)
            return sMsg.processedData;
        return sMsg.data;
    }
}
