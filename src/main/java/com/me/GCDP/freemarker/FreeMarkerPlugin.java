/**
 * 
 */
package com.me.GCDP.freemarker;

import java.io.IOException;
import java.util.Map;

import com.me.GCDP.script.plugin.ScriptPlugin;
import com.me.GCDP.script.plugin.annotation.PluginClass;
import com.me.GCDP.script.plugin.annotation.PluginExample;
import com.me.GCDP.script.plugin.annotation.PluginIsPublic;
import com.me.GCDP.script.plugin.annotation.PluginMethod;

import freemarker.template.TemplateException;

/**
 * @author zhangzy
 * 2012-4-16
 */
@PluginClass(author = "zhangzhiyu", intro = "FreeMarker插件",tag="模板")
@PluginExample(intro = "var freemarker=pluginFactory.getP(\"freemarker\");<br />"+
"var result = freemarker.process(\"template\",map);<br />"
)
public class FreeMarkerPlugin extends ScriptPlugin {
	
	@Override
	public void init() {
		
	}
	
	@PluginIsPublic
	@PluginMethod(intro = "FreeMarker处理模版",
		returnIntro = "处理结果", 
		paramIntro = { "模版内容","数据" })
	public String process(String templateContent, Map<String, Object> rootMap) throws IOException, TemplateException{
		return FreeMarkerHelper.process2(templateContent, rootMap);
	}
}
