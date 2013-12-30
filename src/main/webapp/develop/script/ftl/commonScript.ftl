{<#if ss?exists>
	"ss.id":#{ss.id},
	"ss.name":"${ss.name!""}",
	"ss.creator":"${ss.creator!""}",
	"ss.createDateStr":"${ss.createDateStr!""}",
	"ss.intro":"${(ss.intro!"")?js_string}",
	"ss.script":"${(ss.script!"")?js_string}"
</#if>}
