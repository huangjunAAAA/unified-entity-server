package com.zjht.ui.dto;

import com.zjht.ui.entity.UiComponent;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.List;
import lombok.Data;

@Data
@ApiModel(value = "DTO", description = "",parent = UiComponent.class)
public class UiComponentCompositeDTO extends UiComponent {
  private Long originalId;
  private List<FilesetCompositeDTO> belongtoIdFilesetList;
  public static final String BELONGTOID_BELONGTOTYPE_FILESET_FK="fk-fileset-UiComponent-BELONGTOID-belongtoType";
  private List<UiEventHandleCompositeDTO> componentIdUiEventHandleList;
}