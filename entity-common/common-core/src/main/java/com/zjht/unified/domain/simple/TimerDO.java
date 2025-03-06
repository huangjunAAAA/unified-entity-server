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
@ApiModel(value = "Timer 领域对象", description = "")
public class TimerDO {

	private static final long serialVersionUID = 8269408278133000542L;


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
	 * 调用周期表达式
	 */
	@ApiModelProperty(value = "调用周期表达式")
	private String cron;
	 /**
	 * 线程模型
	 */
	@ApiModelProperty(value = "线程模型")
	private Integer concurrent;
	 /**
	 * 放弃执行的条件
	 */
	@ApiModelProperty(value = "放弃执行的条件")
	private String abort;
	 /**
	 * 原始ID
	 */
	@ApiModelProperty(value = "原始ID")
	private Long originalId;
}