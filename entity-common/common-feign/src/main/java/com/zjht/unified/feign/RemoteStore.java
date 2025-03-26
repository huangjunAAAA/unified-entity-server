package com.zjht.unified.feign;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@FeignClient(value = "entity-store-server", configuration = IgnoreValidateFormDataConfiguration.class)
public interface RemoteStore {
    @PostMapping("/rt/query")
    List<Map<String,Object>> query(@RequestParam String ver, @RequestParam String prjId,  @RequestParam String sql);
}
