 <!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <title>表单视图</title>
	<link rel="stylesheet" type="text/css" href="./../res/js/ext2/resources/css/ext-all.css" />
	<link rel="stylesheet" type="text/css" href="./../res/js/ext2/resources/css/patch.css" />
	<link rel="stylesheet" type="text/css" href="./../res/css/runTime.css" />
	<style>
		html, body {
	        font:normal 12px verdana;
	        margin:0;
	        padding:0;
	        border:0 none;
	        height:100%;
	    }
	    .itemStyle {
		    padding:8px;
		    /*border: 1px solid silver;*/
		    margin-bottom:0;
		}
		.fieldsetStyle{
			margin:4px 10px 4px 10px
		}
	</style>
 	<script type="text/javascript" src="./../res/js/ext2/adapter/ext/ext-base.js"></script>
    <script type="text/javascript" src="./../res/js/ext2/ext-all-debug.js"></script>
	<script type="text/javascript" src="./../res/js/ext2/ext-lang-zh_CN.js"></script>
	<script type="text/javascript" src="./../res/js/ext_base_extension.js"></script>  
	<script type="text/javascript" src="./../res/js/config/commonUI4RT_cfg.js"></script>  
	
	<script type="text/javascript" src="./../res/js/controls/Ext.ux.CheckboxGroup.js"></script>  
	<script type="text/javascript" src="./../res/js/controls/Ext.ux.DateTime.js"></script>  
	<script type="text/javascript" src="./../res/js/controls/Ext.ux.FieldSet.js"></script>  
	<script type="text/javascript" src="./../res/js/controls/Ext.ux.HtmlEditor.js"></script>  
	<script type="text/javascript" src="./../res/js/controls/Ext.ux.ImgUploader.js"></script>  
	<script type="text/javascript" src="./../res/js/controls/Ext.ux.RadioGroup.js"></script>  
	<script type="text/javascript" src="./../res/js/controls/Ext.ux.RuledText.js"></script>  
	<script type="text/javascript" src="./../res/js/controls/Ext.ux.SelectableTextbox.js"></script>  
	<script type="text/javascript" src="./../res/js/controls/Ext.ux.SimpleUploader.js"></script>  
	<script type="text/javascript" src="./../res/js/controls/Ext.ux.Uploader.js"></script>  
	
	
	
	
	<script type="text/javascript">
		//吐出的数据
		var formConfig__ = {
			id:${id},
			formId:${formId},
			viewId:${viewId},
			formData:${formData!""},
			config: ${config},
			viewData:${viewData}			
		};

	</script>

</head>
<body>	
<script type="text/javascript">

