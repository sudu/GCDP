 <!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title>脚本流程列表</title>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />

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
		
		
/*****for table begin*****/ 
.customers   
  {   
  width:100%;   
  border-collapse:collapse;   
  }   
   
.customers  td, .customers  th    
  {   
  font-size:1em;   
  border:1px solid #99BBE8;   
  padding:3px 7px 2px 7px;   
  }   
   
.customers  th    
  {   
  font-size:1.1em;   
  text-align:left;   
  padding-top:5px;   
  padding-bottom:4px;   
  background-color:#99BBE8;   
  color:#ffffff;   
  }   
   
.customers  tr.alt td    
  {   
  color:#000000;   
  background-color:#EAF2D3;   
  }   
/*****for table end*****/ 
	</style>

 	<script type="text/javascript" src="../res/js/ext2/adapter/ext/ext-base.js"></script>
    <script type="text/javascript" src="../res/js/ext2/ext-all-debug.js"></script>
	<script type="text/javascript" src="../res/js/ext2/ext-lang-zh_CN.js"></script>
	<script type="text/javascript" src="../res/js/ext_base_extension.js"></script>
	<script type="text/javascript" src="../res/js/controls/Ext.ux.ListPanel.js"></script>
	<script type="text/javascript">
		var params__ = Ext.parseQuery();
		var globalvars = {
			nodeId:params__.nodeId,
			proccessId:#{id!0},
			params:params__
		}
	</script>

	
</head>
<body style="padding:1px;">

	
<script type="text/javascript">

var openPage = function(url,title){
	if(top && top.openTab){
		top.openTab(url,title);
	}else{
		window.open(url);
	}
}

</script>	

<script>
var listMgr = {
	grid:null,
	store:null,
	init:function(){
		var toolbarArr = [{
			xtype: 'tbfill'
		},{
			xtype:'label',
			text:'名称：'
		},{
			xtype:'textfield',
			id:'txtSearch',
			emptyText:'输入流程名称(支持模糊搜索)',
			width:180		
		},{
			xtype: 'tbbutton',
			cls: 'x-btn-text-icon',
			text:'搜索',
			icon:'../res/img/search.gif',
			handler:this.doSearch.createDelegate(this)
		}];
		
		var paramsStr = 'nodeId='+ globalvars.nodeId;
		//初始化列表页
		this.listPanel = new Ext.ux.ListPanel({
			region:'center',
			//style:'padding:1px 5px 0px 5px;',
			id:'placeholder',
			layout:'fit',
			frame:false,
			header :false,
			gridConfig:{
				hasPageBar:true,//是否需要分页
				autoExpandColumn:1,//第二列自由宽度
				//pagesize:15,//默认为根据高度自动计算
				rowHeight:26,//行高
				//tbar : toolbarArr, //列表顶部工具栏
				storeConfig:{
					url:"workflowMonitor!queryProcessInstance.jhtml",
					remoteSort: false,
					successProperty : 'success',
					successProperty : 'success',
					root : "data",
					totalProperty : "totalCount", 
					fields: ['id','nodeId','formId','articleId','formName','state','processDefinitionId','processStartDate','processStartDate','instanceDesc']
				},
				columnConfig:{
					hasRowNumber:true,//是否显示列序号
					hasSelectionModel:true,//是否需要复选框
					colunms:[{//列表项
						header: "ID",//列表栏标头名称
						sortable: true,//是否支持点击排序
						dataIndex: "id",//绑定的字段名
						align:"center",//对齐方式 left center right
						width:50,//列宽
						tpl:'{id}'//模板，参照Ext.XTemplate的语法
					},{
					   header: "调用者名称",
					   dataIndex: 'formName',
					   sortable:true,
					   align:"left"
					},{
					   header: "nodeId",
					   dataIndex: 'nodeId',
					   sortable:true,
					   align:"left",
					   width:60
					},{
					   header: "formId",
					   dataIndex: 'formId',
					   sortable:true,
					   align:"left",
					   width:60
					},{
					   header: "id",
					   dataIndex: 'articleId',
					   sortable:true,
					   align:"left",
					   width:60
					},{
					   header: "开始执行时间",
					   dataIndex: 'processStartDate',
					   sortable:true,
					   align:"left",
					   width:130
					},{
					    header: "状态",
					    width:60,
						align:"center",
						dataIndex:'state',
						tpl:'{state}'
					},{
					    header: "描述",
					    width:150,
						dataIndex:'instanceDesc',
						tipTpl:'{instanceDesc}'
					},{
					    header: "查看监控",
					    width:80,	
						dataIndex:'id',
						align:"center",	
						tpl:'<a href="#" onclick="openPage(\'workflowMgr!monitor.jhtml?nodeId='+ globalvars.nodeId + '&processId={processDefinitionId}&instanceId={id}\',\'查看监控\')"  title="点击查看监控">查看监控</a>'
						
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
			nodeId: globalvars.nodeId,
			id:globalvars.proccessId
		}
		this.store.load({
			params:{start:0,limit:this.grid.pagesize}
		});//初次搜索
		this.listenerKeybord();
	},
	/*添加 修改 删除 搜索*/
	doSearch:function(){
		var name = Ext.getCmp('txtSearch').getValue();
		var reg = eval("/"+name+"/ig");
		listMgr.store.filter("formName",reg);
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
	}
};

Ext.onReady(function(){
	Ext.BLANK_IMAGE_URL = "../res/js/ext2/resources/images/default/s.gif";
	Ext.QuickTips.init();
	Ext.form.Field.prototype.msgTarget = 'qtip';
	
	listMgr.init();		
});
</script>		

<script>		

/*******onMessage处理******/
function onMessageHandler(e){
	var dataStr = e.data;
	try{
		var dataJson = Ext.util.JSON.decode(dataStr);
		if(dataJson.message){
			var message = Ext.decode(decodeURIComponent(dataJson.message));
			var hanlder = message.handler;//处理函数
			var scope = message.scope;
			if(hanlder){
				eval('0,' + hanlder + '.call(' + scope + ',"'+ encodeURIComponent(Ext.encode(message.data)) + '","'+ encodeURIComponent(Ext.encode(dataJson.data)) +'")');
			}
		}
	}catch(ex){
		console.log(ex);
	}
	
}
//监听postMessage消息事件
if (typeof window.addEventListener != 'undefined') {
	window.addEventListener('message', onMessageHandler, false);
} else if (typeof window.attachEvent != 'undefined') {
	window.attachEvent('onmessage', onMessageHandler);
}
</script>
	
</body>
</html>	
