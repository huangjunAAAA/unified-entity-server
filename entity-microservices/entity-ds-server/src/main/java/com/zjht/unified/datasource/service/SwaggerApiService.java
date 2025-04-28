package com.zjht.unified.datasource.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.zjht.unified.datasource.dto.OperationExt;
import com.zjht.unified.datasource.dto.SystemSpec;
import com.zjht.unified.datasource.dto.ThirdAuthInfo;
import com.zjht.unified.datasource.entity.DtpDataSource;
import com.zjht.unified.datasource.service.sysproxy.SystemProxy;

import com.zjht.unified.common.core.util.StringUtils;
import io.swagger.parser.OpenAPIParser;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.ObjectSchema;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.parser.core.models.AuthorizationValue;
import io.swagger.v3.parser.core.models.SwaggerParseResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SwaggerApiService {

    private Map<String,TimedObject> cache=new ConcurrentHashMap<>();

    private class TimedObject{
        public TimedObject(List<OperationExt> data){
            this.data=data;
        }
        private List<OperationExt> data;
        private Long ts=System.currentTimeMillis();
    }

    @Resource
    private SystemBridgeService systemBridgeService;


    public List<OperationExt> parseSwagger(String url) {
        String key="swagger|"+url;
        TimedObject data = cache.get(key);
        if(data!=null&&System.currentTimeMillis()-data.ts<60000L){
            return data.data;
        }
        synchronized (url) {
            data = cache.get(key);
            if(data!=null&&System.currentTimeMillis()-data.ts<60000L){
                return data.data;
            }
            log.info("retrieving swagger url:" + url);
            //解析认证信息
            List<AuthorizationValue> auths = new ArrayList<>();
            try {
                URL urlString = new URL(url);
                String userInfo = urlString.getUserInfo();
                if (userInfo != null) {
                    String encodedAuth = Base64.getEncoder().encodeToString(userInfo.getBytes());
                    String authHeader = "Basic " + encodedAuth;
                    auths.add(new AuthorizationValue("Authorization", authHeader, "header"));
                }
            } catch (MalformedURLException e) {
                log.error("URL 格式错误: " + e.getMessage());
            }

            SwaggerParseResult result = new OpenAPIParser().readLocation(url, auths, null);
            if (result.getOpenAPI() == null)
                return new ArrayList<>();
            Map<String, Schema> allschemas = result.getOpenAPI().getComponents().getSchemas();
            List<OperationExt> apiList = result.getOpenAPI().getPaths().entrySet().stream()
                    .filter(p -> p.getValue().getPost() != null || p.getValue().getGet() != null)
                    .map(path -> {
                        OperationExt operationExt = new OperationExt();
                        operationExt.setPath(path.getKey());
                        Operation opc = path.getValue().getPost();
                        if (opc == null) {
                            opc = path.getValue().getGet();
                            operationExt.setMethod("get");
                            operationExt.setParameters(opc.getParameters());
                        } else {
                            operationExt.setMethod("post");
                            operationExt.setParameters(opc.getParameters());
                            if (opc.getRequestBody() != null) {
                                operationExt.setRequestBody(opc.getRequestBody());
                                if (StringUtils.isNotEmpty(opc.getRequestBody().get$ref())) {
                                    Set<Schema> reqSchemaList = getRelatedSchema(opc.getRequestBody().get$ref(), allschemas);
                                    List<ObjectSchema> osList = reqSchemaList.stream().map(s -> (ObjectSchema) s).collect(Collectors.toList());
                                    operationExt.getObjectSchemas().addAll(osList);
                                }
                            }
                        }

                        ApiResponse defaultResponse = opc.getResponses().get("200");
                        MediaType resMT = null;
                        for (Iterator<Map.Entry<String, MediaType>> iterator = defaultResponse.getContent().entrySet().iterator(); iterator.hasNext(); ) {
                            Map.Entry<String, MediaType> entry = iterator.next();
                            if (entry.getKey().equals("*/*")) {
                                resMT = entry.getValue();
                            }
                            if (resMT == null) {
                                resMT = entry.getValue();
                            }
                        }


                        if (resMT != null && resMT.getSchema() != null && StringUtils.isNotEmpty(resMT.getSchema().get$ref())) {
                            Set<Schema> resSchemaList = getRelatedSchema(resMT.getSchema().get$ref(), allschemas);
                            List<ObjectSchema> osList = resSchemaList.stream().map(s -> (ObjectSchema) s).collect(Collectors.toList());
                            operationExt.getObjectSchemas().addAll(osList);
                        }
                        return operationExt;
                    }).collect(Collectors.toList());
            cache.put(key,new TimedObject(apiList));
            return apiList;
        }

    }

    private Set<Schema> getRelatedSchema(String schemaName, Map<String, Schema> schemaMap){
        Set<Schema> ret=new HashSet<>();
        if(StringUtils.isEmpty(schemaName))
            return ret;
        schemaName=schemaName.replace("#/components/schemas/","");
        Schema t=schemaMap.get(schemaName);
        if(t==null)
            return ret;
        ret.add(t);
        if(StringUtils.isNotEmpty(t.get$ref())){
            ret.addAll(getRelatedSchema(t.get$ref(),schemaMap));
        }
        if(t.getProperties()!=null){
            for (Iterator iterator = t.getProperties().values().iterator(); iterator.hasNext(); ) {
                Schema s1 = (Schema) iterator.next();
                if(StringUtils.isNotEmpty(s1.getTitle())){
                    ret.add(s1);
                }
                if(StringUtils.isNotEmpty(s1.get$ref())){
                    Set<Schema> slst = getRelatedSchema(s1.get$ref(), schemaMap);
                    if(slst!=null){
                        ret.addAll(slst);
                    }
                }
            }
        }
        return ret;
    }


    public SystemSpec convert(DtpDataSource dtpDataSource) throws JsonProcessingException{
        String json = dtpDataSource.getApiSpec();
        ObjectMapper om = new ObjectMapper();
        om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        SystemSpec dataspec = om.readValue(json, SystemSpec.class);
        elaborate(dataspec);
        ThirdAuthInfo authInfo = new ThirdAuthInfo();
        dataspec.setAuthInfo(authInfo);
        authInfo.setApiKey(dtpDataSource.getApiKey());
        authInfo.setApiSecret(dtpDataSource.getApiSecret());
        SystemProxy.RemoteSystemMeta meta = systemBridgeService.queryProxy(dtpDataSource);
        authInfo.setMethod(meta.authenticationMode);
        dataspec.setSysType(dtpDataSource.getType());
        dataspec.setSourceId(dtpDataSource.getId());
        return dataspec;
    }

    private void elaborate(SystemSpec spec) {

    }

    public static void main(String[] args) {
        List<OperationExt> spec = new SwaggerApiService().parseSwagger("http://192.168.6.114:9714/v2/api-docs");
    }
}
