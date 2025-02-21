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
@Table(name = "attachment_rel_def")
@Data
@TableName("attachment_rel_def")
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "AttachmentRelDef对象", description = "")
public class AttachmentRelDef extends BaseCopyEntity {

	private static final long serialVersionUID = 5508802665329218150L;



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
	@Column(name = "prj_id")
	@TableField(value = "prj_id")  
	private Long prjId;

	 /**
	 * 
	 */
	@ApiModelProperty(value = "")
	@Column(name = "attachment_id")
	@TableField(value = "attachment_id")  
	private String attachmentId;

	 /**
	 * 
	 */
	@ApiModelProperty(value = "")
	@Column(name = "attachment_type")
	@TableField(value = "attachment_type")  
	private String attachmentType;

	 /**
	 * 
	 */
	@ApiModelProperty(value = "")
	@Column(name = "attachment_graph_id")
	@TableField(value = "attachment_graph_id")  
	private Long attachmentGraphId;

	 /**
	 * 
	 */
	@ApiModelProperty(value = "")
	@Column(name = "attach_at_id")
	@TableField(value = "attach_at_id")  
	private String attachAtId;

	 /**
	 * 
	 */
	@ApiModelProperty(value = "")
	@Column(name = "attach_at_type")
	@TableField(value = "attach_at_type")  
	private String attachAtType;

	 /**
	 * 
	 */
	@ApiModelProperty(value = "")
	@Column(name = "attach_at_graph_id")
	@TableField(value = "attach_at_graph_id")  
	private Long attachAtGraphId;

	 /**
	 * 原始ID
	 */
	@ApiModelProperty(value = "原始ID")
	@Column(name = "original_id")
	@TableField(value = "original_id")  
	private Long originalId;
}