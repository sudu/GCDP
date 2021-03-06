package com.me.GCDP.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Company: ifeng.com</p>
 * @author :Hu WeiQi
 * @version 1.0
 * <p>--------------------------------------------------------------------</p>
 * <p>date                   author                    reason             </p>
 * <p>2008-6-25             Hu WeiQi               create the class     </p>
 * <p>--------------------------------------------------------------------</p>
 */

public class MD5 {
	
	public static String encode(String plainText ) { 
		StringBuffer buf = new StringBuffer(""); 
		try { 
			MessageDigest md = MessageDigest.getInstance("MD5"); 
			md.update(plainText.getBytes()); 
			byte b[] = md.digest(); 
	
			int i; 
			for (int offset = 0; offset < b.length; offset++) { 
				i = b[offset]; 
				if(i<0) i+= 256; 
				if(i<16) 
				buf.append("0"); 
				buf.append(Integer.toHexString(i)); 
			} 
	
		} catch (NoSuchAlgorithmException e) { 
			e.printStackTrace(); 
		} 
		return buf.toString().toUpperCase();//32位的加密 
	} 

}
