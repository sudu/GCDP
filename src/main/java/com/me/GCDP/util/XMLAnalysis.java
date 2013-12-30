package com.me.GCDP.util;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

/**
 * 
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Company: ifeng.com</p>
 * @author :jiangy
 * @version 1.0
 * <p>--------------------------------------------------------------------</p>
 * <p>date                   author                    reason             </p>
 * <p>下午3:31:50              jiangy              create the class      </p>
 * <p>--------------------------------------------------------------------</p>
 */
public class XMLAnalysis {
	
	static Document document = null;
	
	public static Map<String,Object> allAnalysis(String xml){
		if(xml != null&&!xml.equals("")){
			initDoc(xml);
			Element element = document.getRootElement();
			String name = element.getPath();
			Map map = new HashMap();
			return doAttributeAndElement(element,map, name);
		}else
		return null;
	}
	
	private static void initDoc(String xml) {
		SAXReader saxReader = null;
		try {
			if (document == null) {
				saxReader = new SAXReader();
				StringReader sr = new StringReader(xml);
				document = saxReader.read(sr);

			}
		} catch (DocumentException e) {
			e.printStackTrace();
		}
	}
	
	private static Map<String, Object> doAttributeAndElement(Element element, Map map,String name) {
		Iterator<Attribute> ita = element.attributeIterator();
		while (ita.hasNext()) {
			Attribute att = ita.next();
			String lastName = att.getName() + "@" + name;
			if (map.containsKey(lastName)) {
				List<Object> list = new ArrayList<Object>();
				list.add(map.get(lastName));// list里面嵌套list
				list.add(att.getValue());
				map.put(lastName, list);
			} else {
				map.put(lastName, att.getValue());
			}
		}

		Iterator<Element> it = element.elementIterator();
		while (it.hasNext()) {
			Element info = (Element) it.next();
			String key = info.getName() + "." + name;
			if (info.elements().size() > 0) {
				doAttributeAndElement(info, map, key);
			} else {
				if (map.containsKey(key)) {
					List<Object> list = new ArrayList<Object>();
					list.add(map.get(key));// list里面嵌套list
					list.add(info.getText());
					map.put(key, list);
				} else {
					map.put(key, info.getText());
				}
			}
		}
		return map;
	}

	

}
