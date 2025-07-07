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
import com.zjht.unified.common.core.util.StringUtils;
import com.zjht.unified.vo.FieldDefVo;
import com.zjht.unified.wrapper.FieldDefWrapper;
import com.zjht.unified.entity.FieldDef;
import com.zjht.unified.service.IFieldDefService;
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
@Api(value = "字段定义维护",tags = {"字段定义维护"})
@RestController
@RequestMapping("/fieldDef")
public class FieldDefController extends BaseController{

	private static final Logger logger = LoggerFactory.getLogger(FieldDefController.class);
	@Autowired
    private IFieldDefService fieldDefService;
	
	/**
     * 查询字段定义列表, 对象形式
     */
    @ApiOperation(value = "查询字段定义列表")
    @PostMapping("/list-ext")
    public TableDataInfo<FieldDefVo> listExt(@RequestBody BaseQueryDTO<FieldDef> fieldDef)
    {
        return list(fieldDef.getCondition(),new PageDomain(fieldDef.getPage(),fieldDef.getSize()));
    }
	
	/**
     * 查询字段定义列表
     */
    @ApiOperation(value = "查询字段定义列表")
    @GetMapping("/list")
    public TableDataInfo<FieldDefVo> list(FieldDef fieldDef, PageDomain  pageDomain)
    {
		TableDataInfo<FieldDefVo> dataInfo = new TableDataInfo();
        Page<FieldDef> page = new Page<>(pageDomain.getPageNum(),pageDomain.getPageSize());
        IPage<FieldDef> list = fieldDefService.page(page, Wrappers.<FieldDef>lambdaQuery(fieldDef).orderByDesc(BaseEntity::getCreateTime));
        IPage<FieldDefVo> rows = FieldDefWrapper.build().pageVO(list);
        dataInfo.setCode(Constants.SUCCESS);
        dataInfo.setData(rows.getRecords());
        dataInfo.setMsg("查询成功");
        dataInfo.setTotal(list.getTotal());
        return dataInfo;
    }

    /**
     * 获取字段定义详细信息
     */
    @ApiOperation(value = "获取字段定义详细信息")
    @GetMapping(value = "/{id}")
    public R<FieldDefVo> getInfo(@PathVariable("id") Long id)
    {
        FieldDef fieldDef = fieldDefService.getById(id);
        return R.ok(FieldDefWrapper.build().entityVO(fieldDef));
    }


    /**
     * 新增字段定义
     */
    @ApiOperation(value = "新增字段定义")
    @PostMapping
    public R<Long> add(@RequestBody FieldDef fieldDef)
    {
        if(StringUtils.isBlank(fieldDef.getName())){
            return R.fail("字段名称不能为空");
        }
        if(StringUtils.isValidVar(fieldDef.getName())){
            return R.fail("字段名称格式错误");
        }
        if(StringUtils.isBlank(fieldDef.getType())){
            return R.fail("字段类型不能为空");
        }
        if(null==fieldDef.getNature()){
            return R.fail("字段属性不能为空");
        }
        fieldDef.setCreateTime(DateUtil.now());
        Boolean b = fieldDefService.save(fieldDef);
        R r = b ? R.ok(fieldDef.getId()) : R.fail();
        return r;
    }

    /**
     * 修改字段定义
     */
    @ApiOperation(value = "修改字段定义")
    @PostMapping("/edit")
    public R<Integer> edit(@RequestBody FieldDef fieldDef)
    {
        fieldDef.setUpdateTime(DateUtil.now());
        Boolean b = fieldDefService.updateById(fieldDef);
        R r = b ? R.ok() : R.fail();
        return r;
    }

    /**
     * 删除字段定义
     */
    @ApiOperation(value = "删除字段定义")
	@PostMapping("/delete/{ids}")
    public R<Integer> remove(@PathVariable Long[] ids)
    {
        Boolean b = fieldDefService.removeByIds(Arrays.asList(ids));
        R r = b ? R.ok() : R.fail();
        return r;
    }
	
	
	 /**
     * 字段定义的动态字典
     */
    @ApiOperation(value = "字段定义的字典接口", notes = "传入id获取唯一对应值，或传空获取所有值",hidden = true)
    @PostMapping("/dict")
    public List<FieldDef> dict(@RequestBody List<Long> ids) {
		List<FieldDef> data = fieldDefService.listByIds(ids);
        return data;
	}
	
}