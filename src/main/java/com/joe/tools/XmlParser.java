package com.joe.tools;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * XML解析
 * 
 * @author Administrator
 *
 */
@Component
public class XmlParser {
	private static final Logger logger = LoggerFactory.getLogger(XmlParser.class);

	/**
	 * 解析XML，将xml解析为map（注意：如果XML是&lt;a&gt;data&lt;b&gt;bbb&lt;/b&gt;&lt;/a&gt;这种格式那么data将不被解析）
	 * 
	 * @param xml
	 *            xml字符串
	 * @return 由xml解析的TreeMap
	 */
	@SuppressWarnings("unchecked")
	public TreeMap<String, Object> parse(String xml) {
		try {
			Document document = DocumentHelper.parseText(xml);
			Element root = document.getRootElement();
			if (root.elements().size() == 0) {
				TreeMap<String, Object> map = new TreeMap<String, Object>();
				map.put(root.getName(), root.getText());
				return map;
			} else {
				return (TreeMap<String, Object>) parse(root);
			}
		} catch (Exception e) {
			logger.error("xml格式不正确", e);
			return null;
		}
	}

	/**
	 * 将XML解析为POJO对象（只能解析简单的对象（对象字段只能包含八大基本类型），复杂对象无法解析）
	 * 
	 * @param xml
	 *            XML源
	 * @param clazz
	 *            POJO对象的class
	 * @return
	 */
	public <T extends Object> T parse(String xml, Class<T> clazz) {
		if (xml == null || clazz == null || xml.isEmpty()) {
			return null;
		}
		T pojo = null;
		Document document = null;
		Map<String, String> fileds = null;

		// 获取pojo对象的实例
		try {
			// 没有权限访问该类或者该类（为接口、抽象类）不能实例化时将抛出异常
			pojo = clazz.newInstance();
		} catch (Exception e) {
			logger.error("class对象生成失败，请检查代码；失败原因：" + e.toString());
			return null;
		}

		// 解析XML
		try {
			document = DocumentHelper.parseText(xml);
		} catch (Exception e) {
			logger.error("xml解析错误", e);
			return null;
		}

		// 获取pojo对象的说明
		PropertyDescriptor[] propertyDescriptor = PropertyUtils.getPropertyDescriptors(pojo);
		fileds = new TreeMap<String, String>();
		for (PropertyDescriptor descript : propertyDescriptor) {
			String name = descript.getName();
			fileds.put(name.toLowerCase(), name);
		}

		Element root = document.getRootElement();
		@SuppressWarnings("unchecked")
		List<Element> elements = root.elements();

		// 遍历xml节点，查看要生成的pojo对象中是否有该节点对应的字段，如果有
		for (Element element : elements) {
			String elementName = element.getName();
			String filedName = fileds.get(elementName.toLowerCase());
			logger.debug("elementName为{}，filedName为{}", elementName, filedName);
			if (filedName != null) {
				try {
					BeanUtils.setProperty(pojo, filedName, element.getText());
				} catch (Exception e) {
					logger.error("copy中复制" + filedName + "时发生错误，忽略该字段");
					logger.debug("copy中复制" + filedName + "时发生错误", e);
				}
			}
		}
		return pojo;
	}

	/**
	 * 将Object解析为xml，字段值为null的将不包含在xml中，暂时只能解析基本类型（list、map等不能正确解析）
	 * 
	 * @param source
	 *            bean
	 * @param rootName
	 *            根节点名称
	 * @param hasNull
	 *            是否包含null元素（true：包含）
	 * @return
	 */
	public String toXml(Object source, String rootName, boolean hasNull) {
		Long start = System.currentTimeMillis();
		Document document = DocumentHelper.createDocument();
		Element root = document.addElement(rootName);
		// 获取bean的字段的说明
		PropertyDescriptor[] descrips = PropertyUtils.getPropertyDescriptors(source);
		Class<?> sourceClass = source.getClass();
		for (PropertyDescriptor descrip : descrips) {
			Element element = null;
			// 获取字段名称
			String filedName = descrip.getName();
			String elementName = filedName;
			if ("class".equals(filedName)) {
				continue;
			}
			try {
				String value = BeanUtils.getProperty(source, filedName);
				if (value == null) {
					if (hasNull) {
						value = "null";
					} else {
						continue;
					}
				}
				Field filed = sourceClass.getDeclaredField(filedName);
				filed.isAnnotationPresent(XmlNode.class);
				boolean cdata = false;
				// 判断该字段是否有XmlNode注解
				if (filed.isAnnotationPresent(XmlNode.class)
						|| descrip.getReadMethod().isAnnotationPresent(XmlNode.class)) {
					// 获取注解
					XmlNode xmlNode = filed.getAnnotation(XmlNode.class) != null ? filed.getAnnotation(XmlNode.class)
							: descrip.getReadMethod().getAnnotation(XmlNode.class);
					// 是否忽略该节点
					if (xmlNode.ignore()) {
						continue;
					}
					// 判断是否是CDATA
					if (xmlNode.isCDATA() || xmlNode.isCDATA()) {
						cdata = true;
					}
					// 解析该节点的名称
					if (!"".equals(xmlNode.name())) {
						elementName = xmlNode.name();
					}
				}
				element = createElement(elementName, value, cdata);
				root.add(element);
			} catch (Exception e) {
				logger.info("bean的" + filedName + "字段解析失败，该字段将不包含在XML中", e);
				continue;
			}
		}
		Long end = System.currentTimeMillis();
		logger.info("解析xml用时" + (end - start) + "ms");
		return root.asXML();
	}

	@SuppressWarnings("unchecked")
	private Object parse(Element element) {
		List<Element> elements = element.elements();
		if (elements.size() == 0) {
			return element.getText();
		} else {
			Map<String, Object> map = new TreeMap<String, Object>();
			for (Element ele : elements) {
				String key = ele.getName();
				if(map.get(key) == null){
					map.put(key, parse(ele));
				}else if(map.get(key) instanceof List){
					((List<Object>)map.get(key)).add(parse(ele));
				}else{
					Object o = map.get(key);
					List<Object> list = new ArrayList<Object>();
					list.add(o);
					list.add(parse(ele));
					map.put(key, list);
				}
			}
			return map;
		}
	}

	/**
	 * 创建一个Element
	 * 
	 * @param name
	 *            element的name
	 * @param text
	 *            内容
	 * @param isCDATA
	 *            text是否需要用CDATA包裹（true：需要）
	 * @return element
	 */
	private Element createElement(String name, String text, boolean isCDATA) {
		Element element = DocumentHelper.createElement(name);
		if (isCDATA) {
			element.add(DocumentHelper.createCDATA(text));
		} else {
			element.setText(text);
		}
		return element;
	}
}
