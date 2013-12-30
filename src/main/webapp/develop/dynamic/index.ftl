 <!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <title>动态前端列表</title>
	<link rel="shortcut icon" href="../res/img/favicon.ico" />
	<link rel="stylesheet" type="text/css" href="../res/js/ext2/resources/css/ext-all.css" />
	<link rel="stylesheet" type="text/css" href="../res/js/ext2/resources/css/patch.css" />
	<style>
		body{font-size:12px;}
		.blank{clear:both;height:18px}
		.blank2{clear:both;height:10px;line-height:0;font-size:0}	
		/*按钮*/
		.addField{background:url("../res/js/ext2/resources/images/default/dd/drop-add.gif") left  no-repeat !important;}
		.delField{background:url("../res/js/ext2/resources/images/default/my/del-form.gif") left  no-repeat !important;}
		.modifyField{background:url("../res/js/ext2/resources/images/default/my/modify-view.gif") left  no-repeat !important;}
		.searchBtn{background:url("../res/img/search.gif") left  no-repeat !important;}
		.x-grid3-cell-inner, .x-grid3-hd-inner {
			overflow: hidden;
			padding: 5px 3px 5px 5px;
			text-overflow: ellipsis;/*ellipsis clip*/
			white-space: nowrap;
		}
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
		/**checkboxGroup radiogroup**/
		.inputUnit {
			display: inline-block;
			margin: 5px;
			width:200px;
			height:20px;
		}
		.inputUnit input{
			margin-right:5px;
		}

		.rightMark{height:20px;background:url(../res/js/ext2/resources/images/default/tree/drop-yes.gif) no-repeat center;cursor:pointer;}
		.faultMark{height:20px;background:url(../res/js/ext2/resources/images/default/layout/panel-close.gif) no-repeat center;cursor:pointer;}
		
	</style>
	
 	<script type="text/javascript" src="../res/js/ext2/adapter/ext/ext-base.js"></script>
    <script type="text/javascript" src="../res/js/ext2/ext-all-debug.js"></script>	
	<script type="text/javascript" src="../res/js/ext2/ext-lang-zh_CN.js"></script>
	<script type="text/javascript" src="../res/js/ext_base_extension.js"></script>
	<script type="text/javascript" src="../res/js/editors/Ext.createRecordsField.js" ></script>
	<script type="text/javascript" src="../res/js/controls/Ext.ux.ListPanel.js"></script>
	<script type="text/javascript" src="../res/js/controls/Ext.ux.CheckboxGroup.js"></script>
	
	<script>
	var nodeId = Ext.parseQuery().nodeId?Ext.parseQuery().nodeId:0;
	</script>
	</head>
	<body>

	<script>
	var editRecord,deleteRecord,pushConfig,pushDynPage,pushTable,pushedTable;
	var dynPageListWindow,tableListWindow,pushedTableListWindow;
	/*
	推送或删除页面
	*/
	function pushDynPage(type){
		var _type =type;
		return function(){
			if(grid.getSelectionModel().getCount()==0)
			{
				Ext.Toast.show('请选择要推送的前端',{
				   title:'操作提示',
				   buttons: Ext.Msg.OK,
				   minWidth:420,
				   icon: Ext.MessageBox.WARNING  
				});
				return;
			}
			if(grid.getSelectionModel().getCount()>1)
			{
				Ext.Toast.show('每次只能选择一个前端',{
				   title:'操作提示',
				   buttons: Ext.Msg.OK,
				   minWidth:420,
				   icon: Ext.MessageBox.WARNING  
				});				
				return;
			}
			var id = grid.getSelectionModel().selections.items[0].data.id;
			if(!dynPageListWindow){
				dynPageListWindow = new Ext.Window({
					title:'动态页面列表',
					width:530, 
					height:520,
					buttonAlign: "center",
					closable:true ,
					closeAction:'hide',
					autoScroll:false,
					maximizable :true,
					modal:true,
					layout:'fit',
					resizable :true,
					items:[{
						layout:'fit',
						html:'<div style="width:100%;height:100%;background:url(../res/img/loading.gif) no-repeat center center;"><iframe id="fr_editor" scrolling="no" frameborder="0" width="100%" height="100%" style="visibility:hidden"></iframe></div>' 
					}],
					buttons:[{
						'text':'推送',
						id:'btnPageMgr',
						handler:function(){
							var selItems = document.getElementById('fr_editor').contentWindow.listMgr.grid.getSelectionModel().selections.items;
							if(selItems.length==0){
								Ext.Toast.show('请选择需要推送的页面(可多选)',{
								   title:'操作提示',
								   buttons: Ext.Msg.OK,
								   minWidth:420,
								   icon: Ext.MessageBox.WARNING  
								});				
								return;
							}
							var ids=[];
							for(var i=0;i<selItems.length;i++){
								ids.push(selItems[i].data.id);
							}
							doRequest('dynamic!pushPage.jhtml?nodeId='+nodeId+'&id='+this.ownerCt.recordId+'&ids=' + ids.join(',')+'&type=' + dynPageListWindow.actionType,this.text + '动态页面');
							this.ownerCt.hide();
						}
					},{
						text:'取消',
						handler:function(){
							this.ownerCt.hide();
						}
					}]
				});
				dynPageListWindow.actionType=_type;
				dynPageListWindow.recordId = id;
				dynPageListWindow.show(null,function(){
					var iframe = document.getElementById('fr_editor');
					iframe.onload = function(){
						var pageGrid = this.contentWindow.listMgr.grid;
						
						var tbItems = pageGrid.topToolbar.items.items;
						for(var j=2;j>=0;j--){
							tbItems[j].destroy();
						}
						
						if(dynPageListWindow.actionType==0){//查看已推送页
							//pageGrid.topToolbar.disable();
							pageGrid.store.baseParams={//从接口获取参数时传递的参数 listId=4&start=0&limit=10&dynId=11
								dynId :dynPageListWindow.recordId,
								listId :4,
								sort :'[{"field":"id","order":"desc"}]',
								limit: pageGrid.pagesize
							}
							pageGrid.store.load({params:{start:0}});
						}else{
							//pageGrid.topToolbar.enable();
							pageGrid.store.baseParams={//从接口获取参数时传递的参数 listId=4&start=0&limit=10&dynId=11
								nodeId :nodeId,
								listId :1,
								sort :'[{"field":"id","order":"desc"}]',
								limit: pageGrid.pagesize
							}
							pageGrid.store.load({params:{start:0}});
						}
						Ext.fly(this).fadeIn({
							endOpacity: 1, //can be any value between 0 and 1 (e.g. .5)
							easing: 'easeIn',
							duration: .5,
							useDisplay: false
						});	
					};	
					iframe.src = 'dynamic/index.jhtml?nodeId='+nodeId+'&listId=1'; 		
				},dynPageListWindow);	
					
			}else{
				dynPageListWindow.actionType=_type;
				dynPageListWindow.recordId = id;
				var pageGrid = document.getElementById('fr_editor').contentWindow.listMgr.grid;
				if(dynPageListWindow.actionType==0){//查看已推送页
					//pageGrid.topToolbar.disable();
					pageGrid.store.baseParams={//从接口获取参数时传递的参数 listId=4&start=0&limit=10&dynId=11
						dynId :dynPageListWindow.recordId,
						listId :4,
						sort :'[{"field":"id","order":"desc"}]',
						limit: pageGrid.pagesize
					}
					pageGrid.store.load({params:{start:0}});
				}else{
					//pageGrid.topToolbar.enable();
					pageGrid.store.baseParams={//从接口获取参数时传递的参数 listId=4&start=0&limit=10&dynId=11
						nodeId :nodeId,
						listId :1,
						sort :'[{"field":"id","order":"desc"}]',
						limit: pageGrid.pagesize
					}
					pageGrid.store.load({params:{start:0}});
				}
				dynPageListWindow.show();
				dynPageListWindow.doLayout();
			}
			Ext.getCmp('btnPageMgr').setText(_type==0?"删除":"推送");

		}
	}
	
	var toolbarArr = [
	{
		text:'添加',
		iconCls:'addField',
		handler:function(){
			editpannel.form.reset();
			editpannel.dataId=0;
			dialog.show();
		}
	},
	{
		text:'修改',
		iconCls:'modifyField',
		handler:editRecord = function(){
			var selItems = grid.getSelectionModel().selections.items;
			if(selItems.length==1){
				var id = selItems[0].data.id;
				editpannel.dataId = id;
				dialog.show(null,function(){
					loadData(editpannel.dataId );
				});
				
			}else{
				Ext.Toast.show('请选择一条记录进行编辑',{
				   title:'操作提示',
				   buttons: Ext.Msg.OK,
				   minWidth:420,
				   icon: Ext.MessageBox.WARNING  
				});
			}
		}
	},
	{
		text:'删除',
		iconCls:'delField',
		handler:deleteRecord=function(){
			if(grid.getSelectionModel().getCount()==0)
			{
				Ext.Toast.show('请选择要删除的列',{
				   title:'操作提示',
				   buttons: Ext.Msg.OK,
				   minWidth:420,
				   icon: Ext.MessageBox.WARNING  
				});
				return;
			}
			if(!confirm("确定要删除吗?")) return;
			var selItems = grid.getSelectionModel().selections.items;
			var ids=[];
			for(var i=0;i<selItems.length;i++){
				ids.push(selItems[i].data.id);
			}
			deleteData(ids.join(','));
		}
	},
	{
		text:'推送配置',
		handler:pushConfig=function(){
			if(grid.getSelectionModel().getCount()==0)
			{
				Ext.Toast.show('请选择要推送的前端',{
				   title:'操作提示',
				   buttons: Ext.Msg.OK,
				   minWidth:420,
				   icon: Ext.MessageBox.WARNING  
				});
				return;
			}
			var selItems = grid.getSelectionModel().selections.items;
			var ids=[];
			for(var i=0;i<selItems.length;i++){
				ids.push(selItems[i].data.id);
			}
			doRequest('dynamic!pushConfig.jhtml?nodeId='+nodeId+'&ids='+ids.join(','),'推送配置');
		}
	},
	{
		text:'推送页面',
		handler:pushDynPage(1)
	},
	{
		text:'已推送页面',
		handler:pushDynPage(0)
	},	
	
	{
		text:'推送表单',
		handler:pushTable = function(){
			if(grid.getSelectionModel().getCount()==0){
				Ext.Toast.show('请选择要推送的前端',{
				   title:'操作提示',
				   buttons: Ext.Msg.OK,
				   minWidth:420,
				   icon: Ext.MessageBox.WARNING  
				});
				return;
			}
			if(grid.getSelectionModel().getCount()>1){
				Ext.Toast.show('每次只能选择一个前端',{
				   title:'操作提示',
				   buttons: Ext.Msg.OK,
				   minWidth:420,
				   icon: Ext.MessageBox.WARNING  
				});				
				return;
			}
			var id = grid.getSelectionModel().selections.items[0].data.id;
			if(!tableListWindow){
				tableListWindow = new Ext.Window({
					title:'表单列表',
					recordId:id,
					width:453, 
					height:378,
					buttonAlign: "center",
					closable:true ,
					closeAction:'hide',
					autoScroll:true,
					maximizable :true,
					modal:true,
					layout:'fit',
					resizable :true,
					buttons:[{
						'text':'推送',
						handler:function(){
							var selItems = Ext.getCmp('chkFrmList').getValue();
							if(selItems==''){
								Ext.Toast.show('请选择需要推送的表单(可多选)',{
								   title:'操作提示',
								   buttons: Ext.Msg.OK,
								   minWidth:420,
								   icon: Ext.MessageBox.WARNING  
								});				
								return;
							}
							var ids=selItems;
							this.ownerCt.hide();
							doRequest('dynamic!pushTable.jhtml?nodeId='+nodeId+'&id='+this.ownerCt.recordId+'&ids=' + ids,'推表单');
							
						}
					},{
						text:'取消',
						handler:function(){
							this.ownerCt.hide();
						}
					}]
				});
				tableListWindow.show();
				tableListWindow.el.mask("正在获取表单列表...");
				Ext.Ajax.request({ 
					url:'xform!FormListConfig.jhtml?nodeId='+ nodeId +'&formId=0',    
					method:'get',
					success:function(resp,opts){  
						tableListWindow.el.unmask();
						try{	
							var ret = Ext.util.JSON.decode(resp.responseText); 	
						}catch(ex){
							Ext.Msg.alert("获取表单列表失败");	
						}	
						var dataSource = [];
						for(var i=0;i<ret.length;i++){
							dataSource.push([ret[i].id,ret[i].title+'('+ ret[i].id +')']);
						}
						tableListWindow.add({
							xtype:'checkboxgroup',
							width:500,
							id:'chkFrmList',
							dataSource:dataSource
						});	
						tableListWindow.doLayout();
							
					},
					failure:function(resp,opts){
						Ext.Msg.show({
							title:'系统消息',
							msg:'获取表单列表失败失败',
							buttons: Ext.Msg.OK,
							icon: Ext.MessageBox.ERROR
					   });
					}
				});
		
			}else{
				tableListWindow.show();
				Ext.getCmp('chkFrmList').setValue('');
			}
		}
	},
	{
		text:'已推送表单',
		handler:pushedTable = function(){
			if(grid.getSelectionModel().getCount()==0){
				Ext.Toast.show('请选择一条记录',{
				   title:'操作提示',
				   buttons: Ext.Msg.OK,
				   minWidth:420,
				   icon: Ext.MessageBox.WARNING  
				});
				return;
			}
			if(grid.getSelectionModel().getCount()>1){
				Ext.Toast.show('每次只能选择一条记录',{
				   title:'操作提示',
				   buttons: Ext.Msg.OK,
				   minWidth:420,
				   icon: Ext.MessageBox.WARNING  
				});				
				return;
			}
			var id = grid.getSelectionModel().selections.items[0].data.id;
			if(!pushedTableListWindow){
				pushedTableListWindow = new Ext.Window({
					title:'已推送表单列表',
					recordId:id,
					width:453, 
					height:378,
					buttonAlign: "center",
					closable:true ,
					closeAction:'hide',
					autoScroll:true,
					maximizable :true,
					modal:true,
					layout:'fit',
					resizable :true,
					buttons:[{
						text:'关闭',
						handler:function(){
							this.ownerCt.hide();
						}
					}]
				});
				pushedTableListWindow.show();		
			}else{
				pushedTableListWindow.show();
			}
			pushedTableListWindow.body.load('data!list.jhtml','listId=3&start=0&nodeId='+ nodeId +'&dynId=' +id,function(el,success,response){
				try{	
					var ret = Ext.util.JSON.decode(response.responseText); 	
				}catch(ex){
					Ext.Msg.alert("获取表单列表失败");	
				}	
				var html="";
				for(var i=0;i<ret.data.length;i++){
					html+='<li style="margin:5px;width:205px;float:left;">'+ ret.data[i].name+'('+ ret.data[i].id +')</li>';
				}
				if(html==""){
					html="没推送过任何表单！";
				}else{
					html='<ul style="float:left;">'+ html +'</ul>';
				}
				el.update(html);
			});
		}
	}];
	
	//初始化列表页
	var listPanel = new Ext.ux.ListPanel({
		region:'center',
		//style:'padding:1px 5px 0px 5px;',
		id:'listPanel_1',
		layout:'fit',
		frame:false,
		header :false,
		gridConfig:{
			autoExpandColumn:1,//第二列自由宽度
			//pagesize:15,//默认为根据高度自动计算
			rowHeight:26,//行高
			tbar : toolbarArr, //列表顶部工具栏
			storeConfig:{
				url:'dynamic!listData.jhtml',
				remoteSort: false,
				successProperty : 'success',
				root: "rows",
				totalProperty:"total",
				fields: ['id','name','svrIp','status']				
			},
			columnConfig:{
				hasRowNumber:true,//是否显示列序号
				hasSelectionModel:true,//是否需要复选框
				colunms:[{//列表项
					header: "ID",//列表栏标头名称
					sortable: true,//是否支持点击排序
					dataIndex: "id",//绑定的字段名
					align:"center",//对齐方式 left center right
					width:60,//列宽
					tpl:'{id}'//模板，参照Ext.XTemplate的语法
				},{
				   header: "名称",
				   dataIndex: 'name',
				   sortable:true,
				   align:"left",
				   width:200,
				   tpl:'<a href="javascript:editRecord()">{name}</a>'
				},{
				   header: "ip",
				   dataIndex: 'svrIp',
				   sortable:true,				   
				   width:120,
				   align:"center"
				},{
				   header: "状态",
				   dataIndex: 'status',
				   sortable:true,
					width:50,
				   align:"center",
				   tpl:'<tpl if="status==1"><font color="green">启用</font></tpl><tpl if="status==0"><font color="gray">停用</font></tpl>'
				},{
					header: "查看状态",
					width:80,
					align:"center",		
					tpl:'<a href="javascript:showWindowStatus(\'dynamic!dynStatus.jhtml?id={id}&nodeId='+nodeId+'\')">查看状态</a>'
				},{
					header: "推送",
					width:270,
					align:"center",		
					tpl:'<a href="javascript:pushDynPage(1)()">推送页面</a> | <a href="javascript:pushDynPage(0)()">已推送页面</a> | <a href="javascript:pushTable()">推送表单</a> | <a href="javascript:pushedTable()">已推送表单</a>'
				},{
					header: "操作",
					width:100,
					align:"center",		
					tpl:'<a href="javascript:editRecord()">修改</a> | <a href="javascript:deleteRecord()">删除</a>'
				}]
			}
		}
	});
	
	var mainViewport = new Ext.Viewport({
		layout: 'border',
		items:[listPanel]
	});
	var grid = listPanel.grid;
	var ds = grid.store;
			
	ds.baseParams={//从接口获取参数时传递的参数
		nodeId :nodeId,
		limit: grid.pagesize
	}
	ds.reload({params:{start:0}});
	
