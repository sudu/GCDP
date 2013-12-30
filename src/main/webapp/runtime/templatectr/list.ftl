[
<#list listData as item>
	<#if item_index!=0>,</#if>
	{
		value:"${item.id!""}",
		text:"${item.name!""}",
		text_qp:${item.quanpin!"[]"},
		text_jp:${item.jianpin!"[]"},
		dataId:"${item.dataId!""}",
		group:<#if item.dataId=="0">"通用模板"<#else>"私有模板"</#if>
	}
</#list>
]