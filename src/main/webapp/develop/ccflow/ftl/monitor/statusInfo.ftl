{
	 success:${hasError?string("false","true")},
	 data:{
	<#list pluginStatusList as p>
<#if p_index!=0>,</#if>
		"${(p.cfgCode?js_string)!"0"}":{
			state:#{p.status!0},
			pluginStartDate:"${p.pluginStartDate?if_exists?string("yyyy-MM-dd HH:mm:ss")}",
			pluginEndDate:"<#if p.pluginEndDate?exists>${p.pluginEndDate?string("yyyy-MM-dd HH:mm:ss")}</#if>"
		}</#list>
	 },
	 message:"${(msg?js_string)!""}"
}
