package com.zjht.unified.datasource.util;

import com.zjht.unified.common.core.constants.Constants;
import com.zjht.unified.common.core.json.GsonUtil;
import com.zjht.unified.datasource.dto.DataValUpdate;
import com.zjht.unified.datasource.dto.response.Feedback;

public class DataValUtils {

    public static Feedback<DataValUpdate> convertSimpleData(String key, String channel, Object data){
        DataValUpdate dvu=new DataValUpdate();
        DataValUpdate.DataMeta meta=new DataValUpdate.DataMeta();
        meta.setKey(key);
        meta.setTs(System.currentTimeMillis());
        meta.setChannel(channel);
        dvu.setMeta(meta);
        dvu.setData(GsonUtil.toJson(data));
        return convertSimpleDataValUpdate(dvu);
    }

    public static Feedback<DataValUpdate> convertSimpleDataValUpdate(DataValUpdate data){
        Feedback<DataValUpdate> feedback=new Feedback();
        feedback.setFeedbackTs(System.currentTimeMillis());
        feedback.setDataSet(data);
        feedback.setFeedbackType(Constants.FEEDBACK_TYPE_DATA);
        return feedback;
    }
}
