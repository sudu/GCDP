package com.me.GCDP.util.property;

import com.me.GCDP.util.ToolsUtil;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Company: ifeng.com</p>
 * @author :huweiqi
 * @version 1.0
 * <p>--------------------------------------------------------------------</p>
 * <p>date                   author                    reason             </p>
 * <p>2011-2-9              huweiqi               create the class      </p>
 * <p>--------------------------------------------------------------------</p>
 */
public class CmppConfig {
	
	private CmppConfig() {}

	/**
	 * @param keyName
	 * @return
	 */
	public static String getKey(String keyName) {
		String filename = ".properties";
		if (keyName == null) {
			return null;
		}
		if(keyName.indexOf(".") > 0){
			filename = keyName.split("\\.")[0] + filename;
		}else{
			filename = "cmpp" + filename;
		}
		String value = ReadConfigFile.getKey("conf//"+filename, keyName.trim());
		if(value.indexOf("{root}")>=0){
			//替换根目录
			value = value.replace("{root}", ToolsUtil.getRootRealPath());
		}
		return value;
	}
	
	public static void main(String[] args){
		System.out.println(CmppConfig.getKey("cmpp.xxx"));
	}
	
}
