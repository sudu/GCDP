/**
 * 
 */
package com.me.GCDP.action;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.ServletActionContext;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.me.GCDP.mapper.AuthorityMapper;
import com.me.GCDP.mapper.GroupMapper;
import com.me.GCDP.mapper.UserGroupMapper;
import com.me.GCDP.mapper.UserMapper;
import com.me.GCDP.model.Authority;
import com.me.GCDP.model.Group;
import com.me.GCDP.model.PageCondition;
import com.me.GCDP.model.User;
import com.me.GCDP.model.UserGroup;
import com.me.GCDP.util.CookieUtil;
import com.me.GCDP.util.JsonUtils;
import com.me.GCDP.util.MD5;
import com.me.GCDP.util.Page;
import com.me.json.JSONException;
import com.me.json.JSONObject;
import com.opensymphony.xwork2.ActionSupport;

/**
 * @author zhangzy
 *
 */
public class AuthorityAction extends ActionSupport {

	private static final long serialVersionUID = 1L;
	
	private static Log log = LogFactory.getLog(AuthorityAction.class);
	
	private UserMapper<User> userMapper = null;
	private UserGroupMapper<UserGroup> userGroupMapper = null;
	private GroupMapper<Group> groupMapper = null;
	private AuthorityMapper<Authority> authorityMapper = null;

    private int start;
    private int limit=10;
	private User user;
	private Group group;
	private Authority authority;
	private int id;
	private String ids;
	private String permission;
    private String filterTxt;
    private String filterValue;
	
    private String url;//验证后的跳转路径
    
    /**************************** BEGIN: LOGIN BY USERNAME AND PASSWORD *****************************/
    
    // added by HANXAINQI for password authentication
    
    /** SSO authentication login mode 0:off; 1:on **/
    private int ssoAuthenModeOn;
    
    /** USERNAME PASSWORD login mode **/
    private String  username;
    private String  password;
    
    /** request URL **/
    private String  requestURL;
    
    /** NODE ID , work as requestURL's parameter **/
    private String  queryString;
    
    /** login msg **/
    private String  msg;
    
	public int getSsoAuthenModeOn()
	{
		return ssoAuthenModeOn;
	}

	public void setSsoAuthenModeOn(int ssoAuthenModeOn)
	{
		this.ssoAuthenModeOn = ssoAuthenModeOn;
	}

	public String getUsername()
	{
		return username;
	}

	public void setUsername(String username)
	{
		this.username = username;
	}

	public String getPassword()
	{
		return password;
	}

	public void setPassword(String password)
	{
		this.password = password;
	}
	
    public String getMsg()
	{
		return msg;
	}

	public void setMsg(String msg)
	{
		this.msg = msg;
	}

	public String getRequestURL()
	{
		return requestURL;
	}

	public void setRequestURL(String requestURL)
	{
		this.requestURL = requestURL;
	}

	public String getQueryString()
	{
		return queryString;
	}

	public void setQueryString(String queryString)
	{
		this.queryString = queryString;
	}

