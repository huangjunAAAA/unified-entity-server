package com.zjht.unified.datasource.service.sysproxy.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zjht.unified.datasource.dto.ApiInvokeParam;

import com.zjht.unified.common.core.domain.HttpR;
import com.zjht.unified.common.core.domain.R;
import com.zjht.unified.common.core.util.HttpUtils;
import com.zjht.unified.common.core.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import java.util.*;

@Slf4j
public class Std20Stub extends AbstractUnifiedSysStub {

    @Override
    public RemoteSystemMeta getMeta() {
        RemoteSystemMeta meta=new RemoteSystemMeta();
        meta.authenticationMode="accesstoken";
        meta.sysName="integrated";
        return meta;
    }

    private String token;

    private Long tokenTimeStamp;


    @Override
    public String token(Boolean useCache) {
        if(dataspec==null){
            return null;
        }
        // 缓存60分钟有效
        if(useCache&& StringUtils.isNotEmpty(token) && System.currentTimeMillis()-tokenTimeStamp<60*60000L)
            return token;
        String authUrl=dtpDataSource.getUrl()+"/api/permission/localSso/login";
        Map<String,Object> params=new HashMap<>();
        params.put("loginName",dataspec.getAuthInfo().getApiKey());
        params.put("password",dataspec.getAuthInfo().getApiSecret());

        Map<String,String> headers=new HashMap<>();
        headers.put("ignoreLogging","true");
        HttpR result = HttpUtils.post(authUrl, headers, params, null);
        ObjectMapper om=new ObjectMapper();
        try {
            R<Map<String, Object>> rMap = om.readValue(result.getMsg(), new TypeReference<R<Map<String, Object>>>() {
            });
            token= rMap.getData().get("accessToken")+"";
            tokenTimeStamp=System.currentTimeMillis();
        } catch (JsonProcessingException e) {
            log.error(e.getMessage(),e);
        }
        return token;
    }

    @Override
    public Boolean auth(ApiInvokeParam param) {
        String token=token(param.useCache);
        param.putHeader("accesstoken",token);
        return true;
    }

    private String[] getArguments(String command){
        String[] parts = command.split("\\|");
        if(parts.length<2)
            return null;
        return parts[1].split(",");
    }
}