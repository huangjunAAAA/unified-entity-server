/*
 *      Copyright (c) 2018-2028, Chill Zhuang All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *
 *  Redistributions of source code must retain the above copyright notice,
 *  this list of conditions and the following disclaimer.
 *  Redistributions in binary form must reproduce the above copyright
 *  notice, this list of conditions and the following disclaimer in the
 *  documentation and/or other materials provided with the distribution.
 *  Neither the name of the dreamlu.net developer nor the names of its
 *  contributors may be used to endorse or promote products derived from
 *  this software without specific prior written permission.
 *  Author: Chill 庄骞 (smallchill@163.com)
 */
package com.zjht.unified.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.PackageVersion;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import com.wukong.core.weblog.utils.*;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Jackson工具类
 *
 * @author Chill
 */
@Slf4j
public class JsonUtilExt {

	private static final String TIME_PREFIX="TIME:";

	private static final String TIME_PATTERN_EXT="'"+TIME_PREFIX+"'"+DateUtil.PATTERN_DATETIME;

	/**
	 * 将对象序列化成json字符串
	 *
	 * @param value javaBean
	 * @return jsonString json字符串
	 */
	public static <T> String toJson(T value) {
		try {
			if(value==null)
				return null;
			return getInstance().writeValueAsString(value);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return "此参数不能序列化为json: "+value;
		}
	}

	/**
	 * 将对象序列化成 json byte 数组
	 *
	 * @param object javaBean
	 * @return jsonString json字符串
	 */
	public static byte[] toJsonAsBytes(Object object) {
		try {
			return getInstance().writeValueAsBytes(object);
		} catch (JsonProcessingException e) {
			throw Exceptions.unchecked(e);
		}
	}

	/**
	 * 将json反序列化成对象
	 *
	 * @param content       content
	 * @return Bean
	 */
	public static Map<String, Object> parseFlattenMap(String content) {
		try {
			Map<String, Object> values = getInstance().readValue(content, new TypeReference<Map<String, Object>>() {
			});
			handleDate(values);
			return values;
		} catch (IOException e) {
			throw Exceptions.unchecked(e);
		}
	}

	private static void handleDate(Map<String, Object> values){
		for (Iterator<Map.Entry<String, Object>> iterator = values.entrySet().iterator(); iterator.hasNext(); ) {
			Map.Entry<String, Object> entry =  iterator.next();
			Object val = entry.getValue();
			if(val instanceof String && val.toString().startsWith(TIME_PREFIX)){
				Date dv=DateUtil.parse(val.toString().substring(TIME_PREFIX.length()),DateUtil.PATTERN_DATETIME);
				if(dv!=null)
					entry.setValue(dv);
			}
		}
	}

	/**
	 * 将json byte 数组反序列化成对象
	 *
	 * @param bytes     json bytes
	 * @return Bean
	 */
	public static Map<String, Object> parseFlattenMap(byte[] bytes) {
		try {
			Map<String, Object> values = getInstance().readValue(bytes, new TypeReference<Map<String, Object>>() {
			});
			handleDate(values);
			return values;
		} catch (IOException e) {
			throw Exceptions.unchecked(e);
		}
	}

	public static <T,K> T jsonCast(K dto, Class<T> clz) {
		String json = JsonUtil.toJson(dto);
		return JsonUtil.parse(json, clz);
	}


	/**
	 * 将json反序列化成List对象
	 *
	 * @param content      content
	 * @return List<T>
	 */
	public static List<Map<String, Object>> parseMapList(String content) {
		try {

			if (!StringUtil.startsWithIgnoreCase(content, StringPool.LEFT_SQ_BRACKET)) {
				content = StringPool.LEFT_SQ_BRACKET + content + StringPool.RIGHT_SQ_BRACKET;
			}

			List<Map<String, Object>> list = getInstance().readValue(content, new TypeReference<List<Map<String, Object>>>() {
			});
			list.stream().forEach(l->handleDate(l));
			return list;
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		}
		return null;
	}

	public static ObjectMapper getInstance() {
		return JacksonHolder.INSTANCE;
	}

	private static class JacksonHolder {
		private static final ObjectMapper INSTANCE = new JacksonObjectMapper();
	}

	private static class JacksonObjectMapper extends ObjectMapper {
		private static final long serialVersionUID = 4288193147502386170L;

		private static final Locale CHINA = Locale.CHINA;

		public JacksonObjectMapper(ObjectMapper src) {
			super(src);
		}

		public JacksonObjectMapper() {
			super();
			//设置地点为中国
			super.setLocale(CHINA);
			//去掉默认的时间戳格式
			super.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
			//设置为中国上海时区
			super.setTimeZone(TimeZone.getTimeZone(ZoneId.systemDefault()));
			//序列化时，日期的统一格式
			super.setDateFormat(new SimpleDateFormat(TIME_PATTERN_EXT, Locale.CHINA));
			// 单引号
			super.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
			// 允许JSON字符串包含非引号控制字符（值小于32的ASCII字符，包含制表符和换行符）
			super.configure(JsonReadFeature.ALLOW_UNESCAPED_CONTROL_CHARS.mappedFeature(), true);
			super.configure(JsonReadFeature.ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER.mappedFeature(), true);
			super.findAndRegisterModules();
			//失败处理
			super.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
			super.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			//单引号处理
			super.configure(JsonReadFeature.ALLOW_SINGLE_QUOTES.mappedFeature(), true);
			//反序列化时，属性不存在的兼容处理s
			super.getDeserializationConfig().withoutFeatures(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
			//日期格式化
			super.registerModule(new JavaTimeModule());
			super.findAndRegisterModules();

			super.setSerializationInclusion(JsonInclude.Include.NON_NULL);
		}

		@Override
		public ObjectMapper copy() {
			return new JacksonObjectMapper(this);
		}
	}

	public static class JavaTimeModule extends SimpleModule {
		public JavaTimeModule() {
			super(PackageVersion.VERSION);
			this.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern(TIME_PATTERN_EXT)));
			this.addDeserializer(LocalDate.class, new LocalDateDeserializer(DateTimeFormatter.ofPattern(TIME_PATTERN_EXT)));
			this.addDeserializer(LocalTime.class, new LocalTimeDeserializer(DateTimeFormatter.ofPattern(TIME_PATTERN_EXT)));
			this.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(DateTimeFormatter.ofPattern(TIME_PATTERN_EXT)));
			this.addSerializer(LocalDate.class, new LocalDateSerializer(DateTimeFormatter.ofPattern(TIME_PATTERN_EXT)));
			this.addSerializer(LocalTime.class, new LocalTimeSerializer(DateTimeFormatter.ofPattern(TIME_PATTERN_EXT)));
		}

	}

}
