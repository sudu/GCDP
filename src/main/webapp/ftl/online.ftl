<#if ulist?exists && ulist?size!=0>
[
	<#list ulist as u>
	<#if u_index!=0>,</#if>{
		userName:"${u.username}"<#if username?exists><#if username==u.username>,isCurrent:true,datetime:"${.now?string("yyyy-MM-dd HH:mm:ss")}"</#if></#if>
	}
	</#list>
]
<#else>
{
"success":${hasError?string("false","true")},
"message":"${(msg?js_string)!""}",
}
</#if>