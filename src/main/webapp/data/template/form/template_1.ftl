 <!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <title>表单视图</title>
	<link rel="stylesheet" type="text/css" href="../res/js/ext2/resources/css/ext-all.css" />
	<link rel="stylesheet" type="text/css" href="../res/js/ext2/resources/css/patch.css" />
	<link rel="stylesheet" type="text/css" href="../res/css/runTime.css?201300910" />
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
		
	    .itemStyle {
		    padding:8px;
		    /*border: 1px solid silver;*/
		    margin-bottom:0;
		}
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
    <script type="text/javascript" src="../res/js/ext2/ext-all-debug.js?20130910"></script>
	<script type="text/javascript" src="../res/js/ext2/ext-lang-zh_CN.js?v=20121210"></script>
	<script type="text/javascript" src="../res/js/ext_base_extension.js?20130517"></script>  
	<script type="text/javascript" src="../res/js/config/commonUI4RT_cfg.js?20130516"></script>  
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
		<script type="text/javascript" src="../res/js/controls/Ext.ux.CheckboxGroup.js"></script>  
		<script type="text/javascript" src="../res/js/controls/Ext.ux.DateTime.js"></script>  
		<script type="text/javascript" src="../res/js/controls/Ext.ux.FieldSet.js"></script>  
		<script type="text/javascript" src="../res/js/controls/Ext.ux.HtmlEditor.js"></script>  
		<script type="text/javascript" src="../res/js/controls/Ext.ux.ImgUploader1.js"></script>  
		<script type="text/javascript" src="../res/js/controls/Ext.ux.ImgUploader2.js"></script>  
		<script type="text/javascript" src="../res/js/controls/Ext.ux.RadioGroup.js"></script>  
		<script type="text/javascript" src="../res/js/controls/Ext.ux.RuledText.js"></script>  
		<script type="text/javascript" src="../res/js/controls/Ext.ux.TitleField.js"></script> 	
		<script type="text/javascript" src="../res/js/controls/Ext.ux.SelectableTextbox.js"></script>  
		<script type="text/javascript" src="../res/js/controls/Ext.ux.SimpleUploader.js"></script>  
		<script type="text/javascript" src="../res/js/controls/Ext.ux.Uploader.js"></script>  
		<script type="text/javascript" src="../res/js/controls/Ext.ux.SelectorField.js"></script>  
		<script type="text/javascript" src="../res/js/controls/Ext.ux.Sort.js"></script> 
		<script type="text/javascript" src="../res/js/swfupload.js"></script> 	 		
		<script type="text/javascript" src="../res/js/controls/Ext.ux.TuiJianWei.js?20120829"></script>  	
		<script type="text/javascript" src="../res/js/controls/Ext.ux.UploadField.js?20120815"></script>  
		<script type="text/javascript" src="../res/js/controls/Ext.ux.LinkageComboBox.js"></script>
		<script type="text/javascript" src="../res/js/controls/Ext.ux.LinkageFieldSet.js"></script>
		<script type="text/javascript" src="../res/js/controls/Ext.ux.LinkageFieldText.js"></script>
		<script type="text/javascript" src="../res/js/controls/html5_upload_base.js"></script>
		<script type="text/javascript" src="../res/js/controls/Ext.ux.UploadDev.js"></script>
		<!--头条新闻管理-->
		<script type="text/javascript" src="../res/js/controls/Ext.ux.TopNewEdit.js"></script>
		<script type="text/javascript" src="../res/js/controls/Ext.ux.TopNewEditField.js"></script>
		<script type="text/javascript" src="../res/js/controls/Ext.ux.TextArea2.js"></script>  
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
			recordData:decodeURIComponent("${(recordData!"{}")?url('UTF-8')}"),
			dataSource:${dataSource!"{}"}			
		};
		formConfig__.recordData = formConfig__.recordData?Ext.decode(formConfig__.recordData):{};
		
		function closePage(){
			//优先判断是否该页面是否包含在弹出的window里，若是则关闭该window
			try{
				var parentMsg = Ext.parseQuery().postMessage__;
				if(parentMsg){
					var parentMsgJson = Ext.decode(decodeURIComponent(parentMsg));
					if(parentMsgJson.containerId){
						parentMsgJson.handler = '(function(receiveData,realData){\
							if(typeof receiveData==="string"){\
								receiveData = Ext.decode(decodeURIComponent(receiveData));\
							}\
							eval("var win =" + receiveData.containerId);\
							if(typeof win==="string") win = Ext.getCmp(win);\
							win.el.fadeOut({\
								endOpacity: 0, \
								easing: "easeOut",\
								duration: .3,\
								useDisplay: false,\
								callback:function(){\
									win.close();\
								}\
							});	\
						})';
						
						var postData = {
							data:{containerId:parentMsgJson.containerId},
							options: encodeURIComponent(Ext.encode(parentMsgJson)) //从URL接收到的数据
						};
						var sender;
						if(parentMsgJson.sender){
							eval('0,' + 'sender = ' + decodeURIComponent(parentMsgJson.sender));
						}else if(!window.opener && parentMsgJson.hostFrameId){//Ext的tabPanel页签之间的通讯
							sender = window.parent.Ext.get(parentMsgJson.hostFrameId).dom.contentWindow;
						}else{
							sender = window.opener;
						}
						sender && sender.postMessage(Ext.encode(postData), '*'); 
						return ;
					}
				}
				
			}catch(ex){}

			if(top.centerTabPanel){
				if(conflictMgr && formConfig__.id>0)conflictMgr.remove();//移除正在编辑状态
				var tab = top.centerTabPanel.getActiveTab();
				top.centerTabPanel.remove(tab);
			}else{
				top.open('','_self','');
				top.close();
			}
		}	

	ajaxRequest=function(url,params,callback,options){
		if(url.indexOf('?')==-1){
			url+='?t=' + (new Date()).valueOf()
		}else{
			url+='&t=' + (new Date()).valueOf()
		}
		Ext.getBody().mask("正在请求数据,请稍候...");
		Ext.Ajax.request({  
			url:url,
			method:"post",
			params:params,
			options:{callback:callback,options:options},
			success:function(response,opts){
				Ext.getBody().unmask();
				var ret=null;
				try{
					ret = Ext.util.JSON.decode(response.responseText);
				}catch(ex){
					console.log(ex);
				}
				if(!ret){
					Ext.CMPP.warn("错误提示","出现异常.");
				}else{
					if(typeof(opts.options.callback)=='function') opts.options.callback(ret,opts.options.options);
				}
			},
			failure:function(response,opts){
				Ext.getBody().unmask();
				Ext.CMPP.warn("错误提示",decodeURIComponent(response.statusText));
			}
		});
	}
	
		
	</script>

	${headInject!""}
