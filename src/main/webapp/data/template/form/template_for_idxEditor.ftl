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
		a{color:#004A99;}
		a:hover{color:#BA2636;}
		
	    .itemStyle5 {
		    padding:5px;
		    /*border: 1px solid silver;*/
		    margin-bottom:0;
		}
		.fieldsetStyle{
			margin:4px 10px 4px 10px
		}
		
		textarea {resize:auto;overflow-x:hidden;}
		.buttonMini {
			cursor:pointer;
		   border-top: 1px solid #96d1f8;
		   background: #65a9d7;
		   background: -webkit-gradient(linear, left top, left bottom, from(#3e779d), to(#65a9d7));
		   background: -webkit-linear-gradient(top, #3e779d, #65a9d7);
		   background: -moz-linear-gradient(top, #3e779d, #65a9d7);
		   background: -ms-linear-gradient(top, #3e779d, #65a9d7);
		   background: -o-linear-gradient(top, #3e779d, #65a9d7);
		   padding: 2px 4px;
		   -webkit-border-radius: 14px;
		   -moz-border-radius: 14px;
		   border-radius: 14px;
		   -webkit-box-shadow: rgba(0,0,0,1) 0 1px 0;
		   -moz-box-shadow: rgba(0,0,0,1) 0 1px 0;
		   box-shadow: rgba(0,0,0,1) 0 1px 0;
		   text-shadow: rgba(0,0,0,.4) 0 1px 0;
		   color: white;
		   font-size: 10px;
		   font-family: Georgia, serif;
		   text-decoration: none;
		   vertical-align: middle;
		}
		.buttonMini:hover {
			cursor:pointer;
		   border-top-color: #28597a;
		   background: #28597a;
		   color: #ccc;
		}
		.buttonMini:active {
		   border-top-color: #1b435e;
		   background: #1b435e;
		}	
		/**列表页样式**/
		.x-grid3-cell-inner, .x-grid3-hd-inner {
			overflow: hidden;
			padding: 4px 3px 4px 5px;
			text-overflow: ellipsis;/*ellipsis clip*/
			white-space: nowrap;
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
		<script type="text/javascript" src="../res/js/CookiesHelper.js"></script>
		<script type="text/javascript" src="../res/js/controls/Ext.ux.TextField.js"></script> 
		<script type="text/javascript" src="../res/js/controls/Ext.ux.TitleField.js"></script> 
		<script type="text/javascript" src="../res/js/controls/Ext.ux.CheckboxGroup.js"></script>  
		<script type="text/javascript" src="../res/js/controls/Ext.ux.DateTime.js"></script>  
		<script type="text/javascript" src="../res/js/controls/Ext.ux.FieldSet.js"></script>  
		<script type="text/javascript" src="../res/js/controls/Ext.ux.RadioGroup.js"></script>  
		<script type="text/javascript" src="../res/js/controls/Ext.ux.Sort.js"></script>   	
		<script type="text/javascript" src="../res/js/swfupload.js"></script> 	 		
		<script type="text/javascript" src="../res/js/controls/Ext.ux.TuiJianWei.js?20120829"></script>  
		<script type="text/javascript" src="../res/js/controls/Ext.ux.UploadField.js?20120815"></script> 
		<script type="text/javascript" src="../res/js/controls/Ext.ux.TextArea2.js"></script>  	
		<script type="text/javascript" src="../res/js/swfupload.js"></script>  
		<!--关联combo-->
		<script type="text/javascript" src="../res/js/controls/Ext.ux.LinkageComboBox.js"></script>
		<script type="text/javascript" src="../res/js/controls/Ext.ux.LinkageFieldSet.js"></script>
		<script type="text/javascript" src="../res/js/controls/Ext.ux.LinkageFieldText.js"></script>
		<!--html5上传-->
		<script type="text/javascript" src="../res/js/controls/html5_upload_base.js"></script>
		<script type="text/javascript" src="../res/js/controls/Ext.ux.UploadDev.js"></script>
		<!--头条新闻管理-->
		<script type="text/javascript" src="../res/js/controls/Ext.ux.TopNewEdit.js"></script>
		<script type="text/javascript" src="../res/js/controls/Ext.ux.TopNewEditField.js"></script>
	</#if>
	<script type="text/javascript" src="../res/js/conflictMgr.js?20120507"></script>		<!--冲突管理 -->
	<script type="text/javascript" src="../res/js/hisListMgr.js"></script>		<!--历史记录管理 -->
	<script type="text/javascript">
		//吐出的数据
		var formConfig__ = {
			id:#{id!0},
			formId:#{formId!0},
			viewId:#{viewId!0},
			nodeId:#{nodeId!0},
			config: ${config!0},
			args:Ext.parseQuery().args,
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
		var params = Ext.parseQuery();
		params.id = id;
		location.href = '../runtime/xform!render.jhtml?' + Ext.urlEncode(params);
	}
}
//表单渲染
RunTime.Render=function(cfg){
	var frm = cfg.config.form;
	//frm.ui.height = offsetHeight__;
	//frm.ui.style='padding:5px';
	frm.ui.bodyStyle='padding:0px;border:0';
	frm.ui.itemCls="itemStyle";
	frm.ui.title='';
	frm.ui.layout = 'xform';
	if(formConfig__.id>0) frm.ui.bbar=[{
		xtype:'textfield',
		style:'border:1px inset #A9BFD3;background:transparent;cursor:default;',
		readOnly:true,
		width:250,
		id:'editorInfoBox'
	},{
		xtype:'tbfill'
	}];
	frm.ui.autoScroll=true;
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
//验证
//callerName:请求验证的函数名
RunTime.validor = function(callerName){
	var ret = true;
	if(typeof(RunTime.jsHanler_b)=='function'){//执行保存前脚本
		ret = RunTime.jsHanler_b(callerName);
	}
	if(ret==false){
		return false;
	}
	if(!RunTime.formPanel.form.isValid()){
		Ext.Toast.show('输入未完成或验证不通过',{
		   title:'提示',
		   buttons: Ext.Msg.OK,
		   animEl: 'elId',
		   minWidth:420,
		   icon: Ext.MessageBox.WARNING
		});
		return false;
	}
	return true;
};
RunTime.save = function(){
	var cfg = formConfig__;
	var params={formId:cfg.formId,viewId:cfg.viewId,id:cfg.id,nodeId:cfg.nodeId};
	params.args = formConfig__.args;
	RunTime.formPanel.form.submit({  
		waitTitle : "请稍候",  
		waitMsg : "正在提交数据，请稍候......",  
		url : RunTime.postUrl,  
		params:params,
		method : "POST",  
		success : function(form, action) {  
			var cfg = formConfig__;
			if(cfg.id!=0){
				//获取新内容以提供预览
				var params={dataFormId:cfg.formId,dataId:cfg.id,nodeId:cfg.nodeId,format:'json'};
				params.args = formConfig__.args;
				delete params[id];
				Ext.getBody().mask("正在生成预览效果...");
				Ext.Ajax.request({  
					url:RunTime.previewUrl,  
					method:'post',	
					params:params,
					success:function(response,opts){
						Ext.getBody().unmask();
						var html = Ext.decode(response.responseText).message;
						var msgJson = {
							action:'save',
							sender:'frame1',
							idxNo:RunTime.idxNo,
							msg:html
						};
						window.parent.postMessage(Ext.util.JSON.encode(msgJson), '*'); 			
					},
					failure:function(ret,opts){
						Ext.getBody().unmask();
						Ext.Msg.show({
							title:'错误提示',
							msg: decodeURIComponent(ret.message?ret.message:ret.statusText),
							buttons: Ext.Msg.OK,
							animEl: 'elId',
							minWidth:420,
							icon: Ext.MessageBox.ERROR 
						});
					}
				});
			}else{
				//var ret = Ext.util.JSON.decode(action.response.responseText);
				
				Ext.Msg.show({
				   title:'提示',
				   msg:  '提交成功！',
				   buttons: Ext.Msg.OK,
				   animEl: 'elId',
				   minWidth:420,
				   icon: Ext.MessageBox.INFO 
				});
				RunTime.redirect(formConfig__.id);
			}								
		},  
		failure : function(form, action) { 
			var msg = action.result?action.result.message:"浏览器端错误";
			Ext.Msg.show({
			   title:'错误提示',
			   msg:  decodeURIComponent(msg),
			   buttons: Ext.Msg.OK,
			   animEl: 'elId',
			   minWidth:420,
			   icon: Ext.MessageBox.ERROR 
			});						
		}  
	}); 
}
RunTime.preview = function(){
	var cfg = formConfig__;					
	var params={dataFormId:cfg.formId,dataId:cfg.id,nodeId:cfg.nodeId,format:'json'};
	params.args = formConfig__.args;
	delete params[id];
	Ext.getBody().mask("正在生成预览效果...");
	RunTime.formPanel.form.submit({  
		waitTitle : "请稍候",  
		//waitMsg : "正在获取预览数据，请稍候......",  
		url : RunTime.previewUrl,  
		params:params,
		method : "POST",  
		success : function(form, action) {  
			Ext.getBody().unmask();
			var msgJson = {
				action:'preview',
				sender:'frame1',
				idxNo:RunTime.idxNo,
				msg:action.result.message
			};
			window.parent.postMessage(Ext.util.JSON.encode(msgJson), '*'); 														
		},  
		failure : function(form, action) { 
			Ext.getBody().unmask();
			var msg = action.result?action.result.message:"浏览器端错误";
			Ext.Msg.show({
			   title:'错误提示',
			   msg:  decodeURIComponent(msg),
			   buttons: Ext.Msg.OK,
			   animEl: 'elId',
			   minWidth:420,
			   icon: Ext.MessageBox.ERROR 
			});						
		}  
	}); 
}
//全页预览
RunTime.wholePreview = function(){
	//先提交本碎片内容并获取该碎片的预览内容，再将预览内容Post到模板预览地址获得全页预览内容
	var cfg = formConfig__;
	//获取新内容以提供预览
	RunTime.formPanel.form.submit({  
		waitTitle : "请稍候",  
		waitMsg : "正在获取预览数据，请稍候......",  
		url : RunTime.previewUrl,  
		params:{dataFormId:cfg.formId,dataId:cfg.id,nodeId:cfg.nodeId,format:'json'},
		method : "POST",  
		success : function(form, action) {  
			var cfg = formConfig__;
			var ret = Ext.util.JSON.decode(action.response.responseText) ;
			var idxContent = ret.message;
			var params = Ext.parseQuery(parentUrl__);
			params.idxData = decodeURIComponent(idxContent);
			params.idxId = RunTime.idxId;
			var url = parentUrl__.replace('template!idxEditor.jhtml','template!preview.jhtml');
			url = url.split('?')[0];
			Ext.getBody().mask("正在获取预览数据......");
			Ext.Ajax.request({ 
				url:url,  
				method:'post',	
				params:params,
				success:function(response,opts){
					Ext.getBody().unmask()
					var previwWin =window.open();
					previwWin.document.write(response.responseText);
				},
				failure:function(ret,opts){
					Ext.getBody().unmask();
					Ext.Msg.show({
						title:'错误提示',
						msg: decodeURIComponent(ret.message?ret.message:ret.statusText),
						buttons: Ext.Msg.OK,
						animEl: 'elId',
						minWidth:420,
						icon: Ext.MessageBox.ERROR 
					});
				}
			});	
		},  
		failure : function(form, action) { 
			var msg = action.result?action.result.message:"浏览器端错误";
			Ext.Msg.show({
			   title:'错误提示',
			   msg:  decodeURIComponent(msg),
			   buttons: Ext.Msg.OK,
			   animEl: 'elId',
			   minWidth:420,
			   icon: Ext.MessageBox.ERROR 
			});						
		}  
	});
}
Ext.onReady(function(){
	Ext.BLANK_IMAGE_URL = "../res/js/ext2/resources/images/default/s.gif";
	//初始化信息提示功能
	Ext.QuickTips.init();
	//统一指定错误信息提示浮动显示方式
	Ext.form.Field.prototype.msgTarget = 'under';//'qtip';
	
	RunTime.idxNo = Ext.parseQuery()['idxNo'];
	RunTime.idxId = Ext.parseQuery()['idxId'];
	var mainPanel = new Ext.Viewport({
		layout: 'border',
		items:[{
			xtype:'panel',
			region:'center',
			id:'mainPanel',
			layout:'fit',
			buttonAlign:'center',
			items:[RunTime.Render(formConfig__)],
			buttons:[{
				text:'保存',
				id:'btn_b_save',
				type:'button',
				handler:function(){
					if(RunTime.validor("RunTime.save")==false){
						return;
					}
					RunTime.save();
				}
			},{
				text:"预览",
				listeners:{
					click:function(){	
						var cfg = formConfig__;
						var text = this.getText();
						this.setText(text=='预览'?'还原':'预览');
						if(text=='预览'){
							if(RunTime.validor("RunTime.preview")==false){
								return;
							}
							RunTime.preview();
						}else{
							var msgJson = {
								action:text=='预览'?'preview':'unPreview',
								sender:'frame1',
								idxNo:RunTime.idxNo,
								msg:text=='预览'?(content):''
							};
							window.parent.postMessage(Ext.util.JSON.encode(msgJson), '*'); 
						}

					}
				}
			},{
				text:"全页预览",
				listeners:{
					click:function(){
						if(RunTime.validor("RunTime.wholePreview")==false){
							return;
						}
						RunTime.wholePreview();
					}	
				}
			},{
				text:"关闭",
				listeners:{
					click:function(){
						var msgJson = {
							action:'close',
							idxNo:RunTime.idxNo,
							sender:'frame1'
						};
						window.parent.postMessage(Ext.util.JSON.encode(msgJson), '*'); 
					}
				}
			}]
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
	
	if(formConfig__.id>0){
		//历史记录
		hisListMgr.formPanel = RunTime.formPanel;
		hisListMgr.versionKey= formConfig__.nodeId+'_'+formConfig__.formId+'_'+formConfig__.viewId+'_'+formConfig__.id;
		initHistory();
	
		//编辑冲突检查以及注册正在编辑状态
		conflictMgr.init(Ext.getCmp('editorInfoBox'),'editform_' + 'formId' + formConfig__.formId  + '_' + 'viewId' + formConfig__.viewId + '_' + 'id' + formConfig__.id + '_' + 'nodeId' + formConfig__.nodeId);
	}
});

</script>	
<script>		

/*******onMessage处理******/
function onMessageHandler(e){
	var dataStr = e.data;
	try{
		var dataJson = Ext.util.JSON.decode(dataStr);
		if(dataJson.options){
			var options = Ext.decode(decodeURIComponent(dataJson.options));
			var hanlder = options.handler;//处理函数
			var scope = options.scope;
			if(hanlder){
				eval('0,' + hanlder + '.call(' + scope + ',"'+ encodeURIComponent(Ext.encode(dataJson.data)) +'")');
			}
		}
	}catch(ex){
		console.log(ex);
	}
	
}
//监听postMessage消息事件
if (typeof window.addEventListener != 'undefined') {
	window.addEventListener('message', onMessageHandler, false);
} else if (typeof window.attachEvent != 'undefined') {
	window.attachEvent('onmessage', onMessageHandler);
}
</script>	
${bodyInject!""}		
</body>
</html>	
	