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
@ApiModel(value = "UiPrj 领域对象", description = "")
public class UiPrjDO {

	private static final long serialVersionUID = -2186332820156183078L;


	/**
	 * 
	 */
	@ApiModelProperty(value = "")
	private Long id;
	 /**
	 * 项目名称
	 */
	@ApiModelProperty(value = "项目名称")
	private String name;
	 /**
	 * 仓库Id
	 */
	@ApiModelProperty(value = "仓库Id")
	private Long gitId;
	 /**
	 * 项目目录
	 */
	@ApiModelProperty(value = "项目目录")
	private String workDir;
	 /**
	 * nodejs版本
	 */
	@ApiModelProperty(value = "nodejs版本")
	private String nodejsVer;
	 /**
	 * 组件库版本
	 */
	@ApiModelProperty(value = "组件库版本")
	private String componentLibVer;
	 /**
	 * 存储方式 db数据库/hybrid混合
	 */
	@ApiModelProperty(value = "存储方式 db数据库/hybrid混合")
	private String storageType;
	 /**
	 * 版本号
	 */
	@ApiModelProperty(value = "版本号")
	private String version;
	 /**
	 * 源项目
	 */
	@ApiModelProperty(value = "源项目")
	private Long originalId;
	 /**
	 * 是否外联项目
	 */
	@ApiModelProperty(value = "是否外联项目")
	private String externalType;
	 /**
	 * 外联项目ID
	 */
	@ApiModelProperty(value = "外联项目ID")
	private String externalId;
}