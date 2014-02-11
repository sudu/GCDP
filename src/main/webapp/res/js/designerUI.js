Designer.toolbarCfg=[{
	xtype: 'tbbutton',
	text: '新建',
	//minWidth :80,
	handler:function(){
		Ext.Msg.show({
		   title:'操作提示',
		   msg: '您确定放弃当前表单吗?',
		   buttons: Ext.Msg.YESNO,
		   fn:function(button,text){   
				if(button=='yes'){
					Designer.newForm();
				}
			},
		   animEl: 'elId',
		   icon: Ext.MessageBox.QUESTION
		});
	}
},
{
	xtype: 'tbbutton',
	text: '通过JSON导入',
	//minWidth :60,
	id:'btnFormOpen',
	handler:function(){
		if(Designer.openWindow){
			Designer.openWindow.show();
		}else{
			var form_win = new Ext.Window({
				title:'导入配置',
				layout:"auto",
				modal:true,
				id:"openFormWin",
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
					text:'确定',
					width:100,
					handler:function(){
						var configStr = Designer.openWindow.findById('openConfigJson').getValue();	
						try{
							var config = Ext.util.JSON.decode(configStr);
							Designer.initForm(config.form);
							Designer.openWindow.hide('btnFormOpen');
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
					xtype:'button',
					text:'取消',
					width:100,
					handler:function(){
						Designer.openWindow.hide('btnFormOpen');
						
					}
					
				}]
			});
			var txt = new Ext.form.TextArea({
				width:620,
				height:440,
				anchor:'100%',
				id:'openConfigJson',
				emptyText:'请复制您之前保存的表单配置内容并粘贴到这里',
				value:""
			});
			form_win.add(txt);
			form_win.setAnimateTarget('btnFormOpen');//动画效果
			form_win.show();
			Designer.openWindow = form_win;
		}
		
	}
},/*{
	xtype: 'tbbutton',
	id:'btnFormPreview',
	text: '预览',
	handler:function(){
		Designer.preview();
	}
},*/
{
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
						Designer.saveFormCfg();
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
	//minWidth :60,
	handler:Designer.btnFormSave2Click = function(){	
		var formConfig = Designer.formatFormCfg();
		Designer.saveFormCfg(Ext.util.JSON.encode(formConfig));
	}
}


];
//编辑器界面基本布局					
Designer.mainUI = Ext.extend(Ext.Viewport, {
	header:false,
	//height:offsetHeight__,
	layout: 'border',
	//renderTo: "placeholder",
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
					layout:'fit',
					//bodyStyle:"overflow:scroll"
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
