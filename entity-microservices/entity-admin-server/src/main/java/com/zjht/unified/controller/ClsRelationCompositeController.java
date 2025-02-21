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
import com.zjht.unified.vo.ClsRelationVo;
import com.zjht.unified.wrapper.ClsRelationWrapper;
import com.zjht.unified.entity.ClsRelation;
import com.zjht.unified.service.IClsRelationService;

import com.zjht.unified.vo.ClsRelationCompositeVO;
import com.zjht.unified.dto.ClsRelationCompositeDTO;
import com.zjht.unified.wrapper.ClsRelationCompositeWrapper;
import com.zjht.unified.service.IClsRelationCompositeService;

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
@Api(value = "整图结构关系(clsRelation)维护",tags = {"整图结构关系(clsRelation)维护"})
@RestController
@RequestMapping("/clsRelation-composite")
public class ClsRelationCompositeController extends BaseController{

    private static final Logger logger = LoggerFactory.getLogger(ClsRelationController.class);
    @Autowired
    private IClsRelationService clsRelationService;
    @Autowired
    private IClsRelationCompositeService clsRelationCompositeService;

    /**
     * 查询整图结构关系(clsRelation)列表, 对象形式
     */
    @ApiOperation(value = "查询整图结构关系(clsRelation)列表")
    @PostMapping("/list-ext")
    public TableDataInfo<ClsRelationCompositeVO> listExt(@RequestBody BaseQueryDTO<ClsRelation> clsRelation)
    {
        return list(clsRelation.getCondition(),new PageDomain(clsRelation.getPage(),clsRelation.getSize()));
    }

    /**
     * 查询整图结构关系(clsRelation)列表
     */
    @ApiOperation(value = "查询整图结构关系(clsRelation)列表")
    @GetMapping("/list")
    public TableDataInfo<ClsRelationCompositeVO> list(ClsRelation clsRelation, PageDomain  pageDomain)
    {
        TableDataInfo<ClsRelationCompositeVO> dataInfo = new TableDataInfo();
        Page<ClsRelation> page = new Page<>(pageDomain.getPageNum(),pageDomain.getPageSize());
        IPage<ClsRelation> list = clsRelationService.page(page, Wrappers.<ClsRelation>lambdaQuery(clsRelation).orderByDesc(BaseEntity::getCreateTime));
        List<ClsRelationCompositeDTO> compositeLst=list.getRecords().stream()
                .map(t->clsRelationCompositeService.selectById(t.getId()))
                .collect(Collectors.toList());
        List<ClsRelationCompositeVO> rows = ClsRelationCompositeWrapper.build().entityVOList(compositeLst);
        dataInfo.setCode(Constants.SUCCESS);
        dataInfo.setData(rows);
        dataInfo.setMsg("查询成功");
        dataInfo.setTotal(list.getTotal());
        return dataInfo;
    }

    /**
     * 获取整图结构关系(clsRelation)Composite详细信息
     */
    @ApiOperation(value = "获取整图结构关系(clsRelation)Composite详细信息")
    @GetMapping(value = "/{id}")
    public R<ClsRelationCompositeVO> getInfo(@PathVariable("id") Long id)
    {
        ClsRelationCompositeDTO clsRelation = clsRelationCompositeService.selectById(id);
        return R.ok(ClsRelationCompositeWrapper.build().entityVO(clsRelation));
    }


    /**
     * 新增整图结构关系(clsRelation)
     */
    @ApiOperation(value = "新增整图结构关系(clsRelation)Composite")
    @PostMapping
    public R<Long> add(@RequestBody ClsRelationCompositeDTO clsRelation)
    {
        clsRelation.setId(null);
        clsRelation.setCreateTime(DateUtil.now());
        Long id = clsRelationCompositeService.submit(clsRelation);
        R r = id!=null ? R.ok(id) : R.fail();
        return r;
    }

    /**
     * 修改整图结构关系(clsRelation)
     */
    @ApiOperation(value = "修改整图结构关系(clsRelation)Composite")
    @PostMapping("/edit")
    public R<Long> edit(@RequestBody ClsRelationCompositeDTO clsRelation)
    {
        clsRelation.setUpdateTime(DateUtil.now());
        Long id = clsRelationCompositeService.submit(clsRelation);
        R r = id!=null ? R.ok(id) : R.fail();
        return r;
    }

    /**
     * 删除整图结构关系(clsRelation)Composite
     */
    @ApiOperation(value = "删除整图结构关系(clsRelation)Composite")
    @PostMapping("/delete/{id}")
    public R<Long> remove(@PathVariable Long id)
    {
        clsRelationCompositeService.removeById(id);
        return R.ok(id);
    }

}