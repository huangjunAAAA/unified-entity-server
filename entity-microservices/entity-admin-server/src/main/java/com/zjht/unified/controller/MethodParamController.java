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
import com.zjht.unified.vo.MethodParamVo;
import com.zjht.unified.wrapper.MethodParamWrapper;
import com.zjht.unified.entity.MethodParam;
import com.zjht.unified.service.IMethodParamService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Arrays;

/**
 *  控制器
 *
 * @author wangy
 */
@Api(value = "类方法的参数维护",tags = {"类方法的参数维护"})
@RestController
@RequestMapping("/methodParam")
public class MethodParamController extends BaseController{

	private static final Logger logger = LoggerFactory.getLogger(MethodParamController.class);
	@Autowired
    private IMethodParamService methodParamService;
	
	/**
     * 查询类方法的参数列表, 对象形式
     */
    @ApiOperation(value = "查询类方法的参数列表")
    @PostMapping("/list-ext")
    public TableDataInfo<MethodParamVo> listExt(@RequestBody BaseQueryDTO<MethodParam> methodParam)
    {
        return list(methodParam.getCondition(),new PageDomain(methodParam.getPage(),methodParam.getSize()));
    }
	
	/**
     * 查询类方法的参数列表
     */
    @ApiOperation(value = "查询类方法的参数列表")
    @GetMapping("/list")
    public TableDataInfo<MethodParamVo> list(MethodParam methodParam, PageDomain  pageDomain)
    {
		TableDataInfo<MethodParamVo> dataInfo = new TableDataInfo();
        Page<MethodParam> page = new Page<>(pageDomain.getPageNum(),pageDomain.getPageSize());
        IPage<MethodParam> list = methodParamService.page(page, Wrappers.<MethodParam>lambdaQuery(methodParam).orderByDesc(BaseEntity::getCreateTime));
        IPage<MethodParamVo> rows = MethodParamWrapper.build().pageVO(list);
        dataInfo.setCode(Constants.SUCCESS);
        dataInfo.setData(rows.getRecords());
        dataInfo.setMsg("查询成功");
        dataInfo.setTotal(list.getTotal());
        return dataInfo;
    }

    /**
     * 获取类方法的参数详细信息
     */
    @ApiOperation(value = "获取类方法的参数详细信息")
    @GetMapping(value = "/{id}")
    public R<MethodParamVo> getInfo(@PathVariable("id") Long id)
    {
        MethodParam methodParam = methodParamService.getById(id);
        return R.ok(MethodParamWrapper.build().entityVO(methodParam));
    }


    /**
     * 新增类方法的参数
     */
    @ApiOperation(value = "新增类方法的参数")
    @PostMapping
    public R<Long> add(@RequestBody MethodParam methodParam)
    {
        if(StringUtils.isBlank(methodParam.getName())){
            return R.fail("参数名称不能为空");
        }
        if(StringUtils.isValidVar(methodParam.getName())){
            return R.fail("参数名称格式错误");
        }
        if(StringUtils.isBlank(methodParam.getType())){
            return R.fail("参数类型不能为空");
        }
        if(null==methodParam.getSort()){
            return R.fail("序号不能为空");
        }

        methodParam.setCreateTime(DateUtil.now());
        Boolean b = methodParamService.save(methodParam);
        R r = b ? R.ok(methodParam.getId()) : R.fail();
        return r;
    }

    /**
     * 修改类方法的参数
     */
    @ApiOperation(value = "修改类方法的参数")
    @PostMapping("/edit")
    public R<Integer> edit(@RequestBody MethodParam methodParam)
    {
        methodParam.setUpdateTime(DateUtil.now());
        Boolean b = methodParamService.updateById(methodParam);
        R r = b ? R.ok() : R.fail();
        return r;
    }

    /**
     * 删除类方法的参数
     */
    @ApiOperation(value = "删除类方法的参数")
	@PostMapping("/delete/{ids}")
    public R<Integer> remove(@PathVariable Long[] ids)
    {
        Boolean b = methodParamService.removeByIds(Arrays.asList(ids));
        R r = b ? R.ok() : R.fail();
        return r;
    }
	
	
	 /**
     * 类方法的参数的动态字典
     */
    @ApiOperation(value = "类方法的参数的字典接口", notes = "传入id获取唯一对应值，或传空获取所有值",hidden = true)
    @PostMapping("/dict")
    public List<MethodParam> dict(@RequestBody List<Long> ids) {
		List<MethodParam> data = methodParamService.listByIds(ids);
        return data;
	}
	
}