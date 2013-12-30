<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>流程图设计</title>
	<link rel="stylesheet" type="text/css" href="../res/js/ext2/resources/css/ext-all.css" />
	<link rel="stylesheet" type="text/css" href="../res/js/ext2/resources/css/patch.css" />
 	<script type="text/javascript" src="../res/js/ext2/adapter/ext/ext-base.js"></script>
	<script type="text/javascript" src="../res/js/ext2/ext-all-debug.js"></script>	
	<script type="text/javascript" src="../res/js/ext2/ext-lang-zh_CN.js"></script>
	<script type="text/javascript" src="../res/js/ext_base_extension.js"></script>
	<script type="text/javascript" src="../res/js/controls/listField.js" ></script>
	<script type="text/javascript" src="../res/js/CookiesHelper.js"></script>
    <script type="text/javascript" src="ccflow/js/lib/raphael.js"></script>
	<script type="text/javascript" src="ccflow/js/ccWindow.js?v=20131017"></script>
	<script type="text/javascript" src="ccflow/js/ccGraphCodec.js?v=20121207"></script>
	<script type="text/javascript" src="ccflow/js/ccWFlow.js?v=20121207"></script>
    <style type="text/css" media="screen">
        #holder{
            top: 0px;
            left: 0px;
            right: 0px;
            bottom: 0px;
            position: absolute;
            z-index:999;
        }
		#graph{position: absolute;width:100%;height:100%;overflow:auto;cursor:default;background: url(ccflow/images/grid.gif);z-index:999;}
        p{text-align: center;}
		#toolbar {margin:10px;text-align: center;}
		#toolbar img{margin-bottom:10px;cursor:move;}
    </style>
	
	<script type = "text/javascript">
		var globalvars = {
			nodeId:#{nodeId!0},
			id:#{processId!0},
			userName:Cookies.get('cmpp_cn'),
			dateTime:"${dateTimeStr!""}"
		}
	</script>
		
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
		
    function mxApplication(){
		var config = {
			locked:false,
			"graph":{
				"container":Ext.getCmp("graphContainer"),
				<#if flowDefInfoJSON!="">"workflow":${flowDefInfoJSON!""},</#if>
				"property":{
					"title":"流程图标题xxxxxxx",
					"author":globalvars.userName,
					"createTime":globalvars.dateTime
				}
			},
			"propertyEditor":{
				"container":Ext.getCmp("propertyContainer"),
			},			
			"toolbar":{
				"container":Ext.getCmp("toolbarContainer"),
				"buttons":[{
					text:"开始",
					template:"begin",
					icon:"images/ellipse-begin.jpg",
					visible:false,
					isBeginCell:true,
					beConnectable:false,
					connectable:true,
					chilrenLimit:1 //儿子数限制
				},{
					text:"活动",
					template:"rectangle",
					icon:"ccflow/images/rectangle.jpg"
					
				},{
					text:"条件",
					template:"rhombus",
					icon:"ccflow/images/rhombus.jpg"
				},{
					text:"文本",
					template:"text",
					icon:"ccflow/images/text.jpg",
					identifyVisible:false,//序号是否可见
					isTextCell:true,
					beConnectable:false,
					connectable:false
				},{
					text:"结束",
					template:"end",
					icon:"ccflow/images/ellipse-end.jpg",
					visible:false,
					isEndCell:true,
					beConnectable:true,
					connectable:false
				}]
			},
			"templates":{
				"begin":{
					width:80,
					height:40,
					style:"ellipse",
					label:"开始",
					shapeType:"ellipse"
				},
				"rectangle":{
					width:80,
					height:40,
					style:"ellipse",
					label:"活动",
					shapeType:"rectangle"
				},
				"rhombus":{
					width:60,
					height:60,
					style:"rhombus",
					label:"条件",
					shapeType:"rhombus"
				},
				"text":{
					width:80,
					height:40,
					style:"text",
					label:"文本",
					shapeType:"text"
				},
				"end":{
					width:80,
					height:40,
					style:"ellipse",
					label:"结束",
					shapeType:"ellipse"
				}
			},
			"styles":{
				"rectangle":{align:"center",fillColor:"#C3D9FF",fontSize:12,gradientColor:"white",perimeter:"rectanglePerimeter",shadow:1,shape:"rectangle",strokeColor:"#C3D9FF",verticalAlign:"middle"},
				"text":{align:"center",fillColor:"transparent",fontSize:12,gradientColor:"transparent",perimeter:"rectanglePerimeter",shadow:0,shape:"rectangle",strokeColor:"transparent",verticalAlign:"middle"},
				"rhombus":{align:"center",fillColor:"#FFCF8A",fontSize:12,gradientColor:"white",perimeter:"rhombusPerimeter",shadow:1,shape:"rhombus",strokeColor:"#FFCF8A",verticalAlign:"middle"}
			}
		}
		window.editor = new ccEditor(config);

    }
	
	</script>
	
