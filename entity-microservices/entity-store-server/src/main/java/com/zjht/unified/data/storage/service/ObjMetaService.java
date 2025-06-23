package com.zjht.unified.data.storage.service;

import com.zjht.unified.common.core.domain.R;
import com.zjht.unified.common.core.domain.dto.GetParam;
import com.zjht.unified.domain.composite.ClazzDefCompositeDO;
import com.zjht.unified.feign.RemoteRT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ObjMetaService {

    @Autowired
    private RemoteRT remoteRT;

    public String createQuery(String clazzName,String clazzGuid,String ver){
        GetParam param = new GetParam();
        param.setObjGuid(clazzGuid);
        param.setObjName(clazzName);
        param.setVer(ver);
        R<ClazzDefCompositeDO> cls = remoteRT.getObjectClassDef(param);
        return null;
    }


}
