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
@ApiModel(value = "FieldDef 领域对象", description = "")
public class FieldDefDO {

	private static final long serialVersionUID = 3639167290021759642L;


	/**
	 * 
	 */
	@ApiModelProperty(value = "")
	private Long id;
	 /**
	 * 属性名
	 */
	@ApiModelProperty(value = "属性名")
	private String name;
	 /**
	 * 属性类型
	 */
	@ApiModelProperty(value = "属性类型")
	private String type;
	 /**
	 * 0 任意类型 1 基础类型 2 普通类 3 结构 4 脚本 5 树节点
	 */
	@ApiModelProperty(value = "0 任意类型 1 基础类型 2 普通类 3 结构 4 脚本 5 树节点")
	private Integer nature;
	 /**
	 * 默认值
	 */
	@ApiModelProperty(value = "默认值")
	private String initValue;
	 /**
	 * 所属类ID
	 */
	@ApiModelProperty(value = "所属类ID")
	private Long clazzId;
	 /**
	 * 项目ID
	 */
	@ApiModelProperty(value = "项目ID")
	private Long prjId;
	 /**
	 * 映射的字段名
	 */
	@ApiModelProperty(value = "映射的字段名")
	private String tblCol;
	 /**
	 * 显示名称
	 */
	@ApiModelProperty(value = "显示名称")
	private String displayName;
	 /**
	 * 原始ID
	 */
	@ApiModelProperty(value = "原始ID")
	private Long originalId;
	 /**
	 * 字段值是否可缓存，以秒为单位，-1表示永久缓存，0 表示不缓存
	 */
	@ApiModelProperty(value = "字段值是否可缓存，以秒为单位，-1表示永久缓存，0 表示不缓存")
	private Integer cachable;
	 /**
	 * 类关系ID
	 */
	@ApiModelProperty(value = "类关系ID")
	private Long clsRelId;
	 /**
	 * 是否锁定默认值
	 */
	@ApiModelProperty(value = "是否锁定默认值")
	private String defaultLock;
	 /**
	 * 类关系的guid
	 */
	@ApiModelProperty(value = "类关系的guid")
	private String clsRelGuid;
	 /**
	 * 类的GUID
	 */
	@ApiModelProperty(value = "类的GUID")
	private String classGuid;
}