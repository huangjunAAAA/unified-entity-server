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
@Table(name = "cls_relation")
@Data
@TableName("cls_relation")
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "ClsRelation对象", description = "")
public class ClsRelation extends BaseCopyEntity {

	private static final long serialVersionUID = 7084936766234447418L;



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
	 * 
	 */
	@ApiModelProperty(value = "")
	@Column(name = "field_id_from")
	@TableField(value = "field_id_from")  
	private Long fieldIdFrom;

	 /**
	 * 
	 */
	@ApiModelProperty(value = "")
	@Column(name = "field_id_to")
	@TableField(value = "field_id_to")  
	private Long fieldIdTo;

	 /**
	 * 1 一对一 2 1对多 3 多对1 4 多对多
	 */
	@ApiModelProperty(value = "1 一对一 2 1对多 3 多对1 4 多对多")
	@Column(name = "rel")
	@TableField(value = "rel")  
	private Integer rel;

	 /**
	 * 多对多的关系表名
	 */
	@ApiModelProperty(value = "多对多的关系表名")
	@Column(name = "n2n_tbl")
	@TableField(value = "n2n_tbl")  
	private String n2nTbl;

	 /**
	 * 多对多关系表的类1的键
	 */
	@ApiModelProperty(value = "多对多关系表的类1的键")
	@Column(name = "n2n_from")
	@TableField(value = "n2n_from")  
	private String n2nFrom;

	 /**
	 * 多对多关系表的类2的键
	 */
	@ApiModelProperty(value = "多对多关系表的类2的键")
	@Column(name = "n2n_to")
	@TableField(value = "n2n_to")  
	private String n2nTo;

	 /**
	 * 原始ID
	 */
	@ApiModelProperty(value = "原始ID")
	@Column(name = "original_id")
	@TableField(value = "original_id")  
	private Long originalId;

	 /**
	 * 项目ID
	 */
	@ApiModelProperty(value = "项目ID")
	@Column(name = "prj_id")
	@TableField(value = "prj_id")  
	private Long prjId;

	 /**
	 * 自定义载入脚本
	 */
	@ApiModelProperty(value = "自定义载入脚本")
	@Column(name = "script")
	@TableField(value = "script")  
	private String script;
}