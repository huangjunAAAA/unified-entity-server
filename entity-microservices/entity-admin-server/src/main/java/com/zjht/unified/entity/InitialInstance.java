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
@Table(name = "initial_instance")
@Data
@TableName("initial_instance")
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "InitialInstance对象", description = "")
public class InitialInstance extends BaseCopyEntity {

	private static final long serialVersionUID = 2477908355208585845L;



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
	 * 所属类id
	 */
	@ApiModelProperty(value = "所属类id")
	@Column(name = "class_id")
	@TableField(value = "class_id")  
	private Long classId;

	 /**
	 * 所属类的guid
	 */
	@ApiModelProperty(value = "所属类的guid")
	@Column(name = "class_guid")
	@TableField(value = "class_guid")  
	private String classGuid;

	 /**
	 * 项目ID
	 */
	@ApiModelProperty(value = "项目ID")
	@Column(name = "prj_id")
	@TableField(value = "prj_id")  
	private Long prjId;

	 /**
	 * 实例GUID
	 */
	@ApiModelProperty(value = "实例GUID")
	@Column(name = "guid")
	@TableField(value = "guid")  
	private String guid;

	 /**
	 * 实例的所有属性值，json结构
	 */
	@ApiModelProperty(value = "实例的所有属性值，json结构")
	@Column(name = "attr_value")
	@TableField(value = "attr_value")  
	private String attrValue;

	 /**
	 * 原始ID
	 */
	@ApiModelProperty(value = "原始ID")
	@Column(name = "original_id")
	@TableField(value = "original_id")  
	private Long originalId;
}