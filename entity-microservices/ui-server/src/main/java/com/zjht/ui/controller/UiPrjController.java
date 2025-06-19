package com.zjht.ui.controller ;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wukong.core.weblog.utils.BeanUtil;
import com.wukong.core.weblog.utils.DateUtil;
import com.wukong.core.mp.base.BaseEntity;
import com.zjht.ui.dto.CreateUiPrjDTO;
import com.zjht.ui.dto.UiPrjListDTO;
import com.zjht.ui.entity.Fileset;
import com.zjht.ui.service.IFilesetService;
import com.zjht.ui.vo.UiPrjVo;
import com.zjht.ui.wrapper.UiPrjWrapper;
import com.zjht.ui.entity.UiPrj;
import com.zjht.ui.service.IUiPrjService;
import com.zjht.unified.common.core.constants.Constants;
import com.zjht.unified.common.core.controller.BaseController;
import com.zjht.unified.common.core.domain.PageDomain;
import com.zjht.unified.common.core.domain.R;
import com.zjht.unified.common.core.domain.TableDataInfo;
import com.zjht.unified.common.core.domain.dto.BaseQueryDTO;
import com.zjht.unified.common.core.domain.dto.ConditionLikeAndIn;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
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
@Api(value = "UI项目表维护",tags = {"UI项目表维护"})
@RestController
@RequestMapping("/uiPrj")
public class UiPrjController extends BaseController {

	private static final Logger logger = LoggerFactory.getLogger(UiPrjController.class);
	@Autowired
    private IUiPrjService uiPrjService;
    @Autowired
    private IFilesetService filesetService;
	
	/**
     * 查询UI项目表列表, 对象形式
     */
    @ApiOperation(value = "查询UI项目表列表")
    @PostMapping("/list-ext")
    public TableDataInfo<UiPrjVo> listExt(@RequestBody BaseQueryDTO<UiPrj> uiPrj)
    {
        return list(uiPrj.getCondition(),new PageDomain(uiPrj.getPage(),uiPrj.getSize()));
    }
	
	/**
     * 查询UI项目表列表
     */
    @ApiOperation(value = "查询UI项目表列表")
    @GetMapping("/list")
    public TableDataInfo<UiPrjVo> list(UiPrj uiPrj, PageDomain  pageDomain)
    {
		TableDataInfo<UiPrjVo> dataInfo = new TableDataInfo();
        Page<UiPrj> page = new Page<>(pageDomain.getPageNum(),pageDomain.getPageSize());
        IPage<UiPrj> list = uiPrjService.page(page, Wrappers.<UiPrj>lambdaQuery(uiPrj).orderByDesc(BaseEntity::getCreateTime));
        IPage<UiPrjVo> rows = UiPrjWrapper.build().pageVO(list);
        dataInfo.setCode(Constants.SUCCESS);
        dataInfo.setData(rows.getRecords());
        dataInfo.setMsg("查询成功");
        dataInfo.setTotal(list.getTotal());
        return dataInfo;
    }

    /**
     * 获取UI项目表详细信息
     */
    @ApiOperation(value = "获取UI项目表详细信息")
    @GetMapping(value = "/{id}")
    public R<UiPrjVo> getInfo(@PathVariable("id") Long id)
    {
        UiPrj uiPrj = uiPrjService.getById(id);
        return R.ok(UiPrjWrapper.build().entityVO(uiPrj));
    }


    /**
     * 新增UI项目表
     */
    @ApiOperation(value = "新增UI项目表")
    @PostMapping
    public R<Long> add(@RequestBody UiPrj uiPrj)
    {
        uiPrj.setCreateTime(DateUtil.now());
        Boolean b = uiPrjService.save(uiPrj);
        R r = b ? R.ok(uiPrj.getId()) : R.fail();
        return r;
    }

