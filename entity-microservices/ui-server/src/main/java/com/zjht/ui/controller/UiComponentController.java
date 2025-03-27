package com.zjht.ui.controller ;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wukong.core.weblog.utils.DateUtil;
import com.wukong.core.mp.base.BaseEntity;
import com.zjht.ui.vo.UiComponentVo;
import com.zjht.ui.wrapper.UiComponentWrapper;
import com.zjht.ui.entity.UiComponent;
import com.zjht.ui.service.IUiComponentService;
import com.zjht.unified.common.core.constants.Constants;
import com.zjht.unified.common.core.controller.BaseController;
import com.zjht.unified.common.core.domain.PageDomain;
import com.zjht.unified.common.core.domain.R;
import com.zjht.unified.common.core.domain.TableDataInfo;
import com.zjht.unified.common.core.domain.dto.BaseQueryDTO;
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
@Api(value = "组件表维护",tags = {"组件表维护"})
@RestController
@RequestMapping("/uiComponent")
public class UiComponentController extends BaseController {

	private static final Logger logger = LoggerFactory.getLogger(UiComponentController.class);
	@Autowired
    private IUiComponentService uiComponentService;
	
	/**
     * 查询组件表列表, 对象形式
     */
    @ApiOperation(value = "查询组件表列表")
    @PostMapping("/list-ext")
    public TableDataInfo<UiComponentVo> listExt(@RequestBody BaseQueryDTO<UiComponent> uiComponent)
    {
        return list(uiComponent.getCondition(),new PageDomain(uiComponent.getPage(),uiComponent.getSize()));
    }
	
	/**
     * 查询组件表列表
     */
    @ApiOperation(value = "查询组件表列表")
    @GetMapping("/list")
    public TableDataInfo<UiComponentVo> list(UiComponent uiComponent, PageDomain  pageDomain)
    {
		TableDataInfo<UiComponentVo> dataInfo = new TableDataInfo();
        Page<UiComponent> page = new Page<>(pageDomain.getPageNum(),pageDomain.getPageSize());
        IPage<UiComponent> list = uiComponentService.page(page, Wrappers.<UiComponent>lambdaQuery(uiComponent).orderByDesc(BaseEntity::getCreateTime));
        IPage<UiComponentVo> rows = UiComponentWrapper.build().pageVO(list);
        dataInfo.setCode(Constants.SUCCESS);
        dataInfo.setData(rows.getRecords());
        dataInfo.setMsg("查询成功");
        dataInfo.setTotal(list.getTotal());
        return dataInfo;
    }

    /**
     * 获取组件表详细信息
     */
    @ApiOperation(value = "获取组件表详细信息")
    @GetMapping(value = "/{id}")
    public R<UiComponentVo> getInfo(@PathVariable("id") Long id)
    {
        UiComponent uiComponent = uiComponentService.getById(id);
        return R.ok(UiComponentWrapper.build().entityVO(uiComponent));
    }


    /**
     * 新增组件表
     */
    @ApiOperation(value = "新增组件表")
    @PostMapping
    public R<Long> add(@RequestBody UiComponent uiComponent)
    {
        uiComponent.setCreateTime(DateUtil.now());
        Boolean b = uiComponentService.save(uiComponent);
        R r = b ? R.ok(uiComponent.getId()) : R.fail();
        return r;
    }

    /**
     * 修改组件表
     */
    @ApiOperation(value = "修改组件表")
    @PostMapping("/edit")
    public R<Integer> edit(@RequestBody UiComponent uiComponent)
    {
        uiComponent.setUpdateTime(DateUtil.now());
        Boolean b = uiComponentService.updateById(uiComponent);
        R r = b ? R.ok() : R.fail();
        return r;
    }

    /**
     * 删除组件表
     */
    @ApiOperation(value = "删除组件表")
	@PostMapping("/delete/{ids}")
    public R<Integer> remove(@PathVariable Long[] ids)
    {
        Boolean b = uiComponentService.removeByIds(Arrays.asList(ids));
        R r = b ? R.ok() : R.fail();
        return r;
    }
	
	
	 /**
     * 组件表的动态字典
     */
    @ApiOperation(value = "组件表的字典接口", notes = "传入id获取唯一对应值，或传空获取所有值",hidden = true)
    @PostMapping("/dict")
    public List<UiComponent> dict(@RequestBody List<Long> ids) {
		List<UiComponent> data = uiComponentService.listByIds(ids);
        return data;
	}
	
}