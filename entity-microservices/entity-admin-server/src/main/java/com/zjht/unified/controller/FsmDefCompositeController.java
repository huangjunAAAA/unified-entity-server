package com.zjht.unified.controller ;

import cn.hutool.core.util.NumberUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
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
import com.zjht.unified.common.core.domain.dto.ConditionLikeAndIn;
import com.zjht.unified.dto.FsmDefListDTO;
import com.zjht.unified.vo.FsmDefVo;
import com.zjht.unified.wrapper.FsmDefWrapper;
import com.zjht.unified.entity.FsmDef;
import com.zjht.unified.service.IFsmDefService;

import com.zjht.unified.vo.FsmDefCompositeVO;
import com.zjht.unified.dto.FsmDefCompositeDTO;
import com.zjht.unified.wrapper.FsmDefCompositeWrapper;
import com.zjht.unified.service.IFsmDefCompositeService;

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
@Api(value = "状态机定义(fsmDef)维护",tags = {"状态机定义(fsmDef)维护"})
@RestController
@RequestMapping("/fsmDef-composite")
public class FsmDefCompositeController extends BaseController{

    private static final Logger logger = LoggerFactory.getLogger(FsmDefController.class);
    @Autowired
    private IFsmDefService fsmDefService;
    @Autowired
    private IFsmDefCompositeService fsmDefCompositeService;

    /**
     * 查询状态机定义(fsmDef)列表, 对象形式
     */
    @ApiOperation(value = "查询状态机定义(fsmDef)列表")
    @PostMapping("/list-ext")
    public TableDataInfo<FsmDefCompositeVO> listExt(@RequestBody BaseQueryDTO<FsmDef> fsmDef)
    {
        return list(fsmDef.getCondition(),new PageDomain(fsmDef.getPage(),fsmDef.getSize()));
    }

    /**
     * 查询状态机定义(fsmDef)列表
     */
    @ApiOperation(value = "查询状态机定义(fsmDef)列表")
    @GetMapping("/list")
    public TableDataInfo<FsmDefCompositeVO> list(FsmDef fsmDef, PageDomain  pageDomain)
    {
        TableDataInfo<FsmDefCompositeVO> dataInfo = new TableDataInfo();
        Page<FsmDef> page = new Page<>(pageDomain.getPageNum(),pageDomain.getPageSize());
        IPage<FsmDef> list = fsmDefService.page(page, Wrappers.<FsmDef>lambdaQuery(fsmDef).orderByDesc(BaseEntity::getCreateTime));
        List<FsmDefCompositeDTO> compositeLst=list.getRecords().stream()
                .map(t->fsmDefCompositeService.selectById(t.getId()))
                .collect(Collectors.toList());
        List<FsmDefCompositeVO> rows = FsmDefCompositeWrapper.build().entityVOList(compositeLst);
        dataInfo.setCode(Constants.SUCCESS);
        dataInfo.setData(rows);
        dataInfo.setMsg("查询成功");
        dataInfo.setTotal(list.getTotal());
        return dataInfo;
    }

