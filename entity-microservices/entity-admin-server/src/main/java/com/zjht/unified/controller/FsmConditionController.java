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
import com.zjht.unified.vo.FsmConditionVo;
import com.zjht.unified.wrapper.FsmConditionWrapper;
import com.zjht.unified.entity.FsmCondition;
import com.zjht.unified.service.IFsmConditionService;
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
@Api(value = "状态机转换条件维护",tags = {"状态机转换条件维护"})
@RestController
@RequestMapping("/fsmCondition")
public class FsmConditionController extends BaseController{

	private static final Logger logger = LoggerFactory.getLogger(FsmConditionController.class);
	@Autowired
    private IFsmConditionService fsmConditionService;
	
	/**
     * 查询状态机转换条件列表, 对象形式
     */
    @ApiOperation(value = "查询状态机转换条件列表")
    @PostMapping("/list-ext")
    public TableDataInfo<FsmConditionVo> listExt(@RequestBody BaseQueryDTO<FsmCondition> fsmCondition)
    {
        return list(fsmCondition.getCondition(),new PageDomain(fsmCondition.getPage(),fsmCondition.getSize()));
    }
	
	/**
     * 查询状态机转换条件列表
     */
    @ApiOperation(value = "查询状态机转换条件列表")
    @GetMapping("/list")
    public TableDataInfo<FsmConditionVo> list(FsmCondition fsmCondition, PageDomain  pageDomain)
    {
		TableDataInfo<FsmConditionVo> dataInfo = new TableDataInfo();
        Page<FsmCondition> page = new Page<>(pageDomain.getPageNum(),pageDomain.getPageSize());
        IPage<FsmCondition> list = fsmConditionService.page(page, Wrappers.<FsmCondition>lambdaQuery(fsmCondition).orderByDesc(BaseEntity::getCreateTime));
        IPage<FsmConditionVo> rows = FsmConditionWrapper.build().pageVO(list);
        dataInfo.setCode(Constants.SUCCESS);
        dataInfo.setData(rows.getRecords());
        dataInfo.setMsg("查询成功");
        dataInfo.setTotal(list.getTotal());
        return dataInfo;
    }

    /**
     * 获取状态机转换条件详细信息
     */
    @ApiOperation(value = "获取状态机转换条件详细信息")
    @GetMapping(value = "/{id}")
    public R<FsmConditionVo> getInfo(@PathVariable("id") Long id)
    {
        FsmCondition fsmCondition = fsmConditionService.getById(id);
        return R.ok(FsmConditionWrapper.build().entityVO(fsmCondition));
    }


    /**
     * 新增状态机转换条件
     */
    @ApiOperation(value = "新增状态机转换条件")
    @PostMapping
    public R<Long> add(@RequestBody FsmCondition fsmCondition)
    {
        fsmCondition.setCreateTime(DateUtil.now());
        Boolean b = fsmConditionService.save(fsmCondition);
        R r = b ? R.ok(fsmCondition.getId()) : R.fail();
        return r;
    }

    /**
     * 修改状态机转换条件
     */
    @ApiOperation(value = "修改状态机转换条件")
    @PostMapping("/edit")
    public R<Integer> edit(@RequestBody FsmCondition fsmCondition)
    {
        fsmCondition.setUpdateTime(DateUtil.now());
        Boolean b = fsmConditionService.updateById(fsmCondition);
        R r = b ? R.ok() : R.fail();
        return r;
    }

    /**
     * 删除状态机转换条件
     */
    @ApiOperation(value = "删除状态机转换条件")
	@PostMapping("/delete/{ids}")
    public R<Integer> remove(@PathVariable Long[] ids)
    {
        Boolean b = fsmConditionService.removeByIds(Arrays.asList(ids));
        R r = b ? R.ok() : R.fail();
        return r;
    }
	
	
	 /**
     * 状态机转换条件的动态字典
     */
    @ApiOperation(value = "状态机转换条件的字典接口", notes = "传入id获取唯一对应值，或传空获取所有值",hidden = true)
    @PostMapping("/dict")
    public List<FsmCondition> dict(@RequestBody List<Long> ids) {
		List<FsmCondition> data = fsmConditionService.listByIds(ids);
        return data;
	}
	
}