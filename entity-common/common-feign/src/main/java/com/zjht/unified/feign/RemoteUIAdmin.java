package com.zjht.unified.feign;


import com.zjht.unified.common.core.domain.R;
import com.zjht.unified.domain.composite.PrjSpecDO;
import com.zjht.unified.domain.simple.UiPrjDO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "ui-server", configuration = IgnoreValidateFormDataConfiguration.class)
public interface RemoteUIAdmin {
    @PostMapping("/uiPrj/{prjId}")
    R<UiPrjDO> getPrjInfo(@PathVariable Long prjId);


    @PostMapping("/uiPrj/edit")
    R<Integer> editPrjInfo(@RequestBody UiPrjDO uiPrjDO);

    @PostMapping("/uiPrj/add")
    R<Long> addPrjInfo(@RequestBody UiPrjDO uiPrjDO);

    @PostMapping("/uiPrj/delete/{prjId}")
    R<Integer> delPrjInfo(@PathVariable Long prjId);
}

