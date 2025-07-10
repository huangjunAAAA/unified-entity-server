package com.zjht.unified.controller ;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wukong.core.weblog.utils.DateUtil;
import com.wukong.core.weblog.utils.StringUtil;
import com.zjht.unified.common.core.constants.Constants;
import com.zjht.unified.common.core.constants.DeleteConstants;
import com.zjht.unified.common.core.controller.BaseController;
import com.zjht.unified.common.core.domain.PageDomain;
import com.zjht.unified.common.core.domain.R;
import com.zjht.unified.common.core.domain.TableDataInfo;
import com.zjht.unified.common.core.domain.dto.BaseQueryDTO;
import com.wukong.core.mp.base.BaseEntity;
import com.zjht.unified.common.core.util.IdUtils;
import com.zjht.unified.vo.ClazzDefVo;
import com.zjht.unified.wrapper.ClazzDefWrapper;
import com.zjht.unified.entity.ClazzDef;
import com.zjht.unified.service.IClazzDefService;
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
@Api(value = "类定义维护",tags = {"类定义维护"})
@RestController
@RequestMapping("/clazzDef")
public class ClazzDefController extends BaseController{

	private static final Logger logger = LoggerFactory.getLogger(ClazzDefController.class);
	@Autowired
    private IClazzDefService clazzDefService;
	
	/**
     * 查询类定义列表, 对象形式
     */
    @ApiOperation(value = "查询类定义列表")
    @PostMapping("/list-ext")
    public TableDataInfo<ClazzDefVo> listExt(@RequestBody BaseQueryDTO<ClazzDef> clazzDef)
    {
        return list(clazzDef.getCondition(),new PageDomain(clazzDef.getPage(),clazzDef.getSize()));
    }
	
	/**
     * 查询类定义列表
     */
    @ApiOperation(value = "查询类定义列表")
    @GetMapping("/list")
    public TableDataInfo<ClazzDefVo> list(ClazzDef clazzDef, PageDomain  pageDomain)
    {
		TableDataInfo<ClazzDefVo> dataInfo = new TableDataInfo();
        Page<ClazzDef> page = new Page<>(pageDomain.getPageNum(),pageDomain.getPageSize());
        IPage<ClazzDef> list = clazzDefService.page(page, Wrappers.<ClazzDef>lambdaQuery(clazzDef).orderByDesc(BaseEntity::getCreateTime));
        IPage<ClazzDefVo> rows = ClazzDefWrapper.build().pageVO(list);
        dataInfo.setCode(Constants.SUCCESS);
        dataInfo.setData(rows.getRecords());
        dataInfo.setMsg("查询成功");
        dataInfo.setTotal(list.getTotal());
        return dataInfo;
    }

    /**
     * 获取类定义详细信息
     */
    @ApiOperation(value = "获取类定义详细信息")
    @GetMapping(value = "/{id}")
    public R<ClazzDefVo> getInfo(@PathVariable("id") Long id)
    {
        ClazzDef clazzDef = clazzDefService.getById(id);
        return R.ok(ClazzDefWrapper.build().entityVO(clazzDef));
    }


    /**
     * 新增类定义
     */
    @ApiOperation(value = "新增类定义")
    @PostMapping
    public R<Long> add(@RequestBody ClazzDef clazzDef)
    {
        clazzDef.setCreateTime(DateUtil.now());
        if (StringUtil.isBlank(clazzDef.getGuid())) {
            clazzDef.setGuid(IdUtils.fastUUID());
        }
        if(clazzDef.getParentId()!=null) {
            ClazzDef parent = clazzDefService.getById(clazzDef.getParentId());
            if (parent != null) {
                clazzDef.setParentGuid(parent.getGuid());
                clazzDef.setParentPrj(parent.getPrjId());
            }
        }
        Boolean b = clazzDefService.save(clazzDef);
        R r = b ? R.ok(clazzDef.getId()) : R.fail();
        return r;
    }

    /**
     * 修改类定义
     */
    @ApiOperation(value = "修改类定义")
    @PostMapping("/edit")
    public R<Integer> edit(@RequestBody ClazzDef clazzDef)
    {
        if(clazzDef.getParentId()!=null) {
            ClazzDef parent = clazzDefService.getById(clazzDef.getParentId());
            if (parent != null) {
                clazzDef.setParentGuid(parent.getGuid());
                clazzDef.setParentPrj(parent.getPrjId());
            }
        }
        clazzDef.setUpdateTime(DateUtil.now());
        Boolean b = clazzDefService.updateById(clazzDef);
        R r = b ? R.ok() : R.fail();
        return r;
    }

    /**
     * 删除类定义
     */
    @ApiOperation(value = "删除类定义")
	@PostMapping("/delete/{ids}")
    public R<Integer> remove(@PathVariable Long[] ids)
    {
        Boolean b = clazzDefService.removeByIds(Arrays.asList(ids));
        R r = b ? R.ok() : R.fail();
        return r;
    }
	
	
	 /**
     * 类定义的动态字典
     */
    @ApiOperation(value = "类定义的字典接口", notes = "传入id获取唯一对应值，或传空获取所有值",hidden = true)
    @PostMapping("/dict")
    public List<ClazzDef> dict(@RequestBody List<Long> ids) {
		List<ClazzDef> data = clazzDefService.listByIds(ids);
        return data;
	}
	
}