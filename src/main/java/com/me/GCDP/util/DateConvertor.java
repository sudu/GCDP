package com.me.GCDP.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.Map;

import org.apache.struts2.util.StrutsTypeConverter;


/**
 * 日期转换器.
 */
public class DateConvertor extends StrutsTypeConverter {
    /** * 日期格式化器. */
	 private SimpleDateFormat dateFormat = new SimpleDateFormat("yy年MM月dd日 HH:mm:ss");
	 private SimpleDateFormat newdateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	 
	 
    /**
     * 字符串转换成日期.
     * @param context 上下文
     * @param value 数据
     * @param toType 转换成什么类型
     * @return 日期
     */
    @Override
    public Object convertFromString(Map context, String[] value,Class toType) {
        if (Date.class == toType) {
            try {
            	 if((value[0]!=null&&!value[0].equals("")))
            	 {
            		 return dateFormat.parse(value[0]);
            	 }
            } catch (Exception ex) { 
            	try {
            		 if((value[0]!=null&&!value[0].equals("")))
                	 {
            			 return newdateFormat.parse(value[0]);
                	 }
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            	  
            }
        }

        return null;
    }

    /**
     * 转换成字符串.
     *
     * @param context 上下文
     * @param o 对象
     * @return 字符串
     */
    @Override
    public String convertToString(Map context, Object o) {
        if (o != null) {
            return dateFormat.format((Date) o);
        } else {
            return null;
        }
    }
}
