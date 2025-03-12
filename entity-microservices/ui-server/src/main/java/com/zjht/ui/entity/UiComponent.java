package com.zjht.ui.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.*;

import com.zjht.unified.common.core.entity.BaseCopyEntity;


/**
 *  实体类
 *
 * @author Chill
 */
@Entity
@Table(name = "ui_component")
@Data
@TableName("ui_component")
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "UiComponent对象", description = "")
public class UiComponent extends BaseCopyEntity {

	private static final long serialVersionUID = -6120888256622867312L;



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
	 * 组件名称
	 */
	@ApiModelProperty(value = "组件名称")
	@Column(name = "name")
	@TableField(value = "name")  
	private String name;

	 /**
	 * 组件文件地址
	 */
	@ApiModelProperty(value = "组件文件地址")
	@Column(name = "path")
	@TableField(value = "path")  
	private String path;

	 /**
	 * 组件接入规格（json）
	 */
	@ApiModelProperty(value = "组件接入规格（json）")
	@Column(name = "plugin_spec")
	@TableField(value = "plugin_spec")  
	private String pluginSpec;

	 /**
	 * 组件类型，native组件/ normal普通组件
	 */
	@ApiModelProperty(value = "组件类型，native组件/ normal普通组件")
	@Column(name = "component_type")
	@TableField(value = "component_type")  
	private String componentType;

	 /**
	 * 是否可见
	 */
	@ApiModelProperty(value = "是否可见")
	@Column(name = "visible")
	@TableField(value = "visible")  
	private Integer visible;

	 /**
	 * 页面ID
	 */
	@ApiModelProperty(value = "页面ID")
	@Column(name = "page_id")
	@TableField(value = "page_id")  
	private Long pageId;

	 /**
	 * 源ID
	 */
	@ApiModelProperty(value = "源ID")
	@Column(name = "parent_id")
	@TableField(value = "parent_id")  
	private Long parentId;

	 /**
	 * 源类型 组件com/页面page 
	 */
	@ApiModelProperty(value = "源类型 组件com/页面page ")
	@Column(name = "parent_type")
	@TableField(value = "parent_type")  
	private String parentType;

	 /**
	 * 组件版本号
	 */
	@ApiModelProperty(value = "组件版本号")
	@Column(name = "ver")
	@TableField(value = "ver")  
	private String ver;

	 /**
	 * third_party / self_develop 是否自研
	 */
	@ApiModelProperty(value = "third_party / self_develop 是否自研")
	@Column(name = "derive_type")
	@TableField(value = "derive_type")  
	private String deriveType;

	 /**
	 * package 依赖说明
	 */
	@ApiModelProperty(value = "package 依赖说明")
	@Column(name = "dep_spec")
	@TableField(value = "dep_spec")  
	private String depSpec;

	 /**
	 * 项目ID
	 */
	@ApiModelProperty(value = "项目ID")
	@Column(name = "rprj_id")
	@TableField(value = "rprj_id")  
	private Long rprjId;

	 /**
	 * 组件接入数据集
	 */
	@ApiModelProperty(value = "组件接入数据集")
	@Column(name = "plugin_data")
	@TableField(value = "plugin_data")  
	private String pluginData;

	 /**
	 * 与页面布局的整合数据
	 */
	@ApiModelProperty(value = "与页面布局的整合数据")
	@Column(name = "layout_data")
	@TableField(value = "layout_data")  
	private String layoutData;

	 /**
	 * 是否模板组件
	 */
	@ApiModelProperty(value = "是否模板组件")
	@Column(name = "template")
	@TableField(value = "template")  
	private Integer template;

	 /**
	 * 组件初始化脚本
	 */
	@ApiModelProperty(value = "组件初始化脚本")
	@Column(name = "plugin_script")
	@TableField(value = "plugin_script")  
	private String pluginScript;

	 /**
	 * 原始ID
	 */
	@ApiModelProperty(value = "原始ID")
	@Column(name = "original_id")
	@TableField(value = "original_id")  
	private Long originalId;
}