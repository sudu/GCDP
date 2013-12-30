[
<#list idxTypeList as item>
   <#if item_index!=0>,</#if>{
    id:${item.id},
	title:'${item.title!""}',
    formId:${item.formId!"0"},
	configUrl:'${item.configUrl!""}'
  }
</#list>
]