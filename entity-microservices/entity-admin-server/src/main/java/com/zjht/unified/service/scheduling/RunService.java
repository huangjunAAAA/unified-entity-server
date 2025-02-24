package com.zjht.unified.service.scheduling;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zjht.unified.admin.utils.EntityDoUtils;
import com.zjht.unified.domain.composite.ClazzDefCompositeDO;
import com.zjht.unified.domain.composite.FsmDefCompositeDO;
import com.zjht.unified.domain.composite.PrjSpecDO;
import com.zjht.unified.domain.simple.SentinelDefDO;
import com.zjht.unified.domain.simple.StaticDefDO;
import com.zjht.unified.domain.simple.UePrjDO;
import com.zjht.unified.domain.simple.ViewDefDO;
import com.zjht.unified.dto.ClazzDefCompositeDTO;
import com.zjht.unified.dto.FsmDefCompositeDTO;
import com.zjht.unified.entity.*;
import com.zjht.unified.service.*;
import com.zjht.unified.utils.JsonUtilExt;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
public class RunService {
    @Autowired
    private IClazzDefCompositeService clazzDefCompositeService;

    @Autowired
    private IClazzDefService clazzDefService;

    @Autowired
    private IDbtableAliasService dbtableAliasService;

    @Autowired
    private IFsmDefCompositeService fsmDefCompositeService;

    @Autowired
    private IFsmDefService fsmDefService;

    @Autowired
    private IPrjDepService prjDepService;

    @Autowired
    private ISentinelDefService sentinelDefService;

    @Autowired
    private IStaticDefService staticDefService;

    @Autowired
    private IViewDefService viewDefService;

    @Autowired
    private IUePrjService prjService;

    @Autowired
    private IPrjExportService prjExportService;

    public PrjSpecDO genPrjSpec(Long prjId){
        PrjSpecDO prjSpec = genSinglePrjSpec(prjId);
        List<PrjDep> depExportList = prjDepService.list(new LambdaQueryWrapper<PrjDep>().eq(PrjDep::getPrjId, prjId));
        if(CollectionUtils.isNotEmpty(depExportList)) {
            prjSpec.setDepPkgList(new ArrayList<>());
            for (Iterator<PrjDep> iterator = depExportList.iterator(); iterator.hasNext(); ) {
                PrjDep dep = iterator.next();
                PrjExport export = prjExportService.getById(dep.getExportId());
                PrjSpecDO depPrjSpec = genPrjSpec(export.getSrcPrjId());
                prjSpec.getDepPkgList().add(depPrjSpec);
            }
        }
        return prjSpec;
    }

    private PrjSpecDO genSinglePrjSpec(Long prjId){
        PrjSpecDO target=new PrjSpecDO();
        UePrj prj = prjService.getById(prjId);
        target.setUePrj(JsonUtilExt.jsonCast(prj, UePrjDO.class));

        List<ClazzDef> clazzDefList = clazzDefService.list(new LambdaQueryWrapper<ClazzDef>().eq(ClazzDef::getPrjId, prjId));
        if(CollectionUtils.isNotEmpty(clazzDefList)) {
            target.setClazzList(new ArrayList<>());
            clazzDefList.forEach(cdf -> {
                ClazzDefCompositeDTO cc = clazzDefCompositeService.selectById(cdf.getId());
                ClazzDefCompositeDO cdfDo = EntityDoUtils.convert(cc);
                target.getClazzList().add(cdfDo);
            });
        }

        List<FsmDef> fsmDefList = fsmDefService.list(new LambdaQueryWrapper<FsmDef>().eq(FsmDef::getPrjId, prjId));
        if(CollectionUtils.isNotEmpty(fsmDefList)) {
            target.setFsmList(new ArrayList<>());
            fsmDefList.forEach(cdf -> {
                FsmDefCompositeDTO cc = fsmDefCompositeService.selectById(cdf.getId());
                FsmDefCompositeDO cdfDo = EntityDoUtils.convert(cc);
                target.getFsmList().add(cdfDo);
            });
        }

        List<SentinelDef> sentinelList = sentinelDefService.list(new LambdaQueryWrapper<SentinelDef>().eq(SentinelDef::getPrjId, prjId));
        if(CollectionUtils.isNotEmpty(sentinelList)){
            target.setSentinelDefList(new ArrayList<>());
            sentinelList.forEach(ss->{
                SentinelDefDO ssdo=JsonUtilExt.jsonCast(ss,SentinelDefDO.class);
                target.getSentinelDefList().add(ssdo);
            });
        }

        List<StaticDef> staticDefList = staticDefService.list(new LambdaQueryWrapper<StaticDef>().eq(StaticDef::getPrjId, prjId));
        if(CollectionUtils.isNotEmpty(staticDefList)){
            target.setStaticDefList(new ArrayList<>());
            staticDefList.forEach(ss->{
                StaticDefDO ssdo=JsonUtilExt.jsonCast(ss,StaticDefDO.class);
                target.getStaticDefList().add(ssdo);
            });
        }

        List<ViewDef> viewDefList = viewDefService.list(new LambdaQueryWrapper<ViewDef>().eq(ViewDef::getPrjId, prjId));
        if(CollectionUtils.isNotEmpty(viewDefList)){
            target.setViewDefList(new ArrayList<>());
            viewDefList.forEach(ss->{
                ViewDefDO ssdo=JsonUtilExt.jsonCast(ss,ViewDefDO.class);
                target.getViewDefList().add(ssdo);
            });
        }

        return target;
    }

    
}
