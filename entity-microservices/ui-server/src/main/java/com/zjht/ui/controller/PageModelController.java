package com.zjht.ui.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zjht.ui.dto.ModelPrjDTO;
import com.zjht.ui.entity.UiPage;
import com.zjht.ui.entity.UiPrj;
import com.zjht.ui.service.IUiPageService;
import com.zjht.ui.service.IUiPrjService;
import com.zjht.ui.service.PageModelService;
import com.zjht.unified.common.core.constants.Constants;
import com.zjht.unified.common.core.domain.R;
import com.zjht.unified.domain.composite.PrjSpecDO;
import com.zjht.unified.domain.exchange.CID;
import com.zjht.unified.domain.exchange.PageSpec;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Api(value = "模型服务",tags = {"模型服务"})
@RestController
@RequestMapping("/page-model")
public class PageModelController {

    @Autowired
    private PageModelService pageModelService;


    @Autowired
    private IUiPrjService uiPrjService;

    @Autowired
    private IUiPageService uiPageService;

    @ApiOperation(value = "查询类方法定义列表")
    @PostMapping("/update")
    public R<PageSpec> update(@RequestBody PageSpec pageSpec){
        PageSpec ret=pageModelService.savePage(pageSpec);
        return R.ok(ret);
    }

    @ApiOperation(value = "查询类方法定义列表")
    @PostMapping("/getSinglePage")
    public R<PageSpec> getSinglePage(@RequestBody CID id){
        PageSpec ret = pageModelService.getPage(id);
        return R.ok(ret);
    }

    @ApiOperation(value = "查询整个项目所有的页面")
    @PostMapping("/getPageList")
    public R<List<PageSpec>> getPageList(@RequestBody ModelPrjDTO id){
        Long rprjId=id.getRPrjId();
        if(rprjId==null){
            UiPrj rprj = uiPrjService.getOne(new LambdaQueryWrapper<UiPrj>().eq(UiPrj::getExternalId, id.getUePrjId()+""));
            if(rprj!=null){
                rprjId=rprj.getId();
            }
        }

        if(rprjId==null)
            return R.fail("找不到对应的项目");

        List<UiPage> pageList=uiPageService.list(new LambdaQueryWrapper<UiPage>().eq(UiPage::getRprjId,rprjId));
        List<PageSpec> ret = pageList.stream().map(p->pageModelService.getPage(p.getId(),p.getGuid())).collect(Collectors.toList());

        return R.ok(ret);
    }


}
