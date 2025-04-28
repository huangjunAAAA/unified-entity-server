package com.zjht.unified.datasource.controller;

import com.wukong.core.weblog.plugins.IgnoreLogging;

import com.zjht.unified.datasource.dto.ApiInvokeParam;
import com.zjht.unified.datasource.dto.BizEntityRequestDTO;
import com.zjht.unified.datasource.dto.SystemSpec;
import com.zjht.unified.datasource.entity.DtpDataSource;
import com.zjht.unified.datasource.service.ApiInvokeService;
import com.zjht.unified.datasource.service.IDtpDataSourceService;
import com.zjht.unified.datasource.service.SwaggerApiService;
import com.zjht.unified.datasource.service.SystemBridgeService;
import com.zjht.unified.datasource.service.sysproxy.SystemProxy;

import com.zjht.unified.common.core.controller.BaseController;
import com.zjht.unified.common.core.domain.HttpR;
import com.zjht.unified.common.core.domain.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;

/**
 *  控制器
 *
 * @author wangy
 */
@Api(value = "数据获取",tags = {"数据获取"})
@RestController
@RequestMapping("/data-feed")
public class DataFeedController extends BaseController {

	private static final Logger logger = LoggerFactory.getLogger(DataFeedController.class);
	@Autowired
    private IDtpDataSourceService dtpDataSourceService;

	@Autowired
	private ApiInvokeService invokeService;

	@Autowired
    private SystemBridgeService systemBridgeService;

	@Autowired
    private SwaggerApiService apiService;
    @ApiOperation(value = "查询系统规格数据",notes = "传入数据源ID")
    @GetMapping(value = "/spec/{id}")
    @IgnoreLogging
    public R<SystemSpec> getSystemSpecInfo(@PathVariable("id") Long id){
        DtpDataSource dtpds = dtpDataSourceService.getById(id);
        try {
            SystemSpec spec = apiService.convert(dtpds);
            return R.ok(spec);
        }catch (Exception e){
            logger.error(e.getMessage(),e);
            return R.fail(e.getMessage());
        }
    }

    @ApiOperation(value = "调用业务系统API",notes = "路径上放置数据源ID和相对路径，其他与所调用的api相同处理",hidden = true)
    @RequestMapping(value = "/invoke/{dsId}/**")
    public HttpR invokeApi(HttpServletRequest req, @PathVariable("dsId") Long dsId){
        String uri=req.getRequestURI();
        logger.info("invoke uri:"+uri);
        String path=uri.replace("/data-feed/invoke/[//d+]","");
        ApiInvokeParam invokeParam=new ApiInvokeParam();
        invokeParam.setDsId(dsId);
        invokeParam.setMethod(req.getMethod());
        invokeParam.setPath(path);
        Enumeration<String> hnames = req.getHeaderNames();
        while(hnames.hasMoreElements()){
            String headerName=hnames.nextElement();
            String headerVal=req.getHeader(headerName);
            invokeParam.putHeader(headerName,headerVal);
        }
        String ct = req.getContentType().toLowerCase();
        if (ct.contains("application/json")) {
            try {
                String body = IOUtils.toString(req.getInputStream(), "utf8");
                invokeParam.setRequestBody(body);
            } catch (IOException e) {
                logger.error(e.getMessage(),e);
            }
        } else {
            for (Iterator<Map.Entry<String, String[]>> iterator = req.getParameterMap().entrySet().iterator(); iterator.hasNext(); ) {
                Map.Entry<String, String[]> param =  iterator.next();
                invokeParam.getParams().put(param.getKey(),param.getValue()[0]);
            }
        }
        return invokeService.invoke(invokeParam);
    }

    @ApiOperation(value = "调用业务系统API",notes = "传入数据源ID等所有参数")
    @PostMapping(value = "/invoke-api")
    public HttpR invokeApi(@RequestBody ApiInvokeParam invokeParam){
        return invokeService.invoke(invokeParam);
    }

    @ApiOperation(value = "调用业务系统实体列表数据",notes = "传入数据源ID和实体代码")
    @PostMapping(value = "/biz-entity-list")
    public R invokeBizListApi(@RequestBody BizEntityRequestDTO request){
        SystemProxy proxy = systemBridgeService.createProxy(request.getDatasourceId());
        return proxy.getBizObjectList(request.getEntityCode(),request.getParams());
    }

    @ApiOperation(value = "调用业务系统实体历史数据",notes = "传入数据源ID和实体代码")
    @PostMapping(value = "/biz-entity-history")
    public R invokeBizHistoryApi(@RequestBody BizEntityRequestDTO request){
        SystemProxy proxy = systemBridgeService.createProxy(request.getDatasourceId());
        return proxy.getBizObjectHistory(request.getEntityCode(),request.getEntityId(),request.getParams());
    }

    @ApiOperation(value = "调用业务系统实体详情数据",notes = "传入数据源ID、实体代码和实体ID")
    @PostMapping(value = "/biz-entity-detail")
    public R invokeBizDetailApi(@RequestBody BizEntityRequestDTO request) {
        SystemProxy proxy = systemBridgeService.createProxy(request.getDatasourceId());
        return proxy.getBizObject(request.getEntityCode(),request.getEntityId(),request.getParams());
    }
}