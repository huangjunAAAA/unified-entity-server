package com.zjht.ui.controller ;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wukong.core.weblog.utils.DateUtil;
import com.wukong.core.mp.base.BaseEntity;
import com.zjht.ui.entity.UiLayout;
import com.zjht.ui.service.IUiLayoutService;

import com.zjht.ui.vo.UiLayoutCompositeVO;
import com.zjht.ui.dto.UiLayoutCompositeDTO;
import com.zjht.ui.wrapper.UiLayoutCompositeWrapper;
import com.zjht.ui.service.IUiLayoutCompositeService;

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
@Api(value = "项目与组件统一模型(uiLayout)维护",tags = {"项目与组件统一模型(uiLayout)维护"})
@RestController
@RequestMapping("/uiLayout-composite")
public class UiLayoutCompositeController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(UiLayoutController.class);
    @Autowired
    private IUiLayoutService uiLayoutService;
    @Autowired
    private IUiLayoutCompositeService uiLayoutCompositeService;

    /**
     * 查询项目与组件统一模型(uiLayout)列表, 对象形式
     */
    @ApiOperation(value = "查询项目与组件统一模型(uiLayout)列表")
    @PostMapping("/list-ext")
    public TableDataInfo<UiLayoutCompositeVO> listExt(@RequestBody BaseQueryDTO<UiLayout> uiLayout)
    {
        return list(uiLayout.getCondition(),new PageDomain(uiLayout.getPage(),uiLayout.getSize()));
    }

    /**
     * 查询项目与组件统一模型(uiLayout)列表
     */
    @ApiOperation(value = "查询项目与组件统一模型(uiLayout)列表")
    @GetMapping("/list")
    public TableDataInfo<UiLayoutCompositeVO> list(UiLayout uiLayout, PageDomain  pageDomain)
    {
        TableDataInfo<UiLayoutCompositeVO> dataInfo = new TableDataInfo();
        Page<UiLayout> page = new Page<>(pageDomain.getPageNum(),pageDomain.getPageSize());
        IPage<UiLayout> list = uiLayoutService.page(page, Wrappers.<UiLayout>lambdaQuery(uiLayout).orderByDesc(BaseEntity::getCreateTime));
        List<UiLayoutCompositeDTO> compositeLst=list.getRecords().stream()
                .map(t->uiLayoutCompositeService.selectById(t.getId()))
                .collect(Collectors.toList());
        List<UiLayoutCompositeVO> rows = UiLayoutCompositeWrapper.build().entityVOList(compositeLst);
        dataInfo.setCode(Constants.SUCCESS);
        dataInfo.setData(rows);
        dataInfo.setMsg("查询成功");
        dataInfo.setTotal(list.getTotal());
        return dataInfo;
    }

    /**
     * 获取项目与组件统一模型(uiLayout)Composite详细信息
     */
    @ApiOperation(value = "获取项目与组件统一模型(uiLayout)Composite详细信息")
    @GetMapping(value = "/{id}")
    public R<UiLayoutCompositeVO> getInfo(@PathVariable("id") Long id)
    {
        UiLayoutCompositeDTO uiLayout = uiLayoutCompositeService.selectById(id);
        return R.ok(UiLayoutCompositeWrapper.build().entityVO(uiLayout));
    }


    /**
     * 新增项目与组件统一模型(uiLayout)
     */
    @ApiOperation(value = "新增项目与组件统一模型(uiLayout)Composite")
    @PostMapping
    public R<Long> add(@RequestBody UiLayoutCompositeDTO uiLayout)
    {
        uiLayout.setId(null);
        uiLayout.setCreateTime(DateUtil.now());
        Long id = uiLayoutCompositeService.submit(uiLayout);
        R r = id!=null ? R.ok(id) : R.fail();
        return r;
    }

    /**
     * 修改项目与组件统一模型(uiLayout)
     */
    @ApiOperation(value = "修改项目与组件统一模型(uiLayout)Composite")
    @PostMapping("/edit")
    public R<Long> edit(@RequestBody UiLayoutCompositeDTO uiLayout)
    {
        uiLayout.setUpdateTime(DateUtil.now());
        Long id = uiLayoutCompositeService.submit(uiLayout);
        R r = id!=null ? R.ok(id) : R.fail();
        return r;
    }

    /**
     * 删除项目与组件统一模型(uiLayout)Composite
     */
    @ApiOperation(value = "删除项目与组件统一模型(uiLayout)Composite")
    @PostMapping("/delete/{id}")
    public R<Long> remove(@PathVariable Long id)
    {
        uiLayoutCompositeService.removeById(id);
        return R.ok(id);
    }

}