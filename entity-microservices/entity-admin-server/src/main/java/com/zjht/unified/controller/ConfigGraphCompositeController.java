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
import com.zjht.unified.vo.ConfigGraphVo;
import com.zjht.unified.wrapper.ConfigGraphWrapper;
import com.zjht.unified.entity.ConfigGraph;
import com.zjht.unified.service.IConfigGraphService;

import com.zjht.unified.vo.ConfigGraphCompositeVO;
import com.zjht.unified.dto.ConfigGraphCompositeDTO;
import com.zjht.unified.wrapper.ConfigGraphCompositeWrapper;
import com.zjht.unified.service.IConfigGraphCompositeService;

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
@Api(value = "整图结构关系(configGraph)维护",tags = {"整图结构关系(configGraph)维护"})
@RestController
@RequestMapping("/configGraph-composite")
public class ConfigGraphCompositeController extends BaseController{

    private static final Logger logger = LoggerFactory.getLogger(ConfigGraphController.class);
    @Autowired
    private IConfigGraphService configGraphService;
    @Autowired
    private IConfigGraphCompositeService configGraphCompositeService;

    /**
     * 查询整图结构关系(configGraph)列表, 对象形式
     */
    @ApiOperation(value = "查询整图结构关系(configGraph)列表")
    @PostMapping("/list-ext")
    public TableDataInfo<ConfigGraphCompositeVO> listExt(@RequestBody BaseQueryDTO<ConfigGraph> configGraph)
    {
        return list(configGraph.getCondition(),new PageDomain(configGraph.getPage(),configGraph.getSize()));
    }

    /**
     * 查询整图结构关系(configGraph)列表
     */
    @ApiOperation(value = "查询整图结构关系(configGraph)列表")
    @GetMapping("/list")
    public TableDataInfo<ConfigGraphCompositeVO> list(ConfigGraph configGraph, PageDomain  pageDomain)
    {
        TableDataInfo<ConfigGraphCompositeVO> dataInfo = new TableDataInfo();
        Page<ConfigGraph> page = new Page<>(pageDomain.getPageNum(),pageDomain.getPageSize());
        IPage<ConfigGraph> list = configGraphService.page(page, Wrappers.<ConfigGraph>lambdaQuery(configGraph).orderByDesc(BaseEntity::getCreateTime));
        List<ConfigGraphCompositeDTO> compositeLst=list.getRecords().stream()
                .map(t->configGraphCompositeService.selectById(t.getId()))
                .collect(Collectors.toList());
        List<ConfigGraphCompositeVO> rows = ConfigGraphCompositeWrapper.build().entityVOList(compositeLst);
        dataInfo.setCode(Constants.SUCCESS);
        dataInfo.setData(rows);
        dataInfo.setMsg("查询成功");
        dataInfo.setTotal(list.getTotal());
        return dataInfo;
    }

    /**
     * 获取整图结构关系(configGraph)Composite详细信息
     */
    @ApiOperation(value = "获取整图结构关系(configGraph)Composite详细信息")
    @GetMapping(value = "/{id}")
    public R<ConfigGraphCompositeVO> getInfo(@PathVariable("id") Long id)
    {
        ConfigGraphCompositeDTO configGraph = configGraphCompositeService.selectById(id);
        return R.ok(ConfigGraphCompositeWrapper.build().entityVO(configGraph));
    }


    /**
     * 新增整图结构关系(configGraph)
     */
    @ApiOperation(value = "新增整图结构关系(configGraph)Composite")
    @PostMapping
    public R<Long> add(@RequestBody ConfigGraphCompositeDTO configGraph)
    {
        configGraph.setId(null);
        configGraph.setCreateTime(DateUtil.now());
        Long id = configGraphCompositeService.submit(configGraph);
        R r = id!=null ? R.ok(id) : R.fail();
        return r;
    }

    /**
     * 修改整图结构关系(configGraph)
     */
    @ApiOperation(value = "修改整图结构关系(configGraph)Composite")
    @PostMapping("/edit")
    public R<Long> edit(@RequestBody ConfigGraphCompositeDTO configGraph)
    {
        configGraph.setUpdateTime(DateUtil.now());
        Long id = configGraphCompositeService.submit(configGraph);
        R r = id!=null ? R.ok(id) : R.fail();
        return r;
    }

    /**
     * 删除整图结构关系(configGraph)Composite
     */
    @ApiOperation(value = "删除整图结构关系(configGraph)Composite")
    @PostMapping("/delete/{id}")
    public R<Long> remove(@PathVariable Long id)
    {
        configGraphCompositeService.removeById(id);
        return R.ok(id);
    }

}