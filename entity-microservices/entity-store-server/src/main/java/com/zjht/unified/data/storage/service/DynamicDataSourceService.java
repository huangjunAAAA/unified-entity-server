package com.zjht.unified.data.storage.service;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import com.google.common.cache.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

import com.zjht.unified.common.core.constants.Constants;

@Slf4j
@Service
public class DynamicDataSourceService {

    @Autowired
    private DataSourceProperties dataSourceProperties;

    @Autowired
    private DataSource defaultDataSource;

    private LoadingCache<String, DataSource> dataSourceCache;

    @PostConstruct
    public void initCache() {
        this.dataSourceCache = CacheBuilder.newBuilder()
                .maximumSize(100)
                .expireAfterAccess(30, TimeUnit.MINUTES)
                .removalListener((RemovalListener<String, DataSource>) notification -> {
                    DataSource ds = notification.getValue();
                    if (ds instanceof HikariDataSource) {
                        log.info("Closing datasource for db: {}", notification.getKey());
                        ((HikariDataSource) ds).close();
                    }
                })
                .build(new CacheLoader<String, DataSource>() {
                    @Override
                    public DataSource load(String dbName) {
                        return createDataSourceIfNotExists(dbName);
                    }
                });
    }

    public JdbcTemplate getJdbcTemplateForVersion(String version) {
        String dbName = Constants.STORE_DBNAME_PREFIX + version;
        try {
            DataSource dataSource = dataSourceCache.get(dbName);
            return new JdbcTemplate(dataSource);
        } catch (Exception e) {
            log.error("获取数据源失败: {}", e.getMessage());
        }
        return null;
    }

    private DataSource createDataSourceIfNotExists(String dbName) {
        try (Connection conn = defaultDataSource.getConnection()) {
            String createSql = "CREATE DATABASE IF NOT EXISTS `" + dbName + "` CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci";
            conn.createStatement().execute(createSql);
            log.info("数据库 {} 已创建", dbName);
        } catch (SQLException e) {
            log.error("创建数据库 {} 失败: {}", dbName, e.getMessage());
            throw new RuntimeException(e);
        }
        String baseUrl = dataSourceProperties.getUrl();
        String jdbcPrefix = baseUrl.substring(0, baseUrl.lastIndexOf("/") + 1);
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(jdbcPrefix + dbName + "?characterEncoding=utf8&useSSL=false");
        config.setUsername(dataSourceProperties.getUsername());
        config.setPassword(dataSourceProperties.getPassword());
        config.setMaximumPoolSize(10);
        config.setMinimumIdle(2);
        config.setIdleTimeout(600000);
        config.setConnectionTimeout(30000);
        log.info("创建新数据源：{}", config.getJdbcUrl());
        return new HikariDataSource(config);
    }
}
