package com.zjht.unified.controller ;

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
import com.zjht.unified.vo.ViewDefVo;
import com.zjht.unified.wrapper.ViewDefWrapper;
import com.zjht.unified.entity.ViewDef;
import com.zjht.unified.service.IViewDefService;

import com.zjht.unified.vo.ViewDefCompositeVO;
import com.zjht.unified.dto.ViewDefCompositeDTO;
import com.zjht.unified.wrapper.ViewDefCompositeWrapper;
import com.zjht.unified.service.IViewDefCompositeService;

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
@Api(value = "整图结构关系(viewDef)维护",tags = {"整图结构关系(viewDef)维护"})
@RestController
@RequestMapping("/viewDef-composite")
public class ViewDefCompositeController extends BaseController{

    private static final Logger logger = LoggerFactory.getLogger(ViewDefController.class);
    @Autowired
    private IViewDefService viewDefService;
    @Autowired
    private IViewDefCompositeService viewDefCompositeService;

    /**
     * 查询整图结构关系(viewDef)列表, 对象形式
     */
    @ApiOperation(value = "查询整图结构关系(viewDef)列表")
    @PostMapping("/list-ext")
    public TableDataInfo<ViewDefCompositeVO> listExt(@RequestBody BaseQueryDTO<ViewDef> viewDef)
    {
        return list(viewDef.getCondition(),new PageDomain(viewDef.getPage(),viewDef.getSize()));
    }

    /**
     * 查询整图结构关系(viewDef)列表
     */
    @ApiOperation(value = "查询整图结构关系(viewDef)列表")
    @GetMapping("/list")
    public TableDataInfo<ViewDefCompositeVO> list(ViewDef viewDef, PageDomain  pageDomain)
    {
        TableDataInfo<ViewDefCompositeVO> dataInfo = new TableDataInfo();
        Page<ViewDef> page = new Page<>(pageDomain.getPageNum(),pageDomain.getPageSize());
        IPage<ViewDef> list = viewDefService.page(page, Wrappers.<ViewDef>lambdaQuery(viewDef).orderByDesc(BaseEntity::getCreateTime));
        List<ViewDefCompositeDTO> compositeLst=list.getRecords().stream()
                .map(t->viewDefCompositeService.selectById(t.getId()))
                .collect(Collectors.toList());
        List<ViewDefCompositeVO> rows = ViewDefCompositeWrapper.build().entityVOList(compositeLst);
        dataInfo.setCode(Constants.SUCCESS);
        dataInfo.setData(rows);
        dataInfo.setMsg("查询成功");
        dataInfo.setTotal(list.getTotal());
        return dataInfo;
    }

    /**
     * 获取整图结构关系(viewDef)Composite详细信息
     */
    @ApiOperation(value = "获取整图结构关系(viewDef)Composite详细信息")
    @GetMapping(value = "/{id}")
    public R<ViewDefCompositeVO> getInfo(@PathVariable("id") Long id)
    {
        ViewDefCompositeDTO viewDef = viewDefCompositeService.selectById(id);
        return R.ok(ViewDefCompositeWrapper.build().entityVO(viewDef));
    }


    /**
     * 新增整图结构关系(viewDef)
     */
    @ApiOperation(value = "新增整图结构关系(viewDef)Composite")
    @PostMapping
    public R<Long> add(@RequestBody ViewDefCompositeDTO viewDef)
    {
        viewDef.setId(null);
        viewDef.setCreateTime(DateUtil.now());
        Long id = viewDefCompositeService.submit(viewDef);
        R r = id!=null ? R.ok(id) : R.fail();
        return r;
    }

    /**
     * 修改整图结构关系(viewDef)
     */
    @ApiOperation(value = "修改整图结构关系(viewDef)Composite")
    @PostMapping("/edit")
    public R<Long> edit(@RequestBody ViewDefCompositeDTO viewDef)
    {
        viewDef.setUpdateTime(DateUtil.now());
        Long id = viewDefCompositeService.submit(viewDef);
        R r = id!=null ? R.ok(id) : R.fail();
        return r;
    }

    /**
     * 删除整图结构关系(viewDef)Composite
     */
    @ApiOperation(value = "删除整图结构关系(viewDef)Composite")
    @PostMapping("/delete/{id}")
    public R<Long> remove(@PathVariable Long id)
    {
        viewDefCompositeService.removeById(id);
        return R.ok(id);
    }

}