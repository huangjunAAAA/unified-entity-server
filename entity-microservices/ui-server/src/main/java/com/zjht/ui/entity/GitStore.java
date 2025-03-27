package com.zjht.ui.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.*;

import com.zjht.unified.common.core.entity.BaseCopyEntity;


/**
 *  实体类
 *
 * @author Chill
 */
@Entity
@Table(name = "git_store")
@Data
@TableName("git_store")
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "GitStore对象", description = "")
public class GitStore extends BaseCopyEntity {

	private static final long serialVersionUID = -3529859251662196554L;



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
	 * 项目地址
	 */
	@ApiModelProperty(value = "项目地址")
	@Column(name = "path")
	@TableField(value = "path")  
	private String path;

	 /**
	 * 
	 */
	@ApiModelProperty(value = "")
	@Column(name = "username")
	@TableField(value = "username")  
	private String username;

	 /**
	 * 
	 */
	@ApiModelProperty(value = "")
	@Column(name = "password")
	@TableField(value = "password")  
	private String password;

	 /**
	 * 分支名称
	 */
	@ApiModelProperty(value = "分支名称")
	@Column(name = "branch")
	@TableField(value = "branch")  
	private String branch;
}