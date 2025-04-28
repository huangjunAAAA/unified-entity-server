package com.zjht.unified.datasource.service.sysproxy.impl;

import cn.hutool.core.thread.ThreadUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wukong.core.util.SpringUtil;
import com.wukong.core.weblog.utils.JsonUtil;

import com.zjht.unified.datasource.dto.*;
import com.zjht.unified.datasource.entity.DtpDataSource;
import com.zjht.unified.datasource.service.ApiInvokeService;
import com.zjht.unified.datasource.service.DataMessageQueueService;
import com.zjht.unified.datasource.service.IDtpDataSourceService;
import com.zjht.unified.datasource.service.SwaggerApiService;

import com.zjht.unified.datasource.service.sysproxy.SystemProxy;

import com.zjht.unified.datasource.util.ParamUtils;

import com.zjht.unified.common.core.constants.Constants;
import com.zjht.unified.common.core.domain.HttpR;
import com.zjht.unified.common.core.domain.R;
import com.zjht.unified.common.core.json.GsonUtil;
import com.zjht.unified.common.core.util.ObjectFieldLocatorUtil;
import com.zjht.unified.common.core.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.lang.ref.WeakReference;
import java.util.*;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Slf4j
public abstract class AbstractUnifiedSysStub implements SystemProxy, DataMessageQueueService.DataMQCallback{

    @Autowired
    protected SwaggerApiService apiService;

    protected volatile SystemSpec dataspec;

    protected DtpDataSource dtpDataSource;

    @Autowired
    private IDtpDataSourceService dataSourceService;

    @Autowired
    protected DataMessageQueueService dataMessageQueueService;

    @Autowired
    protected ApiInvokeService invokeService;

