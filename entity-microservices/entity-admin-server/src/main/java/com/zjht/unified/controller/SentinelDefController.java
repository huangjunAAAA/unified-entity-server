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
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Arrays;

/**
 *  控制器
 *
 * @author wangy
 */
@Api(value = "哨兵定义维护",tags = {"哨兵定义维护"})
@RestController
@RequestMapping("/sentinelDef")
public class SentinelDefController extends BaseController{

	private static final Logger logger = LoggerFactory.getLogger(SentinelDefController.class);
	@Autowired
    private ISentinelDefService sentinelDefService;
	
	/**
     * 查询哨兵定义列表, 对象形式
     */
    @ApiOperation(value = "查询哨兵定义列表")
    @PostMapping("/list-ext")
    public TableDataInfo<SentinelDefVo> listExt(@RequestBody BaseQueryDTO<SentinelDef> sentinelDef)
    {
        return list(sentinelDef.getCondition(),new PageDomain(sentinelDef.getPage(),sentinelDef.getSize()));
    }
	
	/**
     * 查询哨兵定义列表
     */
    @ApiOperation(value = "查询哨兵定义列表")
    @GetMapping("/list")
    public TableDataInfo<SentinelDefVo> list(SentinelDef sentinelDef, PageDomain  pageDomain)
    {
		TableDataInfo<SentinelDefVo> dataInfo = new TableDataInfo();
        Page<SentinelDef> page = new Page<>(pageDomain.getPageNum(),pageDomain.getPageSize());
        IPage<SentinelDef> list = sentinelDefService.page(page, Wrappers.<SentinelDef>lambdaQuery(sentinelDef).orderByDesc(BaseEntity::getCreateTime));
        IPage<SentinelDefVo> rows = SentinelDefWrapper.build().pageVO(list);
        dataInfo.setCode(Constants.SUCCESS);
        dataInfo.setData(rows.getRecords());
        dataInfo.setMsg("查询成功");
        dataInfo.setTotal(list.getTotal());
        return dataInfo;
    }

    /**
     * 获取哨兵定义详细信息
     */
    @ApiOperation(value = "获取哨兵定义详细信息")
    @GetMapping(value = "/{id}")
    public R<SentinelDefVo> getInfo(@PathVariable("id") Long id)
    {
        SentinelDef sentinelDef = sentinelDefService.getById(id);
        return R.ok(SentinelDefWrapper.build().entityVO(sentinelDef));
    }


    /**
     * 新增哨兵定义
     */
    @ApiOperation(value = "新增哨兵定义")
    @PostMapping
    public R<Long> add(@RequestBody SentinelDef sentinelDef)
    {
        sentinelDef.setCreateTime(DateUtil.now());
        Boolean b = sentinelDefService.save(sentinelDef);
        R r = b ? R.ok(sentinelDef.getId()) : R.fail();
        return r;
    }

    /**
     * 修改哨兵定义
     */
    @ApiOperation(value = "修改哨兵定义")
    @PostMapping("/edit")
    public R<Integer> edit(@RequestBody SentinelDef sentinelDef)
    {
        sentinelDef.setUpdateTime(DateUtil.now());
        Boolean b = sentinelDefService.updateById(sentinelDef);
        R r = b ? R.ok() : R.fail();
        return r;
    }

    /**
     * 删除哨兵定义
     */
    @ApiOperation(value = "删除哨兵定义")
	@PostMapping("/delete/{ids}")
    public R<Integer> remove(@PathVariable Long[] ids)
    {
        Boolean b = sentinelDefService.removeByIds(Arrays.asList(ids));
        R r = b ? R.ok() : R.fail();
        return r;
    }
	
	
	 /**
     * 哨兵定义的动态字典
     */
    @ApiOperation(value = "哨兵定义的字典接口", notes = "传入id获取唯一对应值，或传空获取所有值",hidden = true)
    @PostMapping("/dict")
    public List<SentinelDef> dict(@RequestBody List<Long> ids) {
		List<SentinelDef> data = sentinelDefService.listByIds(ids);
        return data;
	}
	
}