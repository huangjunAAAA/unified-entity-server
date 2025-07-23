package com.zjht.unified.controller;


import com.caoccao.javet.annotations.V8Function;
import com.zjht.unified.common.core.domain.R;
import com.zjht.unified.common.core.domain.dto.BaseQueryDTO;
import com.zjht.unified.common.core.domain.dto.GetParam;
import com.zjht.unified.common.core.domain.dto.GuidDTO;
import com.zjht.unified.common.core.domain.dto.SetParam;
import com.zjht.unified.domain.composite.ClazzDefCompositeDO;
import com.zjht.unified.domain.composite.FsmDefCompositeDO;
import com.zjht.unified.domain.composite.PrjSpecDO;
import com.zjht.unified.domain.simple.InitialInstanceDO;
import com.zjht.unified.domain.simple.SentinelDefDO;
import com.zjht.unified.domain.simple.StaticDefDO;
import com.zjht.unified.domain.simple.TNodeDO;
import com.zjht.unified.dto.*;
import com.zjht.unified.service.FrontObjectService;
import com.zjht.unified.service.IScriptEngine;
import com.zjht.unified.service.RtContextService;
import com.zjht.unified.service.TaskService;
import com.zjht.unified.service.ctx.TaskContext;
import com.zjht.unified.service.v8exec.SchemaUtils;
import com.zjht.unified.service.v8exec.model.TableInfo;
import com.zjht.unified.service.v8exec.model.DataViewInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 *  控制器
 *
 * @author wangy
 */
@Api(value = "ExecController",tags = {"ExecController"})
@RestController
@Slf4j
public class ExecController {

    @Autowired
    private IScriptEngine scriptEngine;

    @Autowired
    private FrontObjectService frontObjectService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private RtContextService rtContextService;

    @ApiOperation(value = "后端脚本执行")
    @PostMapping("/backend/exec")
    public R<Object> run(@RequestBody Increment<String> script){
        log.info("version = " + script.getVer());
        log.info("script = " + script);

        TaskContext runningContext = rtContextService.getRunningContext(script.getVer());
        log.info("runningContext = " + runningContext);
        if (Objects.nonNull(runningContext)) {
            Object exec = scriptEngine.exec(script.getData(), null, runningContext, runningContext.getPrjInfo().getPrjGuid(), runningContext.getPrjInfo().getPrjVer());
            return R.ok(exec);
        }else{
            return R.fail("task not found:"+script.getVer());
        }
    }

    @ApiOperation(value = "在指定运行环境新增类定义")
    @PostMapping("/add-cls-def")
    public R<String> addClassDef(@RequestBody Increment<List<ClazzDefCompositeDO>> clazzDefList){
        TaskContext ctx = rtContextService.getRunningContext(clazzDefList.getVer());
        if(ctx==null){
            return R.fail("task not found:"+clazzDefList.getVer());
        }
        String prjGuid=ctx.getPrjInfo().getPrjGuid();
        String prjVer=ctx.getPrjInfo().getPrjVer();
        taskService.setClsDefs(ctx, clazzDefList.getData(), prjGuid, prjVer);
        return R.ok();
    }

    @ApiOperation(value = "在指定运行环境新增状态机")
    @PostMapping("/add-fsm")
    public R<String> addFsmDef(@RequestBody Increment<List<FsmDefCompositeDO>> fsmLst){
        TaskContext ctx = rtContextService.getRunningContext(fsmLst.getVer());
        if(ctx==null){
            return R.fail("task not found:"+fsmLst.getVer());
        }
        String prjGuid=ctx.getPrjInfo().getPrjGuid();
        String prjVer=ctx.getPrjInfo().getPrjVer();
        taskService.setFsm(ctx, fsmLst.getData(), prjGuid, prjVer);
        return R.ok();
    }

    @ApiOperation(value = "在指定运行环境新增哨兵")
    @PostMapping("/add-sentinel-def")
    public R<String> addSentinelDef(@RequestBody Increment<List<SentinelDefDO>> sentinels){
        TaskContext ctx = rtContextService.getRunningContext(sentinels.getVer());
        if(ctx==null){
            return R.fail("task not found:"+sentinels.getVer());
        }
        String prjGuid=ctx.getPrjInfo().getPrjGuid();
        String prjVer=ctx.getPrjInfo().getPrjVer();
        taskService.setSentinels(ctx, sentinels.getData(), prjGuid, prjVer);
        return R.ok();
    }

    @ApiOperation(value = "在指定运行环境设置静态变量")
    @PostMapping("/add-statics")
    public R<String> addStatics(@RequestBody Increment<List<StaticDefDO>> statics){
        TaskContext ctx = rtContextService.getRunningContext(statics.getVer());
        if(ctx==null){
            return R.fail("task not found:"+statics.getVer());
        }
        String prjGuid=ctx.getPrjInfo().getPrjGuid();
        String prjVer=ctx.getPrjInfo().getPrjVer();
        taskService.setStatics(ctx, statics.getData(), prjGuid, prjVer);

        return R.ok();
    }