	/*
	 * AJAX认证登录： 1. SSO; 2. 用户名-密码
	 */
    /*public String loginByUsernamePassword()*/
	public String signInSwitch()
    {
    	HttpServletRequest request = ServletActionContext.getRequest();
    	
    	if (getSsoAuthenModeOn() == 1)
    	{
    		// SSO AUTHEN
    		String context  = "http://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
    		
    		String loginUrl = context + "/develop/authority!loginMethod.jhtml";
    		
			requestURL      = requestURL.replace(request.getContextPath(), "");
			String baclUrl  = context + requestURL + (queryString == null ? "" : "?" + queryString);
			
			// baclUrl: http://localhost:8080/cmpp-dev/develop/authority!loginByUsernamePassword.jhtml
			
			// backUrl 应该是： baclUrl: http://localhost:8080/cmpp-dev/develop/index.jhtml?nodeId=14
			// 此处需要自己组装
			// baclUrl = "http://localhost:8080/cmpp-dev/develop/index.jhtml?nodeId=14";
			
			long    time = System.currentTimeMillis();
			String value = "cmpp" + "N8HxBo" + time + loginUrl + baclUrl;
			String   md5 = MD5.encode(value).toLowerCase();
			
			String fullSSOAuthURL = "";
			
			try
			{
				fullSSOAuthURL = "http://sso.staff.ifeng.com/auth/?from=cmpp&tm="
						+ time
						+ "&authurl="
						+ URLEncoder.encode(loginUrl, "utf-8")
						+ "&backurl="
						+ URLEncoder.encode(baclUrl, "utf-8")
						+ "&token1="
						+ md5;
			}
			catch (Exception ex)
			{
				log.info("sso authen error: " + ex);
				msg = "{ret:0, msg:'sso authen error: " + ex + "'}"; 
				return SUCCESS;
			}
			
			if (msg == null || getMsgRet(msg) != 0)
				msg = "{ret:1, msg:'" + fullSSOAuthURL + "'}"; 
			
			return SUCCESS;
    		
    	}
    	else
    	{
    		User user = userMapper.getByUserName(username);
    		String token = MD5.encode(user.getUsername() + "Ifeng888").toLowerCase();
			//** 写入cookie **//
			try
			{
				HttpServletResponse response = ServletActionContext.getResponse();
				CookieUtil.addCookie(response, "cmpp_user",  user.getUsername(), -1, request.getContextPath()); // 不保存
				CookieUtil.addCookie(response, "cmpp_token", token,     -1, request.getContextPath()); // 不保存
				String cn = URLEncoder.encode(user.getCnname(), "utf-8");
				CookieUtil.addCookie(response, "cmpp_cn", cn, -1, request.getContextPath()); // 不保存
			}
			catch (Exception ex)
			{
				log.error(ex);
				msg = "{ret:0, msg:'writing cookie error: " + ex + "'}"; 
				return SUCCESS;
			}
			
			/** 生成前端（PAGE)跳转的URL **/
			
			requestURL      = requestURL.replace(request.getContextPath(), "");
			String queryURL = requestURL + (queryString == null ? "" : "?" + queryString);
			
			// login url: /develop/index.jhtml?nodeId=14
			log.info("login url: " + queryURL);
			// 传递到前端的参数一定是action的属性
			String fullDirectURL = "http://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath() + queryURL;
			
			log.info("login url: " + fullDirectURL);
			
			if (msg == null || getMsgRet(msg) != 0)
				msg = "{ret:1, msg:'" + fullDirectURL + "'}"; 
			
			return SUCCESS;
    	}
    }
    
    /**
     * 用于AJAX异步验证用户名是否存在
     */
	public String checkUserName()
    {
		try
		{
			User user = userMapper.getByUserName(username);
			
			if (user == null)
			{
				// 用户不存在
				msg = "{ret:0, msg:'用户<b><i>" + username + "</i></b>不存在， 请联系CMPP系统管理员！'}"; 
			}
			else
			{
				msg = "{ret:1, msg:''}"; 
			}
			
			// 用原生AJAX回写
			/*HttpServletResponse response = ServletActionContext.getResponse();
			response.setCharacterEncoding("UTF-8");
			response.getWriter().write(msg);*/
		}
		catch (Exception ex)
		{
			log.error(ex);
		}

    	return SUCCESS;
    }
	
