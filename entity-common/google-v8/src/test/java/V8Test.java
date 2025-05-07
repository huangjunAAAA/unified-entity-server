import com.caoccao.javet.annotations.V8Function;
import com.caoccao.javet.interception.logging.JavetStandardConsoleInterceptor;
import com.caoccao.javet.interop.V8Runtime;
import com.caoccao.javet.interop.converters.JavetProxyConverter;
import com.caoccao.javet.interop.engine.IJavetEngine;
import com.caoccao.javet.interop.engine.IJavetEnginePool;
import com.caoccao.javet.interop.engine.JavetEnginePool;
import com.caoccao.javet.interop.proxy.JavetDirectProxyFunctionHandler;
import com.caoccao.javet.values.V8Value;
import com.caoccao.javet.values.reference.V8ValueFunction;
import com.caoccao.javet.values.reference.V8ValueObject;

import com.zjht.unified.common.core.util.SpringUtils;
import com.zjht.unified.common.core.util.UUID;
import com.zjht.unified.domain.composite.ClazzDefCompositeDO;
import com.zjht.unified.domain.composite.FieldDefCompositeDO;
import com.zjht.unified.domain.composite.MethodDefCompositeDO;
import com.zjht.unified.domain.composite.PrjSpecDO;
import com.zjht.unified.domain.simple.MethodDefDO;
import com.zjht.unified.domain.simple.UiPrjDO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


@Slf4j
@Service
public class V8Test {


    private static final IJavetEnginePool<V8Runtime> javetEnginePool = new JavetEnginePool<>();



    public static void main(String[] args) {
    }

    @V8Function(name = "new")
    public V8Value newInstance( V8Value... args) throws Exception {
        V8Value v8Value = new JavetProxyConverter().toV8Value(getRuntime(), new TestProxyObject());
        if (v8Value instanceof V8ValueObject) {
            V8ValueObject value = (V8ValueObject) v8Value;
            V8ValueFunction m2Function = (V8ValueFunction) getRuntime()
                    .getExecutor("(function() { return function(x) { return this.name + x; }; })()")
                    .execute();

            value.set("m2", m2Function);

        }
        return v8Value;
    }


