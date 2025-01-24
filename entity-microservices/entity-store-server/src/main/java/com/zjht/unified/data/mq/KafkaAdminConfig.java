package com.zjht.unified.data.mq;

import com.wukong.bigdata.kafka.config.BigdataKafkaProperties;
import org.apache.kafka.clients.admin.Admin;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

@Configuration
public class KafkaAdminConfig {

    @Bean
    public Admin createKafkaAdmin(BigdataKafkaProperties bigdataKafkaProperties) {
        Properties properties = new Properties();
        properties.put(
                AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bigdataKafkaProperties.getBootstrapServers()
        );
        Admin admin = Admin.create(properties);
        return admin;
    }
}
