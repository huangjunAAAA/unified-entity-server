package com.zjht.unified;

import com.zjht.unified.common.core.json.GsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.*;

@ComponentScan(value = {"com.zjht","com.wukong"})
@EntityScan(basePackages = {"com.zjht"})
@SpringCloudApplication
@EnableFeignClients(basePackages = "com.zjht.**")
@MapperScan("com.zjht.**.mapper.**")
@EnableAsync
@Slf4j
public class UnifiedEntityConfigAdminApplication {
    public static void main(String[] args) throws Exception {
//        ConfigurableApplicationContext app = SpringApplication.run(UnifiedEntityConfigAdminApplication.class, args);
        log.info("统一实体组态模块启动成功");

        Map<Object,Object> allSettings=new LinkedHashMap<>();
        allSettings.put("instanceExport",new InstancePermitAll());
        allSettings.put("classExport",new InstancePermitAll());
        System.out.println(GsonUtil.toJson(allSettings));
    }

    static class InstancePermitAll{
        int readAll=1;
        int writeAll=1;
        int delAll=0;
        List<InstsancePermit> details=new ArrayList<InstsancePermit>(Arrays.asList(new InstsancePermit(),new InstsancePermit()));
    }

    static class InstsancePermit{
        String guid=UUID.randomUUID().toString();
        int write=0;
        int read=1;
        int del=1;
    }

    static class ClazzPermitAll{
        int newAll=0;
        int inheritAll=1;
        List<ClazzPermit> details=new ArrayList<ClazzPermit>(Arrays.asList(new ClazzPermit(),new ClazzPermit()));
    }

    static class ClazzPermit{
        String guid= UUID.randomUUID().toString();
        int newInstance=0;
        int inherit=1;
    }

}
