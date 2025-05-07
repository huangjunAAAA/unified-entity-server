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
        log.info("数据存储模块启动成功");
    }

}
