package com.zjht.ui.controller ;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wukong.core.weblog.utils.DateUtil;

import com.wukong.core.mp.base.BaseEntity;
import com.zjht.ui.vo.GitStoreVo;
import com.zjht.ui.wrapper.GitStoreWrapper;
import com.zjht.ui.entity.GitStore;
import com.zjht.ui.service.IGitStoreService;
import com.zjht.unified.common.core.constants.Constants;
import com.zjht.unified.common.core.controller.BaseController;
import com.zjht.unified.common.core.domain.PageDomain;
import com.zjht.unified.common.core.domain.R;
import com.zjht.unified.common.core.domain.TableDataInfo;
import com.zjht.unified.common.core.domain.dto.BaseQueryDTO;
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
@Api(value = "git表维护",tags = {"git表维护"})
@RestController
@RequestMapping("/gitStore")
public class GitStoreController extends BaseController {

	private static final Logger logger = LoggerFactory.getLogger(GitStoreController.class);
	@Autowired
    private IGitStoreService gitStoreService;
	
	/**
     * 查询git表列表, 对象形式
     */
    @ApiOperation(value = "查询git表列表")
    @PostMapping("/list-ext")
    public TableDataInfo<GitStoreVo> listExt(@RequestBody BaseQueryDTO<GitStore> gitStore)
    {
        return list(gitStore.getCondition(),new PageDomain(gitStore.getPage(),gitStore.getSize()));
    }
	
	/**
     * 查询git表列表
     */
    @ApiOperation(value = "查询git表列表")
    @GetMapping("/list")
    public TableDataInfo<GitStoreVo> list(GitStore gitStore, PageDomain  pageDomain)
    {
		TableDataInfo<GitStoreVo> dataInfo = new TableDataInfo();
        Page<GitStore> page = new Page<>(pageDomain.getPageNum(),pageDomain.getPageSize());
        IPage<GitStore> list = gitStoreService.page(page, Wrappers.<GitStore>lambdaQuery(gitStore).orderByDesc(BaseEntity::getCreateTime));
        IPage<GitStoreVo> rows = GitStoreWrapper.build().pageVO(list);
        dataInfo.setCode(Constants.SUCCESS);
        dataInfo.setData(rows.getRecords());
        dataInfo.setMsg("查询成功");
        dataInfo.setTotal(list.getTotal());
        return dataInfo;
    }

    /**
     * 获取git表详细信息
     */
    @ApiOperation(value = "获取git表详细信息")
    @GetMapping(value = "/{id}")
    public R<GitStoreVo> getInfo(@PathVariable("id") Long id)
    {
        GitStore gitStore = gitStoreService.getById(id);
        return R.ok(GitStoreWrapper.build().entityVO(gitStore));
    }


    /**
     * 新增git表
     */
    @ApiOperation(value = "新增git表")
    @PostMapping
    public R<Long> add(@RequestBody GitStore gitStore)
    {
        gitStore.setCreateTime(DateUtil.now());
        Boolean b = gitStoreService.save(gitStore);
        R r = b ? R.ok(gitStore.getId()) : R.fail();
        return r;
    }

    /**
     * 修改git表
     */
    @ApiOperation(value = "修改git表")
    @PostMapping("/edit")
    public R<Integer> edit(@RequestBody GitStore gitStore)
    {
        gitStore.setUpdateTime(DateUtil.now());
        Boolean b = gitStoreService.updateById(gitStore);
        R r = b ? R.ok() : R.fail();
        return r;
    }

    /**
     * 删除git表
     */
    @ApiOperation(value = "删除git表")
	@PostMapping("/delete/{ids}")
    public R<Integer> remove(@PathVariable Long[] ids)
    {
        Boolean b = gitStoreService.removeByIds(Arrays.asList(ids));
        R r = b ? R.ok() : R.fail();
        return r;
    }
	
	
	 /**
     * git表的动态字典
     */
    @ApiOperation(value = "git表的字典接口", notes = "传入id获取唯一对应值，或传空获取所有值",hidden = true)
    @PostMapping("/dict")
    public List<GitStore> dict(@RequestBody List<Long> ids) {
		List<GitStore> data = gitStoreService.listByIds(ids);
        return data;
	}
	
}