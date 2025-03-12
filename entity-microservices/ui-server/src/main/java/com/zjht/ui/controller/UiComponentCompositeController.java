package com.zjht.ui.controller ;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wukong.core.weblog.utils.DateUtil;
import com.wukong.core.mp.base.BaseEntity;
import com.zjht.ui.entity.UiComponent;
import com.zjht.ui.service.IUiComponentService;

import com.zjht.ui.vo.UiComponentCompositeVO;
import com.zjht.ui.dto.UiComponentCompositeDTO;
import com.zjht.ui.wrapper.UiComponentCompositeWrapper;
import com.zjht.ui.service.IUiComponentCompositeService;

import com.zjht.unified.common.core.constants.Constants;
import com.zjht.unified.common.core.controller.BaseController;
import com.zjht.unified.common.core.domain.PageDomain;
import com.zjht.unified.common.core.domain.R;
import com.zjht.unified.common.core.domain.TableDataInfo;
import com.zjht.unified.common.core.domain.dto.BaseQueryDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
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
@Api(value = "项目与组件统一模型(uiComponent)维护",tags = {"项目与组件统一模型(uiComponent)维护"})
@RestController
@RequestMapping("/uiComponent-composite")
public class UiComponentCompositeController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(UiComponentController.class);
    @Autowired
    private IUiComponentService uiComponentService;
    @Autowired
    private IUiComponentCompositeService uiComponentCompositeService;

    /**
     * 查询项目与组件统一模型(uiComponent)列表, 对象形式
     */
    @ApiOperation(value = "查询项目与组件统一模型(uiComponent)列表")
    @PostMapping("/list-ext")
    public TableDataInfo<UiComponentCompositeVO> listExt(@RequestBody BaseQueryDTO<UiComponent> uiComponent)
    {
        return list(uiComponent.getCondition(),new PageDomain(uiComponent.getPage(),uiComponent.getSize()));
    }

    /**
     * 查询项目与组件统一模型(uiComponent)列表
     */
    @ApiOperation(value = "查询项目与组件统一模型(uiComponent)列表")
    @GetMapping("/list")
    public TableDataInfo<UiComponentCompositeVO> list(UiComponent uiComponent, PageDomain  pageDomain)
    {
        TableDataInfo<UiComponentCompositeVO> dataInfo = new TableDataInfo();
        Page<UiComponent> page = new Page<>(pageDomain.getPageNum(),pageDomain.getPageSize());
        IPage<UiComponent> list = uiComponentService.page(page, Wrappers.<UiComponent>lambdaQuery(uiComponent).orderByDesc(BaseEntity::getCreateTime));
        List<UiComponentCompositeDTO> compositeLst=list.getRecords().stream()
                .map(t->uiComponentCompositeService.selectById(t.getId()))
                .collect(Collectors.toList());
        List<UiComponentCompositeVO> rows = UiComponentCompositeWrapper.build().entityVOList(compositeLst);
        dataInfo.setCode(Constants.SUCCESS);
        dataInfo.setData(rows);
        dataInfo.setMsg("查询成功");
        dataInfo.setTotal(list.getTotal());
        return dataInfo;
    }

    /**
     * 获取项目与组件统一模型(uiComponent)Composite详细信息
     */
    @ApiOperation(value = "获取项目与组件统一模型(uiComponent)Composite详细信息")
    @GetMapping(value = "/{id}")
    public R<UiComponentCompositeVO> getInfo(@PathVariable("id") Long id)
    {
        UiComponentCompositeDTO uiComponent = uiComponentCompositeService.selectById(id);
        return R.ok(UiComponentCompositeWrapper.build().entityVO(uiComponent));
    }


    /**
     * 新增项目与组件统一模型(uiComponent)
     */
    @ApiOperation(value = "新增项目与组件统一模型(uiComponent)Composite")
    @PostMapping
    public R<Long> add(@RequestBody UiComponentCompositeDTO uiComponent)
    {
        uiComponent.setId(null);
        uiComponent.setCreateTime(DateUtil.now());
        Long id = uiComponentCompositeService.submit(uiComponent);
        R r = id!=null ? R.ok(id) : R.fail();
        return r;
    }

    /**
     * 修改项目与组件统一模型(uiComponent)
     */
    @ApiOperation(value = "修改项目与组件统一模型(uiComponent)Composite")
    @PostMapping("/edit")
    public R<Long> edit(@RequestBody UiComponentCompositeDTO uiComponent)
    {
        uiComponent.setUpdateTime(DateUtil.now());
        Long id = uiComponentCompositeService.submit(uiComponent);
        R r = id!=null ? R.ok(id) : R.fail();
        return r;
    }

    /**
     * 删除项目与组件统一模型(uiComponent)Composite
     */
    @ApiOperation(value = "删除项目与组件统一模型(uiComponent)Composite")
    @PostMapping("/delete/{id}")
    public R<Long> remove(@PathVariable Long id)
    {
        uiComponentCompositeService.removeById(id);
        return R.ok(id);
    }

}