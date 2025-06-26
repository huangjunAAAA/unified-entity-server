package com.zjht.unified.controller ;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
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
import com.zjht.unified.common.core.util.UUID;
import com.zjht.unified.vo.ClazzDefVo;
import com.zjht.unified.wrapper.ClazzDefWrapper;
import com.zjht.unified.entity.ClazzDef;
import com.zjht.unified.service.IClazzDefService;

import com.zjht.unified.vo.ClazzDefCompositeVO;
import com.zjht.unified.dto.ClazzDefCompositeDTO;
import com.zjht.unified.wrapper.ClazzDefCompositeWrapper;
import com.zjht.unified.service.IClazzDefCompositeService;

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
@Api(value = "类定义(clazzDef)维护",tags = {"类定义(clazzDef)维护"})
@RestController
@RequestMapping("/clazzDef-composite")
public class ClazzDefCompositeController extends BaseController{

    private static final Logger logger = LoggerFactory.getLogger(ClazzDefController.class);
    @Autowired
    private IClazzDefService clazzDefService;
    @Autowired
    private IClazzDefCompositeService clazzDefCompositeService;

    /**
     * 查询类定义(clazzDef)列表, 对象形式
     */
    @ApiOperation(value = "查询类定义(clazzDef)列表")
    @PostMapping("/list-ext")
    public TableDataInfo<ClazzDefCompositeVO> listExt(@RequestBody BaseQueryDTO<ClazzDef> clazzDef)
    {
        return list(clazzDef.getCondition(),new PageDomain(clazzDef.getPage(),clazzDef.getSize()));
    }

    /**
     * 查询类定义(clazzDef)列表
     */
    @ApiOperation(value = "查询类定义(clazzDef)列表")
    @GetMapping("/list")
    public TableDataInfo<ClazzDefCompositeVO> list(ClazzDef clazzDef, PageDomain  pageDomain)
    {
        TableDataInfo<ClazzDefCompositeVO> dataInfo = new TableDataInfo();
        Page<ClazzDef> page = new Page<>(pageDomain.getPageNum(),pageDomain.getPageSize());
        IPage<ClazzDef> list = clazzDefService.page(page, Wrappers.<ClazzDef>lambdaQuery(clazzDef).orderByDesc(BaseEntity::getCreateTime));
        List<ClazzDefCompositeDTO> compositeLst=list.getRecords().stream()
                .map(t->clazzDefCompositeService.selectById(t.getId()))
                .collect(Collectors.toList());
        List<ClazzDefCompositeVO> rows = ClazzDefCompositeWrapper.build().entityVOList(compositeLst);
        dataInfo.setCode(Constants.SUCCESS);
        dataInfo.setData(rows);
        dataInfo.setMsg("查询成功");
        dataInfo.setTotal(list.getTotal());
        return dataInfo;
    }

    /**
     * 获取类定义(clazzDef)Composite详细信息
     */
    @ApiOperation(value = "获取类定义(clazzDef)Composite详细信息")
    @GetMapping(value = "/{id}")
    public R<ClazzDefCompositeVO> getInfo(@PathVariable("id") Long id)
    {
        ClazzDefCompositeDTO clazzDef = clazzDefCompositeService.selectById(id);
        return R.ok(ClazzDefCompositeWrapper.build().entityVO(clazzDef));
    }


    /**
     * 新增类定义(clazzDef)
     */
    @ApiOperation(value = "新增类定义(clazzDef)Composite")
    @PostMapping
    public R<Long> add(@RequestBody ClazzDefCompositeDTO clazzDef)
    {
        if(StringUtils.isBlank(clazzDef.getName())){
            return R.fail("类名不能为空");
        }
        if(StringUtils.isBlank(clazzDef.getNameZh())){
            return R.fail("类中文名不能为空");
        }
        if(StringUtils.isBlank(clazzDef.getGuid())){
            clazzDef.setGuid(UUID.fastUUID().toString());
        }
        if(clazzDef.getPrjId()==null){
            return R.fail("项目ID不能为空");
        }
        if(StringUtils.isBlank(clazzDef.getType())){
            clazzDef.setType(Constants.CLASS_TYPE_USER);
        }
        // 检查name或name_zh是否重复
        List<ClazzDef> tmp = clazzDefService.list(new LambdaQueryWrapper<ClazzDef>()
                .eq(ClazzDef::getName, clazzDef.getName())
                .eq(ClazzDef::getPrjId, clazzDef.getPrjId()));
        if(tmp.size()>0){
            return R.fail("类名已存在:"+clazzDef.getName());
        }
        List<ClazzDef> tmp2 = clazzDefService.list(new LambdaQueryWrapper<ClazzDef>()
                .eq(ClazzDef::getNameZh, clazzDef.getNameZh())
                .eq(ClazzDef::getPrjId, clazzDef.getPrjId()));
        if(tmp2.size()>0){
            return R.fail("类中文名已存在:"+clazzDef.getNameZh());
        }
        clazzDef.setId(null);
        clazzDef.setCreateTime(DateUtil.now());
        Long id = clazzDefCompositeService.submit(clazzDef);
        R r = id!=null ? R.ok(id) : R.fail();
        return r;
    }

    /**
     * 修改类定义(clazzDef)
     */
    @ApiOperation(value = "修改类定义(clazzDef)Composite")
    @PostMapping("/edit")
    public R<Long> edit(@RequestBody ClazzDefCompositeDTO clazzDef)
    {
        clazzDef.setUpdateTime(DateUtil.now());
        Long id = clazzDefCompositeService.submit(clazzDef);
        R r = id!=null ? R.ok(id) : R.fail();
        return r;
    }

    /**
     * 删除类定义(clazzDef)Composite
     */
    @ApiOperation(value = "删除类定义(clazzDef)Composite")
    @PostMapping("/delete/{id}")
    public R<Long> remove(@PathVariable Long id)
    {
        clazzDefCompositeService.removeById(id);
        return R.ok(id);
    }

}