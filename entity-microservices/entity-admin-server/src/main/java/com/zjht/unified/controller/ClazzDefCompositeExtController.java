package com.zjht.unified.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wukong.core.mp.base.BaseEntity;
import com.zjht.unified.common.core.constants.Constants;
import com.zjht.unified.common.core.controller.BaseController;
import com.zjht.unified.common.core.domain.PageDomain;
import com.zjht.unified.common.core.domain.R;
import com.zjht.unified.common.core.domain.TableDataInfo;
import com.zjht.unified.common.core.domain.dto.BaseQueryDTO;
import com.zjht.unified.common.core.domain.dto.ConditionLikeAndIn;
import com.zjht.unified.common.core.util.StringUtils;
import com.zjht.unified.dto.ClassDefListDTO;
import com.zjht.unified.dto.ClazzDefCompositeDTO;


import com.zjht.unified.dto.IdNameGuidDTO;
import com.zjht.unified.entity.ClazzDef;
import com.zjht.unified.entity.PrjDep;
import com.zjht.unified.entity.PrjExport;
import com.zjht.unified.service.IClazzDefCompositeService;
import com.zjht.unified.service.IClazzDefService;
import com.zjht.unified.service.IPrjDepService;
import com.zjht.unified.service.IPrjExportService;
import com.zjht.unified.vo.ClazzDefCompositeVO;
import com.zjht.unified.wrapper.ClazzDefCompositeWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 *  控制器
 *
 * @author wangy
 */
@Api(value = "类定义(clazzDef)维护",tags = {"类定义(clazzDef)维护"})
@RestController
@RequestMapping("/clazzDef-composite-ext")
public class ClazzDefCompositeExtController extends BaseController{

    private static final Logger logger = LoggerFactory.getLogger(ClazzDefController.class);
    @Autowired
    private IClazzDefService clazzDefService;
    @Autowired
    private IClazzDefCompositeService clazzDefCompositeService;
    @Autowired
    private IPrjDepService prjDepService;
    @Autowired
    private IPrjExportService prjExportService;

    /**
     * 查询(clazzDef)列表，包括依赖项目, 对象形式
     */
    @ApiOperation(value = "查询(clazzDef)列表，包括依赖项目")
    @PostMapping("/list-ext")
    public TableDataInfo<ClazzDefCompositeVO> listExt(@RequestBody BaseQueryDTO<ClazzDef> clazzDef)
    {
        return list(clazzDef.getCondition(),new PageDomain(clazzDef.getPage(),clazzDef.getSize()));
    }

