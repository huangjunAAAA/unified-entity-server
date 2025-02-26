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
@ApiModel(value = "PrjDep 领域对象", description = "")
public class PrjDepDO {

	private static final long serialVersionUID = -5924251501268155074L;


	/**
	 * 
	 */
	@ApiModelProperty(value = "")
	private Long id;
	 /**
	 * 
	 */
	@ApiModelProperty(value = "")
	private Long prjId;
	 /**
	 * 
	 */
	@ApiModelProperty(value = "")
	private Long exportId;
	 /**
	 * 原始ID
	 */
	@ApiModelProperty(value = "原始ID")
	private Long originalId;
}