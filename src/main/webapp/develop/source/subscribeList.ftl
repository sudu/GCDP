{
	nodeId:#{nodeId!0},
	sourceId:#{source.id!0},
	subs:[
		<#list subscribeList as subscribe>
		<#if subscribe_index!=0>,</#if>{
			id:#{subscribe.id!0},
			callBackUrl:"${subscribe.callBackUrl!""}",
			method:"${subscribe.method!""}",
			creator:"${subscribe.creator!""}",
			status:"${subscribe.status!""}",
			subscribeDateStr:"${subscribe.subscribeDateStr!""}",
			lastPushDateStr:"${(subscribe.info.lastPushDateStr)!""}",
			pushErrCount:#{(subscribe.info.pushErrCount)!0},
			scriptErrCount:#{(subscribe.info.scriptErrCount)!0},
			lastPushErrCount:#{(subscribe.info.lastPushErrCount)!0},
			lastScriptErrCount:#{(subscribe.info.lastScriptErrCount)!0},
			errInfo:"${(subscribe.info.lastErrInfo)!""}",
			totalCount:#{(subscribe.info.totalCount)!0},
			lastId:#{(subscribe.info.lastId)!0}
		}
		</#list>
	]
}
