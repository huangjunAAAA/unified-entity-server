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
@Table(name = "fsm_def")
@Data
@TableName("fsm_def")
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "FsmDef对象", description = "")
public class FsmDef extends BaseCopyEntity {

	private static final long serialVersionUID = 6772856779895040159L;



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
	 * GUID
	 */
	@ApiModelProperty(value = "GUID")
	@Column(name = "guid")
	@TableField(value = "guid")  
	private String guid;

	 /**
	 * 名称
	 */
	@ApiModelProperty(value = "名称")
	@Column(name = "name")
	@TableField(value = "name")  
	private String name;

	 /**
	 * 1 被动 2 定时器
	 */
	@ApiModelProperty(value = "1 被动 2 定时器")
	@Column(name = "driver")
	@TableField(value = "driver")  
	private Integer driver;

	 /**
	 * 定时器执行周期
	 */
	@ApiModelProperty(value = "定时器执行周期")
	@Column(name = "cron")
	@TableField(value = "cron")  
	private String cron;

	 /**
	 * 线程模型
	 */
	@ApiModelProperty(value = "线程模型")
	@Column(name = "concurrent")
	@TableField(value = "concurrent")  
	private Integer concurrent;

	 /**
	 * 放弃执行的条件
	 */
	@ApiModelProperty(value = "放弃执行的条件")
	@Column(name = "abort")
	@TableField(value = "abort")  
	private String abort;

	 /**
	 * 原始ID
	 */
	@ApiModelProperty(value = "原始ID")
	@Column(name = "original_id")
	@TableField(value = "original_id")  
	private Long originalId;
}