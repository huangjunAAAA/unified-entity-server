package com.zjht.unified.datasource.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wukong.core.weblog.utils.JsonUtil;

import com.zjht.unified.datasource.dto.ApiInvokeParam;
import com.zjht.unified.datasource.dto.OpenApiCollectionSpec;
import com.zjht.unified.datasource.dto.OperationExt;
import com.zjht.unified.datasource.dto.SystemSpec;
import com.zjht.unified.datasource.entity.DtpDataSource;
import com.zjht.unified.datasource.service.sysproxy.SystemProxy;
import com.zjht.unified.datasource.util.ParamUtils;

import com.zjht.unified.common.core.domain.HttpR;
import com.zjht.unified.common.core.domain.R;
import com.zjht.unified.common.core.json.GsonUtil;
import com.zjht.unified.common.core.util.ObjectFieldLocatorUtil;
import com.zjht.unified.common.core.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
public class ApiInvokeService {
    @Autowired
    private IDtpDataSourceService dtpDataSourceService;

    @Autowired
    private SwaggerApiService apiService;

    @Autowired
    private SystemBridgeService systemBridgeService;

    private Map<String, CMethodInfo> baseUrlCache=new ConcurrentHashMap<>();

    @Autowired
    private HttpUrlInvokeService httpUrlInvokeService;



    public HttpR invoke(ApiInvokeParam invokeParam){
        DtpDataSource ds = dtpDataSourceService.getById(invokeParam.getDsId());
        String targetKey=invokeParam.getDsId()+"|"+invokeParam.getPath();
        CMethodInfo opExt2 = baseUrlCache.get(targetKey);
        if(opExt2==null) {
            SystemSpec sysSpec = null;
            try {
                sysSpec = apiService.convert(ds);
            } catch (JsonProcessingException e) {
                log.error(e.getMessage(),e);
                return null;
            }
            for (Iterator<OpenApiCollectionSpec> iterator = sysSpec.getApiSpecs().iterator(); iterator.hasNext(); ) {
                OpenApiCollectionSpec spec = iterator.next();
                List<OperationExt> opLst = apiService.parseSwagger(spec.getSwaggerUrl());
                for (Iterator<OperationExt> opIter = opLst.iterator(); opIter.hasNext(); ) {
                    OperationExt op = opIter.next();
                    String opKey=invokeParam.getDsId()+"|"+op.getPath();
                    if(!baseUrlCache.containsKey(opKey)||op.getMethod().equalsIgnoreCase("post")){
                        CMethodInfo ext2=new CMethodInfo();
                        ext2.baseUrl=spec.getBaseUrl();
                        ext2.opExt=op;
                        baseUrlCache.put(opKey,ext2);
                    }
                }
            }
            opExt2=baseUrlCache.get(targetKey);
        }
        if (opExt2 == null)
            return null;
        if(StringUtils.isEmpty(invokeParam.getMethod())){
            invokeParam.setMethod(opExt2.opExt.getMethod());
        }
        if(opExt2.opExt.getRequestBody()!=null && invokeParam.getRequestBody()==null){
            invokeParam.setRequestBody("{}");
        }
        SystemProxy proxy = systemBridgeService.createProxy(ds.getId());
        if(proxy!=null){
            proxy.auth(invokeParam);
        }
        String baseUrl=StringUtils.isNotEmpty(invokeParam.getBaseUrl())?invokeParam.getBaseUrl():opExt2.baseUrl;
        return invoke(invokeParam, baseUrl);
    }

    private static class CMethodInfo {
        private OperationExt opExt;
        private String baseUrl;
    }