	/*
	 * AJAX验证用户名及密码 
	 */
	/*public String checkUsernamePassword()*/
	public String checkSignInInfo()
	{
		String illegalMsg = "";
		// 验证各个字段是否为空
		
		if ("".equals(username))
		{
			illegalMsg = "用户名不能为空！";
			msg = processResult(illegalMsg);
			return SUCCESS;
		}
		
		if (!"".equals(username))
		{
			User user = userMapper.getByUserName(username);
			if (user == null)
			{
				illegalMsg = "用户<b><i>" + username + "</i></b>不存在， 请联系CMPP系统管理员！";
				msg = processResult(illegalMsg);
				return SUCCESS;
			}
		}
		if ("".equals(password))
		{
			illegalMsg = "密码不能为空！";
			msg = processResult(illegalMsg);
			return SUCCESS;
		}
		
		if ("".equals(username) && "".equals(password))
		{
			illegalMsg = "用户名、密码不能为空！";
			msg = processResult(illegalMsg);
			return SUCCESS;
		}
		
		// 再次检查用户名是否存在
		User user = userMapper.getByUserName(username);
		if (user == null)
		{
			illegalMsg = "用户<b><i>" + username + "</i></b>不存在， 请联系CMPP系统管理员！";
			msg = processResult(illegalMsg);
			return SUCCESS;
		}
		
		// DB中用户密码为空， 即还没有设置密码
		if (user.getPassword() == null || user.getPassword() == "")
		{
			illegalMsg = "用户<b><i>" + username + "</i></b>还没有设置密码， 请联系CMPP系统管理员！";
			msg = processResult(illegalMsg);
			return SUCCESS;
		}
		
		if (!user.getPassword().equals(password))
		{
			illegalMsg = "密码不正确， 请重新输入！";
			msg = processResult(illegalMsg);
			return SUCCESS;
		}
		
		// 所有字段合法， 空信息
		msg = processResult(illegalMsg);
		
		return SUCCESS;
	}
	
	private String processResult(String illegalMsg)
	{
		String msg = "";
		
		if (illegalMsg == null || "".equals(illegalMsg))
		{
			msg = "{ret:1, msg:''}"; 
		}
		else
		{
			msg = "{ret:0, msg:'" + illegalMsg + "'}"; 
		}
		
		return msg;
	}
	
	private int getMsgRet(String msg)
	{
		int ret = 0;
		JSONObject jsonObj = null;
		try
		{
			jsonObj = new JSONObject(msg);
			ret = jsonObj.getInt("ret"); 
		}
		catch (JSONException e)
		{
			log.error(e);
			e.printStackTrace();
		}
		return ret;
	}
    
	/**************************** END: LOGIN BY USERNAME AND PASSWORD *****************************/
    
    private String getUserInfo(String uid) throws Exception{

    	long time=System.currentTimeMillis();
    	String md5=MD5.encode(uid+"N8HxBo"+time+"cmpp").toLowerCase();
    	String url="http://sso.staff.ifeng.com/getUserInfo.aspx?uid="
    		+uid+"&tm="+time+"&from=cmpp&fmt=xml&token4="+md5.toLowerCase();
    	URL u = new URL(url);
    	HttpURLConnection conn = (HttpURLConnection)u.openConnection();
    	conn.setConnectTimeout(50000);
    	conn.setReadTimeout(10000);
		conn.setRequestMethod("GET");
		conn.connect(); 
		Scanner scanner=new Scanner(conn.getInputStream(),"UTF-8");
		StringBuffer str=new StringBuffer();
		while(scanner.hasNextLine()) {
		        str.append(scanner.nextLine());
		        str.append("\n");
		}
		scanner.close();
		return str.toString();
    }
    
	public void loginMethod(){
		HttpServletRequest request=ServletActionContext.getRequest();
		String uid=request.getParameter("uid");
		String token = MD5.encode(uid + "Ifeng888").toLowerCase();
		
		String backurl=request.getParameter("backurl");
		
		//先从数据库取用户
		User u=userMapper.getByUserName(uid);
		if(u!=null){
			try {
				CookieUtil.addCookie(ServletActionContext.getResponse(), "cmpp_user", uid, -1,request.getContextPath());//不保存
				CookieUtil.addCookie(ServletActionContext.getResponse(), "cmpp_token", token, -1,request.getContextPath());//不保存
				String cn=URLEncoder.encode(u.getCnname(),"utf-8");
				CookieUtil.addCookie(ServletActionContext.getResponse(), "cmpp_cn", cn, -1,ServletActionContext.getRequest().getContextPath());//不保存
				log.info(uid+" login");//记录用户登录信息
				ServletActionContext.getResponse().sendRedirect(backurl);
			} catch (IOException e) {
				log.error(e);
			}
			return;
		}
		
		//TODO
		//2次验证方法.....

		try {
			String userInfo=getUserInfo(uid);
			xmlToUserDataBase(userInfo);
		} catch (Exception e1) {
			log.error(e1);
		}
		
		log.info(uid+" login");//记录用户登录信息
		CookieUtil.addCookie(ServletActionContext.getResponse(), "cmpp_user", uid, -1,request.getContextPath());//不保存
		CookieUtil.addCookie(ServletActionContext.getResponse(), "cmpp_token", token, -1,request.getContextPath());//不保存
		
		try {
			ServletActionContext.getResponse().sendRedirect(backurl);
		} catch (IOException e) {
			log.error(e);
		}
	}
	
