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
import com.zjht.unified.vo.InitialInstanceVo;
import com.zjht.unified.wrapper.InitialInstanceWrapper;
import com.zjht.unified.entity.InitialInstance;
import com.zjht.unified.service.IInitialInstanceService;
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
@Api(value = "初始实例维护",tags = {"初始实例维护"})
@RestController
@RequestMapping("/initialInstance")
public class InitialInstanceController extends BaseController{

	private static final Logger logger = LoggerFactory.getLogger(InitialInstanceController.class);
	@Autowired
    private IInitialInstanceService initialInstanceService;
	
	/**
     * 查询初始实例列表, 对象形式
     */
    @ApiOperation(value = "查询初始实例列表")
    @PostMapping("/list-ext")
    public TableDataInfo<InitialInstanceVo> listExt(@RequestBody BaseQueryDTO<InitialInstance> initialInstance)
    {
        return list(initialInstance.getCondition(),new PageDomain(initialInstance.getPage(),initialInstance.getSize()));
    }
	
	/**
     * 查询初始实例列表
     */
    @ApiOperation(value = "查询初始实例列表")
    @GetMapping("/list")
    public TableDataInfo<InitialInstanceVo> list(InitialInstance initialInstance, PageDomain  pageDomain)
    {
		TableDataInfo<InitialInstanceVo> dataInfo = new TableDataInfo();
        Page<InitialInstance> page = new Page<>(pageDomain.getPageNum(),pageDomain.getPageSize());
        IPage<InitialInstance> list = initialInstanceService.page(page, Wrappers.<InitialInstance>lambdaQuery(initialInstance).orderByDesc(BaseEntity::getCreateTime));
        IPage<InitialInstanceVo> rows = InitialInstanceWrapper.build().pageVO(list);
        dataInfo.setCode(Constants.SUCCESS);
        dataInfo.setData(rows.getRecords());
        dataInfo.setMsg("查询成功");
        dataInfo.setTotal(list.getTotal());
        return dataInfo;
    }

    /**
     * 获取初始实例详细信息
     */
    @ApiOperation(value = "获取初始实例详细信息")
    @GetMapping(value = "/{id}")
    public R<InitialInstanceVo> getInfo(@PathVariable("id") Long id)
    {
        InitialInstance initialInstance = initialInstanceService.getById(id);
        return R.ok(InitialInstanceWrapper.build().entityVO(initialInstance));
    }


    /**
     * 新增初始实例
     */
    @ApiOperation(value = "新增初始实例")
    @PostMapping
    public R<Long> add(@RequestBody InitialInstance initialInstance)
    {
        initialInstance.setCreateTime(DateUtil.now());
        Boolean b = initialInstanceService.save(initialInstance);
        R r = b ? R.ok(initialInstance.getId()) : R.fail();
        return r;
    }

    /**
     * 修改初始实例
     */
    @ApiOperation(value = "修改初始实例")
    @PostMapping("/edit")
    public R<Integer> edit(@RequestBody InitialInstance initialInstance)
    {
        initialInstance.setUpdateTime(DateUtil.now());
        Boolean b = initialInstanceService.updateById(initialInstance);
        R r = b ? R.ok() : R.fail();
        return r;
    }

    /**
     * 删除初始实例
     */
    @ApiOperation(value = "删除初始实例")
	@PostMapping("/delete/{ids}")
    public R<Integer> remove(@PathVariable Long[] ids)
    {
        Boolean b = initialInstanceService.removeByIds(Arrays.asList(ids));
        R r = b ? R.ok() : R.fail();
        return r;
    }
	
	
	 /**
     * 初始实例的动态字典
     */
    @ApiOperation(value = "初始实例的字典接口", notes = "传入id获取唯一对应值，或传空获取所有值",hidden = true)
    @PostMapping("/dict")
    public List<InitialInstance> dict(@RequestBody List<Long> ids) {
		List<InitialInstance> data = initialInstanceService.listByIds(ids);
        return data;
	}
	
}