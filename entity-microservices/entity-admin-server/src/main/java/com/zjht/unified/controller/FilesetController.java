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
import com.zjht.unified.vo.FilesetVo;
import com.zjht.unified.wrapper.FilesetWrapper;
import com.zjht.unified.entity.Fileset;
import com.zjht.unified.service.IFilesetService;
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
@Api(value = "UE文件列表维护",tags = {"UE文件列表维护"})
@RestController
@RequestMapping("/fileset")
public class FilesetController extends BaseController{

	private static final Logger logger = LoggerFactory.getLogger(FilesetController.class);
	@Autowired
    private IFilesetService filesetService;
	
	/**
     * 查询UE文件列表列表, 对象形式
     */
    @ApiOperation(value = "查询UE文件列表列表")
    @PostMapping("/list-ext")
    public TableDataInfo<FilesetVo> listExt(@RequestBody BaseQueryDTO<Fileset> fileset)
    {
        return list(fileset.getCondition(),new PageDomain(fileset.getPage(),fileset.getSize()));
    }
	
	/**
     * 查询UE文件列表列表
     */
    @ApiOperation(value = "查询UE文件列表列表")
    @GetMapping("/list")
    public TableDataInfo<FilesetVo> list(Fileset fileset, PageDomain  pageDomain)
    {
		TableDataInfo<FilesetVo> dataInfo = new TableDataInfo();
        Page<Fileset> page = new Page<>(pageDomain.getPageNum(),pageDomain.getPageSize());
        IPage<Fileset> list = filesetService.page(page, Wrappers.<Fileset>lambdaQuery(fileset).orderByDesc(BaseEntity::getCreateTime));
        IPage<FilesetVo> rows = FilesetWrapper.build().pageVO(list);
        dataInfo.setCode(Constants.SUCCESS);
        dataInfo.setData(rows.getRecords());
        dataInfo.setMsg("查询成功");
        dataInfo.setTotal(list.getTotal());
        return dataInfo;
    }

    /**
     * 获取UE文件列表详细信息
     */
    @ApiOperation(value = "获取UE文件列表详细信息")
    @GetMapping(value = "/{id}")
    public R<FilesetVo> getInfo(@PathVariable("id") Long id)
    {
        Fileset fileset = filesetService.getById(id);
        return R.ok(FilesetWrapper.build().entityVO(fileset));
    }


    /**
     * 新增UE文件列表
     */
    @ApiOperation(value = "新增UE文件列表")
    @PostMapping
    public R<Long> add(@RequestBody Fileset fileset)
    {
        fileset.setCreateTime(DateUtil.now());
        Boolean b = filesetService.save(fileset);
        R r = b ? R.ok(fileset.getId()) : R.fail();
        return r;
    }

    /**
     * 修改UE文件列表
     */
    @ApiOperation(value = "修改UE文件列表")
    @PostMapping("/edit")
    public R<Integer> edit(@RequestBody Fileset fileset)
    {
        fileset.setUpdateTime(DateUtil.now());
        Boolean b = filesetService.updateById(fileset);
        R r = b ? R.ok() : R.fail();
        return r;
    }

    /**
     * 删除UE文件列表
     */
    @ApiOperation(value = "删除UE文件列表")
	@PostMapping("/delete/{ids}")
    public R<Integer> remove(@PathVariable Long[] ids)
    {
        Boolean b = filesetService.removeByIds(Arrays.asList(ids));
        R r = b ? R.ok() : R.fail();
        return r;
    }
	
	
	 /**
     * UE文件列表的动态字典
     */
    @ApiOperation(value = "UE文件列表的字典接口", notes = "传入id获取唯一对应值，或传空获取所有值",hidden = true)
    @PostMapping("/dict")
    public List<Fileset> dict(@RequestBody List<Long> ids) {
		List<Fileset> data = filesetService.listByIds(ids);
        return data;
	}
	
}