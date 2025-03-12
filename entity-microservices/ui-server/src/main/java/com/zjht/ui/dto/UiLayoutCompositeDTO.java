package com.zjht.ui.dto;

import com.zjht.ui.entity.UiLayout;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.List;
import lombok.Data;

@Data
@ApiModel(value = "DTO", description = "",parent = UiLayout.class)
public class UiLayoutCompositeDTO extends UiLayout {
  private Long originalId;
}