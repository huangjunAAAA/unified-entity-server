package com.zjht.unified.feign;


import com.zjht.unified.common.core.domain.R;
import com.zjht.unified.common.core.domain.dto.BaseQueryDTO;
import com.zjht.unified.common.core.domain.dto.QueryClass;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@FeignClient(value = "entity-store-server-local", configuration = IgnoreValidateFormDataConfiguration.class)
public interface RemoteStore {
    @PostMapping("/store/query-class")
    R<List<Map<String, Object>>> query(@RequestBody BaseQueryDTO<QueryClass> queryDTO);

    @PostMapping("/store/query")
    List<Map<String,Object>> query(@RequestParam String ver, @RequestParam String prjId,  @RequestParam String sql);

    @PostMapping("/store/execute")
    R<Object> execute( @RequestParam String sql,@RequestParam boolean initFlag,@RequestParam String dbName);

    @PostMapping("/store/queryWithArgs")
    List<Map<String, Object>> queryWithArgs(@RequestParam String sql, @RequestBody List<Object> args);
}
