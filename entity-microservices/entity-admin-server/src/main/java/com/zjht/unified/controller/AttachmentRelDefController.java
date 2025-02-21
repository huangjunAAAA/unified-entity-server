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
import com.zjht.unified.vo.AttachmentRelDefVo;
import com.zjht.unified.wrapper.AttachmentRelDefWrapper;
import com.zjht.unified.entity.AttachmentRelDef;
import com.zjht.unified.service.IAttachmentRelDefService;
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
@Api(value = "挂载关系维护",tags = {"挂载关系维护"})
@RestController
@RequestMapping("/attachmentRelDef")
public class AttachmentRelDefController extends BaseController{

	private static final Logger logger = LoggerFactory.getLogger(AttachmentRelDefController.class);
	@Autowired
    private IAttachmentRelDefService attachmentRelDefService;
	
	/**
     * 查询挂载关系列表, 对象形式
     */
    @ApiOperation(value = "查询挂载关系列表")
    @PostMapping("/list-ext")
    public TableDataInfo<AttachmentRelDefVo> listExt(@RequestBody BaseQueryDTO<AttachmentRelDef> attachmentRelDef)
    {
        return list(attachmentRelDef.getCondition(),new PageDomain(attachmentRelDef.getPage(),attachmentRelDef.getSize()));
    }
	
	/**
     * 查询挂载关系列表
     */
    @ApiOperation(value = "查询挂载关系列表")
    @GetMapping("/list")
    public TableDataInfo<AttachmentRelDefVo> list(AttachmentRelDef attachmentRelDef, PageDomain  pageDomain)
    {
		TableDataInfo<AttachmentRelDefVo> dataInfo = new TableDataInfo();
        Page<AttachmentRelDef> page = new Page<>(pageDomain.getPageNum(),pageDomain.getPageSize());
        IPage<AttachmentRelDef> list = attachmentRelDefService.page(page, Wrappers.<AttachmentRelDef>lambdaQuery(attachmentRelDef).orderByDesc(BaseEntity::getCreateTime));
        IPage<AttachmentRelDefVo> rows = AttachmentRelDefWrapper.build().pageVO(list);
        dataInfo.setCode(Constants.SUCCESS);
        dataInfo.setData(rows.getRecords());
        dataInfo.setMsg("查询成功");
        dataInfo.setTotal(list.getTotal());
        return dataInfo;
    }

    /**
     * 获取挂载关系详细信息
     */
    @ApiOperation(value = "获取挂载关系详细信息")
    @GetMapping(value = "/{id}")
    public R<AttachmentRelDefVo> getInfo(@PathVariable("id") Long id)
    {
        AttachmentRelDef attachmentRelDef = attachmentRelDefService.getById(id);
        return R.ok(AttachmentRelDefWrapper.build().entityVO(attachmentRelDef));
    }


    /**
     * 新增挂载关系
     */
    @ApiOperation(value = "新增挂载关系")
    @PostMapping
    public R<Long> add(@RequestBody AttachmentRelDef attachmentRelDef)
    {
        attachmentRelDef.setCreateTime(DateUtil.now());
        Boolean b = attachmentRelDefService.save(attachmentRelDef);
        R r = b ? R.ok(attachmentRelDef.getId()) : R.fail();
        return r;
    }

    /**
     * 修改挂载关系
     */
    @ApiOperation(value = "修改挂载关系")
    @PostMapping("/edit")
    public R<Integer> edit(@RequestBody AttachmentRelDef attachmentRelDef)
    {
        attachmentRelDef.setUpdateTime(DateUtil.now());
        Boolean b = attachmentRelDefService.updateById(attachmentRelDef);
        R r = b ? R.ok() : R.fail();
        return r;
    }

    /**
     * 删除挂载关系
     */
    @ApiOperation(value = "删除挂载关系")
	@PostMapping("/delete/{ids}")
    public R<Integer> remove(@PathVariable Long[] ids)
    {
        Boolean b = attachmentRelDefService.removeByIds(Arrays.asList(ids));
        R r = b ? R.ok() : R.fail();
        return r;
    }
	
	
	 /**
     * 挂载关系的动态字典
     */
    @ApiOperation(value = "挂载关系的字典接口", notes = "传入id获取唯一对应值，或传空获取所有值",hidden = true)
    @PostMapping("/dict")
    public List<AttachmentRelDef> dict(@RequestBody List<Long> ids) {
		List<AttachmentRelDef> data = attachmentRelDefService.listByIds(ids);
        return data;
	}
	
}