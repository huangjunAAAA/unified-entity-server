package com.zjht.ui.controller ;

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
import com.zjht.ui.vo.UiPrjVo;
import com.zjht.ui.wrapper.UiPrjWrapper;
import com.zjht.ui.entity.UiPrj;
import com.zjht.ui.service.IUiPrjService;
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
@Api(value = "UI项目表(uiPrj)维护",tags = {"UI项目表(uiPrj)维护"})
@RestController
@RequestMapping("/uiPrj")
public class UiPrjController extends BaseController{

	private static final Logger logger = LoggerFactory.getLogger(UiPrjController.class);
	@Autowired
    private IUiPrjService uiPrjService;
	
	/**
     * 查询UI项目表(uiPrj)列表, 对象形式
     */
    @ApiOperation(value = "查询UI项目表(uiPrj)列表")
    @PostMapping("/list-ext")
    public TableDataInfo<UiPrjVo> listExt(@RequestBody BaseQueryDTO<UiPrj> uiPrj)
    {
        return list(uiPrj.getCondition(),new PageDomain(uiPrj.getPage(),uiPrj.getSize()));
    }
	
	/**
     * 查询UI项目表(uiPrj)列表
     */
    @ApiOperation(value = "查询UI项目表(uiPrj)列表")
    @GetMapping("/list")
    public TableDataInfo<UiPrjVo> list(UiPrj uiPrj, PageDomain  pageDomain)
    {
		TableDataInfo<UiPrjVo> dataInfo = new TableDataInfo();
        Page<UiPrj> page = new Page<>(pageDomain.getPageNum(),pageDomain.getPageSize());
        IPage<UiPrj> list = uiPrjService.page(page, Wrappers.<UiPrj>lambdaQuery(uiPrj).orderByDesc(BaseEntity::getCreateTime));
        IPage<UiPrjVo> rows = UiPrjWrapper.build().pageVO(list);
        dataInfo.setCode(Constants.SUCCESS);
        dataInfo.setData(rows.getRecords());
        dataInfo.setMsg("查询成功");
        dataInfo.setTotal(list.getTotal());
        return dataInfo;
    }

    /**
     * 获取UI项目表(uiPrj)详细信息
     */
    @ApiOperation(value = "获取UI项目表(uiPrj)详细信息")
    @GetMapping(value = "/{id}")
    public R<UiPrjVo> getInfo(@PathVariable("id") Long id)
    {
        UiPrj uiPrj = uiPrjService.getById(id);
        return R.ok(UiPrjWrapper.build().entityVO(uiPrj));
    }


    /**
     * 新增UI项目表(uiPrj)
     */
    @ApiOperation(value = "新增UI项目表(uiPrj)")
    @PostMapping
    public R<Long> add(@RequestBody UiPrj uiPrj)
    {
        uiPrj.setCreateTime(DateUtil.now());
        Boolean b = uiPrjService.save(uiPrj);
        R r = b ? R.ok(uiPrj.getId()) : R.fail();
        return r;
    }

    /**
     * 修改UI项目表(uiPrj)
     */
    @ApiOperation(value = "修改UI项目表(uiPrj)")
    @PostMapping("/edit")
    public R<Integer> edit(@RequestBody UiPrj uiPrj)
    {
        uiPrj.setUpdateTime(DateUtil.now());
        Boolean b = uiPrjService.updateById(uiPrj);
        R r = b ? R.ok() : R.fail();
        return r;
    }

    /**
     * 删除UI项目表(uiPrj)
     */
    @ApiOperation(value = "删除UI项目表(uiPrj)")
	@PostMapping("/delete/{ids}")
    public R<Integer> remove(@PathVariable Long[] ids)
    {
        Boolean b = uiPrjService.removeByIds(Arrays.asList(ids));
        R r = b ? R.ok() : R.fail();
        return r;
    }
	
	
	 /**
     * UI项目表(uiPrj)的动态字典
     */
    @ApiOperation(value = "UI项目表(uiPrj)的字典接口", notes = "传入id获取唯一对应值，或传空获取所有值",hidden = true)
    @PostMapping("/dict")
    public List<UiPrj> dict(@RequestBody List<Long> ids) {
		List<UiPrj> data = uiPrjService.listByIds(ids);
        return data;
	}
	
}