package com.zjht.ui.controller ;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wukong.core.weblog.utils.DateUtil;
import com.wukong.core.mp.base.BaseEntity;
import com.zjht.ui.entity.Fileset;
import com.zjht.ui.entity.UiPrj;
import com.zjht.ui.service.IFilesetService;
import com.zjht.ui.service.IUiPrjService;

import com.zjht.ui.vo.UiPrjCompositeVO;
import com.zjht.ui.dto.UiPrjCompositeDTO;
import com.zjht.ui.wrapper.UiPrjCompositeWrapper;
import com.zjht.ui.service.IUiPrjCompositeService;

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
@Api(value = "项目与组件统一模型(uiPrj)维护",tags = {"项目与组件统一模型(uiPrj)维护"})
@RestController
@RequestMapping("/uiPrj-composite")
public class UiPrjCompositeController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(UiPrjController.class);
    @Autowired
    private IUiPrjService uiPrjService;
    @Autowired
    private IUiPrjCompositeService uiPrjCompositeService;
    @Autowired
    private IFilesetService filesetService;

    /**
     * 查询项目与组件统一模型(uiPrj)列表, 对象形式
     */
    @ApiOperation(value = "查询项目与组件统一模型(uiPrj)列表")
    @PostMapping("/list-ext")
    public TableDataInfo<UiPrjCompositeVO> listExt(@RequestBody BaseQueryDTO<UiPrj> uiPrj)
    {
        return list(uiPrj.getCondition(),new PageDomain(uiPrj.getPage(),uiPrj.getSize()));
    }

    /**
     * 查询项目与组件统一模型(uiPrj)列表
     */
    @ApiOperation(value = "查询项目与组件统一模型(uiPrj)列表")
    @GetMapping("/list")
    public TableDataInfo<UiPrjCompositeVO> list(UiPrj uiPrj, PageDomain  pageDomain)
    {
        TableDataInfo<UiPrjCompositeVO> dataInfo = new TableDataInfo();
        Page<UiPrj> page = new Page<>(pageDomain.getPageNum(),pageDomain.getPageSize());
        IPage<UiPrj> list = uiPrjService.page(page, Wrappers.<UiPrj>lambdaQuery(uiPrj).orderByDesc(BaseEntity::getCreateTime));
        List<UiPrjCompositeDTO> compositeLst=list.getRecords().stream()
                .map(t->uiPrjCompositeService.selectById(t.getId()))
                .collect(Collectors.toList());
        List<UiPrjCompositeVO> rows = UiPrjCompositeWrapper.build().entityVOList(compositeLst);
        dataInfo.setCode(Constants.SUCCESS);
        dataInfo.setData(rows);
        dataInfo.setMsg("查询成功");
        dataInfo.setTotal(list.getTotal());
        return dataInfo;
    }

    /**
     * 获取项目与组件统一模型(uiPrj)Composite详细信息
     */
    @ApiOperation(value = "获取项目与组件统一模型(uiPrj)Composite详细信息")
    @GetMapping(value = "/{id}")
    public R<UiPrjCompositeVO> getInfo(@PathVariable("id") Long id)
    {
        UiPrjCompositeDTO uiPrj = uiPrjCompositeService.selectById(id);
        return R.ok(UiPrjCompositeWrapper.build().entityVO(uiPrj));
    }


    /**
     * 新增项目与组件统一模型(uiPrj)
     */
    @ApiOperation(value = "新增项目与组件统一模型(uiPrj)Composite")
    @PostMapping
    public R<Long> add(@RequestBody UiPrjCompositeDTO uiPrj)
    {
        uiPrj.setId(null);
        uiPrj.setCreateTime(DateUtil.now());
        Long id = uiPrjCompositeService.submit(uiPrj);
        R r = id!=null ? R.ok(id) : R.fail();
        return r;
    }

    /**
     * 修改项目与组件统一模型(uiPrj)
     */
    @ApiOperation(value = "修改项目与组件统一模型(uiPrj)Composite")
    @PostMapping("/edit")
    public R<Long> edit(@RequestBody UiPrjCompositeDTO uiPrj)
    {
        uiPrj.setUpdateTime(DateUtil.now());
        Long id = uiPrjCompositeService.submit(uiPrj);
        R r = id!=null ? R.ok(id) : R.fail();
        return r;
    }

    /**
     * 删除项目与组件统一模型(uiPrj)Composite
     */
    @ApiOperation(value = "删除项目与组件统一模型(uiPrj)Composite")
    @PostMapping("/delete/{id}")
    public R<Long> remove(@PathVariable Long id)
    {
        uiPrjCompositeService.removeById(id);
        filesetService.remove(Wrappers.<Fileset>lambdaQuery().eq(Fileset::getBelongtoId,id)
                .ne(Fileset::getBelongtoType,Constants.FILE_TYPE_PAGE));
        return R.ok(id);
    }

}