    /**
     * 查询(clazzDef)列表，包括依赖项目
     */
    @ApiOperation(value = "查询(clazzDef)列表，包括依赖项目")
    @GetMapping("/list")
    public TableDataInfo<ClazzDefCompositeVO> list(ClazzDef clazzDef, PageDomain  pageDomain)
    {
        TableDataInfo<ClazzDefCompositeVO> dataInfo = new TableDataInfo();
        Page<ClazzDef> page = new Page<>(pageDomain.getPageNum(), pageDomain.getPageSize());
        Long prjId = clazzDef.getPrjId();
        List<Long> deps = getAllDeps(prjId);
        deps.add(prjId);
        clazzDef.setPrjId(null);
        IPage<ClazzDef> list = clazzDefService.page(page,Wrappers.<ClazzDef>lambdaQuery(clazzDef).in(ClazzDef::getPrjId,deps).orderByDesc(BaseEntity::getCreateTime));
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
     * 获取完整的依赖关系
     */

    private List<Long> getAllDeps(Long prjId){
        List<Long> all=new ArrayList<>();
        List<Long> tmp=new ArrayList<>();
        tmp.add(prjId);
        while(tmp.size()>0) {
            Long tId=tmp.remove(0);
            List<Long> depExports = prjDepService.list(Wrappers.<PrjDep>lambdaQuery(PrjDep.class)
                            .eq(PrjDep::getPrjId, tId))
                    .stream().map(PrjDep::getExportId).collect(Collectors.toList());
            if(depExports.size()>0) {
                List<Long> depPrjs = prjExportService.list(Wrappers.<PrjExport>lambdaQuery(PrjExport.class)
                                .in(PrjExport::getId, depExports))
                        .stream().map(PrjExport::getSrcPrjId).collect(Collectors.toList());
                tmp.addAll(depPrjs);
                all.addAll(depPrjs);
            }
        }
        return all;
    }


    @ApiOperation(value = "查询(clazzDef)列表，接受like和in条件")
    @PostMapping("/list-like-in")
    public TableDataInfo<ClazzDefCompositeVO> listExt2(@RequestBody BaseQueryDTO<ConditionLikeAndIn<ClazzDef, ClassDefListDTO>> baseQueryDTO) {
        // 获取参数
        ConditionLikeAndIn<ClazzDef, ClassDefListDTO> condition = baseQueryDTO.getCondition();
        ClazzDef equalsCondition = condition.getEquals();
        ClazzDef likeCondition = condition.getLike();
        ClassDefListDTO inCondition = condition.getInCondition();

        // 初始化分页信息
        Page<ClazzDef> page = new Page<>(baseQueryDTO.getPage(),baseQueryDTO.getSize());

        // 构建查询条件
        LambdaQueryWrapper<ClazzDef> queryWrapper = Wrappers.<ClazzDef>lambdaQuery();

        // 处理 equals 条件：精确匹配
        if (equalsCondition != null) {
            if (equalsCondition.getId() != null) {
                queryWrapper.eq(ClazzDef::getId, equalsCondition.getId());
            }
            if (equalsCondition.getGuid() != null) {
                queryWrapper.eq(ClazzDef::getGuid, equalsCondition.getGuid());
            }
            if (equalsCondition.getParentId() != null) {
                queryWrapper.eq(ClazzDef::getParentId, equalsCondition.getParentId());
            }
            if (equalsCondition.getParentGuid() != null) {
                queryWrapper.eq(ClazzDef::getParentGuid, equalsCondition.getParentGuid());
            }
            if (equalsCondition.getName() != null) {
                queryWrapper.eq(ClazzDef::getName, equalsCondition.getName());
            }
            if (equalsCondition.getNameZh() != null) {
                queryWrapper.eq(ClazzDef::getNameZh, equalsCondition.getNameZh());
            }
            if (equalsCondition.getType() != null) {
                queryWrapper.eq(ClazzDef::getType, equalsCondition.getType());
            }
            if (equalsCondition.getPrjId() != null) {
                queryWrapper.eq(ClazzDef::getPrjId, equalsCondition.getPrjId());
            }
            if (equalsCondition.getTbl() != null) {
                queryWrapper.eq(ClazzDef::getTbl, equalsCondition.getTbl());
            }
            if (equalsCondition.getVersion() != null) {
                queryWrapper.eq(ClazzDef::getVersion, equalsCondition.getVersion());
            }
            if (equalsCondition.getOriginalId() != null) {
                queryWrapper.eq(ClazzDef::getOriginalId, equalsCondition.getOriginalId());
            }
            if (equalsCondition.getInheritable() != null) {
                queryWrapper.eq(ClazzDef::getInheritable, equalsCondition.getInheritable());
            }
            if (equalsCondition.getPvAttr() != null) {
                queryWrapper.eq(ClazzDef::getPvAttr, equalsCondition.getPvAttr());
            }
            if (equalsCondition.getModifier() != null) {
                queryWrapper.eq(ClazzDef::getModifier, equalsCondition.getModifier());
            }
            if (equalsCondition.getTblHistory() != null) {
                queryWrapper.eq(ClazzDef::getTblHistory, equalsCondition.getTblHistory());
            }
        }

        // 处理 like 条件：模糊匹配
        if (likeCondition != null) {
            if (likeCondition.getName() != null) {
                queryWrapper.like(ClazzDef::getName, StringUtils.setLikeString(likeCondition.getName()));
            }
            if (likeCondition.getNameZh() != null) {
                queryWrapper.like(ClazzDef::getNameZh, StringUtils.setLikeString(likeCondition.getNameZh()));
            }
            if (likeCondition.getVersion() != null) {
                queryWrapper.like(ClazzDef::getVersion, StringUtils.setLikeString(likeCondition.getVersion()));
            }
            if (likeCondition.getModifier() != null) {
                queryWrapper.like(ClazzDef::getModifier, StringUtils.setLikeString(likeCondition.getModifier()));
            }
            if (likeCondition.getPvAttr() != null) {
                queryWrapper.like(ClazzDef::getPvAttr, StringUtils.setLikeString(likeCondition.getPvAttr()));
            }
            if (likeCondition.getTblHistory() != null) {
                queryWrapper.like(ClazzDef::getTblHistory, StringUtils.setLikeString(likeCondition.getTblHistory()));
            }
        }

        // 处理 inCondition 条件：IN 查询
        if (inCondition != null) {
            if (inCondition.getId() != null && !inCondition.getId().isEmpty()) {
                queryWrapper.in(ClazzDef::getId, inCondition.getId());
            }
            if (inCondition.getGuid() != null && !inCondition.getGuid().isEmpty()) {
                queryWrapper.in(ClazzDef::getGuid, inCondition.getGuid());
            }
            if (inCondition.getParentId() != null && !inCondition.getParentId().isEmpty()) {
                queryWrapper.in(ClazzDef::getParentId, inCondition.getParentId());
            }
            if (inCondition.getParentGuid() != null && !inCondition.getParentGuid().isEmpty()) {
                queryWrapper.in(ClazzDef::getParentGuid, inCondition.getParentGuid());
            }
            if (inCondition.getName() != null && !inCondition.getName().isEmpty()) {
                queryWrapper.in(ClazzDef::getName, inCondition.getName());
            }
            if (inCondition.getNameZh() != null && !inCondition.getNameZh().isEmpty()) {
                queryWrapper.in(ClazzDef::getNameZh, inCondition.getNameZh());
            }
            if (inCondition.getType() != null && !inCondition.getType().isEmpty()) {
                queryWrapper.in(ClazzDef::getType, inCondition.getType());
            }
            if (inCondition.getPrjId() != null && !inCondition.getPrjId().isEmpty()) {
                queryWrapper.in(ClazzDef::getPrjId, inCondition.getPrjId());
            }
            if (inCondition.getTbl() != null && !inCondition.getTbl().isEmpty()) {
                queryWrapper.in(ClazzDef::getTbl, inCondition.getTbl());
            }
            if (inCondition.getVersion() != null && !inCondition.getVersion().isEmpty()) {
                queryWrapper.in(ClazzDef::getVersion, inCondition.getVersion());
            }
            if (inCondition.getOriginalId() != null && !inCondition.getOriginalId().isEmpty()) {
                queryWrapper.in(ClazzDef::getOriginalId, inCondition.getOriginalId());
            }
            if (inCondition.getInheritable() != null && !inCondition.getInheritable().isEmpty()) {
                queryWrapper.in(ClazzDef::getInheritable, inCondition.getInheritable());
            }
            if (inCondition.getPvAttr() != null && !inCondition.getPvAttr().isEmpty()) {
                queryWrapper.in(ClazzDef::getPvAttr, inCondition.getPvAttr());
            }
            if (inCondition.getModifier() != null && !inCondition.getModifier().isEmpty()) {
                queryWrapper.in(ClazzDef::getModifier, inCondition.getModifier());
            }
        }

        // 执行查询
        IPage<ClazzDef> clazzDefIPage = clazzDefService.page(page, queryWrapper);

        // 转换为 ClazzDefCompositeDTO 列表
        List<ClazzDefCompositeDTO> compositeLst = clazzDefIPage.getRecords().stream()
                .map(t -> clazzDefCompositeService.selectById(t.getId()))
                .collect(Collectors.toList());

        // 转换为 VO
        List<ClazzDefCompositeVO> rows = ClazzDefCompositeWrapper.build().entityVOList(compositeLst);

        // 封装返回结果
        TableDataInfo<ClazzDefCompositeVO> dataInfo = new TableDataInfo<>();
        dataInfo.setCode(Constants.SUCCESS);
        dataInfo.setData(rows);
        dataInfo.setMsg("查询成功");
        dataInfo.setTotal(clazzDefIPage.getTotal());

        return dataInfo;
    }


    @ApiOperation(value = "删除类")
    @PostMapping("/delete-ext")
    public R<Long> deleteExt(@RequestBody IdNameGuidDTO idAndGuid){
        if(idAndGuid.getId()!=null){
            clazzDefCompositeService.removeById(idAndGuid.getId());
            return R.ok(idAndGuid.getId());
        }
        if(idAndGuid.getGuid()!=null){
            ClazzDef id = clazzDefService.getOne(new LambdaQueryWrapper<ClazzDef>().eq(ClazzDef::getGuid, idAndGuid.getGuid()));
            if(id!=null){
                clazzDefCompositeService.removeById(id.getId());
                return R.ok(id.getId());
            }
            return R.fail("未找到 guid:"+idAndGuid.getGuid());
        }
        return R.fail("请提供正确的id或guid");
    }
}