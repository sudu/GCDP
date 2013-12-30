package com.me.GCDP.util.env;

import java.util.Map;

import com.me.GCDP.script.plugin.ScriptPlugin;
import com.me.GCDP.script.plugin.annotation.PluginClass;
import com.me.GCDP.script.plugin.annotation.PluginExample;
import com.me.GCDP.script.plugin.annotation.PluginIsPublic;
import com.me.GCDP.script.plugin.annotation.PluginMethod;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Company: ifeng.com</p>
 * @author :huwq
 * @version 1.0
 * <p>--------------------------------------------------------------------</p>
 * <p>date                   author                    reason             </p>
 * <p>2012-6-4              huwq               create the class      </p>
 * <p>--------------------------------------------------------------------</p>
 */
@PluginClass(author = "jiangy", intro = "Cmpp环境变量获取插件")
@PluginExample(intro = "var env = pluginFactory.getP(\"env\");<br />"+
		"var test = env.getEnvByKey(14,\"test\")")
public class EnvPlugin extends ScriptPlugin {

	@Override
	public void init() {
	}
	
	@PluginIsPublic
	@PluginMethod(intro = "通过节点ID和Key获取指定的环境变量值", 
			paramIntro = {"节点ID","环境变量Key"},
			returnIntro ="环境变量值(字符串)")
	public String getEnvByKey(int nodeId,String keyValue){
		Map<String,Object> map = NodeEnv.getNodeEnvInstance().getEnvByKey(nodeId, keyValue);
		if(map != null && map.containsKey("value")){
			return map.get("value").toString();
		}else{
			return null;
		}
	}

}
