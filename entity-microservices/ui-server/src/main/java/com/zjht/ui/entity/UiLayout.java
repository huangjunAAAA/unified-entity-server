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
@Table(name = "ui_layout")
@Data
@TableName("ui_layout")
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "UiLayout对象", description = "")
public class UiLayout extends BaseCopyEntity {

	private static final long serialVersionUID = -4075769605072267896L;



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
	 * 页面数据
	 */
	@ApiModelProperty(value = "页面数据")
	@Column(name = "template_data")
	@TableField(value = "template_data")  
	private String templateData;

	 /**
	 * 布局规格
	 */
	@ApiModelProperty(value = "布局规格")
	@Column(name = "layout_spec")
	@TableField(value = "layout_spec")  
	private String layoutSpec;

	 /**
	 * 原始ID
	 */
	@ApiModelProperty(value = "原始ID")
	@Column(name = "original_id")
	@TableField(value = "original_id")  
	private Long originalId;
}