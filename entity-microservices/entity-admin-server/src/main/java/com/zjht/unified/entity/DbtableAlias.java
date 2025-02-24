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
@Table(name = "dbtable_alias")
@Data
@TableName("dbtable_alias")
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "DbtableAlias对象", description = "")
public class DbtableAlias extends BaseCopyEntity {

	private static final long serialVersionUID = 5136897673823247671L;



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
	 * 显示名称
	 */
	@ApiModelProperty(value = "显示名称")
	@Column(name = "display_name")
	@TableField(value = "display_name")  
	private String displayName;

	 /**
	 * 表全名
	 */
	@ApiModelProperty(value = "表全名")
	@Column(name = "tbl_name")
	@TableField(value = "tbl_name")  
	private String tblName;

	 /**
	 * 原始ID
	 */
	@ApiModelProperty(value = "原始ID")
	@Column(name = "original_id")
	@TableField(value = "original_id")  
	private Long originalId;
}