	@SuppressWarnings("unchecked")
	private String xmlToUserDataBase(String xml){
		User user=new User();
		try {
			Document doc = DocumentHelper.parseText(xml);
			Element root = doc.getRootElement();
			for (Iterator<Element> i = root.elementIterator(); i.hasNext();){   
				Element ele = (Element) i.next();
				if (ele.getName().equals("sAMAccountName")){
					user.setUsername(ele.getText());
				}else if(ele.getName().equals("department")){
					user.setDept(ele.getText());
				}else if(ele.getName().equals("cn")){
					String cn=URLEncoder.encode(ele.getText(),"utf-8");
					CookieUtil.addCookie(ServletActionContext.getResponse(), "cmpp_cn", cn, -1,ServletActionContext.getRequest().getContextPath());//不保存
					user.setCnname(ele.getText());
				}else if(ele.getName().equals("mail")){
					user.setEmail(ele.getText());
				}
			}
			User u=userMapper.getByUserName(user.getUsername());
			if(u==null){
				userMapper.insert(user);
			}
		} catch (Exception e) {
			log.error(e);
		}
		return null;
	}
	
	/**
	 * 
	 * @Description:sso第二次验证
	 * @param    
	 * @return void
	 * @throws
	 */
	public void secLogin(){
		
	}
	
	

	
	/**
	 * @Description:验证后处理方法
	 * @param @return   
	 * @return String
	 * @throws
	 */
	public String SSOLogin(){
		//获得SSO返回结果
		//session记录用户
		url="/index.html";//跳转路径
		return "result";
	}
	
	public void modifyAuthority() throws  Exception{
		Authority authority=authorityMapper.getById(id);
		if (authority != null) {
            List<Authority> list = new ArrayList<Authority>();
            list.add(authority);
            HttpServletResponse response = ServletActionContext.getResponse();
   	   	 	response.setCharacterEncoding("UTF-8");// 设置字符集编码
   	   	 	JsonUtils.write(list, response.getWriter());
        }
	}
	
	public void queryAuthorityByGroupId() throws Exception{
		 List<Authority> result=authorityMapper.getByGroupId(id);
		 Page<Authority> p=new Page<Authority>(result, result.size());
		 HttpServletResponse response = ServletActionContext.getResponse();
	   	 response.setCharacterEncoding("UTF-8");// 设置字符集编码
	   	 JsonUtils.write(p, response.getWriter());
	}
	
	public void addAuthority(){
		int pValue=0;
		String ps="";
		if(permission!=null){
			String[] p=permission.split(",");
			for(String per:p){
				pValue=pValue+Integer.parseInt(per.trim());
			}
			ps=permissionToString(pValue);
		}
		if(authority.getId()!=null){
			authority.setPermission(pValue);
			authority.setPermissionString(ps);
			authorityMapper.update(authority);
		}else{
			authority.setPermission(pValue);
			authority.setPermissionString(ps);
			authority.setGroupid(id);
			authorityMapper.insert(authority);
		}
	}
	
	private String permissionToString(int value){
		StringBuffer s=new StringBuffer();
		if((value&1)>0){
			s.append("查看");
		}
		if((value&2)>0){
			if(s.length()>0){
				s.append(",");
			}
			s.append("添加");
		}
		if((value&4)>0){
			if(s.length()>0){
				s.append(",");
			}
			s.append("修改");
		}
		if((value&8)>0){
			if(s.length()>0){
				s.append(",");
			}
			s.append("删除");
		}
		if((value&16)>0){
			if(s.length()>0){
				s.append(",");
			}
			s.append("发布");
		}
		return s.toString();
	}
	
