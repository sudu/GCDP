 <!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <title>表单视图</title>
	<link rel="stylesheet" type="text/css" href="../res/js/ext2/resources/css/ext-all.css" />
	<link rel="stylesheet" type="text/css" href="../res/js/ext2/resources/css/patch.css" />
	<link rel="stylesheet" type="text/css" href="../res/css/runTime.css?20130408" />
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
    <script type="text/javascript" src="../res/js/ext2/ext-all-debug.js?20130411"></script>
	<script type="text/javascript" src="../res/js/ext2/ext-lang-zh_CN.js?v=20121210"></script>
	<script type="text/javascript" src="../res/js/ext_base_extension.js?20130418"></script>  
	<script type="text/javascript" src="../res/js/config/commonUI4RT_cfg.js?20130408"></script>  

	<#if relyJSList??>
	<!-- 控件依赖的JS 开始 -->	
	<#list relyJSList as src>	
		<#if src??>
		<script type="text/javascript" src="../res/js/${src!""}"></script>
		</#if>
	</#list>
	<!-- 控件依赖的JS 结束 -->
	<#else>	
		<script type="text/javascript" src="../res/js/controls/Ext.ux.TextField.js"></script>  
		<script type="text/javascript" src="../res/js/controls/Ext.ux.CheckboxGroup.js"></script>  
		<script type="text/javascript" src="../res/js/controls/Ext.ux.DateTime.js"></script>  
		<script type="text/javascript" src="../res/js/controls/Ext.ux.FieldSet.js"></script>  
		<script type="text/javascript" src="../res/js/controls/Ext.ux.RadioGroup.js"></script>  
		<script type="text/javascript" src="../res/js/controls/Ext.ux.Sort.js"></script>  
		<script type="text/javascript" src="../res/js/controls/Ext.ux.TextArea2.js"></script>  	
		<!--关联combo-->
		<script type="text/javascript" src="../res/js/controls/Ext.ux.LinkageComboBox.js"></script>
		<script type="text/javascript" src="../res/js/controls/Ext.ux.LinkageFieldSet.js"></script>
		<script type="text/javascript" src="../res/js/controls/Ext.ux.LinkageFieldText.js"></script>
		<!--头条新闻管理-->
		<script type="text/javascript" src="../res/js/controls/Ext.ux.TopNewEdit.js"></script>
		<script type="text/javascript" src="../res/js/controls/Ext.ux.TopNewEditField.js"></script>
	</#if>
	<script type="text/javascript" src="../res/js/conflictMgr.js"></script>		<!--冲突管理 -->
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
	postUrl:'../runtime/xform!saveData.jhtml',
	previewUrl:'../runtime/template!preview.jhtml',
	hanler_onload:null,
	jsHanler_b:null,
	onloadJsFunction:{},//注册的事件
	idxNo:0,//当前碎片序号
	redirect:function(id){
		location.href = '../runtime/xform!render.jhtml?viewId='+ formConfig__.viewId +'&formId='+formConfig__.formId+'&id='+ id +'&nodeId=' + formConfig__.nodeId;
	}
}
//表单渲染
RunTime.Render=function(cfg){
	var frm = cfg.config.form;
	//frm.ui.height = offsetHeight__;
	//frm.ui.style='padding:5px';
	frm.ui.bodyStyle='border:0;';
	frm.ui.bodyBorder = false;
	frm.ui.border =false;
	frm.ui.itemCls="itemStyle";
	frm.ui.title='';
	frm.ui.layout = 'xform';
	frm.ui.autoScroll=true;
	frm.ui.border = false;
	frm.ui.frame = false;
	
	var formPanel = new Ext.form.FormPanel(frm.ui);
	if(frm.script && frm.script.s_beforeJs){
		RunTime.jsHanler_b = eval('0,'+frm.script.s_beforeJs);//兼容所有
	}
	if(frm.script && frm.script.s_afterJs){
		RunTime.hanler_onload = eval('0,'+frm.script.s_afterJs);//兼容所有
	}
	if(frm.script && frm.script.s_onloadJs){
		try{
			eval('RunTime.onloadJsFunction={' + frm.script.s_onloadJs + '}');
		}catch(e){
			;
		}
	}
	formPanel = new Ext.form.FormPanel(frm.ui);
	
	if(frm.script && frm.script.s_beforeJs){//保存前脚本
		hanler_b = eval('0,'+frm.script.s_beforeJs);
	}
	if(frm.script && frm.script.s_savedJs){//保存后脚本
		hanler_a = eval('0,'+frm.script.s_savedJs);
	}

	if(frm.script && frm.script.s_afterJs){//保存后脚本
		RunTime.hanler_onload = eval('0,'+frm.script.s_afterJs);
	}

	if(frm.script && frm.script.s_onloadJs){
		try{
			eval('onloadJsFunction={' + frm.script.s_onloadJs + '}');
		}catch(e){
			;
		}
	}		

	createUI(frm.controls,formPanel);
	RunTime.formPanel=formPanel;
	return formPanel;
}

RunTime.message = {
	onMessage:function(e){
		var dataStr = e.data;
		data = Ext.util.JSON.decode(dataStr);
		if(data.action=='submit'){
			RunTime.message.collectFormData(dataStr);
		}else if(data.action=='init'){
			//初始化值
			if(data.params && data.params.params){
				RunTime.formPanel.form.setValues(data.params.params);
			}
		}
	},
	//搜集数据并发送
	collectFormData:function(realData){
		var cfg = formConfig__;
		var msgJson={
			action:'submit',
			data:{
				formId:cfg.formId,
				id:cfg.id,
				params:RunTime.formPanel.form.getValues()
			},
			realData:realData//发出指令的页面传过来的原始值
		};

		window.parent.postMessage(Ext.util.JSON.encode(msgJson), '*'); 
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
			border:false,
			frame:false,
			
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
	
	//读取历史记忆
	MEMHelper.read();
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
	