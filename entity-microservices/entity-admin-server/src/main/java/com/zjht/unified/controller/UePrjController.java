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
import com.zjht.unified.dto.UePrjListDTO;
import com.zjht.unified.entity.*;
import com.zjht.unified.service.*;
import com.zjht.unified.vo.UePrjVo;
import com.zjht.unified.wrapper.UePrjWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Iterator;
import java.util.List;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 *  控制器
 *
 * @author wangy
 */
@Api(value = "统一实体项目维护",tags = {"统一实体项目维护"})
@RestController
@RequestMapping("/uePrj")
public class UePrjController extends BaseController{

	private static final Logger logger = LoggerFactory.getLogger(UePrjController.class);
	@Autowired
    private IUePrjService uePrjService;
	
	/**
     * 查询统一实体项目列表, 对象形式
     */
    @ApiOperation(value = "查询统一实体项目列表")
    @PostMapping("/list-ext")
    public TableDataInfo<UePrjVo> listExt(@RequestBody BaseQueryDTO<UePrj> uePrj)
    {
        return list(uePrj.getCondition(),new PageDomain(uePrj.getPage(),uePrj.getSize()));
    }
	
	/**
     * 查询统一实体项目列表
     */
    @ApiOperation(value = "查询统一实体项目列表")
    @GetMapping("/list")
    public TableDataInfo<UePrjVo> list(UePrj uePrj, PageDomain  pageDomain)
    {
		TableDataInfo<UePrjVo> dataInfo = new TableDataInfo();
        Page<UePrj> page = new Page<>(pageDomain.getPageNum(),pageDomain.getPageSize());
        IPage<UePrj> list = uePrjService.page(page, Wrappers.<UePrj>lambdaQuery(uePrj).orderByDesc(BaseEntity::getCreateTime));
        IPage<UePrjVo> rows = UePrjWrapper.build().pageVO(list);
        dataInfo.setCode(Constants.SUCCESS);
        dataInfo.setData(rows.getRecords());
        dataInfo.setMsg("查询成功");
        dataInfo.setTotal(list.getTotal());
        return dataInfo;
    }

    /**
     * 获取统一实体项目详细信息
     */
    @ApiOperation(value = "获取统一实体项目详细信息")
    @GetMapping(value = "/{id}")
    public R<UePrjVo> getInfo(@PathVariable("id") String id)
    {
        if(NumberUtil.isNumber(id)) {
            UePrj uePrj = uePrjService.getById(Long.parseLong(id));
            return R.ok(UePrjWrapper.build().entityVO(uePrj));
        }else{
            UePrj uePrj=uePrjService.getOne(Wrappers.<UePrj>lambdaQuery().eq(UePrj::getGuid, id));
            return R.ok(UePrjWrapper.build().entityVO(uePrj));
        }
    }


    /**
     * 新增统一实体项目
     */
    @ApiOperation(value = "新增统一实体项目")
    @PostMapping
    public R<Long> add(@RequestBody UePrj uePrj)
    {
        uePrj.setCreateTime(DateUtil.now());
        Boolean b = uePrjService.save(uePrj);
        R r = b ? R.ok(uePrj.getId()) : R.fail();
        return r;
    }

    /**
     * 修改统一实体项目
     */
    @ApiOperation(value = "修改统一实体项目")
    @PostMapping("/edit")
    public R<Integer> edit(@RequestBody UePrj uePrj)
    {
        uePrj.setUpdateTime(DateUtil.now());
        Boolean b = uePrjService.updateById(uePrj);
        R r = b ? R.ok() : R.fail();
        return r;
    }

    @Autowired
    private IClazzDefCompositeService clazzDefCompositeService;
    @Autowired
    private IClazzDefService clazzDefService;

    @Autowired
    private IClsRelationService clsRelationService;
    @Autowired
    private IClsRelationCompositeService clsRelationCompositeService;

    @Autowired
    private IConfigGraphService configGraphService;
    @Autowired
    private IDbtableAliasService dbtableAliasService;

