package com.me.GCDP.security;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.me.GCDP.script.plugin.CoderPlugin;
import com.me.GCDP.script.plugin.DataBasePlugin;
import com.me.GCDP.script.plugin.ScriptPlugin;
import com.me.GCDP.script.plugin.annotation.PluginClass;
import com.me.GCDP.script.plugin.annotation.PluginExample;
import com.me.GCDP.script.plugin.annotation.PluginIsPublic;
import com.me.GCDP.script.plugin.annotation.PluginMethod;

/**
 * <p>Title: </p>
 * <p>Description: 用户权限插件</p>
 * <p>Company: ifeng.com</p>
 * @author :huwq
 * @version 1.0
 * <p>--------------------------------------------------------------------</p>
 * <p>date                   author                    reason             </p>
 * <p>2011-6-23              huwq               create the class      </p>
 * <p>--------------------------------------------------------------------</p>
 */

@PluginClass(author = "huweiqi", intro = "用户权限插件")
@PluginExample(intro = "var info = pluginFactory.getP(\"user\");<br /><br />"+
"var user=info.getUserInfoFromSSO(\"zhangzy\");<br />"+
"var email=user.get(\"email\");<br />")
public class SecurityPlugin extends ScriptPlugin {
	
	@Override
	public void init() {
	}
	
	private DataBasePlugin db = null;
	private SecurityManager securityManager = null;
	
	public void setSecurityManager(SecurityManager securityManager) {
		this.securityManager = securityManager;
	}

	public void setDb(DataBasePlugin db) {
		this.db = db;
	}

	@PluginIsPublic
	@PluginMethod(intro = "获取当前用户的登录ID", paramIntro = { }, returnIntro = "返回当前登录的用户ID" )
	public String getUserId() {
		return AuthorzationUtil.getUserId();
	}
	
	//检查系统权限
	@PluginIsPublic
	@PluginMethod(intro = "验证当前用户系统权限", 
			paramIntro = { "权限类型,分为VIEW(1),ADD(2),MODIFY(4),DELETE(8),PUBLISH(16)"},
			returnIntro = "返回用户权限验证结果true为成功,false为失败"
			)
	public boolean checkSystemPermission(int type){
		return securityManager.checkPermission("/system/", type);
	}
	
	//检查用户权限
	@PluginIsPublic
	@PluginMethod(intro = "验证当前用户权限", 
			paramIntro = { "权限路径","权限类型,分为VIEW(1),ADD(2),MODIFY(4),DELETE(8),PUBLISH(16)"},
			returnIntro = "返回用户权限验证结果true为成功,false为失败")
	public boolean checkPermission(String path, int type){
		return securityManager.checkPermission(path, type);
	}
	
	@PluginIsPublic
	@PluginMethod(intro = "从数据库获得用户信息", 
			paramIntro = { "用户名"}, 
			returnIntro = "用户信息Map&lt;String, String&gt;")
	public Map<String,String> getUserInfoFromDB(String userName) throws Exception{
		List<Map<String,String>> result=db
				.executeSelectSQL("select id,cnname,username,password,email,dept,telphone,sAMAccountName from cmpp_user where username = '"+userName+"'");
		if(result==null ||result.size()==0){
			return null;
		}else{
			return result.get(0);
		}
	}
	
	@PluginIsPublic
	@PluginMethod(intro = "从数据库获得用户信息,该信息包含用户所在的组的名称",
			paramIntro = { "用户名"}, 
			returnIntro = "用户信息List&lt;Map&lt;String, String&gt;&gt;")
	public List<Map<String,String>> getUserInfoFromDBWithGroup(String userName) throws Exception{
		List<Map<String,String>> result=db
				.executeSelectSQL("select tu.*, tg.groupName from cmpp_user tu, " +
						"cmpp_user_group tug, cmpp_group tg where tu.id = tug.userId " +
						"and tug.groupId = tg.id and tu.username = '"+userName+"'");
		if(result==null ||result.size()==0){
			return null;
		}else{
			return result;
		}
	}
	
	@PluginIsPublic
	@PluginMethod(intro = "从认证系统获得用户信息", 
			paramIntro = { "用户名"}, 
			returnIntro = "用户信息Map&lt;String, String&gt;")
	public Map<String,String> getUserInfoFromSSO(String userName) throws IOException {
		CoderPlugin MD5=new CoderPlugin();
		long time=System.currentTimeMillis();
    	String md5=MD5.md5Encode(userName+"N8HxBo"+time+"cmpp");
    	String url="http://sso.staff.ifeng.com/getUserInfo.aspx?uid="
    		+userName+"&tm="+time+"&from=cmpp&fmt=xml&token4="+md5.toLowerCase();
    	URL u = new URL(url);
    	HttpURLConnection conn = (HttpURLConnection)u.openConnection();
    	conn.setConnectTimeout(5000);
    	conn.setReadTimeout(5000);
		conn.setRequestMethod("GET");
		conn.connect(); 
		Scanner scanner=new Scanner(conn.getInputStream(),"UTF-8");
		StringBuffer str=new StringBuffer();
		while(scanner.hasNextLine()) {
		        str.append(scanner.nextLine());
		        str.append("\n");
		}
		return xmlToUserDataBase(str.toString());
	}
	
	@PluginIsPublic
	@PluginMethod(intro = "判断用户是否登录", 
			paramIntro = {}, 
			returnIntro = "用户信息Map&lt;String, String&gt;")	
	public boolean isLogin(){
		return AuthorzationUtil.isLogin();
	}
	
	
	@PluginIsPublic
	@PluginMethod(intro = "获得登录用户名", 
			paramIntro = {}, 
			returnIntro = "用户信息Map&lt;String, String&gt;")
	public static String getUserName(){
		return AuthorzationUtil.getUserName();
	}
	
	@SuppressWarnings("unchecked")
	private Map<String,String> xmlToUserDataBase(String xml){
		//System.out.println(xml);
		Map<String,String> info = new HashMap<String,String>();
		try {
			Document doc = DocumentHelper.parseText(xml);
			Element root = doc.getRootElement();
			for (Iterator<Element> i = root.elementIterator(); i.hasNext();){   
				Element ele = (Element) i.next();
				if (ele.getName().equals("sAMAccountName")){
					info.put("sAMAccountName", ele.getText());
				}else if(ele.getName().equals("department")){
					info.put("department", ele.getText());
				}else if(ele.getName().equals("cn")){
					info.put("cn", ele.getText());
				}else if(ele.getName().equals("mail")){
					info.put("email", ele.getText());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return info;
	}
	
}
