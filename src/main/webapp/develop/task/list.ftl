[
	<#list taskList as task>
		<#if task_index!=0>,</#if>{
			id:#{task.id!0},
			taskName:"${task.taskName!""}"
		}
	</#list>
]
