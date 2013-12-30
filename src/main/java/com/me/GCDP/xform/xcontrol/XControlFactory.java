package com.me.GCDP.xform.xcontrol;

import com.me.GCDP.xform.xcontrol.BaseControl;

public class XControlFactory {
	private static XControlFactory instance;
	private static Object lock = new Object();
	public static XControlFactory getInstance()
	{
		if (instance == null){  
            synchronized( lock ){  
                if (instance == null){  
                    instance = new XControlFactory();  
                }  
            }
		}
		return instance;
	}
	public BaseControl create(String controlName)
	{
		BaseControl rtn=null;
		String className ="com.me.GCDP.xform.xcontrol."+controlName;
		
		try {
			rtn = (BaseControl)Class.forName(className).newInstance();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			//System.out.println(className+"not found");
			rtn = new BaseControl();
		}
		return rtn;
	}

}
