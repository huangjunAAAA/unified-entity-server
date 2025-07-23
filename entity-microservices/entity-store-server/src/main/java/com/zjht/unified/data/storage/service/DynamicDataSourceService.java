package com.zjht.unified.data.storage.service;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import com.google.common.cache.*;
import com.zjht.unified.data.storage.persist.PersistConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

import com.zjht.unified.common.core.constants.Constants;

@Slf4j
@Service
public class DynamicDataSourceService {
    @Resource
    private PersistConfig persistConfig;

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


    public DataSource createDataSourceIfNotExists(String dbName) {
        try (Connection conn = defaultDataSource.getConnection()) {
            String createSql = "CREATE DATABASE IF NOT EXISTS `" + dbName + "`" ;
            if (persistConfig.getEngine().equals(Constants.STORE_ENGINE_MYSQL)) {
                createSql += " CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci ";
            }
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
        HikariDataSource hikariDataSource = new HikariDataSource(config);
        initDefaultTable(hikariDataSource);
        return hikariDataSource;
    }

    private void initDefaultTable(HikariDataSource hikariDataSource) {
        String createSql = "";
        if (Constants.STORE_ENGINE_MYSQL.equalsIgnoreCase(persistConfig.getEngine())) {
            createSql = "CREATE TABLE IF NOT EXISTS `g_mapping` ("
                    + " `data_time` datetime COMMENT '数据时间',"
                    + " `guid` varchar(40) COMMENT 'guid',"
                    + " `meta_data` varchar(200) COMMENT '数据所在位置',"
                    + " `status` smallint COMMENT '状态'"
                    + ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;";
        } else if (Constants.STORE_ENGINE_DORIS.equalsIgnoreCase(persistConfig.getEngine())) {
            createSql = "CREATE TABLE IF NOT EXISTS `g_mapping` ("
                    + " `data_time` datetime COMMENT '数据时间',"
                    + " `guid` varchar(40) COMMENT 'guid',"
                    + " `meta_data` varchar(200) COMMENT '数据所在位置',"
                    + " `status` smallint COMMENT '状态'"
                    + ") ENGINE=olap "
                    + "PARTITION BY RANGE(data_time) () "
                    + "DISTRIBUTED BY HASH(guid) "
                    + "PROPERTIES ("
                    + " \"dynamic_partition.enable\" = \"true\","
                    + " \"dynamic_partition.time_unit\" = \"MONTH\","
                    + " \"dynamic_partition.end\" = \"2\","
                    + " \"dynamic_partition.prefix\" = \"p\","
                    + " \"dynamic_partition.buckets\" = \"8\","
                    + " \"dynamic_partition.start_day_of_month\" = \"1\""
                    + ");";
        }
        try (Connection conn = hikariDataSource.getConnection()) {
            conn.createStatement().execute(createSql);
            log.info("初始化表 g_mapping 成功");
        } catch (SQLException e) {
            log.error("初始化表 g_mapping 失败: {}", e.getMessage(), e);
        }
    }

}
