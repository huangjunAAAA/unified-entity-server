package com.zjht.ui.controller ;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wukong.core.weblog.utils.DateUtil;
import com.zjht.unified.common.core.constants.Constants;
import com.zjht.unified.common.core.constants.DeleteConstants;
import com.zjht.unified.common.core.controller.BaseController;
import com.zjht.unified.common.core.domain.PageDomain;
import com.zjht.unified.common.core.domain.R;
import com.zjht.unified.common.core.domain.TableDataInfo;
import com.zjht.unified.common.core.domain.dto.BaseQueryDTO;
import com.wukong.core.mp.base.BaseEntity;
import com.zjht.ui.vo.UiEventHandleVo;
import com.zjht.ui.wrapper.UiEventHandleWrapper;
import com.zjht.ui.entity.UiEventHandle;
import com.zjht.ui.service.IUiEventHandleService;

import com.zjht.ui.vo.UiEventHandleCompositeVO;
import com.zjht.ui.dto.UiEventHandleCompositeDTO;
import com.zjht.ui.wrapper.UiEventHandleCompositeWrapper;
import com.zjht.ui.service.IUiEventHandleCompositeService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 *  控制器
 *
 * @author wangy
 */
@Api(value = "项目与组件统一模型(uiEventHandle)维护",tags = {"项目与组件统一模型(uiEventHandle)维护"})
@RestController
@RequestMapping("/uiEventHandle-composite")
public class UiEventHandleCompositeController extends BaseController{

    private static final Logger logger = LoggerFactory.getLogger(UiEventHandleController.class);
    @Autowired
    private IUiEventHandleService uiEventHandleService;
    @Autowired
    private IUiEventHandleCompositeService uiEventHandleCompositeService;

    /**
     * 查询项目与组件统一模型(uiEventHandle)列表, 对象形式
     */
    @ApiOperation(value = "查询项目与组件统一模型(uiEventHandle)列表")
    @PostMapping("/list-ext")
    public TableDataInfo<UiEventHandleCompositeVO> listExt(@RequestBody BaseQueryDTO<UiEventHandle> uiEventHandle)
    {
        return list(uiEventHandle.getCondition(),new PageDomain(uiEventHandle.getPage(),uiEventHandle.getSize()));
    }

    /**
     * 查询项目与组件统一模型(uiEventHandle)列表
     */
    @ApiOperation(value = "查询项目与组件统一模型(uiEventHandle)列表")
    @GetMapping("/list")
    public TableDataInfo<UiEventHandleCompositeVO> list(UiEventHandle uiEventHandle, PageDomain  pageDomain)
    {
        TableDataInfo<UiEventHandleCompositeVO> dataInfo = new TableDataInfo();
        Page<UiEventHandle> page = new Page<>(pageDomain.getPageNum(),pageDomain.getPageSize());
        IPage<UiEventHandle> list = uiEventHandleService.page(page, Wrappers.<UiEventHandle>lambdaQuery(uiEventHandle).orderByDesc(BaseEntity::getCreateTime));
        List<UiEventHandleCompositeDTO> compositeLst=list.getRecords().stream()
                .map(t->uiEventHandleCompositeService.selectById(t.getId()))
                .collect(Collectors.toList());
        List<UiEventHandleCompositeVO> rows = UiEventHandleCompositeWrapper.build().entityVOList(compositeLst);
        dataInfo.setCode(Constants.SUCCESS);
        dataInfo.setData(rows);
        dataInfo.setMsg("查询成功");
        dataInfo.setTotal(list.getTotal());
        return dataInfo;
    }

    /**
     * 获取项目与组件统一模型(uiEventHandle)Composite详细信息
     */
    @ApiOperation(value = "获取项目与组件统一模型(uiEventHandle)Composite详细信息")
    @GetMapping(value = "/{id}")
    public R<UiEventHandleCompositeVO> getInfo(@PathVariable("id") Long id)
    {
        UiEventHandleCompositeDTO uiEventHandle = uiEventHandleCompositeService.selectById(id);
        return R.ok(UiEventHandleCompositeWrapper.build().entityVO(uiEventHandle));
    }


    /**
     * 新增项目与组件统一模型(uiEventHandle)
     */
    @ApiOperation(value = "新增项目与组件统一模型(uiEventHandle)Composite")
    @PostMapping
    public R<Long> add(@RequestBody UiEventHandleCompositeDTO uiEventHandle)
    {
        uiEventHandle.setId(null);
        uiEventHandle.setCreateTime(DateUtil.now());
        Long id = uiEventHandleCompositeService.submit(uiEventHandle);
        R r = id!=null ? R.ok(id) : R.fail();
        return r;
    }

    /**
     * 修改项目与组件统一模型(uiEventHandle)
     */
    @ApiOperation(value = "修改项目与组件统一模型(uiEventHandle)Composite")
    @PostMapping("/edit")
    public R<Long> edit(@RequestBody UiEventHandleCompositeDTO uiEventHandle)
    {
        uiEventHandle.setUpdateTime(DateUtil.now());
        Long id = uiEventHandleCompositeService.submit(uiEventHandle);
        R r = id!=null ? R.ok(id) : R.fail();
        return r;
    }

    /**
     * 删除项目与组件统一模型(uiEventHandle)Composite
     */
    @ApiOperation(value = "删除项目与组件统一模型(uiEventHandle)Composite")
    @PostMapping("/delete/{id}")
    public R<Long> remove(@PathVariable Long id)
    {
        uiEventHandleCompositeService.removeById(id);
        return R.ok(id);
    }

}