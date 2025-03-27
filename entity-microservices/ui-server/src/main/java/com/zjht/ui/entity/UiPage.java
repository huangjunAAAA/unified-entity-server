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
@Table(name = "ui_page")
@Data
@TableName("ui_page")
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "UiPage对象", description = "")
public class UiPage extends BaseCopyEntity {

	private static final long serialVersionUID = -2761454216601660631L;



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
	 * 页面名称
	 */
	@ApiModelProperty(value = "页面名称")
	@Column(name = "name")
	@TableField(value = "name")  
	private String name;

	 /**
	 * 页面文件地址
	 */
	@ApiModelProperty(value = "页面文件地址")
	@Column(name = "path")
	@TableField(value = "path")  
	private String path;

	 /**
	 * 布局ID
	 */
	@ApiModelProperty(value = "布局ID")
	@Column(name = "layout_id")
	@TableField(value = "layout_id")  
	private Long layoutId;

	 /**
	 * 路由地址
	 */
	@ApiModelProperty(value = "路由地址")
	@Column(name = "route")
	@TableField(value = "route")  
	private String route;

	 /**
	 * 布局来源
	 */
	@ApiModelProperty(value = "布局来源")
	@Column(name = "page_source_id")
	@TableField(value = "page_source_id")  
	private Long pageSourceId;

	 /**
	 * 项目ID
	 */
	@ApiModelProperty(value = "项目ID")
	@Column(name = "rprj_id")
	@TableField(value = "rprj_id")  
	private Long rprjId;
}