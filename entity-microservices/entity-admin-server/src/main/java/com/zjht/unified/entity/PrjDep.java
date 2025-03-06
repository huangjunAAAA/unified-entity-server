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
@Table(name = "prj_dep")
@Data
@TableName("prj_dep")
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "PrjDep对象", description = "")
public class PrjDep extends BaseCopyEntity {

	private static final long serialVersionUID = 5094920806052043926L;



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
	@Column(name = "prj_id")
	@TableField(value = "prj_id")  
	private Long prjId;

	 /**
	 * 
	 */
	@ApiModelProperty(value = "")
	@Column(name = "export_id")
	@TableField(value = "export_id")  
	private Long exportId;

	 /**
	 * 原始ID
	 */
	@ApiModelProperty(value = "原始ID")
	@Column(name = "original_id")
	@TableField(value = "original_id")  
	private Long originalId;
}