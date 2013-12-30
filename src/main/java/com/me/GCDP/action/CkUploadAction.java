package com.me.GCDP.action;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.me.GCDP.util.MD5;
import com.me.GCDP.util.property.CmppConfig;
import com.opensymphony.xwork2.ActionSupport;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Company: ifeng.com</p>
 * @author :Hu WeiQi
 * @version 1.0
 * <p>--------------------------------------------------------------------</p>
 * <p>date                   author                    reason             </p>
 * <p>2008-12-4               Hu WeiQi               create the class     </p>
 * <p>--------------------------------------------------------------------</p>
 */

public class CkUploadAction extends ActionSupport {
	
	private static final long serialVersionUID = 1L;
	
	private static Log log = LogFactory.getLog(CkUploadAction.class);
	
	private File upload;
	private String uploadFileName;
	
	private String domain = null;
	
	private String url = null;
	
	//仅上传
	public String upload(){
		String file_ext = uploadFileName.substring(uploadFileName.lastIndexOf(".")).toLowerCase();
		String filepath = CmppConfig.getKey("cmpp.upload.dir");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		String filepath2 = "ck/" + sdf.format(new Date()) + "/" + MD5.encode(System.currentTimeMillis()+uploadFileName+new Random().nextInt(1000)).substring(0, 15).toLowerCase();
		File diskFile = new File(filepath, filepath2+file_ext);
		try{
			//处理上传空文件异常的情况
			if(upload.length()==0){
				diskFile.getParentFile().mkdirs();
				diskFile.createNewFile();
			}else{
				if(!diskFile.exists()){
					if(!diskFile.getParentFile().exists()){
						diskFile.getParentFile().mkdirs();
					}
					upload.renameTo(diskFile);
				}else{
					log.error("diskFile already exists");
				}
			}
			url = CmppConfig.getKey("cmpp.upload.fileurl.prefix")+filepath2+file_ext;
			log.info("file upload : " + uploadFileName + " | url : " + url);
		}catch(Exception e){
			log.error(e.getMessage());
			url = "";
		}
		return "upload";
	};
	
	/*//上传并分发
	private String send(){
		String file_ext = uploadFileName.substring(uploadFileName.lastIndexOf(".")).toLowerCase();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy_ww");
		url = "http://" + domain + "/" +CmppConfig.getKey("cmpp.upload.sendfile.prefix")+
				"/"+sdf.format(new Date()) + "/" + MD5.encode(System.currentTimeMillis()+uploadFileName+new Random().nextInt(1000)).substring(0, 15).toLowerCase() +file_ext;
		SendFilePlugin sendFile = new SendFilePlugin();
		sendFile.setDir("");
		try{
			sendFile.sendFile(upload.getAbsolutePath(), url);
			log.info("file upload & send : " + uploadFileName + " | url : " + url);
		}catch(Exception e){
			try {
				sendFile.sendFile(upload.getAbsolutePath(), url);
				log.info("file upload & send retry : " + uploadFileName + " | url : " + url);
			} catch (Exception e1) {
				log.error("send : " + e.getMessage() + " retry!");
				e1.printStackTrace();
			}
			log.error("send : " + e.getMessage());
		}
		return "upload";
	}
	*/
	/*public String execute(){
		
		if(upload == null || upload.length() == 0 || uploadFileName == null){
			url = "";
			return "upload";
		}
		
		if(domain == null || domain.equals("")){
			return this.upload();
		}else{
			return this.send();
		}
	}*/

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public File getUpload() {
		return upload;
	}

	public void setUpload(File upload) {
		this.upload = upload;
	}

	public String getUploadFileName() {
		return uploadFileName;
	}

	public void setUploadFileName(String uploadFileName) {
		this.uploadFileName = uploadFileName;
	}

	public String getUrl() {
		return url;
	}
	
}
