<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <title>列表视图配置</title>
	<link rel="stylesheet" type="text/css" href="./../res/js/ext2/resources/css/ext-all.css" />
	<link rel="stylesheet" type="text/css" href="./../res/js/ext2/resources/css/patch.css" />
	<link rel="stylesheet" type="text/css" href="./../res/css/runTime.css" />
	
	<script type="text/javascript" src="./../res/js/ext2/adapter/ext/ext-base.js"></script>
    <script type="text/javascript" src="./../res/js/ext2/ext-all-debug.js"></script>
	<script type="text/javascript" src="./../res/js/ext_base_extension.js"></script> 
	<script type="text/javascript" src="./../res/js/editors/Ext.ux.CustomEditors.TextArea.js"></script>
	<script type="text/javascript" src="./../res/js/controls/Ext.ux.CheckboxGroup.js"></script>
	<script type="text/javascript" src="./../res/js/controls/Ext.ux.TextField.js"></script>
	<script type="text/javascript" src="./../res/js/ext2/ext-lang-zh_CN.js"></script>
	<style>
		body {font-size:14px;}
		input,select{font-size:12px}
		label {margin:0 2px 0 2px}
		textarea {font-size:12px;font-family:tahoma,arial,helvetica,sans-serif}
		.addField{background:url("./../res/js/ext2/resources/images/default/dd/drop-add.gif") left  no-repeat !important;}
		.delField{background:url("./../res/js/ext2/resources/images/default/my/del-form.gif") left  no-repeat !important;}
		.saveField{background:url("./../res/js/ext2/resources/images/default/my/save.gif") left  no-repeat !important;}
				
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

		.deleteButton{background:url("./../res/img/runTime/delete1.gif") left  no-repeat !important;width:16px;height:16px;cursor:pointer;}
		
		fieldset.x-panel-collapsed{
			border: none;
			padding-bottom: 0 !important;
		}
	</style>
