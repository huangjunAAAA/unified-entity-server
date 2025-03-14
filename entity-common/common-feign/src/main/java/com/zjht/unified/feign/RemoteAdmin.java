package com.zjht.unified.feign;


import com.zjht.unified.common.core.domain.R;
import com.zjht.unified.domain.composite.PrjSpecDO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "entity-admin-server", configuration = IgnoreValidateFormDataConfiguration.class)
public interface RemoteAdmin {
    @PostMapping("/prj/genPrjSpec")
    R<PrjSpecDO> genPrjSpec(@RequestParam Long prjId);
}

