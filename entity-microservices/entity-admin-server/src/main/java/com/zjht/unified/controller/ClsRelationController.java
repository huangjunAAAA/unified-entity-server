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
import com.zjht.unified.vo.ClsRelationVo;
import com.zjht.unified.wrapper.ClsRelationWrapper;
import com.zjht.unified.entity.ClsRelation;
import com.zjht.unified.service.IClsRelationService;
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
@Api(value = "实体关系表维护",tags = {"实体关系表维护"})
@RestController
@RequestMapping("/clsRelation")
public class ClsRelationController extends BaseController{

	private static final Logger logger = LoggerFactory.getLogger(ClsRelationController.class);
	@Autowired
    private IClsRelationService clsRelationService;
	
	/**
     * 查询实体关系表列表, 对象形式
     */
    @ApiOperation(value = "查询实体关系表列表")
    @PostMapping("/list-ext")
    public TableDataInfo<ClsRelationVo> listExt(@RequestBody BaseQueryDTO<ClsRelation> clsRelation)
    {
        return list(clsRelation.getCondition(),new PageDomain(clsRelation.getPage(),clsRelation.getSize()));
    }
	
	/**
     * 查询实体关系表列表
     */
    @ApiOperation(value = "查询实体关系表列表")
    @GetMapping("/list")
    public TableDataInfo<ClsRelationVo> list(ClsRelation clsRelation, PageDomain  pageDomain)
    {
		TableDataInfo<ClsRelationVo> dataInfo = new TableDataInfo();
        Page<ClsRelation> page = new Page<>(pageDomain.getPageNum(),pageDomain.getPageSize());
        IPage<ClsRelation> list = clsRelationService.page(page, Wrappers.<ClsRelation>lambdaQuery(clsRelation).orderByDesc(BaseEntity::getCreateTime));
        IPage<ClsRelationVo> rows = ClsRelationWrapper.build().pageVO(list);
        dataInfo.setCode(Constants.SUCCESS);
        dataInfo.setData(rows.getRecords());
        dataInfo.setMsg("查询成功");
        dataInfo.setTotal(list.getTotal());
        return dataInfo;
    }

    /**
     * 获取实体关系表详细信息
     */
    @ApiOperation(value = "获取实体关系表详细信息")
    @GetMapping(value = "/{id}")
    public R<ClsRelationVo> getInfo(@PathVariable("id") Long id)
    {
        ClsRelation clsRelation = clsRelationService.getById(id);
        return R.ok(ClsRelationWrapper.build().entityVO(clsRelation));
    }


    /**
     * 新增实体关系表
     */
    @ApiOperation(value = "新增实体关系表")
    @PostMapping
    public R<Long> add(@RequestBody ClsRelation clsRelation)
    {
        clsRelation.setCreateTime(DateUtil.now());
        Boolean b = clsRelationService.save(clsRelation);
        R r = b ? R.ok(clsRelation.getId()) : R.fail();
        return r;
    }

    /**
     * 修改实体关系表
     */
    @ApiOperation(value = "修改实体关系表")
    @PostMapping("/edit")
    public R<Integer> edit(@RequestBody ClsRelation clsRelation)
    {
        clsRelation.setUpdateTime(DateUtil.now());
        Boolean b = clsRelationService.updateById(clsRelation);
        R r = b ? R.ok() : R.fail();
        return r;
    }

    /**
     * 删除实体关系表
     */
    @ApiOperation(value = "删除实体关系表")
	@PostMapping("/delete/{ids}")
    public R<Integer> remove(@PathVariable Long[] ids)
    {
        Boolean b = clsRelationService.removeByIds(Arrays.asList(ids));
        R r = b ? R.ok() : R.fail();
        return r;
    }
	
	
	 /**
     * 实体关系表的动态字典
     */
    @ApiOperation(value = "实体关系表的字典接口", notes = "传入id获取唯一对应值，或传空获取所有值",hidden = true)
    @PostMapping("/dict")
    public List<ClsRelation> dict(@RequestBody List<Long> ids) {
		List<ClsRelation> data = clsRelationService.listByIds(ids);
        return data;
	}
	
}