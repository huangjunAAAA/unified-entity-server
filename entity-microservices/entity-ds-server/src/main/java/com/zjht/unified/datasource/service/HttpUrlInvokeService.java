package com.zjht.unified.datasource.service;

import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import com.alibaba.cloud.nacos.NacosServiceManager;
import com.alibaba.nacos.api.naming.NamingMaintainService;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.alibaba.nacos.api.naming.pojo.ServiceInfo;

import com.zjht.unified.common.core.domain.HttpR;
import com.zjht.unified.common.core.util.HttpUtils;
import com.zjht.unified.common.core.util.SpringUtils;
import com.zjht.unified.common.core.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.util.encoders.UrlBase64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
@Slf4j
public class HttpUrlInvokeService {

    @Resource
    private NacosServiceManager nacosServiceManager;

    @Resource
    private NacosDiscoveryProperties nacosDiscoveryProperties;

    public HttpR postJSON(String url, Map<String, String> headers, String requestBody, String ssl) {
        if(!judgeInnerUrl(url)){
            return HttpUtils.postJSON(url,headers,requestBody,ssl);
        }else{
            HttpHeaders h = toHeaders(headers, MediaType.APPLICATION_JSON);
            HttpEntity req=new HttpEntity(requestBody, h);
            RestTemplate restTemplate = SpringUtils.getBean(RestTemplate.class);
            try {
                log.info("json post url:"+url+", params:"+requestBody);
                ResponseEntity<String> resp = restTemplate.exchange(url, HttpMethod.POST,req, String.class);
                return HttpR.ok(resp.getBody());
            }catch (Throwable e){
                log.error(e.getMessage(),e);
                return null;
            }
        }

    }

    private HttpHeaders toHeaders(Map<String, String> headers,MediaType mt){
        HttpHeaders hLst=new HttpHeaders();
        if(mt!=null)
            hLst.setContentType(mt);
        if(!CollectionUtils.isEmpty(headers)){
            headers.entrySet().forEach(h->{
                if(!h.getKey().equalsIgnoreCase("accesstoken")
                        &&!h.getKey().equalsIgnoreCase("authorization"))
                    hLst.add(h.getKey(),h.getValue());
            });
        }
        hLst.add("ignoreValidate","true");
        return hLst;
    }

    public HttpR post(String url, Map<String, String> headers, Map<String, Object> params, String ssl) {
        if(!judgeInnerUrl(url)){
            return HttpUtils.post(url,headers,params,ssl);
        }else{
            MultiValueMap<String, String> pMap = new LinkedMultiValueMap<>();
            params.entrySet().forEach(p->{
                pMap.add(p.getKey(),p.getValue().toString());
            });
            HttpHeaders h = toHeaders(headers, MediaType.APPLICATION_FORM_URLENCODED);
            HttpEntity req=new HttpEntity(pMap, h);
            RestTemplate restTemplate = SpringUtils.getBean(RestTemplate.class);
            try {
                log.info("formdata post url:"+url+", params:"+pMap);
                ResponseEntity<String> resp = restTemplate.exchange(url, HttpMethod.POST, req, String.class);
                return HttpR.ok(resp.getBody());
            }catch (Exception e){
                log.error(e.getMessage(),e);
                return null;
            }
        }
    }

    public HttpR get(String url, Map<String, String> headers, Map<String, Object> params, String ssl) {
        if(!judgeInnerUrl(url)){
            return HttpUtils.get(url,headers,params,ssl);
        }else{
            HttpHeaders h = toHeaders(headers, null);
            HttpEntity req=new HttpEntity(null, h);
            if(params!=null&&!params.isEmpty()) {
                StringBuilder queryString=new StringBuilder();
                queryString.append("?");
                for (Iterator<Map.Entry<String, Object>> iterator = params.entrySet().iterator(); iterator.hasNext(); ) {
                    Map.Entry<String, Object> param = iterator.next();
                    if(param.getValue()!=null&& StringUtils.isNotEmpty(param.getValue()+"")) {
                        queryString.append(param.getKey()).append("=").append(param.getValue());
                        if (iterator.hasNext()) {
                            queryString.append("&");
                        }
                    }
                }
                url=url+queryString;
            }
            RestTemplate restTemplate = SpringUtils.getBean(RestTemplate.class);
            try {
                log.info("get url:"+url);
                ResponseEntity<String> resp = restTemplate.exchange(url, HttpMethod.GET, req, String.class);
                return HttpR.ok(resp.getBody());
            }catch (Exception e){
                log.error(e.getMessage(),e);
                return null;
            }
        }
    }


    private boolean judgeInnerUrl(String url){
        try {
            URL pUrl=new URL(url);
            String host=pUrl.getHost();

            NamingService nacosNamingService = nacosServiceManager.getNamingService(nacosDiscoveryProperties.getNacosProperties());
            List<Instance> instances = nacosNamingService.getAllInstances(host);
            return !CollectionUtils.isEmpty(instances);
        }catch (Exception e){
            log.error(e.getMessage(),e);
        }
        return false;
    }
}
