package com.zjht.unified.data.api.controller;


import com.zjht.unified.common.core.domain.R;
import com.zjht.unified.common.core.domain.dto.BaseQueryDTO;
import com.zjht.unified.data.storage.persist.GeneralStoreService;
import com.zjht.unified.common.core.domain.dto.QueryClass;
import com.zjht.unified.data.storage.service.DynamicDataSourceService;
import io.swagger.annotations.Api;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@RestController
@Api(value = "查询数据",tags = {"查询数据"})
public class QueryController {

    @Resource
    protected JdbcTemplate jdbcTemplate;

    @Resource
    private GeneralStoreService generalStoreService;

    @Resource
    private DynamicDataSourceService dynamicDataSourceService;

    @PostMapping("/store/query-class")
    public R<List<Map<String, Object>>> query(@RequestBody BaseQueryDTO<QueryClass> queryDTO) {

        // 查询数据库中的记录
        List<Map<String, Object>> result = generalStoreService.queryEntity(queryDTO.getCondition().getVer(),queryDTO.getCondition().getClassDef(), queryDTO.getPage(), queryDTO.getSize(), queryDTO.getOrderBy(), queryDTO.getAsc()
                , queryDTO.getCondition().getEquals(), queryDTO.getCondition().getLike(), queryDTO.getCondition().getInCondition());
        return R.ok(result);
    }

    @PostMapping("/store/query")
    public List<Map<String, Object>> query(@RequestParam String ver, @RequestParam String prjId, @RequestParam String sql) {
        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);
        return list;
    }

    @PostMapping("/store/execute")
    public R<Object> executeSql( @RequestParam String sql,@RequestParam boolean initFlag,@RequestParam String dbName) {
        try {
            if (initFlag) {
                dynamicDataSourceService.createDataSourceIfNotExists(dbName);
            }
            jdbcTemplate.execute(sql);
            return R.ok();
        } catch (Exception e) {
            return R.fail("SQL执行失败: " + e.getMessage());
        }
    }

    @PostMapping("/store/queryWithArgs")
    public List<Map<String, Object>> queryWithArgs(@RequestParam String sql, @RequestBody List<Object> args) {
        return jdbcTemplate.queryForList(sql, args.toArray());
    }


}
