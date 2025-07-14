package com.zjht.unified.datasource.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wukong.core.weblog.utils.DateUtil;

import com.wukong.core.mp.base.BaseEntity;
import com.zjht.unified.datasource.entity.DtpDataSource;
import com.zjht.unified.datasource.vo.DtpDataSourceVo;
import com.zjht.unified.datasource.wrapper.DtpDataSourceWrapper;

import com.zjht.unified.datasource.service.IDtpDataSourceService;
import com.zjht.unified.common.core.constants.Constants;
import com.zjht.unified.common.core.controller.BaseController;
import com.zjht.unified.common.core.domain.PageDomain;
import com.zjht.unified.common.core.domain.R;
import com.zjht.unified.common.core.domain.TableDataInfo;
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
@Api(value = "数据源管理(dtpDataSource)维护",tags = {"数据源管理(dtpDataSource)维护"})
@RestController
@RequestMapping("/dtpDataSource")
public class DtpDataSourceController extends BaseController {

	private static final Logger logger = LoggerFactory.getLogger(DtpDataSourceController.class);
	@Autowired
    private IDtpDataSourceService dtpDataSourceService;
	
	
	
	/**
     * 查询数据源管理(dtpDataSource)列表
     */
    @ApiOperation(value = "查询数据源管理(dtpDataSource)列表")
    @GetMapping("/list")
    public TableDataInfo<DtpDataSourceVo> list(DtpDataSource dtpDataSource, PageDomain pageDomain)
    {
		TableDataInfo<DtpDataSourceVo> dataInfo = new TableDataInfo();
        Page<DtpDataSource> page = new Page<>(pageDomain.getPageNum(),pageDomain.getPageSize());
        IPage<DtpDataSource> list = dtpDataSourceService.page(page, Wrappers.<DtpDataSource>lambdaQuery(dtpDataSource).orderByDesc(BaseEntity::getCreateTime));
        IPage<DtpDataSourceVo> rows = DtpDataSourceWrapper.build().pageVO(list);
        dataInfo.setCode(Constants.SUCCESS);
        dataInfo.setData(rows.getRecords());
        dataInfo.setMsg("查询成功");
        dataInfo.setTotal(list.getTotal());
        return dataInfo;
    }

    /**
     * 获取数据源管理(dtpDataSource)详细信息
     */
    @ApiOperation(value = "获取数据源管理(dtpDataSource)详细信息")
    @GetMapping(value = "/{id}")
    public R<DtpDataSourceVo> getInfo(@PathVariable("id") Long id)
    {
        DtpDataSource dtpDataSource = dtpDataSourceService.getById(id);
        return R.ok(DtpDataSourceWrapper.build().entityVO(dtpDataSource));
    }


    /**
     * 新增数据源管理(dtpDataSource)
     */
    @ApiOperation(value = "新增数据源管理(dtpDataSource)")
    @PostMapping
    public R<Long> add(@RequestBody DtpDataSource dtpDataSource)
    {
        dtpDataSource.setCreateTime(DateUtil.now());
        Boolean b = dtpDataSourceService.save(dtpDataSource);
        R r = b ? R.ok(dtpDataSource.getId()) : R.fail();
        return r;
    }

    /**
     * 修改数据源管理(dtpDataSource)
     */
    @ApiOperation(value = "修改数据源管理(dtpDataSource)")
    @PostMapping("/edit")
    public R<Integer> edit(@RequestBody DtpDataSource dtpDataSource)
    {
        dtpDataSource.setUpdateTime(DateUtil.now());
        Boolean b = dtpDataSourceService.updateById(dtpDataSource);
        R r = b ? R.ok() : R.fail();
        return r;
    }

    /**
     * 删除数据源管理(dtpDataSource)
     */
    @ApiOperation(value = "删除数据源管理(dtpDataSource)")
	@PostMapping("/delete/{ids}")
    public R<Integer> remove(@PathVariable Long[] ids)
    {
        Boolean b = dtpDataSourceService.removeByIds(Arrays.asList(ids));
        R r = b ? R.ok() : R.fail();
        return r;
    }
	
	
	 /**
     * 数据源管理(dtpDataSource)的动态字典
     */
    @ApiOperation(value = "数据源管理(dtpDataSource)的字典接口", notes = "传入id获取唯一对应值，或传空获取所有值",hidden = true)
    @PostMapping("/dict")
    public List<DtpDataSource> dict(@RequestBody List<Long> ids) {
		List<DtpDataSource> data = dtpDataSourceService.listByIds(ids);
        return data;
	}
	
}