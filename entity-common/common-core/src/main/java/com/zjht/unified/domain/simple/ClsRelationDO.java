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
@ApiModel(value = "ClsRelation 领域对象", description = "")
public class ClsRelationDO {

	private static final long serialVersionUID = 5291632211703176533L;


	/**
	 * 
	 */
	@ApiModelProperty(value = "")
	private Long id;
	 /**
	 * 1 一对一 2 1对多 3 多对1 4 多对多
	 */
	@ApiModelProperty(value = "1 一对一 2 1对多 3 多对1 4 多对多")
	private Integer rel;
	 /**
	 * 多对多的关系表名
	 */
	@ApiModelProperty(value = "多对多的关系表名")
	private String n2nTbl;
	 /**
	 * 多对多关系表的类1的键
	 */
	@ApiModelProperty(value = "多对多关系表的类1的键")
	private String n2nFrom;
	 /**
	 * 多对多关系表的类2的键
	 */
	@ApiModelProperty(value = "多对多关系表的类2的键")
	private String n2nTo;
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
	 /**
	 * 自定义载入脚本
	 */
	@ApiModelProperty(value = "自定义载入脚本")
	private String script;
	 /**
	 * 字段的GUID，来自方向
	 */
	@ApiModelProperty(value = "字段的GUID，来自方向")
	private String fieldIdFrom;
	 /**
	 * 字段的GUID，去方向
	 */
	@ApiModelProperty(value = "字段的GUID，去方向")
	private String fieldIdTo;
	 /**
	 * 关系的GUID
	 */
	@ApiModelProperty(value = "关系的GUID")
	private String guid;
}