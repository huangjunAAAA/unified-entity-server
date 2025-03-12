package com.zjht.ui.dto;

import com.zjht.ui.entity.GitStore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.List;
import lombok.Data;

@Data
@ApiModel(value = "DTO", description = "",parent = GitStore.class)
public class GitStoreCompositeDTO extends GitStore {
  private Long originalId;
}