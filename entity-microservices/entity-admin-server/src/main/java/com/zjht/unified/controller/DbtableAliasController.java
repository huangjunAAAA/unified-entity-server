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
import com.zjht.unified.vo.DbtableAliasVo;
import com.zjht.unified.wrapper.DbtableAliasWrapper;
import com.zjht.unified.entity.DbtableAlias;
import com.zjht.unified.service.IDbtableAliasService;
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
@Api(value = "数据库表类维护",tags = {"数据库表类维护"})
@RestController
@RequestMapping("/dbtableAlias")
public class DbtableAliasController extends BaseController{

	private static final Logger logger = LoggerFactory.getLogger(DbtableAliasController.class);
	@Autowired
    private IDbtableAliasService dbtableAliasService;
	
	/**
     * 查询数据库表类列表, 对象形式
     */
    @ApiOperation(value = "查询数据库表类列表")
    @PostMapping("/list-ext")
    public TableDataInfo<DbtableAliasVo> listExt(@RequestBody BaseQueryDTO<DbtableAlias> dbtableAlias)
    {
        return list(dbtableAlias.getCondition(),new PageDomain(dbtableAlias.getPage(),dbtableAlias.getSize()));
    }
	
	/**
     * 查询数据库表类列表
     */
    @ApiOperation(value = "查询数据库表类列表")
    @GetMapping("/list")
    public TableDataInfo<DbtableAliasVo> list(DbtableAlias dbtableAlias, PageDomain  pageDomain)
    {
		TableDataInfo<DbtableAliasVo> dataInfo = new TableDataInfo();
        Page<DbtableAlias> page = new Page<>(pageDomain.getPageNum(),pageDomain.getPageSize());
        IPage<DbtableAlias> list = dbtableAliasService.page(page, Wrappers.<DbtableAlias>lambdaQuery(dbtableAlias).orderByDesc(BaseEntity::getCreateTime));
        IPage<DbtableAliasVo> rows = DbtableAliasWrapper.build().pageVO(list);
        dataInfo.setCode(Constants.SUCCESS);
        dataInfo.setData(rows.getRecords());
        dataInfo.setMsg("查询成功");
        dataInfo.setTotal(list.getTotal());
        return dataInfo;
    }

    /**
     * 获取数据库表类详细信息
     */
    @ApiOperation(value = "获取数据库表类详细信息")
    @GetMapping(value = "/{id}")
    public R<DbtableAliasVo> getInfo(@PathVariable("id") Long id)
    {
        DbtableAlias dbtableAlias = dbtableAliasService.getById(id);
        return R.ok(DbtableAliasWrapper.build().entityVO(dbtableAlias));
    }


    /**
     * 新增数据库表类
     */
    @ApiOperation(value = "新增数据库表类")
    @PostMapping
    public R<Long> add(@RequestBody DbtableAlias dbtableAlias)
    {
        dbtableAlias.setCreateTime(DateUtil.now());
        Boolean b = dbtableAliasService.save(dbtableAlias);
        R r = b ? R.ok(dbtableAlias.getId()) : R.fail();
        return r;
    }

    /**
     * 修改数据库表类
     */
    @ApiOperation(value = "修改数据库表类")
    @PostMapping("/edit")
    public R<Integer> edit(@RequestBody DbtableAlias dbtableAlias)
    {
        dbtableAlias.setUpdateTime(DateUtil.now());
        Boolean b = dbtableAliasService.updateById(dbtableAlias);
        R r = b ? R.ok() : R.fail();
        return r;
    }

    /**
     * 删除数据库表类
     */
    @ApiOperation(value = "删除数据库表类")
	@PostMapping("/delete/{ids}")
    public R<Integer> remove(@PathVariable Long[] ids)
    {
        Boolean b = dbtableAliasService.removeByIds(Arrays.asList(ids));
        R r = b ? R.ok() : R.fail();
        return r;
    }
	
	
	 /**
     * 数据库表类的动态字典
     */
    @ApiOperation(value = "数据库表类的字典接口", notes = "传入id获取唯一对应值，或传空获取所有值",hidden = true)
    @PostMapping("/dict")
    public List<DbtableAlias> dict(@RequestBody List<Long> ids) {
		List<DbtableAlias> data = dbtableAliasService.listByIds(ids);
        return data;
	}
	
}