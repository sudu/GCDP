<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <title>列表视图配置</title>
	<link rel="stylesheet" type="text/css" href="../res/js/ext2/resources/css/ext-all.css" />
	<link rel="stylesheet" type="text/css" href="../res/js/ext2/resources/css/patch.css" />
	<link rel="stylesheet" type="text/css" href="../res/css/runTime.css" />
	<link rel="stylesheet" type="text/css" href="../res/css/Ext.ux.VerticalTabPanel.css" />
	
	<script type="text/javascript" src="../res/js/ext2/adapter/ext/ext-base.js"></script>
    <script type="text/javascript" src="../res/js/ext2/ext-all-debug.js"></script>
	<script type="text/javascript" src="../res/js/ext_base_extension.js"></script> 
	<script type="text/javascript" src="../res/js/editors/Ext.ux.CustomEditors.TextArea.js"></script>
	<script type="text/javascript" src="../res/js/controls/Ext.ux.CheckboxGroup.js"></script>
	<script type="text/javascript" src="../res/js/controls/Ext.ux.RadioGroup.js"></script>
	<script type="text/javascript" src="../res/js/controls/Ext.ux.TextField.js"></script>
	<script type="text/javascript" src="../res/js/controls/Ext.ux.VerticalTabPanel.js"></script>
	<script type="text/javascript" src="../res/js/ext2/ext-lang-zh_CN.js"></script>
	<script type="text/javascript" src="../res/js/hisListMgr.js"></script>
	<style>
		body {font-size:14px;}
		input,select{font-size:12px}
		label {margin:0 2px 0 2px}
		textarea {font-size:12px;font-family:tahoma,arial,helvetica,sans-serif}
		.addField{background:url("../res/js/ext2/resources/images/default/dd/drop-add.gif") left  no-repeat !important;}
		.delField{background:url("../res/js/ext2/resources/images/default/my/del-form.gif") left  no-repeat !important;}
		.saveField{background:url("../res/js/ext2/resources/images/default/my/save.gif") left  no-repeat !important;}
				
		.itemStyle {
		    padding:8px;
		    /*border: 1px solid silver;*/
		    margin-bottom:0;
		}
		.itemStyle2{
		    padding:0px;
		    margin-bottom:0;
		}
		.fieldsetStyle{
			margin:4px 10px 4px 10px
		}
		/**checkboxGroup**/
		.inputUnit {
			display: inline-block;
			margin: 5px;
			width: 160px;
		}

		.content{
			BORDER-BOTTOM: #fafafa 1px solid; BORDER-LEFT: #fafafa 1px solid; PADDING-BOTTOM: 0px; BACKGROUND-COLOR: #fafafa; MARGIN: 0px auto; PADDING-LEFT: 5px; WIDTH: auto; PADDING-RIGHT: 5px; DISPLAY: block; BORDER-TOP: #fafafa 1px solid; BORDER-RIGHT: #fafafa 1px solid; PADDING-TOP: 0px;
			font-size: 12px;
		}
		.content .TABLE {
			TEXT-ALIGN: center; BACKGROUND-COLOR: #cccccc; MARGIN: 5px; WIDTH: 95%
		}
		.content .TH {
			BACKGROUND-COLOR: #efeff2; HEIGHT: 30px;width:137px;
		}
		.content .TR {
			BACKGROUND-COLOR: #ffffff
		}
		.content .TD {
			PADDING-LEFT: 1px; HEIGHT: 25px
		}
		.content .head_td {
			TEXT-ALIGN: right; BACKGROUND-COLOR: #efeff2; WIDTH: 15%; PADDING-RIGHT: 10px; HEIGHT: 25px; FONT-WEIGHT: bold;width:137px;
		}
		.content .remark_td {
			TEXT-ALIGN: left; BACKGROUND-COLOR: #ffffd9; PADDING-LEFT: 5px;  HEIGHT: 25px
		}
		.content2 {
			BORDER-BOTTOM: #fafafa 1px solid; BORDER-LEFT: #fafafa 1px solid; PADDING-BOTTOM: 0px; BACKGROUND-COLOR: #fafafa; MARGIN: 0px auto; PADDING-LEFT: 5px; WIDTH: auto; PADDING-RIGHT: 5px; DISPLAY: block; BORDER-TOP: #fafafa 1px solid; BORDER-RIGHT: #fafafa 1px solid; PADDING-TOP: 0px;
			font-size: 12px;
		}
		.content2 .remark_td {
			TEXT-ALIGN: left; BACKGROUND-COLOR: #ffffd9; PADDING-LEFT: 5px;  HEIGHT: 25px
		}
		
		.details {width:100%;margin:5px 0 5px 0;}
		summary {background-color: #99BBE8;cursor:pointer； display: inline-block; cursor: pointer; vertical-align: top; text-indent: 1em; }

		.deleteButton{background:url("../res/img/runTime/delete1.gif") left  no-repeat !important;width:16px;height:16px;cursor:pointer;}
		
		fieldset.x-panel-collapsed{
			border: none;
			padding-bottom: 0 !important;
		}
	</style>
<script>
		var listPage__=${listPageJson};
		var formId__=#{formId!0};
		var listId__=#{listId!0};
		var nodeId__=#{nodeId!0};
		
		var LPCFG;
		var initCommonParams = function(cfg){
			if(typeof cfg != 'undefined'){
				listPage__.config = listPage__.config || {};
				typeof cfg.txtTitle != 'undefined' && ( listPage__.name = cfg.txtTitle );
				Ext.apply(listPage__.config, cfg);
				//console.log('============innner listPage__');
				//console.log(listPage__);
			}
			LPCFG = listPage__.config?listPage__.config:{};
			if(!LPCFG.pagesize) LPCFG.pagesize=20;
			if(!LPCFG.filter)LPCFG.filter ='';
			//if(!LPCFG.template)LPCFG.template ='';
			LPCFG.template ='listTemplate_url_1';
			if(!LPCFG.myTemplate)LPCFG.myTemplate ='';
			
			if(!LPCFG.buttons)LPCFG.buttons ={
				ext:[]
			};
			if(!LPCFG.menus)LPCFG.menus ={};		
			if(!LPCFG.searchSvr)LPCFG.searchSvr =[];
			if(!LPCFG.columns)LPCFG.columns =[];
		}
		initCommonParams();
	</script>
<script type="text/javascript" src="xlist/listManage.js"></script>		
</head>
<body>

<script>
var listMgr2 = {
	init:function(){
		this.mainViewport = new Ext.Viewport({
			frame:true,
			layout:"border",
			margins:'5',
			items:[{
				xtype:'toolbar',
				region: 'north',	
				border:false,
				style:'border:none;',
				id:'topToolbar',
				items:[{
					xtype: 'tbbutton',
					text:'通过JSON导入',
					id:'btnFormImport',
					handler:function(){
						var win =new Ext.Window({
							title:"导入JSON（列表视图配置）",
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
										var configStr = win.items.items[0].getValue();
										var cfg = Ext.util.JSON.decode(configStr);
										//console.log('=============console.log(cfg);');
										//console.log(cfg);
										if(cfg.sourceType != 'url'){
											Ext.Msg.show({
												   title:'警告提示',
												   msg: '不能把数据源类型【' + cfg.sourceType +'】的配置导入自定义URL数据源类型的页面，请选择相应的数据源类型，再进行导入！',
												   buttons: Ext.Msg.OK,
												   animEl: 'elId',
												   minWidth:420,
												   icon: Ext.MessageBox.ERROR 
											});
											return false;
										}
										initCommonParams(cfg);
										win.close();
										listMgr2.reinit();
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
							var form_win = new Ext.Window({
								title:'导出JSON（列表视图配置）',
								layout:"auto",
								modal:true,
								//id:"previewFormWin",
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
									text:'关闭',
									width:100,
									handler:function(){
										form_win.close();
									}
									
								}]
							});
							
							var frm  = listMgr2.getAllFormValues();
							var cfg = listMgr2.collectListCfg(frm);
							delete cfg.viewId;
							var txt = new Ext.form.TextArea({
								width:620,
								height:440,
								anchor:'100%',
								value:Ext.CMPP.JsonUti.toString(cfg)
							});
							form_win.add(txt);
							form_win.show();
						}
				},{
					xtype: 'tbbutton',
					text: '保存(Ctrl+S)',
					//minWidth :60,
					handler:function(){
						listMgr2.btnSaveClick();
					}
				},{
					text:'预览',
					id:'btnPreview',
					scope:this,
					handler:function(){
						listMgr2.btnPreviewClick();
					}
				},
				'->',
				{
					text:'历史记录',
					id:'btnViewHistory'
				}]
			},//end toolbar
			{
				xtype:'vrtabpanel',
				//id:"centerTabPanel",
				region:"center",
				//enableTabScroll :true,
				autoScroll:true, 
				layoutOnTabChange:true,
				resizeTabs:true,
				activeTab:0,
				frame:true,
				//border:false,
				bodyStyle:'overflow:hidden;',
				tabPosition: 'left',
				verticalText: true,//竖直文本标志
				deferredRender:false,//初始时，渲染所有tab页
				items:[{
						xtype:"panel",
						closable:false,
						autoScroll:true,	
						title:"基础配置",
						items:[{
							xtype:'form',
							id:'baseCfgForm',
							style:'padding:10px 10px 10px 10px;overflow-x:hidden;',
							//bodyStyle:'padding:0 5px 10px 10px',
							itemCls:"itemStyle",
							layout:'xform',
							labelAlign :'right',
							labelWidth :110,
							//autoScroll:true,
							border:false,
							items:[{
								xtype:'radiogroup',
								name:'sourceType',
								fieldLabel:'数据源类型',
								dataSource:[['default','默认数据源'],['sql','自定义SQL数据源'],['url','自定义URL数据源']],
								value:'url',
								listeners:{
									change:function(){
										var sourceType = this.getValue();
										location.href = 'listConfig!getListManage.jhtml?nodeId='+ nodeId__ +'&formId='+ formId__ +'&listId=' + listId__ + '&sourceType=' + sourceType;
									}
								}
							},{
								xtype:'xTextField',
								fieldLabel:'列表名称',
								name:'txtTitle',
								anchor:'95%',
								value:listPage__.name?listPage__.name:''
							},{
								xtype:'xTextField',
								fieldLabel:'数据源地址(url)',
								name:'txtUrl',
								anchor:'95%',
								emptyText:"http://",
								value:LPCFG.url?LPCFG.url:''
							},
							{
								xtype:'xTextField',
								fieldLabel:'需要的字段(,号分割)',
								name:'neededFields',
								anchor:'95%',
								emptyText:"用,分割",
								value:LPCFG.neededFields?LPCFG.neededFields:''
							},
							
							//fieldLabel:'每页显示记录数',
							{
								border:false,
								ctCls :"itemStyle2",
								layout:'column',
								items:[{
									layout: 'xform',
									border:false,
									width:200,
									items:[{
										xtype:'checkbox',
										fieldLabel:'每页显示记录数',
										boxLabel :'自适应',
										name:'chkAutoPagesize',
										checked :LPCFG.autoPagesize==false?false:true,
										listeners :{
											scope:this,
											check:function(obj,checked){
												if(listMgr2.baseCfgForm)listMgr2.baseCfgForm.find('name','txtPageCount')[0].setDisabled(checked);
											}
										}
									}]
								},{
									layout: 'xform',
									border:false,
									hideLabels:true,
									width:100,
									items:[{
										xtype:'numberfield',
										name:'txtPageCount',
										disabled:true,
										value:LPCFG.pagesize?LPCFG.pagesize:20,
										width:50
									}]
								}]
							},	
							{//fieldLabel:'自定义按钮配置',
								
								border:false,
								ctCls :"itemStyle2",
								layout:'column',
								items:[{
									layout: 'xform',
									border:false,
									items:[{
										xtype:'xTextField',
										fieldLabel:'自定义按钮配置',
										width:0,
										style:'display:none;'
									}]
								},{
									columnWidth:.6,
									xtype:'toolbar',
									id:'toolbarCustom',
									height:30,
									items:function(){
										var extBtns = LPCFG.buttons.ext;
										var btnsCfg=[];
										if(extBtns){
											for(var i=0;i<extBtns.length;i++){
												extBtns[i].order=extBtns[i].order?extBtns[i].order:(i+1);
												var btnCfg={
													id:extBtns[i].id,
													text:extBtns[i].text,
													iconCls:extBtns[i].iconCls,
													cfg:extBtns[i]
												};

												btnCfg.vtype = 'tbsplit';
												btnCfg.menu=[{
													text         : '配置',
													scope:extBtns[i],
													handler  :function(){
														var sender = listMgr2.toolbarCustom.items.map[this.id];
														this.sender = sender;
														listMgr2.showButtonCfgWin(this); 
													}
												},{
													text         : '查看运行日志',
													scope:extBtns[i],
													handler :function(){
														var sender = listMgr2.toolbarCustom.items.map[this.id];
														var url = '../runtime/getScriptLog.jhtml?id1='+listPage__.formId+'&id2='+ this.id +'&nodeId=' + nodeId__;

														var logWin = new Ext.Window({
															title:'运行日志',
															height:500,
															width:400, 
															buttonAlign: "center",
															closable:true ,
															closeAction:'close',
															autoScroll:true,
															modal:false,
															layout:'fit',
															resizable :true,
															btnId:'',
															defaultButton:'logWinCloseBtn',
															buttons:[{
																text:'关闭',
																id:'logWinCloseBtn',
																handler:function(){
																	this.ownerCt.close();
																}
															}]
														});
														logWin.show(sender.el,function(){
															this.win.body.load({
																url: this.url,
																text: "正在加载日志...",
																callback:function(el,success,response){
																	if(success){
																		if(response.responseText==''){
																			el.update('没有运行的记录');
																		}
																	}else{
																		el.update('获取运行日志出错');
																	}													
																	
																}
														   });
														},{win:logWin,url:url});
														
				
													}
												},{
													text         : '删除',
													iconCls:'delField',
													scope:extBtns[i],
													handler :function(){
														var sender = listMgr2.toolbarCustom.items.map[this.id];
														listMgr2.toolbarCustom.items.removeKey(this.id);
														sender.el.fadeOut({
															endOpacity: 0, 
															easing: 'easeOut',
															duration: .5,
															remove: true
														});
													}
												}]
												btnsCfg.push(btnCfg);
											}
										}
										return btnsCfg;
									}()
								},{
									xtype:'toolbar',
									height:30,
									items:[{
										text:'添加',
										iconCls:'addField',
										handler:function(obj,e){
											listMgr2.showButtonCfgWin({
												sender:obj.getEl(),action:'new',
												text:' ',
												iconCls:'',
												script:'',
												isMenuItem:''
											});
										}
									}]
								}]
							},
							
							{//fieldLabel:'模板',
								
								border:false,
								ctCls :"itemStyle2",
								layout:'column',
								items:[{
									layout: 'xform',
									border:false,
									width:370,
									items:[{
										xtype:'combo',
										fieldLabel:'模板',
										name:'selTpl',
										hiddenName:'selTpl',
										store: new Ext.data.JsonStore({
											fields : [{name : 'text'}, { name : 'value'}],
											data:[{
												text:'Url数据源列表页模板1',
												value:'listTemplate_url_1'
											}]
										}),
										displayField: 'text',
										valueField: 'value',
										typeAhead: true,
										mode: 'local',
										triggerAction: 'all',
										editable : false,
										readOnly:true,
										value: LPCFG.template?LPCFG.template:'listTemplate_url_1',//保持兼容性
										width:200,
										disable:LPCFG.myTemplate?true:false
									}]
								},{
									layout: 'xform',
									border:false,
									hideLabels:true,
									width:520,
									style:'padding-top:15px',
									items:[{
										xtype:'fieldset',
										title :'自己写模板',
										name:'chkUserMyTpl',
										checkboxName:'chkUserMyTpl',
										checkboxToggle :true,
										collapsed  :LPCFG.myTemplate?false:true,
										layout:'fit',
										anchor:'99%',
										height:450,
										style:'padding:0;border:none;',
										items:[{
											xtype:'textarea',
											name:'txtTpl'
										}]
									}]
								}]
							}]
						}]
				       },//end 基础配置 panel
				       {
							xtype:"panel",
							closable:false,
							autoScroll:true,
							title:"更多配置",
							items:{
									xtype:'form',
									id:'moreCfgForm',
									border:false,
									layout:'xform',
									itemCls:"itemStyle",
									style:'margin:10px;',
									labelAlign :'right',
									labelWidth :120,
									items:[
										//</head>之前注入
										{
											xtype:'textarea',
											fieldLabel:'&lt;/head&gt;之前注入',
											name:'txtInjectHead',
											height:150,
											anchor:'95%',
											value:LPCFG.headInject?LPCFG.headInject:''
										},
										//</head>之前注入
										{
											xtype:'textarea',
											fieldLabel:'Ext.onReady注入(JS)',
											name:'txtExtOnReadyJs',
											height:150,
											anchor:'95%',
											value:LPCFG.extOnReadyJs?LPCFG.extOnReadyJs:''
										},
										//</body>之前注入
										{
											xtype:'textarea',
											fieldLabel:'&lt;/body&gt;之前注入',
											name:'txtInjectBody',
											height:150,
											anchor:'95%',
											value:LPCFG.bodyInject?LPCFG.bodyInject:''
										}]
							}
				       },//end 更多配置 panel
				       {
							xtype:"panel",
							closable:false,
							autoScroll:true,
							title:"列表项配置",
							items:{
								xtype:'form',
								id:'listCfgForm',
								border:false,
								layout:'xform',
								itemCls:"itemStyle",
								autoHeight:true,
								autoWidth:true,
								style:'margin:10px;',
								items:[
									this.initColumnSetting()
								]
							}
				       },//end 列表项配置 panel
				       {
							xtype:"panel",
							closable:false,
							autoScroll:true,
							title:"搜索配置",
							items:{
								xtype:'form',
								id:'searchCfgForm',
								border:false,
								ctCls :"itemStyle2",
								anchor:'95%',
								autoHeight:true,
								style:'margin:10px;',
								//autoScroll:true,
								layout:'xform',
								items:[{	//从搜索服务搜索的配置'
										xtype:'fieldset',
										ctCls :"itemStyle2",
										title:'启用从搜索服务搜索',
										//anchor:'98%',
										autoHeight:true,
										checkboxToggle :true,
										name:'chkSearchService',
										checkboxName:'chkSearchService',
										collapsed  :LPCFG.enableSearchSvr==false?true:false,
										//style:'margin:10px;',
										autoScroll:true,
										layout:'xform',
										itemCls :"itemStyle2",
										labelWidth :120,
										items:[{
											xtype:'fieldset',
											style:'padding: 0px;',
											border:false,
											autoHeight:true,
											//anchor:'98%',
											layout:'fit',
											items:[this.initSvrSearchSetting()]
										}]
									}]
							}
				       }//end 搜索配置 panel
				 ]
			}]//end tabpanel
		});//end Ext.Viewport()
		
		this.baseCfgForm = Ext.getCmp('baseCfgForm');
		this.moreCfgForm = Ext.getCmp('moreCfgForm');
		this.listCfgForm = Ext.getCmp('listCfgForm');
		this.searchCfgForm = Ext.getCmp('searchCfgForm');
		
		this.toolbarCustom=Ext.getCmp('toolbarCustom');
		
		//快捷键保存
		var docMap = new Ext.KeyMap(Ext.getDoc(), {
			key: 's',
			shift:false,
			ctrl:true,
			handler: function(key,e){
				e.preventDefault();
				listMgr2.btnSaveClick();
			}
		});
		
		//历史记录
		hisListMgr.initHistory({
			assistUrl : "../runtime/",
			versionKey : nodeId__+'_'+formId__+'_'+listId__ + '_list',
			btnViewHistory : Ext.getCmp('btnViewHistory'),
			reSetFormPanels : function(cfg){
				if(cfg.sourceType != 'url'){
					Ext.Msg.show({
						   title:'警告提示',
						   msg: '不能把数据源类型【' + cfg.sourceType +'】的配置回滚到自定义URL数据源类型的页面，请选择相应的数据源类型，再进行历史回滚！',
						   buttons: Ext.Msg.OK,
						   animEl: 'elId',
						   minWidth:420,
						   icon: Ext.MessageBox.ERROR 
					});
					return false;
				}
				initCommonParams(cfg);
				listMgr2.reinit();
			}
		});
	},//end init()
	//初始化列表项配置
	initColumnSetting:function(){

		//初始化 列表项配置
		var store_col;
		
		var fieldColumn = Ext.data.Record.create([
		   {name: 'title', type: 'string'},
		   {name: 'tpl', type: 'string'},
		   {name: 'width', type: 'int'}, 
			{name: 'align', type: 'string'},  		   
		   {name: 'tipTpl', type: 'string'}, 
			{name: 'isWidthFree', type: 'bool'}, 		   
		   {name: 'isView', type: 'bool'}
		]);	
		btnDeleteButtonClick = function (){
			var selItems = grid_col.getSelectionModel().selections.items;
			if(selItems.length>0){
				var ids=[];
				for(var i=selItems.length-1;i>=0;i--){
					ids.push(selItems[i].data.f_name);
					store_col.remove(selItems[i]);
				}			
			}
		}
		
		var chkHideColumn = new Ext.grid.CheckColumn({
			header: "隐藏列吗?",
			dataIndex: 'isView',
			//width: 75,
			align:'center',
			renderer:function(v,p,record){
				p.css += ' x-grid3-check-col-td'; 
				return '<div class="x-grid3-check-col'+(!v?'-on':'')+' x-grid3-cc-'+this.id+'">&#160;</div>';
			}
		});
		var chkFreeColumn = new Ext.grid.CheckColumn({
			header: "自由宽度?",
			dataIndex: 'isWidthFree',
			//width: 75,
			align:'center',
			renderer:function(v,p,record){
				p.css += ' x-grid3-check-col-td'; 
				return '<div class="x-grid3-check-col'+(v?'-on':'')+' x-grid3-cc-'+this.id+'">&#160;</div>';
			}
		});
		var chkShowTipColumn = new Ext.grid.CheckColumn({
			id:'isShowTip',
			header: "显示Tip吗?",
			align:'center',
			dataIndex: 'isShowTip',
			width: 75
		});	
		var cm = new Ext.grid.ColumnModel([
			new Ext.grid.RowNumberer(),
			new Ext.grid.CheckboxSelectionModel(),{
				id:'title',
				header: "列名",
				dataIndex: 'title',
				resizable:true,
				editor: new Ext.form.TextField({
				   allowBlank: true
				})
			},{
				id:'tpl',
				header: "列模板",
				dataIndex: 'tpl',
				width: 80,
				align:'center',
				editor: new Ext.ux.CustomEditors.TextArea({}),
				renderer:function(v,p,record){
					//return '<textarea style="height:40px;scroll:none;">'+ v +'</textarea>';
					if(v) return "点击编辑";
					else return "";
				}
			},{
				id:'width',
				header: "列宽",
				dataIndex: 'width',
				width: 60,
				align:'right',
				editor: new Ext.form.NumberField({})
			},{
				header: "对齐方式",
				dataIndex: 'align',
				width: 100,
				renderer:function(v,p,record,rowIndex,colIndex,data ){						
					var text = v;
					var combo = Ext.getCmp('comboAlign');
					if(combo.valueField){
						var r = combo.findRecord(combo.valueField, v);
						if(r){
							text = r.data[combo.displayField];
						}else if(combo.valueNotFoundText !== undefined){
							text = combo.valueNotFoundText;
						}
					}
					return text||'left';  //显示显示值
				},
				editor: new Ext.form.ComboBox({
					id:"comboAlign",
					triggerAction: 'all',
					editable:false,
					mode: 'local',
					store:new Ext.data.SimpleStore({
						fields:['value','text'],
						data:[['left','left'],['center','center'],['right','right']]
					}),
					valueField : 'value',//值
					displayField : 'text',//显示文本
					listClass: 'x-combo-list-small'
				})
			},
			chkShowTipColumn,{
				id:'tipTpl',
				header: "Tip模板",
				dataIndex: 'tipTpl',
				align:'center',
				width: 80,
				editor:  new Ext.ux.CustomEditors.TextArea({}),
				renderer:function(v,p,record){
					if(v) return "点击编辑";
					else return "";
				}
			},
			{
				header: "自由宽度",
				align:'center',
				width: 30,
				dataIndex:'isWidthFree',
				renderer:function(v,p,record,rowIndex,colIndex,data ){
					var chkStr = record.data.isWidthFree==true?' checked="checked" ':'';
					return '<input type="radio" ' + chkStr + ' name="isWidthFree" value="'+ rowIndex +'" />	'; 
				}
				
			},chkHideColumn,{
				header: "删除",
				align:'center',
				width: 30,
				dataIndex:'id',
				renderer:function(v,p,record){
					return '<div class="deleteButton" onclick="btnDeleteButtonClick()"></div>'; 
				}
			}
			
		]);
		cm.allowSort = true;
		
		// create the Data Store
		store_col= new Ext.data.Store({
			proxy: new Ext.data.MemoryProxy(LPCFG), 
			reader : new Ext.data.JsonReader({
				autoLoad:true,
				root : "columns",
				fields: ["title","tpl","isView","width","isShowTip","tipTpl","align","isWidthFree"]
			}),
			listeners: { 
				load : function(t, records,options) {
					for(var i=0;i<records.length;i++){
						if(records[i].get('tipTpl'))
							records[i].set('tipTpl',myUnZhuanyi(records[i].get('tipTpl')));
					}
				}
			}
		});

		// create the editor grid_col
		var grid_col = new Ext.grid.EditorGridPanel({
			//id:'gridColumnSetting',
			store: store_col,
			frame:false,
			hideBorders:true,
			enableColumnHide:false,
			stripeRows :true,//表格行颜色间隔显示
			cm: cm,
			sm: new Ext.grid.CheckboxSelectionModel(),
			//renderTo: 'colunmCfgContainer',
			//height:500,
			autoHeight:true,
			autoWidth:true,
			//width:700,
			autoExpandColumn:'title',
			enableHdMenu: false,
			dropConfig: {appendOnly:false},
			trackMouseOver : true,
			enableDragDrop: true,	
			ddGroup: "GridDD",	
			clicksToEdit:1,
			plugins:[chkShowTipColumn,chkFreeColumn,chkHideColumn],
			tbar: [{
				text: '添加列',
				iconCls:"addField",
				handler : function(){
					var p = new fieldColumn({
						title: '',
						tpl:'',
						field:'',
						tipTpl:'',
						isView:true
						
					});
					grid_col.stopEditing();
					var len = store_col.getCount();
					store_col.insert(len, p);
					grid_col.startEditing(len, 1);
				}
			},{
				text: '删除列',
				disable:true,
				iconCls:"delField",
				handler : btnDeleteButtonClick
			},{
				xtype:'tbfill'
			},{
				xtype:'label',
				text:'小提示:拖拽行可实现顺序调换'
			}],
			listeners:{
				render:function(grid_col){
					//拖拽排序
					var ddrow = new Ext.dd.DropTarget(grid_col.container, { 
						ddGroup : 'GridDD', 
						copy : false, 
						notifyDrop : function(dd, e, data) { 
							//var rows = data.selections;
							var sm = grid_col.getSelectionModel(); 
							var rows = sm.getSelections(); 
							var store = grid_col.getStore();
							var cindex = dd.getDragData(e).rowIndex; 
							if (cindex == undefined || cindex < 0){ 
								e.cancel=true; 
								return; 
							} 
							for (i = 0; i < rows.length; i++) { 
								var rowData = rows[i]; 
								if (!this.copy) { 
									store.remove(rowData); 
									store.insert(cindex, rowData); 
									grid_col.getView().refresh();
								} 
							}
						} 
					});
				}
			}

			
		});
		
		// trigger the data store load
		store_col.load();
		this.grid_col = grid_col;
		return grid_col;
	},
	//初始化从数据库搜索的配置
	initSvrSearchSetting:function(){

		var store;
		var chkEnableColumn = new Ext.grid.CheckColumn({
			header: "启用吗?",
			dataIndex: 'enable',
			width: 75,
			renderer:function(v,p,record){
				p.css += ' x-grid3-check-col-td'; 
				return '<div class="x-grid3-check-col'+(v!=false?'-on':'')+' x-grid3-cc-'+this.id+'">&#160;</div>';
			}
		});
		
		var cm = new Ext.grid.ColumnModel([
			new Ext.grid.RowNumberer(),
			{
				header: "字段名",
				dataIndex: 'field',
				width: 200,		
				editor: new Ext.form.TextField({
					allowBlank:true
				})
			},{
				id:'title',
				header: "字段标题",
				dataIndex: 'title',
				editor: new Ext.form.TextField({
					allowBlank:true
				})
			},{
				id:'ctrl',
				header: "控件",
				dataIndex: 'ctrl',
				//width: 120,
				renderer:function(v,p,record,rowIndex,colIndex,data ){						
					var text = v;
					var combo = Ext.getCmp('comboCtrls');
					if(combo.valueField){
						var r = combo.findRecord(combo.valueField, v);
						if(r){
							text = r.data[combo.displayField];
						}else if(combo.valueNotFoundText !== undefined){
							text = combo.valueNotFoundText;
						}
					}
					return text;  //显示显示值
				},
				editor: new Ext.form.ComboBox({
					id:"comboCtrls",
					triggerAction: 'all',
					editable:false,
					mode: 'local',
					store:new Ext.data.SimpleStore({
						fields:['value','text'],
						data:controlTypeEnZh__
					}),
					valueField : 'value',//值
					displayField : 'text',//显示文本
					listClass: 'x-combo-list-small'
				})
			},
			{
				id:'dataSourceType',
				header: "数据源类型",
				dataIndex: 'dataSourceType',
				//width: 80,
				renderer:function(v,p,record,rowIndex,colIndex,data ){						
					var text = v;
					var combo = Ext.getCmp('comboDataSourceType');
					if(combo.valueField){
						var r = combo.findRecord(combo.valueField, v);
						if(r){
							text = r.data[combo.displayField];
						}else if(combo.valueNotFoundText !== undefined){
							text = combo.valueNotFoundText;
						}
					}
					return text;  //显示显示值
				},
				editor: new Ext.form.ComboBox({
					id:"comboDataSourceType",
					triggerAction: 'all',
					editable:false,
					mode: 'local',
					store:new Ext.data.SimpleStore({
						fields:['value','text'],
						data:[['','默认'],['json','json'],['sql','sql'],['url','url']]
					}),
					valueField : 'value',//值
					displayField : 'text',//显示文本
					listClass: 'x-combo-list-small'
				})
			},{
				id:'comboWidth',
				header: "宽度",
				dataIndex: 'width',
				//width: 120,
				editor: new Ext.form.NumberField({})
			},{
				id:'comboDataSource',
				header: "数据源",
				dataIndex: 'dataSource',
				//width: 120,
				editor: new Ext.ux.CustomEditors.TextArea({}),
				renderer:function(v,p,record){
					if(v) return "点击编辑";
					else return "";
				}
			},chkEnableColumn
			
		]);
		cm.allowSort = true;
		
		// create the Data Store
		//初始化搜索项配置
		var sItems = [];
		for(var i=0;i<6;i++){
			var item = LPCFG.searchSvr[i];
			if(item){
				var title = item.title;
				sItems.push({
					field:item.field,
					title:title,
					ctrl:item.ctrl,
					width:item.width,
					dataSourceType:item.dataSourceType,
					dataSource:item.dataSource,
					enable:item.enable==false?false:true
				});
			}else{
				sItems.push({
					field:'',
					title:'',
					ctrl:'',
					dataSourceType:'',
					dataSource:'',
					enable:true
				})	;
			}
		}
		
		store= new Ext.data.Store({
			proxy: new Ext.data.MemoryProxy(sItems), 
			reader : new Ext.data.JsonReader({
				autoLoad:true,
				fields: ["field","title","enable","dataSourceType","ctrl","width","dataSource"]
			})
		});

		// create the editor grid_col
		var grid = new Ext.grid.EditorGridPanel({
			//id:'gridColumnSetting',
			store: store,
			frame:false,
			hideBorders:true,
			enableColumnHide:false,
			stripeRows :true,//表格行颜色间隔显示
			cm: cm,
			sm: new Ext.grid.CheckboxSelectionModel(),
			//renderTo: 'colunmCfgContainer',
			//height:500,
			autoHeight:true,
			width:700,
			autoExpandColumn:'title',
			enableHdMenu: false,
			dropConfig: {appendOnly:false},
			trackMouseOver : true,
			enableDragDrop: true,	
			ddGroup: "GridDD3",	
			clicksToEdit:1,
			plugins:[chkEnableColumn],
			tbar: [{
				xtype:'tbfill'
			},{
				xtype:'label',
				text:'小提示:拖拽行可实现顺序调换'
			}],
			listeners:{
				render:function(grid_col){
					//拖拽排序
					var ddrow = new Ext.dd.DropTarget(grid_col.container, { 
						ddGroup : 'GridDD3', 
						copy : false, 
						notifyDrop : function(dd, e, data) { 
							//var rows = data.selections;
							var sm = grid_col.getSelectionModel(); 
							var rows = sm.getSelections(); 
							var store = grid_col.getStore();
							var cindex = dd.getDragData(e).rowIndex; 
							if (cindex == undefined || cindex < 0){ 
								e.cancel=true; 
								return; 
							} 
							for (i = 0; i < rows.length; i++) { 
								var rowData = rows[i]; 
								if (!this.copy) { 
									store.remove(rowData); 
									store.insert(cindex, rowData); 
									grid_col.getView().refresh();
								} 
							}
						} 
					});
				},
				afteredit:function(e){
					if(e.field=='field'){
						var rec = e.record.store.data.items[e.row];
						//var rec = e.record;
						if(e.value){
							rec.data.title = fieldsJson__[e.value].f_title;
							e.grid.getView().refresh();
						}
					}
				}
			}
			
		});
		
		//clear filter
		this.gridServiceSearch = grid;
		// trigger the data store load
		store.load();
		return grid;
		
	},
	//打开按钮配置窗口
	showButtonCfgWin:function(opts){
		if(!opts){
			opts = this;
		}
		var sender = opts.sender;
		
		if(listMgr2.buttonCfgWin==null){
			listMgr2.buttonCfgWin = new Ext.Window({
				title:'自定义按钮配置',
				height:578,
				width:635, 
				buttonAlign: "center",
				closable:true ,
				closeAction:'hide',
				autoScroll:true,
				modal:false,
				layout:'fit',
				resizable :false,
				btnId:'',
				defaultButton:'btnCfgOK',
				items:[{
					xtype:'form',
					id:'frmBtnCfg',
					style:'padding:5px',
					bodyStyle:'padding:5px',
					itemCls:"itemStyle",
					labelAlign:'right',
					labelWidth:110,
					style:'padding:5px',
					autoScroll:true,
					layout:'xform',
					items:[{
						xtype:'numberfield',
						fieldLabel:'排序号',
						width:50,
						name:'order',
						value:''
					},{
						xtype:'xTextField',
						fieldLabel:'文本',
						anchor:'90%',
						name:'text',
						emptyText:'按钮名称不能为空',
						allowBlank:false,
						value:''
					},{
						xtype:'xTextField',
						fieldLabel:'图标Cls',
						name:'iconCls',
						anchor:'90%',
						value:''
					},{
						xtype:'textarea',
						fieldLabel:'客户端脚本(提交前)',
						anchor:'90%',
						height:100,
						name:'js',
						value:''
						
					},{
						xtype:'textarea',
						fieldLabel:'服务端脚本',
						anchor:'90%',
						height:118,
						name:'script',
						value:'',
						extra:{
							xtype:"button",
							text:"调试脚本",
							style:"margin-left:1em;display:inline-block;",
							listeners:{
								'click':function(){
									setActiveTab('scriptdebug.jhtml?nodeId='+nodeId__ +'&id1='+ listPage__.formId+'&id2='+ listMgr2.buttonCfgWin.btnId +'&stype=form','formmanage_' + listMgr2.buttonCfgWin.btnId,'调试脚本');
								}
							}						
						}
					},{
						xtype:'textarea',
						fieldLabel:'客户端脚本(提交后)',
						anchor:'90%',
						height:100,
						name:'afterjs',
						value:''
						
					},{
						xtype:'checkbox',
						fieldLabel:'其他',
						//hideLabel :true,
						boxLabel: '是否在右键菜单中显示?',
						value:'on',
						name:'isMenuItem'
					}]
				}],
				buttons:[{
					text:'确定',
					id:'btnCfgOK',
					scope:opts,
					handler:function(){
						var frm = listMgr2.buttonCfgWin.find('id','frmBtnCfg')[0];
						if(!frm.form.isValid()){
							return;
						}
						var values = frm.form.getValues();
						if(this.action =='new'){
							values.id=listPage__.formId + '_' + listPage__.listId + '_' + (new Date()).valueOf();
							var btnCfg={
								id:values.id,
								text:values.text,
								iconCls:values.iconCls,
								cfg:values
							};

							btnCfg.vtype = 'tbsplit';
							btnCfg.menu=[{
								text         : '配置',
								scope:values,
								handler  :function(){
									var sender = listMgr2.toolbarCustom.items.map[this.id];
									this.sender = sender;
									listMgr2.showButtonCfgWin(this); 
								}
							},{
								text         : '删除',
								iconCls:'delField',
								scope:values,
								handler :function(){
									var sender = listMgr2.toolbarCustom.items.map[this.id];
									listMgr2.toolbarCustom.items.removeKey(this.id);
									sender.el.fadeOut({
										endOpacity: 0, 
										easing: 'easeOut',
										duration: .5,
										remove: true
									});
								}
							}];
							listMgr2.toolbarCustom.add(btnCfg);
							
							
						}else{
							this.sender.setText(values.text);
							this.sender.setIconClass(values.iconCls);
							this.sender.cfg = values;
						}
						listMgr2.buttonCfgWin.hide();
					}
				},{
					text:'取消',
					handler:function(){
						listMgr2.buttonCfgWin.hide();
					}
				}]
			});
		}
		listMgr2.buttonCfgWin.buttons[0].scope =opts;
		if(sender) listMgr2.buttonCfgWin.setAnimateTarget(sender.el);
		listMgr2.buttonCfgWin.show();
		
		//setValues
		var cfg = sender.cfg;
		if(cfg && typeof(cfg)!='undefined'){
			var frm = listMgr2.buttonCfgWin.find('id','frmBtnCfg')[0];
			frm.form.setValues(cfg);
			frm.find('name','isMenuItem')[0].setValue(cfg.isMenuItem?true:false);
		}else{
			var frm = listMgr2.buttonCfgWin.find('id','frmBtnCfg')[0];
			//frm.find('name','text')[0].setValue('');
			frm.find('name','iconCls')[0].setValue('');
			frm.find('name','js')[0].setValue('');
			frm.find('name','script')[0].setValue('');
			frm.find('name','afterjs')[0].setValue('');
			frm.find('name','isMenuItem')[0].setValue(false);
			frm.find('name','order')[0].setValue(listMgr2.toolbarCustom.items.length+1);
			
			listMgr2.buttonCfgWin.setTitle('自定义按钮配置');
		}
	},
	collectListCfg:function(frm){
		var cfg={
			txtTitle:frm.txtTitle,//列表名称
			sourceType:frm.sourceType,
			url:frm.txtUrl,
			neededFields:frm.neededFields,
			template:frm.selTpl,
			pagesize:frm.txtPageCount?frm.txtPageCount:22,
			autoPagesize:frm.chkAutoPagesize?true:false,
			headInject:frm.txtInjectHead,
			bodyInject:frm.txtInjectBody,	
			extOnReadyJs:frm.txtExtOnReadyJs,
			isWidthFreeRow:-1,
			myTemplate:frm.chkUserMyTpl?frm.txtTpl:'',
			buttons:{
				"ext":[]
			},
			columns:[],
			enableSearchSvr:frm.chkSearchService?true:false,
			searchSvr:[]
		};
				
		////////cfg.columns///////////////
		//todo获得自由宽度的列
		var radios = Ext.query('input[name="isWidthFree"]');
		var isWidthFreeRow=-1;
		var columns = [];
		items = listMgr2.grid_col.store.data.items;
		for(var i=0;i<items.length;i++){
			var item = items[i].data;
			if(item.title){
				item.tipTpl = myZhuanyi(item.tipTpl);		
			}
			if(isWidthFreeRow==-1 && radios[i] && radios[i].checked){
				isWidthFreeRow=i;
				item.isWidthFree = true;
			}else{
				item.isWidthFree = "";
			}
			columns.push(item);	
		}
		if(isWidthFreeRow!=-1){
			cfg.isWidthFreeRow=isWidthFreeRow;
		}
		cfg.columns = columns;
		
		//////cfg.buttons.ext///////
		var extBtns = [];
		var items = listMgr2.toolbarCustom.items.items;
		for(var i=0;i<items.length;i++){
			var item = items[i];
			extBtns.push(item.cfg);
		}
		extBtns.sort(function(x,y){//按照顺序号排序
			return x.order - y.order;
		});
		for(var i=0;i<extBtns.length;i++){
			extBtns[i].id = listPage__.formId + '_' + listPage__.listId + '_' + i;
			delete extBtns[i].sender;
		}
		
		cfg.buttons.ext=extBtns;
		
		//////cfg.searchSvr///////
		var searchSvr=[];
		items = listMgr2.gridServiceSearch.store.data.items;
		for(var i=0;i<items.length;i++){
			var item = items[i].data;
			searchSvr.push(item);
		}
		cfg.searchSvr = searchSvr;		
		
		return cfg;
	},
	btnSaveClick:function(){
		if(!this.checkAllFormIsValid()){
			Ext.Toast.show('输入未完成或验证不通过',{
			   title:'提示',
			   buttons: Ext.Msg.OK,
			   animEl: 'elId',
			   minWidth:420,
			   icon: Ext.MessageBox.WARNING
			});
			return;
		}
		
		var frm  = this.getAllFormValues();
		var cfg = listMgr2.collectListCfg(frm);
		if(cfg==null) return;
		
		Ext.getBody().mask("正在提交,请稍候...");
		Ext.Ajax.request({  
			url: 'listConfig!saveListConfig.jhtml',
			params:{
				formId:listPage__.formId,
				listId:listPage__.listId,
				nodeId:nodeId__,
				name:frm.txtTitle,
				config:Ext.encode(cfg)
			},  
			method:"POST",  
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
						icon: Ext.MessageBox.INFO,  
						time:1000,
						minWidth:420
					});
					//新建列表页状体啊， 跳转到编辑页
					if(listId__==0){
						location.href = 'listConfig!getListManage.jhtml?nodeId='+ nodeId__ +'&formId='+formId__+'&listId=' + ret.listId;
					}					
				}
			},
			failure:function(response,opts){
				Ext.getBody().unmask();
				Ext.Msg.show({
					   title:'错误提示',
					   msg: response.statusText,
					   buttons: Ext.Msg.OK,
					   animEl: 'elId',
					   minWidth:420,
					   icon: Ext.MessageBox.ERROR 
				});

			}		
		});	
	},
	btnPreviewClick:function(){
		if(!this.checkAllFormIsValid()){
			Ext.Toast.show('输入未完成或验证不通过',{
			   title:'提示',
			   buttons: Ext.Msg.OK,
			   animEl: 'elId',
			   minWidth:420,
			   icon: Ext.MessageBox.WARNING
			});
			return;
		}
		
		var frm  = this.getAllFormValues();
		var cfg = listMgr2.collectListCfg(frm);
		if(cfg==null) return;
		
		var tempform = document.createElement('form');
		tempform.setAttribute('method', 'post');
		tempform.setAttribute('target', '_blank');
		tempform.setAttribute('name', 'tempform');
		tempform.setAttribute('action', '../runtime/xlist!preview.jhtml');
		
		newinput = document.createElement('input');
		newinput.setAttribute('type','hidden');
		newinput.setAttribute('name','listCfgJSON');
		newinput.setAttribute('value',Ext.encode(cfg));
		tempform.appendChild(newinput);
		
		newinput = document.createElement('input');
		newinput.setAttribute('type','hidden');
		newinput.setAttribute('name','formId');
		newinput.setAttribute('value',listPage__.formId);
		tempform.appendChild(newinput);
		
		newinput = document.createElement('input');
		newinput.setAttribute('type','hidden');
		newinput.setAttribute('name','listId');
		newinput.setAttribute('value',listPage__.listId);
		tempform.appendChild(newinput);
		
		newinput = document.createElement('input');
		newinput.setAttribute('type','hidden');
		newinput.setAttribute('name','nodeId');
		newinput.setAttribute('value',nodeId__);
		tempform.appendChild(newinput);
		
		document.body.appendChild(tempform);
		tempform.submit();
		document.body.removeChild(tempform);
	},
	checkAllFormIsValid:function(){
		return this.baseCfgForm.getForm().isValid() && this.moreCfgForm.getForm().isValid() && this.listCfgForm.getForm().isValid() && this.searchCfgForm.getForm().isValid();
	},
	getAllFormValues:function(){
		var allFormValues = {};
		Ext.apply(allFormValues, this.baseCfgForm.getForm().getValues());
		Ext.apply(allFormValues, this.moreCfgForm.getForm().getValues());
		Ext.apply(allFormValues, this.listCfgForm.getForm().getValues());
		Ext.apply(allFormValues, this.searchCfgForm.getForm().getValues());
		//console.log('=============allFormValues');
		//console.log(allFormValues);
		return allFormValues;
	},
	reinit:function(){
		typeof this.mainViewport != 'undefined' && this.mainViewport.destroy();
		this.init();
	}
}// end listMgr2

</script>
<script>


Ext.onReady(function(){
	Ext.BLANK_IMAGE_URL = "../res/js/ext2/resources/images/default/s.gif";
	Ext.QuickTips.init();
	Ext.form.Field.prototype.msgTarget = 'side';
		
	listMgr2.init();
	
});	
</script>


</body>
</html>