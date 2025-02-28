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
@Table(name = "method_def")
@Data
@TableName("method_def")
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "MethodDef对象", description = "")
public class MethodDef extends BaseCopyEntity {

	private static final long serialVersionUID = -3407024246885750036L;



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
	 * 方法名
	 */
	@ApiModelProperty(value = "方法名")
	@Column(name = "name")
	@TableField(value = "name")  
	private String name;

	 /**
	 * 方法体
	 */
	@ApiModelProperty(value = "方法体")
	@Column(name = "body")
	@TableField(value = "body")  
	private String body;

	 /**
	 * 所属类
	 */
	@ApiModelProperty(value = "所属类")
	@Column(name = "clazz_id")
	@TableField(value = "clazz_id")  
	private Long clazzId;

	 /**
	 * 项目ID
	 */
	@ApiModelProperty(value = "项目ID")
	@Column(name = "prj_id")
	@TableField(value = "prj_id")  
	private Long prjId;

	 /**
	 * 1 构造方法，2 普通方法
	 */
	@ApiModelProperty(value = "1 构造方法，2 普通方法")
	@Column(name = "type")
	@TableField(value = "type")  
	private Integer type;

	 /**
	 * 原始ID
	 */
	@ApiModelProperty(value = "原始ID")
	@Column(name = "original_id")
	@TableField(value = "original_id")  
	private Long originalId;

	 /**
	 * 方法说明
	 */
	@ApiModelProperty(value = "方法说明")
	@Column(name = "desc")
	@TableField(value = "desc")  
	private String desc;

	 /**
	 * 显示名称
	 */
	@ApiModelProperty(value = "显示名称")
	@Column(name = "display_name")
	@TableField(value = "display_name")  
	private String displayName;

	 /**
	 * 
	 */
	@ApiModelProperty(value = "")
	@Column(name = "guid")
	@TableField(value = "guid")  
	private String guid;

	 /**
	 * 
	 */
	@ApiModelProperty(value = "")
	@Column(name = "clazz_guid")
	@TableField(value = "clazz_guid")  
	private String clazzGuid;
}