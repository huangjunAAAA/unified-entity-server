package com.zjht.ui.vo;

import com.zjht.ui.entity.GitStore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import lombok.Data;

@Data
@ApiModel(value = "VO", description = "",parent = GitStore.class)
public class GitStoreVo extends GitStore {
	
	
}
