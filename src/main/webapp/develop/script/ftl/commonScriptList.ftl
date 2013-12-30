[
<#if slist?exists>
<#list slist as script>
	<#if script_index!=0>,</#if>{
		id:#{script.id},
		name:"${(script.name!"")?js_string}",
		creator:"${(script.creator!"")?js_string}",
		createDateStr:"${script.createDateStr!""}",
		intro:"${(script.intro!"")?js_string}"	
	}
</#list>
</#if>
]