<#if hasError==true>{
"success":${hasError?string("false","true")},
 "message":"${(msg?js_string)!""}"
}<#else>{
	"welcomeUrl":"${(welcomeUrl!"")?js_string}",
	"menuConfig":"${(menuConfig!"")?js_string}",
	"headInject":"${(headInject!"")?js_string}",
	"bodyInject":"${(bodyInject!"")?js_string}"
}</#if>