 <!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title>列表</title>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />

	<link rel="stylesheet" type="text/css" href="./../res/js/ext2/resources/css/ext-all.css" />
	<link rel="stylesheet" type="text/css" href="./../res/js/ext2/resources/css/patch.css" />
	<style>
		body{font-size:12px;}
		.blank{clear:both;height:18px}
		.blank2{clear:both;height:10px;line-height:0;font-size:0}
		
		/*按钮*/
		.addField{background:url("./../res/js/ext2/resources/images/default/dd/drop-add.gif") left  no-repeat !important;}
		.delField{background:url("./../res/js/ext2/resources/images/default/my/del-form.gif") left  no-repeat !important;}
		.modifyField{background:url("./../res/js/ext2/resources/images/default/my/modify-view.gif") left  no-repeat !important;}
		
		.x-grid3-cell-inner, .x-grid3-hd-inner {
			overflow: hidden;
			padding: 5px 3px 5px 5px;
			white-space: nowrap;
		}
	</style>
 	<script type="text/javascript" src="./../res/js/ext2/adapter/ext/ext-base.js"></script>
    <script type="text/javascript" src="./../res/js/ext2/ext-all-debug.js"></script>
	<script type="text/javascript" src="./../res/js/ext2/ext-lang-zh_CN.js"></script>
	<script type="text/javascript" src="./../res/js/ext_base_extension.js"></script>
	<script type="text/javascript" src="./../res/js/controls/Ext.ux.RadioGroup.js"></script>
	<script type="text/javascript">
		var listConfig= ${listConfig!""};
		var searchConfig = ${searchConfig!""};//全文检索的配置
		var formId__ = #{formId!0};
		var listId__= #{listId!0};
		var viewId__= #{viewId!0};
		var nodeId__=#{nodeId!0};
		
		if(listConfig.viewId) viewId__ = listConfig.viewId;	
		var params__ = Ext.parseQuery();
		if(params__.viewId) viewId__ = params__.viewId;

		
		function getFieldsArr(fldsArr){
			var retArr=["id"];
			for(var i = 0;i<fldsArr.length;i++){
				retArr.push(fldsArr[i][0]);
			}
			return retArr;
		}
		//初始化listConfig__
		var listConfig__={
			columns:listConfig.columns,
			pagesize:listConfig.pagesize,
			db : {
				"l_allowSort" : searchConfig.fieldsConfig.sortable,
				"l_allowSearch" :searchConfig.fieldsConfig.searchable
			},
			service : {
				"l_allowSort" : searchConfig.searchConfig.sortable,
				"l_allowSearch" : searchConfig.searchConfig.searchable
			},
			fields:getFieldsArr(searchConfig.fieldsConfig.fields)
		}
	</script>
	
</head>
<body style="padding:1px;">

	<!-- ext 模板渲染结果临时存储  -->
	<textarea id="txtTplValue" style="display:none;"></textarea>
	
<script type="text/javascript">



//初始化搜索区
//搜索区操作符列表框数据
var opStore = new Ext.data.SimpleStore({　
　　fields:['value','text'],　
　　data:[['=','等于(=)'],['like','匹配(like)'],['>','大于(>)'],['>=','大于或等于(>=)'],['<','小于(<)'],['<=','小于或等于(<=)'],['<>','不等于(<>)']]
});

//搜索区搜索字段列表框数据
function getFieldArr(type,type2){
	var sfieldArr = [];
	var str = listConfig__[type][type2].join(',');
	for(var i=0;i<listConfig__.columns.length;i++){
		var col = listConfig__.columns[i];
		if(col.field!='' && str.indexOf(col.field)!=-1){
			sfieldArr[sfieldArr.length] = [col.field,col.title];
		}
	}
	var sfieldStore = new Ext.data.SimpleStore({　
		fields:['value','text'],　
		data:sfieldArr
	})
	return sfieldStore;
}

