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
@ApiModel(value = "ConfigGraph 领域对象", description = "")
public class ConfigGraphDO {

	private static final long serialVersionUID = -5535941004277809904L;


	/**
	 * 
	 */
	@ApiModelProperty(value = "")
	private Long id;
	 /**
	 * 节点ID
	 */
	@ApiModelProperty(value = "节点ID")
	private Long nodeId;
	 /**
	 * 节点类型
	 */
	@ApiModelProperty(value = "节点类型")
	private String nodeType;
	 /**
	 * x排序（坐标）
	 */
	@ApiModelProperty(value = "x排序（坐标）")
	private Integer x;
	 /**
	 * y排序（坐标）
	 */
	@ApiModelProperty(value = "y排序（坐标）")
	private Integer y;
	 /**
	 * 根实例节点ID
	 */
	@ApiModelProperty(value = "根实例节点ID")
	private Long rootId;
	 /**
	 * 父节点ID
	 */
	@ApiModelProperty(value = "父节点ID")
	private Long parentId;
	 /**
	 * 项目ID
	 */
	@ApiModelProperty(value = "项目ID")
	private Long prjId;
	 /**
	 * 用以区别各种树形结构
	 */
	@ApiModelProperty(value = "用以区别各种树形结构")
	private String type;
	 /**
	 * 原始ID
	 */
	@ApiModelProperty(value = "原始ID")
	private Long originalId;
	 /**
	 * 非对象数据
	 */
	@ApiModelProperty(value = "非对象数据")
	private String nodeData;
}