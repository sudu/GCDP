package com.me.GCDP.util.plugin;


import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.me.GCDP.util.Hanyu2Pinyin;
import com.me.GCDP.script.plugin.ScriptPlugin;
import com.me.GCDP.script.plugin.annotation.PluginClass;
import com.me.GCDP.script.plugin.annotation.PluginExample;
import com.me.GCDP.script.plugin.annotation.PluginIsPublic;
import com.me.GCDP.script.plugin.annotation.PluginMethod;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Company: ifeng.com</p>
 * @author :jiangy
 * @version 1.0
 * <p>--------------------------------------------------------------------</p>
 * <p>date                   author                    reason             </p>
 * <p>上午9:35:38              jiangy              create the class      </p>
 * <p>--------------------------------------------------------------------</p>
 */
@PluginClass(author = "jiangy", intro = "汉语拼音处理插件")
@PluginExample(intro = "//#plugin=pinYin#<br />"+"<br>var pinYinSet = pinYin.getPinYin(\"大家好\")"
)
public class pinYinPlugin extends ScriptPlugin {
	
	@SuppressWarnings("unused")
	private static Log log = LogFactory.getLog(pinYinPlugin.class);
	
	@Override
	public void init() {
		
	}
	
	@PluginIsPublic
	@PluginMethod(intro = "获取各种发音的拼音", 
			paramIntro = { "字符串"},
			returnIntro = "拼音集合"
			)
	public Set<String> getPinYin(String src){
		
		return Hanyu2Pinyin.getPinyin(src);
	}
	
	@PluginIsPublic
	@PluginMethod(intro = "获得各种发音的简拼", 
			paramIntro = { "字符串"},
			returnIntro = "简拼集合"
			)
	public Set<String> getJianPin(String src){
		
		return Hanyu2Pinyin.getJianpin(src);
	}
	
	@PluginIsPublic
	@PluginMethod(intro = "获得最常用的一个发音的拼音", 
			paramIntro = { "字符串"},
			returnIntro = "拼音"
			)
	public String getOnePinYin(String src){
		Set<String> set = Hanyu2Pinyin.getPinyin(src, true);
		String pinYin = set.iterator().next();
		return pinYin;
	}
	
	@PluginIsPublic
	@PluginMethod(intro = "获得最常用的一个发音的简拼", 
			paramIntro = { "字符串"},
			returnIntro = "简拼"
			)
	public String getOneJianPin(String src){
		Set<String> set = Hanyu2Pinyin.getJianpin(src, true);
		String jianPin = set.iterator().next();
		return jianPin;
	}
	@PluginIsPublic
	@PluginMethod(intro = "获得最常用的一个发音的拼音和简拼", 
			paramIntro = { "字符串"},
			returnIntro = "{\"JIANPIN\":\"\",\"PINYIN\":\"拼音值\"}"
			)
	public Map<String,String> getPinYinAndJianPin(String src){
		return Hanyu2Pinyin.getPinyinAndJianpin(src);
	}
	
//**********************************测试
//	public static void main(String[] args){
//		pinYinPlugin instance = new pinYinPlugin();
//		System.out.println(new JSONObject(instance.getPinYinAndJianPin("#$%#$%#$")));
//	}
}
