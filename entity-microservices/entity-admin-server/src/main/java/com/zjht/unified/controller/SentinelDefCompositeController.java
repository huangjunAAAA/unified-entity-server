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
import com.zjht.unified.dto.SentinelDefListDTO;
import com.zjht.unified.vo.SentinelDefVo;
import com.zjht.unified.wrapper.SentinelDefWrapper;
import com.zjht.unified.entity.SentinelDef;
import com.zjht.unified.service.ISentinelDefService;

import com.zjht.unified.vo.SentinelDefCompositeVO;
import com.zjht.unified.dto.SentinelDefCompositeDTO;
import com.zjht.unified.wrapper.SentinelDefCompositeWrapper;
import com.zjht.unified.service.ISentinelDefCompositeService;

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
@Api(value = "哨兵定义(sentinelDef)维护",tags = {"哨兵定义(sentinelDef)维护"})
@RestController
@RequestMapping("/sentinelDef-composite")
public class SentinelDefCompositeController extends BaseController{

    private static final Logger logger = LoggerFactory.getLogger(SentinelDefController.class);
    @Autowired
    private ISentinelDefService sentinelDefService;
    @Autowired
    private ISentinelDefCompositeService sentinelDefCompositeService;

    /**
     * 查询哨兵定义(sentinelDef)列表, 对象形式
     */
    @ApiOperation(value = "查询哨兵定义(sentinelDef)列表")
    @PostMapping("/list-ext")
    public TableDataInfo<SentinelDefCompositeVO> listExt(@RequestBody BaseQueryDTO<SentinelDef> sentinelDef)
    {
        return list(sentinelDef.getCondition(),new PageDomain(sentinelDef.getPage(),sentinelDef.getSize()));
    }

    /**
     * 查询哨兵定义(sentinelDef)列表
     */
    @ApiOperation(value = "查询哨兵定义(sentinelDef)列表")
    @GetMapping("/list")
    public TableDataInfo<SentinelDefCompositeVO> list(SentinelDef sentinelDef, PageDomain  pageDomain)
    {
        TableDataInfo<SentinelDefCompositeVO> dataInfo = new TableDataInfo();
        Page<SentinelDef> page = new Page<>(pageDomain.getPageNum(),pageDomain.getPageSize());
        IPage<SentinelDef> list = sentinelDefService.page(page, Wrappers.<SentinelDef>lambdaQuery(sentinelDef).orderByDesc(BaseEntity::getCreateTime));
        List<SentinelDefCompositeDTO> compositeLst=list.getRecords().stream()
                .map(t->sentinelDefCompositeService.selectById(t.getId()))
                .collect(Collectors.toList());
        List<SentinelDefCompositeVO> rows = SentinelDefCompositeWrapper.build().entityVOList(compositeLst);
        dataInfo.setCode(Constants.SUCCESS);
        dataInfo.setData(rows);
        dataInfo.setMsg("查询成功");
        dataInfo.setTotal(list.getTotal());
        return dataInfo;
    }

