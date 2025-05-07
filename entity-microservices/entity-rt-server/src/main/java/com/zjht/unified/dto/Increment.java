package com.zjht.unified.dto;

import lombok.Data;

@Data
public class Increment<T> {
    private String ver;
    private T data;
}