</head>
<body>	
<script src="../data/template/form/FormRenderPackage.js??20130909"></script>

<script type="text/javascript">
var hanler_b,hanler_a;
var onloadJsFunction={};
var formPanel;
RunTime = {
	postUrl:'../runtime/xform!saveData.jhtml',
	hanler_onload:null,
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
	//frm.ui.style='padding:10px';
	frm.ui.bodyStyle='padding:10px';
	frm.ui.itemCls="itemStyle";
	frm.ui.title='';
	frm.ui.autoScroll=true;
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

	if(frm.buttons.b_save){
		formPanel.addButton({
			text:'保存',
			id:'btn_b_save',
			type:'button',
			handler:saveHandler()
		});	
	}
	if(frm.buttons.b_saveAndAdd){
		formPanel.addButton({
			text:'保存并继续添加',
			id:'btn_b_saveAndAdd',
			type:'button',
			handler:saveHandler(null,true)
		});	
	}	
	if(frm.buttons.b_saveAndClose){
		formPanel.addButton({
			text:'保存并关闭',
			id:'btn_b_saveAndClose',
			type:'button',
			handler:saveHandler(closePage)
		});	
	}	
	if(frm.buttons.b_preview){
		formPanel.addButton({
			text:'预览',
			id:'btn_b_preview',
			type:'button',
			handler:function(){
				if(!RunTime.formPanel.form.isValid()){
					Ext.Toast.show('输入未完成或验证不通过',{
					   title:'提示',
					   buttons: Ext.Msg.OK,
					   animEl: 'elId',
					   minWidth:420,
					   icon: Ext.MessageBox.WARNING
					});
					return;
				}
				var ret = true;
				if(typeof(hanler_b)=='function'){//执行保存前脚本
					ret = hanler_b();
				}
				if(ret!=false){
					var formValues = RunTime.formPanel.form.getFieldValues(false);
					var cfg = formConfig__;
					var params=Ext.apply({dataFormId:cfg.formId,dataId:cfg.id,nodeId:cfg.nodeId,format:'json'},formValues);
					ajaxRequest('../runtime/template!preview.jhtml',params,function(ret,opts){
						var previwWin =window.open();
						previwWin.document.write(decodeURIComponent(ret.message));
					});					
				}
			}
		});	
	}
	if(frm.buttons.b_close){	
		formPanel.addButton({
			text:'关闭',
			id:'btn_b_close',
			type:'button',
			handler:closePage
			
		});	
	}	

	createUI(frm.controls,formPanel);

	return formPanel;
}

var Viewport;
Ext.onReady(function(){
	Ext.BLANK_IMAGE_URL = "../res/js/ext2/resources/images/default/s.gif";
	//初始化信息提示功能
	Ext.QuickTips.init();
	//统一指定错误信息提示浮动显示方式
	Ext.form.Field.prototype.msgTarget = 'under';//'qtip';

	RunTime.formPanel= RunTime.Render(formConfig__);
	
	Viewport = new Ext.Viewport({
		items:RunTime.formPanel,
		layout:"fit"
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
		//编辑冲突检查
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
				eval('0,' + hanlder + '.call(' + scope + ',"'+ encodeURIComponent(Ext.encode(dataJson.data)) + '","'+ encodeURIComponent(Ext.encode(options.data)) +'")');
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
	