var sfieldStore = getFieldArr('db','l_allowSearch');
var sfieldStore_svr = getFieldArr('service','l_allowSearch');
var sortfieldStore = getFieldArr('db','l_allowSort');
var sortfieldStore_svr = getFieldArr('service','l_allowSort');
//根据模板渲染内容
function renderField(exttpl){
	var tplStr = exttpl;
	return function(value, p, record){
		var tpl = new Ext.XTemplate(tplStr);
		record.data['formId'] = formId__;
		record.data['listId'] = listId__;
		record.data['viewId'] = viewId__;
		return tpl.applyTemplate(record.data);
	}
}
	

function setActiveTab(url,tabId,title){
	if(top&& top.centerTabPanel){
		var _url = url;
		top.centerTabPanel.addIframe('tab_' + tabId,title ,_url);
	}else{
		window.open(url);	
	}
}

openTab = function(url,title){
	if(!title) title='';
	if(top&& top.centerTabPanel){
		var _url = url;
		top.centerTabPanel.addIframe('tab_' + (new Date()).valueOf(),title ,_url);
	}else{
		window.open(url);	
	}
}

</script>	

<script>
var listMgr = {
	grid:null,
	column:null,
	pagerBar:null,
	store:new Ext.data.Store({ 
		proxy : new Ext.data.HttpProxy({url : 'xlist!data.jhtml',method:'POST'}), 
		reader : new Ext.data.JsonReader({
				autoLoad:true,
				root : "data",
				totalProperty : "totalCount",
				fields: listConfig__.fields
		}),
		remoteSort: false
	}),
	pagerBar:null,
	toolbarArr :null,
	searchPanelConfig_db:null,	
	searchPanelConfig_svr:null,
	init:function(){
		this.initSearchPanelConfig_db();
		this.initSearchPanelConfig_svr();
		var mainViewport = new Ext.Viewport({
			layout: 'border',
			items:[{
				xtype:"tabpanel",
				layoutOnTabChange:true,
				autoHeight:true,
				activeTab:0,
				style:'padding:1px 5px 0px 5px;',
				height:138,
				region:'north',
				items:[{
					xtype:"panel",
					title:"从数据库搜索",
					frame:false,
					border:false,
					items:[this.searchPanelConfig_db]
				},
				{
					xtype:"panel",
					title:"从搜索服务搜索",
					frame:false,
					border:false,
					items:[this.searchPanelConfig_svr]
				}
			]},{
				xtype:'panel',
				region:'center',
				style:'padding:1px 5px 0px 5px;',
				id:'placeholder',
				frame:false,
				autoHeight:true,
				layout:'fit'
			}]
		});
		
		this.toolbarArr = [{ 
			text : "增加", 
			iconCls : 'addField', 
			handler : this.addRecord 

		}, { 
			text : "修改", 
			iconCls : 'modifyField', 
			handler : this.updateRecord 
		}, { 
			text : "删除", 
			iconCls : 'delField', 
			handler : this.deleteRecord 
		}];
		
		this.initPageBar();
		this.initColumn();
		this.initGrid();
	},
	initPageBar:function(){
		this.pagerBar = new Ext.PagingToolbar({ 
			pageSize : listConfig__.pagesize, 
			store : this.store, 
			displayMsg : '显示第 {0} 条到 {1} 条记录,共 {2} 条记录',
			emptyMsg : "没有要显示的数据",
			firstText : "首页",
			prevText : "前一页",
			nextText : "下一页",
			lastText : "尾页",
			refreshText : "刷新",
			displayInfo : true 
		});
	},
	initGrid:function(){
		var grid = new Ext.grid.GridPanel({ 
			store: this.store,
			cm: this.column,
			trackMouseOver:true,
			stripeRows: true,
			sm: new Ext.grid.CheckboxSelectionModel(),
			loadMask: true,
			autoSizeColumns : true,
			autoScroll:true, 
			autoHeight:true,
			viewConfig: {
				forceFit:true,
				enableRowBody:true
			},
			iconCls:'icon-grid',
			layout :'fit',
			frame:false,
			tbar : this.toolbarArr, 
			//bbar : this.toolbarArr, 
			bbar : listMgr.pagerBar, 
			listeners　:　{
	　　　　	'render'　:　function()　{
					//listMgr.pagerBar.render(grid.bbar);
				}
				
			}
		}); 
		this.grid = grid;
		grid.render('placeholder');
		this.store.baseParams.formId =formId__;
		this.store.baseParams.listId =listId__;
		this.store.baseParams.limit = listConfig__.pagesize;
		this.store.baseParams.sort = '[{"field":"id","order":"desc"}]';
		this.store.load({params:{start:0,limit:listConfig__.pagesize}});//初次搜索,默认从DB搜索
		grid.getSelectionModel().selectFirstRow();

	},
	initColumn:function(){
		var colModelArr = [new Ext.grid.CheckboxSelectionModel()];
		for(var i=0;i<listConfig__.columns.length;i++){
			var col = listConfig__.columns[i];
			if(col.isView==true){
				var o = {
					header : col.title, 
					l_allowSort : true, 
					dataIndex : col.field,
					renderer:renderField(col.tpl)
				};
				if(col.width)o.width = col.width;
				colModelArr[colModelArr.length]=o;
			}
			
		}
		var column = new Ext.grid.ColumnModel(colModelArr);
		this.column = column;
	},
	//搜索
	doSearch:function (){
		//搜集搜索条件,以json格式post到服务端解析
		var searchPanel = Ext.getCmp('searchPanel');
		var filter={where:[],sort:[]};
		for(var i=0;i<searchPanel.items.length;i++){
			var itemPanel = searchPanel.items.get(i);
			var fld = itemPanel.findById('cbFldSearch_' + i).getValue();
			var op = itemPanel.findById('cbOpsSearch_' + i).getValue();
			var value = itemPanel.findById('txtSearch_' + i).getValue();
			var andor = 'and';
			if(i!=0) andor = itemPanel.findById('radioAndor_' + i).getValue();
			if(fld!="" && op!="" && value!=""){
				filter.where.push({field:fld,op:op,value:value,andor:andor});
			}
		}
		var fsl_allowSort = Ext.getCmp('fsl_allowSort');
		var sortFld = fsl_allowSort.findById('cbOrderField').getValue();
		var sortValue = fsl_allowSort.findById('rdOrderValue').getValue();
		if(sortFld!='' && sortValue!=''){
			filter.sort.push({field:sortFld,order:sortValue});
		}
		filter.sort.push({field:'id',order:'desc'});
		listMgr.store.baseParams ={
			from:'db',
			where:Ext.util.JSON.encode(filter.where),
			sort:Ext.util.JSON.encode(filter.sort),
			formId:formId__,
			listId:listId__
		};
		listMgr.store.load({
			params:{
				start:0, 
				limit:listConfig__.pagesize
			},
			callback: function(r, options, success){  
				if(!success){   
					Ext.Msg.alert('操作','失败！');  
				}  
			}
		});
	},
	doSearch_svr:function (){
		//搜集搜索条件,以json格式post到服务端解析
		var searchPanel = Ext.getCmp('searchPanel_svr');
		
		var q=[];
		for(var i=0;i<searchPanel.items.length;i++){
			var itemPanel = searchPanel.items.get(i);
			var fld = itemPanel.findById('cbFldSearch_svr_' + i).getValue();
			var value = itemPanel.findById('txtSearch_svr_' + i).getValue();
			
			if(fld!="" && value!=""){
				q.push(value + ':' + fld);
			}
		}
		var sort=[];
		var fsl_allowSort = Ext.getCmp('fsl_allowSort_svr');
		var sortFld = fsl_allowSort.findById('cbOrderField_svr').getValue();
		var sortValue =fsl_allowSort.findById('rdOrderValue_svr').getValue();
		if(sortFld!='' && sortValue!=''){
			sort.push(sortFld + ' ' + sortValue);
		}
		sort.push('id desc');
		//http://localhost/search/do.action?type=select&start=0&len=10&fl=id,name&q=10:id,1280335088000-1280421494000:time,ifeng*:name&sort=id asc,date desc&wt=xml
		listMgr.store.baseParams ={
			from:'service',
			fd:listConfig__.fields.join(','),//需要输出的字段
			q:q.join(','),
			sort:sort.join(','),
			formId:formId__,
			listId:listId__
		};
		listMgr.store.load({
			params:{
				start:0, 
				limit:listConfig__.pagesize
			},
			callback: function(r, options, success){  
				if(!success){   
					Ext.Msg.alert('操作','失败！');  
				}  
			}
		});
		
	},
	
	/*添加 修改 删除*/
	addRecord:function(){
		setActiveTab('./../runtime/xform!render.jhtml?viewId='+ viewId__ +'&formId=' + formId__ + '&id=0&nodeId=' + nodeId__,'list_add_' + formId__ + '_' + viewId__,'添加');
	},
	updateRecord:function(){
		var selItems = listMgr.grid.getSelectionModel().selections.items;
		if(selItems.length==1){
			setActiveTab('./../runtime/xform!render.jhtml?viewId='+ viewId__ +'&formId=' + formId__ + '&id=' + selItems[0].data.id + '&nodeId=' + nodeId__,'list_add_' + formId__ + '_' + viewId__ + '_' + selItems[0].data.id,'修改');
		}else{
			Ext.Msg.show({
			   title:'操作提示',
			   msg: "请选择一条记录",
			   buttons: Ext.Msg.OK,
			   animEl: 'elId',
			   minWidth:420,
			   icon: Ext.MessageBox.INFO  
			});
		}
	},
	deleteRecord:function(){
		var selItems = listMgr.grid.getSelectionModel().selections.items;
		if(selItems.length>0){
			Ext.MessageBox.confirm("提示","确定删除吗？", 
				function(button,text){   
					if(button=='yes'){
						var ids=[];
						for(var i=0;i<selItems.length;i++){
							ids.push(selItems[i].data.id);
						}
						
						Ext.Ajax.request({  
							url:'xlist!delete.jhtml?viewId='+ viewId__ +'&formId=' + formId__ + '&ids=' + ids.join(','), 
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
									listMgr.store.load({params:{start:0}});
								}else{
									Ext.Msg.show({
									   title:'错误提示',
									   msg: decodeURIComponet(ret.message),
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
			Ext.Msg.show({
			   title:'操作提示',
			   msg: "请选择记录",
			   buttons: Ext.Msg.OK,
			   animEl: 'elId',
			   minWidth:420,
			   icon: Ext.MessageBox.INFO  
			});
		}
	},

	//数据库搜索并查		
	addSearchItem:function(index){
		var i = index;
		
		return function(){
			var searchPanel = Ext.getCmp('searchPanel');
			if(this.getValue()){
				var sp = new Ext.Panel({
					xtype:"panel",
					header :false,
					id:'panelSearchItem_' + (i+1),
					layout:'table',
					border:false,
					layoutConfig:{
						columns:5
					}
				});
				var radioGroup = new Ext.ux.RadioGroup({   
					fieldLabel : "",   
					width:80,
					id:"radioAndor_" + (i+1),
					allowBlank : true,   
					horizontal:true,   
					value:'and',    
					name:'andor_' + (i+1),					
					data:[['and','并且'],['or','或者']]  
				});
				sp.add(radioGroup);
				
				var cbFields = new Ext.form.ComboBox({
					triggerAction:"all",
					store:sfieldStore,
					id:'cbFldSearch_' + (i+1),
					valueField : 'value',
					displayField : 'text',
					mode:'local',
					width:120
				});
				sp.add(cbFields);
				
				var cbOps = new Ext.form.ComboBox({
					id:'cbOpsSearch_' + (i+1),
					allowBlank:true,
					store:opStore,
					valueField : 'value',
					displayField : 'text',
					value:'=',
					editable: true,
					forceSelection: true,
					mode:'local',
					triggerAction:'all',
					selectOnFocus:false,
					width:100
				});
				sp.add(cbOps);
				
				var txtKeys = new Ext.form.TextField({
					id:'txtSeach_' + (i+1),
					width:120
				});
				sp.add(txtKeys);
				
				if(i<2){
					var chkAdd = new Ext.form.Checkbox({
						boxLabel:"并查",
						id:'chkSearch_' + (i+1),
						style:"margin:0 5px 0 5px",
						listeners:{
							'check':listMgr.addSearchItem(i+1)
						}
					});
					sp.add(chkAdd);
				}
				searchPanel.add(sp);
				searchPanel.doLayout();
			}else{
				var len = searchPanel.items.length;
				for(var j=i+1;j<len;j++){
					searchPanel.remove('panelSearchItem_' + j,true);
				}
			}
		}
	},
	//搜索服务并查
	addSearchItem_svr:function(index){
		var i = index;
		
		return function(){
			var searchPanel = Ext.getCmp('searchPanel_svr');
			if(this.getValue()){
				var sp = new Ext.Panel({
					xtype:"panel",
					id:'panelSearchItem_svr_' + (i+1),
					layout:'table',
					border:false,
					layoutConfig:
					{
						columns:5
					}
				});
				var radioGroup = new Ext.ux.RadioGroup({   
					fieldLabel : "",   
					width:80,
					allowBlank : true,  
					id:"radioAndor_svr_" + (i+1),					
					horizontal:true,   
					maxLength : 200,   
					value:'and',  
					name:'andor_svr_' + (i+1),					
					data:[['and','并且'],['or','或者']]  
				});
				sp.add(radioGroup);
				
				var cbFields = new Ext.form.ComboBox({
					triggerAction:"all",
					store:sfieldStore_svr,
					id:'cbFldSearch_svr_' + (i+1),
					valueField : 'value',
					displayField : 'text',
					mode:'local',
					width:120
				});
				sp.add(cbFields);
				
				var cbOps = new Ext.form.Label({
					text:":",
					style:"margin:0 5px 0 5px"
				});
				sp.add(cbOps);
				
				var txtKeys = new Ext.form.TextField({
					id:'txtSeach_svr_' + (i+1),
					width:180
				});
				sp.add(txtKeys);
				
				if(i<2){
					var chkAdd = new Ext.form.Checkbox({
						boxLabel:"并查",
						id:'chkSearch_svr_' + (i+1),
						style:"margin:0 5px 0 5px",
						listeners:{
							'check':listMgr.addSearchItem_svr(i+1)
						}
					});
					sp.add(chkAdd);
				}
				searchPanel.add(sp);
				searchPanel.doLayout();
			}else{
				var len = searchPanel.items.length;
				for(var j=i+1;j<len;j++){
					searchPanel.remove('panelSearchItem_svr_' + j,true);
				}
			}
		}
	},
	//根据搜索服务的初始配置
	initSearchPanelConfig_svr:function(){
		this.searchPanelConfig_svr ={
			height:128,
			layout:"table",
			border:true,
			frame:true,
			layoutConfig:
			{
				columns:2
			},
			buttons:[{
					text:'搜索',
					style :'margin:-7px 5px -7px 0',
					border:false,
					handler:listMgr.doSearch_svr
			}],
			items:[{
				xtype:"panel",
				id:'searchPanel_svr',
				border:false,
				title:"",
				width:560,
				height:92,
				layout:"auto",
				items:[{
					xtype:"panel",
					id:'panelSearchItem_svr_0',
					style:'padding-left:80px;',
					layout:'table',
					border:false,
					layoutConfig:{
						columns:4
					},
					items:[{
						xtype:"combo",
						triggerAction:"all",
						store:sfieldStore_svr,
						id:'cbFldSearch_svr_0',
						valueField : 'value',
						displayField : 'text',
						mode:'local',
						width:120
					},{
						xtype:"label",
						text:":",
						style:"margin:0 5px 0 5px"
					},{
						xtype:"textfield",
						id:'txtSearch_svr_0',
						width:180
					},{
						xtype:"checkbox",
						boxLabel:"并查",
						id:'chkSearch_svr_0',
						style:"margin:0 5px 0 5px",
						listeners:{
							'check':listMgr.addSearchItem_svr(0)
						}
					}]
				}]
			},{//排序
				xtype:"panel",
				width:360,	
				height:92,
				border:false,
				layout:"auto",
				items:[{
					xtype:"fieldset",
					id:"fsl_allowSort_svr",
					title:"请选择排序字段和排序方式(可不填)",	
					layout:'table',	
					autoHeight:true,
					items:[{
						xtype:"combo",
						triggerAction:"all",
						id:"cbOrderField_svr",
						store:sortfieldStore_svr,
						valueField : 'value',
						displayField : 'text',
						mode:'local',
						width:120
					},{
						horizontal:true,    
						id:"rdOrderValue_svr",
						xtype:"radiogroup",
						value:'asc',   
						name:'order_svr',						
						data:[['asc','升序'],['desc','降序']]
					}]
				}]	
			}]
		}
	},
		
	initSearchPanelConfig_db:function(){
		this.searchPanelConfig_db = {
			//根据数据库搜索页的初始配置
			height:128,
			layout:"table",
			border:false,
			frame:true,
			layoutConfig:{
				columns:2
			},
			buttons:[{
				text:'搜索',
				style :'margin:-7px 5px -7px 0',
				border:false,
				handler:listMgr.doSearch
			}],
			items:[{
				xtype:"panel",
				id:'searchPanel',
				border:false,
				header :false,
				width:560,
				height:92,
				layout:"auto",
				items:[{
					xtype:"panel",
					header :false,
					id:'panelSearchItem_0',
					layout:'table',
					style:'padding-left:80px;',
					border:false,
					layoutConfig:{
						columns:4
					},
					items:[{
						xtype:"combo",
						triggerAction:"all",
						id:'cbFldSearch_0',
						store:getFieldArr('db','l_allowSearch'),
						valueField : 'value',
						displayField : 'text',
						mode:'local',
						width:120
					},{
						xtype:"combo",
						id:'cbOpsSearch_0',
						allowBlank:true,
						store:new Ext.data.SimpleStore({　
						　　fields:['value','text'],　
						　　data:[['=','等于(=)'],['like','匹配(like)'],['>','大于(>)'],['>=','大于或等于(>=)'],['<','小于(<)'],['<=','小于或等于(<=)'],['<>','不等于(<>)']]
						}),
						valueField : 'value',
						displayField : 'text',
						value:'=',
						editable: true,
						forceSelection: true,
						mode:'local',
						triggerAction:'all',
						selectOnFocus:false,
						width:100
					},{
						xtype:"textfield",
						id:'txtSearch_0',
						width:120
					},{
						xtype:"checkbox",
						boxLabel:"并查",
						id:'chkSearch_0',
						style:"margin:0 5px 0 5px",
						listeners:{
							'check':listMgr.addSearchItem(0)
						}
					}]
				}]
			},{//排序
				xtype:"panel",
				width:360,	
				height:92,
				border:false,
				layout:"auto",
				items:[{
					xtype:"fieldset",
					id:"fsl_allowSort",
					title:"请选择排序字段和排序方式(可不填)",	
					layout:'table',	
					autoHeight:true,
					items:[{
						xtype:"combo",
						triggerAction:"all",
						id:"cbOrderField",
						store:sortfieldStore,
						valueField : 'value',
						displayField : 'text',
						mode:'local',
						width:120
					},{
						horizontal:true,    
						id:"rdOrderValue",
						name:'order',
						xtype:"radiogroup",
						value:'asc',     
						data:[['asc','升序'],['desc','降序']]
					}]
				}]		
			}]
		};
	}
}

Ext.onReady(function(){
	Ext.BLANK_IMAGE_URL = "./../res/js/ext2/resources/images/default/s.gif";
	Ext.QuickTips.init();
	Ext.form.Field.prototype.msgTarget = 'qtip';
	
	listMgr.init();		
});
</script>		
	
</body>
</html>	
