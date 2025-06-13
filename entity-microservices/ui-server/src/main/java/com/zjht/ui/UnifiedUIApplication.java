package com.zjht.ui;

import com.zjht.ui.service.DeployService;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;

import java.io.File;


@ComponentScan(value = {"com.zjht","com.wukong"})
@EntityScan(basePackages = {"com.zjht"})
@SpringCloudApplication
@EnableFeignClients(basePackages = "com.zjht.**")
@MapperScan("com.zjht.**.mapper.**")
@EnableAsync
@Slf4j
public class UnifiedUIApplication {

    public static void main(String[] args) throws Exception {
        ConfigurableApplicationContext app = SpringApplication.run(UnifiedUIApplication.class, args);
        log.info("统一UI框架数据模块启动成功");
    }
}
