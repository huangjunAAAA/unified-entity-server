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
@Table(name = "fileset")
@Data
@TableName("fileset")
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "Fileset对象", description = "")
public class Fileset extends BaseCopyEntity {

	private static final long serialVersionUID = -8679490286379594449L;



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
	 * 文件地址
	 */
	@ApiModelProperty(value = "文件地址")
	@Column(name = "path")
	@TableField(value = "path")  
	private String path;

	 /**
	 * 文件说明和附加信息
	 */
	@ApiModelProperty(value = "文件说明和附加信息")
	@Column(name = "options")
	@TableField(value = "options")  
	private String options;

	 /**
	 * 排序
	 */
	@ApiModelProperty(value = "排序")
	@Column(name = "sort")
	@TableField(value = "sort")  
	private Float sort;

	 /**
	 * 存储类型 数据库db/git
	 */
	@ApiModelProperty(value = "存储类型 数据库db/git")
	@Column(name = "storage_type")
	@TableField(value = "storage_type")  
	private String storageType;

	 /**
	 * git存储ID
	 */
	@ApiModelProperty(value = "git存储ID")
	@Column(name = "git_id")
	@TableField(value = "git_id")  
	private Long gitId;

	 /**
	 * 组件/页面ID
	 */
	@ApiModelProperty(value = "组件/页面ID")
	@Column(name = "belongto_id")
	@TableField(value = "belongto_id")  
	private Long belongtoId;

	 /**
	 * ID类型，组件或页面
	 */
	@ApiModelProperty(value = "ID类型，组件或页面")
	@Column(name = "belongto_type")
	@TableField(value = "belongto_type")  
	private String belongtoType;

	 /**
	 * 文件内容
	 */
	@ApiModelProperty(value = "文件内容")
	@Column(name = "content")
	@TableField(value = "content")  
	private String content;

	 /**
	 * 原始ID
	 */
	@ApiModelProperty(value = "原始ID")
	@Column(name = "original_id")
	@TableField(value = "original_id")  
	private Long originalId;
}