	public void removeAuthority() throws IOException{
		for (String str : ids.split(",")) {
            try {
                int id = Integer.parseInt(str);
                Authority authority=authorityMapper.getById(id);
                authorityMapper.delete(authority);
            } catch (NumberFormatException ex) {
                continue;
            }
        }
		HttpServletResponse response =ServletActionContext.getResponse();
		response.setCharacterEncoding("UTF-8");
		response.getWriter().print("{success:true,msg:'删除成功'}");
	}
	
	/**
	 * 
	 * @Description:查看用户加入的组
	 * @param @throws Exception   
	 * @return void
	 * @throws
	 */
	public void groupPagedQueryByUserId() throws Exception{
		 List<Group> result=groupMapper.getGroupByUserId(id);
		 Page<Group> p=new Page<Group>(result, result.size());
		 HttpServletResponse response = ServletActionContext.getResponse();
	   	 response.setCharacterEncoding("UTF-8");// 设置字符集编码
	   	 JsonUtils.write(p, response.getWriter());
	}
	
	/**
	 * 
	 * @Description:查看用户未加入的组
	 * @param @throws Exception   
	 * @return void
	 * @throws
	 */
	public void notJoinGroupByUserId() throws Exception{
		PageCondition pc=new PageCondition();
		pc.setFrom(start);
		pc.setLimit(limit);
		pc.setUserId(id);
		 if ((filterTxt != null) && (filterValue != null)
	                && (!filterTxt.equals("")) && (!filterValue.equals(""))) {
			 pc.setFilterTxt(filterTxt);
			 pc.setFilterValue(filterValue);
	     } 
		 List<Group> result=groupMapper.getNotJoinGroupByUserId(pc);
		 Page<Group> p=new Page<Group>(result, groupMapper.getNotJoinGroupCount(pc));
		 HttpServletResponse response = ServletActionContext.getResponse();
	   	 response.setCharacterEncoding("UTF-8");// 设置字符集编码
	   	 JsonUtils.write(p, response.getWriter());
	}
	
	
	
	/**
	 * 
	 * @Description:加入组
	 * @param @throws IOException   
	 * @return void
	 * @throws
	 */
	public void joinGroup() throws IOException{
		String[] selectId=ids.split(",");
		for(String groupId:selectId){
			UserGroup ug=new UserGroup();
			ug.setUserId(id);
			ug.setGroupId(Integer.parseInt(groupId));
			userGroupMapper.insert(ug);
		}
		HttpServletResponse response =ServletActionContext.getResponse();
		response.setCharacterEncoding("UTF-8");
		response.getWriter().print("{success:true,msg:'加入组成功'}");
		
	}
	
	
	/**
	 * 
	 * @Description:退出组
	 * @param @throws IOException   
	 * @return void
	 * @throws
	 */
	public void quitGroup() throws IOException{
		String[] selectId=ids.split(",");
		for(String groupId:selectId){
			UserGroup ug=new UserGroup();
			ug.setUserId(id);
			ug.setGroupId(Integer.parseInt(groupId));
			userGroupMapper.delete(ug);
		}
		HttpServletResponse response =ServletActionContext.getResponse();
		response.setCharacterEncoding("UTF-8");
		response.getWriter().print("{success:true,msg:'退出组成功'}");
	}
	
	
	/**
	 * 
	 * @Description:添加组
	 * @param @return   
	 * @return String
	 * @throws
	 */
	public String addGroup(){
		if(group.getId()!=null){
			groupMapper.update(group);
		}else{
			groupMapper.insert(group);
		}
		return null;
	}
	
	/**
	 * 
	 * @Description:修改组
	 * @param @throws Exception   
	 * @return void
	 * @throws
	 */
	public void modifyGroup() throws  Exception{
		Group group=groupMapper.getById(id);
		if (group != null) {
            List<Group> list = new ArrayList<Group>();
            list.add(group);
            HttpServletResponse response = ServletActionContext.getResponse();
	   		response.setCharacterEncoding("UTF-8");// 设置字符集编码
	   		JsonUtils.write(list, response.getWriter());
        }
	}
	
