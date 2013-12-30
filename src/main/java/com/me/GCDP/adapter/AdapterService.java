package com.me.GCDP.adapter;

import java.util.List;
import java.util.Map;

import com.me.GCDP.mapper.NodeMapper;
import com.me.GCDP.model.Node;
import com.me.GCDP.util.SpringContextUtil;
import com.me.GCDP.xform.SysInitializationService;
import com.me.json.JSONArray;
import com.me.json.JSONException;
import com.me.json.JSONObject;

/**
 * 
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Company: ifeng.com</p>
 * @author :dengzc
 * @version 1.0
 * <p>--------------------------------------------------------------------</p>
 * <p>date                   author                    reason                    </p>
 * <p>2013-12-5        dengzc               create the class             </p>
 * <p>--------------------------------------------------------------------</p>
 */
public class AdapterService {

	/**
	 * 从节点环境变量配置中获取文章字段映射配置信息
	 * @param nodeId
	 * @return
	 * @throws JSONException
	 */
	public JSONObject getDocFieldCfg(int nodeId) throws JSONException {
		return getFieldCfg(nodeId, SysInitializationService.KEY_docFieldAdapterCfg);
	}
	
	/**
	 * 从节点环境变量配置中获取栏目字段映射配置信息
	 * @param nodeId
	 * @return
	 * @throws JSONException
	 */
	public JSONObject getColumnFieldCfg(int nodeId) throws JSONException {
		return getFieldCfg(nodeId, SysInitializationService.KEY_columnFieldAdapterCfg);
	}
	
	/**
	 * 字段（输入参数）的适配转换
	 * @param fd 字段（输入参数），格式为 "field1,field2,field3"
	 * @param fieldMap 字段映射map
	 * @return 适配转换后的字段，格式为 "adapterfield1,adapterfield2,adapterfield3"（若没有相应映射，则去除）
	 */
	public String getInAdapterFd(String fd, Map<String, String> fieldMap) {
		if (fd == null || fd.isEmpty()) {
			return "";
		}
		StringBuilder sb = new StringBuilder();
		for (String s : fd.split(",")) {
			if (!s.equals("id")) {
				String mapField = fieldMap.get(s);
				if (mapField != null && !mapField.isEmpty()) {
					sb.append(mapField).append(",");
				}
			} else {
				sb.append("id").append(",");
			}
		}
		if (sb.length() < 1) {
			return "";
		} else {
			return sb.substring(0, sb.length() - 1);
		}
	}
	/**
	 * 查询条件（输入参数）的适配转换
	 * @param q 查询条件（输入参数），格式为 "value1:field1,value2:field2,value3:field3"
	 * @param fieldMap 字段映射map
	 * @return 适配转换后的查询条件，格式为 "value1:adapterfield1,value2:adapterfield2,value3:adapterfield3"（若没有相应映射，则去除）
	 */
	public String getInAdapterQ(String q, Map<String, String> fieldMap) {
		if (q == null || q.isEmpty()) {
			return "";
		}
		StringBuilder sb = new StringBuilder();
		for (String s : q.split(",")) {
			String[] a = s.split(":");
			if (a.length < 1) {
				continue;
			}
			if (!a[1].equals("id")) {
				String mapField = fieldMap.get(a[1]);
				if (mapField != null && !mapField.isEmpty()) {
					sb.append(a[0]).append(":").append(mapField).append(",");
				}
			} else {
				sb.append(a[0]).append(":id,");
			}
		}
		if (sb.length() < 1) {
			return "";
		} else {
			return sb.substring(0, sb.length() - 1);
		}
	}

	/**
	 * 排序（输入参数）的适配转换
	 * @param sort 排序（输入参数），格式为 [{"field":"field1","order":"desc"},{"field":"field2","order":"desc"}]
	 * @param fieldMap 字段映射map
	 * @return 适配转换后的排序，格式为 [{"field":"adapterfield1","order":"desc"},{"field":"adapterfield2","order":"desc"}]（若没有相应映射，则去除）
	 * @throws JSONException
	 */
	public String getInAdapterSort(String sort, Map<String, String> fieldMap) throws JSONException {
		JSONArray jsArr = new JSONArray(sort);
		for (int i = 0; i < jsArr.length(); i++) {
			@SuppressWarnings("unchecked")
			Map<String, String> map = jsArr.getJSONObject(i).toMap();
			if (!map.get("field").equals("id")) {
				String mapField = fieldMap.get(map.get("field"));
				if (mapField != null && !mapField.isEmpty()) {
					map.put("field", mapField);
				} else {
					jsArr.remove(i);
				}
			}
		}
		return jsArr.toString();
	}
	
	/**
	 * 结果（输出参数）的适配转换（fieldMap数据改变）
	 * @param data 结果（输出参数），格式为  {totalCount:2,data:[{"adapterfield1":"2","adapterfield2":"xx"},{"adapterfield1":"1","adapterfield2":"xx"}]}
	 * @param fieldMap 字段映射map
	 * @return 适配转换后的结果，格式为 {totalCount:2,data:[{"field1":"2","field2":"xx"},{"field1":"1","field2":"xx"}]}
	 * @throws JSONException
	 */
	public String getOutAdapterData(String data, Map<String, String> fieldMap) throws JSONException {
		reverseMap(fieldMap);
		JSONObject ret = new JSONObject(data);
		JSONArray jsonArray = ret.getJSONArray("data");

		if (jsonArray.length() < 1) {
			return data;
		}
		@SuppressWarnings("unchecked")
		String[] keys = ((Map<String, String>) jsonArray.getJSONObject(0).toMap()).keySet().toArray(new String[0]);
		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject obj = jsonArray.getJSONObject(i);
			for (String key : keys) {
				if ("id".equals(key)) {
					continue;
				}
				obj.put(fieldMap.get(key), obj.get(key));
				obj.remove(key);
			}
		}
		return ret.toString();
	}
	
	
	/**
	 * 从节点环境变量配置中获取字段映射配置信息
	 * @param nodeId
	 * @param key
	 * @return
	 * @throws JSONException
	 */
	private JSONObject getFieldCfg(int nodeId, String key) throws JSONException {
		@SuppressWarnings("unchecked")
		NodeMapper<Node> nodeMapper = (NodeMapper<Node>) SpringContextUtil.getBean("nodeMapper");
		Node node = new Node();
		node.setId(nodeId);
		List<Node> list = nodeMapper.get(node);
		if (list == null || list.size() < 1) {
			throw new RuntimeException("节点【"+ nodeId + "】没有节点配置数据！");
		}
		List<Map<String, Object>> envList = list.get(0).getEnvMap();
		for (Map<String, Object> map : envList) {
			if (key.equals(map.get("key"))) {
				return new JSONObject(map.get("value").toString());
			}
		}
		throw new RuntimeException("节点【" + nodeId + "】环境变量配置数据中没有key【" + key + "】的配置数据！请先配置");
	}
	
	/**
	 * key，value对调（过滤掉原map value为空；若存在相同value，则覆盖）
	 * @param map
	 */
	private void reverseMap(Map<String, String> map) {
		String[] keys = map.keySet().toArray(new String[0]);
		for (String key : keys) {
			String value = map.get(key);
			if (value != null && !value.isEmpty()) {
				map.put(value, key);
			}
			map.remove(key);
		}
	}
}
