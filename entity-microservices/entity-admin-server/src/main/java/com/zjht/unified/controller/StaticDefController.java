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
import com.zjht.unified.vo.StaticDefVo;
import com.zjht.unified.wrapper.StaticDefWrapper;
import com.zjht.unified.entity.StaticDef;
import com.zjht.unified.service.IStaticDefService;
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
@Api(value = "静态变量定义维护",tags = {"静态变量定义维护"})
@RestController
@RequestMapping("/staticDef")
public class StaticDefController extends BaseController{

	private static final Logger logger = LoggerFactory.getLogger(StaticDefController.class);
	@Autowired
    private IStaticDefService staticDefService;
	
	/**
     * 查询静态变量定义列表, 对象形式
     */
    @ApiOperation(value = "查询静态变量定义列表")
    @PostMapping("/list-ext")
    public TableDataInfo<StaticDefVo> listExt(@RequestBody BaseQueryDTO<StaticDef> staticDef)
    {
        return list(staticDef.getCondition(),new PageDomain(staticDef.getPage(),staticDef.getSize()));
    }
	
	/**
     * 查询静态变量定义列表
     */
    @ApiOperation(value = "查询静态变量定义列表")
    @GetMapping("/list")
    public TableDataInfo<StaticDefVo> list(StaticDef staticDef, PageDomain  pageDomain)
    {
		TableDataInfo<StaticDefVo> dataInfo = new TableDataInfo();
        Page<StaticDef> page = new Page<>(pageDomain.getPageNum(),pageDomain.getPageSize());
        IPage<StaticDef> list = staticDefService.page(page, Wrappers.<StaticDef>lambdaQuery(staticDef).orderByDesc(BaseEntity::getCreateTime));
        IPage<StaticDefVo> rows = StaticDefWrapper.build().pageVO(list);
        dataInfo.setCode(Constants.SUCCESS);
        dataInfo.setData(rows.getRecords());
        dataInfo.setMsg("查询成功");
        dataInfo.setTotal(list.getTotal());
        return dataInfo;
    }

    /**
     * 获取静态变量定义详细信息
     */
    @ApiOperation(value = "获取静态变量定义详细信息")
    @GetMapping(value = "/{id}")
    public R<StaticDefVo> getInfo(@PathVariable("id") Long id)
    {
        StaticDef staticDef = staticDefService.getById(id);
        return R.ok(StaticDefWrapper.build().entityVO(staticDef));
    }


    /**
     * 新增静态变量定义
     */
    @ApiOperation(value = "新增静态变量定义")
    @PostMapping
    public R<Long> add(@RequestBody StaticDef staticDef)
    {
        staticDef.setCreateTime(DateUtil.now());
        Boolean b = staticDefService.save(staticDef);
        R r = b ? R.ok(staticDef.getId()) : R.fail();
        return r;
    }

    /**
     * 修改静态变量定义
     */
    @ApiOperation(value = "修改静态变量定义")
    @PostMapping("/edit")
    public R<Integer> edit(@RequestBody StaticDef staticDef)
    {
        staticDef.setUpdateTime(DateUtil.now());
        Boolean b = staticDefService.updateById(staticDef);
        R r = b ? R.ok() : R.fail();
        return r;
    }

    /**
     * 删除静态变量定义
     */
    @ApiOperation(value = "删除静态变量定义")
	@PostMapping("/delete/{ids}")
    public R<Integer> remove(@PathVariable Long[] ids)
    {
        Boolean b = staticDefService.removeByIds(Arrays.asList(ids));
        R r = b ? R.ok() : R.fail();
        return r;
    }
	
	
	 /**
     * 静态变量定义的动态字典
     */
    @ApiOperation(value = "静态变量定义的字典接口", notes = "传入id获取唯一对应值，或传空获取所有值",hidden = true)
    @PostMapping("/dict")
    public List<StaticDef> dict(@RequestBody List<Long> ids) {
		List<StaticDef> data = staticDefService.listByIds(ids);
        return data;
	}
	
}