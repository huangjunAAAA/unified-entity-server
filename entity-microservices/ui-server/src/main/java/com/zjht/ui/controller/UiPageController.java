package com.zjht.ui.controller ;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wukong.core.weblog.utils.DateUtil;
import com.wukong.core.mp.base.BaseEntity;
import com.zjht.ui.vo.UiPageVo;
import com.zjht.ui.wrapper.UiPageWrapper;
import com.zjht.ui.entity.UiPage;
import com.zjht.ui.service.IUiPageService;
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
@Api(value = "页面表维护",tags = {"页面表维护"})
@RestController
@RequestMapping("/uiPage")
public class UiPageController extends BaseController {

	private static final Logger logger = LoggerFactory.getLogger(UiPageController.class);
	@Autowired
    private IUiPageService uiPageService;
	
	/**
     * 查询页面表列表, 对象形式
     */
    @ApiOperation(value = "查询页面表列表")
    @PostMapping("/list-ext")
    public TableDataInfo<UiPageVo> listExt(@RequestBody BaseQueryDTO<UiPage> uiPage)
    {
        return list(uiPage.getCondition(),new PageDomain(uiPage.getPage(),uiPage.getSize()));
    }
	
	/**
     * 查询页面表列表
     */
    @ApiOperation(value = "查询页面表列表")
    @GetMapping("/list")
    public TableDataInfo<UiPageVo> list(UiPage uiPage, PageDomain  pageDomain)
    {
		TableDataInfo<UiPageVo> dataInfo = new TableDataInfo();
        Page<UiPage> page = new Page<>(pageDomain.getPageNum(),pageDomain.getPageSize());
        IPage<UiPage> list = uiPageService.page(page, Wrappers.<UiPage>lambdaQuery(uiPage).orderByDesc(BaseEntity::getCreateTime));
        IPage<UiPageVo> rows = UiPageWrapper.build().pageVO(list);
        dataInfo.setCode(Constants.SUCCESS);
        dataInfo.setData(rows.getRecords());
        dataInfo.setMsg("查询成功");
        dataInfo.setTotal(list.getTotal());
        return dataInfo;
    }

    /**
     * 获取页面表详细信息
     */
    @ApiOperation(value = "获取页面表详细信息")
    @GetMapping(value = "/{id}")
    public R<UiPageVo> getInfo(@PathVariable("id") Long id)
    {
        UiPage uiPage = uiPageService.getById(id);
        return R.ok(UiPageWrapper.build().entityVO(uiPage));
    }


    /**
     * 新增页面表
     */
    @ApiOperation(value = "新增页面表")
    @PostMapping
    public R<Long> add(@RequestBody UiPage uiPage)
    {
        uiPage.setCreateTime(DateUtil.now());
        Boolean b = uiPageService.save(uiPage);
        R r = b ? R.ok(uiPage.getId()) : R.fail();
        return r;
    }

    /**
     * 修改页面表
     */
    @ApiOperation(value = "修改页面表")
    @PostMapping("/edit")
    public R<Integer> edit(@RequestBody UiPage uiPage)
    {
        uiPage.setUpdateTime(DateUtil.now());
        Boolean b = uiPageService.updateById(uiPage);
        R r = b ? R.ok() : R.fail();
        return r;
    }

    /**
     * 删除页面表
     */
    @ApiOperation(value = "删除页面表")
	@PostMapping("/delete/{ids}")
    public R<Integer> remove(@PathVariable Long[] ids)
    {
        Boolean b = uiPageService.removeByIds(Arrays.asList(ids));
        R r = b ? R.ok() : R.fail();
        return r;
    }
	
	
	 /**
     * 页面表的动态字典
     */
    @ApiOperation(value = "页面表的字典接口", notes = "传入id获取唯一对应值，或传空获取所有值",hidden = true)
    @PostMapping("/dict")
    public List<UiPage> dict(@RequestBody List<Long> ids) {
		List<UiPage> data = uiPageService.listByIds(ids);
        return data;
	}
	
}