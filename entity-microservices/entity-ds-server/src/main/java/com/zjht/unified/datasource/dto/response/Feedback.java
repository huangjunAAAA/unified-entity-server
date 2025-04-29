package com.zjht.unified.datasource.dto.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
@ApiModel(value = "websocket数据反馈结构",description = "websocket数据反馈结构")
public class Feedback<T> {

    @ApiModelProperty(value = "连接的ID")
    private String connectionId;

    @ApiModelProperty(value = "消息的时间戳，服务器时间")
    private Date ts;

    /**
     * 反馈类型 1 数据更新
     */
    @ApiModelProperty(value = "反馈类型 1 数据更新 2 UI事件反馈 3 其他")
    private Integer feedbackType;

    /**
     * 反馈ID
     */
    @ApiModelProperty(value = "反馈ID")
    private String feedbackId;

    /**
     * 反馈事件的ID
     */
    @ApiModelProperty(value = "反馈事件的ID")
    private String sourceEventId;

    /**
     * 关联的反馈列表
     */
    @ApiModelProperty(value = "关联的反馈列表")
    private List<Feedback> history;

    /**
     * 反馈数据集
     */
    @ApiModelProperty(value = "反馈数据集")
    private T dataSet;

    /**
     * 反馈的时间戳
     */
    @ApiModelProperty(value = "反馈的时间戳")
    private Long feedbackTs;

    /**
     * 事件的最终消费者
     */
    @ApiModelProperty(value = "事件的最终消费者")
    private String consumed;

    /**
     * 事件是否已结束
     */
    @ApiModelProperty(value = "是否结束  YES/NO")
    private String isCompleted;
}
