package com.zjht.unified;

import com.alibaba.fastjson.JSON;
import com.caoccao.javet.interop.V8Host;
import com.caoccao.javet.interop.V8Runtime;
import com.caoccao.javet.interop.engine.IJavetEngine;
import com.caoccao.javet.interop.engine.IJavetEnginePool;
import com.caoccao.javet.interop.engine.JavetEnginePool;
import com.zjht.unified.common.core.util.SpringUtils;
import com.zjht.unified.domain.composite.PrjSpecDO;
import com.zjht.unified.service.RtContextService;
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

        // 正常启动
        ConfigurableApplicationContext app = SpringApplication.run(UnifiedEntityRTApplication.class, args);
        log.info("数据存储模块启动成功");
//        for (int i = 0; i < 1000; i++) {
//            app.getBean(V8EngineService.class).test2();
//            Thread.sleep(1000);
//        }
    }

    private static void test2() throws Exception {
        try (IJavetEngine<V8Runtime> javetEngine = javetEnginePool.getEngine()) {
            V8Runtime v8Runtime = javetEngine.getV8Runtime();
        }
    }

    private static void test() {
        String json = "{\n" +
                "        \"clazzList\": [\n" +
                "            {\n" +
                "                \"id\": 19,\n" +
                "                \"guid\": \"b65f7c18-732e-4d3c-bf21-cabc14939867\",\n" +
                "                \"name\": \"Device\",\n" +
                "                \"nameZh\": \"设备类\",\n" +
                "                \"type\": \"1\",\n" +
                "                \"prjId\": 2,\n" +
                "                \"tbl\": \"device\",\n" +
                "                \"persistent\": 0,\n" +
                "                \"version\": \"1\",\n" +
                "                \"pvAttr\": \"\",\n" +
                "                \"constructor\": \"  constructor(name, type) {\\r\\n    this.name = name;\\r\\n    this.type = type;\\r\\n  }\",\n" +
                "                \"prjGuid\": \"49e40d23-f3f4-11ef-bac4-8csdasbcbca77\",\n" +
                "                \"prjVer\": \"deviceversion\",\n" +
                "                \"clazzIdFieldDefList\": [\n" +
                "                    {\n" +
                "                        \"id\": 20,\n" +
                "                        \"name\": \"deviceName\",\n" +
                "                        \"type\": \"String\",\n" +
                "                        \"nature\": 1,\n" +
                "                        \"initValue\": \"测试设备1\",\n" +
                "                        \"clazzId\": 19,\n" +
                "                        \"prjId\": 2,\n" +
                "                        \"tblCol\": \"device_name\",\n" +
                "                        \"displayName\": \"设备名称\",\n" +
                "                        \"cachable\": -1,\n" +
                "                        \"defaultLock\": \"lock\",\n" +
                "                        \"classGuid\": \"b65f7c18-732e-4d3c-bf21-cabc14939867\",\n" +
                "                        \"guid\": \"97e4b32c-96aa-4a80-9647-3a90ea0751db\",\n" +
                "                        \"archiveStatus\": 0\n" +
                "                    },\n" +
                "                    {\n" +
                "                        \"id\": 21,\n" +
                "                        \"name\": \"deviceStatus\",\n" +
                "                        \"type\": \"String\",\n" +
                "                        \"nature\": 1,\n" +
                "                        \"initValue\": \"offline\",\n" +
                "                        \"clazzId\": 19,\n" +
                "                        \"prjId\": 2,\n" +
                "                        \"tblCol\": \"device_status\",\n" +
                "                        \"displayName\": \"设备状态\",\n" +
                "                        \"cachable\": 0,\n" +
                "                        \"defaultLock\": \"lock\",\n" +
                "                        \"classGuid\": \"b65f7c18-732e-4d3c-bf21-cabc14939867\",\n" +
                "                        \"guid\": \"f0bc2053-d1c9-4cbd-8121-4514d4b7f968\",\n" +
                "                        \"archiveStatus\": 0\n" +
                "                    }\n" +
                "                ],\n" +
                "                \"clazzIdMethodDefList\": [\n" +
                "                    {\n" +
                "                        \"id\": 17,\n" +
                "                        \"name\": \"power_on\",\n" +
                "                        \"body\": \"{\\r\\n    this.deviceStatus = 'online';  // 修改实例属性\\r\\n    console.log(this.pvAttr);\\r\\n    console.log(`${this.deviceName} 已启动`);\\r\\n    return this.deviceStatus;\\r\\n  }\",\n" +
                "                        \"clazzId\": 19,\n" +
                "                        \"prjId\": 90519240,\n" +
                "                        \"type\": 0,\n" +
                "                        \"description\": \"改变pvAttr值为online\",\n" +
                "                        \"displayName\": \"启动设备\",\n" +
                "                        \"guid\": \"7e290ff8-069f-46ed-97e4-ffe42f25a001\",\n" +
                "                        \"clazzGuid\": \"b65f7c18-732e-4d3c-bf21-cabc14939867\",\n" +
                "                        \"methodIdMethodParamList\": []\n" +
                "                    },\n" +
                "                    {\n" +
                "                        \"id\": 18,\n" +
                "                        \"name\": \"power_off\",\n" +
                "                        \"body\": \"{\\r\\n    this.deviceStatus = 'offline'; // 修改实例属性\\r\\n    console.log(this.pvAttr);\\r\\n    console.log(`${this.deviceName} 已关闭`);\\r\\n    return this.deviceStatus;\\r\\n  }\",\n" +
                "                        \"clazzId\": 19,\n" +
                "                        \"prjId\": 90519240,\n" +
                "                        \"type\": 0,\n" +
                "                        \"description\": \"改变pvAttr值为offline\",\n" +
                "                        \"displayName\": \"关闭设备\",\n" +
                "                        \"guid\": \"dbc34bd7-a0d4-49c9-9227-1c58b5f5bdee\",\n" +
                "                        \"clazzGuid\": \"b65f7c18-732e-4d3c-bf21-cabc14939867\",\n" +
                "                        \"methodIdMethodParamList\": []\n" +
                "                    },\n" +
                "                    {\n" +
                "                        \"id\": 21,\n" +
                "                        \"name\": \"consfunc1\",\n" +
                "                        \"body\": \"{\\r\\n    this.deviceStatus = a;   this.deviceName = b ;  console.log('构造方法 consfunc1 执行') ; }\",\n" +
                "                        \"clazzId\": 19,\n" +
                "                        \"prjId\": 90519240,\n" +
                "                        \"type\": 1,\n" +
                "                        \"description\": \"构造方法1\",\n" +
                "                        \"displayName\": \"构造方法1\",\n" +
                "                        \"guid\": \"dbc3sdasd7-a0d4-49c9-9227-1c58b5f5bdee\",\n" +
                "                        \"clazzGuid\": \"b65f7c18-732e-4d3c-bf21-cabc14939867\",\n" +
                "                        \"methodIdMethodParamList\": [\n" +
                "                            {\n" +
                "                                \"id\": 1,\n" +
                "                                \"name\": \"a\",\n" +
                "                                \"type\": \"String\",\n" +
                "                                \"sort\": 1,\n" +
                "                                \"description\": \"参数 a\",\n" +
                "                                \"methodId\": 21,\n" +
                "                                \"guid\": \"guid-a1234567-89ab-cdef-0123-456789abcdef\",\n" +
                "                                \"methodGuid\": \"dbc3sdasd7-a0d4-49c9-9227-1c58b5f5bdee\"\n" +
                "                            },\n" +
                "                            {\n" +
                "                                \"id\": 2,\n" +
                "                                \"name\": \"b\",\n" +
                "                                \"type\": \"Integer\",\n" +
                "                                \"sort\": 2,\n" +
                "                                \"description\": \"参数 b\",\n" +
                "                                \"methodId\": 21,\n" +
                "                                \"guid\": \"guid-b1234567-89ab-cdef-0123-456789abcdef\",\n" +
                "                                \"methodGuid\": \"dbc3sdasd7-a0d4-49c9-9227-1c58b5f5bdee\"\n" +
                "                            }\n" +
                "                        ]\n" +
                "                    },\n" +
                "                    {\n" +
                "                        \"id\": 22,\n" +
                "                        \"name\": \"consfunc2\",\n" +
                "                        \"body\": \"{\\r\\n    this.deviceStatus = a;   console.log('构造方法 consfunc2 执行') ; }\",\n" +
                "                        \"clazzId\": 19,\n" +
                "                        \"prjId\": 90519240,\n" +
                "                        \"type\": 1,\n" +
                "                        \"description\": \"构造方法2\",\n" +
                "                        \"displayName\": \"构造方法2\",\n" +
                "                        \"guid\": \"e8f2a3bc-1234-5678-9101-1a2b3c4d5e6f\",\n" +
                "                        \"clazzGuid\": \"b65f7c18-732e-4d3c-bf21-cabc14939867\",\n" +
                "                        \"methodIdMethodParamList\": [\n" +
                "                            {\n" +
                "                                \"id\": 31,\n" +
                "                                \"name\": \"a\",\n" +
                "                                \"type\": \"string\",\n" +
                "                                \"sort\": 1,\n" +
                "                                \"description\": \"参数 a，用于设置 deviceStatus\",\n" +
                "                                \"methodId\": 22,\n" +
                "                                \"guid\": \"f7a9d5e2-7890-4567-1234-abcdef123456\",\n" +
                "                                \"methodGuid\": \"e8f2a3bc-1234-5678-9101-1a2b3c4d5e6f\"\n" +
                "                            }\n" +
                "                        ]\n" +
                "                    }\n" +
                "                ]\n" +
                "            }\n" +
                "        ],\n" +
                "        \"sentinelDefList\": [\n" +
                "            {\n" +
                "                \"id\": 4,\n" +
                "                \"name\": \"设备开门\",\n" +
                "                \"body\": \"var myDevice = ClassUtils.newPersist('Device'); \\r\\nmyDevice.power_on(); \\r\\nmyDevice.deviceName = '新name';\\r\\n// setTimeout(() => { myDevice.power_off();}, 10000);  \",\n" +
                "                \"cron\": \"0 * * * * ?\",\n" +
                "                \"concurrent\": 0,\n" +
                "                \"abort\": \"运行3小时后关闭\",\n" +
                "                \"prjId\": 2,\n" +
                "                \"guid\": \"b65f7c18-732e-4d3c-bf21-cabc14939867\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"id\": 5,\n" +
                "                \"name\": \"设备关门\",\n" +
                "                \"body\": \"var myDevice = ClassUtils.newPersist('Device'); \\r\\nmyDevice.power_off(); \",\n" +
                "                \"cron\": \"0 * * * * ?\",\n" +
                "                \"concurrent\": 0,\n" +
                "                \"abort\": \"运行3小时后关闭\",\n" +
                "                \"prjId\": 2,\n" +
                "                \"guid\": \"b65f7c18-732e-4d3c-bf21-cabc14939868\"\n" +
                "            }\n" +
                "        ],\n" +
                "        \"uePrj\": {\n" +
                "            \"id\": 2,\n" +
                "            \"name\": \"deviceprj\",\n" +
                "            \"uiPrjId\": 2,\n" +
                "            \"version\": \"deviceversion\",\n" +
                "            \"guid\": \"49e40d23-f3f4-11ef-bac4-8csdasbcbca77\"\n" +
                "        }\n" +
                "    }";

        PrjSpecDO prjSpecDO = JSON.parseObject(json, PrjSpecDO.class);
//        TaskController bean = SpringUtils.getBean(TaskController.class);
//        bean.run(prjSpecDO);


        RtRedisObjectStorageService rtRedisObjectStorageService = SpringUtils.getBean(RtRedisObjectStorageService.class);
        TaskContext ctx = SpringUtils.getBean(RtContextService.class).startNewSession(prjSpecDO, System.currentTimeMillis() + "");
        rtRedisObjectStorageService.initSpecDefinition(ctx, prjSpecDO);
        rtRedisObjectStorageService.initializeInstances(ctx, prjSpecDO);
        V8EngineService engineService = SpringUtils.getBean(V8EngineService.class);
        engineService.testM(ctx);
    }


}
