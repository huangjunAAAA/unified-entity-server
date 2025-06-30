package com.zjht.unified.data.storage.service;


import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import com.zjht.unified.common.core.domain.store.EntityStoreMessageDO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class DynamicDataSourceService {

    private final Map<String, DataSource> dataSourceCache = new ConcurrentHashMap<>();
    private DataSource adminDataSource;

    private final String DB_HOST = "192.168.4.89";
    private final int DB_PORT = 3306;
    private final String DB_USERNAME = "root";
    private final String DB_PASSWORD = "Root123456";

    @PostConstruct
    public void initAdminDataSource() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(String.format("jdbc:mysql://%s:%d/mysql?characterEncoding=utf8&useSSL=false", DB_HOST, DB_PORT));
        config.setUsername(DB_USERNAME);
        config.setPassword(DB_PASSWORD);
        config.setDriverClassName("com.mysql.cj.jdbc.Driver");
        this.adminDataSource = new HikariDataSource(config);
    }

    public JdbcTemplate getJdbcTemplateForVersion(String version) {
        String dbName = "uui_store_" + version.replace(".", "_");
        DataSource dataSource = dataSourceCache.computeIfAbsent(dbName, this::createDataSourceIfNotExists);
        return new JdbcTemplate(dataSource);
    }

    private DataSource createDataSourceIfNotExists(String dbName) {
        try (Connection conn = adminDataSource.getConnection()) {
            String sql = "CREATE DATABASE IF NOT EXISTS `" + dbName + "` CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci";
            conn.createStatement().execute(sql);
            log.info("Database '{}' ensured.", dbName);
        } catch (SQLException e) {
            log.error(" Failed to ensure database '{}'", dbName, e);
            throw new RuntimeException(e);
        }

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(String.format("jdbc:mysql://%s:%d/%s?characterEncoding=utf8&useSSL=false", DB_HOST, DB_PORT, dbName));
        config.setUsername(DB_USERNAME);
        config.setPassword(DB_PASSWORD);
        config.setDriverClassName("com.mysql.cj.jdbc.Driver");
        config.setMaximumPoolSize(10);
        return new HikariDataSource(config);
    }

}

