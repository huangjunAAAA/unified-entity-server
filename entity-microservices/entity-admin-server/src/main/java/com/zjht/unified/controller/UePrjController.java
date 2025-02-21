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
import com.zjht.unified.vo.UePrjVo;
import com.zjht.unified.wrapper.UePrjWrapper;
import com.zjht.unified.entity.UePrj;
import com.zjht.unified.service.IUePrjService;
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
@Api(value = "统一实体项目维护",tags = {"统一实体项目维护"})
@RestController
@RequestMapping("/uePrj")
public class UePrjController extends BaseController{

	private static final Logger logger = LoggerFactory.getLogger(UePrjController.class);
	@Autowired
    private IUePrjService uePrjService;
	
	/**
     * 查询统一实体项目列表, 对象形式
     */
    @ApiOperation(value = "查询统一实体项目列表")
    @PostMapping("/list-ext")
    public TableDataInfo<UePrjVo> listExt(@RequestBody BaseQueryDTO<UePrj> uePrj)
    {
        return list(uePrj.getCondition(),new PageDomain(uePrj.getPage(),uePrj.getSize()));
    }
	
	/**
     * 查询统一实体项目列表
     */
    @ApiOperation(value = "查询统一实体项目列表")
    @GetMapping("/list")
    public TableDataInfo<UePrjVo> list(UePrj uePrj, PageDomain  pageDomain)
    {
		TableDataInfo<UePrjVo> dataInfo = new TableDataInfo();
        Page<UePrj> page = new Page<>(pageDomain.getPageNum(),pageDomain.getPageSize());
        IPage<UePrj> list = uePrjService.page(page, Wrappers.<UePrj>lambdaQuery(uePrj).orderByDesc(BaseEntity::getCreateTime));
        IPage<UePrjVo> rows = UePrjWrapper.build().pageVO(list);
        dataInfo.setCode(Constants.SUCCESS);
        dataInfo.setData(rows.getRecords());
        dataInfo.setMsg("查询成功");
        dataInfo.setTotal(list.getTotal());
        return dataInfo;
    }

    /**
     * 获取统一实体项目详细信息
     */
    @ApiOperation(value = "获取统一实体项目详细信息")
    @GetMapping(value = "/{id}")
    public R<UePrjVo> getInfo(@PathVariable("id") Long id)
    {
        UePrj uePrj = uePrjService.getById(id);
        return R.ok(UePrjWrapper.build().entityVO(uePrj));
    }


    /**
     * 新增统一实体项目
     */
    @ApiOperation(value = "新增统一实体项目")
    @PostMapping
    public R<Long> add(@RequestBody UePrj uePrj)
    {
        uePrj.setCreateTime(DateUtil.now());
        Boolean b = uePrjService.save(uePrj);
        R r = b ? R.ok(uePrj.getId()) : R.fail();
        return r;
    }

    /**
     * 修改统一实体项目
     */
    @ApiOperation(value = "修改统一实体项目")
    @PostMapping("/edit")
    public R<Integer> edit(@RequestBody UePrj uePrj)
    {
        uePrj.setUpdateTime(DateUtil.now());
        Boolean b = uePrjService.updateById(uePrj);
        R r = b ? R.ok() : R.fail();
        return r;
    }

    /**
     * 删除统一实体项目
     */
    @ApiOperation(value = "删除统一实体项目")
	@PostMapping("/delete/{ids}")
    public R<Integer> remove(@PathVariable Long[] ids)
    {
        Boolean b = uePrjService.removeByIds(Arrays.asList(ids));
        R r = b ? R.ok() : R.fail();
        return r;
    }
	
	
	 /**
     * 统一实体项目的动态字典
     */
    @ApiOperation(value = "统一实体项目的字典接口", notes = "传入id获取唯一对应值，或传空获取所有值",hidden = true)
    @PostMapping("/dict")
    public List<UePrj> dict(@RequestBody List<Long> ids) {
		List<UePrj> data = uePrjService.listByIds(ids);
        return data;
	}
	
}