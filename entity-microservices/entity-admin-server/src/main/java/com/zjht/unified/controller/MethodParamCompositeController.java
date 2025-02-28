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
import com.zjht.unified.vo.MethodParamVo;
import com.zjht.unified.wrapper.MethodParamWrapper;
import com.zjht.unified.entity.MethodParam;
import com.zjht.unified.service.IMethodParamService;

import com.zjht.unified.vo.MethodParamCompositeVO;
import com.zjht.unified.dto.MethodParamCompositeDTO;
import com.zjht.unified.wrapper.MethodParamCompositeWrapper;
import com.zjht.unified.service.IMethodParamCompositeService;

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
@Api(value = "整图结构关系(methodParam)维护",tags = {"整图结构关系(methodParam)维护"})
@RestController
@RequestMapping("/methodParam-composite")
public class MethodParamCompositeController extends BaseController{

    private static final Logger logger = LoggerFactory.getLogger(MethodParamController.class);
    @Autowired
    private IMethodParamService methodParamService;
    @Autowired
    private IMethodParamCompositeService methodParamCompositeService;

    /**
     * 查询整图结构关系(methodParam)列表, 对象形式
     */
    @ApiOperation(value = "查询整图结构关系(methodParam)列表")
    @PostMapping("/list-ext")
    public TableDataInfo<MethodParamCompositeVO> listExt(@RequestBody BaseQueryDTO<MethodParam> methodParam)
    {
        return list(methodParam.getCondition(),new PageDomain(methodParam.getPage(),methodParam.getSize()));
    }

    /**
     * 查询整图结构关系(methodParam)列表
     */
    @ApiOperation(value = "查询整图结构关系(methodParam)列表")
    @GetMapping("/list")
    public TableDataInfo<MethodParamCompositeVO> list(MethodParam methodParam, PageDomain  pageDomain)
    {
        TableDataInfo<MethodParamCompositeVO> dataInfo = new TableDataInfo();
        Page<MethodParam> page = new Page<>(pageDomain.getPageNum(),pageDomain.getPageSize());
        IPage<MethodParam> list = methodParamService.page(page, Wrappers.<MethodParam>lambdaQuery(methodParam).orderByDesc(BaseEntity::getCreateTime));
        List<MethodParamCompositeDTO> compositeLst=list.getRecords().stream()
                .map(t->methodParamCompositeService.selectById(t.getId()))
                .collect(Collectors.toList());
        List<MethodParamCompositeVO> rows = MethodParamCompositeWrapper.build().entityVOList(compositeLst);
        dataInfo.setCode(Constants.SUCCESS);
        dataInfo.setData(rows);
        dataInfo.setMsg("查询成功");
        dataInfo.setTotal(list.getTotal());
        return dataInfo;
    }

    /**
     * 获取整图结构关系(methodParam)Composite详细信息
     */
    @ApiOperation(value = "获取整图结构关系(methodParam)Composite详细信息")
    @GetMapping(value = "/{id}")
    public R<MethodParamCompositeVO> getInfo(@PathVariable("id") Long id)
    {
        MethodParamCompositeDTO methodParam = methodParamCompositeService.selectById(id);
        return R.ok(MethodParamCompositeWrapper.build().entityVO(methodParam));
    }


    /**
     * 新增整图结构关系(methodParam)
     */
    @ApiOperation(value = "新增整图结构关系(methodParam)Composite")
    @PostMapping
    public R<Long> add(@RequestBody MethodParamCompositeDTO methodParam)
    {
        methodParam.setId(null);
        methodParam.setCreateTime(DateUtil.now());
        Long id = methodParamCompositeService.submit(methodParam);
        R r = id!=null ? R.ok(id) : R.fail();
        return r;
    }

    /**
     * 修改整图结构关系(methodParam)
     */
    @ApiOperation(value = "修改整图结构关系(methodParam)Composite")
    @PostMapping("/edit")
    public R<Long> edit(@RequestBody MethodParamCompositeDTO methodParam)
    {
        methodParam.setUpdateTime(DateUtil.now());
        Long id = methodParamCompositeService.submit(methodParam);
        R r = id!=null ? R.ok(id) : R.fail();
        return r;
    }

    /**
     * 删除整图结构关系(methodParam)Composite
     */
    @ApiOperation(value = "删除整图结构关系(methodParam)Composite")
    @PostMapping("/delete/{id}")
    public R<Long> remove(@PathVariable Long id)
    {
        methodParamCompositeService.removeById(id);
        return R.ok(id);
    }

}