    @Autowired
    private IFsmDefCompositeService fsmDefCompositeService;
    @Autowired
    private IFsmDefService fsmDefService;

    @Autowired
    private ISentinelDefService sentinelDefService;

    @Autowired
    private IViewDefService viewDefService;
    @Autowired
    private IPrjDepService prjDepService;
    @Autowired
    private IPrjExportService prjExportService;

    /**
     * 删除统一实体项目
     */
    @ApiOperation(value = "删除统一实体项目")
	@PostMapping("/delete/{id}")
    public R<Long> remove(@PathVariable String uid)
    {
        Long id=null;
        if(NumberUtil.isNumber(uid)){
            id=Long.parseLong(uid);
        }else{
            UePrj toBeDeleted = uePrjService.getOne(Wrappers.<UePrj>lambdaQuery().eq(UePrj::getGuid, uid));
            if(toBeDeleted==null){
                return R.fail("项目不存在");
            }
            id=toBeDeleted.getId();
        }
        Boolean b = uePrjService.removeById(id);
        List<ClazzDef> clsList = clazzDefService.list(Wrappers.<ClazzDef>lambdaQuery().eq(ClazzDef::getPrjId, id));
        for (Iterator<ClazzDef> iteratored = clsList.iterator(); iteratored.hasNext(); ) {
            ClazzDef clazzDef = iteratored.next();
            clazzDefCompositeService.removeById(clazzDef.getId());
        }
        List<ClsRelation> rList = clsRelationService.list(Wrappers.<ClsRelation>lambdaQuery().eq(ClsRelation::getPrjId, id));
        for (Iterator<ClsRelation> iteratored = rList.iterator(); iteratored.hasNext(); ) {
            ClsRelation clsR =  iteratored.next();
            clsRelationCompositeService.removeById(clsR.getId());
        }
        configGraphService.remove(Wrappers.<ConfigGraph>lambdaQuery().eq(ConfigGraph::getPrjId, id));
        dbtableAliasService.remove(Wrappers.<DbtableAlias>lambdaQuery().eq(DbtableAlias::getPrjId, id));

        List<FsmDef> fsmList = fsmDefService.list(Wrappers.<FsmDef>lambdaQuery().eq(FsmDef::getPrjId, id));
        for (Iterator<FsmDef> iteratored = fsmList.iterator(); iteratored.hasNext(); ) {
            FsmDef fsmDef = iteratored.next();
            fsmDefCompositeService.removeById(fsmDef.getId());
        }
        sentinelDefService.remove(Wrappers.<SentinelDef>lambdaQuery().eq(SentinelDef::getPrjId, id));
        viewDefService.remove(Wrappers.<ViewDef>lambdaQuery().eq(ViewDef::getPrjId, id));
        prjDepService.remove(Wrappers.<PrjDep>lambdaQuery().eq(PrjDep::getPrjId, id));
        prjExportService.remove(Wrappers.<PrjExport>lambdaQuery().eq(PrjExport::getSrcPrjId, id));
        return R.ok(id);
    }
	
	
	 /**
     * 统一实体项目的动态字典
     */
    @ApiOperation(value = "统一实体项目的字典接口", notes = "传入id获取唯一对应值，或传空获取所有值",hidden = true)
    @PostMapping("/dict")
    public List<UePrj> dict(@RequestBody List<Long> ids) {
		List<UePrj> data = uePrjService.listByIds(ids);
        return data;
    }

