package com.zjht.unified.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wukong.core.mp.base.BaseEntity;
import com.wukong.core.weblog.utils.DateUtil;
import com.zjht.unified.common.core.constants.Constants;
import com.zjht.unified.common.core.controller.BaseController;
import com.zjht.unified.common.core.domain.PageDomain;
import com.zjht.unified.common.core.domain.R;
import com.zjht.unified.common.core.domain.TableDataInfo;
import com.zjht.unified.common.core.domain.dto.BaseQueryDTO;
import com.zjht.unified.dto.ClazzDefCompositeDTO;
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

}