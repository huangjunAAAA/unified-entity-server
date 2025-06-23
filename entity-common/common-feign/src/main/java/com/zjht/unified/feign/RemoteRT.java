package com.zjht.unified.feign;

import com.zjht.unified.common.core.domain.R;
import com.zjht.unified.common.core.domain.dto.GetParam;
import com.zjht.unified.domain.composite.ClazzDefCompositeDO;
import com.zjht.unified.domain.composite.PrjSpecDO;
import com.zjht.unified.feign.model.ReturnMap;
import com.zjht.unified.feign.model.ReturnT;
import com.zjht.unified.feign.model.XxlJobGroup;
import com.zjht.unified.feign.model.XxlJobInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "entity-rt-server", configuration = IgnoreValidateFormDataConfiguration.class)
public interface RemoteRT {
    @PostMapping("/rt/task/run-project")
    R<String> startProject(@RequestBody PrjSpecDO spec);
    @PostMapping("/rt/task/stop-project")
    R<String> stopProject(@RequestParam("prjId") Long prjId);
    @PostMapping("/get-class-def")
    R<ClazzDefCompositeDO> getObjectClassDef(@RequestBody GetParam param);
}
