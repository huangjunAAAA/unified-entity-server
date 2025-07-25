package com.zjht.unified.feign;


import com.zjht.unified.common.core.domain.R;
import com.zjht.unified.domain.composite.PrjSpecDO;
import com.zjht.unified.domain.simple.UiPrjDO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(value = "ui-server", configuration = IgnoreValidateFormDataConfiguration.class)
public interface RemoteUIAdmin {
    @GetMapping("/uiPrj/{prjId}")
    R<UiPrjDO> getPrjInfoById(@PathVariable Long prjId);


    @PostMapping("/uiPrj/edit")
    R<Integer> editPrjInfo(@RequestBody UiPrjDO uiPrjDO);

    @PostMapping("/uiPrj/add")
    R<Long> addPrjInfo(@RequestBody UiPrjDO uiPrjDO);

    @PostMapping("/uiPrj/delete/{prjId}")
    R<Integer> delPrjInfo(@PathVariable Long prjId);
}