    public static Object exec(String script) {
        try {
            V8Runtime v8Runtime = getRuntime();
            V8Value tst = new JavetProxyConverter().toV8Value(v8Runtime, new TestProxyObject());
            v8Runtime.getGlobalObject().set("tst", tst);
            Object o = v8Runtime.getExecutor(script).executeObject();
            return o;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static V8Runtime getRuntime() {
        try (IJavetEngine<V8Runtime> javetEngine = javetEnginePool.getEngine()) {
            V8Runtime v8Runtime = javetEngine.getV8Runtime();
            JavetStandardConsoleInterceptor consoleInterceptor = new JavetStandardConsoleInterceptor(v8Runtime);
            consoleInterceptor.register(v8Runtime.getGlobalObject());

            try (V8ValueObject v8ValueObject = v8Runtime.createV8ValueObject()) {
                v8Runtime.getGlobalObject().set("V8Test", v8ValueObject);
                V8Test v8Test = new V8Test();
                v8ValueObject.bind(v8Test);
            }            return v8Runtime;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }


    public void testM() {

        ExecutorService executor = Executors.newFixedThreadPool(2);

//        exec(
//                "var a =ClassUtils.new(\"ClassA\");\n" +
//                        "var b  = ClassUtils.new(\"ClassA\")\n;" +
//                        "a.f1 = b ;\n" +
//                        "b.f2 = 3;\n" +
//                        "console.log(\"111111111111111111111111\");\n"+
//                        "console.log(\"\"+a.f1.f2);"

//                "var a = ClassUtils.newPersist(\"ClassA\");"+
//                "a.name = \"张三new\";"+
//                "var a = ClassUtils.new(\"Device\",1,2);"+
//                        " //console.log(a.power_on()); \n"+
//                        " console.log(`a.pv = ${a.pv}`);" +
//                        " console.log(`a.name.archiveStatus = ${a.deviceName.archiveStatus}`);"+
//                        " console.log(`a.deviceName + 1 = ${a.deviceName + 1}`);"+
//                        " console.log(`a.deviceName.eval = ${a.deviceName.eval}`);"+
//                        " console.log(`a.deviceName.currentvalue is ${a.deviceName }`);"+
//                        " console.log(`a.deviceName.lastvalue is  ${a.deviceName.lastValue}`);"+
//                        " console.log(`a.deviceName.lastEv is  ${a.deviceName.lastEv}`);"+
//
//                        " console.log(` exec a.deviceName + 1   ${a.deviceName = a.deviceName + 1 }`);"+
//
//                        " console.log(`a.deviceName.currentvalue is ${a.deviceName }`);"+
//                        " console.log(`a.deviceName.lastvalue is  ${a.deviceName.lastValue}`);"+
//                        " console.log(`a.deviceName.lastEv is  ${a.deviceName.lastEv}`);"
//                "var myDevice = ClassUtils.newPersist('Device'); \n" +
//                        "myDevice.power_on(); \n" +
//                        "myDevice.deviceName = '新name';\n"
//                , taskContext);

//        exec(
//                "var myDevice = ClassUtils.newPersist('Device'); \n" +
//                        "myDevice.power_off(); "
//                , taskContext);

//        Runnable task1 = () -> {
//            exec(
//                    "var myDevice = ClassUtils.newPersist('Device'); \n" +
//                            "myDevice.power_on(); \n" +
//                            "myDevice.deviceName = '新name';\n",
////                            "myDevice.deviceStatus = '新name';\n",
//                    taskContext
//            );
//        };
//
//        Runnable task2 = () -> {
//            exec(
//                    "var myDevice = ClassUtils.newPersist('Device'); \n" +
//                            "myDevice.power_off();",
//                    taskContext
//            );
//        };
//
//        executor.submit(task1);
//        executor.submit(task2);
//        executor.shutdown();
    }

    public void testExec() {
        ExecutorService executor = Executors.newFixedThreadPool(2);

        Runnable task1 = () -> {
            exec(
                    "1+1"

            );
        };

        Runnable task2 = () -> {
            exec(
                    "1+2"
            );
        };

        executor.submit(task1);
        executor.submit(task2);
        executor.shutdown();
    }


//    private static void test() {
//        String json = "{\n" +
//                "        \"clazzList\": [\n" +
//                "            {\n" +
//                "                \"id\": 19,\n" +
//                "                \"guid\": \"b65f7c18-732e-4d3c-bf21-cabc14939867\",\n" +
//                "                \"name\": \"Device\",\n" +
//                "                \"nameZh\": \"设备类\",\n" +
//                "                \"type\": \"1\",\n" +
//                "                \"prjId\": 2,\n" +
//                "                \"tbl\": \"device\",\n" +
//                "                \"persistent\": 0,\n" +
//                "                \"version\": \"1\",\n" +
//                "                \"pvAttr\": \"\",\n" +
//                "                \"constructor\": \"  constructor(name, type) {\\r\\n    this.name = name;\\r\\n    this.type = type;\\r\\n  }\",\n" +
//                "                \"prjGuid\": \"49e40d23-f3f4-11ef-bac4-8csdasbcbca77\",\n" +
//                "                \"prjVer\": \"deviceversion\",\n" +
//                "                \"clazzIdFieldDefList\": [\n" +
//                "                    {\n" +
//                "                        \"id\": 20,\n" +
//                "                        \"name\": \"deviceName\",\n" +
//                "                        \"type\": \"String\",\n" +
//                "                        \"nature\": 1,\n" +
//                "                        \"initValue\": \"测试设备1\",\n" +
//                "                        \"clazzId\": 19,\n" +
//                "                        \"prjId\": 2,\n" +
//                "                        \"tblCol\": \"device_name\",\n" +
//                "                        \"displayName\": \"设备名称\",\n" +
//                "                        \"cachable\": -1,\n" +
//                "                        \"defaultLock\": \"lock\",\n" +
//                "                        \"classGuid\": \"b65f7c18-732e-4d3c-bf21-cabc14939867\",\n" +
//                "                        \"guid\": \"97e4b32c-96aa-4a80-9647-3a90ea0751db\",\n" +
//                "                        \"archiveStatus\": 0\n" +
//                "                    },\n" +
//                "                    {\n" +
//                "                        \"id\": 21,\n" +
//                "                        \"name\": \"deviceStatus\",\n" +
//                "                        \"type\": \"String\",\n" +
//                "                        \"nature\": 1,\n" +
//                "                        \"initValue\": \"offline\",\n" +
//                "                        \"clazzId\": 19,\n" +
//                "                        \"prjId\": 2,\n" +
//                "                        \"tblCol\": \"device_status\",\n" +
//                "                        \"displayName\": \"设备状态\",\n" +
//                "                        \"cachable\": 0,\n" +
//                "                        \"defaultLock\": \"lock\",\n" +
//                "                        \"classGuid\": \"b65f7c18-732e-4d3c-bf21-cabc14939867\",\n" +
//                "                        \"guid\": \"f0bc2053-d1c9-4cbd-8121-4514d4b7f968\",\n" +
//                "                        \"archiveStatus\": 0\n" +
//                "                    }\n" +
//                "                ],\n" +
//                "                \"clazzIdMethodDefList\": [\n" +
//                "                    {\n" +
//                "                        \"id\": 17,\n" +
//                "                        \"name\": \"power_on\",\n" +
//                "                        \"body\": \"{\\r\\n    this.deviceStatus = 'online';  // 修改实例属性\\r\\n    console.log(this.pvAttr);\\r\\n    console.log(`${this.deviceName} 已启动`);\\r\\n    return this.deviceStatus;\\r\\n  }\",\n" +
//                "                        \"clazzId\": 19,\n" +
//                "                        \"prjId\": 90519240,\n" +
//                "                        \"type\": 0,\n" +
//                "                        \"description\": \"改变pvAttr值为online\",\n" +
//                "                        \"displayName\": \"启动设备\",\n" +
//                "                        \"guid\": \"7e290ff8-069f-46ed-97e4-ffe42f25a001\",\n" +
//                "                        \"clazzGuid\": \"b65f7c18-732e-4d3c-bf21-cabc14939867\",\n" +
//                "                        \"methodIdMethodParamList\": []\n" +
//                "                    },\n" +
//                "                    {\n" +
//                "                        \"id\": 18,\n" +
//                "                        \"name\": \"power_off\",\n" +
//                "                        \"body\": \"{\\r\\n    this.deviceStatus = 'offline'; // 修改实例属性\\r\\n    console.log(this.pvAttr);\\r\\n    console.log(`${this.deviceName} 已关闭`);\\r\\n    return this.deviceStatus;\\r\\n  }\",\n" +
//                "                        \"clazzId\": 19,\n" +
//                "                        \"prjId\": 90519240,\n" +
//                "                        \"type\": 0,\n" +
//                "                        \"description\": \"改变pvAttr值为offline\",\n" +
//                "                        \"displayName\": \"关闭设备\",\n" +
//                "                        \"guid\": \"dbc34bd7-a0d4-49c9-9227-1c58b5f5bdee\",\n" +
//                "                        \"clazzGuid\": \"b65f7c18-732e-4d3c-bf21-cabc14939867\",\n" +
//                "                        \"methodIdMethodParamList\": []\n" +
//                "                    },\n" +
//                "                    {\n" +
//                "                        \"id\": 21,\n" +
//                "                        \"name\": \"consfunc1\",\n" +
//                "                        \"body\": \"{\\r\\n    this.deviceStatus = a;   this.deviceName = b ;  console.log('构造方法 consfunc1 执行') ; }\",\n" +
//                "                        \"clazzId\": 19,\n" +
//                "                        \"prjId\": 90519240,\n" +
//                "                        \"type\": 1,\n" +
//                "                        \"description\": \"构造方法1\",\n" +
//                "                        \"displayName\": \"构造方法1\",\n" +
//                "                        \"guid\": \"dbc3sdasd7-a0d4-49c9-9227-1c58b5f5bdee\",\n" +
//                "                        \"clazzGuid\": \"b65f7c18-732e-4d3c-bf21-cabc14939867\",\n" +
//                "                        \"methodIdMethodParamList\": [\n" +
//                "                            {\n" +
//                "                                \"id\": 1,\n" +
//                "                                \"name\": \"a\",\n" +
//                "                                \"type\": \"String\",\n" +
//                "                                \"sort\": 1,\n" +
//                "                                \"description\": \"参数 a\",\n" +
//                "                                \"methodId\": 21,\n" +
//                "                                \"guid\": \"guid-a1234567-89ab-cdef-0123-456789abcdef\",\n" +
//                "                                \"methodGuid\": \"dbc3sdasd7-a0d4-49c9-9227-1c58b5f5bdee\"\n" +
//                "                            },\n" +
//                "                            {\n" +
//                "                                \"id\": 2,\n" +
//                "                                \"name\": \"b\",\n" +
//                "                                \"type\": \"Integer\",\n" +
//                "                                \"sort\": 2,\n" +
//                "                                \"description\": \"参数 b\",\n" +
//                "                                \"methodId\": 21,\n" +
//                "                                \"guid\": \"guid-b1234567-89ab-cdef-0123-456789abcdef\",\n" +
//                "                                \"methodGuid\": \"dbc3sdasd7-a0d4-49c9-9227-1c58b5f5bdee\"\n" +
//                "                            }\n" +
//                "                        ]\n" +
//                "                    },\n" +
//                "                    {\n" +
//                "                        \"id\": 22,\n" +
//                "                        \"name\": \"consfunc2\",\n" +
//                "                        \"body\": \"{\\r\\n    this.deviceStatus = a;   console.log('构造方法 consfunc2 执行') ; }\",\n" +
//                "                        \"clazzId\": 19,\n" +
//                "                        \"prjId\": 90519240,\n" +
//                "                        \"type\": 1,\n" +
//                "                        \"description\": \"构造方法2\",\n" +
//                "                        \"displayName\": \"构造方法2\",\n" +
//                "                        \"guid\": \"e8f2a3bc-1234-5678-9101-1a2b3c4d5e6f\",\n" +
//                "                        \"clazzGuid\": \"b65f7c18-732e-4d3c-bf21-cabc14939867\",\n" +
//                "                        \"methodIdMethodParamList\": [\n" +
//                "                            {\n" +
//                "                                \"id\": 31,\n" +
//                "                                \"name\": \"a\",\n" +
//                "                                \"type\": \"string\",\n" +
//                "                                \"sort\": 1,\n" +
//                "                                \"description\": \"参数 a，用于设置 deviceStatus\",\n" +
//                "                                \"methodId\": 22,\n" +
//                "                                \"guid\": \"f7a9d5e2-7890-4567-1234-abcdef123456\",\n" +
//                "                                \"methodGuid\": \"e8f2a3bc-1234-5678-9101-1a2b3c4d5e6f\"\n" +
//                "                            }\n" +
//                "                        ]\n" +
//                "                    }\n" +
//                "                ]\n" +
//                "            }\n" +
//                "        ],\n" +
//                "        \"sentinelDefList\": [\n" +
//                "            {\n" +
//                "                \"id\": 4,\n" +
//                "                \"name\": \"设备开门\",\n" +
//                "                \"body\": \"var myDevice = ClassUtils.newPersist('Device'); \\r\\nmyDevice.power_on(); \\r\\nmyDevice.deviceName = '新name';\\r\\n// setTimeout(() => { myDevice.power_off();}, 10000);  \",\n" +
//                "                \"cron\": \"0 * * * * ?\",\n" +
//                "                \"concurrent\": 0,\n" +
//                "                \"abort\": \"运行3小时后关闭\",\n" +
//                "                \"prjId\": 2,\n" +
//                "                \"guid\": \"b65f7c18-732e-4d3c-bf21-cabc14939867\"\n" +
//                "            },\n" +
//                "            {\n" +
//                "                \"id\": 5,\n" +
//                "                \"name\": \"设备关门\",\n" +
//                "                \"body\": \"var myDevice = ClassUtils.newPersist('Device'); \\r\\nmyDevice.power_off(); \",\n" +
//                "                \"cron\": \"0 * * * * ?\",\n" +
//                "                \"concurrent\": 0,\n" +
//                "                \"abort\": \"运行3小时后关闭\",\n" +
//                "                \"prjId\": 2,\n" +
//                "                \"guid\": \"b65f7c18-732e-4d3c-bf21-cabc14939868\"\n" +
//                "            }\n" +
//                "        ],\n" +
//                "        \"uePrj\": {\n" +
//                "            \"id\": 2,\n" +
//                "            \"name\": \"deviceprj\",\n" +
//                "            \"uiPrjId\": 2,\n" +
//                "            \"version\": \"deviceversion\",\n" +
//                "            \"guid\": \"49e40d23-f3f4-11ef-bac4-8csdasbcbca77\"\n" +
//                "        }\n" +
//                "    }";
//
//        PrjSpecDO prjSpecDO = JSON.parseObject(json, PrjSpecDO.class);
////        TaskController bean = SpringUtils.getBean(TaskController.class);
////        bean.run(prjSpecDO);
//
//
//        RtRedisObjectStorageService rtRedisObjectStorageService = SpringUtils.getBean(RtRedisObjectStorageService.class);
//        TaskContext ctx = SpringUtils.getBean(RtContextService.class).startNewSession(prjSpecDO, System.currentTimeMillis() + "");
//        rtRedisObjectStorageService.initSpecDefinition(ctx, prjSpecDO);
//        rtRedisObjectStorageService.initializeInstances(ctx, prjSpecDO);
//        V8EngineService engineService = SpringUtils.getBean(V8EngineService.class);
//        engineService.testM(ctx);
//    }




}
