[
<#list sourceList as source>
	<#if source_index!=0>,</#if>
	{
		id:#{source.id!0},
		title:"${source.name?js_string!""}"
	}
</#list>
]
