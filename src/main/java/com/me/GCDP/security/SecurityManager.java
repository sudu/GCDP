package com.me.GCDP.security;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.me.GCDP.freemarker.FreeMarkerHelper;
import com.me.GCDP.mapper.AuthorityMapper;
import com.me.GCDP.model.Authority;
import com.me.GCDP.model.Permission;
import com.me.GCDP.util.MySQLHelper;
import com.me.GCDP.xform.FormConfig;
import com.me.json.JSONException;

import freemarker.template.TemplateException;

public class SecurityManager {
	
	private static Log log = LogFactory.getLog(SecurityManager.class);
	
	private AuthorityMapper<Authority> authorityMapper;
	
	public boolean checkSystemPermission(Permission pType){
		return  checkPermission("/system/", pType);
	}

	public List<Authority> getAuthority(int type){
		List<Authority> rtn = new ArrayList<Authority>();
		List<Authority> alist = authorityMapper.getByUserName(AuthorzationUtil.getUserId());
		for(Authority au:alist){
			if(au.getType()==type){
				rtn.add(au);
			}
		}
		return rtn;
	}
	
	public boolean checkPermission(String path, Permission pType,Map<String,Object> data,int formId,int id){
		String pth;
		try {
			pth = formatPath(path, data, formId, id);
		} catch (Exception e) {
			log.error(e);
			return false;
		}
		return checkPermission(pth, pType);
	}
	
	public boolean checkPermission(String path, Permission pType){
        //如果路径为空则认为默认具有权限
        if (path==null||("".equals(path))){
            return true;
        }
        List<Authority> deniedList = getAuthority(0);
        if (checkPermission(deniedList,path, pType)){
            return false;
        }
        List<Authority> allowList = getAuthority(1);
        return checkPermission(allowList,path, pType);
    }
		
	public boolean checkPermission(String path, int type){
		Permission pType = Permission.getInstance(type);
		if(pType!=null){
			return checkPermission(path, pType);
		}
		return false;
	}
	
	private boolean checkPermission(List<Authority> list,String path,Permission pType){
		for(Authority auth:list){
			String rStr = auth.getPowerpath();
			Pattern pattern = Pattern.compile(rStr);
			Matcher m = pattern.matcher(path.replaceAll("//+", "/"));
			if(m.matches()){
				int pt = auth.getPermission();
				if((pType.getValue()&pt)!=0){
					return true;
				}
			}
		}
		return false;
	}
	
	public String formatPath(String tStr,Map<String,Object> data,int formId,int id) throws SQLException, JSONException, IOException, TemplateException{
		Map<String,Object> context = new HashMap<String, Object>();
		if(data!=null){
			context.putAll(data);
		}
		Pattern p = Pattern.compile("(?<=\\$\\{)[\\w\\d]+?(?=\\})");
		Matcher m = p.matcher(tStr);
		List<String> fields = new ArrayList<String>();
		//获取字符串模板中的不在data中的字段
		while(m.find()){
			String field = m.group();
			if(data!=null){
				if((!data.containsKey(field))&&(!fields.contains(field))){
					fields.add(field);
				}
			}else{
				if(!fields.contains(field)){
					fields.add(field);
				}
			}
		}
		if(fields.size()>0){
			String strf="";
			for(String f:fields){
				if(strf.equals("")){
					strf ="`"+f+"`";
				}else{
					strf =strf+",`"+f+"`";
				}
			}
			//获取路径模板中使用的并且未包含在data中的数据
			if(formId!=0&&id!=0){
				FormConfig fc = FormConfig.getInstance(0, formId);
				String sql = "select "+strf+" from "+fc.getTableName()+" where id="+id;
				Vector<Map<String,Object>> datas = MySQLHelper.ExecuteSql(sql, null);
				if(datas.size()>0){
					Map<String,Object> data2 = datas.get(0);
					context.putAll(data2);
				}
			}
		}
		return FreeMarkerHelper.process2(tStr, context);
	}
	
	public void setAuthorityMapper(AuthorityMapper<Authority> authorityMapper) {
		this.authorityMapper = authorityMapper;
	}
	
}