RunTime = {
	postUrl:'xform!saveData.jhtml?nodeId='+Ext.parseQuery().nodeId,
	formPanel:null,
	hanler_onload:function(){}//頁面加載后脚本
	
}
//表单渲染
RunTime.Render=function(cfg){
	var frm = cfg.config.form;
	//frm.ui.height = offsetHeight__;
	frm.ui.style='padding:10px';
	frm.ui.bodyStyle='padding:10px';
	frm.ui.itemCls="itemStyle";
	frm.ui.title='';
	frm.ui.autoScroll=true;
	var formPanel = new Ext.form.FormPanel(frm.ui);
	var hanler_b;
	if(frm.script && frm.script.s_beforeJs){
		hanler_b = eval('0,'+frm.script.s_beforeJs);//兼容所有
	}
	if(frm.script && frm.script.s_afterJs){
		RunTime.hanler_onload = eval('0,'+frm.script.s_afterJs);//兼容所有
	}
	var onloadJsFunction={};
	if(frm.script && frm.script.s_onloadJs){
		try{
			eval('onloadJsFunction={' + frm.script.s_onloadJs + '}');
		}catch(e){
			;
		}
	}

	var saveHandler = function(callback){
		var cb = callback;
		var jsHanler_b = hanler_b;
		return function(){
			var ret = true;
			if(ret!=false){
				RunTime.formPanel.form.submit({  
					waitTitle : "请稍候",  
					waitMsg : "正在提交数据，请稍候......",  
					url : formPanel.form.url ? formPanel.form.url:RunTime.postUrl,  
					params:{formId:cfg.formId,viewId:cfg.viewId,id:cfg.id},
					method : "POST",  
					success : function(form, action) {  
						Ext.Msg.show({
						   title:'提示',
						   msg:  '提交成功！',
						   buttons: Ext.Msg.OK,
						   animEl: 'elId',
						   minWidth:420,
						   icon: Ext.MessageBox.INFO 
						});
						if(typeof(cb)=='function') cb();
					},  
					failure : function(form, action) {  
						Ext.Msg.show({
						   title:'错误提示',
						   msg:  decodeURIComponent(action.result.message),
						   buttons: Ext.Msg.OK,
						   animEl: 'elId',
						   minWidth:420,
						   icon: Ext.MessageBox.ERROR 
						});						
					}  
				}); 
			}	
		};
	}	
	
	if(frm.buttons.b_save){
		formPanel.addButton({
			text:'保存',
			id:'btn_b_save',
			type:'button',
			handler:saveHandler()
		});	
	}
	if(frm.buttons.b_saveAndClose){
		formPanel.addButton({
			text:'保存并关闭',
			id:'btn_b_saveAndClose',
			type:'button',
			handler:saveHandler(function(){
				window.close();
			})
		});	
	}	
	if(frm.buttons.b_preview){
		formPanel.addButton({
			text:'预览',
			id:'btn_b_preview',
			type:'button'
			
		});	
	}
	if(frm.buttons.b_reset){	
		formPanel.addButton({
			text:'重填',
			id:'btn_b_reset',
			type:'reset',
			handler:function(){
				RunTime.Render.formPanel.getForm().reset();
			}
			
		});	
	}	
	var getUIType = function(uiName){
		var arr = uiName.split('.');
		if(arr.length>0){
			return arr[arr.length-1];
		}else{
			return '';
		}
	};
	var createUI  = function(ctrlCfg,parentCtrl){
		for(var i=0;i<ctrlCfg.length;i++){
			var item = ctrlCfg[i];
			var uiType = getUIType(item.name);
			var commonUI = Designer.controls.ui[uiType];
			if(commonUI){
				Ext.applyDeep(item.ui,commonUI.runtime.ui);
			}
			if(item.controls){
				item.ui.cls = 'fieldsetStyle';
				eval('var obj = new ' + item.name + '(item.ui)');			
				parentCtrl.add(obj);
				createUI(item.controls,obj);
			}else{
				if(item.data && item.data.ext_dataSource_value){
					item.ui.dataSource = item.data.ext_dataSource_value;
				}
				if(item.f_name){ 
					item.ui.name = 'xform.' + item.f_name;
				}else{
					item.ui.name =null;//不指定name
				}
				var inputType = 'inputType_new';
				if(formConfig__.id==0){//添加状态
					inputType = 'inputType_new';
				}else{//编辑状态
					inputType = 'inputType_edit';
				}
				var obj=null;
				if(item.mod[inputType]=="3"){
					item.ui.readOnly=true;
					eval('obj = new ' + item.name + '(item.ui)');
					if(item.f_name)
						obj.setValue(cfg.formData[item.f_name]);			
					parentCtrl.add(obj);
				}else if(item.mod[inputType]=="2"){
					obj = new Ext.form.Hidden({ 
						value:item.ui.value,
						name:'xform.' + item.f_name
					});
					if(item.f_name)
						obj.setValue(cfg.formData[item.f_name]);			
					parentCtrl.add(obj);
				}else if(item.mod[inputType]=="1" || item.mod[inputType]==undefined  || item.mod[inputType]==''){
					eval('obj = new ' + item.name + '(item.ui)');
					if(item.f_name)
						obj.setValue(cfg.formData[item.f_name]);
					parentCtrl.add(obj);
				}
				if(obj && item.events){
					for(var k in item.events){
						if(onloadJsFunction[item.events[k]])
							obj.on(k,onloadJsFunction[item.events[k]]) ;
					}
				}
			}
		}
	};
	createUI(frm.controls,formPanel);

	new Ext.Viewport({items:formPanel,layout:"fit"});
	
	return formPanel;
}

//var offsetHeight__ = document.getElementById('placeholder').offsetHeight;
//	offsetHeight__-=30;
Ext.onReady(function(){
	Ext.BLANK_IMAGE_URL = "./../res/js/ext2/resources/images/default/s.gif";
	//初始化信息提示功能
	Ext.QuickTips.init();
	//统一指定错误信息提示浮动显示方式
	Ext.form.Field.prototype.msgTarget = 'side';

	RunTime.formPanel= RunTime.Render(formConfig__);
	
	//运行onload脚本
	try{
		if(typeof(RunTime.hanler_onload)=='function'){
			RunTime.hanler_onload();
		}
	}catch(ex){
		
	}
	
});

</script>	
	
</body>
</html>	
	