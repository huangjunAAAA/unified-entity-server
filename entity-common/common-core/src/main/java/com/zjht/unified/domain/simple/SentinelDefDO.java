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
@ApiModel(value = "SentinelDef 领域对象", description = "")
public class SentinelDefDO {

	private static final long serialVersionUID = -8424710261464050692L;


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
	 * 方法体
	 */
	@ApiModelProperty(value = "方法体")
	private String body;
	 /**
	 * 触发周期
	 */
	@ApiModelProperty(value = "触发周期")
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
	 /**
	 * 项目ID
	 */
	@ApiModelProperty(value = "项目ID")
	private Long prjId;
}