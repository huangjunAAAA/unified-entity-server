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
@Table(name = "method_param")
@Data
@TableName("method_param")
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "MethodParam对象", description = "")
public class MethodParam extends BaseCopyEntity {

	private static final long serialVersionUID = 264795075494085767L;



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
	 * 参数名称
	 */
	@ApiModelProperty(value = "参数名称")
	@Column(name = "name")
	@TableField(value = "name")  
	private String name;

	 /**
	 * 参数类型
	 */
	@ApiModelProperty(value = "参数类型")
	@Column(name = "type")
	@TableField(value = "type")  
	private String type;

	 /**
	 * 参数默认值
	 */
	@ApiModelProperty(value = "参数默认值")
	@Column(name = "default_val")
	@TableField(value = "default_val")  
	private String defaultVal;

	 /**
	 * 排序
	 */
	@ApiModelProperty(value = "排序")
	@Column(name = "sort")
	@TableField(value = "sort")  
	private Integer sort;

	 /**
	 * 参数说明
	 */
	@ApiModelProperty(value = "参数说明")
	@Column(name = "desc")
	@TableField(value = "desc")  
	private String desc;

	 /**
	 * 方法ID
	 */
	@ApiModelProperty(value = "方法ID")
	@Column(name = "method_id")
	@TableField(value = "method_id")  
	private Long methodId;

	 /**
	 * 项目ID
	 */
	@ApiModelProperty(value = "项目ID")
	@Column(name = "prj_id")
	@TableField(value = "prj_id")  
	private Long prjId;

	 /**
	 * 原始ID
	 */
	@ApiModelProperty(value = "原始ID")
	@Column(name = "original_id")
	@TableField(value = "original_id")  
	private Long originalId;

	 /**
	 * 
	 */
	@ApiModelProperty(value = "")
	@Column(name = "guid")
	@TableField(value = "guid")  
	private String guid;

	 /**
	 * 
	 */
	@ApiModelProperty(value = "")
	@Column(name = "method_guid")
	@TableField(value = "method_guid")  
	private String methodGuid;
}