Ext.createRecordsField("listfield_interface",[{
		header: "参数名",
		dataIndex: 'name',
		width: 150,
		required:true
	},{
		header: "参数描述",
		width: 200,
		required:false,
		dataIndex: 'desc'
	}
]); 

var editpannel =new Ext.form.FormPanel({
	method:'POST',
	fileUpload: true,
	labelAlign: 'center',
	frame: true,
	dataId:0,
	autoHeight:true,
	url: 'data!save.jhtml',
	items:[
		{
			 fieldLabel:"动态前端名称",
			 xtype:"textfield",
			 validateOnBlur:false,
			 id:"name",
			 name:"name",
			 width:400,
			 allowBlank:false,
			 blankText:"不能为空!",			                         
			 
		},
		{
			 fieldLabel:"服务器IP",
			 xtype:"textfield",
			 validateOnBlur:false, 
			 id:"svrIp",
			 name:"svrIp",
			 width:400
		},
		{
			 fieldLabel:"服务器目录",
			 xtype:"textfield",
			 validateOnBlur:false, 
			 id:"svrPath",
			 name:"svrPath",
			 width:400
		},
		{
			 fieldLabel:"数据库IP",
			 xtype:"textfield",
			 validateOnBlur:false, 
			 id:"dbUrl",
			 name:"dbUrl",
			 width:400
		},
		{
			 fieldLabel:"数据库用户名",
			 xtype:"textfield",
			 validateOnBlur:false, 
			 id:"dbUser",
			 name:"dbUser",
			 width:400
		},
		{
			 fieldLabel:"数据库密码",
			 xtype:"textfield",
			 validateOnBlur:false, 
			 id:"dbPwd",
			 name:"dbPwd",
			 width:400
		},
		{
			 fieldLabel:"缓存服务地址",
			 xtype:"textfield",
			 validateOnBlur:false, 
			 id:"cache",
			 name:"cache",
			 width:400
		},
		{
			 fieldLabel:"是否启用",
			 xtype:"combo",
			 id:"status",
			 name:"status",
			 width:400,
			 allowBlank:false,
			 triggerAction:"all",
			 valueField : 'value',
			 displayField : 'text',
			 editable  :false,
			 mode:'local',
			 dataSource:[
						 ['1','启用'],
						 ['0','停用'],
			 ] 
		},{
			xtype:'fieldset',
			ctCls :"itemStyle2",
			title:'搜索配置',
			collapsed:false,
			//height:350,
			autoHeight:true,
			collapsible :true,
			titleCollapse:true,
			style:'margin:10px;',
			autoScroll:true,
			layout: 'xform2',
			items:[{
				 fieldLabel:"读取者IP",
				 xtype:"textfield",
				 validateOnBlur:false, 
				 id:"searchReaderIp",
				 name:"searchReaderIp",
				 emptyText :"Ip地址:端口号(多个地址之间用,号隔开)",
				 width:410,
				 extra:{
					xtype:'label',
					style:'color:green;',
					text:'输入多组用,号隔开'
				 }
			},{
				 fieldLabel:"写入者IP",
				 xtype:"textfield",
				 validateOnBlur:false, 
				 id:"searchWriterIp",
				 name:"searchWriterIp",
				 emptyText :"Ip地址:端口号(多个地址之间用,号隔开)",
				 width:410,
				 extra:{
					xtype:'label',
					style:'color:green;',
					text:'输入多组用,号隔开'
				 }
			},{
				 fieldLabel:"搜索路径",
				 xtype:"textfield",
				 validateOnBlur:false, 
				 id:"searchServerName",
				 name:"searchServerName",
				 width:410
			}]
		},
		{
			fieldLabel :'扩展配置',
			xtype:'listfield_interface',
			name:'ext',
			id:'ext',
			width:400
		}						       						        
	]
            
});
 var dialog = new Ext.Window({
	width:600,
	height:500,
	title:'录入动态前端配置信息',
	closeAction: 'hide',
	autoScroll:true,
	modal:true,
	items: [editpannel],
	buttonAlign:'center',
	buttons: [{
		text: '确定',
		handler:function()
		{
			var form = editpannel.form;  
			//alert(form.getValues().name);  
			var params = form.getValues();//获取表单数据
			params.formId = 2;
			params.id = editpannel.dataId;
			params.nodeId = nodeId;
			Ext.getBody().mask("正在提交保存...");
			Ext.Ajax.request({ 
				url:editpannel.url,  
				method:'POST',                        
				params:params,
				success:function(response,opts){
					Ext.getBody().unmask();
					var ret = Ext.util.JSON.decode(response.responseText);
					if(ret.success){
						editpannel.dataId = ret.id
						//Ext.Msg.alert('消息', '系统保存成功'); 
						dialog.hide(); 
						ds.reload({params:{start:0}});
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
					   msg: "出错！请重试",
					   buttons: Ext.Msg.OK,
					   animEl: 'elId',
					   minWidth:420,
					   icon: Ext.MessageBox.ERROR  
					});
				}
			});  		            
		}
	},{
		text: '取消',
		handler: function() { 
			dialog.hide();
		}.createDelegate(this)
	}]
})
function loadData(id)
{
	dialog.el.mask("正在获取数据...");
	var dataUrl = 'data!data.jhtml?nodeId='+nodeId+'&formId=2&id='+id;
	Ext.Ajax.request({ 
		url:dataUrl,    
		method:'get',
		success:function(resp,opts){    
			dialog.el.unmask();
			var jsonResult = Ext.util.JSON.decode(resp.responseText); 
			var form = editpannel.form;
			form.setValues(jsonResult);
						  
		},
		failure:function(resp,opts){
			dialog.el.unmask();
			Ext.Msg.show({
			   title:'系统消息',
			   msg:'数据加载失败',
			   buttons: Ext.Msg.OK,
			   icon: Ext.MessageBox.ERROR
			});
		}
	});
}
function deleteData(ids)
{
   Ext.Msg.show({
   title:'系统消息',
   msg: '数据删除后不可恢复，你确定要删除吗？',
   buttons: Ext.Msg.YESNO,
   fn: function(rtn){
	   if(rtn=='yes')
		{
			var dataUrl = 'data!delete.jhtml?nodeId='+nodeId+'&listId=2&ids='+ids;
			Ext.Ajax.request({ 
				url:dataUrl,    
				method:'get',
				success:function(resp,opts){             
						var jsonResult = Ext.util.JSON.decode(resp.responseText); 
						if(!jsonResult.success)
						{
							//Ext.Msg.alert('系统提示',jsonResult.message);
							Ext.Msg.show({
							   title:'系统消息',
							   msg:jsonResult.message,
							   buttons: Ext.Msg.OK,
							   icon: Ext.MessageBox.ERROR});
						}
						else
						{
							ds.reload({params:{start:0}});
						}
																		  
				},
				failure:function(resp,opts){
					Ext.Msg.show({
					   title:'系统消息',
					   msg:'删除失败',
					   buttons: Ext.Msg.OK,
					   icon: Ext.MessageBox.ERROR
				   });
				}
			});
		}
   },
   animEl: 'elId',
   icon: Ext.MessageBox.QUESTION
	});		
}
function doRequest(url,title)
{
	Ext.Msg.show({
		title:'系统消息',
		msg: '你确定要'+title+'吗？',
		buttons: Ext.Msg.YESNO,
		fn: function(rtn){
		   if(rtn=='yes')
			{
				var dataUrl = url;
				Ext.getBody().mask("正在处理中...");
				Ext.Ajax.request({ 
					url:dataUrl,    
					method:'get',
					success:function(resp,opts){      
							Ext.getBody().unmask();
							var jsonResult = Ext.util.JSON.decode(resp.responseText); 
							if(!jsonResult.success)
							{
								Ext.Msg.show({
								   title:'系统消息',
								   msg:jsonResult.message,
								   buttons: Ext.Msg.OK,
								   icon: Ext.MessageBox.ERROR
							   });
							}
							else
							{
								//Ext.Msg.alert('系统提示',title+'成功');
								Ext.Toast.show(title+'成功',{
								   title:'系统提示',
								   buttons: Ext.Msg.OK,
								   minWidth:420,
								   icon: Ext.MessageBox.INFO
								});
							}
																			  
					},
					failure:function(resp,opts){
						Ext.getBody().unmask();
						Ext.Msg.show({
							title:'系统消息',
							msg:title+'失败',
							buttons: Ext.Msg.OK,
							icon: Ext.MessageBox.ERROR
					   });
					}
				});
			}
		},
	    animEl: 'elId',
	    icon: Ext.MessageBox.QUESTION
	});	
}

function showWindowStatus(url){
	 var win = new Ext.Window({
		 title:"动态前端状态",
		width:1050,
		height:550,
		autoScroll:true,
		layout:'auto',
		buttonAlign:'center',
		resizable : false, 
		draggable:false,
		constrain:true,//将拖动范围限制在容器内
		autoDestroy:false,
		modal : true,
		html:"<iframe src='" + url + "' frameborder=\"0\" width=\"100%\" height=\"100%\"></iframe>"
		/*,
		buttons:[{
			text:'关闭',
			handler:function(){
				this.ownerCt.close();
			}
		}]*/
	  });
	win.show();
};


Ext.onReady(function(){
	Ext.BLANK_IMAGE_URL = "../res/js/ext2/resources/images/default/s.gif";
	Ext.QuickTips.init();
	Ext.form.Field.prototype.msgTarget = 'qtip';

});
</script>
</body>
</html>