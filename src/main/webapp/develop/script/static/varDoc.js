[
	{
		name:"dataPool",
		intro:"数据池，java.util.Map类型，该对象用于存储脚本输入、输出、表单数据。",
		methods:[
		         {name:"dataPool.get(key)",intro:"获取key对应的值"},
		         {name:"dataPool.put(key,value)",intro:"写入key-value键值对"},
		         {name:"dataPool.remove(key)",intro:"移除key对应的值"}
		         ]
	}
	,{
		name:"pluginFactory",
		intro:"插件工厂对象，用于获取指定工具插件。",
		methods:[
		         {name:"pluginFactory.getP(pluginName)",intro:"获取指定插件，详情见“工具插件”。"}
		         ]
	}
	//,{
	//	name:"envMap",
	//	intro:"该对象用于存储环境变量，通常情况下为只读",
	//	methods:[
	//	         {name:"envMap.get(key)",intro:"获取key对应的值"}
	//	         ]
	//}
]