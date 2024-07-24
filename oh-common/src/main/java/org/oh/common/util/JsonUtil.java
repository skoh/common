/*
 * Copyright 2002-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.oh.common.util;

import com.fasterxml.jackson.databind.SerializationFeature;
import org.oh.common.annotation.ExcludeLogging;
import org.oh.common.exception.CommonError;
import org.oh.common.exception.CommonException;
import org.oh.common.model.CommonModel;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.github.wnameless.json.flattener.FlattenMode;
import com.github.wnameless.json.flattener.JsonFlattener;
import com.github.wnameless.json.unflattener.JsonUnflattener;
import com.google.common.collect.Streams;
import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * JSON 유틸리티
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public final class JsonUtil {
	/**
	 * jackson JSON 매퍼
	 */
	public static final ObjectMapper OBJECT_MAPPER = JsonMapper.builder()
			.enable(MapperFeature.DEFAULT_VIEW_INCLUSION)
			.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
			.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
			.serializationInclusion(JsonInclude.Include.NON_NULL)
			.build();
	/**
	 * GSON JSON 매퍼
	 */
	public static final Gson GSON = new GsonBuilder()
			.registerTypeHierarchyAdapter(JsonNode.class, new JsonNodeConverter())
			.setExclusionStrategies(new AnnotationBasedExclusionStrategy())
			.create();

	private static final ObjectWriter PRETTY_WRITER = OBJECT_MAPPER.writerWithDefaultPrettyPrinter();
	private static final CsvMapper CSV_MAPPER = (CsvMapper) new CsvMapper()
			.configure(JsonGenerator.Feature.IGNORE_UNKNOWN, true);

	/**
	 * 해당 객체를 JSON 정보로 변환
	 *
	 * @param value 객체
	 * @return JSON 정보
	 */
	public static JsonNode readTree(Object value) {
		try {
			return value instanceof String ? OBJECT_MAPPER.readTree((String) value) :
					OBJECT_MAPPER.valueToTree(value);
		} catch (JsonProcessingException e) {
			throw new CommonException(CommonError.COM_JSON_ERROR, "value: " + value, e);
		}
	}

	/**
	 * 해당 파일을 JSON 정보로 변환
	 *
	 * @param file 파일
	 * @return JSON 정보
	 */
	public static JsonNode readTree(File file) {
		try {
			return OBJECT_MAPPER.readTree(file);
		} catch (IOException e) {
			throw new CommonException(CommonError.COM_JSON_ERROR, "file: " + file.getAbsolutePath(), e);
		}
	}

	/**
	 * JSON 문자열을 맵 형태로 변환
	 *
	 * @param value JSON 문자열
	 * @return 맵
	 */
	public static Map<String, Object> readValueMap(String value) {
		return readValue(value, new TypeReference<Map<String, Object>>() {
		});
	}

	/**
	 * JSON 문자열을 리스트 형태로 변환
	 *
	 * @param value JSON 문자열
	 * @return 리스트
	 */
	public static List<Map<String, Object>> readValueListMap(String value) {
		return readValue(value, new TypeReference<List<Map<String, Object>>>() {
		});
	}

	/**
	 * JSON 문자열을 원한는 클래스 타입의 객체로 변환
	 *
	 * @param value     JSON 문자열
	 * @param valueType 클래스 타입
	 * @return 클래스 타입의 객체
	 */
	public static <T> T readValue(String value, Class<T> valueType) {
		try {
			return OBJECT_MAPPER.readValue(value, valueType);
		} catch (JsonProcessingException e) {
			throw new CommonException(CommonError.COM_JSON_ERROR,
					String.format("value: %s valueType: %s", value, valueType), e);
		}
	}

	/**
	 * JSON 문자열을 원한는 타입 레퍼런스의 객체로 변환
	 *
	 * @param value        JSON 문자열
	 * @param valueTypeRef 타입 레퍼런스
	 * @return 타입 레퍼런스의 객체
	 */
	public static <T> T readValue(String value, TypeReference<T> valueTypeRef) {
		try {
			return OBJECT_MAPPER.readValue(value, valueTypeRef);
		} catch (JsonProcessingException e) {
			throw new CommonException(CommonError.COM_JSON_ERROR,
					String.format("value: %s valueTypeRef: %s", value, valueTypeRef.getType()), e);
		}
	}

	/**
	 * JSON 문자열을 원한는 클래스 타입의 객체로 변환
	 *
	 * @param file      파일
	 * @param valueType 클래스 타입
	 * @return 클래스 타입의 객체
	 */
	public static <T> T readValue(File file, Class<T> valueType) {
		try {
			return OBJECT_MAPPER.readValue(file, valueType);
		} catch (IOException e) {
			throw new CommonException(CommonError.COM_JSON_ERROR, "file: " + file.getAbsolutePath(), e);
		}
	}

	/**
	 * JSON 문자열을 원한는 타입 레퍼런스의 객체로 변환
	 *
	 * @param file         파일
	 * @param valueTypeRef 타입 레퍼런스
	 * @return 타입 레퍼런스의 객체
	 */
	public static <T> T readValue(File file, TypeReference<T> valueTypeRef) {
		try {
			return OBJECT_MAPPER.readValue(file, valueTypeRef);
		} catch (IOException e) {
			throw new CommonException(CommonError.COM_JSON_ERROR, "file: " + file.getAbsolutePath(), e);
		}
	}

	/**
	 * 해당 객체를 원한는 클래스 타입의 객체로 변환
	 *
	 * @param value     객체
	 * @param valueType 클래스 타입
	 * @return 클래스 타입의 객체
	 */
	public static <T> T convertValue(Object value, Class<T> valueType) {
		JsonNode node = OBJECT_MAPPER.convertValue(value, JsonNode.class);
		try {
			return OBJECT_MAPPER.treeToValue(node, valueType);
		} catch (JsonProcessingException e) {
			throw new CommonException(CommonError.COM_JSON_ERROR,
					String.format("value: %s valueType: %s", value, valueType), e);
		}
	}

	/**
	 * 해당 객체를 원한는 타입 레퍼런스의 객체로 변환
	 *
	 * @param value        객체
	 * @param valueTypeRef 타입 레퍼런스
	 * @return 타입 레퍼런스의 객체
	 */
	public static <T> T convertValue(Object value, TypeReference<T> valueTypeRef) {
		JsonNode node = OBJECT_MAPPER.convertValue(value, JsonNode.class);
		return OBJECT_MAPPER.convertValue(node, valueTypeRef);
	}

	/**
	 * 해당 객체를 맵 형태로 변환
	 *
	 * @param value 객체
	 * @return 맵
	 */
	public static Map<String, Object> convertValueMap(Object value) {
		return OBJECT_MAPPER.convertValue(value, new TypeReference<Map<String, Object>>() {
		});
	}

	/**
	 * 해당 객체 리스트 형태로 변환
	 *
	 * @param value 객체
	 * @return 리스트
	 */
	public static List<Map<String, Object>> convertValueListMap(Object value) {
		return OBJECT_MAPPER.convertValue(value, new TypeReference<List<Map<String, Object>>>() {
		});
	}

	/**
	 * JSON 객체를 원한는 클래스 타입의 객체로 변환
	 *
	 * @param node      JSON 객체
	 * @param valueType 클래스 타입
	 * @return 클래스 타입의 객체
	 */
	public static <T> T treeToValue(TreeNode node, Class<T> valueType) {
		try {
			return OBJECT_MAPPER.treeToValue(node, valueType);
		} catch (JsonProcessingException e) {
			throw new CommonException(CommonError.COM_JSON_ERROR,
					String.format("node: %s valueType: %s", node, valueType), e);
		}
	}

	/**
	 * 해당 객체를 JSON 문자열로 변환
	 *
	 * @param value 객체
	 * @return JSON 문자열
	 */
	public static String writeValueAsString(Object value) {
		return writeValueAsString(OBJECT_MAPPER, value);
	}

	/**
	 * 해당 매퍼를 이용해서 객체를 JSON 문자열로 변환
	 *
	 * @param mapper 매퍼
	 * @param value  객체
	 * @return JSON 문자열
	 */
	public static String writeValueAsString(ObjectMapper mapper, Object value) {
		try {
			return mapper.writeValueAsString(value);
		} catch (JsonProcessingException e) {
			throw new CommonException(CommonError.COM_JSON_ERROR, "value: " + value, e);
		}
	}

	/**
	 * 해당 Writer를 이용해서 객체를 JSON 문자열로 변환
	 *
	 * @param writer Writer
	 * @param value  객체
	 * @return JSON 문자열
	 */
	public static String writeValueAsString(ObjectWriter writer, Object value) {
		try {
			return writer.writeValueAsString(value);
		} catch (JsonProcessingException e) {
			throw new CommonException(CommonError.COM_JSON_ERROR, "value: " + value, e);
		}
	}

	///////////////////////////////////////////////////////////////////////////

	/**
	 * 파일을 JSON 객체로 변환
	 *
	 * @param filePath 파일 경로
	 * @return JSON 객체
	 */
	public static JsonNode readFile(String filePath) {
		File file = FileUtil.createFile(filePath);
		try {
			return OBJECT_MAPPER.readTree(file);
		} catch (IOException e) {
			throw new CommonException(CommonError.COM_JSON_ERROR, "file: " + file.getAbsolutePath(), e);
		}
	}

	/**
	 * 해당 객체를 파일 경로에 생성
	 *
	 * @param filePath 파일 경로
	 * @param value    객체
	 */
	public static void writeFile(String filePath, Object value) {
		File file = FileUtil.createFile(filePath);
		try {
			OBJECT_MAPPER.writeValue(file, value);
		} catch (IOException e) {
			throw new CommonException(CommonError.COM_JSON_ERROR,
					String.format("file: %s value: %s", file.getAbsolutePath(), value), e);
		}
	}

	///////////////////////////////////////////////////////////////////////////

	/**
	 * 해당 객체를 JSON 문자열로 변환
	 *
	 * @param value 객체
	 * @return JSON 문자열
	 */
	public static String toString(Object value) {
		return Optional.ofNullable(getObjectNotThrow(value))
				.map(v -> {
					try {
						return OBJECT_MAPPER.writeValueAsString(v);
					} catch (JsonProcessingException e) {
						log.debug(ExceptionUtil.getMessageAndType(e));
						return value.toString();
					}
				})
				.orElse("");
	}

	/**
	 * 해당 객체를 JSON 뷰 모델에 맞게 선택한 필드만 변환
	 *
	 * @param value    객체
	 * @param jsonView JSON 뷰 모델
	 * @return JSON 문자열
	 */
	public static String toString(Object value, Class<? extends CommonModel.None> jsonView) {
		return Optional.ofNullable(getObjectNotThrow(value))
				.map(v -> {
					try {
						return OBJECT_MAPPER.writerWithView(jsonView)
								.writeValueAsString(v);
					} catch (JsonProcessingException e) {
						log.debug(ExceptionUtil.getMessageAndType(e));
						return value.toString();
					}
				})
				.orElse("");
	}

	/**
	 * 해당 객체를 포맷팅된 JSON 문자열로 변환
	 *
	 * @param value 객체
	 * @return 포맷팅된 JSON 문자열
	 */
	public static String toPrettyString(Object value) {
		return Optional.ofNullable(getObject(value))
				.map(v -> writeValueAsString(PRETTY_WRITER, v))
				.orElse("");
	}

	/**
	 * 해당 객체를 JSON 뷰 모델에 맞게 선택한 필드만 포맷팅된 JSON 문자열로 변환
	 *
	 * @param value    객체
	 * @param jsonView JSON 뷰 모델
	 * @return 포맷팅된 JSON 문자열
	 */
	public static String toPrettyString(Object value, Class<? extends CommonModel.None> jsonView) {
		return toPrettyString(toString(value, jsonView));
	}

	private static Object getObject(Object value) {
		return value instanceof String ? readTree(value) : value;
	}

	private static Object getObjectNotThrow(Object value) {
		try {
			return getObject(value);
		} catch (CommonException e) {
			log.debug(ExceptionUtil.getMessageAndType(e));
			return value.toString();
		}
	}

	///////////////////////////////////////////////////////////////////////////

	/**
	 * 원본 객체를 복사한 후 대상 객체를 병합하여 원한는 클래스 타입의 객체로 변환
	 *
	 * @param source    원본 객체
	 * @param target    대상 객체
	 * @param valueType 클래스 타입
	 * @return 클래스 타입의 객체
	 */
	public static <T> T copyAndMerge(T source, T target, Class<T> valueType) {
		T sourceTemp = copy(source, valueType);
		return merge(sourceTemp, target);
	}

	/**
	 * 해당 객체를 복사하여 원한는 클래스 타입의 객체로 변환
	 *
	 * @param value     객체
	 * @param valueType 클래스 타입
	 * @return 클래스 타입의 객체
	 */
	public static <T> T copy(T value, Class<T> valueType) {
		try {
			return OBJECT_MAPPER.readValue(writeValueAsString(OBJECT_MAPPER, value), valueType);
		} catch (JsonProcessingException e) {
			throw new CommonException(CommonError.COM_JSON_ERROR,
					String.format("value: %s valueType: %s", value, valueType), e);
		}
	}

	/**
	 * 원본 객체를 대상 객체와 병합하여 변환
	 *
	 * @param source 원본 객체
	 * @param target 대상 객체
	 * @return 병합한 객체
	 */
	public static <T> T merge(T source, T target) {
		try {
			JsonNode targetTemp = OBJECT_MAPPER.valueToTree(target);
			return OBJECT_MAPPER.readerForUpdating(source)
					.readValue(targetTemp);
		} catch (IOException e) {
			throw new CommonException(CommonError.COM_JSON_ERROR,
					String.format("source: %s target: %s", source, target), e);
		}
	}

	///////////////////////////////////////////////////////////////////////////

	/**
	 * 해당 객체를 CVS 형태의 문자열로 변환
	 * https://github.com/FasterXML/jackson-dataformats-text/tree/master/csv 참고
	 *
	 * @param value 객체
	 * @param sep   구분자
	 * @return CVS 형태의 문자열
	 */
	public static String toCsv(Object value, String sep) {
		JsonNode jsonNode = readTree(value);

		Iterator<String> fieldNames;
		if (jsonNode.isArray()) {
			ArrayNode arrayNode = (ArrayNode) jsonNode;
			if (arrayNode.size() > 0) {
				fieldNames = arrayNode.get(0).fieldNames();
			} else {
				return "";
			}
		} else {
			fieldNames = jsonNode.fieldNames();
		}
		String fieldNamesTemp = Streams.stream(fieldNames)
				.collect(Collectors.joining(sep));

		CsvSchema csvSchema = getCvsScheme(fieldNamesTemp, sep, true);
		return toCvs(jsonNode, csvSchema, null);
	}

	/**
	 * 해당 객체를 CVS 형태의 문자열로 변환
	 *
	 * @param value      객체
	 * @param sep        구분자
	 * @param fieldNames 필드 리스트
	 * @param header     헤더 리스트
	 * @return CVS 형태의 문자열
	 */
	public static String toCsv(Object value, String sep, String fieldNames, String header) {
		CsvSchema csvSchema = getCvsScheme(fieldNames, sep, false);
		return toCvs(value, csvSchema, header);
	}

	/**
	 * 해당 객체를 CVS 형태의 문자열로 변환
	 *
	 * @param value     객체
	 * @param csvSchema CVS 스키마
	 * @param header    헤더 리스트
	 * @return CVS 형태의 문자열
	 */
	public static String toCvs(Object value, CsvSchema csvSchema, String header) {
		Object valueTemp = getObject(value);
		ObjectWriter writer = CSV_MAPPER.writer(csvSchema);
		String headerTemp = !csvSchema.usesHeader()
				&& StringUtils.isNotEmpty(header) ? header + '\n' : "";
		String body = writeValueAsString(writer, valueTemp);
		return new StringBuilder()
				.append(headerTemp)
				.append(body)
				.toString();
	}

	/**
	 * 조건에 만족하는 CVS 스키마를 반환
	 *
	 * @param fieldNames 필드 리스트
	 * @param sep        구분자
	 * @param autoHeader 헤더 사용 여부
	 * @return CVS 스키마
	 */
	public static CsvSchema getCvsScheme(String fieldNames, String sep, boolean autoHeader) {
		CsvSchema.Builder csvBuilder = CsvSchema.builder();
		Arrays.stream(fieldNames.split(sep))
				.forEach(csvBuilder::addColumn);
		return autoHeader ?
				csvBuilder.build()
						.withHeader()
						.withColumnSeparator(sep.charAt(0)) :
				csvBuilder.build()
						.withColumnSeparator(sep.charAt(0));
	}

	/**
	 * CVS 문자열을 CVS 스키마에 맞게 객체로 변환
	 *
	 * @param csv       CVS 문자열
	 * @param csvSchema CVS 스키마
	 * @param type      클래스 타입
	 * @return 객체 목록
	 */
	public static <T> List<T> toPojo(String csv, CsvSchema csvSchema, Class<T> type) {
		try {
			MappingIterator<T> iterator = CSV_MAPPER.readerFor(type)
					.with(csvSchema)
					.readValues(csv);
			return iterator.readAll();
		} catch (IOException e) {
			throw new CommonException(CommonError.COM_JSON_ERROR, "csv: " + csv, e);
		}
	}

	/**
	 * flatten된 JSON 문자열을 반환
	 * https://github.com/wnameless/json-flattener 참고
	 *
	 * @param json JSON 문자열
	 * @return flatten된 JSON 문자열
	 */
	public static String flatten(String json) {
		return new JsonFlattener(json)
				.withFlattenMode(FlattenMode.KEEP_ARRAYS)
				.flatten();
	}

	/**
	 * unflatten된 JSON 문자열을 반환
	 *
	 * @param json flatten된 JSON 문자열
	 * @return unflatten된 JSON 문자열
	 */
	public static String unflatten(String json) {
		return new JsonUnflattener(json)
				.unflatten();
	}

	///////////////////////////////////////////////////////////////////////////

	private static final class JsonNodeConverter
			implements JsonSerializer<JsonNode> {
		@Override
		public JsonElement serialize(JsonNode src, Type typeOfSrc, JsonSerializationContext context) {
			return GSON.fromJson(src.toString(), JsonElement.class);
		}
	}

	private static final class AnnotationBasedExclusionStrategy
			implements ExclusionStrategy {
		@Override
		public boolean shouldSkipField(FieldAttributes f) {
			return f.getAnnotation(ExcludeLogging.class) != null;
		}

		@Override
		public boolean shouldSkipClass(Class<?> clazz) {
			return false;
		}
	}
}
