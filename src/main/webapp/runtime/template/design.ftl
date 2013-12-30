<script>
	window.document.title = "模板加载中...";
</script>
${content!""}

<!-- ///////////////////////inspector and templateEditor/////////////////////// -->
<link rel="stylesheet" type="text/css" href="template/res/inspector.css?20130828" />
<link rel="stylesheet" type="text/css" href="./../res/js/ext2/resources/css/ext-all.css" />
<link rel="stylesheet" type="text/css" href="./../res/js/ext2/resources/css/patch.css" />
<script type="text/javascript" src="./../res/js/ext2/adapter/ext/ext-base.js"></script>
<script type="text/javascript" src="./../res/js/ext2/ext-all-debug.js"></script>
<script type="text/javascript" src="./../res/js/ext_base_extension.js"></script>
<script type="text/javascript" src="./../res/js/controls/Ext.ux.RadioGroup.js"></script>  

<script type="text/javascript" src="template/res/inspector.js"></script>
<script type="text/javascript" src="template/res/TPE.js?20130828"></script>

<script type="text/javascript">
	var params__ = Ext.parseQuery();
	var nodeId__ = params__.nodeId;
	//提交操作指令
	submitCommands = function(cmd){
		var params = params__;
		params.cmd = cmd;
		Ext.getBody().mask("正在提交.....");
		Ext.Ajax.request({  
			url:'template!build.jhtml',
			method:'post',	
			params:params,
			success:function(response,opts){
				Ext.getBody().unmask();
				var ret = Ext.util.JSON.decode(response.responseText);
				if(ret.success==false){
					Ext.Msg.show({
						   title:'错误提示',
						   msg: decodeURIComponent(ret.message?ret.message:ret.statusText),
						   buttons: Ext.Msg.OK,
						   animEl: 'elId',
						   minWidth:420,
						   icon: Ext.MessageBox.ERROR 
					});
				}else{
					TPE.console.submitResponsehandler(ret.actionData);
				}

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
	}
	
	
Ext.onReady(function(){
	Ext.BLANK_IMAGE_URL = "./../res/js/ext2/resources/images/default/s.gif";
	Ext.QuickTips.init();
	Ext.form.Field.prototype.msgTarget = 'qtip';
    FBL.initialize();
    TPE.initialize(); 
	
	//监听postMessage消息事件
	if (typeof window.addEventListener != 'undefined') {
		window.addEventListener('message', TPE.message.onMessage, false);
	} else if (typeof window.attachEvent != 'undefined') {
		window.attachEvent('onmessage', TPE.message.onMessage);
	}
	document.title = '【模板可视化设计】';
});


</script>