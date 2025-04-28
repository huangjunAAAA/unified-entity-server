package com.zjht.unified.datasource.util;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.zjht.unified.datasource.dto.ApiInvokeParam;
import com.zjht.unified.datasource.dto.ApiSpec;
import com.zjht.unified.common.core.util.StringUtils;
import org.apache.commons.text.StringSubstitutor;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ParamUtils {

    public static void setInvokeParam(ApiInvokeParam invokeParam, ApiSpec api){
        if (StringUtils.isNotEmpty(api.getParamTemplate())) {
            if(!isJson(api.getParamTemplate())) {
                MultiValueMap<String, String> params = UriComponentsBuilder.fromUriString("http://localhost?" + api.getParamTemplate()).build().getQueryParams();
                for (Iterator<Map.Entry<String, List<String>>> iterator = params.entrySet().iterator(); iterator.hasNext(); ) {
                    Map.Entry<String, List<String>> param = iterator.next();
                    invokeParam.putParameter(param.getKey(), param.getValue().get(0));
                }
            }else{
                invokeParam.setRequestBody(api.getParamTemplate());
            }
        }
    }

    private static boolean isJson(String json){
        try{
            new ObjectMapper().readValue(json,Object.class);
            return true;
        }catch (Exception e){
            return false;
        }
    }

    public static Double getNonZeroValue(Map<String, Object> map, String... keys) {
        for (int i = 0; i < keys.length; i++) {
            String k = keys[i];
            Object v = map.get(k);
            if (v != null) {
                if (v instanceof Float && (Float) v != 0) {
                    return ((Float) v).doubleValue();
                }
                if (v instanceof Integer && (Integer) v != 0) {
                    return ((Integer) v).doubleValue();
                }
                if (v instanceof Double && (Double) v != 0) {
                    return ((Double) v);
                }
            }
        }
        return null;
    }

    public static String substitue(String source,Map<String,Object> params){
        StringSubstitutor sub = new StringSubstitutor(params);
        String content = sub.replace(source);
        return content;
    }
}
