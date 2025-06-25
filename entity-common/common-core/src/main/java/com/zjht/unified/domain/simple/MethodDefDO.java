package com.zjht.unified.domain.simple;

import lombok.Data;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.Date;
import java.time.LocalDateTime;
import java.math.BigDecimal;

/**
 *  实体类
 *
 * @author Chill
 */
@Data
@ApiModel(value = "MethodDef 领域对象", description = "")
public class MethodDefDO {

	private static final long serialVersionUID = 4719440169440892262L;


	/**
	 * 
	 */
	@ApiModelProperty(value = "")
	private Long id;
	 /**
	 * 方法名
	 */
	@ApiModelProperty(value = "方法名")
	private String name;
	 /**
	 * 方法体
	 */
	@ApiModelProperty(value = "方法体")
	private String body;
	 /**
	 * 所属类
	 */
	@ApiModelProperty(value = "所属类")
	private Long clazzId;
	 /**
	 * 项目ID
	 */
	@ApiModelProperty(value = "项目ID")
	private Long prjId;
	 /**
	 * 1 构造方法，2 普通方法
	 */
	@ApiModelProperty(value = "1 构造方法，2 普通方法")
	private Integer type;
	 /**
	 * 原始ID
	 */
	@ApiModelProperty(value = "原始ID")
	private Long originalId;
	 /**
	 * 显示名称
	 */
	@ApiModelProperty(value = "显示名称")
	private String displayName;
	 /**
	 * 
	 */
	@ApiModelProperty(value = "")
	private String guid;
	 /**
	 * 
	 */
	@ApiModelProperty(value = "")
	private String clazzGuid;
	 /**
	 * private/public/protected
	 */
	@ApiModelProperty(value = "private/public/protected")
	private String modifier;
	 /**
	 * 方法说明
	 */
	@ApiModelProperty(value = "方法说明")
	private String description;
}