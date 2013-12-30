<#if callback?? && callback!=''>${callback}(</#if>{
	"totalCount":#{recordCount},
	"data":${recordJson!"[]"}
}<#if callback?? && callback!=''><#if ext?? && ext!=''>,${ext}</#if>)</#if>