    /**
     * 查询哨兵定义(sentinelDef)列表，接受like和in条件
     */
    @ApiOperation(value = "查询哨兵定义(sentinelDef)列表，接受like和in条件")
    @PostMapping("/list-like-in")
    public TableDataInfo<SentinelDefCompositeVO> listExt2(@RequestBody BaseQueryDTO<ConditionLikeAndIn<SentinelDef, SentinelDefListDTO>> baseQueryDTO) {
        // 获取参数
        ConditionLikeAndIn<SentinelDef, SentinelDefListDTO> condition = baseQueryDTO.getCondition();
        SentinelDef equalsCondition = condition.getEquals();
        SentinelDef likeCondition = condition.getLike();
        SentinelDefListDTO inCondition = condition.getInCondition();

        // 初始化分页信息
        Page<SentinelDef> page = new Page<>(baseQueryDTO.getPage(), baseQueryDTO.getSize());

        // 构建查询条件
        LambdaQueryWrapper<SentinelDef> queryWrapper = Wrappers.<SentinelDef>lambdaQuery();

        // 处理 equals 条件：精确匹配
        if (equalsCondition != null) {
            if (equalsCondition.getId() != null) {
                queryWrapper.eq(SentinelDef::getId, equalsCondition.getId());
            }
            if (equalsCondition.getPrjId() != null) {
                queryWrapper.eq(SentinelDef::getPrjId, equalsCondition.getPrjId());
            }
            if (equalsCondition.getGuid()!= null){
                queryWrapper.eq(SentinelDef::getGuid, equalsCondition.getGuid());
            }
            if (equalsCondition.getName() != null) {
                queryWrapper.eq(SentinelDef::getName, equalsCondition.getName());
            }
            if (equalsCondition.getBody() != null) {
                queryWrapper.eq(SentinelDef::getBody, equalsCondition.getBody());
            }
            if (equalsCondition.getCron() != null) {
                queryWrapper.eq(SentinelDef::getCron, equalsCondition.getCron());
            }
            if (equalsCondition.getConcurrent() != null) {
                queryWrapper.eq(SentinelDef::getConcurrent, equalsCondition.getConcurrent());
            }
            if (equalsCondition.getAbort() != null) {
                queryWrapper.eq(SentinelDef::getAbort, equalsCondition.getAbort());
            }
        }

        // 处理 like 条件：模糊匹配
        if (likeCondition != null) {
            if (likeCondition.getName() != null) {
                queryWrapper.like(SentinelDef::getName, likeCondition.getName());
            }
            if (likeCondition.getBody() != null) {
                queryWrapper.like(SentinelDef::getBody, likeCondition.getBody());
            }
            if (likeCondition.getCron() != null) {
                queryWrapper.like(SentinelDef::getCron, likeCondition.getCron());
            }
            if (likeCondition.getAbort() != null) {
                queryWrapper.like(SentinelDef::getAbort, likeCondition.getAbort());
            }
        }

        // 处理 inCondition 条件：IN 查询
        if (inCondition != null) {
            if (inCondition.getId() != null && !inCondition.getId().isEmpty()) {
                queryWrapper.in(SentinelDef::getId, inCondition.getId());
            }
            if (inCondition.getGuid() != null && !inCondition.getGuid().isEmpty()) {
                queryWrapper.in(SentinelDef::getGuid, inCondition.getGuid());
            }
            if (inCondition.getName() != null && !inCondition.getName().isEmpty()) {
                queryWrapper.in(SentinelDef::getName, inCondition.getName());
            }
            if (inCondition.getBody() != null && !inCondition.getBody().isEmpty()) {
                queryWrapper.in(SentinelDef::getBody, inCondition.getBody());
            }
            if (inCondition.getCron() != null && !inCondition.getCron().isEmpty()) {
                queryWrapper.in(SentinelDef::getCron, inCondition.getCron());
            }
            if (inCondition.getConcurrent() != null && !inCondition.getConcurrent().isEmpty()) {
                queryWrapper.in(SentinelDef::getConcurrent, inCondition.getConcurrent());
            }
            if (inCondition.getAbort() != null && !inCondition.getAbort().isEmpty()) {
                queryWrapper.in(SentinelDef::getAbort, inCondition.getAbort());
            }
        }

        // 执行查询
        IPage<SentinelDef> sentinelDefIPage = sentinelDefService.page(page, queryWrapper);

        // 转换为 VO
        List<SentinelDefCompositeDTO> compositeLst = sentinelDefIPage.getRecords().stream()
                .map(t -> sentinelDefCompositeService.selectById(t.getId()))
                .collect(Collectors.toList());
        List<SentinelDefCompositeVO> rows = SentinelDefCompositeWrapper.build().entityVOList(compositeLst);

        // 封装返回结果
        TableDataInfo<SentinelDefCompositeVO> dataInfo = new TableDataInfo<>();
        dataInfo.setCode(Constants.SUCCESS);
        dataInfo.setData(rows);
        dataInfo.setMsg("查询成功");
        dataInfo.setTotal(sentinelDefIPage.getTotal());

        return dataInfo;
    }

    /**
     * 获取哨兵定义(sentinelDef)Composite详细信息
     */
    @ApiOperation(value = "获取哨兵定义(sentinelDef)Composite详细信息")
    @GetMapping(value = "/{id}")
    public R<SentinelDefCompositeVO> getInfo(@PathVariable("id") String id)
    {
        if(NumberUtil.isNumber(id)) {
            SentinelDefCompositeDTO sentinelDef = sentinelDefCompositeService.selectById(Long.parseLong(id));
            return R.ok(SentinelDefCompositeWrapper.build().entityVO(sentinelDef));
        }else{
            SentinelDefCompositeDTO param = new SentinelDefCompositeDTO();
            param.setGuid(id);
            SentinelDefCompositeDTO sentinelDef = sentinelDefCompositeService.selectOne(param);
            return R.ok(SentinelDefCompositeWrapper.build().entityVO(sentinelDef));
        }
    }


    /**
     * 新增哨兵定义(sentinelDef)
     */
    @ApiOperation(value = "新增哨兵定义(sentinelDef)Composite")
    @PostMapping
    public R<Long> add(@RequestBody SentinelDefCompositeDTO sentinelDef)
    {
        sentinelDef.setId(null);
        sentinelDef.setCreateTime(DateUtil.now());
        Long id = sentinelDefCompositeService.submit(sentinelDef);
        R r = id!=null ? R.ok(id) : R.fail();
        return r;
    }

    /**
     * 修改哨兵定义(sentinelDef)
     */
    @ApiOperation(value = "修改哨兵定义(sentinelDef)Composite")
    @PostMapping("/edit")
    public R<Long> edit(@RequestBody SentinelDefCompositeDTO sentinelDef)
    {
        sentinelDef.setUpdateTime(DateUtil.now());
        Long id = sentinelDefCompositeService.submit(sentinelDef);
        R r = id!=null ? R.ok(id) : R.fail();
        return r;
    }

    /**
     * 删除哨兵定义(sentinelDef)Composite
     */
    @ApiOperation(value = "删除哨兵定义(sentinelDef)Composite")
    @PostMapping("/delete/{id}")
    public R<String> remove(@PathVariable String id)
    {
        if(NumberUtil.isNumber(id)) {
            sentinelDefCompositeService.removeById(Long.parseLong(id));
        }else{
            sentinelDefService.remove(new LambdaQueryWrapper<SentinelDef>().eq(SentinelDef::getGuid,id));
        }
        return R.ok(id);
    }

}