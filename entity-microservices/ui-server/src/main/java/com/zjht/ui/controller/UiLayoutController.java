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
import com.zjht.ui.vo.UiLayoutVo;
import com.zjht.ui.wrapper.UiLayoutWrapper;
import com.zjht.ui.entity.UiLayout;
import com.zjht.ui.service.IUiLayoutService;
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
@Api(value = "页面布局(uiLayout)维护",tags = {"页面布局(uiLayout)维护"})
@RestController
@RequestMapping("/uiLayout")
public class UiLayoutController extends BaseController{

	private static final Logger logger = LoggerFactory.getLogger(UiLayoutController.class);
	@Autowired
    private IUiLayoutService uiLayoutService;
	
	/**
     * 查询页面布局(uiLayout)列表, 对象形式
     */
    @ApiOperation(value = "查询页面布局(uiLayout)列表")
    @PostMapping("/list-ext")
    public TableDataInfo<UiLayoutVo> listExt(@RequestBody BaseQueryDTO<UiLayout> uiLayout)
    {
        return list(uiLayout.getCondition(),new PageDomain(uiLayout.getPage(),uiLayout.getSize()));
    }
	
	/**
     * 查询页面布局(uiLayout)列表
     */
    @ApiOperation(value = "查询页面布局(uiLayout)列表")
    @GetMapping("/list")
    public TableDataInfo<UiLayoutVo> list(UiLayout uiLayout, PageDomain  pageDomain)
    {
		TableDataInfo<UiLayoutVo> dataInfo = new TableDataInfo();
        Page<UiLayout> page = new Page<>(pageDomain.getPageNum(),pageDomain.getPageSize());
        IPage<UiLayout> list = uiLayoutService.page(page, Wrappers.<UiLayout>lambdaQuery(uiLayout).orderByDesc(BaseEntity::getCreateTime));
        IPage<UiLayoutVo> rows = UiLayoutWrapper.build().pageVO(list);
        dataInfo.setCode(Constants.SUCCESS);
        dataInfo.setData(rows.getRecords());
        dataInfo.setMsg("查询成功");
        dataInfo.setTotal(list.getTotal());
        return dataInfo;
    }

    /**
     * 获取页面布局(uiLayout)详细信息
     */
    @ApiOperation(value = "获取页面布局(uiLayout)详细信息")
    @GetMapping(value = "/{id}")
    public R<UiLayoutVo> getInfo(@PathVariable("id") Long id)
    {
        UiLayout uiLayout = uiLayoutService.getById(id);
        return R.ok(UiLayoutWrapper.build().entityVO(uiLayout));
    }


    /**
     * 新增页面布局(uiLayout)
     */
    @ApiOperation(value = "新增页面布局(uiLayout)")
    @PostMapping
    public R<Long> add(@RequestBody UiLayout uiLayout)
    {
        uiLayout.setCreateTime(DateUtil.now());
        Boolean b = uiLayoutService.save(uiLayout);
        R r = b ? R.ok(uiLayout.getId()) : R.fail();
        return r;
    }

    /**
     * 修改页面布局(uiLayout)
     */
    @ApiOperation(value = "修改页面布局(uiLayout)")
    @PostMapping("/edit")
    public R<Integer> edit(@RequestBody UiLayout uiLayout)
    {
        uiLayout.setUpdateTime(DateUtil.now());
        Boolean b = uiLayoutService.updateById(uiLayout);
        R r = b ? R.ok() : R.fail();
        return r;
    }

    /**
     * 删除页面布局(uiLayout)
     */
    @ApiOperation(value = "删除页面布局(uiLayout)")
	@PostMapping("/delete/{ids}")
    public R<Integer> remove(@PathVariable Long[] ids)
    {
        Boolean b = uiLayoutService.removeByIds(Arrays.asList(ids));
        R r = b ? R.ok() : R.fail();
        return r;
    }
	
	
	 /**
     * 页面布局(uiLayout)的动态字典
     */
    @ApiOperation(value = "页面布局(uiLayout)的字典接口", notes = "传入id获取唯一对应值，或传空获取所有值",hidden = true)
    @PostMapping("/dict")
    public List<UiLayout> dict(@RequestBody List<Long> ids) {
		List<UiLayout> data = uiLayoutService.listByIds(ids);
        return data;
	}
	
}