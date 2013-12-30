{
	success:${hasError?string("false","true")},
	totalCount:#{totalCount!0},
	data:[<#list processInstanceList as p><#if p_index!=0>,</#if>{
			id:#{p.id!0},
			nodeId:#{p.nodeId!0},
			formId:#{p.formId!0},
			articleId:#{p.articleId!0},
			formName:"${(p.formName?js_string)!""}",
			state:#{p.state!0},
			processDefinitionId:${p.processDefinitionId!"0"},
			processStartDate:"${p.processStartDate?if_exists?string("yyyy-MM-dd HH:mm:ss")}" ,
			instanceDesc:"${(p.instanceDesc?js_string)!""}"
		}</#list>
	],
	message:"${(msg?js_string)!""}"
}
