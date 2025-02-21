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
import com.zjht.unified.vo.FsmDefVo;
import com.zjht.unified.wrapper.FsmDefWrapper;
import com.zjht.unified.entity.FsmDef;
import com.zjht.unified.service.IFsmDefService;
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
@Api(value = "状态机实例维护",tags = {"状态机实例维护"})
@RestController
@RequestMapping("/fsmDef")
public class FsmDefController extends BaseController{

	private static final Logger logger = LoggerFactory.getLogger(FsmDefController.class);
	@Autowired
    private IFsmDefService fsmDefService;
	
	/**
     * 查询状态机实例列表, 对象形式
     */
    @ApiOperation(value = "查询状态机实例列表")
    @PostMapping("/list-ext")
    public TableDataInfo<FsmDefVo> listExt(@RequestBody BaseQueryDTO<FsmDef> fsmDef)
    {
        return list(fsmDef.getCondition(),new PageDomain(fsmDef.getPage(),fsmDef.getSize()));
    }
	
	/**
     * 查询状态机实例列表
     */
    @ApiOperation(value = "查询状态机实例列表")
    @GetMapping("/list")
    public TableDataInfo<FsmDefVo> list(FsmDef fsmDef, PageDomain  pageDomain)
    {
		TableDataInfo<FsmDefVo> dataInfo = new TableDataInfo();
        Page<FsmDef> page = new Page<>(pageDomain.getPageNum(),pageDomain.getPageSize());
        IPage<FsmDef> list = fsmDefService.page(page, Wrappers.<FsmDef>lambdaQuery(fsmDef).orderByDesc(BaseEntity::getCreateTime));
        IPage<FsmDefVo> rows = FsmDefWrapper.build().pageVO(list);
        dataInfo.setCode(Constants.SUCCESS);
        dataInfo.setData(rows.getRecords());
        dataInfo.setMsg("查询成功");
        dataInfo.setTotal(list.getTotal());
        return dataInfo;
    }

    /**
     * 获取状态机实例详细信息
     */
    @ApiOperation(value = "获取状态机实例详细信息")
    @GetMapping(value = "/{id}")
    public R<FsmDefVo> getInfo(@PathVariable("id") Long id)
    {
        FsmDef fsmDef = fsmDefService.getById(id);
        return R.ok(FsmDefWrapper.build().entityVO(fsmDef));
    }


    /**
     * 新增状态机实例
     */
    @ApiOperation(value = "新增状态机实例")
    @PostMapping
    public R<Long> add(@RequestBody FsmDef fsmDef)
    {
        fsmDef.setCreateTime(DateUtil.now());
        Boolean b = fsmDefService.save(fsmDef);
        R r = b ? R.ok(fsmDef.getId()) : R.fail();
        return r;
    }

    /**
     * 修改状态机实例
     */
    @ApiOperation(value = "修改状态机实例")
    @PostMapping("/edit")
    public R<Integer> edit(@RequestBody FsmDef fsmDef)
    {
        fsmDef.setUpdateTime(DateUtil.now());
        Boolean b = fsmDefService.updateById(fsmDef);
        R r = b ? R.ok() : R.fail();
        return r;
    }

    /**
     * 删除状态机实例
     */
    @ApiOperation(value = "删除状态机实例")
	@PostMapping("/delete/{ids}")
    public R<Integer> remove(@PathVariable Long[] ids)
    {
        Boolean b = fsmDefService.removeByIds(Arrays.asList(ids));
        R r = b ? R.ok() : R.fail();
        return r;
    }
	
	
	 /**
     * 状态机实例的动态字典
     */
    @ApiOperation(value = "状态机实例的字典接口", notes = "传入id获取唯一对应值，或传空获取所有值",hidden = true)
    @PostMapping("/dict")
    public List<FsmDef> dict(@RequestBody List<Long> ids) {
		List<FsmDef> data = fsmDefService.listByIds(ids);
        return data;
	}
	
}