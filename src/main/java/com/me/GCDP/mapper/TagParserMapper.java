package com.me.GCDP.mapper;

import java.util.List;

import com.me.GCDP.model.BaseModel;

public interface TagParserMapper<T extends BaseModel> extends BaseMapper<T> {
	public List<T> getByKey(String key);
	public List<T> getById(int id);
}
