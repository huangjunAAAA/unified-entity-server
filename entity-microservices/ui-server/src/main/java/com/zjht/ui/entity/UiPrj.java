package com.zjht.ui.entity;

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
@Table(name = "ui_prj")
@Data
@TableName("ui_prj")
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "UiPrj对象", description = "")
public class UiPrj extends BaseCopyEntity {

	private static final long serialVersionUID = -4075769605072267896L;



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
	 * 项目名称
	 */
	@ApiModelProperty(value = "项目名称")
	@Column(name = "name")
	@TableField(value = "name")  
	private String name;

	 /**
	 * 仓库Id
	 */
	@ApiModelProperty(value = "仓库Id")
	@Column(name = "git_id")
	@TableField(value = "git_id")  
	private Long gitId;

	 /**
	 * 项目目录
	 */
	@ApiModelProperty(value = "项目目录")
	@Column(name = "work_dir")
	@TableField(value = "work_dir")  
	private String workDir;

	 /**
	 * nodejs版本
	 */
	@ApiModelProperty(value = "nodejs版本")
	@Column(name = "nodejs_ver")
	@TableField(value = "nodejs_ver")  
	private String nodejsVer;

	 /**
	 * 组件库版本
	 */
	@ApiModelProperty(value = "组件库版本")
	@Column(name = "component_lib_ver")
	@TableField(value = "component_lib_ver")  
	private String componentLibVer;

	 /**
	 * 存储方式 db数据库/hybrid混合
	 */
	@ApiModelProperty(value = "存储方式 db数据库/hybrid混合")
	@Column(name = "storage_type")
	@TableField(value = "storage_type")  
	private String storageType;

	 /**
	 * 版本号
	 */
	@ApiModelProperty(value = "版本号")
	@Column(name = "version")
	@TableField(value = "version")  
	private String version;

	 /**
	 * 源项目
	 */
	@ApiModelProperty(value = "源项目")
	@Column(name = "original_id")
	@TableField(value = "original_id")  
	private Long originalId;

	 /**
	 * 是否外联项目
	 */
	@ApiModelProperty(value = "是否外联项目")
	@Column(name = "external_type")
	@TableField(value = "external_type")  
	private String externalType;

	 /**
	 * 外联项目ID
	 */
	@ApiModelProperty(value = "外联项目ID")
	@Column(name = "external_id")
	@TableField(value = "external_id")  
	private String externalId;
}