{
	"success":${hasError?string("false","true")},
	"message":"${(msg?js_string)!""}"<#if fileExists??>,
	"fileExists":${fileExists?string("true","false")}</#if><#if deletionKey??>,
	"deletionKey":"${(deletionKey?js_string)!""}"</#if><#if undeletedFiles??>,
	"undeletedFiles":[
		<#list undeletedFiles as f>
			<#if f_index!=0>,</#if>{
				distributionAddress:"${f.distributionAddress!""}"
			}
		</#list>
	]</#if>
}