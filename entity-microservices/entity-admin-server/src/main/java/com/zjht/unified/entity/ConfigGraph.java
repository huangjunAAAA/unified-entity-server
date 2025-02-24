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
@Table(name = "config_graph")
@Data
@TableName("config_graph")
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "ConfigGraph对象", description = "")
public class ConfigGraph extends BaseCopyEntity {

	private static final long serialVersionUID = -6755701482468641051L;



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
	 * 节点ID
	 */
	@ApiModelProperty(value = "节点ID")
	@Column(name = "node_id")
	@TableField(value = "node_id")  
	private Long nodeId;

	 /**
	 * 节点类型
	 */
	@ApiModelProperty(value = "节点类型")
	@Column(name = "node_type")
	@TableField(value = "node_type")  
	private String nodeType;

	 /**
	 * x排序（坐标）
	 */
	@ApiModelProperty(value = "x排序（坐标）")
	@Column(name = "x")
	@TableField(value = "x")  
	private Integer x;

	 /**
	 * y排序（坐标）
	 */
	@ApiModelProperty(value = "y排序（坐标）")
	@Column(name = "y")
	@TableField(value = "y")  
	private Integer y;

	 /**
	 * 根实例节点ID
	 */
	@ApiModelProperty(value = "根实例节点ID")
	@Column(name = "root_id")
	@TableField(value = "root_id")  
	private Long rootId;

	 /**
	 * 父节点ID
	 */
	@ApiModelProperty(value = "父节点ID")
	@Column(name = "parent_id")
	@TableField(value = "parent_id")  
	private Long parentId;

	 /**
	 * 项目ID
	 */
	@ApiModelProperty(value = "项目ID")
	@Column(name = "prj_id")
	@TableField(value = "prj_id")  
	private Long prjId;

	 /**
	 * 用以区别各种树形结构
	 */
	@ApiModelProperty(value = "用以区别各种树形结构")
	@Column(name = "type")
	@TableField(value = "type")  
	private String type;

	 /**
	 * 原始ID
	 */
	@ApiModelProperty(value = "原始ID")
	@Column(name = "original_id")
	@TableField(value = "original_id")  
	private Long originalId;
}