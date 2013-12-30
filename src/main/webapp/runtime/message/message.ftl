[
<#list msgList as msg>
<#if msg_index!=0>,</#if>
	{
		id:#{msg.id},
		title:"${msg.title?js_string!""}",
		creator:"${msg.creator?js_string!""}",
		datetime:"${msg.createDateStr!""}",
		content:"${msg.content?js_string!""}"
	}
</#list>
]