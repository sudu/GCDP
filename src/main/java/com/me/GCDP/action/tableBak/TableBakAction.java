package com.me.GCDP.action.tableBak;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.me.GCDP.mapper.FormConfigMapper;
import com.me.GCDP.model.FormConfig;
import com.me.GCDP.util.MySQLHelper;
import com.me.GCDP.util.SpringContextUtil;
import com.me.json.JSONObject;
import com.opensymphony.xwork2.ActionSupport;

/**
 * 将主表的数据转移到bak表的接口，
 * 需要在服务器配置计划任务执行这个接口，
 * 配置方法：0 4 * * * wget --spider http://127.0.0.1:8080/Cmpp/develop/tabBak.jhtml?nodeId={nodeId}\&formId={formId}\&step=99999
 * @author chengds
 *
 */

public class TableBakAction extends ActionSupport{
	
	private static final long serialVersionUID = 1L;
	private static Log log = LogFactory.getLog(TableBakAction.class);
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private FormConfigMapper<FormConfig> fm = (FormConfigMapper)SpringContextUtil.getBean("formConfigMapper");
	
	private String formId = null;
	private String nodeId = "0";
	private String step = "1000";
	
	@Override
	public String execute(){
		
//		nodeId = "14";
//		formId = "119";
		TableBak tb = new TableBak(nodeId, formId,Integer.parseInt(step));
		tb.start();
		return "tableBak";
	}
	
	
	//getter and setter
	public String getFormId() {
		return formId;
	}
	public void setFormId(String formId) {
		this.formId = formId;
	}
	public String getNodeId() {
		return nodeId;
	}
	public void setNodeId(String nodeId) {
		this.nodeId = nodeId;
	}
	public String getStep() {
		return step;
	}
	public void setStep(String step) {
		this.step = step;
	}
	
	private class TableBak extends Thread{
		
		private String nodeid = null;
		private String formid = null;
		private int step;
		
		private String r_creatTableSQL = "create table {bakTableName} as select * from {tableName} order by id limit 0,";
		private String r_insertSQL = "insert into {bakTableName} (select * from {tableName} order by id limit 0,{to})";
		
		private String d_creatTableSQL = "create table {bakTableName} as select * from {tableName} where {dateField}< {startDate} order by id limit 0,";
		private String d_insertSQL = "insert into {bakTableName} (select * from {tableName} where {dateField}< {startDate} order by id limit 0,{to})";
		private String d_recordCountSQL = "select count(*) a from {tableName} where {dateField}<";

		private String addPrimaryKey = "alter table {bakTableName} add primary key (id)";
		private String deleteSQL = "delete from {tableName} order by id limit ";
		
		public TableBak(String nid, String fid, int s){
			this.nodeid = nid;
			this.formid = fid;
			this.step = s;
			
		}
		public void run(){
			FormConfig ft = new FormConfig();
			ft.setNodeid(Integer.parseInt(nodeid));
			if(formid != null){
				ft.setId(Integer.parseInt(formid));
			}
			
			int limit = 0;
			int total = 0;
			int to = step;
			int left = 0;
			String tName = null;
			String tBakName = null;
			JSONObject param = null;
			boolean isTabExist = false;
			
			String creatsql = null;
			String addPK = null;
			String insertsql = null;
			String delsql = null;

			try{
				List<FormConfig> formList = fm.get(ft);
				Long st = System.currentTimeMillis();
				if(formList != null && formList.size()>0){
					for(FormConfig tmp: formList){
						String insert = null;
						param = new JSONObject(tmp.getConfig());
						if(param.has("tableBak")){
							tName = tmp.getTableName();
							tBakName = tName + "_bak";
							
							param = new JSONObject(param.get("tableBak").toString());
							if(!param.getBoolean("enableBak")){
								continue;
							}
							
							if(param.get("recordCount") != null && 
									!param.get("recordCount").toString().equals("")){
//							if(false){
								
								insertsql = r_insertSQL.replaceAll("\\{tableName\\}", tName).replaceAll("\\{bakTableName\\}", tBakName);
								creatsql = r_creatTableSQL.replaceAll("\\{tableName\\}", tName).replaceAll("\\{bakTableName\\}", tBakName);
								addPK =  addPrimaryKey.replaceAll("\\{bakTableName\\}", tBakName);
								delsql = deleteSQL.replaceAll("\\{tableName\\}", tName);

								to = step;
	
								limit = Integer.parseInt(param.get("recordCount").toString());
//								limit = 0;
								total = Integer.parseInt(MySQLHelper.ExecuteSql("select count(*) a from " + tmp.getTableName(), null).get(0).get("a").toString());
								
								left = total - limit;

							}else if(param.get("dateFieldName") != null && 
									!param.get("dateFieldName").toString().equals("")){
				
								String dateFieldName = param.get("dateFieldName").toString();
								String[] arr = dateFieldName.split(":");
								if(arr.length<2){
									throw new Exception("分表条件(天数)配置的数据格式不正确，应该是 日期字段:天数");
								}
								
								String dateField = arr[0];
//								String recordSQL = "select count(*) from {tableName} where createTime < ";
								int expireLimit = Integer.parseInt(arr[1]);
								Date d =  new Date((new Date()).getTime()-24*60*60*1000*expireLimit);
								String tempDate = (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(d).toString();
//								String tempDate = "2011-10-26 11:58:40";
								
								String rcsql = d_recordCountSQL.replaceAll("\\{tableName\\}", tName).replaceAll("\\{dateField\\}", dateField);

								total = Integer.parseInt(MySQLHelper.ExecuteSql(rcsql+"'"+tempDate+"'", null).get(0).get("a").toString());
								

								
								insertsql = d_insertSQL.replaceAll("\\{tableName\\}", tName)
										.replaceAll("\\{bakTableName\\}", tBakName)
										.replaceAll("\\{dateField\\}", dateField)
										.replaceAll("\\{startDate\\}", "'"+tempDate+"'");
								
								creatsql = d_creatTableSQL.replaceAll("\\{tableName\\}", tName)
										.replaceAll("\\{bakTableName\\}", tBakName)
										.replaceAll("\\{dateField\\}", dateField)
										.replaceAll("\\{startDate\\}", "'"+tempDate+"'");
								
								addPK =  addPrimaryKey.replaceAll("\\{bakTableName\\}", tBakName);
								delsql = deleteSQL.replaceAll("\\{tableName\\}", tName);
								
								left = total;

							}
							
							if(left<=0)continue;
							
							isTabExist = MySQLHelper.ExecuteSql("show tables like '" + tBakName + "'", null).size()> 0?true:false;
							
							to = left>step?step:left;
							
							if(isTabExist){
								
								insert = insertsql.replaceAll("\\{to\\}", to+"");
								MySQLHelper.ExecuteNoQuery(insert ,null);
								MySQLHelper.ExecuteNoQuery(delsql+to ,null);
							}else{
								
								MySQLHelper.ExecuteNoQuery(creatsql+to,null);
								MySQLHelper.ExecuteNoQuery(addPK,null);
								MySQLHelper.ExecuteNoQuery(delsql+to ,null);
							}
							
							left = left - step;
							
							while(left>0){								
								to = left>step?step:left;
								insert = insertsql.replaceAll("\\{to\\}", to+"");
								MySQLHelper.ExecuteNoQuery(insert,null);
								MySQLHelper.ExecuteNoQuery(delsql+to ,null);
								left = left - step;
							}
						}
					}
				}
				log.info("Total time: " + (System.currentTimeMillis() - st));
			}catch(Exception e){
				log.error("TableBak error: " + e.getMessage());
			}
		}	
	}
}