    public HttpR invoke(ApiInvokeParam invokeParam, String baseUrl){
        long ts=System.currentTimeMillis();
        String url=baseUrl+invokeParam.getPath();
        HashMap<String, Object> params = new HashMap<>(invokeParam.getParams());
        Pattern pathVar=Pattern.compile("\\{([a-zA-Z_]+\\w*)\\}");
        Matcher m = pathVar.matcher(url);
        if(m.find()){
            for (int i = 0; i < m.groupCount(); i++) {
                String pvar=m.group(i);
                String pv = pvar.replace("{","").replace("}","");
                Object actual = params.remove(pv);
                if(actual==null){
                    throw new RuntimeException(pv+" path variable not found:"+url);
                }
                url=url.replace(pvar,actual+"");
            }
        }
        if(StringUtils.isNotEmpty(invokeParam.getRequestBody())&& !CollectionUtils.isEmpty(params)){
            String content= ParamUtils.substitue(invokeParam.getRequestBody(),params);
            invokeParam.setRequestBody(content);
        }

        HttpR result=null;
        if(StringUtils.isNotEmpty(invokeParam.getRequestBody())){
            if(!params.isEmpty()){
                HashMap<String, Object> jsonParam = JsonUtil.parse(invokeParam.getRequestBody(), new TypeReference<HashMap<String, Object>>() {
                });
                jsonParam.putAll(params);
                invokeParam.setRequestBody(JsonUtil.toJson(jsonParam));
            }
            result= httpUrlInvokeService.postJSON(url,invokeParam.getHeaders(),invokeParam.getRequestBody(),null);
        }else{
            if(invokeParam.getMethod().equals("post")){
                result= httpUrlInvokeService.post(url,invokeParam.getHeaders(),params,null);
            }else{
                result= httpUrlInvokeService.get(url,invokeParam.getHeaders(),params,null);
            }
        }
        if(result==null||StringUtils.isEmpty(result.getMsg()))
            log.error("invoke url:"+url+", time consumes:"+(System.currentTimeMillis()-ts)+" ms, result:"+ GsonUtil.toJson(result));
        return result;
    }


    public HttpR repeatedInvoke(ApiInvokeParam param) {
        for (int i = 0; i < 10; i++) {
            HttpR result = invoke(param);
            if (result.getCode() == 200) {
                R r = GsonUtil.fromJson(result.getMsg(), R.class);
                if (r.getCode() == 401 || r.getCode() == 100020) {
                    param.useCache = false;
                    continue;
                } else {
                    param.useCache = true;
                }
            }
            log.debug(param.getPath() + ":" + GsonUtil.toJson(result));
            return result;
        }
        log.warn(param.getPath() + ", 401 failed");
        return HttpR.fail(401, "");
    }

