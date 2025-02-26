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
@ApiModel(value = "StaticDef 领域对象", description = "")
public class StaticDefDO {

	private static final long serialVersionUID = -6393556936669910349L;


	/**
	 * 
	 */
	@ApiModelProperty(value = "")
	private Long id;
	 /**
	 * 变量名
	 */
	@ApiModelProperty(value = "变量名")
	private String fieldName;
	 /**
	 * 变量初始值
	 */
	@ApiModelProperty(value = "变量初始值")
	private String fieldValue;
	 /**
	 * 类型
	 */
	@ApiModelProperty(value = "类型")
	private Integer fieldType;
	 /**
	 * 原始ID
	 */
	@ApiModelProperty(value = "原始ID")
	private Long originalId;
	 /**
	 * 项目ID
	 */
	@ApiModelProperty(value = "项目ID")
	private Long prjId;
}