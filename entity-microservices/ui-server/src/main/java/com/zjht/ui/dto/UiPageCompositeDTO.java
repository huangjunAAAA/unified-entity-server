package com.zjht.ui.dto;

import com.zjht.ui.entity.UiPage;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.List;
import lombok.Data;

@Data
@ApiModel(value = "DTO", description = "",parent = UiPage.class)
public class UiPageCompositeDTO extends UiPage {
  private Long originalId;
  private List<UiComponentCompositeDTO> pageIdUiComponentList;
  private UiLayoutCompositeDTO layoutIdUiLayoutComposite;
}