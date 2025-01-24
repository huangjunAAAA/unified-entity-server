package com.zjht.unified.data.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;


import com.zjht.unified.common.core.entity.BaseCopyEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.util.Date;


/**
 *  实体类
 *
 * @author Chill
 */
@Entity
@Table(name = "raw_data")
@Data
@TableName("raw_data")
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "RawData对象", description = "")
public class RawData extends BaseCopyEntity {

	private static final long serialVersionUID = -8100368938846718633L;



	/**
	 * 主键ID
	 */
	@ApiModelProperty(value = "主键ID")
	@Column(name = "id")
	@Id
	@TableId(value = "id", type = IdType.AUTO)
  @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	 /**
	 * 驱动ID
	 */
	@ApiModelProperty(value = "驱动ID")
	@Column(name = "driver_id")
	@TableField(value = "driver_id")  
	private Long driverId;

	 /**
	 * 设备ID
	 */
	@ApiModelProperty(value = "设备ID")
	@Column(name = "device_id")
	@TableField(value = "device_id")  
	private Long deviceId;

	 /**
	 * 采集点ID
	 */
	@ApiModelProperty(value = "采集点ID")
	@Column(name = "point_id")
	@TableField(value = "point_id")  
	private Long pointId;

	 /**
	 * 数据时间
	 */
	@ApiModelProperty(value = "数据时间")
	@Column(name = "data_time")
	@TableField(value = "data_time")  
	private Date dataTime;

	 /**
	 * 数据类型 text/blob
	 */
	@ApiModelProperty(value = "数据类型 text/blob")
	@Column(name = "data_type")
	@TableField(value = "data_type")  
	private String dataType;

	 /**
	 * 原始数据
	 */
	@ApiModelProperty(value = "原始数据")
	@Column(name = "raw_data")
	@TableField(value = "raw_data")  
	private String rawData;

	 /**
	 * 经过处理的数据
	 */
	@ApiModelProperty(value = "经过处理的数据")
	@Column(name = "processed_data")
	@TableField(value = "processed_data")  
	private String processedData;

	 /**
	 * 存储时间
	 */
	@ApiModelProperty(value = "存储时间")
	@Column(name = "store_time")
	@TableField(value = "store_time")  
	private Date storeTime;

	 /**
	 * 系统ID
	 */
	@ApiModelProperty(value = "系统ID")
	@Column(name = "sys_id")
	@TableField(value = "sys_id")  
	private Long sysId;

	 /**
	 * 数值型原始数据
	 */
	@ApiModelProperty(value = "数值型原始数据")
	@Column(name = "numeric_data")
	@TableField(value = "numeric_data")  
	private Double numericData;

	 /**
	 * 项目ID
	 */
	@ApiModelProperty(value = "项目ID")
	@Column(name = "colp_id")
	@TableField(value = "colp_id")  
	private Long colpId;
}