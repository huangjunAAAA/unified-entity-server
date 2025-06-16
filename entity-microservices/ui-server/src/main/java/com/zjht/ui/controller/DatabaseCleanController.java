package com.zjht.ui.controller;

import com.zjht.unified.common.core.domain.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/dbclean")
public class DatabaseCleanController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @PostMapping("/cleanUi")
    public R<String> cleanUiData() {
        try {
            // 1. 查询所有页面 ID
            List<Long> pageIds = jdbcTemplate.queryForList(
                    "SELECT id FROM ui_page",
                    Long.class
            );
            if (pageIds.isEmpty()) {
                return R.ok("无页面记录，无需清理");
            }

            // 2. 查询所有组件 ID（通过页面 ID）
            List<Long> componentIds = jdbcTemplate.queryForList(
                    "SELECT id FROM ui_component WHERE page_id IN (" + joinIds(pageIds) + ")",
                    Long.class
            );

            // 3. 删除 ui_event_handle 中关联的 component_id
            if (!componentIds.isEmpty()) {
                int deletedEventHandles = jdbcTemplate.update(
                        "DELETE FROM ui_event_handle WHERE component_id IN (" + joinIds(componentIds) + ")"
                );
                log.info("删除 ui_event_handle 记录数：{}", deletedEventHandles);
            }

            // 4. 删除组件
            int deletedComponents = jdbcTemplate.update(
                    "DELETE FROM ui_component WHERE page_id IN (" + joinIds(pageIds) + ")"
            );
            log.info("删除 ui_component 记录数：{}", deletedComponents);

            // 5. 删除页面
            int deletedPages = jdbcTemplate.update("DELETE FROM ui_page");
            log.info("删除 ui_page 记录数：{}", deletedPages);

            // 6. 删除 fileset 中关联页面的记录
            int deletedFiles = jdbcTemplate.update("DELETE FROM fileset WHERE belongto_type = 'page'");
            log.info("删除 fileset 记录数（belongto_type = 'page'）：{}", deletedFiles);

            return R.ok("UI 数据清理完成：ui_page：" + deletedPages +
                    "，ui_component：" + deletedComponents +
                    "，ui_event_handle：" + componentIds.size() +
                    "，fileset：" + deletedFiles);
        } catch (Exception e) {
            log.error("UI 数据清理失败", e);
            return R.fail("清理失败：" + e.getMessage());
        }
    }

    private String joinIds(List<Long> ids) {
        return String.join(",", ids.stream().map(String::valueOf).toArray(String[]::new));
    }
}
