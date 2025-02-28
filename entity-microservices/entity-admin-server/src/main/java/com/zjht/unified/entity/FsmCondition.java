package com.zjht.unified.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.wukong.core.mp.base.BaseUserEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.Date;
import java.time.LocalDateTime;
import java.math.BigDecimal;
import javax.persistence.*;

import com.zjht.unified.common.core.entity.BaseCopyEntity;


/**
 *  实体类
 *
 * @author Chill
 */
@Entity
@Table(name = "fsm_condition")
@Data
@TableName("fsm_condition")
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "FsmCondition对象", description = "")
public class FsmCondition extends BaseCopyEntity {

	private static final long serialVersionUID = 3814767861621597686L;



	/**
	 * 
	 */
	@ApiModelProperty(value = "")
	@Column(name = "id")
	@Id
	@TableId(value = "id", type = IdType.AUTO)
  @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	 /**
	 * 条件表达式
	 */
	@ApiModelProperty(value = "条件表达式")
	@Column(name = "condition_expr")
	@TableField(value = "condition_expr")  
	private String conditionExpr;

	 /**
	 * 被动模式watcher id
	 */
	@ApiModelProperty(value = "被动模式watcher id")
	@Column(name = "condition_watcher")
	@TableField(value = "condition_watcher")  
	private Long conditionWatcher;

	 /**
	 * 所属状态机id
	 */
	@ApiModelProperty(value = "所属状态机id")
	@Column(name = "fsm_id")
	@TableField(value = "fsm_id")  
	private Long fsmId;

	 /**
	 * 状态转换的脚本
	 */
	@ApiModelProperty(value = "状态转换的脚本")
	@Column(name = "script")
	@TableField(value = "script")  
	private String script;

	 /**
	 * 原始ID
	 */
	@ApiModelProperty(value = "原始ID")
	@Column(name = "original_id")
	@TableField(value = "original_id")  
	private Long originalId;

	 /**
	 * 当前值
	 */
	@ApiModelProperty(value = "当前值")
	@Column(name = "current_state")
	@TableField(value = "current_state")  
	private String currentState;

	 /**
	 * 转换后的值
	 */
	@ApiModelProperty(value = "转换后的值")
	@Column(name = "next_state")
	@TableField(value = "next_state")  
	private String nextState;
}