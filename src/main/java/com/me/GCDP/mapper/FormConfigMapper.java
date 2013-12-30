package com.me.GCDP.mapper;

import java.util.List;

import com.me.GCDP.model.BaseModel;
import com.me.GCDP.model.TableField;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Company: ifeng.com</p>
 * @author :huwq
 * @version 1.0
 * <p>--------------------------------------------------------------------</p>
 * <p>date                   author                    reason             </p>
 * <p>2011-7-18              huwq               create the class      </p>
 * <p>--------------------------------------------------------------------</p>
 */

public interface FormConfigMapper<T extends BaseModel> extends BaseMapper<T> {
	
	public List<TableField> descTable(String tablename);

}
