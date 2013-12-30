{
	formlist:[
	<#list formList as form>
		<#if form_index!=0>,</#if>
		{
			id:#{form.id!0},
			title:"${form.name?js_string!""}"
		}
	</#list>
	]
}
			