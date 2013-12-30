<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <title>视图管理</title>
	<link rel="stylesheet" type="text/css" href="../res/js/ext2/resources/css/ext-all.css" />
	<link rel="stylesheet" type="text/css" href="../res/js/ext2/resources/css/patch.css" />
	<link rel="stylesheet" type="text/css" href="../res/css/designTime.css" />
	<script type="text/javascript" src="../res/js/ext2/adapter/ext/ext-base.js"></script>
    <script type="text/javascript" src="../res/js/ext2/ext-all-debug.js"></script>
	<script type="text/javascript" src="../res/js/ext2/ext-lang-zh_CN.js"></script>    
	<script type="text/javascript" src="../res/js/ext_base_extension.js"></script>  
	<script type="text/javascript" src="../res/js/ext_vtypes.js"></script>  
	<script type="text/javascript" src="../res/js/designTimeClass.js"></script>  
	<script type="text/javascript" src="../res/js/controls/Ext.ux.TitleField.js"></script> 
	<script type="text/javascript" src="../res/js/controls/Ext.ux.grid.GroupPropertyGrid.js"></script>
	<script type="text/javascript" src="../res/js/controls/Ext.ux.Sort.js"></script>
	<script type="text/javascript" src="../res/js/controls/Ext.ux.ControlSort.js"></script>
	<script type="text/javascript" src="../res/js/editors/Ext.ux.CustomEditors.TextArea.js"></script>
	<script type="text/javascript" src="../res/js/editors/Ext.ux.CustomEditors.ArrayEditor.js"></script>
	<script type="text/javascript" src="../res/js/editors/Ext.ux.CustomEditors.KeyValueEditor.js"></script>
	<script type="text/javascript" src="../res/js/editors/Ext.ux.ListItemEditor.js"></script>
	<script type="text/javascript" src="../res/js/editors/Ext.createRecordsField.js"></script>
	<script type="text/javascript" src="../res/js/editors/Ext.createWinEditor.js"></script>
	<script type="text/javascript" src="../res/js/editors/Ext.ux.DimenEditor.js"></script>
	<script type="text/javascript" src="../res/js/editors/Ext.ux.ArrayEditor.js"></script>
	<script type="text/javascript" src="../res/js/editors/Ext.ux.JsonEditor.js"></script>
	<script type="text/javascript" src="../res/js/controls/Ext.ux.CheckboxGroup.js"></script>
	<script type="text/javascript" src="../res/js/editors/Ext.ux.CheckGroupEditor.js"></script>
	<script type="text/javascript" src="../res/js/controls/Ext.ux.LinkageComboBox.js"></script>
	<script type="text/javascript" src="../res/js/controls/Ext.ux.LinkageFieldSet.js"></script>
	<script type="text/javascript" src="../res/js/controls/Ext.ux.LinkageFieldText.js"></script>	
	<script type="text/javascript" src="../res/js/controls/Ext.ux.TopNewEditField.js"></script>
	<script type="text/javascript" src="../res/js/controls/Ext.ux.TextArea2.js"></script> 	
	<script type="text/javascript" src="../res/js/controls/Ext.ux.HTMLField.js"></script> 	
	
	<script>
		var formId__ = ${formId};
		var viewId__ = ${viewId};
		var nodeId__ = ${nodeId};
		var viewConfig__ = {
			formView:${viewConfig?replace("/","\\/")},
			fields:${fields}
		};
	</script>	
		
	<script type="text/javascript">
		Ext.BLANK_IMAGE_URL = "../res/js/ext2/resources/images/default/s.gif";
		Ext.namespace('Designer');
		Ext.namespace('Designer.controls');
		Ext.namespace('Designer.controls.ui');	
	</script>	

	<script type="text/javascript">
		//首字母大写：  
		function UpperFirstLetter(str)  
		{  
			return str.replace(/\b\w+\b/g, function(word) {  
								   return word.substring(0,1).toUpperCase( ) +  
										  word.substring(1);  
								 });  
		  
		}  
	</script>
	<script>
		function closePage(){
			if(top && top.centerTabPanel){
				top.centerTabPanel.remove(top.centerTabPanel.getActiveTab());
			}else{
				window.close();
			}
		}
	</script>
</head>
<body>	
	
<script src="../res/js/designer_v2.0.js?20120830" type="text/javascript"></script>
<script src="../res/js/config/formTemplate.js?20120515" type="text/javascript"></script>
<script src="../res/js/config/controlsUI_cfg.js?20120515" type="text/javascript"></script>

