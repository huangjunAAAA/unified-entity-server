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
@Table(name = "static_def")
@Data
@TableName("static_def")
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "StaticDef对象", description = "")
public class StaticDef extends BaseCopyEntity {

	private static final long serialVersionUID = -7724516487739033306L;



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
	 * 变量名
	 */
	@ApiModelProperty(value = "变量名")
	@Column(name = "field_name")
	@TableField(value = "field_name")  
	private String fieldName;

	 /**
	 * 变量初始值
	 */
	@ApiModelProperty(value = "变量初始值")
	@Column(name = "field_value")
	@TableField(value = "field_value")  
	private String fieldValue;

	 /**
	 * 类型
	 */
	@ApiModelProperty(value = "类型")
	@Column(name = "field_type")
	@TableField(value = "field_type")  
	private Integer fieldType;

	 /**
	 * 原始ID
	 */
	@ApiModelProperty(value = "原始ID")
	@Column(name = "original_id")
	@TableField(value = "original_id")  
	private Long originalId;
}