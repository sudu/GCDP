 <!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <title>脚本库管理</title>
	<link rel="shortcut icon" href="../res/img/favicon.ico" />
	<link rel="stylesheet" type="text/css" href="../res/js/ext2/resources/css/ext-all.css" />
	<link rel="stylesheet" type="text/css" href="../res/js/ext2/resources/css/patch.css" />
	<link rel="stylesheet" type="text/css" href="../res/css/runTime.css?20130509" />
 	<script type="text/javascript" src="../res/js/ext2/adapter/ext/ext-base.js"></script>
    <script type="text/javascript" src="../res/js/ext2/ext-all-debug.js"></script>	
	<script type="text/javascript" src="../res/js/ext2/ext-lang-zh_CN.js"></script>
	<script type="text/javascript" src="../res/js/ext_base_extension.js?20130528"></script>
	<script type="text/javascript" src="../res/js/controls/Ext.ux.form.LovCombo.js"></script>
	<script type="text/javascript" src="../res/js/controls/Ext.ux.ScriptEditor.js"></script>
	<script type="text/javascript" src="../res/js/lib/editArea/edit_area_loader.js"></script>

	<style>

		.extAdd{background:url("../res/js/ext2/resources/images/default/dd/drop-add.gif") left  no-repeat !important;}
		/*按钮*/
		.addField{background:url("../res/js/ext2/resources/images/default/dd/drop-add.gif") left  no-repeat !important;}
		.delField{background:url("../res/js/ext2/resources/images/default/my/del-form.gif") left  no-repeat !important;}
		.modifyField{background:url("../res/js/ext2/resources/images/default/my/modify-view.gif") left  no-repeat !important;}
	    /*
		 * Ext.ux.form.LovCombo CSS File
		 */
		.ux-lovcombo-icon {
			width:16px;
			height:16px;
			float:left;
			background-position: -1px -1px ! important;
			background-repeat:no-repeat ! important;
		}
		.ux-lovcombo-icon-checked {
			background: transparent url(../res/js/ext2/resources/images/default/menu/checked.gif);
		}
		.ux-lovcombo-icon-unchecked {
			background: transparent url(../res/js/ext2/resources/images/default/menu/unchecked.gif);
		}
		/* eof */
		
		.itemStyle {
		    padding:8px;
		    /*border: 1px solid silver;*/
		    margin-bottom:0;
		}
		/*formPanel右侧输入提示*/
		.x-form-item-note{float:right;padding:3px;padding-right:0;clear:right;z-index:2;position:relative;}
		
		/* 表格gridPanel */
		.x-grid3-cell-inner, .x-grid3-hd-inner {
			overflow: hidden;
			padding: 5px 3px 5px 5px;
			text-overflow: ellipsis;/*ellipsis clip*/
			white-space: nowrap;
		}
	</style>	
	
	<script type="text/javascript">
		var formId__= Ext.parseQuery().formId;;
		var nodeId__= Ext.parseQuery().nodeId;;
		
		function setActiveTab(url,title){
			var tabId = 'tab_' + escape(url);
			if(top&& top.centerTabPanel){
				top.centerTabPanel.addIframe(tabId,title ,url,window);
			}else{
				window.open(url);	
			}
		}
	</script>	

