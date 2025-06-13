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
import com.zjht.unified.vo.FsmConditionVo;
import com.zjht.unified.wrapper.FsmConditionWrapper;
import com.zjht.unified.entity.FsmCondition;
import com.zjht.unified.service.IFsmConditionService;

import com.zjht.unified.vo.FsmConditionCompositeVO;
import com.zjht.unified.dto.FsmConditionCompositeDTO;
import com.zjht.unified.wrapper.FsmConditionCompositeWrapper;
import com.zjht.unified.service.IFsmConditionCompositeService;

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
@Api(value = "状态机条件(fsmCondition)维护",tags = {"状态机条件(fsmCondition)维护"})
@RestController
@RequestMapping("/fsmCondition-composite")
public class FsmConditionCompositeController extends BaseController{

    private static final Logger logger = LoggerFactory.getLogger(FsmConditionController.class);
    @Autowired
    private IFsmConditionService fsmConditionService;
    @Autowired
    private IFsmConditionCompositeService fsmConditionCompositeService;

    /**
     * 查询状态机条件(fsmCondition)列表, 对象形式
     */
    @ApiOperation(value = "查询状态机条件(fsmCondition)列表")
    @PostMapping("/list-ext")
    public TableDataInfo<FsmConditionCompositeVO> listExt(@RequestBody BaseQueryDTO<FsmCondition> fsmCondition)
    {
        return list(fsmCondition.getCondition(),new PageDomain(fsmCondition.getPage(),fsmCondition.getSize()));
    }

    /**
     * 查询状态机条件(fsmCondition)列表
     */
    @ApiOperation(value = "查询状态机条件(fsmCondition)列表")
    @GetMapping("/list")
    public TableDataInfo<FsmConditionCompositeVO> list(FsmCondition fsmCondition, PageDomain  pageDomain)
    {
        TableDataInfo<FsmConditionCompositeVO> dataInfo = new TableDataInfo();
        Page<FsmCondition> page = new Page<>(pageDomain.getPageNum(),pageDomain.getPageSize());
        IPage<FsmCondition> list = fsmConditionService.page(page, Wrappers.<FsmCondition>lambdaQuery(fsmCondition).orderByDesc(BaseEntity::getCreateTime));
        List<FsmConditionCompositeDTO> compositeLst=list.getRecords().stream()
                .map(t->fsmConditionCompositeService.selectById(t.getId()))
                .collect(Collectors.toList());
        List<FsmConditionCompositeVO> rows = FsmConditionCompositeWrapper.build().entityVOList(compositeLst);
        dataInfo.setCode(Constants.SUCCESS);
        dataInfo.setData(rows);
        dataInfo.setMsg("查询成功");
        dataInfo.setTotal(list.getTotal());
        return dataInfo;
    }

    /**
     * 获取状态机条件(fsmCondition)Composite详细信息
     */
    @ApiOperation(value = "获取状态机条件(fsmCondition)Composite详细信息")
    @GetMapping(value = "/{id}")
    public R<FsmConditionCompositeVO> getInfo(@PathVariable("id") Long id)
    {
        FsmConditionCompositeDTO fsmCondition = fsmConditionCompositeService.selectById(id);
        return R.ok(FsmConditionCompositeWrapper.build().entityVO(fsmCondition));
    }


    /**
     * 新增状态机条件(fsmCondition)
     */
    @ApiOperation(value = "新增状态机条件(fsmCondition)Composite")
    @PostMapping
    public R<Long> add(@RequestBody FsmConditionCompositeDTO fsmCondition)
    {
        fsmCondition.setId(null);
        fsmCondition.setCreateTime(DateUtil.now());
        Long id = fsmConditionCompositeService.submit(fsmCondition);
        R r = id!=null ? R.ok(id) : R.fail();
        return r;
    }

    /**
     * 修改状态机条件(fsmCondition)
     */
    @ApiOperation(value = "修改状态机条件(fsmCondition)Composite")
    @PostMapping("/edit")
    public R<Long> edit(@RequestBody FsmConditionCompositeDTO fsmCondition)
    {
        fsmCondition.setUpdateTime(DateUtil.now());
        Long id = fsmConditionCompositeService.submit(fsmCondition);
        R r = id!=null ? R.ok(id) : R.fail();
        return r;
    }

    /**
     * 删除状态机条件(fsmCondition)Composite
     */
    @ApiOperation(value = "删除状态机条件(fsmCondition)Composite")
    @PostMapping("/delete/{id}")
    public R<Long> remove(@PathVariable Long id)
    {
        fsmConditionCompositeService.removeById(id);
        return R.ok(id);
    }

}