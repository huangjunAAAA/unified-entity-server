package com.zjht.unified.data.storage.persist;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

@Data
@Component
@ConfigurationProperties(prefix = "persistence")
public class PersistConfig {
    private String engine;
    private Map<String,String> doris;
}
