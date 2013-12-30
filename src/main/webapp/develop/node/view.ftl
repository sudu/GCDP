<#if node?exists>
{
	nodeId:#{node.id!0},
	name:"${node.name!""}",
	remoteHostJson:${node.remoteHostJson!"{}"},
	masterDBJson:${node.masterDBJson!"{}"},
	slaveDBJson:${node.slaveDBJson!"{}"},
	publicSearchHostsJson:${node.publicSearchHostsJson!"{}"},
	privateSearchHostsJson:${node.privateSearchHostsJson!"{}"},
	nosqlHostJson:${node.nosqlHostJson!"{}"},
	envMapJson:${node.envMapJson!"[]"}
}
<#else>
{
	success:false,
	message:'节点信息不存在'
}
</#if>