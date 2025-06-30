package com.zjht.unified;

import alluxio.shaded.client.org.apache.yetus.audience.InterfaceAudience;
import com.alibaba.fastjson.JSON;
import com.caoccao.javet.interop.V8Host;
import com.caoccao.javet.interop.V8Runtime;
import com.caoccao.javet.interop.engine.IJavetEngine;
import com.caoccao.javet.interop.engine.IJavetEnginePool;
import com.caoccao.javet.interop.engine.JavetEnginePool;
import com.zjht.unified.common.core.constants.Constants;
import com.zjht.unified.common.core.domain.R;
import com.zjht.unified.common.core.util.SpringUtils;
import com.zjht.unified.domain.composite.PrjSpecDO;
import com.zjht.unified.feign.RemoteAdmin;
import com.zjht.unified.service.RtContextService;
import com.zjht.unified.service.TaskService;
import com.zjht.unified.service.ctx.RtRedisObjectStorageService;
import com.zjht.unified.service.ctx.TaskContext;
import com.zjht.unified.service.v8exec.V8EngineService;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;

@ComponentScan(value = {"com.zjht", "com.wukong"})
@EntityScan(basePackages = {"com.zjht"})
@SpringCloudApplication
@EnableFeignClients(basePackages = "com.zjht.**")
@MapperScan("com.zjht.**.mapper.**")
@EnableAsync
@Slf4j
public class UnifiedEntityRTApplication {

    private static final IJavetEnginePool<V8Runtime> javetEnginePool = new JavetEnginePool<>();

    public static void main(String[] args) throws Exception {
        // 规避cpu型号导致的v8线程初始化问题
        try (V8Runtime v8Runtime = V8Host.getV8Instance().createV8Runtime()) {
        }

        ConfigurableApplicationContext app = SpringApplication.run(UnifiedEntityRTApplication.class, args);
        log.info("引擎模块启动成功");

        test();
//        initScriptContext();



    }

    /**
     * 初始化后端脚本默认执行的context
     */
    private static void initScriptContext() {
        String version = "test624";
        long prjId = 2L;
        String prjGuid = "efd5f3ab-32fb-46c4-a35b-5f81fd87472c";
        RemoteAdmin remoteAdmin = SpringUtils.getBean(RemoteAdmin.class);
        RtContextService rtContextService = SpringUtils.getBean(RtContextService.class);
        TaskService taskService = SpringUtils.getBean(TaskService.class);
        R<PrjSpecDO> prjSpecDOR = remoteAdmin.genPrjSpec(prjId);
        if (prjSpecDOR.getCode()== Constants.SUCCESS) {
            PrjSpecDO prjSpecDO = prjSpecDOR.getData();
            RtRedisObjectStorageService rtRedisObjectStorageService = SpringUtils.getBean(RtRedisObjectStorageService.class);
            TaskContext ctx = rtContextService.startNewSession(prjSpecDO, version);


            V8EngineService engineService = SpringUtils.getBean(V8EngineService.class);
            String script = "var d = createEntity('Device',true);  d.deviceName = 'test——device——624';d.deviceStatus = 'online'; d.save();";
//            String script = "var d = createEntity('Device',true);  console.log(d.deviceName);";
            engineService.exec(script, null, ctx, prjGuid, version);
            taskService.initSpecDefinition(ctx, prjSpecDO);
            rtContextService.saveRunningContext(ctx);
        }
    }