    /**
     * 查询状态机定义(fsmDef)列表，接受like和in条件
     */
    @ApiOperation(value = "查询状态机定义(fsmDef)列表，接受like和in条件")
    @PostMapping("/list-like-in")
    public TableDataInfo<FsmDefCompositeVO> listExt2(@RequestBody BaseQueryDTO<ConditionLikeAndIn<FsmDef, FsmDefListDTO>> baseQueryDTO) {
        // 获取参数
        ConditionLikeAndIn<FsmDef, FsmDefListDTO> condition = baseQueryDTO.getCondition();
        FsmDef equalsCondition = condition.getEquals();
        FsmDef likeCondition = condition.getLike();
        FsmDefListDTO inCondition = condition.getInCondition();

        // 初始化分页信息
        Page<FsmDef> page = new Page<>(baseQueryDTO.getPage(), baseQueryDTO.getSize());

        // 构建查询条件
        LambdaQueryWrapper<FsmDef> queryWrapper = Wrappers.<FsmDef>lambdaQuery();

        // 处理 equals 条件：精确匹配
        if (equalsCondition != null) {
            if (equalsCondition.getId() != null) {
                queryWrapper.eq(FsmDef::getId, equalsCondition.getId());
            }
            if (equalsCondition.getGuid() != null) {
                queryWrapper.eq(FsmDef::getGuid, equalsCondition.getGuid());
            }
            if (equalsCondition.getName() != null) {
                queryWrapper.eq(FsmDef::getName, equalsCondition.getName());
            }
            if (equalsCondition.getDriver() != null) {
                queryWrapper.eq(FsmDef::getDriver, equalsCondition.getDriver());
            }
            if (equalsCondition.getCron() != null) {
                queryWrapper.eq(FsmDef::getCron, equalsCondition.getCron());
            }
            if (equalsCondition.getConcurrent() != null) {
                queryWrapper.eq(FsmDef::getConcurrent, equalsCondition.getConcurrent());
            }
            if (equalsCondition.getAbort() != null) {
                queryWrapper.eq(FsmDef::getAbort, equalsCondition.getAbort());
            }
            if (equalsCondition.getInitialState() != null) {
                queryWrapper.eq(FsmDef::getInitialState, equalsCondition.getInitialState());
            }
        }

        // 处理 like 条件：模糊匹配
        if (likeCondition != null) {
            if (likeCondition.getName() != null) {
                queryWrapper.like(FsmDef::getName, likeCondition.getName());
            }
            if (likeCondition.getCron() != null) {
                queryWrapper.like(FsmDef::getCron, likeCondition.getCron());
            }
            if (likeCondition.getAbort() != null) {
                queryWrapper.like(FsmDef::getAbort, likeCondition.getAbort());
            }
            if (likeCondition.getInitialState() != null) {
                queryWrapper.like(FsmDef::getInitialState, likeCondition.getInitialState());
            }
        }

        // 处理 inCondition 条件：IN 查询
        if (inCondition != null) {
            if (inCondition.getId() != null && !inCondition.getId().isEmpty()) {
                queryWrapper.in(FsmDef::getId, inCondition.getId());
            }
            if (inCondition.getName() != null && !inCondition.getName().isEmpty()) {
                queryWrapper.in(FsmDef::getName, inCondition.getName());
            }
            if (inCondition.getDriver() != null && !inCondition.getDriver().isEmpty()) {
                queryWrapper.in(FsmDef::getDriver, inCondition.getDriver());
            }
            if (inCondition.getCron() != null && !inCondition.getCron().isEmpty()) {
                queryWrapper.in(FsmDef::getCron, inCondition.getCron());
            }
            if (inCondition.getConcurrent() != null && !inCondition.getConcurrent().isEmpty()) {
                queryWrapper.in(FsmDef::getConcurrent, inCondition.getConcurrent());
            }
            if (inCondition.getAbort() != null && !inCondition.getAbort().isEmpty()) {
                queryWrapper.in(FsmDef::getAbort, inCondition.getAbort());
            }
            if (inCondition.getInitialState() != null && !inCondition.getInitialState().isEmpty()) {
                queryWrapper.in(FsmDef::getInitialState, inCondition.getInitialState());
            }
            if (inCondition.getGuid() != null && !inCondition.getGuid().isEmpty()) {
                queryWrapper.in(FsmDef::getGuid, inCondition.getGuid());
            }
        }

        // 执行查询
        IPage<FsmDef> fsmDefIPage = fsmDefService.page(page, queryWrapper);

        // 转换为 VO
        List<FsmDefCompositeDTO> compositeLst = fsmDefIPage.getRecords().stream()
                .map(t -> fsmDefCompositeService.selectById(t.getId()))
                .collect(Collectors.toList());
        List<FsmDefCompositeVO> rows = FsmDefCompositeWrapper.build().entityVOList(compositeLst);

        // 封装返回结果
        TableDataInfo<FsmDefCompositeVO> dataInfo = new TableDataInfo<>();
        dataInfo.setCode(Constants.SUCCESS);
        dataInfo.setData(rows);
        dataInfo.setMsg("查询成功");
        dataInfo.setTotal(fsmDefIPage.getTotal());

        return dataInfo;
    }

    /**
     * 获取状态机定义(fsmDef)Composite详细信息
     */
    @ApiOperation(value = "获取状态机定义(fsmDef)Composite详细信息")
    @GetMapping(value = "/{id}")
    public R<FsmDefCompositeVO> getInfo(@PathVariable("id") String id)
    {
        if(NumberUtil.isNumber(id)) {
            FsmDefCompositeDTO fsmDef = fsmDefCompositeService.selectById(Long.parseLong(id));
            return R.ok(FsmDefCompositeWrapper.build().entityVO(fsmDef));
        } else {
            FsmDefCompositeDTO param = new FsmDefCompositeDTO();
            param.setGuid(id);
            FsmDefCompositeDTO fsmDef = fsmDefCompositeService.selectOne(param);
            return R.ok(FsmDefCompositeWrapper.build().entityVO(fsmDef));
        }
    }


    /**
     * 新增状态机定义(fsmDef)
     */
    @ApiOperation(value = "新增状态机定义(fsmDef)Composite")
    @PostMapping
    public R<Long> add(@RequestBody FsmDefCompositeDTO fsmDef)
    {
        fsmDef.setId(null);
        fsmDef.setCreateTime(DateUtil.now());
        Long id = fsmDefCompositeService.submit(fsmDef);
        R r = id!=null ? R.ok(id) : R.fail();
        return r;
    }

    /**
     * 修改状态机定义(fsmDef)
     */
    @ApiOperation(value = "修改状态机定义(fsmDef)Composite")
    @PostMapping("/edit")
    public R<Long> edit(@RequestBody FsmDefCompositeDTO fsmDef)
    {
        fsmDef.setUpdateTime(DateUtil.now());
        Long id = fsmDefCompositeService.submit(fsmDef);
        R r = id!=null ? R.ok(id) : R.fail();
        return r;
    }

    /**
     * 删除状态机定义(fsmDef)Composite
     */
    @ApiOperation(value = "删除状态机定义(fsmDef)Composite")
    @PostMapping("/delete/{id}")
    public R<String> remove(@PathVariable String id)
    {
        if(NumberUtil.isNumber(id)) {
            fsmDefCompositeService.removeById(Long.parseLong(id));
        } else {
            fsmDefService.remove(new LambdaQueryWrapper<FsmDef>().eq(FsmDef::getGuid, id));
        }
        return R.ok(id);
    }

}