<script>
		var listPage__=${listPageJson};
		var formConfig__= ${formConfig};	
		var viewConfig__ =  ${viewConfig};	
		var nodeId__=#{nodeId!0};
		//数据预处理
		var fields__ = [['','不绑定字段'],['id','ID']];
		var fieldsJson__={};
		for(var i=0;i<listPage__.fields.length;i++){
			var f = listPage__.fields[i];
			if(f[1]!=''){
				fields__.push([f[0],f[0] + '(' + f[1] + ')']);
			}else{
				fields__.push([f[0],f[0]]);	
			}
			fieldsJson__[f[0]] = f[1]||f[0];
		}
		var LPCFG = listPage__.config?listPage__.config:{};
		if(!LPCFG.pagesize) LPCFG.pagesize=20;
		if(!LPCFG.filter)LPCFG.filter ='';
		if(!LPCFG.sql)LPCFG.sql ='';
		if(!LPCFG.template)LPCFG.template ='';
		if(!LPCFG.myTemplate)LPCFG.myTemplate ='';
		if(!LPCFG.viewId)LPCFG.viewId =0;
		if(!LPCFG.searchableFields)LPCFG.searchableFields ={};
		
		if(!LPCFG.buttons)LPCFG.buttons ={
			add:true,
			modify:true,
			"delete":true,
			ext:[]
			
		};
		if(!LPCFG.menus)LPCFG.menus ={
			add:false,
			modify:true,
			"delete":true
			
		};		
		if(!LPCFG.search)LPCFG.search =[];
		if(!LPCFG.searchSvr)LPCFG.searchSvr =[];
		if(!LPCFG.columns)LPCFG.columns =[];
		//if(!LPCFG.customSearch)LPCFG.customSearch ={};
		
		var fieldsJson__ = {};
		for(var i=0;i<formConfig__.fieldsConfig.fieldsConfig.length;i++){
			var item = formConfig__.fieldsConfig.fieldsConfig[i];
			fieldsJson__[item.f_name] = item;
		}
		
		var opEnZh__=[
			['=','等于(=)'],
			['like','包含(like)'],
			['not like','不包含(not like)'],
			['>','大于(>)'],
			['>=','大于或等于(≥)'],
			['<','小于(<)'],
			['<=','小于或等于(≤)'],
			['<>','不等于(≠)']
		];
		var controlTypeEnZh__=[['textfield','文本框'],['combo','下拉框'],['radiogroup','单选框'],['datetimeextent','日期区间']];
		var andorEnZH__ ={
			'and':'并且','or':'或者'
		}
		
		
		Ext.grid.CheckColumn = function(config){
			Ext.apply(this, config);
			if(!this.id){
				this.id = Ext.id();
			}
			this.renderer = this.renderer.createDelegate(this);
		};

		Ext.grid.CheckColumn.prototype ={
			init : function(grid){
				this.grid = grid;
				this.grid.on('render', function(){
					var view = this.grid.getView();
					view.mainBody.on('mousedown', this.onMouseDown, this);
				}, this);
			},

			onMouseDown : function(e, t){
				if(t.className && t.className.indexOf('x-grid3-cc-'+this.id) != -1){
					e.stopEvent();
					var index = this.grid.getView().findRowIndex(t);
					var record = this.grid.store.getAt(index);
					record.set(this.dataIndex, !record.data[this.dataIndex]);
				}
			},

			renderer : function(v, p, record){
				p.css += ' x-grid3-check-col-td'; 
				return '<div class="x-grid3-check-col'+(v?'-on':'')+' x-grid3-cc-'+this.id+'">&#160;</div>';
			}
		};	
	function myZhuanyi(str){
		var ret = str;
		//ret = ret.replace(/&/g,'&amp;');
		ret = ret.replace(/"/g,'&quot;');
		ret = ret.replace(/>/g,'&gt;');
		ret = ret.replace(/</g,'&lt;');
		return ret;
	}	
	function myUnZhuanyi(str){
		var ret = str;
		ret = ret.replace(/&quot;/g,'"');
		ret = ret.replace(/&gt;/g,'>');
		ret = ret.replace(/&lt;/g,'<');
		//ret = ret.replace(/&amp;/g,'&');		
		return ret;
	}	

	function setActiveTab(url,tabId,title){
		if(top&& top.centerTabPanel){
			var _url = url;
			top.centerTabPanel.addIframe('tab_' + tabId,title ,_url);
		}else{
			window.open(url);	
		}
	}	
	</script>
	
</head>
<body>

<script>
var listMgr = {
	init:function(){
		new Ext.Viewport({
			layout:"fit",
			items:[{
				xtype:'form',
				id:'panelMain',
				style:'padding:10px 10px 10px 10px;overflow-x:hidden;',
				//bodyStyle:'padding:0 5px 10px 10px',
				itemCls:"itemStyle",
				layout:'xform',
				labelAlign :'right',
				labelWidth :110,
				autoScroll:true,
				items:[{
					xtype:'xTextField',
					fieldLabel:'列表名称',
					name:'txtTitle',
					anchor:'95%',
					value:listPage__.name?listPage__.name:''
				},{
					xtype:'xTextField',
					fieldLabel:'过滤条件(filter)',
					name:'txtFilter',
					anchor:'95%',
					emptyText:"eg. type='视频' and id>456",
					value:LPCFG.filter?LPCFG.filter:''
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
									if(this.mainForm)this.mainForm.find('name','txtPageCount')[0].setDisabled(checked);
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
				
				{//fieldLabel:'按钮设置',
					
					border:false,
					ctCls :"itemStyle2",
					layout:'column',
					items:[{
						layout: 'xform',
						border:false,
						width:180,
						items:[{
							xtype:'checkbox',
							fieldLabel:'按钮设置',
							boxLabel :'添加',
							name:'chkBtnAdd',
							checked :LPCFG.buttons.add==false?false:true
						}]
					},{
						layout: 'xform',
						border:false,
						hideLabels:true,
						width:65,
						items:[{
							xtype:'checkbox',
							boxLabel :'修改',
							name:'chkBtnModify',
							checked :LPCFG.buttons.modify==false?false:true
						}]
					},{
						layout: 'xform',
						border:false,
						hideLabels:true,
						width:65,
						items:[{
							xtype:'checkbox',
							boxLabel :'删除',
							name:'chkBtnDel',
							checked :LPCFG.buttons.delete==false?false:true
						}]
					},{
						layout: 'xform',
						border:false,
						autoWidth:true,
						labelWidth:110,
						items:[{
							xtype:'combo',
							fieldLabel:'依赖哪个表单视图?',
							labelSeparator:'',
							name:'selViewId',
							hiddenName:'selViewId',
							store: new Ext.data.JsonStore({
								fields : [{name : 'name'}, { name : 'viewId'}],
								data:viewConfig__
							}),
							displayField: 'name',
							valueField: 'viewId',
							typeAhead: true,
							mode: 'local',
							triggerAction: 'all',
							editable : false,
							readOnly:true,
							value: LPCFG.viewId?LPCFG.viewId:viewConfig__[0].viewId,
							width:160
						}]
					}]
				},
				
				{//fieldLabel:'右键菜单配置',
					
					border:false,
					ctCls :"itemStyle2",
					layout:'column',
					items:[{
						layout: 'xform',
						border:false,
						width:180,
						items:[{
							xtype:'checkbox',
							fieldLabel:'右键菜单配置',
							boxLabel :'添加',
							name:'chkMenuAdd',
							checked :LPCFG.menus.add==false?false:true
						}]
					},{
						layout: 'xform',
						border:false,
						hideLabels:true,
						width:65,
						items:[{
							xtype:'checkbox',
							boxLabel :'修改',
							name:'chkMenuModify',
							checked :LPCFG.menus.modify==false?false:true
						}]
					},{
						layout: 'xform',
						border:false,
						hideLabels:true,
						width:65,
						items:[{
							xtype:'checkbox',
							boxLabel :'删除',
							name:'chkMenuDel',
							checked :LPCFG.menus.delete==false?false:true
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
											var sender = listMgr.toolbarCustom.items.map[this.id];
											this.sender = sender;
											listMgr.showButtonCfgWin(this); 
										}
									},{
										text         : '查看运行日志',
										scope:extBtns[i],
										handler :function(){
											var sender = listMgr.toolbarCustom.items.map[this.id];
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
											var sender = listMgr.toolbarCustom.items.map[this.id];
											listMgr.toolbarCustom.items.removeKey(this.id);
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
								listMgr.showButtonCfgWin({
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
						width:270,
						items:[{
							xtype:'combo',
							fieldLabel:'模板',
							name:'selTpl',
							hiddenName:'selTpl',
							store: new Ext.data.JsonStore({
								fields : [{name : 'text'}, { name : 'value'}],
								data:[{
									text:'默认列表页模板1',
									value:'listTemplate_default_1'
								}]
							}),
							displayField: 'text',
							valueField: 'value',
							typeAhead: true,
							mode: 'local',
							triggerAction: 'all',
							editable : false,
							readOnly:true,
							value: LPCFG.template?LPCFG.template:'listTemplate_default_1',
							width:160,
							disable:LPCFG.myTemplate?true:false
						}]
					},{
						layout: 'xform',
						border:false,
						hideLabels:true,
						width:620,
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
				},
				//更多配置
				{
					xtype:'fieldset',
					ctCls :"itemStyle2",
					title:'更多配置>>(&lt;/head&gt;之前注入|Ext.onReady注入(JS)|&lt;/body&gt;之前注入)',
					collapsible :true,
					titleCollapse:true,
					collapsed:true,
					//height:550,
					autoHeight:true,
					style:'margin:10px;',
					//collapsedCls:'cmpp-x-panel-collapsed',
					layout:'xform',
					itemCls:"itemStyle",
					labelAlign :'right',
					labelWidth :110,
					autoScroll:true,
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
				},
				////////////////////////列表项配置//////////////////////////////
				{	
					xtype:'fieldset',
					ctCls :"itemStyle2",
					title:'列表项配置',
					collapsed:false,
					//height:350,
					anchor:'95%',
					autoHeight:true,
					collapsible :true,
					titleCollapse:true,
					style:'margin:10px;',
					autoScroll:true,
					layout:'fit',
					items:[
						this.initColumnSetting()
					]
				
				},
				////////////////////////字段项配置//////////////////////////////
				{	
					xtype:'fieldset',
					ctCls :"itemStyle2",
					title:'勾选字段',
					collapsed:false,
					//height:350,
					anchor:'95%',
					autoHeight:true,
					collapsible :true,
					titleCollapse:true,
					style:'margin:10px;',
					autoScroll:true,
					layout:'fit',
					items:[
						this.initNeedFieldsSetting()
					]
				
				},
				{	
					xtype:'fieldset',
					ctCls :"itemStyle2",
					title:'搜索配置',
					collapsed:false,
					//height:350,
					anchor:'95%',
					autoHeight:true,
					collapsible :true,
					titleCollapse:true,
					style:'margin:10px;',
					autoScroll:true,
					layout:'xform',
					items:[
					{	//给可搜索字段绑定控件、设置标题'
						xtype:'fieldset',
						ctCls :"itemStyle2",
						title:'给可搜索字段绑定控件、设置标题',
						collapsed:false,
						//anchor:'98%',
						autoHeight:true,
						collapsible :true,
						titleCollapse:true,
						//style:'margin:10px;',
						autoScroll:true,
						layout:'fit',
						items:[this.initBindCtrlSetting()]
					},
					{	//从数据库搜索的配置'
						xtype:'fieldset',
						ctCls :"itemStyle2",
						title:'启用从数据库搜索',
						//anchor:'98%',
						autoHeight:true,
						checkboxToggle :true,
						name:'chkSearchDb',
						checkboxName:'chkSearchDb',
						collapsed  :LPCFG.enableSearchDb==false?true:false,
						//style:'margin:10px;',
						autoScroll:true,
						layout:'xform',
						hideLabels:true,
						items:[
						{
							xtype:'checkbox',
							name:'chkEnableSearchHistory',
							boxLabel:'是否允许搜索历史数据?',
							checked :LPCFG.enableSearchHistory==false?false:true
						},
						{
							xtype:'fieldset',
							style:'padding: 0px;',
							border:false,
							autoHeight:true,
							//anchor:'98%',
							layout:'fit',
							items:[this.initDbSearchSetting()]
						}]
					},
					{	//从搜索服务搜索的配置'
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
						items:[
						{
							xtype:'xTextField',
							name:'filter4Svr',
							fieldLabel:'过滤条件(filter)',
							anchor:'50%',
							emptyText :"eg.{title:'风云',area:'大陆'}",//这个值设置必须用双引号，不能用单引号，否则提交时会把空置也提交上去
							fieldNote:'eg.{title:"风云",area:"大陆"}',
							value:LPCFG.filter4Svr
						},
						{
							xtype:'fieldset',
							style:'padding: 0px;',
							border:false,
							autoHeight:true,
							//anchor:'98%',
							layout:'fit',
							items:[this.initSvrSearchSetting()]
						}]
					}
					
					]
				
				},

				
				],
				buttons:[{
					text:'保存(Ctrl+S)',
					id:'btnOK',
					scope:this,
					handler:function(){
						this.btnSaveClick();
					}
				},{
					text:'关闭',
					id:'btnCancel',
					handler:function(){
						if(top && top.centerTabPanel){
							top.centerTabPanel.remove(top.centerTabPanel.getActiveTab());
						}else{
							window.open('','_parent','');
							window.close();
						}
					}
				}]
			}]
		});//viewport end
		this.mainForm = Ext.getCmp('panelMain');
		this.toolbarCustom=Ext.getCmp('toolbarCustom');
		
		//快捷键保存
		var docMap = new Ext.KeyMap(Ext.getDoc(), {
			key: 's',
			shift:false,
			ctrl:true,
			handler: function(key,e){
				e.preventDefault();
				listMgr.btnSaveClick();
			}
		});	
	},
	//初始化列表项配置
	initColumnSetting:function(){

		//初始化 列表项配置
		var store_col;
		
		var fieldColumn = Ext.data.Record.create([
		   {name: 'title', type: 'string'},
		   {name: 'tpl', type: 'string'},
		   {name: 'field', type: 'string'},  
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
				header: "字段名",
				dataIndex: 'field',
				width: 200,
				renderer:function(v,p,record,rowIndex,colIndex,data ){						
					var text = v;
					var combo = Ext.getCmp('comboField');
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
					id:"comboField",
					triggerAction: 'all',
					editable:false,
					mode: 'local',
					store:new Ext.data.SimpleStore({　
					　　fields:['value','text'],　
					　　data:fields__
					}),
					valueField : 'value',//值
					displayField : 'text',//显示文本
					listClass: 'x-combo-list-small'
				})
			},{
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
				fields: ["title","tpl","field","isView","width","isShowTip","tipTpl","align","isWidthFree"]
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
			id:'gridColumnSetting',
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
			width:700,
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
				},
				afteredit:function(e){
					if(e.field=='field'){
						var rec = e.record.store.data.items[e.row];
						//var rec = e.record;
						if(e.value){
							var text = fieldsJson__[e.value];
							rec.data.title = text;
							rec.data.tpl = '{'+ e.value +'}';
							e.grid.getView().refresh();
						}
					}
				}
			}

			
		});
		
		// trigger the data store load
		store_col.load();
		this.grid_col = grid_col;
		return grid_col;
	},
	//初始化需要的字段配置
	initNeedFieldsSetting:function(){
		//listPage__.fields
		var ds_fields = [['id','id']];
		for(var i=0;i<listPage__.fields.length;i++){
			ds_fields.push([listPage__.fields[i][0],listPage__.fields[i][0]]);
		}
		var panel_needFields = new Ext.Panel({
			title:'请选择上面列模板和Tip模板使用到的字段',
			autoHeight:true,
			items:[{
				xtype:'checkboxgroup',
				id:'chkgp_needFields',
				name:'chkgp_needFields',
				width: 300,
				dataSource: ds_fields,
				value: LPCFG.mustReturnFields?LPCFG.mustReturnFields:null
			}]
		});
		return panel_needFields;
	},
	//给可搜索字段绑定控件	初始化			
	initBindCtrlSetting:function(){
		//初始化 列表项配置
		var store;

		var chViewInDbSearchColumn = new Ext.grid.CheckColumn({
			header: "在数据库搜索中显示吗?",
			dataIndex: 'viewInDbSearch',
			width: 140,
			sortable :true,
			renderer:function(v,p,record){
				p.css += ' x-grid3-check-col-td'; 
				if(v==-1)
					return '';
				else
					return '<div class="x-grid3-check-col'+(v?'-on':'')+' x-grid3-cc-'+this.id+'">&#160;</div>';
			}
		});
		var chViewInServiceSearchColumn = new Ext.grid.CheckColumn({
			header: "在服务搜索中显示吗?",
			dataIndex: 'viewInServiceSearch',
			width: 140,
			sortable :true,
			renderer:function(v,p,record){
				p.css += ' x-grid3-check-col-td'; 
				if(v==-1)
					return '';
				else
					return '<div class="x-grid3-check-col'+(v?'-on':'')+' x-grid3-cc-'+this.id+'">&#160;</div>';
			}
		});
		var chSortablDbSearchColumn = new Ext.grid.CheckColumn({
			header: "在数据库搜索排序栏显示吗?",
			dataIndex: 'sortableDb',
			width: 140,
			sortable :true,
			renderer:function(v,p,record){
				p.css += ' x-grid3-check-col-td'; 
				if(v==-1)
					return '';
				else
					return '<div class="x-grid3-check-col'+(v?'-on':'')+' x-grid3-cc-'+this.id+'">&#160;</div>';
			}
		});		
		var chSortablSvrSearchColumn = new Ext.grid.CheckColumn({
			header: "在服务搜索排序栏显示吗?",
			dataIndex: 'sortableSvr',
			width: 140,
			sortable :true,
			renderer:function(v,p,record){
				p.css += ' x-grid3-check-col-td'; 
				if(v==-1)
					return '';
				else
					return '<div class="x-grid3-check-col'+(v?'-on':'')+' x-grid3-cc-'+this.id+'">&#160;</div>';
			}
		});	
		var cm = new Ext.grid.ColumnModel([
			new Ext.grid.CheckboxSelectionModel({singleSelect :false}),
			new Ext.grid.RowNumberer(),
			{
				header: "字段名",
				dataIndex: 'field',
				sortable :true,
				width: 130
			},{
				id:'title',
				header: "字段标题",
				dataIndex: 'title',
				width:140,
				sortable :true,
				editor: new Ext.form.TextField({
				   allowBlank: true
				})
			},chViewInDbSearchColumn,chViewInServiceSearchColumn,chSortablDbSearchColumn,chSortablSvrSearchColumn,
			{
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
					　　data:[['json','json'],['sql','sql'],['url','url']]
					}),
					valueField : 'value',//值
					displayField : 'text',//显示文本
					listClass: 'x-combo-list-small'
				})
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
			},{
				header: "自定义HTML",
				align:'center',
				//width: 60,
				dataIndex:'html',
				editor:  new Ext.ux.CustomEditors.TextArea({}),
				renderer:function(v,p,record){
					if(v) return "点击编辑";
					else return "";
				}
			}
			
		]);
		cm.allowSort = true;
		
		// create the Data Store
		var allSearchable = [];
		var dbs =[];
		var searchableArr=[];
		Ext.apply(dbs,formConfig__.fieldsConfig.searchable);
		Ext.apply(dbs,formConfig__.searchConfig.searchable);
		
		var schFlds = LPCFG.searchableFields;
		for(var i =0;i<dbs.length;i++){
			var fld = dbs[i];
			var viewInDbSearch = formConfig__.fieldsConfig.searchable.indexOf(fld)==-1?-1:(schFlds[fld]&&schFlds[fld].viewInDbSearch==false?false:true);
			var viewInServiceSearch = formConfig__.searchConfig.searchable.indexOf(fld)==-1?-1:(schFlds[fld]&&schFlds[fld].viewInServiceSearch==false?false:true);
			var sortableDb = formConfig__.fieldsConfig.sortable.indexOf(fld)==-1?-1:(schFlds[fld]&&schFlds[fld].sortableDb==false?false:true);
			var sortableSvr = formConfig__.searchConfig.sortable.indexOf(fld)==-1?-1:(schFlds[fld]&&schFlds[fld].sortableSvr==false?false:true);
			searchableArr.push({
				field:fld,
				title:schFlds[fld]?schFlds[fld].title:fieldsJson__[fld].f_title,
				ctrl:schFlds[fld]?schFlds[fld].ctrl:'',
				viewInDbSearch:viewInDbSearch,
				viewInServiceSearch:viewInServiceSearch,
				sortableDb:sortableDb,
				sortableSvr:sortableSvr,
				dataSourceType:schFlds[fld]?schFlds[fld].dataSourceType:'',
				dataSource:schFlds[fld]?schFlds[fld].dataSource:'',
				html:schFlds[fld]?schFlds[fld].html:''
			});
		}
		this.searchableArr = searchableArr;
		store= new Ext.data.Store({
			proxy: new Ext.data.MemoryProxy(searchableArr), 
			reader : new Ext.data.JsonReader({
				autoLoad:true,
				fields: ["field","title","viewInDbSearch","viewInServiceSearch","sortableDb","sortableSvr","ctrl","dataSourceType","dataSource","html"]
			}),
			listeners: { 
				load : function(t, records,options) {
					for(var i=0;i<records.length;i++){
						if(records[i].get('html'))
							records[i].set('html',myUnZhuanyi(records[i].get('html')));
					}
				}
			}
		});

		// create the editor grid_col
		var grid = new Ext.grid.EditorGridPanel({
			id:'gridBindCtrlSetting',
			store: store,
			frame:false,
			hideBorders:true,
			enableColumnHide:false,
			stripeRows :true,//表格行颜色间隔显示
			cm: cm,
			sm: new Ext.grid.CheckboxSelectionModel({singleSelect :false}),
			autoHeight:true,
			width:700,
			autoExpandColumn:'comboDataSource',
			enableHdMenu: false,
			dropConfig: {appendOnly:false},
			trackMouseOver : true,
			enableDragDrop: true,	
			ddGroup: "GridDD",	
			clicksToEdit:1,
			plugins:[chViewInDbSearchColumn,chViewInServiceSearchColumn,chSortablDbSearchColumn,chSortablSvrSearchColumn],
			listeners:{
				render:function(grid){
					//拖拽排序
					var ddrow = new Ext.dd.DropTarget(grid.container, { 
						ddGroup : 'GridDD', 
						copy : false, 
						notifyDrop : function(dd, e, data) { 
							//var rows = data.selections;
							var sm = grid.getSelectionModel(); 
							var rows = sm.getSelections(); 
							var store = grid.getStore();
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
									grid.getView().refresh();
								} 
							}
						} 
					});
				}
			}

			
		});
		
		// trigger the data store load
		store.load();
		this.grid_bindCtrl = grid;
		return grid;
	},
	//初始化从数据库搜索的配置
	initDbSearchSetting:function(){

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
		
		var searchableArr =[{'field':'',title:'未指定'}];
		var len = this.grid_bindCtrl.store.getCount();
		for(var i=0;i<len;i++){
			var item = this.grid_bindCtrl.store.getAt(i).data;
			if(item.viewInDbSearch==true){
				searchableArr.push(item);
			}
		}		

		var cm = new Ext.grid.ColumnModel([
			new Ext.grid.RowNumberer(),
			{
				header: "字段名",
				dataIndex: 'field',
				width: 200,
				renderer:function(v,p,record,rowIndex,colIndex,data ){						
					var text = v;
					var combo = Ext.getCmp('comboField2');
					if(combo.valueField){
						var r = combo.findRecord(combo.valueField, v);
						if(r){
							text = r.data[combo.displayField];
						}else if(combo.valueNotFoundText !== undefined){
							text = combo.valueNotFoundText;
						}
					}
					if(!v) return '未指定';
					else return text+'('+ v +')';  //显示显示值
				},			
				editor: new Ext.form.ComboBox({
					id:"comboField2",
					triggerAction: 'all',
					editable:false,
					mode: 'local',
					store: new Ext.data.JsonStore({
						fields : [{name : 'field'}, { name : 'title'}],
						data:searchableArr
					}), 
					valueField : 'field',//值
					displayField : 'title',//显示文本
					listClass: 'x-combo-list-small'
				})
			},{
				id:'title',
				header: "字段标题",
				dataIndex: 'title',
				editor: new Ext.form.TextField({
					
				})
			},{
				id:'op',
				header: "运算符",
				dataIndex: 'op',
				width: 200,
				renderer:function(v,p,record,rowIndex,colIndex,data ){						
					var text = v;
					var combo = Ext.getCmp('comboOp');
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
					id:"comboOp",
					triggerAction: 'all',
					editable:false,
					mode: 'local',
					store:new Ext.data.SimpleStore({　
					　　fields:['value','text'],　
					　　data:opEnZh__
					}),
					valueField : 'value',//值
					displayField : 'text',//显示文本
					listClass: 'x-combo-list-small'
				})
			},chkEnableColumn
			
		]);
		cm.allowSort = true;
		
		// create the Data Store
		//初始化搜索项配置
		var sItems = [];
		len = searchableArr.length>6?6:searchableArr.length;
		for(var i=0;i<len;i++){
			var item = LPCFG.search[i];
			if(item){
				var title = item.title;
				if(!title){
					var row = this.grid_bindCtrl.store.find('field',item.field);
					if(row!=-1) title= this.grid_bindCtrl.store.getAt(row).data.title;
				}
				sItems.push({
					field:item.field,
					title:title,
					op:item.op,
					andor:item.andor,
					enable:item.enable==false?false:true
				});
			}else{
				sItems.push({
					field:'',
					title:'',
					op:'like',
					andor:'and',
					enable:i<3?true:false
				})	;
			}
		}
		
		store= new Ext.data.Store({
			proxy: new Ext.data.MemoryProxy(sItems), 
			reader : new Ext.data.JsonReader({
				autoLoad:true,
				fields: ["field","title","op","andor","enable"]
			})
		});

		// create the editor grid_col
		var grid = new Ext.grid.EditorGridPanel({
			id:'gridColumnSetting',
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
			ddGroup: "GridDD2",	
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
						ddGroup : 'GridDD2', 
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
							var item = LPCFG.searchableFields[e.value] ;
							var text = item && item.title ? item.title:fieldsJson__[e.value].f_title;
							rec.data.title = text;
						}else{
							rec.data.title = '';
						}
						e.grid.getView().refresh();
					}
				}
			}

			
		});
		
		// trigger the data store load
		store.load();
		this.gridDbSearch = grid;
		return grid;
		
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
		
		var searchableArr =[{'field':'',title:'未指定'}];
		var len = this.grid_bindCtrl.store.getCount();
		for(var i=0;i<len;i++){
			var item = this.grid_bindCtrl.store.getAt(i).data;
			if(item.viewInServiceSearch==true){
				searchableArr.push(item);
			}
		}		

		var cm = new Ext.grid.ColumnModel([
			new Ext.grid.RowNumberer(),
			{
				header: "字段名",
				dataIndex: 'field',
				width: 200,
				renderer:function(v,p,record,rowIndex,colIndex,data ){						
					var text = v;
					var combo = Ext.getCmp('comboField3');
					if(combo.valueField){
						var r = combo.findRecord(combo.valueField, v);
						if(r){
							text = r.data[combo.displayField];
						}else if(combo.valueNotFoundText !== undefined){
							text = combo.valueNotFoundText;
						}
					}
					if(!v) return '未指定';
					else return text+'('+ v +')';  //显示显示值
				},			
				editor: new Ext.form.ComboBox({
					id:"comboField3",
					triggerAction: 'all',
					editable:false,
					mode: 'local',
					store: new Ext.data.JsonStore({
						fields : [{name : 'field'}, { name : 'title'}],
						data:searchableArr
					}), 
					valueField : 'field',//值
					displayField : 'title',//显示文本
					listClass: 'x-combo-list-small'
				})
			},{
				id:'title',
				header: "字段标题",
				dataIndex: 'title',
				editor: new Ext.form.TextField({
					
				})
			},{
				id:'op',
				header: "运算符",
				dataIndex: 'op',
				width: 200,
				renderer:function(v,p,record,rowIndex,colIndex,data ){						
					var text = v;
					var combo = Ext.getCmp('comboOp2');
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
					id:"comboOp2",
					triggerAction: 'all',
					editable:false,
					mode: 'local',
					store:new Ext.data.SimpleStore({　
					　　fields:['value','text'],　
					　　data:opEnZh__
					}),
					valueField : 'value',//值
					displayField : 'text',//显示文本
					listClass: 'x-combo-list-small'
				})
			},chkEnableColumn
			
		]);
		cm.allowSort = true;
		
		// create the Data Store
		//初始化搜索项配置
		var sItems = [];
		len = searchableArr.length>6?6:searchableArr.length;
		for(var i=0;i<len;i++){
			var item = LPCFG.search[i];
			if(item){
				var title = item.title;
				if(!title){
					var row = this.grid_bindCtrl.store.find('field',item.field);
					if(row!=-1) title= this.grid_bindCtrl.store.getAt(row).data.title;
				}
				sItems.push({
					field:item.field,
					title:title,
					op:item.op,
					andor:item.andor,
					enable:item.enable==false?false:true
				});
			}else{
				sItems.push({
					field:'',
					title:'',
					op:'like',
					andor:'and',
					enable:i<3?true:false
				})	;
			}
		}
		
		store= new Ext.data.Store({
			proxy: new Ext.data.MemoryProxy(sItems), 
			reader : new Ext.data.JsonReader({
				autoLoad:true,
				fields: ["field","title","op","andor","enable"]
			})
		});

		// create the editor grid_col
		var grid = new Ext.grid.EditorGridPanel({
			id:'gridColumnSetting',
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
		
		if(listMgr.buttonCfgWin==null){
			listMgr.buttonCfgWin = new Ext.Window({
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
									setActiveTab('scriptdebug.jhtml?nodeId='+nodeId__ +'&id1='+ listPage__.formId+'&id2='+ listMgr.buttonCfgWin.btnId +'&stype=form','formmanage_' + listMgr.buttonCfgWin.btnId,'调试脚本');
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
						var frm = listMgr.buttonCfgWin.find('id','frmBtnCfg')[0];
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
									var sender = listMgr.toolbarCustom.items.map[this.id];
									this.sender = sender;
									listMgr.showButtonCfgWin(this); 
								}
							},{
								text         : '删除',
								iconCls:'delField',
								scope:values,
								handler :function(){
									var sender = listMgr.toolbarCustom.items.map[this.id];
									listMgr.toolbarCustom.items.removeKey(this.id);
									sender.el.fadeOut({
										endOpacity: 0, 
										easing: 'easeOut',
										duration: .5,
										remove: true
									});
								}
							}];
							listMgr.toolbarCustom.add(btnCfg);
							
							
						}else{
							this.sender.setText(values.text);
							this.sender.setIconClass(values.iconCls);
							this.sender.cfg = values;
						}
						listMgr.buttonCfgWin.hide();
					}
				},{
					text:'取消',
					handler:function(){
						listMgr.buttonCfgWin.hide();
					}
				}]
			});
		}
		listMgr.buttonCfgWin.buttons[0].scope =opts;
		if(sender) listMgr.buttonCfgWin.setAnimateTarget(sender.el);
		listMgr.buttonCfgWin.show();
		
		//setValues
		var cfg = sender.cfg;
		if(cfg && typeof(cfg)!='undefined'){
			var frm = listMgr.buttonCfgWin.find('id','frmBtnCfg')[0];
			frm.form.setValues(cfg);
			frm.find('name','isMenuItem')[0].setValue(cfg.isMenuItem?true:false);
		}else{
			var frm = listMgr.buttonCfgWin.find('id','frmBtnCfg')[0];
			//frm.find('name','text')[0].setValue('');
			frm.find('name','iconCls')[0].setValue('');
			frm.find('name','js')[0].setValue('');
			frm.find('name','script')[0].setValue('');
			frm.find('name','afterjs')[0].setValue('');
			frm.find('name','isMenuItem')[0].setValue(false);
			frm.find('name','order')[0].setValue(listMgr.toolbarCustom.items.length+1);
			
			listMgr.buttonCfgWin.setTitle('自定义按钮配置');
		}
	},
	btnSaveClick:function(){//保存
		var form = this.mainForm.getForm();
		var frm  = form.getValues();
		var cfg={
			viewId:frm.selViewId,
			filter:frm.txtFilter,
			filter4Svr:frm.filter4Svr,
			template:frm.selTpl,
			pagesize:frm.txtPageCount?frm.txtPageCount:22,
			autoPagesize:frm.chkAutoPagesize?true:false,
			headInject:frm.txtInjectHead,
			bodyInject:frm.txtInjectBody,	
			extOnReadyJs:frm.txtExtOnReadyJs,
			isWidthFreeRow:-1,
			myTemplate:frm.chkUserMyTpl?frm.txtTpl:'',
			mustReturnFields:[],
			searchableFields:{},//字段绑定控件配置
			enableSearchHistory:frm.chkEnableSearchHistory?true:false,
			buttons:{
				"add":frm.chkBtnAdd?true:false,
				"delete":frm.chkBtnDel?true:false,
				"modify":frm.chkBtnModify?true:false,
				"ext":[]
			},
			menus:{
				"add":frm.chkMenuAdd?true:false,
				"delete":frm.chkMenuDel?true:false,
				"modify":frm.chkMenuModify?true:false,
			},
			columns:[],
			enableSearchDb:frm.chkSearchDb?true:false,
			enableSearchSvr:frm.chkSearchService?true:false,
			search:[],
			searchSvr:[]
		};
		if(!Ext.isArray(frm.chkgp_needFields))
			cfg.mustReturnFields = frm.chkgp_needFields.split(',');
		
		////////cfg.fieldsCtrls///////////////
		var fieldsCtrls={};
		var items = listMgr.grid_bindCtrl.store.data.items;
		for(var i=0;i<items.length;i++){
			var item = items[i].data;
			item.f_type = fieldsJson__[item.field].f_type;
			fieldsCtrls[item.field] = item;	
		}
		cfg.searchableFields = fieldsCtrls;
		
		
		
		////////cfg.columns///////////////
		//todo获得自由宽度的列
		var radios = Ext.query('input[name="isWidthFree"]');
		var isWidthFreeRow=-1;
		var columns = [];
		items = listMgr.grid_col.store.data.items;
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
		var items = listMgr.toolbarCustom.items.items;
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
		
		//////cfg.search///////
		var search=[];
		items = listMgr.gridDbSearch.store.data.items;
		for(var i=0;i<items.length;i++){
			var item = items[i].data;
			search.push(item);
		}
		cfg.search = search;
		
		//////cfg.searchSvr///////
		var searchSvr=[];
		items = listMgr.gridServiceSearch.store.data.items;
		for(var i=0;i<items.length;i++){
			var item = items[i].data;
			searchSvr.push(item);
		}
		cfg.searchSvr = searchSvr;		
		
		if(!form.isValid()){
			Ext.Toast.show('输入未完成或验证不通过',{
			   title:'提示',
			   buttons: Ext.Msg.OK,
			   animEl: 'elId',
			   minWidth:420,
			   icon: Ext.MessageBox.WARNING
			});
			return;
		}
		
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
	}

	

}


</script>
<script>


Ext.onReady(function(){
	Ext.BLANK_IMAGE_URL = "../res/js/ext2/resources/images/default/s.gif";
	Ext.QuickTips.init();
	Ext.form.Field.prototype.msgTarget = 'side';
		
	listMgr.init();
	
});	
</script>


</body>
</html>