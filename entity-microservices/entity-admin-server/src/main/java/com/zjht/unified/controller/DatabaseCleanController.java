package com.zjht.unified.controller;

import com.google.common.collect.Lists;
import com.zjht.unified.common.core.domain.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/dbclean")
public class DatabaseCleanController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final List<String> EXCLUDED_TABLES = Lists.newArrayList("ue_prj");

    @PostMapping("/clean")
    public R<String> cleanByPrjId(@RequestParam(value = "prjId", required = false) Long prjId) {
        try {
            List<String> allTables = jdbcTemplate.queryForList(
                    "SELECT table_name FROM information_schema.tables WHERE table_schema = 'unified_entity' AND table_type = 'BASE TABLE'",
                    String.class
            );

            List<String> targetTables = new ArrayList<>();

            for (String table : allTables) {
                if (EXCLUDED_TABLES.contains(table)) continue;

                // 检查是否包含 prj_id 字段
                Integer count = jdbcTemplate.queryForObject(
                        "SELECT COUNT(*) FROM information_schema.columns " +
                                "WHERE table_schema = 'unified_entity' AND table_name = ? AND column_name = 'prj_id'",
                        Integer.class, table
                );

                if (count != null && count > 0) {
                    targetTables.add(table);
                }
            }

            for (String table : targetTables) {
                int affected;
                if (prjId == null) {
                    affected = jdbcTemplate.update("DELETE FROM `" + table + "`");
                    log.info("清理表 {}（全表删除），影响行数: {}", table, affected);
                } else {
                    affected = jdbcTemplate.update("DELETE FROM `" + table + "` WHERE prj_id = ?", prjId);
                    log.info("清理表 {} WHERE prj_id={}，影响行数: {}", table, prjId, affected);
                }
            }

            String msg = prjId == null
                    ? "已清除所有项目数据（不含 " + EXCLUDED_TABLES + "），共处理 " + targetTables.size() + " 张表。"
                    : "项目 ID 为 " + prjId + " 的数据清理完成，共处理 " + targetTables.size() + " 张表。";

            return R.ok(msg);
        } catch (Exception e) {
            log.error("清理失败", e);
            return R.fail("清理失败: " + e.getMessage());
        }
    }
}
