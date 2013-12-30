{result:[
	<#list slist as s>
	
		{
			key:"${s.key}",
			startDate:"${s.startDate?string('yyyy-MM-dd HH:mm:ss')}",
			nodeId:${s.nodeId},
			stype:"${s.stype!""}",
			timeout:${s.timeout},
			excutor:"${s.excutor!""}",
			status:"${s.status}",
			dataPoolJSON:"${(s.dataPoolJSON!"")?html?replace('[\r\n]','','r')}",
			daemonFlag:"${s.daemonFlag}"
		}
		<#if s_has_next>,</#if>
	</#list>
]}