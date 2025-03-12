package com.zjht.ui.controller ;

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
import com.zjht.ui.vo.GitStoreVo;
import com.zjht.ui.wrapper.GitStoreWrapper;
import com.zjht.ui.entity.GitStore;
import com.zjht.ui.service.IGitStoreService;

import com.zjht.ui.vo.GitStoreCompositeVO;
import com.zjht.ui.dto.GitStoreCompositeDTO;
import com.zjht.ui.wrapper.GitStoreCompositeWrapper;
import com.zjht.ui.service.IGitStoreCompositeService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 *  控制器
 *
 * @author wangy
 */
@Api(value = "项目与组件统一模型(gitStore)维护",tags = {"项目与组件统一模型(gitStore)维护"})
@RestController
@RequestMapping("/gitStore-composite")
public class GitStoreCompositeController extends BaseController{

    private static final Logger logger = LoggerFactory.getLogger(GitStoreController.class);
    @Autowired
    private IGitStoreService gitStoreService;
    @Autowired
    private IGitStoreCompositeService gitStoreCompositeService;

    /**
     * 查询项目与组件统一模型(gitStore)列表, 对象形式
     */
    @ApiOperation(value = "查询项目与组件统一模型(gitStore)列表")
    @PostMapping("/list-ext")
    public TableDataInfo<GitStoreCompositeVO> listExt(@RequestBody BaseQueryDTO<GitStore> gitStore)
    {
        return list(gitStore.getCondition(),new PageDomain(gitStore.getPage(),gitStore.getSize()));
    }

    /**
     * 查询项目与组件统一模型(gitStore)列表
     */
    @ApiOperation(value = "查询项目与组件统一模型(gitStore)列表")
    @GetMapping("/list")
    public TableDataInfo<GitStoreCompositeVO> list(GitStore gitStore, PageDomain  pageDomain)
    {
        TableDataInfo<GitStoreCompositeVO> dataInfo = new TableDataInfo();
        Page<GitStore> page = new Page<>(pageDomain.getPageNum(),pageDomain.getPageSize());
        IPage<GitStore> list = gitStoreService.page(page, Wrappers.<GitStore>lambdaQuery(gitStore).orderByDesc(BaseEntity::getCreateTime));
        List<GitStoreCompositeDTO> compositeLst=list.getRecords().stream()
                .map(t->gitStoreCompositeService.selectById(t.getId()))
                .collect(Collectors.toList());
        List<GitStoreCompositeVO> rows = GitStoreCompositeWrapper.build().entityVOList(compositeLst);
        dataInfo.setCode(Constants.SUCCESS);
        dataInfo.setData(rows);
        dataInfo.setMsg("查询成功");
        dataInfo.setTotal(list.getTotal());
        return dataInfo;
    }

    /**
     * 获取项目与组件统一模型(gitStore)Composite详细信息
     */
    @ApiOperation(value = "获取项目与组件统一模型(gitStore)Composite详细信息")
    @GetMapping(value = "/{id}")
    public R<GitStoreCompositeVO> getInfo(@PathVariable("id") Long id)
    {
        GitStoreCompositeDTO gitStore = gitStoreCompositeService.selectById(id);
        return R.ok(GitStoreCompositeWrapper.build().entityVO(gitStore));
    }


    /**
     * 新增项目与组件统一模型(gitStore)
     */
    @ApiOperation(value = "新增项目与组件统一模型(gitStore)Composite")
    @PostMapping
    public R<Long> add(@RequestBody GitStoreCompositeDTO gitStore)
    {
        gitStore.setId(null);
        gitStore.setCreateTime(DateUtil.now());
        Long id = gitStoreCompositeService.submit(gitStore);
        R r = id!=null ? R.ok(id) : R.fail();
        return r;
    }

    /**
     * 修改项目与组件统一模型(gitStore)
     */
    @ApiOperation(value = "修改项目与组件统一模型(gitStore)Composite")
    @PostMapping("/edit")
    public R<Long> edit(@RequestBody GitStoreCompositeDTO gitStore)
    {
        gitStore.setUpdateTime(DateUtil.now());
        Long id = gitStoreCompositeService.submit(gitStore);
        R r = id!=null ? R.ok(id) : R.fail();
        return r;
    }

    /**
     * 删除项目与组件统一模型(gitStore)Composite
     */
    @ApiOperation(value = "删除项目与组件统一模型(gitStore)Composite")
    @PostMapping("/delete/{id}")
    public R<Long> remove(@PathVariable Long id)
    {
        gitStoreCompositeService.removeById(id);
        return R.ok(id);
    }

}