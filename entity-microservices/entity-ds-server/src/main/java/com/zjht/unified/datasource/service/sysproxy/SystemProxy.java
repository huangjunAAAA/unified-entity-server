package com.zjht.unified.datasource.service.sysproxy;



import com.zjht.unified.datasource.dto.ApiInvokeParam;
import com.zjht.unified.datasource.entity.DtpDataSource;
import com.zjht.unified.common.core.domain.R;

import java.util.List;
import java.util.Map;

public interface SystemProxy {
    void init(DtpDataSource dtpDataSource);
    String token(Boolean useCache);
    Boolean auth(ApiInvokeParam param);
    R getBizObject(String type, String id, Map<String,Object> extra);
    R<List> getBizObjectList(String type, Map<String,Object> extra);
    R<List> getBizObjectHistory(String type, String id,Map<String,Object> extra);
    RemoteSystemMeta getMeta();

    class RemoteSystemMeta{
        public String sysName;
        public String authenticationMode;
    }
}
