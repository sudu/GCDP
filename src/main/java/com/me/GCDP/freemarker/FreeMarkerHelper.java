package com.me.GCDP.freemarker;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

/**
 * <p>Title: freemarker助手类</p>
 * <p>Description: 一些处理freemarker的静态方法</p>
 * <p>Company: ifeng.com</p>
 * @author :huwq
 * @version 1.0
 * <p>--------------------------------------------------------------------</p>
 * <p>date                   author                    reason             </p>
 * <p>2011-6-27              huwq               create the class      </p>
 * <p>--------------------------------------------------------------------</p>
 */

public class FreeMarkerHelper {
	
	private static Log log = LogFactory.getLog(FreeMarkerHelper.class);
	
	private static Configuration cfg = new Configuration();
	
	static{
		cfg.setNumberFormat("#");
		cfg.setSharedVariable("env", new GlobalVarModel());
		cfg.setSharedVariable("include", new IncludeDirective());
	}
	
	/*static {
		try {
			cfg.setDirectoryForTemplateLoading(new File(ToolsUtil.getRootRealPath()));
			//cfg.setDirectoryForTemplateLoading(new File("D:/work/workspace110126/Cmpp_v2/WebContent"));
			cfg.setObjectWrapper(new DefaultObjectWrapper()); 
		} catch (IOException e) {
			log.error(e.getMessage());
		} 
	}*/
	
	/**
	 * 读取指定freemarker模板文件，并代入数据
	 * @param templateUrl 模板相对工程根目录路径
	 * @param rootMap 数据模型
	 * @return 模板和数据模型合并后的内容
	 * @throws IOException 
	 * @throws TemplateException 
	 */
	/*public static String process(String templateUrl, Map<String, Object> rootMap) throws IOException, TemplateException{
		String result = null;
		try {
			Template temp = cfg.getTemplate(templateUrl, "UTF-8");
	        Writer out = new StringWriter();
	        temp.process(rootMap, out);
	        result = out.toString();
	        out.flush();
	        out.close();
		} catch (IOException e) {
			result = e.getMessage();
			log.error(e.getMessage());
			throw e;
		} catch (TemplateException te){
			result = te.getMessage();
			log.error(te.getMessage());
			throw te;
		}
		return result;
	}*/
	
	/**
	 * 向freemarker模板代码中代入数据
	 * @param templateContent 模板代码
	 * @param rootMap 数据模型
	 * @return 模板和数据模型合并后的内容
	 * @throws IOException 
	 * @throws TemplateException 
	 */
	public static String process2(String templateContent, Map<String, Object> rootMap) throws IOException, TemplateException{
		String result = null;
		try {
			StringReader sr = new StringReader(templateContent);
			Template temp = new Template("test", sr, cfg, "UTF-8");
	        Writer out = new StringWriter();
	        temp.process(rootMap, out);
	        result = out.toString();
	        out.flush();
	        out.close();
	        sr.close();
		} catch (IOException e) {
			result = e.getMessage();
			log.error(e.getMessage());
			throw e;
		} catch (TemplateException te){
			result = te.getMessage();
			log.error(te.getMessage());
			throw te;
		}
		return result;
	}

	public static void main(String[] args) throws IOException, TemplateException {
        Map root = new HashMap(); 
        root.put("result", "好的dddsssbbbccc");
        
        String templateContent = "<@env nodeId=\"11003\" var=\"abc\" />";
        //String templateURI = "ftl/result.ftl";
        
        //String result1 = FreeMarkerHelper.process(templateURI, root);
        //System.out.println(result1);
        String result2 = FreeMarkerHelper.process2(templateContent, root);
        System.out.println(result2);
	        
	}

}
