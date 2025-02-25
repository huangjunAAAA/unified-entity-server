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
@ApiModel(value = "FsmData 领域对象", description = "")
public class FsmDataDO {

	private static final long serialVersionUID = 44653040544509273L;


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
	 * 状态机定义ID
	 */
	@ApiModelProperty(value = "状态机定义ID")
	private Long fsmId;
	 /**
	 * 定时器ID
	 */
	@ApiModelProperty(value = "定时器ID")
	private Long timerId;
	 /**
	 * 状态变量数据
	 */
	@ApiModelProperty(value = "状态变量数据")
	private String data;
	 /**
	 * 原始ID
	 */
	@ApiModelProperty(value = "原始ID")
	private Long originalId;
}