<#if info?exists>
{
	lastPushDateStr:"${(info.lastPushDateStr)!""}",
	pushErrCount:#{(info.pushErrCount)!0},
	scriptErrCount:#{(info.scriptErrCount)!0},
	lastPushErrCount:#{(info.lastPushErrCount)!0},
	lastScriptErrCount:#{(info.lastScriptErrCount)!0},
	errInfo:"${((info.lastErrInfo)!"")?js_string}",
	totalCount:#{(info.totalCount)!0},
	lastId:#{(info.lastId)!0}
}
<#else>
{
	success:false,
	message:'没有数据在回放'
}
</#if>