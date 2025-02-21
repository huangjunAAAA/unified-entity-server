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
@Api(value = "视图定义维护",tags = {"视图定义维护"})
@RestController
@RequestMapping("/viewDef")
public class ViewDefController extends BaseController{

	private static final Logger logger = LoggerFactory.getLogger(ViewDefController.class);
	@Autowired
    private IViewDefService viewDefService;
	
	/**
     * 查询视图定义列表, 对象形式
     */
    @ApiOperation(value = "查询视图定义列表")
    @PostMapping("/list-ext")
    public TableDataInfo<ViewDefVo> listExt(@RequestBody BaseQueryDTO<ViewDef> viewDef)
    {
        return list(viewDef.getCondition(),new PageDomain(viewDef.getPage(),viewDef.getSize()));
    }
	
	/**
     * 查询视图定义列表
     */
    @ApiOperation(value = "查询视图定义列表")
    @GetMapping("/list")
    public TableDataInfo<ViewDefVo> list(ViewDef viewDef, PageDomain  pageDomain)
    {
		TableDataInfo<ViewDefVo> dataInfo = new TableDataInfo();
        Page<ViewDef> page = new Page<>(pageDomain.getPageNum(),pageDomain.getPageSize());
        IPage<ViewDef> list = viewDefService.page(page, Wrappers.<ViewDef>lambdaQuery(viewDef).orderByDesc(BaseEntity::getCreateTime));
        IPage<ViewDefVo> rows = ViewDefWrapper.build().pageVO(list);
        dataInfo.setCode(Constants.SUCCESS);
        dataInfo.setData(rows.getRecords());
        dataInfo.setMsg("查询成功");
        dataInfo.setTotal(list.getTotal());
        return dataInfo;
    }

    /**
     * 获取视图定义详细信息
     */
    @ApiOperation(value = "获取视图定义详细信息")
    @GetMapping(value = "/{id}")
    public R<ViewDefVo> getInfo(@PathVariable("id") Long id)
    {
        ViewDef viewDef = viewDefService.getById(id);
        return R.ok(ViewDefWrapper.build().entityVO(viewDef));
    }


    /**
     * 新增视图定义
     */
    @ApiOperation(value = "新增视图定义")
    @PostMapping
    public R<Long> add(@RequestBody ViewDef viewDef)
    {
        viewDef.setCreateTime(DateUtil.now());
        Boolean b = viewDefService.save(viewDef);
        R r = b ? R.ok(viewDef.getId()) : R.fail();
        return r;
    }

    /**
     * 修改视图定义
     */
    @ApiOperation(value = "修改视图定义")
    @PostMapping("/edit")
    public R<Integer> edit(@RequestBody ViewDef viewDef)
    {
        viewDef.setUpdateTime(DateUtil.now());
        Boolean b = viewDefService.updateById(viewDef);
        R r = b ? R.ok() : R.fail();
        return r;
    }

    /**
     * 删除视图定义
     */
    @ApiOperation(value = "删除视图定义")
	@PostMapping("/delete/{ids}")
    public R<Integer> remove(@PathVariable Long[] ids)
    {
        Boolean b = viewDefService.removeByIds(Arrays.asList(ids));
        R r = b ? R.ok() : R.fail();
        return r;
    }
	
	
	 /**
     * 视图定义的动态字典
     */
    @ApiOperation(value = "视图定义的字典接口", notes = "传入id获取唯一对应值，或传空获取所有值",hidden = true)
    @PostMapping("/dict")
    public List<ViewDef> dict(@RequestBody List<Long> ids) {
		List<ViewDef> data = viewDefService.listByIds(ids);
        return data;
	}
	
}