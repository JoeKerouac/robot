package com.joe.tools;

import java.util.Collection;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionLikeType;
import com.fasterxml.jackson.databind.type.MapType;

@Component
public class JsonParser {
	private static final ObjectMapper mapper = new ObjectMapper();
	private static final Logger logger = LoggerFactory.getLogger(JsonParser.class);

	/**
	 * 将Object序列化
	 * 
	 * @param obj
	 * @return 序列化失败将返回空字符串
	 */
	public String toJson(Object obj) {
		return toJson(obj, false);
	}

	/**
	 * 将Object序列化
	 * 
	 * @param obj
	 *            要序列化的对象
	 * @param ignoreNull
	 *            是否忽略空元素
	 *            <li>true：忽略</li>
	 *            <li>false：不忽略</li>
	 * @return 序列化失败将返回空字符串
	 */
	public String toJson(Object obj, boolean ignoreNull) {
		try {
			if (ignoreNull) {
				mapper.setSerializationInclusion(Include.NON_NULL);
			} else {
				mapper.setSerializationInclusion(Include.ALWAYS);
			}
			return mapper.writeValueAsString(obj);
		} catch (Exception e) {
			logger.error("序列化失败，失败原因：", e);
			return "";
		}
	}

	/**
	 * 解析json
	 * 
	 * @param content
	 *            json字符串
	 * @param type
	 *            json解析后对应的实体类型
	 * @return 解析失败将返回null
	 */
	public <T> T readAsObject(String content, Class<T> type) {
		try {
			return mapper.readValue(content, type);
		} catch (Exception e) {
			logger.error("json解析失败，失败原因：", e);
			return null;
		}
	}

	/**
	 * 将json数据读取为带泛型的map类型的数据
	 * 
	 * @param content
	 *            json数据
	 * @param mapType
	 *            要返回的map类型
	 * @param keyType
	 *            map的key的泛型
	 * @param valueType
	 *            map的value的泛型
	 * @return map
	 */
	public <T extends Map<K, V>, K, V> T readAsMap(String content,
			@SuppressWarnings("rawtypes") Class<? extends Map> mapType, Class<K> keyType, Class<V> valueType) {
		try {
			MapType type = mapper.getTypeFactory().constructMapType(mapType, keyType, valueType);
			return mapper.readValue(content, type);
		} catch (Exception e) {
			logger.error("json解析失败，失败原因：", e);
			return null;
		}
	}

	/**
	 * 将json读取为collection类型的数据
	 * 
	 * @param content
	 *            json数据
	 * @param collectionType
	 *            collection类型
	 * @param elementsType
	 *            collection泛型
	 * @return
	 */
	public <T extends Collection<V>, V> T readAsCollection(String content,
			@SuppressWarnings("rawtypes") Class<? extends Collection> collectionType, Class<V> elementsType) {
		try {
			CollectionLikeType type = mapper.getTypeFactory().constructCollectionLikeType(collectionType, elementsType);
			return mapper.readValue(content, type);
		} catch (Exception e) {
			logger.error("json解析失败，失败原因：", e);
			return null;
		}
	}
}
