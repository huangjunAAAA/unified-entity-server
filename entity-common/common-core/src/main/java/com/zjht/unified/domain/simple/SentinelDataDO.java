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
@ApiModel(value = "SentinelData 领域对象", description = "")
public class SentinelDataDO {

	private static final long serialVersionUID = 2154742501333468005L;


	/**
	 * 
	 */
	@ApiModelProperty(value = "")
	private Long id;
	 /**
	 * GUID
	 */
	@ApiModelProperty(value = "GUID")
	private String guid;
	 /**
	 * 版本号
	 */
	@ApiModelProperty(value = "版本号")
	private String version;
	 /**
	 * 定时器ID
	 */
	@ApiModelProperty(value = "定时器ID")
	private Long timerId;
	 /**
	 * 哨兵ID
	 */
	@ApiModelProperty(value = "哨兵ID")
	private Long sentinelId;
	 /**
	 * 原始ID
	 */
	@ApiModelProperty(value = "原始ID")
	private Long originalId;
}