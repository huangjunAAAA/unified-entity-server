package com.zjht.unified.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.wukong.core.mp.base.BaseUserEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.Date;
import java.time.LocalDateTime;
import java.math.BigDecimal;
import javax.persistence.*;

import com.zjht.unified.common.core.entity.BaseCopyEntity;


/**
 *  实体类
 *
 * @author Chill
 */
@Entity
@Table(name = "field_def")
@Data
@TableName("field_def")
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "FieldDef对象", description = "")
public class FieldDef extends BaseCopyEntity {

	private static final long serialVersionUID = 4492286281109800902L;



	/**
	 * 
	 */
	@ApiModelProperty(value = "")
	@Column(name = "id")
	@Id
	@TableId(value = "id", type = IdType.AUTO)
  @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	 /**
	 * 属性名
	 */
	@ApiModelProperty(value = "属性名")
	@Column(name = "name")
	@TableField(value = "name")  
	private String name;

	 /**
	 * 属性类型
	 */
	@ApiModelProperty(value = "属性类型")
	@Column(name = "type")
	@TableField(value = "type")  
	private String type;

	 /**
	 * 0 任意类型 1 基础类型 2 普通类 3 结构 4 脚本 5 树节点
	 */
	@ApiModelProperty(value = "0 任意类型 1 基础类型 2 普通类 3 结构 4 脚本 5 树节点")
	@Column(name = "nature")
	@TableField(value = "nature")  
	private Integer nature;

	 /**
	 * 默认值
	 */
	@ApiModelProperty(value = "默认值")
	@Column(name = "init_value")
	@TableField(value = "init_value")  
	private String initValue;

	 /**
	 * 所属类ID
	 */
	@ApiModelProperty(value = "所属类ID")
	@Column(name = "clazz_id")
	@TableField(value = "clazz_id")  
	private Long clazzId;

	 /**
	 * 项目ID
	 */
	@ApiModelProperty(value = "项目ID")
	@Column(name = "prj_id")
	@TableField(value = "prj_id")  
	private Long prjId;

	 /**
	 * 映射的字段名
	 */
	@ApiModelProperty(value = "映射的字段名")
	@Column(name = "tbl_col")
	@TableField(value = "tbl_col")  
	private String tblCol;

	 /**
	 * 显示名称
	 */
	@ApiModelProperty(value = "显示名称")
	@Column(name = "display_name")
	@TableField(value = "display_name")  
	private String displayName;

	 /**
	 * 原始ID
	 */
	@ApiModelProperty(value = "原始ID")
	@Column(name = "original_id")
	@TableField(value = "original_id")  
	private Long originalId;

	 /**
	 * 字段值是否可缓存，以秒为单位，-1表示永久缓存，0 表示不缓存
	 */
	@ApiModelProperty(value = "字段值是否可缓存，以秒为单位，-1表示永久缓存，0 表示不缓存")
	@Column(name = "cachable")
	@TableField(value = "cachable")  
	private Integer cachable;

	 /**
	 * 类关系ID
	 */
	@ApiModelProperty(value = "类关系ID")
	@Column(name = "cls_rel_id")
	@TableField(value = "cls_rel_id")  
	private Long clsRelId;

	 /**
	 * 是否锁定默认值
	 */
	@ApiModelProperty(value = "是否锁定默认值")
	@Column(name = "default_lock")
	@TableField(value = "default_lock")  
	private String defaultLock;
}