    @ApiOperation(value = "在指定运行环境新增类实例")
    @PostMapping("/add-instance")
    public R<String> addInstance(@RequestBody Increment<List<InitialInstanceDO>> instances){
        TaskContext ctx = rtContextService.getRunningContext(instances.getVer());
        if(ctx==null){
            return R.fail("task not found:"+instances.getVer());
        }
        String prjGuid=ctx.getPrjInfo().getPrjGuid();
        String prjVer=ctx.getPrjInfo().getPrjVer();
        taskService.setInstances(ctx, instances.getData(), prjGuid, prjVer);

        return R.ok();
    }

    @ApiOperation(value = "在指定运行环境增加依赖项目")
    @PostMapping("/add-dep")
    public R<String> addDep(@RequestBody Increment<PrjSpecDO> spec){
        TaskContext ctx = rtContextService.getRunningContext(spec.getVer());
        if(ctx==null){
            return R.fail("task not found:"+spec.getVer());
        }
        taskService.initSpecDefinition(ctx, spec.getData());
        return R.ok();
    }

    @ApiOperation(value = "在指定运行环境运行某个对象的方法")
    @PostMapping("/exec-object-method")
    public R<Object> execObjMethod(@RequestBody MethodInvokeParam methodInvokeParam){
        TaskContext ctx = rtContextService.getRunningContext(methodInvokeParam.getVer());
        if(ctx==null){
            return R.fail("task not found:"+methodInvokeParam.getVer());
        }

        Object ret = frontObjectService.execMethod(methodInvokeParam);
        return R.ok(ret);
    }

    @ApiOperation(value = "在指定运行环境获取对象")
    @PostMapping("/get-object")
    public R<Object> getObject(@RequestBody GetParam param){
        TaskContext ctx = rtContextService.getRunningContext(param.getVer());
        if(ctx==null){
            return R.fail("task not found:"+param.getVer());
        }
        Map<String, Object> ret = frontObjectService.getObject(param);
        return R.ok(ret);
    }

    @ApiOperation(value = "在指定运行环境获取对象")
    @PostMapping("/delete-object")
    public R<Object> delObject(@RequestBody GetParam param){
        TaskContext ctx = rtContextService.getRunningContext(param.getVer());
        if(ctx==null){
            return R.fail("task not found:"+param.getVer());
        }
        frontObjectService.delObject(param);
        return R.ok();
    }

    @ApiOperation(value = "在指定运行环境获取对象")
    @PostMapping("/save-object")
    public R<Object> saveObject(@RequestBody SetParam param){
        TaskContext ctx = rtContextService.getRunningContext(param.getVer());
        if(ctx==null){
            return R.fail("task not found:"+param.getVer());
        }
        frontObjectService.setObject(param);
        return R.ok();
    }

    @ApiOperation(value = "在指定运行环境创建对象")
    @PostMapping("/create-object")
    public R<Object> createObject(@RequestBody CreateObjectParam param){
        TaskContext ctx = rtContextService.getRunningContext(param.getVer());
        if(ctx==null){
            return R.fail("task not found:"+param.getVer());
        }
        Map<String, Object> ret = frontObjectService.createObject(param);
        if(ret==null)
            return R.fail("class not found: "+param.getClsName()+"|"+param.getClsGuid());
        return R.ok(ret);
    }

    @ApiOperation(value = "在指定运行环境获取对象")
    @PostMapping("/get-object-value")
    public R<Object> getObjectValue(@RequestBody GetParam param){
        TaskContext ctx = rtContextService.getRunningContext(param.getVer());
        if(ctx==null){
            return R.fail("task not found:"+param.getVer());
        }
        Object ret=frontObjectService.getObjectValue(param);
        return R.ok(ret);
    }

    @ApiOperation(value = "在指定运行环境获取对象")
    @PostMapping("/list-object")
    public R<List<Map<String, Object>>> listObject(@RequestBody BaseQueryDTO<QueryObjectDTO>  param){
        TaskContext ctx = rtContextService.getRunningContext(param.getCondition().getVer());
        if(ctx==null){
            return R.fail("task not found:"+param.getCondition().getVer());
        }
        List<Map<String, Object>> result = frontObjectService.listObject(ctx,param);
        return R.ok(result);
    }

    @ApiOperation(value = "在指定运行环境获取对象")
    @PostMapping("/list-all-object")
    public R<List<Map<String, Object>>> listAllObject(@RequestBody QueryAllObjectDTO  param){
        TaskContext ctx = rtContextService.getRunningContext(param.getVer());
        if(ctx==null){
            return R.fail("task not found:"+param.getVer());
        }
        List<Map<String, Object>> result = frontObjectService.listAllObject(ctx,param);
        return R.ok(result);
    }

