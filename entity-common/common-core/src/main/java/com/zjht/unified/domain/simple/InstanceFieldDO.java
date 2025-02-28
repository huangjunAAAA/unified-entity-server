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
@ApiModel(value = "InstanceField 领域对象", description = "")
public class InstanceFieldDO {

	private static final long serialVersionUID = 2113924606712443988L;


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
	 * 字段名
	 */
	@ApiModelProperty(value = "字段名")
	private String field;
	 /**
	 * 序列化后的数据，类型为类时只序列化ID
	 */
	@ApiModelProperty(value = "序列化后的数据，类型为类时只序列化ID")
	private String currentValue;
	 /**
	 * 上次值
	 */
	@ApiModelProperty(value = "上次值")
	private String lastValue;
	 /**
	 * 上次有效值
	 */
	@ApiModelProperty(value = "上次有效值")
	private String lastEv;
	 /**
	 * 原始ID
	 */
	@ApiModelProperty(value = "原始ID")
	private Long originalId;
}