    /**
     * 新增UI项目表
     */
    @ApiOperation(value = "新增UI项目表")
    @PostMapping("/create")
    public R<Long> create(@RequestBody CreateUiPrjDTO uiPrj)
    {
        uiPrj.setCreateTime(DateUtil.now());
        boolean b = uiPrjService.save(uiPrj);
        if(!b){
            return R.fail();
        }
        if(uiPrj.getBaseOn() != null) {
            UiPrj baseOnPrj = uiPrjService.getById(uiPrj.getBaseOn());
            BeanUtil.copyNonNull(uiPrj, baseOnPrj);
            uiPrjService.updateById(baseOnPrj);
            List<Fileset> files = filesetService.list(new LambdaQueryWrapper<Fileset>()
                    .eq(Fileset::getBelongtoId, uiPrj.getBaseOn())
                    .ne(Fileset::getBelongtoType, Constants.FILE_TYPE_PAGE));
            files.forEach(f -> {
                f.setId(null);
                f.setBelongtoId(uiPrj.getId());
                filesetService.save(f);
            });
        }
        return R.ok(uiPrj.getId());
    }


    /**
     * 修改UI项目表
     */
    @ApiOperation(value = "修改UI项目表")
    @PostMapping("/edit")
    public R<Integer> edit(@RequestBody UiPrj uiPrj)
    {
        uiPrj.setUpdateTime(DateUtil.now());
        Boolean b = uiPrjService.updateById(uiPrj);
        R r = b ? R.ok() : R.fail();
        return r;
    }

    /**
     * 删除UI项目表
     */
    @ApiOperation(value = "删除UI项目表")
	@PostMapping("/delete/{ids}")
    public R<Integer> remove(@PathVariable Long[] ids)
    {
        Boolean b = uiPrjService.removeByIds(Arrays.asList(ids));
        R r = b ? R.ok() : R.fail();
        return r;
    }
	
	
	 /**
     * UI项目表的动态字典
     */
    @ApiOperation(value = "UI项目表的字典接口", notes = "传入id获取唯一对应值，或传空获取所有值",hidden = true)
    @PostMapping("/dict")
    public List<UiPrj> dict(@RequestBody List<Long> ids) {
		List<UiPrj> data = uiPrjService.listByIds(ids);
        return data;
    }

