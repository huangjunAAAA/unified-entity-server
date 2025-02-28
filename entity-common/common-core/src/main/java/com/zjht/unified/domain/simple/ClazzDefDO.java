package com.zjht.unified.domain.simple;

import lombok.Data;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.Date;
import java.time.LocalDateTime;
import java.math.BigDecimal;

/**
 *  实体类
 *
 * @author Chill
 */
@Data
@ApiModel(value = "ClazzDef 领域对象", description = "")
public class ClazzDefDO {

	private static final long serialVersionUID = -1984235570255490577L;


	/**
	 * 
	 */
	@ApiModelProperty(value = "")
	private Long id;
	 /**
	 * 类GUID
	 */
	@ApiModelProperty(value = "类GUID")
	private String guid;
	 /**
	 * 父类ID
	 */
	@ApiModelProperty(value = "父类ID")
	private Long parentId;
	 /**
	 * 父类GUID
	 */
	@ApiModelProperty(value = "父类GUID")
	private String parentGuid;
	 /**
	 * 父类是否源自依赖项目
	 */
	@ApiModelProperty(value = "父类是否源自依赖项目")
	private Long parentPrj;
	 /**
	 * 类名
	 */
	@ApiModelProperty(value = "类名")
	private String name;
	 /**
	 * 类中文名
	 */
	@ApiModelProperty(value = "类中文名")
	private String nameZh;
	 /**
	 * 用户定义/系统定义
	 */
	@ApiModelProperty(value = "用户定义/系统定义")
	private String type;
	 /**
	 * 项目ID
	 */
	@ApiModelProperty(value = "项目ID")
	private Long prjId;
	 /**
	 * 映射的表名
	 */
	@ApiModelProperty(value = "映射的表名")
	private String tbl;
	 /**
	 * 是否持久化
	 */
	@ApiModelProperty(value = "是否持久化")
	private Integer persistent;
	 /**
	 * 版本号
	 */
	@ApiModelProperty(value = "版本号")
	private String version;
	 /**
	 * 原始ID
	 */
	@ApiModelProperty(value = "原始ID")
	private Long originalId;
	 /**
	 * 修饰符
	 */
	@ApiModelProperty(value = "修饰符")
	private String modifer;
	 /**
	 * pv的同义属性
	 */
	@ApiModelProperty(value = "pv的同义属性")
	private String pvAttr;
	 /**
	 * 是否可继承
	 */
	@ApiModelProperty(value = "是否可继承")
	private String modifier;
	 /**
	 * 继承的类是否可读基类
	 */
	@ApiModelProperty(value = "继承的类是否可读基类")
	private Integer inheritRead;
	 /**
	 * 继承的类是否可写基类
	 */
	@ApiModelProperty(value = "继承的类是否可写基类")
	private Integer inheritWrite;
	 /**
	 * 构建函数脚本
	 */
	@ApiModelProperty(value = "构建函数脚本")
	private String constructor;
}