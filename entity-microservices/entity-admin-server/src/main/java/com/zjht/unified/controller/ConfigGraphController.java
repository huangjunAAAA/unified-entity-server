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
import com.zjht.unified.vo.ConfigGraphVo;
import com.zjht.unified.wrapper.ConfigGraphWrapper;
import com.zjht.unified.entity.ConfigGraph;
import com.zjht.unified.service.IConfigGraphService;
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
@Api(value = "组态图维护",tags = {"组态图维护"})
@RestController
@RequestMapping("/configGraph")
public class ConfigGraphController extends BaseController{

	private static final Logger logger = LoggerFactory.getLogger(ConfigGraphController.class);
	@Autowired
    private IConfigGraphService configGraphService;
	
	/**
     * 查询组态图列表, 对象形式
     */
    @ApiOperation(value = "查询组态图列表")
    @PostMapping("/list-ext")
    public TableDataInfo<ConfigGraphVo> listExt(@RequestBody BaseQueryDTO<ConfigGraph> configGraph)
    {
        return list(configGraph.getCondition(),new PageDomain(configGraph.getPage(),configGraph.getSize()));
    }
	
	/**
     * 查询组态图列表
     */
    @ApiOperation(value = "查询组态图列表")
    @GetMapping("/list")
    public TableDataInfo<ConfigGraphVo> list(ConfigGraph configGraph, PageDomain  pageDomain)
    {
		TableDataInfo<ConfigGraphVo> dataInfo = new TableDataInfo();
        Page<ConfigGraph> page = new Page<>(pageDomain.getPageNum(),pageDomain.getPageSize());
        IPage<ConfigGraph> list = configGraphService.page(page, Wrappers.<ConfigGraph>lambdaQuery(configGraph).orderByDesc(BaseEntity::getCreateTime));
        IPage<ConfigGraphVo> rows = ConfigGraphWrapper.build().pageVO(list);
        dataInfo.setCode(Constants.SUCCESS);
        dataInfo.setData(rows.getRecords());
        dataInfo.setMsg("查询成功");
        dataInfo.setTotal(list.getTotal());
        return dataInfo;
    }

    /**
     * 获取组态图详细信息
     */
    @ApiOperation(value = "获取组态图详细信息")
    @GetMapping(value = "/{id}")
    public R<ConfigGraphVo> getInfo(@PathVariable("id") Long id)
    {
        ConfigGraph configGraph = configGraphService.getById(id);
        return R.ok(ConfigGraphWrapper.build().entityVO(configGraph));
    }


    /**
     * 新增组态图
     */
    @ApiOperation(value = "新增组态图")
    @PostMapping
    public R<Long> add(@RequestBody ConfigGraph configGraph)
    {
        configGraph.setCreateTime(DateUtil.now());
        Boolean b = configGraphService.save(configGraph);
        R r = b ? R.ok(configGraph.getId()) : R.fail();
        return r;
    }

    /**
     * 修改组态图
     */
    @ApiOperation(value = "修改组态图")
    @PostMapping("/edit")
    public R<Integer> edit(@RequestBody ConfigGraph configGraph)
    {
        configGraph.setUpdateTime(DateUtil.now());
        Boolean b = configGraphService.updateById(configGraph);
        R r = b ? R.ok() : R.fail();
        return r;
    }

    /**
     * 删除组态图
     */
    @ApiOperation(value = "删除组态图")
	@PostMapping("/delete/{ids}")
    public R<Integer> remove(@PathVariable Long[] ids)
    {
        Boolean b = configGraphService.removeByIds(Arrays.asList(ids));
        R r = b ? R.ok() : R.fail();
        return r;
    }
	
	
	 /**
     * 组态图的动态字典
     */
    @ApiOperation(value = "组态图的字典接口", notes = "传入id获取唯一对应值，或传空获取所有值",hidden = true)
    @PostMapping("/dict")
    public List<ConfigGraph> dict(@RequestBody List<Long> ids) {
		List<ConfigGraph> data = configGraphService.listByIds(ids);
        return data;
	}
	
}