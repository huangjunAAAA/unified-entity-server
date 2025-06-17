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
@Table(name = "ue_prj")
@Data
@TableName("ue_prj")
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "UePrj对象", description = "")
public class UePrj extends BaseCopyEntity {

	private static final long serialVersionUID = -436985692075407827L;



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
	@Column(name = "name")
	@TableField(value = "name")  
	private String name;

	 /**
	 * 
	 */
	@ApiModelProperty(value = "")
	@Column(name = "ui_prj_id")
	@TableField(value = "ui_prj_id")  
	private Long uiPrjId;

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
	 * GUID
	 */
	@ApiModelProperty(value = "GUID")
	@Column(name = "guid")
	@TableField(value = "guid")  
	private String guid;

	 /**
	 * 是否模板
	 */
	@ApiModelProperty(value = "是否模板")
	@Column(name = "is_template")
	@TableField(value = "is_template")  
	private Integer template;
}