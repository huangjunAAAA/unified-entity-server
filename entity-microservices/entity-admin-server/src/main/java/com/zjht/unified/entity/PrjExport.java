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
@Table(name = "prj_export")
@Data
@TableName("prj_export")
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "PrjExport对象", description = "")
public class PrjExport extends BaseCopyEntity {

	private static final long serialVersionUID = -1646955374409864324L;



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
	@Column(name = "src_prj_id")
	@TableField(value = "src_prj_id")  
	private Long srcPrjId;

	 /**
	 * 
	 */
	@ApiModelProperty(value = "")
	@Column(name = "spec")
	@TableField(value = "spec")  
	private String spec;

	 /**
	 * 
	 */
	@ApiModelProperty(value = "")
	@Column(name = "pkg_name")
	@TableField(value = "pkg_name")  
	private String pkgName;

	 /**
	 * 
	 */
	@ApiModelProperty(value = "")
	@Column(name = "pkg_version")
	@TableField(value = "pkg_version")  
	private String pkgVersion;

	 /**
	 * 原始ID
	 */
	@ApiModelProperty(value = "原始ID")
	@Column(name = "original_id")
	@TableField(value = "original_id")  
	private Long originalId;
}