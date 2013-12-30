alter TABLE `${tableName}`
  <#list data as field>
	${field}<#if field_has_next>,</#if>
  </#list>