    /**
     * 查询UI项目表列表，接受like和in条件
     */
    @ApiOperation(value = "查询UI项目表列表，接受like和in条件")
    @GetMapping("/list-like-in")
    public TableDataInfo<UiPrjVo> listExt2(@RequestBody BaseQueryDTO<ConditionLikeAndIn<UiPrj, UiPrjListDTO>> baseQueryDTO) {
        // 获取参数
        ConditionLikeAndIn<UiPrj, UiPrjListDTO> condition = baseQueryDTO.getCondition();
        UiPrj equalsCondition = condition.getEquals();
        UiPrj likeCondition = condition.getLike();
        UiPrjListDTO inCondition = condition.getInCondition();

        // 初始化分页信息
        Page<UiPrj> page = new Page<>(baseQueryDTO.getPage(), baseQueryDTO.getSize());

        // 构建查询条件
        LambdaQueryWrapper<UiPrj> queryWrapper = Wrappers.<UiPrj>lambdaQuery();

        // 处理 equals 条件：精确匹配
        if (equalsCondition != null) {
            if (equalsCondition.getId() != null) {
                queryWrapper.eq(UiPrj::getId, equalsCondition.getId());
            }
            if (equalsCondition.getName() != null) {
                queryWrapper.eq(UiPrj::getName, equalsCondition.getName());
            }
            if (equalsCondition.getGitId() != null) {
                queryWrapper.eq(UiPrj::getGitId, equalsCondition.getGitId());
            }
            if (equalsCondition.getWorkDir() != null) {
                queryWrapper.eq(UiPrj::getWorkDir, equalsCondition.getWorkDir());
            }
            if (equalsCondition.getNodejsVer() != null) {
                queryWrapper.eq(UiPrj::getNodejsVer, equalsCondition.getNodejsVer());
            }
            if (equalsCondition.getComponentLibVer() != null) {
                queryWrapper.eq(UiPrj::getComponentLibVer, equalsCondition.getComponentLibVer());
            }
            if (equalsCondition.getStorageType() != null) {
                queryWrapper.eq(UiPrj::getStorageType, equalsCondition.getStorageType());
            }
            if (equalsCondition.getVersion() != null) {
                queryWrapper.eq(UiPrj::getVersion, equalsCondition.getVersion());
            }
            if (equalsCondition.getOriginalId() != null) {
                queryWrapper.eq(UiPrj::getOriginalId, equalsCondition.getOriginalId());
            }
            if (equalsCondition.getExternalType() != null) {
                queryWrapper.eq(UiPrj::getExternalType, equalsCondition.getExternalType());
            }
            if (equalsCondition.getExternalId() != null) {
                queryWrapper.eq(UiPrj::getExternalId, equalsCondition.getExternalId());
            }
        }

        // 处理 like 条件：模糊匹配
        if (likeCondition != null) {
            if (likeCondition.getName() != null) {
                queryWrapper.like(UiPrj::getName, likeCondition.getName());
            }
            if (likeCondition.getWorkDir() != null) {
                queryWrapper.like(UiPrj::getWorkDir, likeCondition.getWorkDir());
            }
            if (likeCondition.getNodejsVer() != null) {
                queryWrapper.like(UiPrj::getNodejsVer, likeCondition.getNodejsVer());
            }
            if (likeCondition.getComponentLibVer() != null) {
                queryWrapper.like(UiPrj::getComponentLibVer, likeCondition.getComponentLibVer());
            }
            if (likeCondition.getStorageType() != null) {
                queryWrapper.like(UiPrj::getStorageType, likeCondition.getStorageType());
            }
            if (likeCondition.getVersion() != null) {
                queryWrapper.like(UiPrj::getVersion, likeCondition.getVersion());
            }
            if (likeCondition.getExternalType() != null) {
                queryWrapper.like(UiPrj::getExternalType, likeCondition.getExternalType());
            }
            if (likeCondition.getExternalId() != null) {
                queryWrapper.like(UiPrj::getExternalId, likeCondition.getExternalId());
            }
        }

        // 处理 inCondition 条件：IN 查询
        if (inCondition != null) {
            if (inCondition.getId() != null && !inCondition.getId().isEmpty()) {
                queryWrapper.in(UiPrj::getId, inCondition.getId());
            }
            if (inCondition.getName() != null && !inCondition.getName().isEmpty()) {
                queryWrapper.in(UiPrj::getName, inCondition.getName());
            }
            if (inCondition.getWorkDir() != null && !inCondition.getWorkDir().isEmpty()) {
                queryWrapper.in(UiPrj::getWorkDir, inCondition.getWorkDir());
            }
            if (inCondition.getNodejsVer() != null && !inCondition.getNodejsVer().isEmpty()) {
                queryWrapper.in(UiPrj::getNodejsVer, inCondition.getNodejsVer());
            }
            if (inCondition.getComponentLibVer() != null && !inCondition.getComponentLibVer().isEmpty()) {
                queryWrapper.in(UiPrj::getComponentLibVer, inCondition.getComponentLibVer());
            }
            if (inCondition.getStorageType() != null && !inCondition.getStorageType().isEmpty()) {
                queryWrapper.in(UiPrj::getStorageType, inCondition.getStorageType());
            }
            if (inCondition.getVersion() != null && !inCondition.getVersion().isEmpty()) {
                queryWrapper.in(UiPrj::getVersion, inCondition.getVersion());
            }
            if (inCondition.getOriginalId() != null && !inCondition.getOriginalId().isEmpty()) {
                queryWrapper.in(UiPrj::getOriginalId, inCondition.getOriginalId());
            }
            if (inCondition.getExternalType() != null && !inCondition.getExternalType().isEmpty()) {
                queryWrapper.in(UiPrj::getExternalType, inCondition.getExternalType());
            }
            if (inCondition.getExternalId() != null && !inCondition.getExternalId().isEmpty()) {
                queryWrapper.in(UiPrj::getExternalId, inCondition.getExternalId());
            }
        }

        // 执行查询
        IPage<UiPrj> uiPrjIPage = uiPrjService.page(page, queryWrapper);

        // 转换为 VO
        List<UiPrjVo> rows = uiPrjIPage.getRecords().stream()
                .map(t -> UiPrjWrapper.build().entityVO(t))
                .collect(Collectors.toList());

        // 封装返回结果
        TableDataInfo<UiPrjVo> dataInfo = new TableDataInfo<>();
        dataInfo.setCode(Constants.SUCCESS);
        dataInfo.setData(rows);
        dataInfo.setMsg("查询成功");
        dataInfo.setTotal(uiPrjIPage.getTotal());

        return dataInfo;
    }
}