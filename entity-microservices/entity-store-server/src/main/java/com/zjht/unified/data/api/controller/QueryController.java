package com.zjht.unified.data.api.controller;


import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@Api(value = "查询控制器",tags = {"查询控制器"})
public class QueryController {
    @PostMapping("/rt/query")
    List<Map<String, Object>> query(String ver, String prjId, String sql) {
        //todo
        return null;
    }
}