<script>
var bindFunc = function(obj,func){
	return function(){
		func.apply(obj,arguments);
	};
};
var addEventHandler=function(obj, type, func) {
		if(!obj){return;}
		var doOn=function(o){
			if(o.addEventListener){o.addEventListener(type, func, false);}
			else if(o.attachEvent){o.attachEvent("on" + type, func);}
			else{o["on" + type] = func;}
		}
		var IsArray=function(v){
			try{
				var a = v[0];
				return typeof(a) != "undefined";
			}catch(e){
				return false;
			}
		}
		if(obj.tagName!='SELECT'&&IsArray(obj)){
			for(var i=0,oLen=obj.length;i<oLen;i++){
				doOn(obj[i],type.func);
			}
		}else{
			doOn(obj);
		}
};
var getEvent=function(e){
	var event=e||window.event;
	if(event){return event.srcElement||event.target;}
}
function myZhuanyi(str){
		var ret = str;
		//ret = ret.replace(/&/g,'&amp;');
		ret = ret.replace(/"/g,'&quot;');
		ret = ret.replace(/>/g,'&gt;');
		ret = ret.replace(/</g,'&lt;');
		return ret;
	}
</script>	
</head>
<body>	
<script type="text/javascript">
var scriptMgr ={

	comboForm:null,
	init:function(){
		this.initUI();
		this.leftPanel = Ext.getCmp('leftPanel');
		this.rightPanel = Ext.getCmp('rightPanel');
		this.comboForm = Ext.getCmp('comboForm');
		gridMgr.init();
		gridCommonMgr.init();
	},
	initUI:function(){
		new Ext.Viewport({
			layout : 'border', 
			autoScroll:true,
			items:[{
				xtype:'panel',
				region:'center',
				layout:'anchor',
				//height:400,
				id:'leftPanel',
				border:true,
				tbar:formId__?null:[{
					xtype:'lovcombo',
					id:'comboForm',
					style:'margin-left:5px;',
					width:200,
					maxHeight:350,
					allowBlank : true, 
					readOnly : false,  
					hideOnSelect:false,	
					mode : "local",   
					emptyText:'列出属于下列所选表单的脚本库',		
					triggerAction: 'all',
					//checkField:'checked',
					store : new Ext.data.JsonStore({ //填充的数据
						url : "source!add.jhtml?nodeId=" + nodeId__,
						fields : ['title', 'id'],
						root : 'formlist',
						sortInfo : {field: "id", direction: "ASC"} ,
						autoLoad :true,
						listeners:{
							"load":function(store,records,options){
								
								store.insert(0,new Ext.data.Record({title:'==全部==',id:0}));
							}
						}
					}),
					valueField : 'id',
					displayField : 'title'
				},{
					text:'确定',
					style:'margin-left:5px',
					scope:this,
					handler:function(){
						var formIds =  this.comboForm.getValue() ;
						if(!formIds) return;
						formIdArr = formIds.split(',');
						var formIdstr='';
						for(var i=0;i<formIdArr.length;i++){
							formIdArr[i] ='frm' + formIdArr[i] + '.'
						}
						
						formIdstr= formIdArr.join(',');
						gridMgr.store.clearFilter();
						if(formIdstr.indexOf('frm0.')!=-1){
							gridMgr.store.load();
						}else{
							//gridMgr.store.baseParams['ss.name'] =formIdstr;
							gridMgr.store.load({
								params :{'ss.name':formIdstr}
							});//全部
						}
						
					}
				}]
			},{
				xtype:'panel',
				region:'east',
				width:380,
				layout:'anchor',
				id:'rightPanel',
				title:'公共脚本库',
				border:false,
				split:true,
				collapsible: true,
			}]
		});
		
	}

}

var gridMgr = {
	grid:null,
	column:null,
	pagerBar:null,
	store:new Ext.data.Store({ 
		proxy : new Ext.data.HttpProxy({url : 'script!getCommonScriptList.jhtml',method:'post'}), 
		sortInfo : {field: "name", direction: "ASC"} ,
		reader : new Ext.data.JsonReader({
			autoLoad:true,
			//root : "data",
			fields: ['id','name','createDateStr','creator','intro']
		}),
		remoteSort: false
	}),
	toolbarArr :null,
	txtSearch:null,
	init:function(){		
		this.toolbarArr = [{ 
			text : "增加", 
			iconCls : 'addField', 
			scope:this,
			handler : this.addRecord 

		}, { 
			text : "修改", 
			iconCls : 'modifyField', 
			scope:this,
			handler : this.updateRecord 
		}, { 
			text : "删除", 
			iconCls : 'delField', 
			scope:this,
			handler : this.deleteRecord 
		},{
			xtype:'tbfill'
		},{
			xtype:'textfield',
			id:'txtSearch',
			width:250,
			emptyText:'输入名称进行模糊过滤',
			enableKeyEvents:true,
			style:'background:url(../res/img/search.gif) 230px 1px no-repeat;margin:0 5px;padding-right:20px;border-radius:10px;',
			listeners:{
				scope:this,
				'specialkey':function(obj,e){
					if(e.getCharCode()==Ext.EventObject.ENTER){
						var searchtxt=obj.getValue().replace(/\s/ig,'');
						this.search(e,searchtxt);
					}
				}
			}
			
		}];	
		this.initColumn();
		this.initGrid();
		
		this.txtSearch = Ext.getCmp('txtSearch');
		if(this.txtSearch){
			addEventHandler(this.txtSearch.getEl().dom,'keyup',bindFunc(this.txtSearch.getEl().dom,function(e){
				if(gridMgr.timeoutId) clearTimeout(gridMgr.timeoutId);
				gridMgr.timeoutId = setTimeout(gridMgr.search,50);
			}));
		}	
	},
	initGrid:function(){
		var grid = new Ext.grid.GridPanel({ 
			stripeRows: true,　　//隔行换色
			loadMask: { msg: '正在加载数据，请稍侯……' },
			columnLines: true,　　//显示列线条
			anchor:'100%',
			store: this.store,
			cm: this.column,
			trackMouseOver:true,
			stripeRows: true,
			sm: new Ext.grid.CheckboxSelectionModel(),
			loadMask: true,
			autoSizeColumns : true,
			autoScroll:true, 
			height:scriptMgr.leftPanel.body.getHeight(),
			viewConfig: {
				forceFit:true,
				enableRowBody:true
			},
			iconCls:'icon-grid',
			layout :'fit',
			frame:false,
			tbar : this.toolbarArr
		}); 
		this.grid = grid;
		
		scriptMgr.leftPanel.add(grid);
		scriptMgr.leftPanel.doLayout();

		this.store.baseParams.nodeId =nodeId__;
		if(formId__) this.store.baseParams["ss.name"] = 'frm' + formId__ + '.';
		
		this.store.load();
	},
	initColumn:function(){
		var cm = new Ext.grid.ColumnModel([
			new Ext.grid.CheckboxSelectionModel({width:18}),{
			   header: "ID",
			   dataIndex: 'id',
			   menuDisabled: true,
			   sortable:true,
			   width:32,
			   renderer:function(value, metadata, record){
					 return '<div style="width:'+ 30 +'px;overflow:hidden;">'+ value +'</div>';
				}
			},{
			    header: "名称",
			    dataIndex: 'name',
			    menuDisabled: true,
				sortable:true,
				width:130,
			    renderer:function(value, metadata, record){
					 return '<div style="width:'+ 128 +'px;overflow:hidden;" title="'+ value +'">'+ value +'</div>';
				}

			},{
			   header: "创建者",
			   menuDisabled: true,
			   sortable:true,
			   dataIndex: 'creator',
			   width:120,
				renderer:function(value, metadata, record){
					 return '<div style="width:'+ 118 +'px;overflow:hidden;" title="'+ value +'">'+ value +'</div>';
				}
			},{
			   header: "创建时间",
			   menuDisabled: true,
			   sortable:true,
			   dataIndex: 'createDateStr',
			    width: 120,
			   renderer:function(value, metadata, record){
					 return '<div style="width:'+ 118 +'px;overflow:hidden;" title="'+ value +'">'+ value +'</div>';
				}
			  
			},{
			   header: "功能描述",
			   menuDisabled: true,
			   sortable:false,
			   dataIndex: 'intro',
			   renderer:function(value, metadata, record){
					var text = value;
					var displayHtml=text;
					var style = "";
					var divTitle='';

					var tip = value;
					metadata.attr = 'ext:qtitle=""' + ' ext:qtip="' + tip + '"'; 
					displayHtml= '<div style="width:'+ 150 +'px;overflow:hidden;">'+ text +'</div>';
					return displayHtml;
					
				}
			},{
				hander:'',
				menuDisabled: true,
				dataIndex:'id',
				renderer:function(value, metadata, record){
					return '<a href="javascript:gridMgr.updateRecord()" style="margin-right:5px">修改</a><a href="javascript:gridMgr.deleteRecord()">删除</a>';
				}
			}
		]);
		this.column = cm;
	},
	openWin:function(data){
		var win = new Ext.Window({
			title:'脚本窗口',
			height:478,
			width:525, 
			buttonAlign: "center",
			closable:true ,
			closeAction:'close',
			autoScroll:true,
			modal:false,
			layout:'fit',
			resizable :false,
			defaultButton:'btnCfgOK',
			items:[{
				xtype:'form',
				layout:'xform',
				itemCls:'itemStyle',
				labelAlign:'right',
				labelWidth:70,
				autoScroll:true,
				items:[{
					xtype:'hidden',
					name:'ss.id',
					value:data?data.id:0
				},data?{
					xtype:'textfield',
					fieldLabel:'名称',
					width:120,
					readOnly:true,
					style:'background:transparent;border:none;color:#666666',
					value:data.name
				}:{
					xtype:'textfield',
					fieldLabel:'名称',
					name:'ss.name',
					width:120,
					allowBlank :false,
					value:'',
					fieldNote:'<font color="red">只能是字母\-\_\.且不能包含common和frm</font>'
				},(formId__|| data) ?{
					xtype:'hidden',
					name:'formId',
					value:formId__?formId__:'0'
				}:{
					xtype:'combo',
					fieldLabel:'属于',
					name:'formId',
					style:'margin-left:5px;',
					width:200,
					maxHeight:350,
					allowBlank : true, 
					editable  : false,  
					mode : "local",   
					forceSelection:true, 
					emptyText:'该脚本库属于某个表单或通用？',		
					triggerAction: 'all',
					//checkField:'checked',
					store : new Ext.data.JsonStore({ //填充的数据
						url : "source!add.jhtml?nodeId=" + nodeId__,
						fields : ['title', 'id'],
						root : 'formlist',
						sortInfo : {field: "id", direction: "ASC"} ,
						autoLoad :true,
						listeners:{
							"load":function(store,records,options){
								store.insert(0,new Ext.data.Record({title:'公共库',id:0}));
							}
						}
					}),
					valueField : 'id',
					displayField : 'title'
				},{
					xtype:'textarea',
					fieldLabel:'功能描述',
					width:400,
					height:50,
					name:'ss.intro',
					value:data?data.intro:''
				},{
					xtype:'scripteditor',
					fieldLabel:'脚本内容',
					name:'ss.script',
					width:400,
					height:228,
					value:data?data.script:'',
					syntax:'js',
					debugButtonEnable:true,
					debuger:'Ext.jsDebugger("../develop/",'+nodeId__+',"'+(data?data.name:"")+'",null,"common")'
				}],
				
				buttons:[{
					text:'确定',
					id:'btnCfgOK',
					handler:function(){
						var form = this.ownerCt.form;
						if(!form.isValid()){
							Ext.Toast.show("输入不合法!",{time:1500,icon: Ext.MessageBox.ERROR });
							return;
						}
					
						Ext.getBody().mask("正在提交,请稍候......");
						var params = form.getValues();
						var url = params['ss.id']=='0'?"script!addCommonScript.jhtml":"script!updateCommonScript.jhtml";
						params.nodeId= nodeId__;
						if(params['ss.id']=='0'){
							params['ss.name'] = params['formId']=='0' || !params['formId'] ? 'common.' + params['ss.name']:'frm' + params['formId'] + '.' + params['ss.name'];
						}else{
							delete params['ss.name'];
						}
						delete params.formId;
						Ext.Ajax.request({  
							url:url,
							method:"post",
							params:params,
							scope:gridMgr,
							options:{win:this.ownerCt.ownerCt},
							success:function(response,opts){
								Ext.getBody().unmask();
								var ret = Ext.util.JSON.decode(response.responseText);
								if(ret.success){
									Ext.Toast.show('提交成功',{
										title:'提示',
									    buttons: Ext.Msg.OK,
									    animEl: 'elId',
									    icon: Ext.MessageBox.INFO,  
										time:1000,
										minWidth:420
									});
									opts.options.win.close();
									this.store.load();
									
								}else{
									Ext.Msg.show({
									   title:'错误提示',
									   msg: decodeURIComponent(ret.message),
									   buttons: Ext.Msg.OK,
									   animEl: 'elId',
									   minWidth:420,
									   icon: Ext.MessageBox.ERROR  
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
				},{
					text:'取消',
					handler:function(){
						this.ownerCt.ownerCt.close();
					}
				}]
			}]
		});
		win.show();
		
		if(data) {
			win.body.mask('正在请求数据');
			Ext.Ajax.request({  
				url:'script!getCommonScript.jhtml?ss.id='+ data.id +'&nodeId=' + nodeId__,
				method:"get",
				scope:this,
				options:{win:win},
				success:function(response,opts){
					opts.options.win.body.unmask();
					var ret = Ext.util.JSON.decode(response.responseText);	
					opts.options.win.items.get(0).form.setValues(ret);
				}
			});
		}
	},
	/*添加 修改 删除 搜索*/
	addRecord:function(){
		this.openWin();
	},
	updateRecord:function(){
		var selItems = this.grid.getSelectionModel().selections.items;
		if(selItems.length==1){
			this.openWin(selItems[0].data);
		}else{
			Ext.Toast.show('请选择一条记录',{
			   title:'操作提示',
			   buttons: Ext.Msg.OK,
			   animEl: 'elId',
			   minWidth:420,
			   icon: Ext.MessageBox.WARNING  
			});
		}
	},
	deleteRecord:function(){
		var selItems = this.grid.getSelectionModel().selections.items;
		if(selItems.length>0){
			Ext.MessageBox.confirm("提示","确定删除吗？", 
				function(button,text){   
					if(button=='yes'){
						var ids=[];
						for(var i=0;i<selItems.length;i++){
							ids.push(selItems[i].data.id);
						}
						
						Ext.Ajax.request({  
							url:'script!deleteCommonScript.jhtml?ids=' + ids.join(',') + '&nodeId=' + nodeId__, 
							method:"get",
							waitTitle : "请稍候",  
							waitMsg : "正在处理中，请稍候......",  
							success:function(response,opts){
								var ret = Ext.util.JSON.decode(response.responseText);
								if(ret.success){
									Ext.Toast.show('删除成功',{
										title:'提示',
									    buttons: Ext.Msg.OK,
									    animEl: 'elId',
									    icon: Ext.MessageBox.INFO,  
										time:1000,
										minWidth:420
									});
									gridMgr.store.load();
								}else{
									Ext.Msg.show({
									   title:'错误提示',
									   msg: decodeURIComponent(ret.message),
									   buttons: Ext.Msg.OK,
									   animEl: 'elId',
									   minWidth:420,
									   icon: Ext.MessageBox.ERROR  
									});
								}
							},
							failure:function(response,opts){
								Ext.Msg.show({
								   title:'错误提示',
								   msg: decodeURIComponet(response.responseText),
								   buttons: Ext.Msg.OK,
								   animEl: 'elId',
								   minWidth:420,
								   icon: Ext.MessageBox.ERROR  
								});
							}
						});
					}
				}
			);
		}else{
			Ext.Toast.show('未选择任何记录',{
			   title:'操作提示',
			   buttons: Ext.Msg.OK,
			   animEl: 'elId',
			   minWidth:420,
			   icon: Ext.MessageBox.WARNING  
			});
		}
	},
	//本地即时搜索
	search:function(e,searchtxt){
		if(!searchtxt){
			var target=getEvent(e),searchtxt=gridMgr.txtSearch.getValue().replace(/\s/ig,'');
		}
		gridMgr.store.filter('name',searchtxt,true,false);
	}
}

var gridCommonMgr = {
	grid:null,
	column:null,
	pagerBar:null,
	store:new Ext.data.Store({ 
		proxy : new Ext.data.HttpProxy({url : 'script!getCommonScriptList.jhtml',method:'post'}), 
		sortInfo : {field: "name", direction: "ASC"} ,
		reader : new Ext.data.JsonReader({
			autoLoad:true,
			//root : "data",
			fields: ['id','name','createDateStr','creator','intro']
		}),
		remoteSort: false
	}),
	toolbarArr :null,
	txtSearch:null,
	init:function(){		
		this.toolbarArr = [{ 
			text : "增加", 
			iconCls : 'addField', 
			scope:this,
			handler : this.addRecord 

		}, { 
			text : "修改", 
			iconCls : 'modifyField', 
			scope:this,
			handler : this.updateRecord 
		}, { 
			text : "删除", 
			iconCls : 'delField', 
			scope:this,
			handler : this.deleteRecord 
		},{
			xtype:'tbfill'
		},{
			xtype:'textfield',
			id:'txtCommonSearch',
			width:200,
			emptyText:'输入名称进行模糊过滤',
			enableKeyEvents:true,
			style:'background:url(../res/img/search.gif) 180px 1px no-repeat;margin:0 5px;padding-right:20px;border-radius:10px;',
			listeners:{
				scope:this,
				'specialkey':function(obj,e){
					if(e.getCharCode()==Ext.EventObject.ENTER){
						var searchtxt=obj.getValue().replace(/\s/ig,'');
						this.search(e,searchtxt);
					}
				}
			}
			
		}];	
		this.initColumn();
		this.initGrid();
		
		this.txtCommonSearch = Ext.getCmp('txtCommonSearch');
		if(this.txtCommonSearch){
			addEventHandler(this.txtCommonSearch.getEl().dom,'keyup',bindFunc(this.txtCommonSearch.getEl().dom,function(e){
				if(gridCommonMgr.timeoutId) clearTimeout(gridCommonMgr.timeoutId);
				gridCommonMgr.timeoutId = setTimeout(gridCommonMgr.search,50);
			}));
		}	
	},
	initGrid:function(){
		var grid = new Ext.grid.GridPanel({ 
			stripeRows: true,　　//隔行换色
			loadMask: { msg: '正在加载数据，请稍侯……' },
			columnLines: true,　　//显示列线条
			anchor:'100%',
			store: this.store,
			cm: this.column,
			trackMouseOver:true,
			stripeRows: true,
			sm: new Ext.grid.CheckboxSelectionModel(),
			loadMask: true,
			autoSizeColumns : true,
			autoScroll:true, 
			height:scriptMgr.rightPanel.body.getHeight(),
			viewConfig: {
				forceFit:true,
				enableRowBody:true
			},
			iconCls:'icon-grid',
			layout :'fit',
			frame:false,
			tbar : this.toolbarArr
		}); 
		this.grid = grid;
		
		scriptMgr.rightPanel.add(grid);
		scriptMgr.rightPanel.doLayout();

		this.store.baseParams.nodeId =nodeId__;
		this.store.baseParams["ss.name"] = 'common.';
		
		this.store.load();
	},
	initColumn:function(){
		var cm = new Ext.grid.ColumnModel([
			new Ext.grid.CheckboxSelectionModel({width:18}),{
			   header: "ID",
			   dataIndex: 'id',
			   menuDisabled: true,
			   sortable:true,
			   width:32,
			   renderer:function(value, metadata, record){
					
					 return '<div style="width:'+ 30 +'px;overflow:hidden;">'+ value +'</div>';
				}
			},{
			    header: "名称",
			    dataIndex: 'name',
			    menuDisabled: true,
				sortable:true,
				width:130,
			    renderer:function(value, metadata, record){
					var tipHtml = '<div><ul>';
					tipHtml+='<li><span style="width:60px;margin:0 5px">创建者:</span><span>'+ record.data.creator +'</span></li>';
					tipHtml+='<li><span style="width:60px;margin:0 5px">创建时间:</span><span>'+ record.data.createDateStr +'</span></li>';
					tipHtml+='</ul></div>';
					metadata.attr = 'ext:qtitle=""' + ' ext:qtip="' + myZhuanyi(tipHtml) + '"'; 
				
					return value;
				}

			},{
			   header: "功能描述",
			   menuDisabled: true,
			   sortable:false,
			   dataIndex: 'intro',
			   renderer:function(value, metadata, record){
					var text = value;
					var displayHtml=text;
					var style = "";
					var divTitle='';

					var tip = value;
					metadata.attr = 'ext:qtitle=""' + ' ext:qtip="' + tip + '"'; 
					displayHtml= '<div style="width:'+ 100 +'px;overflow:hidden;">'+ text +'</div>';
					return displayHtml;
					
				}
			},{
				hander:'',
				menuDisabled: true,
				dataIndex:'id',
				renderer:function(value, metadata, record){
					return '<a href="javascript:gridCommonMgr.updateRecord()" style="margin-right:5px">修改</a><a href="javascript:gridCommonMgr.deleteRecord()">删除</a>';
				}
			}
		]);
		this.column = cm;
	},
	openWin:function(data){
		var win = new Ext.Window({
			title:'脚本窗口',
			height:478,
			width:525, 
			buttonAlign: "center",
			closable:true ,
			closeAction:'close',
			autoScroll:true,
			modal:false,
			layout:'fit',
			resizable :false,
			defaultButton:'btnCfgOK',
			items:[{
				xtype:'form',
				layout:'xform',
				itemCls:'itemStyle',
				labelAlign:'right',
				labelWidth:70,
				autoScroll:true,
				items:[{
					xtype:'hidden',
					name:'ss.id',
					value:data?data.id:0
				},data?{
					xtype:'textfield',
					fieldLabel:'名称',
					width:120,
					readOnly:true,
					style:'background:transparent;border:none;color:#666666',
					value:data.name
				}:{
					xtype:'textfield',
					fieldLabel:'名称',
					name:'ss.name',
					width:120,
					allowBlank :false,
					value:'',
					fieldNote:'<font color="red">只能是字母\-\_\.且不能包含common和frm</font>'
				},{
					xtype:'hidden',
					name:'formId',
					value:'0'
				},{
					xtype:'textarea',
					fieldLabel:'功能描述',
					width:400,
					height:80,
					name:'ss.intro',
					value:data?data.intro:''
				},{
					xtype:'scripteditor',
					fieldLabel:'脚本内容',
					name:'ss.script',
					width:400,
					height:228,
					value:data?data.script:'',
					syntax:'js',
					debugButtonEnable:true,
					debuger:'Ext.jsDebugger("../develop/",'+nodeId__+',"'+(data?data.name:"")+'",null,"common")'
				}],
				
				buttons:[{
					text:'确定',
					id:'btnCfgOK',
					handler:function(){
						var form = this.ownerCt.form;
						if(!form.isValid()){
							Ext.Toast.show("输入不合法!",{time:1500,icon: Ext.MessageBox.ERROR });
							return;
						}
					
						Ext.getBody().mask("正在提交,请稍候......");
						var params = form.getValues();
						var url = params['ss.id']=='0'?"script!addCommonScript.jhtml":"script!updateCommonScript.jhtml";
						params.nodeId= nodeId__;
						if(params['ss.id']=='0'){
							params['ss.name'] = params['formId']=='0' || !params['formId'] ? 'common.' + params['ss.name']:'frm' + params['formId'] + '.' + params['ss.name'];
						}else{
							delete params['ss.name'];
						}
						delete params.formId;
						Ext.Ajax.request({  
							url:url,
							method:"post",
							params:params,
							scope:gridCommonMgr,
							options:{win:this.ownerCt.ownerCt},
							success:function(response,opts){
								Ext.getBody().unmask();
								var ret = Ext.util.JSON.decode(response.responseText);
								if(ret.success){
									Ext.Toast.show('提交成功',{
										title:'提示',
									    buttons: Ext.Msg.OK,
									    animEl: 'elId',
									    icon: Ext.MessageBox.INFO,  
										time:1000,
										minWidth:420
									});
									opts.options.win.close();
									this.store.load();
									
								}else{
									Ext.Msg.show({
									   title:'错误提示',
									   msg: decodeURIComponent(ret.message),
									   buttons: Ext.Msg.OK,
									   animEl: 'elId',
									   minWidth:420,
									   icon: Ext.MessageBox.ERROR  
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
				},{
					text:'取消',
					handler:function(){
						this.ownerCt.ownerCt.close();
					}
				}]
			}]
		});
		win.show();
		
		if(data) {
			win.body.mask('正在请求数据');
			Ext.Ajax.request({  
				url:'script!getCommonScript.jhtml?ss.id='+ data.id +'&nodeId=' + nodeId__,
				method:"get",
				scope:this,
				options:{win:win},
				success:function(response,opts){
					opts.options.win.body.unmask();
					var ret = Ext.util.JSON.decode(response.responseText);	
					opts.options.win.items.get(0).form.setValues(ret);
				}
			});
		}		
	},
	/*添加 修改 删除 搜索*/
	addRecord:function(){
		this.openWin();
	},
	updateRecord:function(){
		var selItems = this.grid.getSelectionModel().selections.items;
		if(selItems.length==1){
			this.openWin(selItems[0].data);
		}else{
			Ext.Toast.show('请选择一条记录',{
			   title:'操作提示',
			   buttons: Ext.Msg.OK,
			   animEl: 'elId',
			   minWidth:420,
			   icon: Ext.MessageBox.WARNING  
			});
		}
	},
	deleteRecord:function(){
		var selItems = this.grid.getSelectionModel().selections.items;
		if(selItems.length>0){
			Ext.MessageBox.confirm("提示","确定删除吗？", 
				function(button,text){   
					if(button=='yes'){
						var ids=[];
						for(var i=0;i<selItems.length;i++){
							ids.push(selItems[i].data.id);
						}
						
						Ext.Ajax.request({  
							url:'script!deleteCommonScript.jhtml?ids=' + ids.join(',') + '&nodeId=' + nodeId__, 
							method:"get",
							waitTitle : "请稍候",  
							waitMsg : "正在处理中，请稍候......",  
							success:function(response,opts){
								var ret = Ext.util.JSON.decode(response.responseText);
								if(ret.success){
									Ext.Toast.show('删除成功',{
										title:'提示',
									    buttons: Ext.Msg.OK,
									    animEl: 'elId',
									    icon: Ext.MessageBox.INFO,  
										time:1000,
										minWidth:420
									});
									gridCommonMgr.store.load();
								}else{
									Ext.Msg.show({
									   title:'错误提示',
									   msg: decodeURIComponent(ret.message),
									   buttons: Ext.Msg.OK,
									   animEl: 'elId',
									   minWidth:420,
									   icon: Ext.MessageBox.ERROR  
									});
								}
							},
							failure:function(response,opts){
								Ext.Msg.show({
								   title:'错误提示',
								   msg: decodeURIComponet(response.responseText),
								   buttons: Ext.Msg.OK,
								   animEl: 'elId',
								   minWidth:420,
								   icon: Ext.MessageBox.ERROR  
								});
							}
						});
					}
				}
			);
		}else{
			Ext.Toast.show('未选择任何记录',{
			   title:'操作提示',
			   buttons: Ext.Msg.OK,
			   animEl: 'elId',
			   minWidth:420,
			   icon: Ext.MessageBox.WARNING  
			});
		}
	},
	//本地即时搜索
	search:function(e,searchtxt){
		if(!searchtxt){
			var target=getEvent(e),searchtxt=gridCommonMgr.txtCommonSearch.getValue().replace(/\s/ig,'');
		}
		gridCommonMgr.store.filter('name',searchtxt,true,false);
	}
}

</script>
<script>

Ext.onReady(function(){
	Ext.BLANK_IMAGE_URL = "../res/js/ext2/resources/images/default/s.gif";
	Ext.lOADING_IMAGE_URL= "../res/js/ext2/resources/images/default/grid/wait.gif"; //resources\images\default\grid
	Ext.QuickTips.init();
	Ext.form.Field.prototype.msgTarget = 'qtip';
	scriptMgr.init();
});
</script>
	
</body>
</html>