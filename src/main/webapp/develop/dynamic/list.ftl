 <!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <title>页面配置</title>

	<link rel="stylesheet" type="text/css" href="../../res/js/ext2/resources/css/ext-all.css" />
	<link rel="stylesheet" type="text/css" href="../../res/js/ext2/resources/css/patch.css" />
 	<script type="text/javascript" src="../../res/js/ext2/adapter/ext/ext-base.js"></script>
    <script type="text/javascript" src="../../res/js/ext2/ext-all-debug.js"></script>	
	<script type="text/javascript" src="../../res/js/ext2/ext-lang-zh_CN.js"></script>
	<script type="text/javascript" src="../../res/js/ext_base_extension.js"></script>
	<script type="text/javascript" src="../../res/js/controls/Ext.ux.ListPanel.js"></script>
	<style>
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
			margin-right: 5px;
		}		
		/*按钮*/
		.addField{background:url("../../res/js/ext2/resources/images/default/dd/drop-add.gif") left  no-repeat !important;}
		.delField{background:url("../../res/js/ext2/resources/images/default/my/del-form.gif") left  no-repeat !important;}
		.modifyField{background:url("../../res/js/ext2/resources/images/default/my/modify-view.gif") left  no-repeat !important;}
		
		/*****for gridpanel***/
		.x-grid3-cell-inner, .x-grid3-hd-inner {
			overflow: hidden;
			padding: 5px 3px 5px 5px;
			white-space: nowrap;
		}
		td a {
			color:#004A99;
		}
		td a:hover {
			color:#BA2636;
		}
		/*****for gridpanel end*****/
	</style>
<script type="text/javascript">
	
openTab = function(url,title){
	if(!title) title='';
	if(top&& top.centerTabPanel){
		var _url = 'dynamic/' + url;
		top.centerTabPanel.addIframe('tab_' + escape(_url),title ,_url);
	}else{
		window.open(url);	
	}
}
</script>

</head>
<body>

<script type="text/javascript">
var params__ = Ext.parseQuery();

var listMgr = {
	grid:null,
	store:null,
	init:function(){
		var toolbarArr = [{ 
			text : "新建", 
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
		},{
			xtype: 'tbfill'
		},{
			xtype:'label',
			text:'名称：'
		},{
			xtype:'textfield',
			id:'txtSearch',
			emptyText:'输入名称',
			width:180		
		},{
			cls: 'x-btn-text-icon',
			text:'搜索',
			icon:'../../res/img/search.gif',
			handler:this.doSearch
		}];
		
		var paramsStr = 'id={id}&nodeId='+ params__.nodeId;
		//初始化列表页
		this.listPanel = new Ext.ux.ListPanel({
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
					url:'../data!list.jhtml',
					remoteSort: false,
					successProperty : 'success',
					root : "data",
					totalProperty : "totalCount",
					fields: ['id','name','createDateTime','author','url']
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
					   tpl:'<a href="javascript:openTab(\'view.jhtml?formId=1&' + paramsStr + '\',\'编辑页面{name}\')">{name}</a>'
					},{
					   header: "页面URL",
					   dataIndex: 'url',
					   sortable:false,
					   width:250,
					   align:"left",
					   tpl:'{url}'
					},{
					   header: "创建者",
					   dataIndex: 'author',
					   sortable:true,
					   align:"center",
					   tpl:'{author}'
					},{
					   header: "创建时间",
					   dataIndex: 'createDateTime',
					   width:150,
					   sortable:true,
					   align:"center",
					   tpl:'{createDateTime}'
					},{
					    header: "编辑",
					    width:50,
						align:"center",		
						dataIndex:'id',
					    tpl:'<a href="javascript:openTab(\'view.jhtml?formId=1&' + paramsStr + '\',\'编辑页面{name}\')">编辑</a>'
					}]
				}
			}
		});
		
		var mainViewport = new Ext.Viewport({
			layout: 'border',
			items:[this.listPanel]
		});
		this.grid = this.listPanel.grid;
		this.store = this.grid.store;
				
		this.store.baseParams={//从接口获取参数时传递的参数
			nodeId :params__.nodeId,
			listId :1,
			sort :'[{"field":"id","order":"desc"}]',
			limit: this.grid.pagesize
		}
		this.store.load({params:{start:0}});//初次搜索,默认从DB搜索
		this.listenerKeybord();
	},
	/*添加 修改 删除 搜索*/
	addRecord:function(){
		openTab("view.jhtml?nodeId="+ params__.nodeId +"&formId=1&id=0" ,'新建页面');
	},
	updateRecord:function(){
		var selItems = listMgr.grid.getSelectionModel().selections.items;
		if(selItems.length==1){
			var id = selItems[0].data.id;
			openTab("view.jhtml?nodeId="+ params__.nodeId +"&formId=1&id="+ id ,'编辑页面');
		}else{
			Ext.Toast.show('请选择一条记录',{
			   title:'操作提示',
			   buttons: Ext.Msg.OK,
			   minWidth:420,
			   icon: Ext.MessageBox.WARNING  
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
							url:'data!delete.jhtml?listId=1&ids=' + ids.join(','), 
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
								   msg: decodeURIComponent(response.responseText),
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
	doSearch:function(){
		var name = Ext.getCmp('txtSearch').getValue();
		listMgr.store.baseParams.where='[{"field":"name","op":"like","value":"'+ name + '","andor":"and"}]'; 
		listMgr.store.baseParams.limit=listMgr.grid.pagesize;
		listMgr.store.load({
			params:{
				start:0
			},
			callback: function(r, options, success){  
				if(!success){   
					Ext.Msg.alert('操作','失败！');  
				}  
			}
		});
	},
	//绑定键盘事件
	listenerKeybord:function(){
		new Ext.KeyMap(Ext.get('txtSearch'), {
			key: Ext.EventObject.ENTER,
			fn: function(){
				this.doSearch();
			},
			scope: this
		});	
	},

}

Ext.onReady(function(){
	Ext.BLANK_IMAGE_URL = "../../res/js/ext2/resources/images/default/s.gif";
	Ext.QuickTips.init();
	Ext.form.Field.prototype.msgTarget = 'qtip';
	
	listMgr.init();		
});

</script>	

</body>	
</html>