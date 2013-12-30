{
"success":${hasError?string("false","true")},
 "message":"${(msg?js_string)!""}"<#if interf??><#if interf.id??>,
 "id":${interf.id!"0"}</#if></#if>
}