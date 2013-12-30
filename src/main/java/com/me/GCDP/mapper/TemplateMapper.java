package com.me.GCDP.mapper;

import java.util.List;
import java.util.Map;

import com.me.GCDP.model.BaseModel;

public interface TemplateMapper<T extends BaseModel> extends BaseMapper<T> {
	/**
	 * 可插入字段，name,content,dataFormId,dataId,`enable`,powerPath
	 * createDate将会设置为NULL
	 * 获取插入ID的方法：
	 * Template t = new Template();
	 * t.setName("test");
	 * t.setContent("test");
	 * t.setDataFormID(1);
	 * t.setDataId(0); //0代表公共模版
	 * t.setEnable(1);
	 * t.setPowerPath("/test/");
	 * templateMapper.insert(t);
	 * int newId = t.getId();
	 * 全字段插入: 
	 * @see com.me.GCDP.mapper.TemplateMapper#insertFull(com.me.GCDP.model.TemplateModel)
	 * @param template
	 * @return 返回插入操作影响行数
	 */
	public int insert(T template);
	
	/**
	 * @see com.me.GCDP.mapper.TemplateMapper#insert(com.me.GCDP.model.TemplateModel)
	 * @param template
	 * @return
	 */
	public int insertFull(T template);

	public int deleteById(int id);

	public T getById(int id);

	public List<String> getPowerPathInFormConfig();

	public List<T> getByCustom(Map<String, Object> custom);
	
	public int countByCustom(String customWhere);
}