</head>
<body>
<script>
function saveGraph(config){
	Ext.getBody().mask("正在保存,请稍候......");
	var content = Ext.encode(config);
	var g = config.graph;
	Ext.Ajax.request({  
		url:"workflowMgr!saveOrUpdateProcess.jhtml",  
		params:{nodeId:globalvars.nodeId,existingProcessId:globalvars.id,flowDefInfo:content},
		method:"POST",  
		options:{
			processTitle:g.title,
			status:0
		},
		success:function(response,opts){
			Ext.getBody().unmask();
			try{
				var ret = Ext.util.JSON.decode(response.responseText);
			}catch(ex){
				ret = {success:false,message:"接口错误"};
			}			
			if(!ret.success){
				Ext.Msg.show({
				   title:'错误提示',
				   msg: ret.message?decodeURIComponent(ret.message):"很抱歉，保存失败",
				   buttons: Ext.Msg.OK,
				   animEl: 'elId',
				   minWidth:420,
				   icon: Ext.MessageBox.ERROR 
				});
			}else{
				Ext.Toast.show('保存成功',{
					title:'提示',
					buttons: Ext.Msg.OK,
					animEl: 'elId',
					minWidth:420,
					icon: Ext.MessageBox.INFO ,
					callback:function(){
						var parentMsg = Ext.parseQuery().postMessage__;
						var parentMsgJson = Ext.decode(decodeURIComponent(Ext.parseQuery().postMessage__));
						
						var postData = {
							data:opts.options,
							message: parentMsg //从URL接收到的数据
						};
						if(!window.opener && parentMsgJson.hostFrameId){//Ext的tabPanel页签之间的通讯
							window.parent.Ext.get(parentMsgJson.hostFrameId).dom.contentWindow.postMessage(Ext.encode(postData), '*'); 
						}else{
							window.opener && window.opener.postMessage(Ext.encode(postData), '*');
						}
						if(globalvars.id==0){
							location.href = "workflowMgr.jhtml?nodeId=" + globalvars.nodeId + "&processId=" + ret.id + (parentMsg?("&postMessage__=" + parentMsg):"");
						}
					}
				});
			}
		},
		failure:function(response,opts){
			//Ext.Msg.alert('错误提示',response.responseText)	;
			Ext.getBody().unmask();
			Ext.Msg.show({
				   title:'错误提示',
				   msg: "很抱歉，保存过程出现异常",
				   buttons: Ext.Msg.OK,
				   minWidth:420,
				   icon: Ext.MessageBox.ERROR 
			});

		}		
	});	
}

Ext.onReady(function(){
	Ext.BLANK_IMAGE_URL = "../res/js/ext2/resources/images/default/s.gif";
	Ext.lOADING_IMAGE_URL= "../res/js/ext2/resources/images/default/grid/wait.gif"; //resources\images\default\grid
	Ext.QuickTips.init();
	Ext.form.Field.prototype.msgTarget = 'qtip';
	
	new Ext.Viewport({
		layout:'border',
		items:[{
			xtype: 'toolbar',
			region: 'north',
			items:[/*{
				xtype:'tbfill',
			},*/{
				text:"通过JSON导入",
				tip:'导入另存的JSON',
				handler:function(){
					var win =new Ext.Window({
						title:"导入JSON",
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
									var win = this.ownerCt;
									var configStr = win.items.items[0].getValue();
									var config = Ext.util.JSON.decode(configStr);
									
									editor.graphCodec = new ccGraphCodec(editor,config);
									editor.initWorkflow();
									
									win.close();
								}catch(ex){
									Ext.Msg.show({
										   title:'错误提示',
										   msg: 'JSON格式有误!<br />' + ex.toString(),
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
								var win = this.ownerCt;
								win.close();
							}
						}]
						
					});
					win.setAnimateTarget('btnFormImport');
					win.show();
				}
			},{
				text:'导出为JSON',
				handler:function(){
					var json = window.editor.graphCodec.encode();
					
					var win =new Ext.Window({
						title:"导出代码",
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
							readOnly:true,
							value:Ext.CMPP.JsonUti.toString(json)
						}],
						buttons:[{
							text:'保存',
							scope:json,
							handler:function(obj){
								saveGraph(this);
								var win = obj.ownerCt;
								win.close();
							}
						},{
							text:'取消',
							handler:function(){
								var win = this.ownerCt;
								win.close();
							}
						}]
						
					});
					win.show();
				}
			},{
				text:'保存(Ctrl+S)',
				handler:function(){
					var json = window.editor.graphCodec.encode();
					saveGraph(json);
				}
			},{
				xtype:'tbfill',
			},{
				text:'查看日志',
				handler:function(){
					openPage('../runtime/getScriptLog.jhtml?nodeId='+ globalvars.nodeId +'&id1='+ globalvars.id +'&id2=null&stype=process','查看日志');
				}
			}]
		},{
			xtype:'panel',
			title:'图形库',
			region: 'west',
			width: 100,
			split: true,
			collapsible: true,
			autoScroll: true,
			layout:'accordion',
			id:'toolbarContainer',
			html:'<div id="toolbar"></div>'
		},{
			xtype:'panel',
			title:'流程图',
			region: 'center',
			id:'graphContainer',
			html:'<div id="graph" class="graph"></div>'
		},{
			xtype:'panel',
			title:'配置',
			region: 'east',
			width: 250,
			split: true,
			collapsible: true,
			autoScroll: true,
			layout:'fit',
			id:'propertyContainer'
		}/*,{
			xtype:'panel',
			title:'控制台',
			region: 'south',
			height: 100,
			split: true,
			collapsible: true,
			collapsed :true,
			autoScroll: true,
			id:'consoleContainer'
		}*/]
	});
	mxApplication();
});

//快捷键保存
var docMap = new Ext.KeyMap(Ext.getDoc(), {
	key: 's',
	shift:false,
	ctrl:true,
	handler: function(key,e){
		e.preventDefault();
		var json = window.editor.graphCodec.encode();
		saveGraph(json);
	}
});	
window.focus();
window.onbeforeunload = function(event) { 
	(event || window.event).returnValue = "确定退出吗"; 
} 	
</script>

</body>
</html>
