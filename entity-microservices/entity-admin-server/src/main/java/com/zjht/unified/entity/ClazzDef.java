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
@Table(name = "clazz_def")
@Data
@TableName("clazz_def")
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "ClazzDef对象", description = "")
public class ClazzDef extends BaseCopyEntity {

	private static final long serialVersionUID = -9195826148040857572L;



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
	 * 类GUID
	 */
	@ApiModelProperty(value = "类GUID")
	@Column(name = "guid")
	@TableField(value = "guid")  
	private String guid;

	 /**
	 * 父类ID
	 */
	@ApiModelProperty(value = "父类ID")
	@Column(name = "parent_id")
	@TableField(value = "parent_id")  
	private Long parentId;

	 /**
	 * 父类GUID
	 */
	@ApiModelProperty(value = "父类GUID")
	@Column(name = "parent_guid")
	@TableField(value = "parent_guid")  
	private String parentGuid;

	 /**
	 * 父类是否源自依赖项目
	 */
	@ApiModelProperty(value = "父类是否源自依赖项目")
	@Column(name = "parent_prj")
	@TableField(value = "parent_prj")  
	private Long parentPrj;

	 /**
	 * 类名
	 */
	@ApiModelProperty(value = "类名")
	@Column(name = "name")
	@TableField(value = "name")  
	private String name;

	 /**
	 * 类中文名
	 */
	@ApiModelProperty(value = "类中文名")
	@Column(name = "name_zh")
	@TableField(value = "name_zh")  
	private String nameZh;

	 /**
	 * 用户定义/系统定义
	 */
	@ApiModelProperty(value = "用户定义/系统定义")
	@Column(name = "type")
	@TableField(value = "type")  
	private String type;

	 /**
	 * 项目ID
	 */
	@ApiModelProperty(value = "项目ID")
	@Column(name = "prj_id")
	@TableField(value = "prj_id")  
	private Long prjId;

	 /**
	 * 映射的表名
	 */
	@ApiModelProperty(value = "映射的表名")
	@Column(name = "tbl")
	@TableField(value = "tbl")  
	private String tbl;

	 /**
	 * 是否持久化
	 */
	@ApiModelProperty(value = "是否持久化")
	@Column(name = "persistent")
	@TableField(value = "persistent")  
	private Integer persistent;

	 /**
	 * 版本号
	 */
	@ApiModelProperty(value = "版本号")
	@Column(name = "version")
	@TableField(value = "version")  
	private String version;

	 /**
	 * 原始ID
	 */
	@ApiModelProperty(value = "原始ID")
	@Column(name = "original_id")
	@TableField(value = "original_id")  
	private Long originalId;

	 /**
	 * 修饰符
	 */
	@ApiModelProperty(value = "修饰符")
	@Column(name = "modifer")
	@TableField(value = "modifer")  
	private String modifer;

	 /**
	 * pv的同义属性
	 */
	@ApiModelProperty(value = "pv的同义属性")
	@Column(name = "pv_attr")
	@TableField(value = "pv_attr")  
	private String pvAttr;

	 /**
	 * 是否可继承
	 */
	@ApiModelProperty(value = "是否可继承")
	@Column(name = "modifier")
	@TableField(value = "modifier")  
	private String modifier;

	 /**
	 * 继承的类是否可读基类
	 */
	@ApiModelProperty(value = "继承的类是否可读基类")
	@Column(name = "inherit_read")
	@TableField(value = "inherit_read")  
	private Integer inheritRead;

	 /**
	 * 继承的类是否可写基类
	 */
	@ApiModelProperty(value = "继承的类是否可写基类")
	@Column(name = "inherit_write")
	@TableField(value = "inherit_write")  
	private Integer inheritWrite;

}