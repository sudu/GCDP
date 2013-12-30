{
success:true,
total:${total},
rows:[
	<#list data as item>
	 {id:#{item.id},name:'${item.name!""}',status:#{item.status!0},svrIp:'${item.svrIp!""}'}<#if item_has_next>,</#if>
	</#list>
	]
}