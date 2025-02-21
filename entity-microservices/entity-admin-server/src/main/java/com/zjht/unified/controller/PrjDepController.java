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
import com.zjht.unified.vo.PrjDepVo;
import com.zjht.unified.wrapper.PrjDepWrapper;
import com.zjht.unified.entity.PrjDep;
import com.zjht.unified.service.IPrjDepService;
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
@Api(value = "项目依赖关系维护",tags = {"项目依赖关系维护"})
@RestController
@RequestMapping("/prjDep")
public class PrjDepController extends BaseController{

	private static final Logger logger = LoggerFactory.getLogger(PrjDepController.class);
	@Autowired
    private IPrjDepService prjDepService;
	
	/**
     * 查询项目依赖关系列表, 对象形式
     */
    @ApiOperation(value = "查询项目依赖关系列表")
    @PostMapping("/list-ext")
    public TableDataInfo<PrjDepVo> listExt(@RequestBody BaseQueryDTO<PrjDep> prjDep)
    {
        return list(prjDep.getCondition(),new PageDomain(prjDep.getPage(),prjDep.getSize()));
    }
	
	/**
     * 查询项目依赖关系列表
     */
    @ApiOperation(value = "查询项目依赖关系列表")
    @GetMapping("/list")
    public TableDataInfo<PrjDepVo> list(PrjDep prjDep, PageDomain  pageDomain)
    {
		TableDataInfo<PrjDepVo> dataInfo = new TableDataInfo();
        Page<PrjDep> page = new Page<>(pageDomain.getPageNum(),pageDomain.getPageSize());
        IPage<PrjDep> list = prjDepService.page(page, Wrappers.<PrjDep>lambdaQuery(prjDep).orderByDesc(BaseEntity::getCreateTime));
        IPage<PrjDepVo> rows = PrjDepWrapper.build().pageVO(list);
        dataInfo.setCode(Constants.SUCCESS);
        dataInfo.setData(rows.getRecords());
        dataInfo.setMsg("查询成功");
        dataInfo.setTotal(list.getTotal());
        return dataInfo;
    }

    /**
     * 获取项目依赖关系详细信息
     */
    @ApiOperation(value = "获取项目依赖关系详细信息")
    @GetMapping(value = "/{id}")
    public R<PrjDepVo> getInfo(@PathVariable("id") Long id)
    {
        PrjDep prjDep = prjDepService.getById(id);
        return R.ok(PrjDepWrapper.build().entityVO(prjDep));
    }


    /**
     * 新增项目依赖关系
     */
    @ApiOperation(value = "新增项目依赖关系")
    @PostMapping
    public R<Long> add(@RequestBody PrjDep prjDep)
    {
        prjDep.setCreateTime(DateUtil.now());
        Boolean b = prjDepService.save(prjDep);
        R r = b ? R.ok(prjDep.getId()) : R.fail();
        return r;
    }

    /**
     * 修改项目依赖关系
     */
    @ApiOperation(value = "修改项目依赖关系")
    @PostMapping("/edit")
    public R<Integer> edit(@RequestBody PrjDep prjDep)
    {
        prjDep.setUpdateTime(DateUtil.now());
        Boolean b = prjDepService.updateById(prjDep);
        R r = b ? R.ok() : R.fail();
        return r;
    }

    /**
     * 删除项目依赖关系
     */
    @ApiOperation(value = "删除项目依赖关系")
	@PostMapping("/delete/{ids}")
    public R<Integer> remove(@PathVariable Long[] ids)
    {
        Boolean b = prjDepService.removeByIds(Arrays.asList(ids));
        R r = b ? R.ok() : R.fail();
        return r;
    }
	
	
	 /**
     * 项目依赖关系的动态字典
     */
    @ApiOperation(value = "项目依赖关系的字典接口", notes = "传入id获取唯一对应值，或传空获取所有值",hidden = true)
    @PostMapping("/dict")
    public List<PrjDep> dict(@RequestBody List<Long> ids) {
		List<PrjDep> data = prjDepService.listByIds(ids);
        return data;
	}
	
}