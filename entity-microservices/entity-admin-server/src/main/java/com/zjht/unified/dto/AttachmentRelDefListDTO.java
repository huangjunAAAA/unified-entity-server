package com.zjht.unified.dto;

import lombok.Data;

import java.util.List;

@Data
public class AttachmentRelDefListDTO {
    private List<Long> id;

    /**
     * 挂载ID
     */
    private List<String> attachmentId;

    /**
     * 挂载类型
     */
    private List<String> attachmentType;

    /**
     * 挂载图ID
     */
    private List<String> attachmentGraphId;

    /**
     * 被挂载ID
     */
    private List<String> attachAtId;

    /**
     * 被挂载类型
     */
    private List<String> attachAtType;

    /**
     * 被挂载图ID
     */
    private List<String> attachAtGraphId;
}