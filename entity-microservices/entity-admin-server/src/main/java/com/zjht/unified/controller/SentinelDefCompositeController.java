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
import com.zjht.unified.vo.SentinelDefVo;
import com.zjht.unified.wrapper.SentinelDefWrapper;
import com.zjht.unified.entity.SentinelDef;
import com.zjht.unified.service.ISentinelDefService;

import com.zjht.unified.vo.SentinelDefCompositeVO;
import com.zjht.unified.dto.SentinelDefCompositeDTO;
import com.zjht.unified.wrapper.SentinelDefCompositeWrapper;
import com.zjht.unified.service.ISentinelDefCompositeService;

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
@Api(value = "哨兵定义(sentinelDef)维护",tags = {"哨兵定义(sentinelDef)维护"})
@RestController
@RequestMapping("/sentinelDef-composite")
public class SentinelDefCompositeController extends BaseController{

    private static final Logger logger = LoggerFactory.getLogger(SentinelDefController.class);
    @Autowired
    private ISentinelDefService sentinelDefService;
    @Autowired
    private ISentinelDefCompositeService sentinelDefCompositeService;

    /**
     * 查询哨兵定义(sentinelDef)列表, 对象形式
     */
    @ApiOperation(value = "查询哨兵定义(sentinelDef)列表")
    @PostMapping("/list-ext")
    public TableDataInfo<SentinelDefCompositeVO> listExt(@RequestBody BaseQueryDTO<SentinelDef> sentinelDef)
    {
        return list(sentinelDef.getCondition(),new PageDomain(sentinelDef.getPage(),sentinelDef.getSize()));
    }

    /**
     * 查询哨兵定义(sentinelDef)列表
     */
    @ApiOperation(value = "查询哨兵定义(sentinelDef)列表")
    @GetMapping("/list")
    public TableDataInfo<SentinelDefCompositeVO> list(SentinelDef sentinelDef, PageDomain  pageDomain)
    {
        TableDataInfo<SentinelDefCompositeVO> dataInfo = new TableDataInfo();
        Page<SentinelDef> page = new Page<>(pageDomain.getPageNum(),pageDomain.getPageSize());
        IPage<SentinelDef> list = sentinelDefService.page(page, Wrappers.<SentinelDef>lambdaQuery(sentinelDef).orderByDesc(BaseEntity::getCreateTime));
        List<SentinelDefCompositeDTO> compositeLst=list.getRecords().stream()
                .map(t->sentinelDefCompositeService.selectById(t.getId()))
                .collect(Collectors.toList());
        List<SentinelDefCompositeVO> rows = SentinelDefCompositeWrapper.build().entityVOList(compositeLst);
        dataInfo.setCode(Constants.SUCCESS);
        dataInfo.setData(rows);
        dataInfo.setMsg("查询成功");
        dataInfo.setTotal(list.getTotal());
        return dataInfo;
    }

    /**
     * 获取哨兵定义(sentinelDef)Composite详细信息
     */
    @ApiOperation(value = "获取哨兵定义(sentinelDef)Composite详细信息")
    @GetMapping(value = "/{id}")
    public R<SentinelDefCompositeVO> getInfo(@PathVariable("id") Long id)
    {
        SentinelDefCompositeDTO sentinelDef = sentinelDefCompositeService.selectById(id);
        return R.ok(SentinelDefCompositeWrapper.build().entityVO(sentinelDef));
    }


    /**
     * 新增哨兵定义(sentinelDef)
     */
    @ApiOperation(value = "新增哨兵定义(sentinelDef)Composite")
    @PostMapping
    public R<Long> add(@RequestBody SentinelDefCompositeDTO sentinelDef)
    {
        sentinelDef.setId(null);
        sentinelDef.setCreateTime(DateUtil.now());
        Long id = sentinelDefCompositeService.submit(sentinelDef);
        R r = id!=null ? R.ok(id) : R.fail();
        return r;
    }

    /**
     * 修改哨兵定义(sentinelDef)
     */
    @ApiOperation(value = "修改哨兵定义(sentinelDef)Composite")
    @PostMapping("/edit")
    public R<Long> edit(@RequestBody SentinelDefCompositeDTO sentinelDef)
    {
        sentinelDef.setUpdateTime(DateUtil.now());
        Long id = sentinelDefCompositeService.submit(sentinelDef);
        R r = id!=null ? R.ok(id) : R.fail();
        return r;
    }

    /**
     * 删除哨兵定义(sentinelDef)Composite
     */
    @ApiOperation(value = "删除哨兵定义(sentinelDef)Composite")
    @PostMapping("/delete/{id}")
    public R<Long> remove(@PathVariable Long id)
    {
        sentinelDefCompositeService.removeById(id);
        return R.ok(id);
    }

}