	/**
	 * 
	 * @Description:删除组
	 * @param @throws IOException   
	 * @return void
	 * @throws
	 */
	public void removeGroup() throws IOException{
		for (String str : ids.split(",")) {
            try {
                int id = Integer.parseInt(str);
                Group group=groupMapper.getById(id);
                groupMapper.delete(group);
            } catch (NumberFormatException ex) {
                continue;
            }
        }
		HttpServletResponse response =ServletActionContext.getResponse();
		response.setCharacterEncoding("UTF-8");
		response.getWriter().print("{success:true,msg:'删除成功'}");
	}
	
	/**
	 * 
	 * @Description:组分页
	 * @param @throws Exception   
	 * @return void
	 * @throws
	 */
	public void groupPagedQuery() throws Exception {
		PageCondition pc=new PageCondition();
		pc.setFrom(start);
		pc.setLimit(limit);
		 if ((filterTxt != null) && (filterValue != null)
	                && (!filterTxt.equals("")) && (!filterValue.equals(""))) {
			 pc.setFilterTxt(filterTxt);
			 pc.setFilterValue(filterValue);
	     } 
		 List<Group> result=groupMapper.getGroupByPage(pc);
		 Page<Group> p=new Page<Group>(result, groupMapper.getGroupCount());
		 HttpServletResponse response = ServletActionContext.getResponse();
		 response.setCharacterEncoding("UTF-8");// 设置字符集编码
		 JsonUtils.write(p, response.getWriter());
	}
	
	/**
	 * @throws IOException 
	 * 
	 * @Description:删除组用户
	 * @param    
	 * @return void
	 * @throws
	 */
	public void deleteGroupUser() throws IOException{
		String[] selectId=ids.split(",");
		for(String userId:selectId){
			UserGroup ug=new UserGroup();
			ug.setUserId(Integer.parseInt(userId));
			ug.setGroupId(id);
			userGroupMapper.delete(ug);
		}
		HttpServletResponse response =ServletActionContext.getResponse();
		response.setCharacterEncoding("UTF-8");
		response.getWriter().print("{success:true,msg:'退出组成功'}");
	}
	
	/**
	 * 
	 * @Description:用户加入组
	 * @param @throws Exception   
	 * @return void
	 * @throws
	 */
	public void doUserJoin() throws Exception{
		String[] selectId=ids.split(",");
		for(String userId:selectId){
			UserGroup ug=new UserGroup();
			ug.setUserId(Integer.parseInt(userId));
			ug.setGroupId(id);
			userGroupMapper.insert(ug);
		}
		HttpServletResponse response =ServletActionContext.getResponse();
		response.setCharacterEncoding("UTF-8");
		response.getWriter().print("{success:true,msg:'加入组成功'}");
	}
	
	/**
	 * 
	 * @Description: 获取未加入组用户，分页
	 * @param @throws Exception   
	 * @return void
	 * @throws
	 */
	public void userNotJoinByGroupId() throws Exception{
		PageCondition pc=new PageCondition();
		pc.setFrom(start);
		pc.setLimit(limit);
		pc.setGroupId(id);
		if ((filterTxt != null) && (filterValue != null)
                && (!filterTxt.equals("")) && (!filterValue.equals(""))) {
		 pc.setFilterTxt(filterTxt);
		 pc.setFilterValue(filterValue);
		} 
		 List<User> result=userMapper.getNotJoinByGroupId(pc);
		 Page<User> p=new Page<User>(result, userMapper.getNotJoinUserCount(pc));
		 HttpServletResponse response = ServletActionContext.getResponse();
		 response.setCharacterEncoding("UTF-8");// 设置字符集编码
		 JsonUtils.write(p, response.getWriter());
	}
	
	/**
	 * 
	 * @Description:获取组用户，分页
	 * @param    
	 * @return void
	 * @throws
	 */
	public void userPagedQueryByGroupId() throws Exception{
		PageCondition pc=new PageCondition();
		pc.setFrom(start);
		pc.setLimit(limit);
		pc.setGroupId(id);
		if ((filterTxt != null) && (filterValue != null)
                && (!filterTxt.equals("")) && (!filterValue.equals(""))) {
			 pc.setFilterTxt(filterTxt);
			 pc.setFilterValue(filterValue);
		} 
		 List<User> result=userMapper.getByGroupId(pc);
		 Page<User> p=new Page<User>(result, userMapper.getJoinUserCount(pc));
		 HttpServletResponse response = ServletActionContext.getResponse();
		 response.setCharacterEncoding("UTF-8");// 设置字符集编码
		 JsonUtils.write(p, response.getWriter());
	}
	
