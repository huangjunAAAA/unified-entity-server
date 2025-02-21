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
import com.zjht.unified.vo.FieldDefVo;
import com.zjht.unified.wrapper.FieldDefWrapper;
import com.zjht.unified.entity.FieldDef;
import com.zjht.unified.service.IFieldDefService;

import com.zjht.unified.vo.FieldDefCompositeVO;
import com.zjht.unified.dto.FieldDefCompositeDTO;
import com.zjht.unified.wrapper.FieldDefCompositeWrapper;
import com.zjht.unified.service.IFieldDefCompositeService;

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
@Api(value = "整图结构关系(fieldDef)维护",tags = {"整图结构关系(fieldDef)维护"})
@RestController
@RequestMapping("/fieldDef-composite")
public class FieldDefCompositeController extends BaseController{

    private static final Logger logger = LoggerFactory.getLogger(FieldDefController.class);
    @Autowired
    private IFieldDefService fieldDefService;
    @Autowired
    private IFieldDefCompositeService fieldDefCompositeService;

    /**
     * 查询整图结构关系(fieldDef)列表, 对象形式
     */
    @ApiOperation(value = "查询整图结构关系(fieldDef)列表")
    @PostMapping("/list-ext")
    public TableDataInfo<FieldDefCompositeVO> listExt(@RequestBody BaseQueryDTO<FieldDef> fieldDef)
    {
        return list(fieldDef.getCondition(),new PageDomain(fieldDef.getPage(),fieldDef.getSize()));
    }

    /**
     * 查询整图结构关系(fieldDef)列表
     */
    @ApiOperation(value = "查询整图结构关系(fieldDef)列表")
    @GetMapping("/list")
    public TableDataInfo<FieldDefCompositeVO> list(FieldDef fieldDef, PageDomain  pageDomain)
    {
        TableDataInfo<FieldDefCompositeVO> dataInfo = new TableDataInfo();
        Page<FieldDef> page = new Page<>(pageDomain.getPageNum(),pageDomain.getPageSize());
        IPage<FieldDef> list = fieldDefService.page(page, Wrappers.<FieldDef>lambdaQuery(fieldDef).orderByDesc(BaseEntity::getCreateTime));
        List<FieldDefCompositeDTO> compositeLst=list.getRecords().stream()
                .map(t->fieldDefCompositeService.selectById(t.getId()))
                .collect(Collectors.toList());
        List<FieldDefCompositeVO> rows = FieldDefCompositeWrapper.build().entityVOList(compositeLst);
        dataInfo.setCode(Constants.SUCCESS);
        dataInfo.setData(rows);
        dataInfo.setMsg("查询成功");
        dataInfo.setTotal(list.getTotal());
        return dataInfo;
    }

    /**
     * 获取整图结构关系(fieldDef)Composite详细信息
     */
    @ApiOperation(value = "获取整图结构关系(fieldDef)Composite详细信息")
    @GetMapping(value = "/{id}")
    public R<FieldDefCompositeVO> getInfo(@PathVariable("id") Long id)
    {
        FieldDefCompositeDTO fieldDef = fieldDefCompositeService.selectById(id);
        return R.ok(FieldDefCompositeWrapper.build().entityVO(fieldDef));
    }


    /**
     * 新增整图结构关系(fieldDef)
     */
    @ApiOperation(value = "新增整图结构关系(fieldDef)Composite")
    @PostMapping
    public R<Long> add(@RequestBody FieldDefCompositeDTO fieldDef)
    {
        fieldDef.setId(null);
        fieldDef.setCreateTime(DateUtil.now());
        Long id = fieldDefCompositeService.submit(fieldDef);
        R r = id!=null ? R.ok(id) : R.fail();
        return r;
    }

    /**
     * 修改整图结构关系(fieldDef)
     */
    @ApiOperation(value = "修改整图结构关系(fieldDef)Composite")
    @PostMapping("/edit")
    public R<Long> edit(@RequestBody FieldDefCompositeDTO fieldDef)
    {
        fieldDef.setUpdateTime(DateUtil.now());
        Long id = fieldDefCompositeService.submit(fieldDef);
        R r = id!=null ? R.ok(id) : R.fail();
        return r;
    }

    /**
     * 删除整图结构关系(fieldDef)Composite
     */
    @ApiOperation(value = "删除整图结构关系(fieldDef)Composite")
    @PostMapping("/delete/{id}")
    public R<Long> remove(@PathVariable Long id)
    {
        fieldDefCompositeService.removeById(id);
        return R.ok(id);
    }

}