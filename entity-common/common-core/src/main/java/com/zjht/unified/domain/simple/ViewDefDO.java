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
@ApiModel(value = "ViewDef 领域对象", description = "")
public class ViewDefDO {

	private static final long serialVersionUID = -7555897309656432764L;


	/**
	 * 
	 */
	@ApiModelProperty(value = "")
	private Long id;
	 /**
	 * 视图名称
	 */
	@ApiModelProperty(value = "视图名称")
	private String name;
	 /**
	 * 
	 */
	@ApiModelProperty(value = "")
	private Long prjId;
	 /**
	 * 
	 */
	@ApiModelProperty(value = "")
	private String sqlScript;
	 /**
	 * 原始ID
	 */
	@ApiModelProperty(value = "原始ID")
	private Long originalId;
}