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
@ApiModel(value = "FsmDef 领域对象", description = "")
public class FsmDefDO {

	private static final long serialVersionUID = -6954752741827331939L;


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
	 * 名称
	 */
	@ApiModelProperty(value = "名称")
	private String name;
	 /**
	 * 1 被动 2 定时器
	 */
	@ApiModelProperty(value = "1 被动 2 定时器")
	private Integer driver;
	 /**
	 * 定时器执行周期
	 */
	@ApiModelProperty(value = "定时器执行周期")
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