[
<#list nlist as node>
	<#if node_index!=0>,</#if>
	{
		nodeId:#{node.id!0},
		title:"${(node.name!"")?js_string}",
		img:[],
		//nodeUrl:"${node.runtimeUrl!""}",
		nodeUrl:"runtime/index.jhtml?nodeId=#{node.id!0}",
		en:[${node.quanPin!"[]"},${node.jianPin!"[]"}]
	}
</#list>
]
