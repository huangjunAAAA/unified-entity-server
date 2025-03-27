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
import com.zjht.ui.vo.UiEventHandleVo;
import com.zjht.ui.wrapper.UiEventHandleWrapper;
import com.zjht.ui.entity.UiEventHandle;
import com.zjht.ui.service.IUiEventHandleService;
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
@Api(value = "事件交互(uiEventHandle)维护",tags = {"事件交互(uiEventHandle)维护"})
@RestController
@RequestMapping("/uiEventHandle")
public class UiEventHandleController extends BaseController{


	private static final Logger logger = LoggerFactory.getLogger(UiEventHandleController.class);
	@Autowired
    private IUiEventHandleService uiEventHandleService;
	
	/**
     * 查询事件交互(uiEventHandle)列表, 对象形式
     */
    @ApiOperation(value = "查询事件交互(uiEventHandle)列表")
    @PostMapping("/list-ext")
    public TableDataInfo<UiEventHandleVo> listExt(@RequestBody BaseQueryDTO<UiEventHandle> uiEventHandle)
    {
        return list(uiEventHandle.getCondition(),new PageDomain(uiEventHandle.getPage(),uiEventHandle.getSize()));
    }
	
	/**
     * 查询事件交互(uiEventHandle)列表
     */
    @ApiOperation(value = "查询事件交互(uiEventHandle)列表")
    @GetMapping("/list")
    public TableDataInfo<UiEventHandleVo> list(UiEventHandle uiEventHandle, PageDomain  pageDomain)
    {
		TableDataInfo<UiEventHandleVo> dataInfo = new TableDataInfo();
        Page<UiEventHandle> page = new Page<>(pageDomain.getPageNum(),pageDomain.getPageSize());
        IPage<UiEventHandle> list = uiEventHandleService.page(page, Wrappers.<UiEventHandle>lambdaQuery(uiEventHandle).orderByDesc(BaseEntity::getCreateTime));
        IPage<UiEventHandleVo> rows = UiEventHandleWrapper.build().pageVO(list);
        dataInfo.setCode(Constants.SUCCESS);
        dataInfo.setData(rows.getRecords());
        dataInfo.setMsg("查询成功");
        dataInfo.setTotal(list.getTotal());
        return dataInfo;
    }

    /**
     * 获取事件交互(uiEventHandle)详细信息
     */
    @ApiOperation(value = "获取事件交互(uiEventHandle)详细信息")
    @GetMapping(value = "/{id}")
    public R<UiEventHandleVo> getInfo(@PathVariable("id") Long id)
    {
        UiEventHandle uiEventHandle = uiEventHandleService.getById(id);
        return R.ok(UiEventHandleWrapper.build().entityVO(uiEventHandle));
    }


    /**
     * 新增事件交互(uiEventHandle)
     */
    @ApiOperation(value = "新增事件交互(uiEventHandle)")
    @PostMapping
    public R<Long> add(@RequestBody UiEventHandle uiEventHandle)
    {
        uiEventHandle.setCreateTime(DateUtil.now());
        Boolean b = uiEventHandleService.save(uiEventHandle);
        R r = b ? R.ok(uiEventHandle.getId()) : R.fail();
        return r;
    }

    /**
     * 修改事件交互(uiEventHandle)
     */
    @ApiOperation(value = "修改事件交互(uiEventHandle)")
    @PostMapping("/edit")
    public R<Integer> edit(@RequestBody UiEventHandle uiEventHandle)
    {
        uiEventHandle.setUpdateTime(DateUtil.now());
        Boolean b = uiEventHandleService.updateById(uiEventHandle);
        R r = b ? R.ok() : R.fail();
        return r;
    }

    /**
     * 删除事件交互(uiEventHandle)
     */
    @ApiOperation(value = "删除事件交互(uiEventHandle)")
	@PostMapping("/delete/{ids}")
    public R<Integer> remove(@PathVariable Long[] ids)
    {
        Boolean b = uiEventHandleService.removeByIds(Arrays.asList(ids));
        R r = b ? R.ok() : R.fail();
        return r;
    }
	
	
	 /**
     * 事件交互(uiEventHandle)的动态字典
     */
    @ApiOperation(value = "事件交互(uiEventHandle)的字典接口", notes = "传入id获取唯一对应值，或传空获取所有值",hidden = true)
    @PostMapping("/dict")
    public List<UiEventHandle> dict(@RequestBody List<Long> ids) {
		List<UiEventHandle> data = uiEventHandleService.listByIds(ids);
        return data;
	}
	
}