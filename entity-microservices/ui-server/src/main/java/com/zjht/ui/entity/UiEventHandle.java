package com.zjht.ui.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.wukong.core.mp.base.BaseUserEntity;
import com.zjht.unified.common.core.entity.BaseCopyEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.Date;
import java.time.LocalDateTime;
import java.math.BigDecimal;
import javax.persistence.*;




/**
 *  实体类
 *
 * @author Chill
 */
@Entity
@Table(name = "ui_event_handle")
@Data
@TableName("ui_event_handle")
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "UiEventHandle对象", description = "")
public class UiEventHandle extends BaseCopyEntity {

	private static final long serialVersionUID = -5342644284113649750L;



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
	 * 事件代码
	 */
	@ApiModelProperty(value = "事件代码")
	@Column(name = "event_code")
	@TableField(value = "event_code")  
	private String eventCode;

	 /**
	 * context表示contextmenu处理事件，event表示普通事件处理
	 */
	@ApiModelProperty(value = "context表示contextmenu处理事件，event表示普通事件处理")
	@Column(name = "event_type")
	@TableField(value = "event_type")  
	private String eventType;

	 /**
	 * front/front-submit/backend
	 */
	@ApiModelProperty(value = "front/front-submit/backend")
	@Column(name = "type")
	@TableField(value = "type")  
	private String type;

	 /**
	 * 排序
	 */
	@ApiModelProperty(value = "排序")
	@Column(name = "sort")
	@TableField(value = "sort")  
	private Integer sort;

	 /**
	 * 将所有的target的数据通过json转为字符串存储在这里
	 */
	@ApiModelProperty(value = "将所有的target的数据通过json转为字符串存储在这里")
	@Column(name = "target_data")
	@TableField(value = "target_data")  
	private String targetData;

	 /**
	 * 脚本内容
	 */
	@ApiModelProperty(value = "脚本内容")
	@Column(name = "content")
	@TableField(value = "content")  
	private String content;

	 /**
	 * 所属的组件ID
	 */
	@ApiModelProperty(value = "所属的组件ID")
	@Column(name = "component_id")
	@TableField(value = "component_id")  
	private Long componentId;

	 /**
	 * 原始ID
	 */
	@ApiModelProperty(value = "原始ID")
	@Column(name = "original_id")
	@TableField(value = "original_id")  
	private Long originalId;

	 /**
	 * 前端的方法名
	 */
	@ApiModelProperty(value = "前端的方法名")
	@Column(name = "event_method")
	@TableField(value = "event_method")  
	private String eventMethod;

	 /**
	 * 事件处理引擎
	 */
	@ApiModelProperty(value = "事件处理引擎")
	@Column(name = "event_engine")
	@TableField(value = "event_engine")  
	private String eventEngine;
}