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
@ApiModel(value = "UePrj 领域对象", description = "")
public class UePrjDO {

	private static final long serialVersionUID = 8944841609928079534L;


	/**
	 * 
	 */
	@ApiModelProperty(value = "")
	private Long id;
	 /**
	 * 
	 */
	@ApiModelProperty(value = "")
	private String name;
	 /**
	 * 
	 */
	@ApiModelProperty(value = "")
	private Long uiPrjId;
	 /**
	 * 版本号
	 */
	@ApiModelProperty(value = "版本号")
	private String version;
	 /**
	 * 原始ID
	 */
	@ApiModelProperty(value = "原始ID")
	private Long originalId;
	 /**
	 * GUID
	 */
	@ApiModelProperty(value = "GUID")
	private String guid;
	 /**
	 * 是否模板
	 */
	@ApiModelProperty(value = "是否模板")
	private Integer template;
}