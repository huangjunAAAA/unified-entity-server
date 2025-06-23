package com.zjht.unified.data.api.controller;


import com.zjht.unified.common.core.domain.R;
import com.zjht.unified.common.core.domain.dto.BaseQueryDTO;
import com.zjht.unified.data.storage.persist.GeneralStoreService;
import com.zjht.unified.common.core.domain.dto.QueryClass;
import com.zjht.unified.feign.RemoteRT;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@RestController
@Api(value = "查询数据",tags = {"查询数据"})
public class QueryController {

    @Resource
    private GeneralStoreService generalStoreService;

    @Resource
    private RemoteRT remoteRT;

    @PostMapping("/store/query-class")
    public R<List<Map<String, Object>>> query(@RequestBody BaseQueryDTO<QueryClass> queryDTO) {

        // 查询数据库中的记录
        List<Map<String, Object>> result = generalStoreService.queryEntity(queryDTO.getCondition().getClassDef(), queryDTO.getPage(), queryDTO.getSize(), queryDTO.getOrderBy(), queryDTO.getAsc());
        return R.ok(result);
    }
}