    @ApiOperation(value = "在指定运行环境获取树根节点列表")
    @PostMapping("/list-tree")
    public R<List<TNodeDO>> listTree(@RequestBody BaseQueryDTO<QueryTree>  param){
        TaskContext ctx = rtContextService.getRunningContext(param.getCondition().getVer());
        if(ctx==null){
            return R.fail("task not found:"+param.getCondition().getVer());
        }
        List<TNodeDO> result = frontObjectService.listTree(ctx,param.getCondition().getType(),param.getCondition().getSubtype());
        return R.ok(result);
    }

    @ApiOperation(value = "在指定运行环境删除树节点")
    @PostMapping("/delete-tree")
    public R<Integer> deleteTree(@RequestBody GuidDTO<String> param){
        TaskContext ctx = rtContextService.getRunningContext(param.getData());
        if(ctx==null){
            return R.fail("task not found:"+param.getData());
        }
        Integer ret = frontObjectService.deleteTree(ctx,param.getGuid());
        return R.ok(ret);
    }
    @ApiOperation("获取数据库表对象")
    @PostMapping("/get-table")
    public R<TableInfo> getTable(@RequestBody SchemaActionParam param) {
        TaskContext ctx = rtContextService.getRunningContext(param.getVer());
        if (ctx == null) return R.fail("task not found: " + param.getVer());
        SchemaUtils utils = SchemaUtils.getSchemaUtils(ctx);
        TableInfo table = utils.getTableObj("", param.getTableName());
        Method[] methods = TableInfo.class.getDeclaredMethods();
        //数据库表对象支持的方法
        table.methodNames = (Arrays.stream(methods)
                .filter(m -> m.isAnnotationPresent(V8Function.class))
                .map(Method::getName)
                .collect(Collectors.toList()));
        return R.ok(table);
    }

    @ApiOperation("执行数据库表对象的方法")
    @PostMapping("/table-exec")
    public R<Object> execTableObjMethod(@RequestBody SchemaActionParam param) {
        TaskContext ctx = rtContextService.getRunningContext(param.getVer());
        if (ctx == null) return R.fail("task not found: " + param.getVer());
        SchemaUtils utils = SchemaUtils.getSchemaUtils(ctx);
        TableInfo table = utils.getTableObj("", param.getTableName());

        if (table == null) {
            return R.fail("TableInfo not found: " + param.getTableName());
        }
        try {
            Object[] args = param.getParams() == null ? new Object[0] : param.getParams();
            Method method = findMethodByName(TableInfo.class, param.getMethodName());
            if (method == null) return R.fail("Method not found or argument mismatch: " + param.getMethodName());
            method.setAccessible(true);
            Object result = method.invoke(table, args);
            return R.ok(result);

        } catch (Exception e) {
            log.error("执行数据表对象方法失败: method={}, table={}, error={}", param.getMethodName(), param.getTableName(), e.getMessage(), e);
            return R.fail("执行失败: " + e.getMessage());
        }
    }

    private Method findMethodByName(Class<?> clazz, String methodName) {
        for (Method method : clazz.getMethods()) {
            if (method.getName().equals(methodName)) {
                return method;
            }
        }
        return null;
    }


    @ApiOperation("创建视图")
    @PostMapping("/create-view")
    public R<DataViewInfo> createView(@RequestBody SchemaActionParam param) {
        TaskContext ctx = rtContextService.getRunningContext(param.getVer());
        if (ctx == null) return R.fail("task not found: " + param.getVer());
        SchemaUtils utils = SchemaUtils.getSchemaUtils(ctx);
        DataViewInfo view = utils.createDataViewObj(param.getViewSql(), param.getViewName());
        return R.ok(view);
    }

    @ApiOperation("列出视图列表")
    @PostMapping("/list-view")
    public R<List<DataViewInfo>> listViews(@RequestBody SchemaActionParam param) {
        TaskContext ctx = rtContextService.getRunningContext(param.getVer());
        if (ctx == null) return R.fail("task not found: " + param.getVer());
        SchemaUtils utils = SchemaUtils.getSchemaUtils(ctx);
        List<DataViewInfo> views = utils.listDVObj();
        return R.ok(views);
    }

    @ApiOperation("删除视图")
    @PostMapping("/drop-view")
    public R<Boolean> dropView(@RequestBody SchemaActionParam param) {
        TaskContext ctx = rtContextService.getRunningContext(param.getVer());
        if (ctx == null) return R.fail("task not found: " + param.getVer());
        SchemaUtils utils = SchemaUtils.getSchemaUtils(ctx);
        boolean result = utils.dropDVObj(param.getViewName());
        return R.ok(result);
    }


}
