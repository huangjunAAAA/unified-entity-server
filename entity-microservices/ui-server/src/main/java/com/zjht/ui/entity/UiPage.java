package com.zjht.ui.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.wukong.core.mp.base.BaseUserEntity;
import com.zjht.unified.common.core.entity.BaseCopyEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.Date;
import java.time.LocalDateTime;
import java.math.BigDecimal;
import javax.persistence.*;




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

	private static final long serialVersionUID = -1664972154463987313L;



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
	 * 
	 */
	@ApiModelProperty(value = "")
	@Column(name = "guid")
	@TableField(value = "guid")  
	private String guid;

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

	 /**
	 * 原始ID
	 */
	@ApiModelProperty(value = "原始ID")
	@Column(name = "original_id")
	@TableField(value = "original_id")  
	private Long originalId;

	 /**
	 * 根元素ID
	 */
	@ApiModelProperty(value = "根元素ID")
	@Column(name = "root_com_id")
	@TableField(value = "root_com_id")  
	private Long rootComId;

	 /**
	 * 画布的额外数据
	 */
	@ApiModelProperty(value = "画布的额外数据")
	@Column(name = "canvas_data")
	@TableField(value = "canvas_data")  
	private String canvasData;
}