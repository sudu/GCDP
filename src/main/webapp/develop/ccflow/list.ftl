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
			text : "新建", 
			iconCls : 'addField', 
			handler : this.addRecord.createDelegate(this)

		}, { 
			text : "编辑", 
			iconCls : 'modifyField', 
			handler : this.updateRecord.createDelegate(this)
		},{
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
				hasPageBar:false,//不需要分页
				autoExpandColumn:2,//第二列自由宽度
				//pagesize:15,//默认为根据高度自动计算
				rowHeight:26,//行高
				tbar : toolbarArr, //列表顶部工具栏
				storeConfig:{
					url:'workflowMgr!getExistingProcessList.jhtml',
					remoteSort: false,
					successProperty : 'success',
					root : "data",
					fields: ['id','processTitle','createTime','creator','recentModifyTime','status']
				},
				columnConfig:{
					hasRowNumber:false,//是否显示列序号
					hasSelectionModel:true,//是否需要复选框
					colunms:[{//列表项
						header: "ID",//列表栏标头名称
						sortable: true,//是否支持点击排序
						dataIndex: "id",//绑定的字段名
						align:"center",//对齐方式 left center right
						width:50,//列宽
						tpl:'{id}'//模板，参照Ext.XTemplate的语法
					},{
					    header: "编辑",
					    width:50,
						align:"center",		
						dataIndex:'id',
						tpl:'<a href="javascript:listMgr.updateRecord()">编辑</a>'
					},{
					   header: "名称",
					   dataIndex: 'processTitle',
					   sortable:true,
					   align:"left",
					   tpl:'<a href="javascript:listMgr.updateRecord()">{processTitle}</a>'
					},{
					   header: "创建者",
					   dataIndex: 'creator',
					   sortable:true,
					   align:"left",
					   width:80
					},{
					   header: "创建时间",
					   dataIndex: 'createTime',
					   sortable:true,
					   align:"left",
					   width:130
					},{
					   header: "最后修改时间",
					   dataIndex: 'recentModifyTime',
					   sortable:true,
					   align:"left",
					   width:130
					},{
					    header: "状态",
					    width:50,
						align:"center",		
						dataIndex:'status',
						tpl:'<tpl if="status==0"><span style="color:#000">设计中...</span></tpl><tpl if="status&&(status==-1)"><span style="color:#BDBDBD"><FONT style="TEXT-DECORATION:line-through">已下线</font></span></tpl><tpl if="status==1"><span style="color:#5FB404">已上线</span></tpl>'
					},{
					    header: "调用者",
					    width:80,	
						dataIndex:'id',
						align:"left",	
						tpl:'<a href="javascript:void(0);" onclick="openPage(\'workflowMonitor!list.jhtml?nodeId='+ globalvars.nodeId + '&id={id}\',\'调用者实例列表\')" title="点击查看调用者">调用者</a>'
					},{
					    header: "日志",
					    width:50,	
						dataIndex:'id',
						align:"center",	
						tpl:'<a href="#" onclick="openPage(\'../runtime/getScriptLog.jhtml?nodeId='+ globalvars.nodeId +'&id1={id}&id2=null&stype=process\',\'查看日志\')"  title="点击查看运行日志">日志</a>'
					},{
					   header: "操作",
					   dataIndex: 'id',
					   sortable:true,
					   align:"center",
					   width:100,
					   tpl:'<a href="javascript:listMgr.setStatus({id},1)">上线</a> | <a href="javascript:listMgr.setStatus({id},-1)">下线</a>'
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
			nodeId: globalvars.nodeId
		}
		this.store.load();//初次搜索
		this.listenerKeybord();
	},
	/*添加 修改 删除 搜索*/
	addRecord:function(){
		openPage('workflowMgr.jhtml?nodeId='+ globalvars.nodeId + '&processId=0&' + this.setPostMessageParams(0),'新建流程');
	},
	updateRecord:function(){
		var selItems = listMgr.grid.getSelectionModel().selections.items;
		if(selItems.length==1){
			var id = selItems[0].data.id;
			
			openPage('workflowMgr.jhtml?nodeId='+ globalvars.nodeId + '&processId=' + id + '&' + this.setPostMessageParams(id),'修改流程-'+selItems[0].data.processTitle);
		}else{
			Ext.Toast.show('请选择一条记录',{
			   title:'操作提示',
			   buttons: Ext.Msg.OK,
			   minWidth:420,
			   icon: Ext.MessageBox.WARNING  
			});
		}
	},
	setStatus:function(id,status){
		if(!confirm("确定要" + (status==1?"上线":"下线") + "吗?")) return;
	
		Ext.getBody().mask("正在处理中...");
		Ext.Ajax.request({  
			url:'workflowMgr!enableOrDisableProcess.jhtml?nodeId='+globalvars.nodeId +'&id=' + id+"&status=" +  status,
			method:"get",
			success:function(response,opts){
				Ext.getBody().unmask();
				var ret = Ext.util.JSON.decode(response.responseText);
				if(ret.success){
					Ext.Toast.show('操作成功',{
						title:'提示',
						buttons: Ext.Msg.OK,
						icon: Ext.MessageBox.INFO,  
						time:1000,
						minWidth:420
					});
					listMgr.store.load();
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
				   msg: decodeURIComponent(response.responseText),
				   buttons: Ext.Msg.OK,
				   animEl: 'elId',
				   minWidth:420,
				   icon: Ext.MessageBox.ERROR  
				});
			}
		});
	},
	setPostMessageParams:function(id){
		var messageData = {
			handler:'listMgr.reloadRecord',
			scope:'listMgr',
			data:{id:id},
			hostFrameId:window.frameElement?window.frameElement.id:null
		};

		var postMessageParams = 'postMessage__=' + encodeURIComponent(Ext.encode(messageData));
		return postMessageParams;
	},
	doSearch:function(){
		var name = Ext.getCmp('txtSearch').getValue();
		var reg = eval("/"+name+"/ig");
		listMgr.store.filter("processTitle",reg);
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
	reloadRecord:function(realData,receiveData){
		realData = Ext.decode(decodeURIComponent(realData));
		receiveData = Ext.decode(decodeURIComponent(receiveData));
		var id=realData.id;
		if(id===0){
			this.store.load();
		}else{
			var index = this.store.find("id",id);
			var record = this.store.getAt(index);
			for(var key in receiveData){
				record.set(key,receiveData[key]);	
			}
		}
	}
};

var monitor = {
	viewCaller:function(proccessId,obj){		
		Ext.fly(obj).parent("div.x-grid3-row").mask("正在获取调用者信息......");
		Ext.Ajax.request({  
			url:"workflowMonitor!queryProcessInstance.jhtml?nodeId="+globalvars.nodeId+"&id=" + proccessId,  
			method:"get",  
			options:{sender:obj},
			success:function(response,opts){
				var rowEl = Ext.fly(opts.options.sender).parent("div.x-grid3-row");
				rowEl.unmask();
				try{
					var ret = Ext.util.JSON.decode(response.responseText);
				}catch(ex){
					ret = {success:false,message:"接口错误"};
				}			
				if(!ret.success){
					Ext.Msg.show({
					   title:'错误提示',
					   msg: ret.message?decodeURIComponent(ret.message):"很抱歉，请求失败",
					   buttons: Ext.Msg.OK,
					   animEl: 'elId',
					   minWidth:420,
					   icon: Ext.MessageBox.ERROR 
					});
				}else{
					//显示
					if(ret.data.length==0){
						var senderEl = Ext.fly(opts.options.sender);
						var infoEl = senderEl.parent().createChild({
							tag:'span',
							html:'无调用'
						});
						infoEl.fadeOut({
							endOpacity: 0, //can be any value between 0 and 1 (e.g. .5)
							easing: 'easeOut',
							duration: 1,
							useDisplay: false,
							callback:function(){
								infoEl.remove();
							}
						});
						return;
					}
					
					var pos = {x:rowEl.getLeft(),y:rowEl.getTop()};
					var tpl = new Ext.XTemplate(
						'<div id="instanceTable" style="z-index:90;visibility:hidden;background:#fff;position:absolute;left:{x}px;top:{y}px;">',
						'<table class="customers">',   
						'<tr>',    
						'<th>调用者名称</th>',    
						'<th>id</th>',    
						'<th>formId</th>',    
						'<th>nodeId</th>',    
						'<th>状态</th>',    
						'<th>开始执行时间</th>',   
						'<th>描述</th>',
						'<th style="text-align:right"><a href="javascript:void(0);" class="W_close" title="关闭" node-type="close">关闭</a></th>',
						'</tr>',    
						'<tpl for="data">',
							'<tr>',   
							'<td>{formName}</td>',   
							'<td>{articleId}</td>',   
							'<td>{formId}</td>',   
							'<td>{nodeId}</td>',   
							'<td>{state}</td>',   
							'<td>{processStartDate}</td>',   
							'<td>{instanceDesc}</td>',   
							'<td><a href="javascript:openPage(\'workflowMgr!monitor.jhtml?nodeId={nodeId}&processId={processDefinitionId}&instanceId={id}\',\'流程运行状态-{formName}\')">查看监控</td>',   
							'</tr>',
					    '</tpl>', 
						'</table>',
						'</div>'); 
					var tableEl = Ext.get("instanceTable");
					if(tableEl) tableEl.remove();
					
					tableEl= tpl.insertBefore(Ext.getBody().first(), {data:ret.data,x:pos.x,y:pos.y});
					tableEl = Ext.fly(tableEl);	
					
					tableEl.fadeIn({
						endOpacity: 1,  
						easing: 'easeIn',
						duration: .3,
						useDisplay: false
					});
					
					var closeEl = tableEl.child("a.W_close");
					closeEl.on("click",function(e,obj,opts){
						var tableEl = Ext.get("instanceTable");
						tableEl.fadeOut({
							endOpacity: 0, //can be any value between 0 and 1 (e.g. .5)
							easing: 'easeOut',
							duration: .3,
							useDisplay: false,
							callback:function(){
								tableEl.remove();
							}
						});
					});
					tableEl.setLeft(listMgr.grid.el.getWidth()-	tableEl.getWidth());	
					tableEl.setOpacity(.9);
					
				}
			},
			failure:function(response,opts){
				//Ext.Msg.alert('错误提示',response.responseText)	;
				Ext.fly(opts.options.sender).parent("div.x-grid3-row").unmask();
				Ext.Msg.show({
					   title:'错误提示',
					   msg: ret?decodeURIComponent(ret.message):"很抱歉，请求过程出现异常",
					   buttons: Ext.Msg.OK,
					   minWidth:420,
					   icon: Ext.MessageBox.ERROR 
				});

			}		
		});	
		
	}
}


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