    /**
     * 查询UE项目表列表，接受like和in条件
     */
    @ApiOperation(value = "查询UE项目表列表，接受like和in条件")
    @PostMapping("/list-like-in")
    public TableDataInfo<UePrjVo> listExt2(@RequestBody BaseQueryDTO<ConditionLikeAndIn<UePrj, UePrjListDTO>> baseQueryDTO) {
        // 获取参数
        ConditionLikeAndIn<UePrj, UePrjListDTO> condition = baseQueryDTO.getCondition();
        UePrj equalsCondition = condition.getEquals();
        UePrj likeCondition = condition.getLike();
        UePrjListDTO inCondition = condition.getInCondition();

        // 初始化分页信息
        Page<UePrj> page = new Page<>(baseQueryDTO.getPage(), baseQueryDTO.getSize());

        // 构建查询条件
        LambdaQueryWrapper<UePrj> queryWrapper = Wrappers.<UePrj>lambdaQuery();

        // 处理 equals 条件：精确匹配
        if (equalsCondition != null) {
            if (equalsCondition.getId() != null) {
                queryWrapper.eq(UePrj::getId, equalsCondition.getId());
            }
            if (equalsCondition.getName() != null) {
                queryWrapper.eq(UePrj::getName, equalsCondition.getName());
            }
            if (equalsCondition.getUiPrjId() != null) {
                queryWrapper.eq(UePrj::getUiPrjId, equalsCondition.getUiPrjId());
            }
            if (equalsCondition.getVersion() != null) {
                queryWrapper.eq(UePrj::getVersion, equalsCondition.getVersion());
            }
            if (equalsCondition.getOriginalId() != null) {
                queryWrapper.eq(UePrj::getOriginalId, equalsCondition.getOriginalId());
            }
            if (equalsCondition.getGuid() != null) {
                queryWrapper.eq(UePrj::getGuid, equalsCondition.getGuid());
            }
            if (equalsCondition.getTemplate() != null) {
                queryWrapper.eq(UePrj::getTemplate, equalsCondition.getTemplate());
            }
        }

        // 处理 like 条件：模糊匹配
        if (likeCondition != null) {
            if (likeCondition.getName() != null) {
                queryWrapper.like(UePrj::getName, likeCondition.getName());
            }
            if (likeCondition.getVersion() != null) {
                queryWrapper.like(UePrj::getVersion, likeCondition.getVersion());
            }
            if (likeCondition.getGuid() != null) {
                queryWrapper.like(UePrj::getGuid, likeCondition.getGuid());
            }
        }

        // 处理 inCondition 条件：IN 查询
        if (inCondition != null) {
            if (inCondition.getId() != null && !inCondition.getId().isEmpty()) {
                queryWrapper.in(UePrj::getId, inCondition.getId());
            }
            if (inCondition.getName() != null && !inCondition.getName().isEmpty()) {
                queryWrapper.in(UePrj::getName, inCondition.getName());
            }
            if (inCondition.getUiPrjId() != null && !inCondition.getUiPrjId().isEmpty()) {
                queryWrapper.in(UePrj::getUiPrjId, inCondition.getUiPrjId());
            }
            if (inCondition.getVersion() != null && !inCondition.getVersion().isEmpty()) {
                queryWrapper.in(UePrj::getVersion, inCondition.getVersion());
            }
            if (inCondition.getOriginalId() != null && !inCondition.getOriginalId().isEmpty()) {
                queryWrapper.in(UePrj::getOriginalId, inCondition.getOriginalId());
            }
            if (inCondition.getGuid() != null && !inCondition.getGuid().isEmpty()) {
                queryWrapper.in(UePrj::getGuid, inCondition.getGuid());
            }
            if (inCondition.getTemplate() != null && !inCondition.getTemplate().isEmpty()) {
                queryWrapper.in(UePrj::getTemplate, inCondition.getTemplate());
            }
        }

        // 执行查询
        IPage<UePrj> uePrjIPage = uePrjService.page(page, queryWrapper);

        // 转换为 VO
        IPage<UePrjVo> rows = UePrjWrapper.build().pageVO(uePrjIPage);

        // 封装返回结果
        TableDataInfo<UePrjVo> dataInfo = new TableDataInfo<>();
        dataInfo.setCode(Constants.SUCCESS);
        dataInfo.setData(rows.getRecords());
        dataInfo.setMsg("查询成功");
        dataInfo.setTotal(uePrjIPage.getTotal());

        return dataInfo;
    }
}