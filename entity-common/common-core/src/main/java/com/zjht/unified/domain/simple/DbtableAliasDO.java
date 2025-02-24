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
@ApiModel(value = "DbtableAlias 领域对象", description = "")
public class DbtableAliasDO {

	private static final long serialVersionUID = -4728103682244018377L;


	/**
	 * 
	 */
	@ApiModelProperty(value = "")
	private Long id;
	 /**
	 * 显示名称
	 */
	@ApiModelProperty(value = "显示名称")
	private String displayName;
	 /**
	 * 表全名
	 */
	@ApiModelProperty(value = "表全名")
	private String tblName;
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