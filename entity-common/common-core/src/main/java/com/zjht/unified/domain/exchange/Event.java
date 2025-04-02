package com.zjht.unified.domain.exchange;

import lombok.Data;

import java.util.List;

@Data
public class Event {
    private String key;

    private List<Script> scripts;
}