<script type="text/javascript">
//如果上一个页面传过来默认模板参数,则在此设置
var __param = Ext.parseQuery();
if(__param.templateValue !== 'undefined' && Ext.util.Format.trim(__param.templateValue) != ''){
	Designer.controls.ui.FormPanel.property.template.value.defaultTpl.value = __param.templateValue;
}
//console.log("=======__param:"+__param);

Designer.saveViewCfg = function(configStr){
	Ext.getBody().mask("正在提交,请稍候......");
	var params={formId:formId__,viewId:viewId__};
	var url = "xform!saveViewConfig.jhtml";
	if(typeof(configStr)=='undefined'){
		params.config = Ext.getCmp('formConfigJson').getValue();
	}else{
		params.config = configStr
	}
	Ext.Ajax.request({  
		url:url,  
		params:params,  
		method:"POST",  
		options:{},
		success:function(response,opts){
			Ext.getBody().unmask();
			var ret = Ext.util.JSON.decode(response.responseText);
			if(!ret.success){
				Ext.Msg.show({
				   title:'错误提示',
				   msg: decodeURIComponent(ret.message),
				   buttons: Ext.Msg.OK,
				   animEl: 'elId',
				   minWidth:420,
				   icon: Ext.MessageBox.ERROR 
				});
			}else{
				Ext.Toast.show('提交成功',{
				   title:'提示',
				   buttons: Ext.Msg.OK,
				   animEl: 'elId',
				   minWidth:420,
				   icon: Ext.MessageBox.INFO  
				});
				var previewFormWin = Ext.getCmp('previewFormWin');
				if(previewFormWin) previewFormWin.hide();
				if(viewId__==0)
				{
					if(top.devMgr)
						top.devMgr.initXFormPanel();
					else if(opener && opener.devMgr)
						opener.devMgr.initXFormPanel();
				}
				//刷新当前页面
				var redirectUrl = "xform!viewManage.jhtml?nodeId="+ nodeId__ +"&formId="+formId__+"&viewId=" + ret.viewId;	
				if(top && top.centerTabPanel && window.frameElement){
					var id = window.frameElement.parentElement.parentElement.parentElement.id;
					var title = Ext.getCmp("FormPanel_1").title||"";
					top.reloadTab(id,redirectUrl,"设计视图-" + title); 
				}else{
					location.href = redirectUrl;
				}
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

Designer.toolbarCfg=[{
	xtype: 'tbbutton',
	text:'通过JSON导入',
	id:'btnFormImport',
	handler:function(){
		var win =new Ext.Window({
			title:"导入JSON",
			closable: true,
			//closeAction: "hide",
			modal: true,
			buttonAlign: "center",
			resizable: true,
			layout: "fit", 
			width:645,
			height :500,
			maximizable:true,
			layout:'fit',
			items:[{
				xtype:'textarea',
				emptyText :'在这里粘贴你曾经导出的JSON'
			}],
			buttons:[{
				text:'确定',
				handler:function(obj){
					try{
						var win = this.ownerCt;
						var configStr = win.items.items[0].getValue();
						var config = Ext.util.JSON.decode(configStr);
						Designer.initForm(config.form);
						win.close();
					}catch(ex){
						Ext.Msg.show({
							   title:'错误提示',
							   msg: '格式有误!<br />' + ex.toString(),
							   buttons: Ext.Msg.OK,
							   animEl: 'elId',
							   minWidth:420,
							   icon: Ext.MessageBox.ERROR 
						});
						return false;
					}
				}
			},{
				text:'取消',
				handler:function(){
					var win = this.ownerCt;
					win.close();
				}
			}]
			
		});
		win.setAnimateTarget('btnFormImport');
		win.show();
	}	
},{
	xtype: 'tbbutton',
	id:'btnFormSave',
	text: '导出为JSON',
	//minWidth :60,
	handler:function(){					
		//var formConfig = Designer.getAllControls();
		var formConfig = Designer.formatFormCfg();
		if(Designer.saveWindow){
			Designer.saveWindow.show();
			Designer.saveWindow.findById('formConfigJson').setValue(Ext.CMPP.JsonUti.toString(formConfig));
		}else{
			var form_win = new Ext.Window({
				title:'保存表单',
				layout:"auto",
				modal:true,
				id:"previewFormWin",
				closeAction:'hide',
				maximizable:true,
				width:640,
				height:500,
				buttonAlign: "center",
				layout:'fit',
				autoScroll:true,
				padding:"10px",
				buttons:[{
					xtype:'button',
					text:'保存',
					width:100,
					handler:function(){
						Designer.saveViewCfg();
					}
				},{
					xtype:'button',
					text:'取消',
					width:100,
					handler:function(){
						Designer.saveWindow.hide('btnFormSave');
						
					}
					
				}]
			});
			var txt = new Ext.form.TextArea({
				width:620,
				height:440,
				anchor:'100%',
				id:'formConfigJson',
				value:Ext.CMPP.JsonUti.toString(formConfig)
			});
			form_win.add(txt);
			form_win.setAnimateTarget('btnFormSave');//动画效果
			form_win.show();
			Designer.saveWindow = form_win;
		}
	}
},{
	xtype: 'tbbutton',
	id:'btnFormSave2',
	text: '保存(S)',
	tooltip :'支持快捷键Ctrl+S',
	//minWidth :60,
	handler:Designer.btnFormSave2Click = function(){	
		var formConfig = Designer.formatFormCfg();
		Designer.saveViewCfg(Ext.util.JSON.encode(formConfig));
	}
}];
//编辑器界面基本布局					
Designer.mainUI = Ext.extend(Ext.Viewport, {
	header:false,
	layout: 'border',
	initComponent: function() {
			this.items = [
				{
					title: '',
					xtype: 'toolbar',
					region: 'north',
					id:'toolbar',
					layout: 'border',
					//margins: '5 5 0 5',
					items: Designer.toolbarCfg

				},
				{
					xtype: 'panel',
					title: '控件库',
					region: 'west',
					width: 200,
					split: true,
					collapsible: true,
					autoScroll: true,
					layout:'accordion',
					layoutConfig:{
						animate:true
					},
					id:'controlsBox',
					//margins: '5 0 5 5'
				},
				{
					region : 'center',
					//margins : '5 0 5 0',
					id : 'centerPanel',
					border:false,
					layout:"fit"
				},
				{
					xtype: 'tabpanel',
					region: 'east',
					width: 300,
					collapsible: true,
					split: true,
					activeTab: 0,
					//autoScroll: true,
					items:Designer.CreatePropsGrid(),
					//margins: '5 5 5 0'
				}
			];
			Designer.mainUI.superclass.initComponent.call(this);
		}
	});



(function(){

Designer.controls.baseIcon="../res/js/ext2/resources/images/default/grid/group-by.gif";
var allFields={};
	
var formatFormCfg=Designer.formatFormCfg;
Designer.formatFormCfg=function (oriCfg) {
	var frm=formatFormCfg.call(Designer,oriCfg);
	var cfg=frm.form;
	(function(config){
		var ctrls=config.controls;
		if(ctrls){
			var len=ctrls.length;
			while(len--){
				var c=ctrls[len];
				if(c.db){
					c["f_name"]=c.db["f_name"];	
					delete c.db;
				}
				arguments.callee(c);
			}
		}
	})(cfg);
	return 	frm;
}

var createProps=Designer.objFunction.createProps;
Designer.objFunction.createProps=function (ctrlId,formCfg) {
	var propObj=createProps.call(this,ctrlId,formCfg);
	if(!formCfg&&!propObj.controls){
		propObj.ui["db"].value.f_name.value="";//new control, field set to empty;
	}
	return propObj;
}
var renderHtml=Designer.controls.renderHtml;
Designer.controls.renderHtml=function(){	
	var flds=viewConfig__.fields;
	flds.unshift(["","　"]);
	var len=flds.length;
	/*
	while(len--){
		var subFlds=flds[len];
		subFlds[1]=subFlds[0];		
	}
	*/
	allFields = {"f_name":{value:"",lan:Designer.controls.commonProperty.db.value["f_name"].lan,type:'ComboBox_1',extra:{loadStore:viewConfig__.fields}}};
	Designer.controls.commonProperty["db"]={lan:"字段配置",value:allFields};
	delete Designer.controls.ui.FormPanel.property.listPage;

	return renderHtml.call(this);
}
var _initForm=Designer.initForm;

Designer.initForm=function (fConfig,isEdit) {
	if(fConfig){
		//var ctrls=fConfig.controls;
		//delete fConfig.controls;
		(isEdit?this.preProcCfgForEdit(fConfig):this.preProcCfgForOpen(fConfig));
		if(fConfig.script)
			Designer.setJsFunction(fConfig.script.s_onloadJs);
	}else{
		fConfig=viewConfig__&&viewConfig__.formView&&viewConfig__.formView.form?viewConfig__.formView.form:null;
	}
	_initForm.call(Designer,fConfig,true);
}
})();
</script>



<script type="text/javascript">

Ext.onReady(function(){
	Designer.Init({});
});

</script>		


</body>
</html>