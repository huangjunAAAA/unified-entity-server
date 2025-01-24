package com.zjht.unified.feign.model;

import lombok.Data;

import java.util.List;

@Data
public class ReturnMap<T> {
    private	Integer recordsTotal;
	private Integer recordsFiltered;
	private	List<T> data;
}
