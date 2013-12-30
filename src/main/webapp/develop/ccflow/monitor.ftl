<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>流程图运行监控</title>
	<link rel="stylesheet" type="text/css" href="../res/js/ext2/resources/css/ext-all.css" />
	<link rel="stylesheet" type="text/css" href="../res/js/ext2/resources/css/patch.css" />
 	<script type="text/javascript" src="../res/js/ext2/adapter/ext/ext-base.js"></script>
	<script type="text/javascript" src="../res/js/ext2/ext-all-debug.js"></script>	
	<script type="text/javascript" src="../res/js/ext2/ext-lang-zh_CN.js"></script>
	<script type="text/javascript" src="../res/js/ext_base_extension.js"></script>
    <script type="text/javascript" src="ccflow/js/lib/raphael.js"></script>
	<script type="text/javascript" src="ccflow/js/ccWindow.js"></script>
	<script type="text/javascript" src="ccflow/js/ccGraphCodec.js"></script>
	<script type="text/javascript" src="ccflow/js/ccWFlow.js"></script>
	
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
		#toolbar img{margin-bottom:10px;width:32px;height:32px;cursor:move;}
    </style>
	
	<script type = "text/javascript">
		var globalvars = {
			nodeId:#{nodeId!0},
			processId:#{processId!0},
			id:#{instanceId!0},
			dateTime:"${dateTimeStr!""}"
		}
	</script>
	
<script>	
/*
* 查看流程图时的plugin,覆写一些方法
*/

ccCellWindow.prototype.inputScript = function(type){
	if(globalvars.id==0){
		alert("保存后才能录入脚本");	
		return;
	}
	var url = "scriptdebug.jhtml?nodeId=" + globalvars.nodeId + "&id1="+ globalvars.processId +"&id2=" + this.cell.objectIdentity + "&stype="+type;
	if(top && top.openTab){
		top.openTab(url,"脚本窗口");
	}else{
		window.open(url);
	}
};

ccCell.prototype.bindEvents = function() {
	var el = this.shape.element,connEl = this.connectElement;
	var clickHandler = function(cell){
		var _t = cell;
		return function(e,x,y){
			clearTimeout(_t.graph.clickTimeoutId);
			var cell = _t;
			var cw = cell.graph.editor.cellWindow;
			cw.setCell.call(cw,cell);
			_t.graph.graphSelection.select(cell);
			
			e.cancelBubble = true;
		}
	};
	if(!this.isBeginCell && !this.isEndCell){
		el.click(clickHandler(this));
		if(connEl) connEl.click(clickHandler(this));
	}
	
};

ccCellWindow.prototype.plugin = function(){
	var color1 = "blue",color2 = "red",color3 = "green",color4 = "yellow";
	//创建各种状态的渐变样式
	this.editor.graph.createSvgGradient("pluginState1",color1,"white");
	this.editor.graph.createSvgGradient("pluginState2",color2,"white");
	this.editor.graph.createSvgGradient("pluginState3",color3,"white");
	this.editor.graph.createSvgGradient("pluginState4",color4,"white");
	//创建图例
	var p = this.editor.graph.paper;
	var w = 130,h=130;
	var x=p.width-w-10,y=p.height-h-10;
	p.rect(x,y,w,h);
	var rectW = 60,rectH = 20;
	var el = p.rect(x+10,y+10,rectW,rectH);el.node.setAttribute("fill","url(#pluginState1)");
	el = p.rect(x+10,y+10 + (rectH+10),rectW,rectH);el.node.setAttribute("fill","url(#pluginState2)");
	el=p.rect(x+10,y+10 + (rectH+10)*2,rectW,rectH);el.node.setAttribute("fill","url(#pluginState3)");
	el=p.rect(x+10,y+10 + (rectH+10)*3,rectW,rectH);el.node.setAttribute("fill","url(#pluginState4)");
	p.setStart();
	p.text(x+10+rectW+30,y+20,"正在执行");
	p.text(x+10+rectW+30,y+50,"出现异常");
	p.text(x+10+rectW+30,y+80,"正常结束");
	p.text(x+10+rectW+30,y+110,"挂起");	
	var st = p.setFinish();
	st.attr({"font-size": "12px"});

	var statusPanel = new Ext.Panel({
		title:"运行状态监控",
		labelWidth:80,
		labelAlign:'left',
		layout:'xform2',
		autoScroll:false,
		autoHeight:true,
		frame:true,
		itemCls:"itemStyle5",
		items:[{
			fieldLabel :'开始执行时间',
			xtype:'textfield',
			name:'pluginStartDate',
			readOnly:!this.isEditState,
			anchor:'95%'
		},{
			fieldLabel :'结束执行时间',
			xtype:'textfield',
			name:'pluginEndDate',
			readOnly:!this.isEditState,
			anchor:'95%'
		},{
			fieldLabel :'状态',
			xtype:'textfield',
			name:'state',
			readOnly:!this.isEditState,
			anchor:'95%'
		}]
	});
	this.form.add(statusPanel);
	this.form.doLayout();
};
ccCellWindow.prototype.setCell = function(v){
	this.show();
	this.cell = v;
	
	for(var name in v.property){
		var ctrs = this.form.find("name",name);
		var value = v.property[name];
		if(ctrs.length>0){
			if(name==="state"){
				var color = value=="正常结束"?"green":(value=="出现异常"?"red":(value=="正在执行"?"#99BBE8":""));
				ctrs[0].el.parent().setStyle("background-color",color);
			}
			ctrs[0].setValue(value);
		}
	}
	//this.form.getForm().setValues();

	var chkMail = this.form.find("name","mailTo")[0].extra;
	chkMail.setValue(v.property.mailToEnable!==0?true:false);
	var chkSms = this.form.find("name","smsTo")[0].extra;
	chkSms.setValue(v.property.smsToEnable!==0?true:false);
	
	this.hideSomething();
};