    private static void test () {
        String version = "test624";
        long prjId = 2L;
        String prjGuid = "efd5f3ab-32fb-46c4-a35b-5f81fd87472c";
        RemoteAdmin remoteAdmin = SpringUtils.getBean(RemoteAdmin.class);
        RtContextService rtContextService = SpringUtils.getBean(RtContextService.class);
        TaskService taskService = SpringUtils.getBean(TaskService.class);
        R<PrjSpecDO> prjSpecDOR = remoteAdmin.genPrjSpec(prjId);
        if (prjSpecDOR.getCode()== Constants.SUCCESS) {
            PrjSpecDO prjSpecDO = prjSpecDOR.getData();
            RtRedisObjectStorageService rtRedisObjectStorageService = SpringUtils.getBean(RtRedisObjectStorageService.class);
            TaskContext ctx = rtContextService.startNewSession(prjSpecDO, version);


            V8EngineService engineService = SpringUtils.getBean(V8EngineService.class);

            String script = ""
                    + "console.log('hello schema');\n"
                    + "var ds1 = SchemaUtils.getTable(\"\", \"device\");\n"+
            "console.log(\"表名 ds1.name =\", ds1.name);         // 表名\n" +
                    "console.log(\"所属数据库 ds1.dbname =\", ds1.dbname); // 数据库名\n" +
                     "var condition = { device_name: '测试设备1', device_status: 'offline' };\n"
                    + "var result = ds1.filter(condition, 0, 2);\n"
                    + "console.log('Filtered result:', JSON.stringify(result));\n"
                    + "var count = ds1.filterLen(condition);\n"
                    + "console.log('Total matched:', count);\n"
                    + "var total = ds1.length();\n"
                    + "console.log('Total records in device:', total);";



//            String script = "console.log('helloschema'); " +
//                    "var ds1 = getTable(\"\", \"device\");\n" +
//                    "console.log(\"✅ 表对象获取成功:\");\n" +
//                    "console.log(\"表名 ds1.name =\", ds1.name);         // 表名\n" +
//                    "console.log(\"所属数据库 ds1.dbname =\", ds1.dbname); // 数据库名\n" +
//                    "\n" +
//                    "console.log(\"\\n✅ 字段列表:\");\n" +
//                    "for (var i = 0; i < ds1.columns.length; i++) {\n" +
//                    "    var col = ds1.columns[i];\n" +
//                    "    console.log(\"第 \" + i + \" 个字段:\");\n" +
//                    "    console.log(\"  名字    =\", col.name);\n" +
//                    "    console.log(\"  类型    =\", col.type);\n" +
//                    "    console.log(\"  备注    =\", col.comment);\n" +
//                    "}\n" +
//                    "\n" +
//                    "// 单独访问第一个字段\n" +
//                    "var col1 = ds1.columns[0];\n" +
//                    "console.log(\"\\n\uD83D\uDD0D 第一个字段对象验证:\");\n" +
//                    "console.log(\"col1.name =\", col1.name);\n" +
//                    "console.log(\"col1.type =\", col1.type);\n" +
//                    "console.log(\"col1.comment =\", col1.comment);\n" +
//                    "\n" +
//                    "console.log(\"\\n✅ 索引列表:\");\n" +
//                    "for (var j = 0; j < ds1.indices.length; j++) {\n" +
//                    "    var idx = ds1.indices[j];\n" +
//                    "    console.log(\"第 \" + j + \" 个索引:\");\n" +
//                    "    console.log(\"  名字    =\", idx.name);\n" +
//                    "    console.log(\"  类型    =\", idx.type);\n" +
//                    "    console.log(\"  包含字段 =\", idx.columns.join(\", \"));\n" +
//                    "}\n" +
//                    "\n" +
//                    "// 单独访问第一个索引\n" +
//                    "var idx1 = ds1.indices[0];\n" +
//                    "console.log(\"\\n\uD83D\uDD0D 第一个索引对象验证:\");\n" +
//                    "console.log(\"idx1.name =\", idx1.name);\n" +
//                    "console.log(\"idx1.type =\", idx1.type);\n" +
//                    "console.log(\"idx1.columns =\", idx1.columns);\n" +
//                    "console.log(\"idx1.columns[0] =\", idx1.columns[0]); // 注意：这是字符串类型\n";
            engineService.exec(script, null, ctx, prjGuid, version);
            taskService.initSpecDefinition(ctx, prjSpecDO);
            rtContextService.saveRunningContext(ctx);
        }
    }

}
