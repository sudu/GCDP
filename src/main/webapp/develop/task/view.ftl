<#if task?exists>
{
	id:#{task.id!0},
	taskName:"${task.taskName!""}",
	taskGroup:"${task.taskGroup!""}",
	startTime:'${task.startTime?string("yyyy-MM-dd HH:mm:ss")}',
	endTime:'${task.endTime?string("yyyy-MM-dd HH:mm:ss")}',
	nodeId:#{task.nodeId!0},
	repeatCount:#{task.repeatCount!0},
	remainCount:#{task.remainCount!0},
	repeatInterval:#{task.repeatInterval!0},
	exp:"${task.exp!""}",
	script:"${(task.script?js_string)!""}",
	status:#{task.status!0},
	previousFireTime:"${(info.previousFireTime?string("yyyy-MM-dd HH:mm:ss"))!"-"}",
	nextFireTime:"${(info.nextFireTime?string("yyyy-MM-dd HH:mm:ss"))!"-"}",
	lastErrTime:"${(info.lastErrTime?string("yyyy-MM-dd HH:mm:ss"))!"-"}",
	errCount:#{info.errCount},
	runCount:#{info.runCount},
	currentStatus:"${(info.status?js_string)!""}",
	runTime:#{info.runTime!0},
	errInfo:
	[
		<#list info.errInfo as info>
			<#if info_index!=0>,</#if>"${(info?js_string)!""}"
		</#list>
	]
}
<#else>
{
	success:false,
	message:'任务信息不存在'
}
</#if>