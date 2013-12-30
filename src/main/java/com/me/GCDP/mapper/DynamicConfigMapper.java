package com.me.GCDP.mapper;

import java.util.List;

import com.me.GCDP.model.BaseModel;

public interface DynamicConfigMapper<T extends BaseModel> extends BaseMapper<T> {
	public List<T> getAllDyns();

}
