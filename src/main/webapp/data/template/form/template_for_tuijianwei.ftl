 <!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <title>表单视图for推荐位</title>
	<link rel="stylesheet" type="text/css" href="../res/js/ext2/resources/css/ext-all.css" />
	<link rel="stylesheet" type="text/css" href="../res/js/ext2/resources/css/patch.css" />
	<link rel="stylesheet" type="text/css" href="../res/css/runTime.css" />
	<#if relyCSSList??>
	<!-- 控件依赖的CSS 开始 -->
	<#list relyCSSList as src>	
		<#if src??>
	<link rel="stylesheet" type="text/css" href="../res/css/${src!""}" />
		</#if>
	</#list>
	<!-- 控件依赖的CSS 结束 -->
	</#if>
	<style>
		html, body {
	        font:normal 12px verdana;
	        margin:0;
	        padding:0;
	        border:0 none;
	        height:100%;
	    }
	    .itemStyle {
		    padding:5px;
		    /*border: 1px solid silver;*/
		    margin-bottom:0;
		}
		.fieldsetStyle{
			margin:4px 10px 4px 10px
		}
	</style>
 	<script type="text/javascript" src="../res/js/ext2/adapter/ext/ext-base.js"></script>
    <script type="text/javascript" src="../res/js/ext2/ext-all-debug.js"></script>
	<script type="text/javascript" src="../res/js/ext2/ext-lang-zh_CN.js?v=20121210"></script>
	<script type="text/javascript" src="../res/js/ext_base_extension.js"></script>  
	<script type="text/javascript" src="../res/js/config/commonUI4RT_cfg.js"></script>  

	<#if relyJSList??>
	<!-- 控件依赖的JS 开始 -->	
	<#list relyJSList as src>	
		<#if src??>
		<script type="text/javascript" src="../res/js/${src!""}"></script>
		</#if>
	</#list>
	</#if>
	<!-- 控件依赖的JS 结束 -->
	<script type="text/javascript">
		//吐出的数据
		var formConfig__ = {
			id:#{id!0},
			formId:#{formId!0},
			viewId:#{viewId!0},
			nodeId:#{nodeId!0},
			config: ${config!0},
			recordData:decodeURIComponent("${(recordData!"{}")?url('UTF-8')}"),
			dataSource:${dataSource!"{}"}			
		};
		formConfig__.recordData = formConfig__.recordData?Ext.decode(formConfig__.recordData):{};
		
	</script>
	${headInject!""}
</head>
<body>

