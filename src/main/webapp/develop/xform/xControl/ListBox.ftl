<select name="xform.${id}" id="xform.${id}">
<#list data as r>
			<option value="${r.value}" <#if value==r.value>selected="selected"</#if>>${r.key}</option>
</#list>
</select>
