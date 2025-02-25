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
@ApiModel(value = "InitialInstance 领域对象", description = "")
public class InitialInstanceDO {

	private static final long serialVersionUID = -550920534880364600L;


	/**
	 * 
	 */
	@ApiModelProperty(value = "")
	private Long id;
	 /**
	 * 所属类id
	 */
	@ApiModelProperty(value = "所属类id")
	private Long classId;
	 /**
	 * 所属类的guid
	 */
	@ApiModelProperty(value = "所属类的guid")
	private String classGuid;
	 /**
	 * 项目ID
	 */
	@ApiModelProperty(value = "项目ID")
	private Long prjId;
	 /**
	 * 实例GUID
	 */
	@ApiModelProperty(value = "实例GUID")
	private String guid;
	 /**
	 * 实例的所有属性值，json结构
	 */
	@ApiModelProperty(value = "实例的所有属性值，json结构")
	private String attrValue;
	 /**
	 * 原始ID
	 */
	@ApiModelProperty(value = "原始ID")
	private Long originalId;
}