    public static void main(String[] args) throws JsonProcessingException {
        String  result="{\"code\":200,\"msg\":\"{\\\"code\\\":0,\\\"msg\\\":null,\\\"count\\\":0,\\\"data\\\":{\\\"records\\\":[{\\\"protocol\\\":\\\"gb28181\\\",\\\"id\\\":\\\"28181001315000005000/00871486411311188197\\\",\\\"deviceId\\\":\\\"28181001315000005000\\\",\\\"channelId\\\":\\\"00871486411311188197\\\",\\\"name\\\":\\\"景迈大寨三岔路口1\\\",\\\"provinceCode\\\":null,\\\"cityCode\\\":null,\\\"districtCode\\\":null,\\\"entityCode\\\":null,\\\"entityName\\\":null,\\\"longitude\\\":0.0,\\\"latitude\\\":0.0,\\\"manufacturer\\\":\\\"HuaWei\\\",\\\"deviceType\\\":null,\\\"addWay\\\":null,\\\"apiKey\\\":null,\\\"sourceStatus\\\":0,\\\"apiSecret\\\":null,\\\"currentAddress\\\":null,\\\"ip\\\":\\\"192.168.18.61\\\",\\\"updateTs\\\":\\\"2023-08-30 12:14:06\\\",\\\"reqUri\\\":\\\"http://wvp.zjht100.com\\\",\\\"platform\\\":\\\"39.129.183.150\\\",\\\"sort\\\":null},{\\\"protocol\\\":\\\"gb28181\\\",\\\"id\\\":\\\"41162600002000000111/41160201581314000069\\\",\\\"deviceId\\\":\\\"41162600002000000111\\\",\\\"channelId\\\":\\\"41160201581314000069\\\",\\\"name\\\":\\\"二殿正门东\\\",\\\"provinceCode\\\":null,\\\"cityCode\\\":null,\\\"districtCode\\\":null,\\\"entityCode\\\":null,\\\"entityName\\\":null,\\\"longitude\\\":0.0,\\\"latitude\\\":0.0,\\\"manufacturer\\\":\\\"dahua\\\",\\\"deviceType\\\":null,\\\"addWay\\\":null,\\\"apiKey\\\":null,\\\"sourceStatus\\\":0,\\\"apiSecret\\\":null,\\\"currentAddress\\\":null,\\\"ip\\\":\\\"192.168.1.79\\\",\\\"updateTs\\\":\\\"2023-01-31 16:38:40\\\",\\\"reqUri\\\":\\\"http://wvp.zjht100.com\\\",\\\"platform\\\":\\\"61.163.190.253\\\",\\\"sort\\\":null},{\\\"protocol\\\":\\\"gb28181\\\",\\\"id\\\":\\\"50011205082000000001/44010200491320000090\\\",\\\"deviceId\\\":\\\"50011205082000000001\\\",\\\"channelId\\\":\\\"44010200491320000090\\\",\\\"name\\\":\\\"后门梯坎路4\\\",\\\"provinceCode\\\":null,\\\"cityCode\\\":null,\\\"districtCode\\\":null,\\\"entityCode\\\":null,\\\"entityName\\\":null,\\\"longitude\\\":0.0,\\\"latitude\\\":0.0,\\\"manufacturer\\\":\\\"uniview\\\",\\\"deviceType\\\":null,\\\"addWay\\\":null,\\\"apiKey\\\":null,\\\"sourceStatus\\\":2,\\\"apiSecret\\\":null,\\\"currentAddress\\\":null,\\\"ip\\\":\\\"192.168.10.48\\\",\\\"updateTs\\\":\\\"2023-08-30 12:14:43\\\",\\\"reqUri\\\":\\\"http://wvp.zjht100.com\\\",\\\"platform\\\":\\\"218.206.6.74\\\",\\\"sort\\\":null},{\\\"protocol\\\":\\\"gb28181\\\",\\\"id\\\":\\\"50011205082000000001/44010200491320000080\\\",\\\"deviceId\\\":\\\"50011205082000000001\\\",\\\"channelId\\\":\\\"44010200491320000080\\\",\\\"name\\\":\\\"机房后路口3\\\",\\\"provinceCode\\\":null,\\\"cityCode\\\":null,\\\"districtCode\\\":null,\\\"entityCode\\\":null,\\\"entityName\\\":null,\\\"longitude\\\":0.0,\\\"latitude\\\":0.0,\\\"manufacturer\\\":\\\"uniview\\\",\\\"deviceType\\\":null,\\\"addWay\\\":null,\\\"apiKey\\\":null,\\\"sourceStatus\\\":0,\\\"apiSecret\\\":null,\\\"currentAddress\\\":null,\\\"ip\\\":\\\"192.168.10.87\\\",\\\"updateTs\\\":\\\"2023-08-30 12:14:43\\\",\\\"reqUri\\\":\\\"http://wvp.zjht100.com\\\",\\\"platform\\\":\\\"218.206.6.74\\\",\\\"sort\\\":null},{\\\"protocol\\\":\\\"gb28181\\\",\\\"id\\\":\\\"41162600002000000111/41160201581314000186\\\",\\\"deviceId\\\":\\\"41162600002000000111\\\",\\\"channelId\\\":\\\"41160201581314000186\\\",\\\"name\\\":\\\"太昊陵码头北2\\\",\\\"provinceCode\\\":null,\\\"cityCode\\\":null,\\\"districtCode\\\":null,\\\"entityCode\\\":null,\\\"entityName\\\":null,\\\"longitude\\\":0.0,\\\"latitude\\\":0.0,\\\"manufacturer\\\":\\\"hikvision\\\",\\\"deviceType\\\":null,\\\"addWay\\\":null,\\\"apiKey\\\":null,\\\"sourceStatus\\\":0,\\\"apiSecret\\\":null,\\\"currentAddress\\\":null,\\\"ip\\\":\\\"172.1.6.244\\\",\\\"updateTs\\\":\\\"2023-01-31 16:38:40\\\",\\\"reqUri\\\":\\\"http://wvp.zjht100.com\\\",\\\"platform\\\":\\\"61.163.190.253\\\",\\\"sort\\\":null},{\\\"protocol\\\":\\\"gb28181\\\",\\\"id\\\":\\\"41162600002000000111/41160201581314000065\\\",\\\"deviceId\\\":\\\"41162600002000000111\\\",\\\"channelId\\\":\\\"41160201581314000065\\\",\\\"name\\\":\\\"午朝门正门东\\\",\\\"provinceCode\\\":null,\\\"cityCode\\\":null,\\\"districtCode\\\":null,\\\"entityCode\\\":null,\\\"entityName\\\":null,\\\"longitude\\\":0.0,\\\"latitude\\\":0.0,\\\"manufacturer\\\":\\\"dahua\\\",\\\"deviceType\\\":null,\\\"addWay\\\":null,\\\"apiKey\\\":null,\\\"sourceStatus\\\":0,\\\"apiSecret\\\":null,\\\"currentAddress\\\":null,\\\"ip\\\":\\\"192.168.1.136\\\",\\\"updateTs\\\":\\\"2023-01-31 16:38:40\\\",\\\"reqUri\\\":\\\"http://wvp.zjht100.com\\\",\\\"platform\\\":\\\"61.163.190.253\\\",\\\"sort\\\":null},{\\\"protocol\\\":\\\"gb28181\\\",\\\"id\\\":\\\"41162600002000000111/41160201581314000187\\\",\\\"deviceId\\\":\\\"41162600002000000111\\\",\\\"channelId\\\":\\\"41160201581314000187\\\",\\\"name\\\":\\\"太昊陵码头凉亭北4\\\",\\\"provinceCode\\\":null,\\\"cityCode\\\":null,\\\"districtCode\\\":null,\\\"entityCode\\\":null,\\\"entityName\\\":null,\\\"longitude\\\":0.0,\\\"latitude\\\":0.0,\\\"manufacturer\\\":\\\"hikvision\\\",\\\"deviceType\\\":null,\\\"addWay\\\":null,\\\"apiKey\\\":null,\\\"sourceStatus\\\":0,\\\"apiSecret\\\":null,\\\"currentAddress\\\":null,\\\"ip\\\":\\\"172.1.6.244\\\",\\\"updateTs\\\":\\\"2023-01-31 16:38:40\\\",\\\"reqUri\\\":\\\"http://wvp.zjht100.com\\\",\\\"platform\\\":\\\"61.163.190.253\\\",\\\"sort\\\":null},{\\\"protocol\\\":\\\"gb28181\\\",\\\"id\\\":\\\"41162600002000000111/41160201581314000066\\\",\\\"deviceId\\\":\\\"41162600002000000111\\\",\\\"channelId\\\":\\\"41160201581314000066\\\",\\\"name\\\":\\\"IPC\\\",\\\"provinceCode\\\":null,\\\"cityCode\\\":null,\\\"districtCode\\\":null,\\\"entityCode\\\":null,\\\"entityName\\\":null,\\\"longitude\\\":0.0,\\\"latitude\\\":0.0,\\\"manufacturer\\\":\\\"dahua\\\",\\\"deviceType\\\":null,\\\"addWay\\\":null,\\\"apiKey\\\":null,\\\"sourceStatus\\\":2,\\\"apiSecret\\\":null,\\\"currentAddress\\\":null,\\\"ip\\\":\\\"192.168.1.163\\\",\\\"updateTs\\\":\\\"2023-01-31 16:38:40\\\",\\\"reqUri\\\":\\\"http://wvp.zjht100.com\\\",\\\"platform\\\":\\\"61.163.190.253\\\",\\\"sort\\\":null},{\\\"protocol\\\":\\\"gb28181\\\",\\\"id\\\":\\\"41162600002000000111/41160201581314000188\\\",\\\"deviceId\\\":\\\"41162600002000000111\\\",\\\"channelId\\\":\\\"41160201581314000188\\\",\\\"name\\\":\\\"太昊陵凉亭北3\\\",\\\"provinceCode\\\":null,\\\"cityCode\\\":null,\\\"districtCode\\\":null,\\\"entityCode\\\":null,\\\"entityName\\\":null,\\\"longitude\\\":0.0,\\\"latitude\\\":0.0,\\\"manufacturer\\\":\\\"hikvision\\\",\\\"deviceType\\\":null,\\\"addWay\\\":null,\\\"apiKey\\\":null,\\\"sourceStatus\\\":0,\\\"apiSecret\\\":null,\\\"currentAddress\\\":null,\\\"ip\\\":\\\"172.1.6.244\\\",\\\"updateTs\\\":\\\"2023-01-31 16:38:40\\\",\\\"reqUri\\\":\\\"http://wvp.zjht100.com\\\",\\\"platform\\\":\\\"61.163.190.253\\\",\\\"sort\\\":null},{\\\"protocol\\\":\\\"gb28181\\\",\\\"id\\\":\\\"41162600002000000111/41160201581314000067\\\",\\\"deviceId\\\":\\\"41162600002000000111\\\",\\\"channelId\\\":\\\"41160201581314000067\\\",\\\"name\\\":\\\"显仁殿西小门\\\",\\\"provinceCode\\\":null,\\\"cityCode\\\":null,\\\"districtCode\\\":null,\\\"entityCode\\\":null,\\\"entityName\\\":null,\\\"longitude\\\":0.0,\\\"latitude\\\":0.0,\\\"manufacturer\\\":\\\"dahua\\\",\\\"deviceType\\\":null,\\\"addWay\\\":null,\\\"apiKey\\\":null,\\\"sourceStatus\\\":0,\\\"apiSecret\\\":null,\\\"currentAddress\\\":null,\\\"ip\\\":\\\"192.168.1.73\\\",\\\"updateTs\\\":\\\"2023-01-31 16:38:40\\\",\\\"reqUri\\\":\\\"http://wvp.zjht100.com\\\",\\\"platform\\\":\\\"61.163.190.253\\\",\\\"sort\\\":null}],\\\"total\\\":692,\\\"size\\\":10,\\\"current\\\":1,\\\"orders\\\":[],\\\"optimizeCountSql\\\":true,\\\"hitCount\\\":false,\\\"countId\\\":null,\\\"maxLimit\\\":null,\\\"searchCount\\\":true,\\\"pages\\\":70}}\"}\n" +
                "2023-08-30 12:15:26.160  INFO 40152 --- [           main] c.z.d.t.d.s.impl.AbstractDTwinStub       : /video/list:{\"code\":200,\"msg\":\"{\\\"code\\\":0,\\\"msg\\\":null,\\\"count\\\":0,\\\"data\\\":{\\\"records\\\":[{\\\"protocol\\\":\\\"gb28181\\\",\\\"id\\\":\\\"28181001315000005000/00871486411311188197\\\",\\\"deviceId\\\":\\\"28181001315000005000\\\",\\\"channelId\\\":\\\"00871486411311188197\\\",\\\"name\\\":\\\"景迈大寨三岔路口1\\\",\\\"provinceCode\\\":null,\\\"cityCode\\\":null,\\\"districtCode\\\":null,\\\"entityCode\\\":null,\\\"entityName\\\":null,\\\"longitude\\\":0.0,\\\"latitude\\\":0.0,\\\"manufacturer\\\":\\\"HuaWei\\\",\\\"deviceType\\\":null,\\\"addWay\\\":null,\\\"apiKey\\\":null,\\\"sourceStatus\\\":0,\\\"apiSecret\\\":null,\\\"currentAddress\\\":null,\\\"ip\\\":\\\"192.168.18.61\\\",\\\"updateTs\\\":\\\"2023-08-30 12:14:06\\\",\\\"reqUri\\\":\\\"http://wvp.zjht100.com\\\",\\\"platform\\\":\\\"39.129.183.150\\\",\\\"sort\\\":null},{\\\"protocol\\\":\\\"gb28181\\\",\\\"id\\\":\\\"41162600002000000111/41160201581314000069\\\",\\\"deviceId\\\":\\\"41162600002000000111\\\",\\\"channelId\\\":\\\"41160201581314000069\\\",\\\"name\\\":\\\"二殿正门东\\\",\\\"provinceCode\\\":null,\\\"cityCode\\\":null,\\\"districtCode\\\":null,\\\"entityCode\\\":null,\\\"entityName\\\":null,\\\"longitude\\\":0.0,\\\"latitude\\\":0.0,\\\"manufacturer\\\":\\\"dahua\\\",\\\"deviceType\\\":null,\\\"addWay\\\":null,\\\"apiKey\\\":null,\\\"sourceStatus\\\":0,\\\"apiSecret\\\":null,\\\"currentAddress\\\":null,\\\"ip\\\":\\\"192.168.1.79\\\",\\\"updateTs\\\":\\\"2023-01-31 16:38:40\\\",\\\"reqUri\\\":\\\"http://wvp.zjht100.com\\\",\\\"platform\\\":\\\"61.163.190.253\\\",\\\"sort\\\":null},{\\\"protocol\\\":\\\"gb28181\\\",\\\"id\\\":\\\"50011205082000000001/44010200491320000090\\\",\\\"deviceId\\\":\\\"50011205082000000001\\\",\\\"channelId\\\":\\\"44010200491320000090\\\",\\\"name\\\":\\\"后门梯坎路4\\\",\\\"provinceCode\\\":null,\\\"cityCode\\\":null,\\\"districtCode\\\":null,\\\"entityCode\\\":null,\\\"entityName\\\":null,\\\"longitude\\\":0.0,\\\"latitude\\\":0.0,\\\"manufacturer\\\":\\\"uniview\\\",\\\"deviceType\\\":null,\\\"addWay\\\":null,\\\"apiKey\\\":null,\\\"sourceStatus\\\":2,\\\"apiSecret\\\":null,\\\"currentAddress\\\":null,\\\"ip\\\":\\\"192.168.10.48\\\",\\\"updateTs\\\":\\\"2023-08-30 12:14:43\\\",\\\"reqUri\\\":\\\"http://wvp.zjht100.com\\\",\\\"platform\\\":\\\"218.206.6.74\\\",\\\"sort\\\":null},{\\\"protocol\\\":\\\"gb28181\\\",\\\"id\\\":\\\"50011205082000000001/44010200491320000080\\\",\\\"deviceId\\\":\\\"50011205082000000001\\\",\\\"channelId\\\":\\\"44010200491320000080\\\",\\\"name\\\":\\\"机房后路口3\\\",\\\"provinceCode\\\":null,\\\"cityCode\\\":null,\\\"districtCode\\\":null,\\\"entityCode\\\":null,\\\"entityName\\\":null,\\\"longitude\\\":0.0,\\\"latitude\\\":0.0,\\\"manufacturer\\\":\\\"uniview\\\",\\\"deviceType\\\":null,\\\"addWay\\\":null,\\\"apiKey\\\":null,\\\"sourceStatus\\\":0,\\\"apiSecret\\\":null,\\\"currentAddress\\\":null,\\\"ip\\\":\\\"192.168.10.87\\\",\\\"updateTs\\\":\\\"2023-08-30 12:14:43\\\",\\\"reqUri\\\":\\\"http://wvp.zjht100.com\\\",\\\"platform\\\":\\\"218.206.6.74\\\",\\\"sort\\\":null},{\\\"protocol\\\":\\\"gb28181\\\",\\\"id\\\":\\\"41162600002000000111/41160201581314000186\\\",\\\"deviceId\\\":\\\"41162600002000000111\\\",\\\"channelId\\\":\\\"41160201581314000186\\\",\\\"name\\\":\\\"太昊陵码头北2\\\",\\\"provinceCode\\\":null,\\\"cityCode\\\":null,\\\"districtCode\\\":null,\\\"entityCode\\\":null,\\\"entityName\\\":null,\\\"longitude\\\":0.0,\\\"latitude\\\":0.0,\\\"manufacturer\\\":\\\"hikvision\\\",\\\"deviceType\\\":null,\\\"addWay\\\":null,\\\"apiKey\\\":null,\\\"sourceStatus\\\":0,\\\"apiSecret\\\":null,\\\"currentAddress\\\":null,\\\"ip\\\":\\\"172.1.6.244\\\",\\\"updateTs\\\":\\\"2023-01-31 16:38:40\\\",\\\"reqUri\\\":\\\"http://wvp.zjht100.com\\\",\\\"platform\\\":\\\"61.163.190.253\\\",\\\"sort\\\":null},{\\\"protocol\\\":\\\"gb28181\\\",\\\"id\\\":\\\"41162600002000000111/41160201581314000065\\\",\\\"deviceId\\\":\\\"41162600002000000111\\\",\\\"channelId\\\":\\\"41160201581314000065\\\",\\\"name\\\":\\\"午朝门正门东\\\",\\\"provinceCode\\\":null,\\\"cityCode\\\":null,\\\"districtCode\\\":null,\\\"entityCode\\\":null,\\\"entityName\\\":null,\\\"longitude\\\":0.0,\\\"latitude\\\":0.0,\\\"manufacturer\\\":\\\"dahua\\\",\\\"deviceType\\\":null,\\\"addWay\\\":null,\\\"apiKey\\\":null,\\\"sourceStatus\\\":0,\\\"apiSecret\\\":null,\\\"currentAddress\\\":null,\\\"ip\\\":\\\"192.168.1.136\\\",\\\"updateTs\\\":\\\"2023-01-31 16:38:40\\\",\\\"reqUri\\\":\\\"http://wvp.zjht100.com\\\",\\\"platform\\\":\\\"61.163.190.253\\\",\\\"sort\\\":null},{\\\"protocol\\\":\\\"gb28181\\\",\\\"id\\\":\\\"41162600002000000111/41160201581314000187\\\",\\\"deviceId\\\":\\\"41162600002000000111\\\",\\\"channelId\\\":\\\"41160201581314000187\\\",\\\"name\\\":\\\"太昊陵码头凉亭北4\\\",\\\"provinceCode\\\":null,\\\"cityCode\\\":null,\\\"districtCode\\\":null,\\\"entityCode\\\":null,\\\"entityName\\\":null,\\\"longitude\\\":0.0,\\\"latitude\\\":0.0,\\\"manufacturer\\\":\\\"hikvision\\\",\\\"deviceType\\\":null,\\\"addWay\\\":null,\\\"apiKey\\\":null,\\\"sourceStatus\\\":0,\\\"apiSecret\\\":null,\\\"currentAddress\\\":null,\\\"ip\\\":\\\"172.1.6.244\\\",\\\"updateTs\\\":\\\"2023-01-31 16:38:40\\\",\\\"reqUri\\\":\\\"http://wvp.zjht100.com\\\",\\\"platform\\\":\\\"61.163.190.253\\\",\\\"sort\\\":null},{\\\"protocol\\\":\\\"gb28181\\\",\\\"id\\\":\\\"41162600002000000111/41160201581314000066\\\",\\\"deviceId\\\":\\\"41162600002000000111\\\",\\\"channelId\\\":\\\"41160201581314000066\\\",\\\"name\\\":\\\"IPC\\\",\\\"provinceCode\\\":null,\\\"cityCode\\\":null,\\\"districtCode\\\":null,\\\"entityCode\\\":null,\\\"entityName\\\":null,\\\"longitude\\\":0.0,\\\"latitude\\\":0.0,\\\"manufacturer\\\":\\\"dahua\\\",\\\"deviceType\\\":null,\\\"addWay\\\":null,\\\"apiKey\\\":null,\\\"sourceStatus\\\":2,\\\"apiSecret\\\":null,\\\"currentAddress\\\":null,\\\"ip\\\":\\\"192.168.1.163\\\",\\\"updateTs\\\":\\\"2023-01-31 16:38:40\\\",\\\"reqUri\\\":\\\"http://wvp.zjht100.com\\\",\\\"platform\\\":\\\"61.163.190.253\\\",\\\"sort\\\":null},{\\\"protocol\\\":\\\"gb28181\\\",\\\"id\\\":\\\"41162600002000000111/41160201581314000188\\\",\\\"deviceId\\\":\\\"41162600002000000111\\\",\\\"channelId\\\":\\\"41160201581314000188\\\",\\\"name\\\":\\\"太昊陵凉亭北3\\\",\\\"provinceCode\\\":null,\\\"cityCode\\\":null,\\\"districtCode\\\":null,\\\"entityCode\\\":null,\\\"entityName\\\":null,\\\"longitude\\\":0.0,\\\"latitude\\\":0.0,\\\"manufacturer\\\":\\\"hikvision\\\",\\\"deviceType\\\":null,\\\"addWay\\\":null,\\\"apiKey\\\":null,\\\"sourceStatus\\\":0,\\\"apiSecret\\\":null,\\\"currentAddress\\\":null,\\\"ip\\\":\\\"172.1.6.244\\\",\\\"updateTs\\\":\\\"2023-01-31 16:38:40\\\",\\\"reqUri\\\":\\\"http://wvp.zjht100.com\\\",\\\"platform\\\":\\\"61.163.190.253\\\",\\\"sort\\\":null},{\\\"protocol\\\":\\\"gb28181\\\",\\\"id\\\":\\\"41162600002000000111/41160201581314000067\\\",\\\"deviceId\\\":\\\"41162600002000000111\\\",\\\"channelId\\\":\\\"41160201581314000067\\\",\\\"name\\\":\\\"显仁殿西小门\\\",\\\"provinceCode\\\":null,\\\"cityCode\\\":null,\\\"districtCode\\\":null,\\\"entityCode\\\":null,\\\"entityName\\\":null,\\\"longitude\\\":0.0,\\\"latitude\\\":0.0,\\\"manufacturer\\\":\\\"dahua\\\",\\\"deviceType\\\":null,\\\"addWay\\\":null,\\\"apiKey\\\":null,\\\"sourceStatus\\\":0,\\\"apiSecret\\\":null,\\\"currentAddress\\\":null,\\\"ip\\\":\\\"192.168.1.73\\\",\\\"updateTs\\\":\\\"2023-01-31 16:38:40\\\",\\\"reqUri\\\":\\\"http://wvp.zjht100.com\\\",\\\"platform\\\":\\\"61.163.190.253\\\",\\\"sort\\\":null}],\\\"total\\\":692,\\\"size\\\":10,\\\"current\\\":1,\\\"orders\\\":[],\\\"optimizeCountSql\\\":true,\\\"hitCount\\\":false,\\\"countId\\\":null,\\\"maxLimit\\\":null,\\\"searchCount\\\":true,\\\"pages\\\":70}}\"}";

        HttpR r=new ObjectMapper().readValue(result,HttpR.class);
        R r2=new ObjectMapper().readValue(r.getMsg(), R.class);
        System.out.println(ObjectFieldLocatorUtil.extractMapList(r2.getData()));

        String id="RTSP/12312412";
        String pureId = id.replaceFirst("\\w+/", "");
        System.out.println(pureId);
    }
}
