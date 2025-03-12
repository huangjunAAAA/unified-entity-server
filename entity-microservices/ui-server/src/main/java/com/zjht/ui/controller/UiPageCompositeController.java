package com.zjht.ui.controller ;

import cn.hutool.core.codec.Base64;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wukong.core.weblog.utils.DateUtil;
import com.wukong.core.mp.base.BaseEntity;
import com.zjht.ui.entity.UiPage;
import com.zjht.ui.service.IUiPageService;

import com.zjht.ui.vo.UiPageCompositeVO;
import com.zjht.ui.dto.UiPageCompositeDTO;
import com.zjht.ui.wrapper.UiPageCompositeWrapper;
import com.zjht.ui.service.IUiPageCompositeService;

import com.zjht.unified.common.core.constants.Constants;
import com.zjht.unified.common.core.controller.BaseController;
import com.zjht.unified.common.core.domain.PageDomain;
import com.zjht.unified.common.core.domain.R;
import com.zjht.unified.common.core.domain.TableDataInfo;
import com.zjht.unified.common.core.domain.dto.BaseQueryDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 *  控制器
 *
 * @author wangy
 */
@Api(value = "项目与组件统一模型(uiPage)维护",tags = {"项目与组件统一模型(uiPage)维护"})
@RestController
@RequestMapping("/uiPage-composite")
public class UiPageCompositeController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(UiPageController.class);
    @Autowired
    private IUiPageService uiPageService;
    @Autowired
    private IUiPageCompositeService uiPageCompositeService;

    /**
     * 查询项目与组件统一模型(uiPage)列表, 对象形式
     */
    @ApiOperation(value = "查询项目与组件统一模型(uiPage)列表")
    @PostMapping("/list-ext")
    public TableDataInfo<UiPageCompositeVO> listExt(@RequestBody BaseQueryDTO<UiPage> uiPage)
    {
        return list(uiPage.getCondition(),new PageDomain(uiPage.getPage(),uiPage.getSize()));
    }

    /**
     * 查询项目与组件统一模型(uiPage)列表
     */
    @ApiOperation(value = "查询项目与组件统一模型(uiPage)列表")
    @GetMapping("/list")
    public TableDataInfo<UiPageCompositeVO> list(UiPage uiPage, PageDomain  pageDomain)
    {
        TableDataInfo<UiPageCompositeVO> dataInfo = new TableDataInfo();
        Page<UiPage> page = new Page<>(pageDomain.getPageNum(),pageDomain.getPageSize());
        IPage<UiPage> list = uiPageService.page(page, Wrappers.<UiPage>lambdaQuery(uiPage).orderByDesc(BaseEntity::getCreateTime));
        List<UiPageCompositeDTO> compositeLst=list.getRecords().stream()
                .map(t->uiPageCompositeService.selectById(t.getId()))
                .collect(Collectors.toList());
        List<UiPageCompositeVO> rows = UiPageCompositeWrapper.build().entityVOList(compositeLst);
        dataInfo.setCode(Constants.SUCCESS);
        dataInfo.setData(rows);
        dataInfo.setMsg("查询成功");
        dataInfo.setTotal(list.getTotal());
        return dataInfo;
    }

    /**
     * 获取项目与组件统一模型(uiPage)Composite详细信息
     */
    @ApiOperation(value = "获取项目与组件统一模型(uiPage)Composite详细信息")
    @GetMapping(value = "/{id}")
    public R<UiPageCompositeVO> getInfo(@PathVariable("id") Long id)
    {
        UiPageCompositeDTO uiPage = uiPageCompositeService.selectById(id);
        return R.ok(UiPageCompositeWrapper.build().entityVO(uiPage));
    }


    /**
     * 新增项目与组件统一模型(uiPage)
     */
    @ApiOperation(value = "新增项目与组件统一模型(uiPage)Composite")
    @PostMapping
    public R<Long> add(@RequestBody UiPageCompositeDTO uiPage)
    {
        uiPage.setId(null);
        uiPage.setCreateTime(DateUtil.now());
        decodePage(uiPage);
        Long id = uiPageCompositeService.submit(uiPage);
        R r = id!=null ? R.ok(id) : R.fail();
        return r;
    }

    private void decodePage(UiPageCompositeDTO uiPage){
        if(uiPage.getPageIdUiComponentList()!=null){
            if(uiPage.getBelongtoIdFilesetList()!=null){
                uiPage.getBelongtoIdFilesetList().forEach(f->{
                    String d = Base64.decodeStr(f.getContent());
                    if(StringUtils.isNotBlank(d))
                        f.setContent(d);
                });
            }
            uiPage.getPageIdUiComponentList().forEach(ic->{
                if(ic.getBelongtoIdFilesetList()!=null){
                    ic.getBelongtoIdFilesetList().forEach(f->{
                        String d = Base64.decodeStr(f.getContent());
                        if(StringUtils.isNotBlank(d))
                            f.setContent(d);
                    });
                }
                if(StringUtils.isNotBlank(ic.getPluginScript())){
                    String d=Base64.decodeStr(ic.getPluginScript());
                    if(StringUtils.isNotBlank(d))
                        ic.setPluginScript(d);
                }
            });
        }
    }

    /**
     * 修改项目与组件统一模型(uiPage)
     */
    @ApiOperation(value = "修改项目与组件统一模型(uiPage)Composite")
    @PostMapping("/edit")
    public R<Long> edit(@RequestBody UiPageCompositeDTO uiPage)
    {
        uiPage.setUpdateTime(DateUtil.now());
        decodePage(uiPage);
        Long id = uiPageCompositeService.submit(uiPage);
        R r = id!=null ? R.ok(id) : R.fail();
        return r;
    }

    /**
     * 删除项目与组件统一模型(uiPage)Composite
     */
    @ApiOperation(value = "删除项目与组件统一模型(uiPage)Composite")
    @PostMapping("/delete/{id}")
    public R<Long> remove(@PathVariable Long id)
    {
        uiPageCompositeService.removeById(id);
        return R.ok(id);
    }

}