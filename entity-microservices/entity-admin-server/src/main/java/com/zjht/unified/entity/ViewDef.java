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
@Table(name = "view_def")
@Data
@TableName("view_def")
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "ViewDef对象", description = "")
public class ViewDef extends BaseCopyEntity {

	private static final long serialVersionUID = -6466823268499803539L;



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
	 * 视图名称
	 */
	@ApiModelProperty(value = "视图名称")
	@Column(name = "name")
	@TableField(value = "name")  
	private String name;

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
	@Column(name = "sql_script")
	@TableField(value = "sql_script")  
	private String sqlScript;

	 /**
	 * 原始ID
	 */
	@ApiModelProperty(value = "原始ID")
	@Column(name = "original_id")
	@TableField(value = "original_id")  
	private Long originalId;
}