	/**
	 * 
	 * @Description:用户分页
	 * @param @throws Exception   
	 * @return void
	 * @throws
	 */
	public void userPagedQuery() throws Exception {
		PageCondition pc=new PageCondition();
		pc.setFrom(start);
		pc.setLimit(limit);
		 if ((filterTxt != null) && (filterValue != null)
	                && (!filterTxt.equals("")) && (!filterValue.equals(""))) {
			 pc.setFilterTxt(filterTxt);
			 pc.setFilterValue(filterValue);
	     } 
		 List<User> result=userMapper.getUserByPage(pc);
		 Page<User> p=new Page<User>(result, userMapper.getUserCount());
		 HttpServletResponse response = ServletActionContext.getResponse();
		 response.setCharacterEncoding("UTF-8");// 设置字符集编码
		 JsonUtils.write(p, response.getWriter());
	}
	
	/**
	 * @Description:添加用户
	 * @param @return   
	 * @return String
	 * @throws
	 */
	public String addUser(){
		if(user.getId()!=null){
			userMapper.update(user);
		}else{
			userMapper.insert(user);
		}
		return null;
	}
	
	public void modifyUser() throws  Exception{
		User user=userMapper.getById(id);
		if (user != null) {
            List<User> list = new ArrayList<User>();
            list.add(user);
            HttpServletResponse response = ServletActionContext.getResponse();
   		 	response.setCharacterEncoding("UTF-8");// 设置字符集编码
            JsonUtils.write(list,
            		response.getWriter());
        }
	}
	
	public void removeUser() throws IOException{
		for (String str : ids.split(",")) {
            try {
                int id = Integer.parseInt(str);
                User user=userMapper.getById(id);
                userMapper.delete(user);
            } catch (NumberFormatException ex) {
                continue;
            }
        }
		HttpServletResponse response =ServletActionContext.getResponse();
		response.setCharacterEncoding("UTF-8");
		response.getWriter().print("{success:true,msg:'删除成功'}");
	}
	
	public String getUserManagerPage(){
		return "user_manager";
	}
	
	public String getGroupManagerPage(){
		return "group_manager";
	}
	
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public UserMapper<User> getUserMapper() {
		return userMapper;
	}
	public void setUserMapper(UserMapper<User> userMapper) {
		this.userMapper = userMapper;
	}
	public UserGroupMapper<UserGroup> getUserGroupMapper() {
		return userGroupMapper;
	}
	public void setUserGroupMapper(UserGroupMapper<UserGroup> userGroupMapper) {
		this.userGroupMapper = userGroupMapper;
	}
	public GroupMapper<Group> getGroupMapper() {
		return groupMapper;
	}
	public void setGroupMapper(GroupMapper<Group> groupMapper) {
		this.groupMapper = groupMapper;
	}
	public AuthorityMapper<Authority> getAuthorityMapper() {
		return authorityMapper;
	}
	public void setAuthorityMapper(AuthorityMapper<Authority> authorityMapper) {
		this.authorityMapper = authorityMapper;
	}



	public int getStart() {
		return start;
	}

	public void setStart(int start) {
		this.start = start;
	}

	public int getLimit() {
		return limit;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getIds() {
		return ids;
	}

	public void setIds(String ids) {
		this.ids = ids;
	}

	public Group getGroup() {
		return group;
	}

	public void setGroup(Group group) {
		this.group = group;
	}

	public Authority getAuthority() {
		return authority;
	}

	public void setAuthority(Authority authority) {
		this.authority = authority;
	}

	public String getPermission() {
		return permission;
	}

	public void setPermission(String permission) {
		this.permission = permission;
	}

	public String getFilterTxt() {
		return filterTxt;
	}

	public void setFilterTxt(String filterTxt) {
		this.filterTxt = filterTxt;
	}

	public String getFilterValue() {
		return filterValue;
	}

	public void setFilterValue(String filterValue) {
		this.filterValue = filterValue;
	}


	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	
}
