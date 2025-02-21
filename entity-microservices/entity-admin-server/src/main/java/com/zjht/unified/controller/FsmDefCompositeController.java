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
import com.zjht.unified.vo.FsmDefVo;
import com.zjht.unified.wrapper.FsmDefWrapper;
import com.zjht.unified.entity.FsmDef;
import com.zjht.unified.service.IFsmDefService;

import com.zjht.unified.vo.FsmDefCompositeVO;
import com.zjht.unified.dto.FsmDefCompositeDTO;
import com.zjht.unified.wrapper.FsmDefCompositeWrapper;
import com.zjht.unified.service.IFsmDefCompositeService;

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
@Api(value = "整图结构关系(fsmDef)维护",tags = {"整图结构关系(fsmDef)维护"})
@RestController
@RequestMapping("/fsmDef-composite")
public class FsmDefCompositeController extends BaseController{

    private static final Logger logger = LoggerFactory.getLogger(FsmDefController.class);
    @Autowired
    private IFsmDefService fsmDefService;
    @Autowired
    private IFsmDefCompositeService fsmDefCompositeService;

    /**
     * 查询整图结构关系(fsmDef)列表, 对象形式
     */
    @ApiOperation(value = "查询整图结构关系(fsmDef)列表")
    @PostMapping("/list-ext")
    public TableDataInfo<FsmDefCompositeVO> listExt(@RequestBody BaseQueryDTO<FsmDef> fsmDef)
    {
        return list(fsmDef.getCondition(),new PageDomain(fsmDef.getPage(),fsmDef.getSize()));
    }

    /**
     * 查询整图结构关系(fsmDef)列表
     */
    @ApiOperation(value = "查询整图结构关系(fsmDef)列表")
    @GetMapping("/list")
    public TableDataInfo<FsmDefCompositeVO> list(FsmDef fsmDef, PageDomain  pageDomain)
    {
        TableDataInfo<FsmDefCompositeVO> dataInfo = new TableDataInfo();
        Page<FsmDef> page = new Page<>(pageDomain.getPageNum(),pageDomain.getPageSize());
        IPage<FsmDef> list = fsmDefService.page(page, Wrappers.<FsmDef>lambdaQuery(fsmDef).orderByDesc(BaseEntity::getCreateTime));
        List<FsmDefCompositeDTO> compositeLst=list.getRecords().stream()
                .map(t->fsmDefCompositeService.selectById(t.getId()))
                .collect(Collectors.toList());
        List<FsmDefCompositeVO> rows = FsmDefCompositeWrapper.build().entityVOList(compositeLst);
        dataInfo.setCode(Constants.SUCCESS);
        dataInfo.setData(rows);
        dataInfo.setMsg("查询成功");
        dataInfo.setTotal(list.getTotal());
        return dataInfo;
    }

    /**
     * 获取整图结构关系(fsmDef)Composite详细信息
     */
    @ApiOperation(value = "获取整图结构关系(fsmDef)Composite详细信息")
    @GetMapping(value = "/{id}")
    public R<FsmDefCompositeVO> getInfo(@PathVariable("id") Long id)
    {
        FsmDefCompositeDTO fsmDef = fsmDefCompositeService.selectById(id);
        return R.ok(FsmDefCompositeWrapper.build().entityVO(fsmDef));
    }


    /**
     * 新增整图结构关系(fsmDef)
     */
    @ApiOperation(value = "新增整图结构关系(fsmDef)Composite")
    @PostMapping
    public R<Long> add(@RequestBody FsmDefCompositeDTO fsmDef)
    {
        fsmDef.setId(null);
        fsmDef.setCreateTime(DateUtil.now());
        Long id = fsmDefCompositeService.submit(fsmDef);
        R r = id!=null ? R.ok(id) : R.fail();
        return r;
    }

    /**
     * 修改整图结构关系(fsmDef)
     */
    @ApiOperation(value = "修改整图结构关系(fsmDef)Composite")
    @PostMapping("/edit")
    public R<Long> edit(@RequestBody FsmDefCompositeDTO fsmDef)
    {
        fsmDef.setUpdateTime(DateUtil.now());
        Long id = fsmDefCompositeService.submit(fsmDef);
        R r = id!=null ? R.ok(id) : R.fail();
        return r;
    }

    /**
     * 删除整图结构关系(fsmDef)Composite
     */
    @ApiOperation(value = "删除整图结构关系(fsmDef)Composite")
    @PostMapping("/delete/{id}")
    public R<Long> remove(@PathVariable Long id)
    {
        fsmDefCompositeService.removeById(id);
        return R.ok(id);
    }

}