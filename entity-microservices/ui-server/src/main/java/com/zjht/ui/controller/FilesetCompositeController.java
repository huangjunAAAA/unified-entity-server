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
import com.zjht.ui.vo.FilesetVo;
import com.zjht.ui.wrapper.FilesetWrapper;
import com.zjht.ui.entity.Fileset;
import com.zjht.ui.service.IFilesetService;

import com.zjht.ui.vo.FilesetCompositeVO;
import com.zjht.ui.dto.FilesetCompositeDTO;
import com.zjht.ui.wrapper.FilesetCompositeWrapper;
import com.zjht.ui.service.IFilesetCompositeService;

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
@Api(value = "项目与组件统一模型(fileset)维护",tags = {"项目与组件统一模型(fileset)维护"})
@RestController
@RequestMapping("/fileset-composite")
public class FilesetCompositeController extends BaseController{

    private static final Logger logger = LoggerFactory.getLogger(FilesetController.class);
    @Autowired
    private IFilesetService filesetService;
    @Autowired
    private IFilesetCompositeService filesetCompositeService;

    /**
     * 查询项目与组件统一模型(fileset)列表, 对象形式
     */
    @ApiOperation(value = "查询项目与组件统一模型(fileset)列表")
    @PostMapping("/list-ext")
    public TableDataInfo<FilesetCompositeVO> listExt(@RequestBody BaseQueryDTO<Fileset> fileset)
    {
        return list(fileset.getCondition(),new PageDomain(fileset.getPage(),fileset.getSize()));
    }

    /**
     * 查询项目与组件统一模型(fileset)列表
     */
    @ApiOperation(value = "查询项目与组件统一模型(fileset)列表")
    @GetMapping("/list")
    public TableDataInfo<FilesetCompositeVO> list(Fileset fileset, PageDomain  pageDomain)
    {
        TableDataInfo<FilesetCompositeVO> dataInfo = new TableDataInfo();
        Page<Fileset> page = new Page<>(pageDomain.getPageNum(),pageDomain.getPageSize());
        IPage<Fileset> list = filesetService.page(page, Wrappers.<Fileset>lambdaQuery(fileset).orderByDesc(BaseEntity::getCreateTime));
        List<FilesetCompositeDTO> compositeLst=list.getRecords().stream()
                .map(t->filesetCompositeService.selectById(t.getId()))
                .collect(Collectors.toList());
        List<FilesetCompositeVO> rows = FilesetCompositeWrapper.build().entityVOList(compositeLst);
        dataInfo.setCode(Constants.SUCCESS);
        dataInfo.setData(rows);
        dataInfo.setMsg("查询成功");
        dataInfo.setTotal(list.getTotal());
        return dataInfo;
    }

    /**
     * 获取项目与组件统一模型(fileset)Composite详细信息
     */
    @ApiOperation(value = "获取项目与组件统一模型(fileset)Composite详细信息")
    @GetMapping(value = "/{id}")
    public R<FilesetCompositeVO> getInfo(@PathVariable("id") Long id)
    {
        FilesetCompositeDTO fileset = filesetCompositeService.selectById(id);
        return R.ok(FilesetCompositeWrapper.build().entityVO(fileset));
    }


    /**
     * 新增项目与组件统一模型(fileset)
     */
    @ApiOperation(value = "新增项目与组件统一模型(fileset)Composite")
    @PostMapping
    public R<Long> add(@RequestBody FilesetCompositeDTO fileset)
    {
        fileset.setId(null);
        fileset.setCreateTime(DateUtil.now());
        Long id = filesetCompositeService.submit(fileset);
        R r = id!=null ? R.ok(id) : R.fail();
        return r;
    }

    /**
     * 修改项目与组件统一模型(fileset)
     */
    @ApiOperation(value = "修改项目与组件统一模型(fileset)Composite")
    @PostMapping("/edit")
    public R<Long> edit(@RequestBody FilesetCompositeDTO fileset)
    {
        fileset.setUpdateTime(DateUtil.now());
        Long id = filesetCompositeService.submit(fileset);
        R r = id!=null ? R.ok(id) : R.fail();
        return r;
    }

    /**
     * 删除项目与组件统一模型(fileset)Composite
     */
    @ApiOperation(value = "删除项目与组件统一模型(fileset)Composite")
    @PostMapping("/delete/{id}")
    public R<Long> remove(@PathVariable Long id)
    {
        filesetCompositeService.removeById(id);
        return R.ok(id);
    }

}