    @Override
    public void init(DtpDataSource dtpDataSource) {
        if (dataspec != null)
            return;
        log.info("init cdp data proxy for datasource id:" + dtpDataSource.getId());
        try {
            dataspec = apiService.convert(dtpDataSource);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage(), e);
            return;
        }
        this.dtpDataSource = dtpDataSource;
        specKeeper.schedule(new SpecChecker(this),600, TimeUnit.SECONDS);
        dataMessageQueueService.registerOnMessage(dataspec.getMqConnection().getMqType(), dataspec.getMqConnection().getConnectionString(), dataspec.getMqConnection().getTopic(), this);
        log.info("init completed for datasource id:" + dtpDataSource.getId());
    }

    private static final ScheduledThreadPoolExecutor specKeeper= ThreadUtil.createScheduledExecutor(10);

    private static class SpecChecker implements Runnable{

        private WeakReference<AbstractUnifiedSysStub> ref;

        private SpecChecker(AbstractUnifiedSysStub instance){
            ref=new WeakReference<>(instance);
        }
        @Override
        public void run() {
            AbstractUnifiedSysStub inst = ref.get();
            if(inst==null)
                return;

            if (inst.dtpDataSource != null)
                try {
                    DtpDataSource ds = inst.dataSourceService.getById(inst.dtpDataSource.getId());
                    if(ds==null){
                        return;
                    }
                    BeanUtils.copyProperties(ds,inst.dtpDataSource);
                    SwaggerApiService apiService= SpringUtil.getBean(SwaggerApiService.class);
                    SystemSpec spec = apiService.convert(inst.dtpDataSource);
                    if(spec!=null)
                        inst.dataspec=spec;
                } catch (JsonProcessingException e) {
                    log.error(e.getMessage(), e);
                }
            if(Thread.currentThread().isInterrupted())
                return;
            specKeeper.schedule(this,600, TimeUnit.SECONDS);
        }
    }


    /**
     * 通过定义的详情接口api获取一个业务实体的详情，通过map方式返回
     *
     * @param type 实体类型
     * @param id   实体ID
     * @return
     */
    public R getBizObject(String type, String id, Map<String,Object> extra) {
        SystemSpec spec=dataspec;
        if (spec == null)
            return null;
        long ts=System.currentTimeMillis();
        Optional<BizEntitySpec> bizSpec = spec.getBizObjects().stream().filter(biz -> biz.getObjectType().equalsIgnoreCase(type)).findFirst();
        if (!bizSpec.isPresent())
            return R.fail();

        ApiSpec api = bizSpec.get().getDetailApi();
        ApiInvokeParam param = new ApiInvokeParam();
        param.setPath(api.getPath());
        if(StringUtils.isNotEmpty(api.getBaseUrl()))
            param.setBaseUrl(api.getBaseUrl());
        param.setDsId(dtpDataSource.getId());
        param.putParameter("id", id);
        if(extra!=null){
            param.getParams().putAll(extra);
        }
        ParamUtils.setInvokeParam(param,api);
        HttpR result = invokeService.repeatedInvoke(param);
        R<Map<String, Object>> rMap = null;
        try {
            rMap = new ObjectMapper().readValue(result.getMsg(), new TypeReference<R<Map<String, Object>>>() {
            });
            return rMap;
        } catch (JsonProcessingException e) {
            log.error(e.getMessage(), e);
        } finally {
            log.info("get biz object:["+type+"."+id+"], time consumes:"+(System.currentTimeMillis()-ts)+" ms");
            log.debug("["+type+"."+id+"]:"+ GsonUtil.toJson(result));
        }
        return R.fail();
    }

    @Override
    public R<List> getBizObjectList(String type,Map<String,Object> extra) {
        SystemSpec spec=dataspec;
        if (spec == null)
            return null;
        Optional<BizEntitySpec> bizSpec = spec.getBizObjects().stream()
                .filter(biz -> biz.getObjectType().equalsIgnoreCase(type)).findFirst();
        if (!bizSpec.isPresent())
            return R.ok(null,"biz type not found:"+type);
        BizEntitySpec bizEntitySpec = bizSpec.get();
        ApiInvokeParam invokeParam=new ApiInvokeParam();
        ApiSpec api = bizEntitySpec.getListApi();
        if(StringUtils.isNotEmpty(api.getBaseUrl()))
            invokeParam.setBaseUrl(api.getBaseUrl());
        invokeParam.setDsId(dtpDataSource.getId());
        invokeParam.setPath(api.getPath());
        if(extra!=null){
            invokeParam.getParams().putAll(extra);
        }
        ParamUtils.setInvokeParam(invokeParam,api);
        HttpR hresult = invokeService.invoke(invokeParam);
        if(hresult.getCode()== Constants.SUCCESS||hresult.getCode()==Constants.SUCCESS_A){
            Object r = JsonUtil.parse(hresult.getMsg(), Object.class);
            List<Map<String, Object>> rMapList = ObjectFieldLocatorUtil.extractMapList(r);
            if(rMapList!=null)
                return R.ok(rMapList);
        }
        return R.fail(hresult.getCode(),hresult.getMsg());
    }


    @Override
    public void OnMessage(DataValUpdate data, StringRedisTemplate redisTemplate) {

    }

    @Override
    public R<List> getBizObjectHistory(String type, String id,Map<String,Object> extra) {
        SystemSpec spec=dataspec;
        if (spec == null)
            return null;
        Optional<BizEntitySpec> bizSpec = spec.getBizObjects().stream()
                .filter(biz -> biz.getObjectType().equalsIgnoreCase(type)).findFirst();
        if (!bizSpec.isPresent())
            return R.ok(null,"biz type not found:"+type);
        BizEntitySpec bizEntitySpec = bizSpec.get();
        ApiInvokeParam invokeParam=new ApiInvokeParam();
        ApiSpec historyApi = bizEntitySpec.getHistoryApi();
        invokeParam.putParameter("id", id);
        invokeParam.setDsId(dtpDataSource.getId());
        invokeParam.setPath(historyApi.getPath());
        if(extra!=null){
            invokeParam.getParams().putAll(extra);
        }
        ParamUtils.setInvokeParam(invokeParam,historyApi);
        HttpR hresult = invokeService.invoke(invokeParam);
        if(hresult.getCode()==Constants.SUCCESS||hresult.getCode()==Constants.SUCCESS_A){
            Object r = JsonUtil.parse(hresult.getMsg(), Object.class);
            List<Map<String, Object>> rMapList = ObjectFieldLocatorUtil.extractMapList(r);
            if(rMapList!=null)
                return R.ok(rMapList);
        }
        return R.fail(hresult.getCode(),hresult.getMsg());
    }
}
