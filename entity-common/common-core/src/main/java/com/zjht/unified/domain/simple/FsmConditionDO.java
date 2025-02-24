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
@ApiModel(value = "FsmCondition 领域对象", description = "")
public class FsmConditionDO {

	private static final long serialVersionUID = -6733319892007410973L;


	/**
	 * 
	 */
	@ApiModelProperty(value = "")
	private Long id;
	 /**
	 * 条件表达式
	 */
	@ApiModelProperty(value = "条件表达式")
	private String conditionExpr;
	 /**
	 * 被动模式watcher id
	 */
	@ApiModelProperty(value = "被动模式watcher id")
	private Long conditionWatcher;
	 /**
	 * 所属状态机id
	 */
	@ApiModelProperty(value = "所属状态机id")
	private Long fsmId;
	 /**
	 * 状态转换的脚本
	 */
	@ApiModelProperty(value = "状态转换的脚本")
	private String script;
	 /**
	 * 原始ID
	 */
	@ApiModelProperty(value = "原始ID")
	private Long originalId;
}