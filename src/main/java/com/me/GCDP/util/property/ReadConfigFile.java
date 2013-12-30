package com.me.GCDP.util.property;

import java.io.File;
import java.io.FileInputStream;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.PropertyResourceBundle;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

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
public class ReadConfigFile {
	
	private static boolean isAutoReload = true;
	
	private static final Log log = LogFactory.getLog(ReadConfigFile.class);
    //配置文件上一次修改时间
    private static HashMap<String, Long> lastmodifyconfigFileTime = new HashMap<String, Long>();
    //上一次读配置文件的时间
    private static HashMap<String, Long> lastreadconfigFileTime = new HashMap<String, Long>();
    //配置文件被保存在HashMap中。
    private static HashMap<String, HashMap<String, String>> allhashtable = new HashMap<String, HashMap<String, String>>();

    static{
    	log.info("loading config file ...");
    	isAutoReload = false;
		ReadConfigFile.initConfigFile("conf//cmpp.properties");
		ReadConfigFile.initConfigFile("conf//script.properties");
		ReadConfigFile.initConfigFile("conf//jdbc.properties");
		ReadConfigFile.initConfigFile("conf//search.properties");
		ReadConfigFile.initConfigFile("conf//image.properties");
		ReadConfigFile.initConfigFile("conf//queue.properties");
		ReadConfigFile.initConfigFile("conf//workflow.properties");
		log.info("load finish !");
    }
    
    public static void setAutoReload(boolean isReload){
    	isAutoReload = isReload;
    }
    
    /**
     * 获取一个指定键所对应的值。
     *
     * @param keyName String 指定键的名称。
     * @return String
     */
    public static String getKey(String ConfigFileName, String keyName) {
        if (ConfigFileName == null || keyName == null) {
            return null;
        }
        if(isAutoReload){
        	log.info("testasfsdfasdfaf");
        	initConfigFile(ConfigFileName);
        }
        HashMap<String, String> ahash = allhashtable.get(ConfigFileName);
        if (ahash != null && ahash instanceof HashMap) {
            return ahash.get(keyName.trim());
        } else {
            return null;
        }
    }

    /**
     * 从文件中读出配置文件的内容。
     */
    public static void initConfigFile(String ConfigFileName) {
        try {
            if (reloadfile(ConfigFileName)) {
                ClassLoader classloader = ReadConfigFile.class.getClassLoader();
                PropertyResourceBundle resource = null;
                //ConfigFileName
                File configFile = new File(URLDecoder.decode(classloader.getResource(ConfigFileName).getPath(),"utf-8"));
                lastmodifyconfigFileTime.put(ConfigFileName, new Long(configFile.lastModified()));
                resource = new PropertyResourceBundle(new FileInputStream(configFile));
                Enumeration<String> enumeration = resource.getKeys();
                HashMap<String, String> temphast = new HashMap<String, String>();
                while (enumeration.hasMoreElements()) {
                    String name = enumeration.nextElement();
                    temphast.put(name.trim(), resource.getString(name).trim());
                }
                allhashtable.put(ConfigFileName, temphast);
            }
        } catch (Exception e) {
            log.error("在读" + ConfigFileName + "配置文件时出现异常", e);
        }
    }

    /**
     * 判断是否需要重新阅读配置文件
     *
     * @return
     */
    private static boolean reloadfile(String ConfigFileName) throws Exception {
        Object readtime = lastreadconfigFileTime.get(ConfigFileName);
        long longreadtime = 0;
        if (readtime != null && readtime instanceof Long) {
            longreadtime = ((Long) readtime).longValue();
        }

        if (longreadtime == 0 || (System.currentTimeMillis() - longreadtime > 10 * 1000)) {
            lastreadconfigFileTime.put(ConfigFileName, new Long(System.currentTimeMillis()));
            long configFileTime = 0;
            Object otime = lastmodifyconfigFileTime.get(ConfigFileName);
            if (otime != null && otime instanceof Long) {
                configFileTime = ((Long) otime).longValue();
            }
            if (configFileTime == 0) {
                return true;
            }

            ClassLoader classloader = ReadConfigFile.class.getClassLoader();

            if (classloader.getResource(ConfigFileName) == null) {
                throw new Exception();
            }
            File tempfile = new File(classloader.getResource(ConfigFileName).getPath());

            if (tempfile.lastModified() != configFileTime) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }
    
}