{
	 success:${hasError?string("false","true")},
	 data:[
	<#list exsistingProcessList as p>
<#if p_index!=0>,</#if>{
			id:#{p.id!0},
			status:#{p.status!0},
			processTitle:"${(p.processTitle?js_string)!""}",
			creator:"${(p.creator?js_string)!""}",
			createTime:"${p.createTime?if_exists?string("yyyy-MM-dd HH:mm:ss")}" ,
			recentModifyTime:"${p.recentModifyTime?if_exists?string("yyyy-MM-dd HH:mm:ss")}"
		}</#list>
	 ],
	 message:"${(msg?js_string)!""}"
}
