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
@ApiModel(value = "MethodParam 领域对象", description = "")
public class MethodParamDO {

	private static final long serialVersionUID = 7886032008653525589L;


	/**
	 * 
	 */
	@ApiModelProperty(value = "")
	private Long id;
	 /**
	 * 参数名称
	 */
	@ApiModelProperty(value = "参数名称")
	private String name;
	 /**
	 * 参数类型
	 */
	@ApiModelProperty(value = "参数类型")
	private String type;
	 /**
	 * 参数默认值
	 */
	@ApiModelProperty(value = "参数默认值")
	private String defaultVal;
	 /**
	 * 排序
	 */
	@ApiModelProperty(value = "排序")
	private Integer sort;
	 /**
	 * 参数说明
	 */
	@ApiModelProperty(value = "参数说明")
	private String description;
	 /**
	 * 方法ID
	 */
	@ApiModelProperty(value = "方法ID")
	private Long methodId;
	 /**
	 * 项目ID
	 */
	@ApiModelProperty(value = "项目ID")
	private Long prjId;
	 /**
	 * 原始ID
	 */
	@ApiModelProperty(value = "原始ID")
	private Long originalId;
	 /**
	 * 
	 */
	@ApiModelProperty(value = "")
	private String guid;
	 /**
	 * 
	 */
	@ApiModelProperty(value = "")
	private String methodGuid;
}