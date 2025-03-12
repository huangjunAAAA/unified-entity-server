package com.zjht.ui.dto;

import com.zjht.ui.entity.UiPrj;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.List;
import lombok.Data;

@Data
@ApiModel(value = "DTO", description = "",parent = UiPrj.class)
public class UiPrjCompositeDTO extends UiPrj {
  private Long originalId;
  private List<UiPageCompositeDTO> rprjIdUiPageList;
  private GitStoreCompositeDTO gitIdGitStoreComposite;
}