<script src="../data/template/form/FormRenderPackage.js?20130909"></script>
<script type="text/javascript">
var hanler_b,hanler_a;
var onloadJsFunction={};
var formPanel;
RunTime = {
	hanler_onload:null,
	jsHanler_b:null,
	onloadJsFunction:{},//注册的事件
	editRow:-1,//当前编辑的记录行
	postData:function(closePage){
		//从url获取postMessage__参数的值，postMessage__是从父页面弹出时注入到url的	
		if(!this.postMessage__) {
			this.postMessage__ = (function(){
				var query = location.search.substring(1); // get query string 
				var pairs = query.split("&");
				for (var i = 0; i < pairs.length; i++) { 
					var pos = pairs[i].indexOf('='); // look for "name=value" 
					if (pos == -1) continue; // if not found, skip 
					var argname = pairs[i].substring(0, pos);
					if(argname==="postMessage__"){
						var value = pairs[i].substring(pos + 1);
						return value;
					}
				}
				return "{}";
			})();
		}
		var values={};
		if(!closePage){
			//运行保存前脚本
			try{
				if(typeof(hanler_b)=='function'){
					hanler_b();
				}
			}catch(ex){alert("执行保存前脚本出错");}
			
			values = this.formPanel.form.getValues();
		}
		for(var name in values){
			values[name.replace(/xform./g,"")] = values[name];
			delete values[name];
		}
		var parentMsgJson = eval("(" + decodeURIComponent(this.postMessage__) + ")");
		var postData = {
			data:{
				values:values,
				editRow:this.editRow,
				action:closePage?'cancel':'submit'
			},
			options: this.postMessage__ //从URL接收到的数据
		};
		var sender;
		if(parentMsgJson.sender){//让父页面来决定sender
			eval('0,' + 'sender = ' + decodeURIComponent(parentMsgJson.sender));
		}else{
			sender = window.opener||window.parent;
		}
		sender && sender.postMessage(Ext.encode(postData), '*');	
		
		if(!closePage){
			//运行保存后脚本
			try{
				if(typeof(hanler_a)=='function'){
					hanler_a();
				}
			}catch(ex){alert("执行保存后脚本出错");}
		}
	}
}
//表单渲染
RunTime.Render=function(cfg){
	var frm = cfg.config.form;
	frm.ui.bodyStyle='padding:10px';
	frm.ui.itemCls="itemStyle";
	frm.ui.title='';
	frm.ui.autoScroll=true;
	frm.ui.layout = 'xform2';
	formPanel = new Ext.form.FormPanel(frm.ui);
	
	if(frm.script && frm.script.s_beforeJs){//保存前脚本
		hanler_b = eval('0,'+frm.script.s_beforeJs);
	}
	if(frm.script && frm.script.s_savedJs){//保存后脚本
		hanler_a = eval('0,'+frm.script.s_savedJs);
	}

	if(frm.script && frm.script.s_afterJs){//加载后脚本
		RunTime.hanler_onload = eval('0,'+frm.script.s_afterJs);
	}

	if(frm.script && frm.script.s_onloadJs){
		try{
			eval('onloadJsFunction={' + frm.script.s_onloadJs + '}');
		}catch(e){
			;
		}
	}

	formPanel.addButton({
		text:'确定',
		id:'btn_b_save',
		type:'button',
		handler:function(){
			RunTime.postData();
		}
	});	

	formPanel.addButton({
		text:'关闭',
		id:'btn_b_close',
		type:'button',
		handler:function(){
			RunTime.postData(true);
		}
	});	

	createUI(frm.controls,formPanel);
	RunTime.formPanel=formPanel;
	return formPanel;
}

RunTime.message = {
	onMessage:function(e){
		var recieve = e.data;
		recieve = Ext.util.JSON.decode(recieve);
		RunTime.editRow = recieve.editRow;
		if(recieve.action=='submit'){
			RunTime.postData();
		}else if(recieve.action=='init'){
			//初始化值
			if(recieve.data){
				var r = recieve.data;
				for(var name in r){
					r["xform." + name] = r[name];
					delete r[name];
				}
				var form = RunTime.formPanel.form;
				form.reset();
				form.setValues(r);
			}
		}
	}
}


Ext.onReady(function(){
	Ext.BLANK_IMAGE_URL = "../res/js/ext2/resources/images/default/s.gif";
	//初始化信息提示功能
	Ext.QuickTips.init();
	//统一指定错误信息提示浮动显示方式
	Ext.form.Field.prototype.msgTarget = 'under';//'qtip';
	
	var mainPanel = new Ext.Viewport({
		layout: 'border',
		items:[{
			xtype:'panel',
			region:'center',
			layout:'fit',
			buttonAlign:'center',
			items:[RunTime.Render(formConfig__)]
		}]
	});
	
	//运行onload脚本
	try{
		if(typeof(RunTime.hanler_onload)=='function'){
			RunTime.hanler_onload();
		}
	}catch(ex){
		
	}

});


//监听postMessage消息事件
if (typeof window.addEventListener != 'undefined') {
	window.addEventListener('message', RunTime.message.onMessage, false);
} else if (typeof window.attachEvent != 'undefined') {
	window.attachEvent('onmessage', RunTime.message.onMessage);
}
</script>	
${bodyInject!""}		
</body>
</html>	
	