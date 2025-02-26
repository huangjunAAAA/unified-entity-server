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
@ApiModel(value = "InstancesData 领域对象", description = "")
public class InstancesDataDO {

	private static final long serialVersionUID = 2361302993613297226L;


	/**
	 * 
	 */
	@ApiModelProperty(value = "")
	private Long id;
	 /**
	 * 实例GUID
	 */
	@ApiModelProperty(value = "实例GUID")
	private String guid;
	 /**
	 * 版本号
	 */
	@ApiModelProperty(value = "版本号")
	private String version;
	 /**
	 * 类ID
	 */
	@ApiModelProperty(value = "类ID")
	private Long clazzId;
	 /**
	 * 项目ID
	 */
	@ApiModelProperty(value = "项目ID")
	private Long prjId;
	 /**
	 * 归档状态
	 */
	@ApiModelProperty(value = "归档状态")
	private Integer archiveStatus;
	 /**
	 * 原始ID
	 */
	@ApiModelProperty(value = "原始ID")
	private Long originalId;
}