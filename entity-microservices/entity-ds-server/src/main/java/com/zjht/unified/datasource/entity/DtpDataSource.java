package com.zjht.unified.datasource.entity;

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


/**
 *  实体类
 *
 * @author Chill
 */
@Entity
@Table(name = "dtp_data_source")
@Data
@TableName("dtp_data_source")
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "DtpDataSource对象", description = "")
public class DtpDataSource extends BaseCopyEntity {

	private static final long serialVersionUID = 2436753780138472687L;



	/**
	 * 主键ID
	 */
	@ApiModelProperty(value = "主键ID")
	@TableId(value = "id", type = IdType.AUTO)
	@Column(name = "id")
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	 /**
	 * 数据源名称
	 */
	@ApiModelProperty(value = "数据源名称")
	@Column(name = "name")
	@TableField(value = "name")  
	private String name;

	 /**
	 * 数据源URL
	 */
	@ApiModelProperty(value = "数据源URL")
	@Column(name = "url")
	@TableField(value = "url")  
	private String url;

	 /**
	 * 额外数据
	 */
	@ApiModelProperty(value = "额外数据")
	@Column(name = "extra")
	@TableField(value = "extra")  
	private String extra;

	 /**
	 * 鉴权数据
	 */
	@ApiModelProperty(value = "鉴权数据")
	@Column(name = "protection")
	@TableField(value = "protection")  
	private String protection;

	 /**
	 * 协议名称
	 */
	@ApiModelProperty(value = "协议名称")
	@Column(name = "protocol")
	@TableField(value = "protocol")  
	private String protocol;

	 /**
	 * 
	 */
	@ApiModelProperty(value = "")
	@Column(name = "api_spec")
	@TableField(value = "api_spec")  
	private String apiSpec;

	 /**
	 * 是否外部数据源
	 */
	@ApiModelProperty(value = "是否外部数据源")
	@Column(name = "external")
	@TableField(value = "external")  
	private Integer external;

	 /**
	 * 是否模拟数据源
	 */
	@ApiModelProperty(value = "是否模拟数据源")
	@Column(name = "simulated")
	@TableField(value = "simulated")  
	private Integer simulated;

	 /**
	 * 类型
	 */
	@ApiModelProperty(value = "类型")
	@Column(name = "type")
	@TableField(value = "type")  
	private String type;

	 /**
	 * 
	 */
	@ApiModelProperty(value = "")
	@Column(name = "api_key")
	@TableField(value = "api_key")  
	private String apiKey;

	 /**
	 * 
	 */
	@ApiModelProperty(value = "")
	@Column(name = "api_secret")
	@TableField(value = "api_secret")  
	private String apiSecret;
}