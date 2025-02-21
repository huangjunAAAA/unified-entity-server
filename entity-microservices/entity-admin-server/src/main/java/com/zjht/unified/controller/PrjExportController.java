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
import com.zjht.unified.vo.PrjExportVo;
import com.zjht.unified.wrapper.PrjExportWrapper;
import com.zjht.unified.entity.PrjExport;
import com.zjht.unified.service.IPrjExportService;
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
@Api(value = "项目导出配置维护",tags = {"项目导出配置维护"})
@RestController
@RequestMapping("/prjExport")
public class PrjExportController extends BaseController{

	private static final Logger logger = LoggerFactory.getLogger(PrjExportController.class);
	@Autowired
    private IPrjExportService prjExportService;
	
	/**
     * 查询项目导出配置列表, 对象形式
     */
    @ApiOperation(value = "查询项目导出配置列表")
    @PostMapping("/list-ext")
    public TableDataInfo<PrjExportVo> listExt(@RequestBody BaseQueryDTO<PrjExport> prjExport)
    {
        return list(prjExport.getCondition(),new PageDomain(prjExport.getPage(),prjExport.getSize()));
    }
	
	/**
     * 查询项目导出配置列表
     */
    @ApiOperation(value = "查询项目导出配置列表")
    @GetMapping("/list")
    public TableDataInfo<PrjExportVo> list(PrjExport prjExport, PageDomain  pageDomain)
    {
		TableDataInfo<PrjExportVo> dataInfo = new TableDataInfo();
        Page<PrjExport> page = new Page<>(pageDomain.getPageNum(),pageDomain.getPageSize());
        IPage<PrjExport> list = prjExportService.page(page, Wrappers.<PrjExport>lambdaQuery(prjExport).orderByDesc(BaseEntity::getCreateTime));
        IPage<PrjExportVo> rows = PrjExportWrapper.build().pageVO(list);
        dataInfo.setCode(Constants.SUCCESS);
        dataInfo.setData(rows.getRecords());
        dataInfo.setMsg("查询成功");
        dataInfo.setTotal(list.getTotal());
        return dataInfo;
    }

    /**
     * 获取项目导出配置详细信息
     */
    @ApiOperation(value = "获取项目导出配置详细信息")
    @GetMapping(value = "/{id}")
    public R<PrjExportVo> getInfo(@PathVariable("id") Long id)
    {
        PrjExport prjExport = prjExportService.getById(id);
        return R.ok(PrjExportWrapper.build().entityVO(prjExport));
    }


    /**
     * 新增项目导出配置
     */
    @ApiOperation(value = "新增项目导出配置")
    @PostMapping
    public R<Long> add(@RequestBody PrjExport prjExport)
    {
        prjExport.setCreateTime(DateUtil.now());
        Boolean b = prjExportService.save(prjExport);
        R r = b ? R.ok(prjExport.getId()) : R.fail();
        return r;
    }

    /**
     * 修改项目导出配置
     */
    @ApiOperation(value = "修改项目导出配置")
    @PostMapping("/edit")
    public R<Integer> edit(@RequestBody PrjExport prjExport)
    {
        prjExport.setUpdateTime(DateUtil.now());
        Boolean b = prjExportService.updateById(prjExport);
        R r = b ? R.ok() : R.fail();
        return r;
    }

    /**
     * 删除项目导出配置
     */
    @ApiOperation(value = "删除项目导出配置")
	@PostMapping("/delete/{ids}")
    public R<Integer> remove(@PathVariable Long[] ids)
    {
        Boolean b = prjExportService.removeByIds(Arrays.asList(ids));
        R r = b ? R.ok() : R.fail();
        return r;
    }
	
	
	 /**
     * 项目导出配置的动态字典
     */
    @ApiOperation(value = "项目导出配置的字典接口", notes = "传入id获取唯一对应值，或传空获取所有值",hidden = true)
    @PostMapping("/dict")
    public List<PrjExport> dict(@RequestBody List<Long> ids) {
		List<PrjExport> data = prjExportService.listByIds(ids);
        return data;
	}
	
}