package com.zjht.ui.entity;

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
@Table(name = "ui_component")
@Data
@TableName("ui_component")
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "UiComponent对象", description = "")
public class UiComponent extends BaseCopyEntity {

	private static final long serialVersionUID = -8738053806867096047L;



	/**
	 *            
	 */
	@ApiModelProperty(value = "           ")
	@Column(name = "id")
	@Id
	@TableId(value = "id", type = IdType.AUTO)
  @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	 /**
	 * 
	 */
	@ApiModelProperty(value = "")
	@Column(name = "guid")
	@TableField(value = "guid")  
	private String guid;

	 /**
	 * 组件名称
	 */
	@ApiModelProperty(value = "组件名称")
	@Column(name = "name")
	@TableField(value = "name")  
	private String name;

	 /**
	 * 组件接入规格（json）
	 */
	@ApiModelProperty(value = "组件接入规格（json）")
	@Column(name = "plugin_spec")
	@TableField(value = "plugin_spec")  
	private String pluginSpec;

	 /**
	 * 组件初始化脚本
	 */
	@ApiModelProperty(value = "组件初始化脚本")
	@Column(name = "plugin_script")
	@TableField(value = "plugin_script")  
	private String pluginScript;

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
	 * 组件类型，native组件/ normal普通组件
	 */
	@ApiModelProperty(value = "组件类型，native组件/ normal普通组件")
	@Column(name = "component_type")
	@TableField(value = "component_type")  
	private String componentType;

	 /**
	 * 组件的css
	 */
	@ApiModelProperty(value = "组件的css")
	@Column(name = "css_data")
	@TableField(value = "css_data")  
	private String cssData;

	 /**
	 * 源组件ID
	 */
	@ApiModelProperty(value = "源组件ID")
	@Column(name = "component_ref")
	@TableField(value = "component_ref")  
	private Long componentRef;

	 /**
	 * 排序
	 */
	@ApiModelProperty(value = "排序")
	@Column(name = "sort")
	@TableField(value = "sort")  
	private Integer sort;

	 /**
	 * 页面ID
	 */
	@ApiModelProperty(value = "页面ID")
	@Column(name = "page_id")
	@TableField(value = "page_id")  
	private Long pageId;

	 /**
	 * 上级ID
	 */
	@ApiModelProperty(value = "上级ID")
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
	 * 原始ID
	 */
	@ApiModelProperty(value = "原始ID")
	@Column(name = "original_id")
	@TableField(value = "original_id")  
	private Long originalId;

	 /**
	 * 项目ID
	 */
	@ApiModelProperty(value = "项目ID")
	@Column(name = "rprj_id")
	@TableField(value = "rprj_id")  
	private Long rprjId;

	 /**
	 * 是否模板组件 1是 0否
	 */
	@ApiModelProperty(value = "是否模板组件 1是 0否")
	@Column(name = "template")
	@TableField(value = "template")  
	private Integer template;

	 /**
	 * 是否动态容器
	 */
	@ApiModelProperty(value = "是否动态容器")
	@Column(name = "dynamic_container")
	@TableField(value = "dynamic_container")  
	private Integer dynamicContainer;

	 /**
	 * 动态元素的关联容器ID
	 */
	@ApiModelProperty(value = "动态元素的关联容器ID")
	@Column(name = "dynamic_attached")
	@TableField(value = "dynamic_attached")  
	private Long dynamicAttached;

	 /**
	 * 所有的Render数据统一序列化为json字符串保存在这个字段
	 */
	@ApiModelProperty(value = "所有的Render数据统一序列化为json字符串保存在这个字段")
	@Column(name = "render_data")
	@TableField(value = "render_data")  
	private String renderData;
}