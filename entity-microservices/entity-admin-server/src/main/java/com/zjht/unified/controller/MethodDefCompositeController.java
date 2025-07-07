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
import com.zjht.unified.dto.MethodParamCompositeDTO;
import com.zjht.unified.vo.MethodDefVo;
import com.zjht.unified.wrapper.MethodDefWrapper;
import com.zjht.unified.entity.MethodDef;
import com.zjht.unified.service.IMethodDefService;

import com.zjht.unified.vo.MethodDefCompositeVO;
import com.zjht.unified.dto.MethodDefCompositeDTO;
import com.zjht.unified.wrapper.MethodDefCompositeWrapper;
import com.zjht.unified.service.IMethodDefCompositeService;

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
@Api(value = "方法定义(methodDef)维护",tags = {"方法定义(methodDef)维护"})
@RestController
@RequestMapping("/methodDef-composite")
public class MethodDefCompositeController extends BaseController{

    private static final Logger logger = LoggerFactory.getLogger(MethodDefController.class);
    @Autowired
    private IMethodDefService methodDefService;
    @Autowired
    private IMethodDefCompositeService methodDefCompositeService;

    /**
     * 查询方法定义(methodDef)列表, 对象形式
     */
    @ApiOperation(value = "查询方法定义(methodDef)列表")
    @PostMapping("/list-ext")
    public TableDataInfo<MethodDefCompositeVO> listExt(@RequestBody BaseQueryDTO<MethodDef> methodDef)
    {
        return list(methodDef.getCondition(),new PageDomain(methodDef.getPage(),methodDef.getSize()));
    }

    /**
     * 查询方法定义(methodDef)列表
     */
    @ApiOperation(value = "查询方法定义(methodDef)列表")
    @GetMapping("/list")
    public TableDataInfo<MethodDefCompositeVO> list(MethodDef methodDef, PageDomain  pageDomain)
    {
        TableDataInfo<MethodDefCompositeVO> dataInfo = new TableDataInfo();
        Page<MethodDef> page = new Page<>(pageDomain.getPageNum(),pageDomain.getPageSize());
        IPage<MethodDef> list = methodDefService.page(page, Wrappers.<MethodDef>lambdaQuery(methodDef).orderByDesc(BaseEntity::getCreateTime));
        List<MethodDefCompositeDTO> compositeLst=list.getRecords().stream()
                .map(t->methodDefCompositeService.selectById(t.getId()))
                .collect(Collectors.toList());
        List<MethodDefCompositeVO> rows = MethodDefCompositeWrapper.build().entityVOList(compositeLst);
        dataInfo.setCode(Constants.SUCCESS);
        dataInfo.setData(rows);
        dataInfo.setMsg("查询成功");
        dataInfo.setTotal(list.getTotal());
        return dataInfo;
    }

    /**
     * 获取方法定义(methodDef)Composite详细信息
     */
    @ApiOperation(value = "获取方法定义(methodDef)Composite详细信息")
    @GetMapping(value = "/{id}")
    public R<MethodDefCompositeVO> getInfo(@PathVariable("id") Long id)
    {
        MethodDefCompositeDTO methodDef = methodDefCompositeService.selectById(id);
        return R.ok(MethodDefCompositeWrapper.build().entityVO(methodDef));
    }


    /**
     * 新增方法定义(methodDef)
     */
    @ApiOperation(value = "新增方法定义(methodDef)Composite")
    @PostMapping
    public R<Long> add(@RequestBody MethodDefCompositeDTO methodDef)
    {
        if(StringUtils.isBlank(methodDef.getName())){
            return R.fail("方法名不能为空");
        }
        if(StringUtils.isValidVar(methodDef.getName())){
            return R.fail("方法名格式错误");
        }
        if(StringUtils.isBlank(methodDef.getBody())){
            return R.fail("方法体不能为空");
        }
        if(StringUtils.isBlank(methodDef.getModifier())){
            return R.fail("private/public/protected,不能为空");
        }
        if(null==methodDef.getType()){
            return R.fail("1 构造方法，2 普通方法,不能为空");
        }
        if(null!=methodDef.getMethodIdMethodParamList()){
            for(MethodParamCompositeDTO param:methodDef.getMethodIdMethodParamList()){
                if(StringUtils.isBlank(param.getName())){
                    return R.fail("参数名称不能为空");
                }
                if(StringUtils.isValidVar(param.getName())){
                    return R.fail("参数名称格式错误");
                }
                if(StringUtils.isBlank(param.getType())){
                    return R.fail("参数类型不能为空");
                }
            }
        }


        methodDef.setId(null);
        methodDef.setCreateTime(DateUtil.now());
        Long id = methodDefCompositeService.submit(methodDef);
        R r = id!=null ? R.ok(id) : R.fail();
        return r;
    }

    /**
     * 修改方法定义(methodDef)
     */
    @ApiOperation(value = "修改方法定义(methodDef)Composite")
    @PostMapping("/edit")
    public R<Long> edit(@RequestBody MethodDefCompositeDTO methodDef)
    {
        methodDef.setUpdateTime(DateUtil.now());
        Long id = methodDefCompositeService.submit(methodDef);
        R r = id!=null ? R.ok(id) : R.fail();
        return r;
    }

    /**
     * 删除方法定义(methodDef)Composite
     */
    @ApiOperation(value = "删除方法定义(methodDef)Composite")
    @PostMapping("/delete/{id}")
    public R<Long> remove(@PathVariable Long id)
    {
        methodDefCompositeService.removeById(id);
        return R.ok(id);
    }

}