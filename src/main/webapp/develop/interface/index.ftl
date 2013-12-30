[<#if ifList?exists>
	<#list ifList as interface>
	<#if interface_index != 0>,</#if>
	{id:#{interface.id},name:"${(interface.name!"")?js_string}",creator:"${interface.creator!""}",createTime:"${interface.createdatestr!""}"}
	</#list>
</#if>]

