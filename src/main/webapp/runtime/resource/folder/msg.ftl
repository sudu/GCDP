{
	"success":${hasError?string("false","true")},
	 "message":"${(msg?js_string)!""}"
	 <#if fileExists??>,
		"fileExists":${fileExists?string("true","false")}
	 </#if>
}