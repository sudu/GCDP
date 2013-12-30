{
	nodeId:#{nodeid!0},
	formId:#{form.id!0},
	fields:[
		<#list fieldList as field>
		<#if field_index!=0>,</#if>{
			name:"${field.field!""}",
			type:"${field.type!""}",
			title:"${field.desc!""}"
		}
		</#list>
	]
}