var monitor = {
	editor:null,
	instanceId:0,
	init:function(editor,instanceId){
		this.editor = editor;
		this.instanceId = instanceId;
		
		this.requestStatusData();
	},
	requestStatusData:function(){
		if(this.instanceId==0){
			this.showError("未传入调用者ID(intanceId)");	
			return;
		}
		Ext.getBody().mask("正在请求数据...");
		Ext.Ajax.request({  
			url:"workflowMonitor!queryPluginStatus.jhtml?instanceId=" + this.instanceId,  
			scope:this,
			success:function(response,opts){
				Ext.getBody().unmask();
				try{
					var ret = Ext.util.JSON.decode(response.responseText);
				}catch(ex){
					ret = {success:false,message:"接口错误"};
				}			
				if(!ret.success){
					this.showError(ret.message);
				}else{
					for(var key in ret.data){
						var cell = this.editor.graph.cellCollection[key];
						var status = ret.data[key];
						if(cell){
							for(var k in status){
								if(k==="state"){
									cell.setGradient("pluginState" + status[k]);
									status[k] = status[k]==4?"挂起":status[k]==3?"正常结束":(status[k]==2?"出现异常":(status[k]==1?"正在执行":"未知状态"));
								}
								cell.property[k] = status[k];
							}
						}
					}
					
				}
			},
			failure:function(response,opts){
				Ext.getBody().unmask();
				this.showError("网络异常");
			}		
		});	
		
	},
	showInfo:function(msg){
		Ext.Toast.show(msg,{
			title:'提示',
			buttons: Ext.Msg.OK,
			minWidth:420,
			icon: Ext.MessageBox.INFO 
		});
	},
	showError:function(msg){
		Ext.Msg.show({
			   title:'错误提示',
			   msg: msg,
			   buttons: Ext.Msg.OK,
			   minWidth:420,
			   icon: Ext.MessageBox.ERROR 
		});	
	}
};
</script>	
	
	<script>
		
    function mxApplication(){
		var config = {
			locked:true,
			"graph":{
				"container":Ext.getCmp("graphContainer"),
				<#if flowDefInfoJSON!="">"workflow":${flowDefInfoJSON!""},</#if>
				"property":{
					"title":"该流程图根本就不存在这个调用实例",
					"author":(globalvars.userName||"未知"),
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
				"rectangle":{align:"center",fillColor:"#C3D9FF",fontSize:11,gradientColor:"white",perimeter:"rectanglePerimeter",shadow:1,shape:"rectangle",strokeColor:"#C3D9FF",verticalAlign:"middle"},
				"rhombus":{align:"center",fillColor:"#FFCF8A",fontSize:11,gradientColor:"white",perimeter:"rhombusPerimeter",shadow:1,shape:"rhombus",strokeColor:"#FFCF8A",verticalAlign:"middle"}
			}
		}
		window.editor = new ccEditor(config);

    }
		
	</script>
	
</head>
<body>
<script>

Ext.onReady(function(){
	Ext.BLANK_IMAGE_URL = "../res/js/ext2/resources/images/default/s.gif";
	Ext.lOADING_IMAGE_URL= "../res/js/ext2/resources/images/default/grid/wait.gif"; //resources\images\default\grid
	Ext.QuickTips.init();
	Ext.form.Field.prototype.msgTarget = 'qtip';
	
	
	new Ext.Viewport({
		layout:'border',
		items:[{
			xtype:'panel',
			title:'图形库',
			region: 'west',
			width: 70,
			split: true,
			collapsible: true,
			autoScroll: true,
			layout:'accordion',
			style:'display:none;',
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
		}]
	});
	mxApplication();
	monitor.init(window.editor,globalvars.id);
});
	
</script>

</body>
</html>
