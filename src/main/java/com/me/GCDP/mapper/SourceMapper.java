package com.me.GCDP.mapper;

import java.util.List;

import com.me.GCDP.model.BaseModel;
import com.me.GCDP.model.SourceSubscribe;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Company: ifeng.com</p>
 * @author :huwq
 * @version 1.0
 * <p>--------------------------------------------------------------------</p>
 * <p>date                   author                    reason             </p>
 * <p>2011-7-19              huwq               create the class      </p>
 * <p>--------------------------------------------------------------------</p>
 */

public interface SourceMapper<T extends BaseModel> extends BaseMapper<T> {
	
	public List<SourceSubscribe> getSubscribeList(SourceSubscribe subscribe);
	
	public void insertSubscribe(SourceSubscribe subscribe);
	
	public void updateSubscribe(SourceSubscribe subscribe);
	
	public void updateSubscribeStatus(SourceSubscribe subscribe);
	
	public void deleteSubscribe(SourceSubscribe subscribe);
	
}
