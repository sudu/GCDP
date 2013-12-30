<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
<link rel="shortcut icon" href="res/img/favicon.ico" />
<title>CMPP_节点管理</title>
	<style type="text/css">
	/*清除文档类型声明中的默认属性*/
	body,div,dl,dt,dd,ul,ol,li,h1,h2,h3,h4,h5,h6,pre,form,fieldset,input,p,blockquote,th,td{margin:0;padding:0;}
	table{border-collapse:collapse;border-spacing:0;} 
	fieldset,img{border:0;}
	address,caption,cite,code,dfn,em,strong,th,var{font-style:normal;font-weight:normal;}
	ol,ul {list-style:none;}
	caption,th {text-align:left;}
	h1,h2,h3,h4,h5,h6{font-size:100%;}
	q:before,q:after{content:'';}
	/*字体定义*/
	body {font-size:12px; font-family: simsun,arial,helvetica,clean,sans-serif; background:#fff;}
	table {font-size:inherit;font:100%;} 
	html{height:100%;}
	body {font-size:12px;height:100%;}
	#placeholder{width:100%;height:100%;}
	
	.starUl{ width:100%; padding:10px 0 0; clear:both; float:left;}
	.starUl li{ width:191px; float:left; margin:0 15px 10px 17px; display:inline; padding:0 0 15px;}
	.starUl .pic{ width:160px; height:120px; border:1px solid #ccc; padding:7px 15px 8px 14px; display:block;}
	.starUl .pic img{width:160px; height:120px; overflow:hidden;}
	.starUl p{ width:190px; display:block; line-height:20px; height:20px; overflow:hidden;}
	.us_gray,.us_gray a{ color:#3A3A3A;}
	.line{ height:1px; background:#DEDBDB; width:191px; float:left;margin-top:5px}
	
	.item-tittle {
		background: none repeat scroll 0 0 #6F6F6A;
		border-bottom: 1px solid #86867F;
		color: #FFFFFF;
		font: 16px/24px "微软雅黑";
		height: 24px;
		text-indent: 16px;
		width: 191px;
	}
	.itemStyle {
		padding:8px;
		/*border: 1px solid silver;*/
		margin-bottom:0;
	}
	.control-link {float:right}
	.control-link a{padding:0 5px 0 5px}
	
	.add{background:url("./res/js/ext2/resources/images/default/dd/drop-add.gif") left  no-repeat !important;}
	
	.panelBody {width:190px;height:190px;}
		
	.x-btn button {color: #004276;}
	</style>
	<link rel="stylesheet" type="text/css" href="res/js/ext2/resources/css/ext-all.css" />
	<link rel="stylesheet" type="text/css" href="res/js/ext2/resources/css/patch.css" />
	<link rel="stylesheet" type="text/css" href="res/css/runTime.css" />
 	<script type="text/javascript" src="res/js/ext2/adapter/ext/ext-base.js"></script>
    <script type="text/javascript" src="res/js/ext2/ext-all-debug.js"></script>	
	<script type="text/javascript" src="res/js/ext2/ext-lang-zh_CN.js"></script>
	<script type="text/javascript" src="res/js/ext_base_extension.js"></script>
	<script type="text/javascript" src="res/js/ext_vtypes.js"></script>
	<script type="text/javascript" src="res/js/controls/Ext.ux.JSONEditor.js"></script>
	<script type="text/javascript" src="res/js/hisListMgr.js"></script>		<!--历史记录管理 -->	
    <script type="text/javascript">
	Cookies={};
	/*
	 * 读取Cookies
	 */
	Cookies.get = function(name){
		var arg = name + "=";
		var alen = arg.length;
		var clen = document.cookie.length;
		var i = 0;
		var j = 0;
		while(i < clen){
			j = i + alen;
			if (document.cookie.substring(i, j) == arg)
				return Cookies.getCookieVal(j);
			i = document.cookie.indexOf(" ", i) + 1;
			if(i == 0)
				break;
		}
		return null;
	};
	
	Cookies.getCookieVal = function(offset){
	   var endstr = document.cookie.indexOf(";", offset);
	   if(endstr == -1){
		   endstr = document.cookie.length;
	   }
	   return decodeURIComponent(document.cookie.substring(offset, endstr));
	};
	</script>
</head>
<body>
<textarea id="txtMenuConfigTpl" style="display:none;">
/***********这只是一个样例****************/
{
	"accordion": [{
		"title": "菜单分类一",
		"tooltip": "",
		"children": [{
			"text": "菜单一",
			"blank": false,//点击后是否弹出显示
			"tooltip": "这里输入功能提示",
			"url": "这里输入链接地址"//这里输入链接地址
		},{
			"text": "菜单二",
			"blank": false,
			"tooltip": "这里输入功能提示",
			"url": "xlist!render.jhtml?nodeId=16&formId=149&listId=255"
		}]		
	},{
		"title": "菜单分类二",
		"tooltip": "",
		"children": [{
			"text": "菜单一",
			"blank": false,//点击后是否弹出显示
			"tooltip": "这里输入功能提示",
			"url": "这里输入链接地址"//这里输入链接地址
		},{
			"text": "菜单二",
			"blank": false,
			"tooltip": "这里输入功能提示",
			"url": "xlist!render.jhtml?nodeId=16&formId=149&listId=255"
		}]		
	}],
	"topBar": {
		"children": [{
			"text": "工具栏按钮一",
			"blank": false,
			"tooltip": "",
			"url": ""
		},{
			"text": "工具栏按钮二",
			"blank": true,
			"tooltip": "",
			"url": ""
		}]
	}
}
</textarea>


<script>
var menuConfigTpl = document.getElementById("txtMenuConfigTpl").value;

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
</script>

<!-- 节点列表、节点查看修改、节点查询、节点列表初始化 begin-->
<script type="text/javascript">

	var nodeMgr = {
		container:null,
		nodeInfoData:[],
		init:function(dataList){
			this.initUserInfo();
			this.container = Ext.getCmp('nodeListBox');
			Ext.getBody().mask("正在获取数据,请稍候......");
			Ext.Ajax.request({  
				url:'develop/node.jhtml',  //获取节点列表
				method:"GET",  
				success:function(response,opts){
					Ext.getBody().unmask();
					var res = Ext.util.JSON.decode(response.responseText);
					if(res.success == false){
						Ext.Msg.show({
							   title:'错误提示',
							   msg: decodeURIComponent(ret.message?ret.message:ret.statusText),
							   buttons: Ext.Msg.OK,
							   animEl: 'elId',
							   minWidth:420,
							   icon: Ext.MessageBox.ERROR 
						});
					}else{
						nodeMgr.nodeInfoData = res;
						nodeMgr.setHtml(nodeMgr.nodeInfoData);
					}
				},
				failure:function(ret,opts){
					Ext.getBody().unmask();
					Ext.Msg.show({
						title:'错误提示',
						msg: decodeURIComponent(ret.message?ret.message:ret.statusText),
						buttons: Ext.Msg.OK,
						animEl: 'elId',
						minWidth:420,
						icon: Ext.MessageBox.ERROR 
					});
				}	
			});
		},
		search:function(e){
			var target=nodeMgr.getEvent(e),searchtxt=target.value.replace(/\s/ig,'');
			var _data = nodeMgr.nodeInfoData;
			var result=[];
			for (i=0;i<_data.length;i++){
				if (nodeMgr._match(_data[i],searchtxt)){
					result.push(_data[i]);
				}
			}
			nodeMgr.setHtml(result);
		},
		_match:function(item,searchtxt){
			var len = searchtxt.length;
			var pinyins = item.en[0];
			for(var i=0;i<pinyins.length;i++){
				if(pinyins[i].substr(0,len).toLowerCase()==searchtxt){
					return true;
				}
			}
			var jianpins = item.en[1];
			for(var i=0;i<jianpins.length;i++){
				if(jianpins[i].substr(0,len).toLowerCase()==searchtxt){
					return true;
				}
			}
			if(item.title.substr(0,len).toLowerCase()==searchtxt){
				return true;
			}
			return false;
		},
		initUserInfo:function(){
			var uid = Cookies.get('cmpp_cn');
			if(uid)
				Ext.getCmp('lbUserInfo').setText('欢迎您,<font color="blue">' + uid + '</font>');
		},		
		setHtml:function(dataList){
			while(this.container.items.length>0){
				this.container.remove(this.container.items.get(0));
			}
			for(var i=dataList.length-1;i>=0;i--){
				var item = dataList[i];
				var bg = item.img.length>0?item.img[0]:'./res/img/logo-30.png';
				bg= ' background:url('+ bg +') no-repeat';
				var html = '<div class="panelBody" style="'+ bg +';-moz-background-size:100% 100%;background-size:100% 100%;">'+ (item.comment?item.comment:'') +'</div>';
				var subPanel = new Ext.Panel({
					title:'【节点' + item.nodeId +'】' + item.title,
					width:200,
					style:'margin:20px 0 0 20px',
					html:html,
					tbar:[{
						text:'节点配置',
						menu:{
							items :[{
								text:'修改',
								scope:item,
								handler:function(){
									nodeCreater.edit(this.nodeId,'edit');
								}
							},{
								text:'查看',
								scope:item,
								handler:function(){
									nodeCreater.view(this.nodeId);
								}
							}]
						}
					},{
						text:'运行时配置',
						scope:item,
						handler:function(obj,e){
							menuConfig.init(this.nodeId);
						}
					},{
						text:'消息',
						menu:{
							items:[{
								text:'发布消息',
								scope:item,
								handler:function(obj,e){
									notifyMgr.init(this.nodeId,obj.el);
								}
							}//,{
							//	text:'历史消息',
							//	scope:item,
								//handler:function(obj,e){
									
								//}
							//}
							]
						}	
					}],
					bbar:[{
						text:'数据接口',
						handler:function(cfg){
							var it = cfg;
							return function(){
								window.open('develop/xform!formDescriptionIndex.jhtml?nodeId='+ it.nodeId);
							}
						}(item)
					},'->',{
						text:'设计时',
						handler:function(cfg){
							var it = cfg;
							return function(){
								window.open('develop/index.jhtml?nodeId='+ it.nodeId);
							}
						}(item)
					},{
						xtype:'tbsplit',
						text:'运行时',
						menu:{
							items:[{
								text:'使用人数',
								scope:item,
								listeners :{
									scope:item,
									click:function(){
										Ext.Ajax.request({  
											url:'runtime/online!list.jhtml',
											method:'post',	
											params:{url:'runtimeIndex_nodeId' + this.nodeId},
											success:function(response,options){
												try{
													var ret = Ext.util.JSON.decode(response.responseText);
													var count = 0;	
													if(Ext.isArray(ret)){
														count = ret.length;
													}
													Ext.Msg.alert('', '当前节点运行时使用人数为:<font color="green">' + count + '</font>');

												}catch(ex){
													Ext.Msg.show({
													   title:"错误提示",
													   msg: '出现异常.',
													   buttons: Ext.Msg.OK,
													   minWidth:420,
													   icon: Ext.MessageBox.ERROR  
													});
												}
											}
										});
									}
								}
							}]
						},
						handler:function(cfg){
							var it = cfg;
							return function(){
								window.open('runtime/index.jhtml?nodeId='+ it.nodeId);
							}
						}(item)
					}]
				});
				this.container.add(subPanel);	
				this.container.doLayout();
			}
				
		},
		configMenu:function(nodeId){
			
		},
		$:function(elId){
			return document.getElementById(elId);
		},
		getEvent:function(e){
			var event=e||window.event;
			if(event){return event.srcElement||event.target;}
		}
	};
	
	
	/*
	* 节点创建、修改、查看
	*/
	var nodeCreater = {
		win:null,
		nodeId:0,
		init:function(nodeConfig,type){
			var newNodeCfg = {};
			if(nodeConfig && nodeConfig.nodeId){
				nodeCreater.nodeId = nodeConfig.nodeId;
				Ext.applyDeep(newNodeCfg,nodeConfig);

			}else{
				nodeCreater.nodeId=0;
			}
			nodeCreater.form = new Ext.form.FormPanel({
				xform:'formpanel',
				layout:"form",
				id:'frmNodeMgr',
				labelAlign:'right',
				labelWidth:120,
				border:false,
				frame:false,
				autoScroll:true,
				style:'padding:5px;',
				items:[{
					xtype:'textfield',
					name:'name',
					style:'margin:2px 10px 2px 10px',
					allowBlank:false,
					maxLength:20,
					value:newNodeCfg.name,
					fieldLabel:'节点名称'
				},{
					xtype:'fieldset',
					title:'节点服务器部署配置',
					name:'remoteHostJson',
					height:200,
					labelAlign:'right',
					labelWidth: 120,
					style:'margin:2px 10px 2px 10px',
					items:[{
						xtype:'textfield',
						name:'ip',
						vtype:'ip',
						value:newNodeCfg.remoteHostJson?newNodeCfg.remoteHostJson.ip:'',
						fieldLabel:'IP地址'
					},{
						xtype:'numberfield',
						name:'port',
						vtype:'port',
						value:newNodeCfg.remoteHostJson?newNodeCfg.remoteHostJson.port:'',
						fieldLabel:'端口'
					},{
						xtype:'textfield',
						name:'remoteDeployPath',
						value:newNodeCfg.remoteHostJson?newNodeCfg.remoteHostJson.remoteDeployPath:'',
						fieldLabel:'远程部署目录'
					},{
						xtype:'textfield',
						name:'remoteDataPath',
						value:newNodeCfg.remoteHostJson?newNodeCfg.remoteHostJson.remoteDataPath:'',
						fieldLabel:'远程存储目录'
					},{
						xtype:'textfield',
						name:'uname',
						value:newNodeCfg.remoteHostJson?newNodeCfg.remoteHostJson.uname:'',
						fieldLabel:'用户名'
					},{
						xtype:'textfield',
						name:'pwd',
						value:newNodeCfg.remoteHostJson?newNodeCfg.remoteHostJson.pwd:'',
						fieldLabel:'密码'
					}]
				},{
					xtype:'fieldset',
					title:'节点主数据库配置',
					name:'masterDBJson',
					height:220,
					labelAlign:'right',
					labelWidth: 120,
					style:'margin:2px 10px 2px 10px',
					items:[{
						xtype:'textfield',
						name:'ip',
						vtype:'ip',
						value:newNodeCfg.masterDBJson?newNodeCfg.masterDBJson.ip:'',
						fieldLabel:'IP地址'
					},{
						xtype:'numberfield',
						name:'port',
						vtype:'port',
						value:newNodeCfg.masterDBJson?newNodeCfg.masterDBJson.port:'',
						fieldLabel:'端口',
						value:3306
					},{
						xtype:'textfield',
						name:'dbname',
						value:newNodeCfg.masterDBJson?newNodeCfg.masterDBJson.dbname:'',
						fieldLabel:'数据库名'
					},{
						xtype:'textfield',
						name:'uname',
						value:newNodeCfg.masterDBJson?newNodeCfg.masterDBJson.uname:'',
						fieldLabel:'用户名'
					},{
						xtype:'textfield',
						name:'pwd',
						value:newNodeCfg.masterDBJson?newNodeCfg.masterDBJson.pwd:'',
						fieldLabel:'密码'
					},{
						xtype:'checkbox',
						name:'readable',
						checked:newNodeCfg.masterDBJson?newNodeCfg.masterDBJson.readable:true,
						fieldLabel:'是否可读'
					},{
						xtype:'checkbox',
						name:'writable',
						fieldLabel:'是否可写',
						checked:newNodeCfg.masterDBJson?newNodeCfg.masterDBJson.writable:true
					}]
				},{
					xtype:'fieldset',
					title:'节点从数据库配置',
					height:220,
					labelAlign:'right',
					labelWidth: 120,
					name:'slaveDBJson',
					style:'margin:2px 10px 2px 10px',
					items:[{
						xtype:'textfield',
						name:'ip',
						vtype:'ip',
						value:newNodeCfg.slaveDBJson?newNodeCfg.slaveDBJson.ip:'',
						fieldLabel:'IP地址'
					},{
						xtype:'numberfield',
						name:'port',
						vtype:'port',
						value:newNodeCfg.slaveDBJson?newNodeCfg.slaveDBJson.port:'',
						fieldLabel:'端口',
						value:3306
					},{
						xtype:'textfield',
						name:'dbname',
						value:newNodeCfg.slaveDBJson?newNodeCfg.slaveDBJson.dbname:'',
						fieldLabel:'数据库名'
					},{
						xtype:'textfield',
						name:'uname',
						value:newNodeCfg.slaveDBJson?newNodeCfg.slaveDBJson.uname:'',
						fieldLabel:'用户名'
					},{
						xtype:'textfield',
						name:'pwd',
						value:newNodeCfg.slaveDBJson?newNodeCfg.slaveDBJson.pwd:'',
						fieldLabel:'密码'
					},{
						xtype:'checkbox',
						name:'readable',
						checked:newNodeCfg.slaveDBJson?newNodeCfg.slaveDBJson.readable:true,
						fieldLabel:'是否可读'
					},{
						xtype:'checkbox',
						name:'writable',
						fieldLabel:'是否可写',
						checked:newNodeCfg.slaveDBJson?newNodeCfg.slaveDBJson.writable:false
					}]
				},{
					xtype:'jsoneditor',
					fieldLabel:'公共检索服务器',
					name:'publicSearchHostsJson',
					width:465,
					columns:[{
						header: "ip",
						field:'ip',
						width:150,
						editor:{name: 'Ext.form.TextField',ui:{allowBlank:false,vtype:'ip'}}
					},{
						header: "port",
						field:'port',
						width:150,
						editor:{name: 'Ext.form.TextField',ui:{allowBlank:true,vtype:'port'}}
					}],
					value:newNodeCfg.publicSearchHostsJson
				},{
					xtype:'jsoneditor',
					fieldLabel:'私有检索服务器列表',
					name:'privateSearchHostsJson',
					width:465,
					columns:[{
						header: "ip",
						field:'ip',
						width:150,
						editor:{name: 'Ext.form.TextField',ui:{allowBlank:false,vtype:'ip'}}
					},{
						header: "port",
						field:'port',
						width:150,
						editor:{name: 'Ext.form.TextField',ui:{allowBlank:true,vtype:'port'}}
					}],
					value:newNodeCfg.privateSearchHostsJson
				},{
					xtype:'fieldset',
					title:'Nosql服务器',
					height:90,
					labelAlign:'right',
					labelWidth: 120,
					name:'nosqlHostJson',
					style:'margin:2px 10px 2px 10px',
					items:[{
						xtype:'textfield',
						name:'ip',
						vtype:'ip',
						value:newNodeCfg.nosqlHostJson?newNodeCfg.nosqlHostJson.ip:'',
						fieldLabel:'IP地址'
					},{
						xtype:'numberfield',
						name:'port',
						vtype:'port',
						value:newNodeCfg.nosqlHostJson?newNodeCfg.nosqlHostJson.port:11211,
						fieldLabel:'端口'
					}]
				},{
					xtype:'jsoneditor',
					fieldLabel:'环境变量',
					name:'envMapJson',
					width:465,
					columns:[{
						header: "key",
						field:'key',
						width:100,
						editor:{name: 'Ext.form.TextField',ui:{allowBlank:false,vtype:'alphanum'}}
					},{
						header: "value",
						field:'value',
						width:150,
						editor:{name: 'Ext.form.TextField',ui:{allowBlank:true}}
					},{
						header: "说明",
						width:150,
						field:'desc',
						editor:{name: 'Ext.form.TextField',ui:{allowBlank:true}}
					}],
					value:newNodeCfg.envMapJson
				}],
				buttons:[]
			});
			//非查看时
			if(type!='view'){
				var btnSubmitCfg = {
					xtype:'button',
					text:'提交',
					id:'btnSubmit',
					width:100,
					handler:function(){
						//验证						
						var frm = nodeCreater.form;
						if(!frm.form.isValid()){
							Ext.Toast.show("输入不合法!",{time:1500,icon: Ext.MessageBox.ERROR });
							return;
						}
					
						Ext.getBody().mask("正在提交,请稍候......");
						
						var url = nodeCreater.nodeId>0?"develop/node!update.jhtml":"develop/node!add.jhtml";
						var params =  {"node.id":nodeCreater.nodeId};

						//搜集参数
						for(var i=0;i<frm.items.items.length;i++){
							var item = frm.items.items[i];
							var key = 'node.' + item.name;
							var value = '';
							if(item.xtype=='fieldset'){
								for(var j=0;j<item.items.items.length;j++){
									var it = item.items.items[j];
									if(value!='') {
										value+=',';
									}else{
										value='{';
									}
									 value += '"' + it.name + '":' + '"' + it.getValue() + '"'; 
								}
								value+='}';
							}else{
								value = item.getValue();
							}
							params[key] =  value ;
						}
						Ext.Ajax.request({  
							url:url,  
							method:"POST",  
							params:params,
							success:function(response,opts){
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
									Ext.Toast.show(nodeCreater.nodeId>0?ret.message:'提交成功',{
									   title:'提示',
									   buttons: Ext.Msg.OK,
									   animEl: 'elId',
									   minWidth:420,
									   icon: Ext.MessageBox.INFO
								    });
									nodeCreater.win.close();
									nodeMgr.init();
								}
								Ext.getBody().unmask();
							},
							failure:function(ret,opts){
								Ext.Msg.show({
									   title:'错误提示',
									   msg: decodeURIComponent(ret.message?ret.message:ret.statusText),
									   buttons: Ext.Msg.OK,
									   animEl: 'elId',
									   minWidth:420,
									   icon: Ext.MessageBox.ERROR 
								});
								Ext.getBody().unmask();
							}		
						});	
					}
				};
				var btnCancelCfg = {
					xtype:'button',
					text:'取消',
					width:100,
					handler:function(){
						nodeCreater.win.close('btnAddNode');
					}	
				};
				nodeCreater.form.addButton(btnSubmitCfg);
				nodeCreater.form.addButton(btnCancelCfg);
			}
			//创建窗口
			nodeCreater.win= new Ext.Window({
				title:'节点配置',
				layout:"fit",
				modal:true,
				id:"openFormWin",
				closeAction:'close',
				maximizable:true,
				width:640,
				height:500,
				buttonAlign: "center",
				animateTarget:'btnAddNode',
				html:'<a id="ajaxInfo-tip">提交提示信息</a>',
				items:[nodeCreater.form]
			});

			if(type=='view'){
				nodeCreater.win.setTitle('查看节点【'+ nodeConfig.name +'】的配置信息');
			}else if(type=='edit'){
				nodeCreater.win.setTitle('修改节点【'+ nodeConfig.name +'】的配置信息');
			}else{
				nodeCreater.win.setTitle('创建新节点');
			}
			nodeCreater.win.show('btnAddNode');
		},
		edit:function(nodeId,type){
			Ext.getBody().mask("正在获取数据,请稍候......");
			Ext.Ajax.request({  
				url:'develop/node!view.jhtml',  
				method:"GET",  
				params:{"node.id":nodeId},
				success:function(response,opts){
					Ext.getBody().unmask();
					var res = Ext.util.JSON.decode(response.responseText);
					if(res.success == false){
						Ext.Msg.show({
							   title:'错误提示',
							   msg: decodeURIComponent(ret.message?ret.message:ret.statusText),
							   buttons: Ext.Msg.OK,
							   animEl: 'elId',
							   minWidth:420,
							   icon: Ext.MessageBox.ERROR 
						});
					}else{
						nodeCreater.init(res,type);
					}
					
				},
				failure:function(ret,opts){
					Ext.getBody().unmask();
					Ext.Msg.show({
						title:'错误提示',
						msg: decodeURIComponent(ret.message?ret.message:ret.statusText),
						buttons: Ext.Msg.OK,
						animEl: 'elId',
						minWidth:420,
						icon: Ext.MessageBox.ERROR 
					});
				}	
			});
		},
		view:function(nodeId){
			nodeCreater.edit(nodeId,'view');
		}
	}
	//创建节点
	function createNode(){
		//nodeCreater.win.show('btnAddNode');
		nodeCreater.init();
	}
	
	/*
	* 节点菜单配置
	*/
	var menuConfig ={
		win:{},
		setUrl:'runtime/rtMgr!writeRuntimeConfig.jhtml',
		getUrl:'runtime/rtMgr!readRuntimeConfig.jhtml',
		previewUrl:'runtime/index!preview.jhtml',
		init:function(nodeId){
			
			var win = new Ext.Window({
				title:'运行时配置',
				height:500,
				width:600, 
				modal: true,
				buttonAlign: "center",
				closable:true ,
				closeAction:'close',
				maskDisabled:false,
				maximizable:true,
				layout:'fit',
				autoScroll:true,
				items:[{
					xtype:'form',
					layout:"xform",
					labelAlign:"right",
					autoScroll:true,
					bodyStyle:"padding:5px;",					
					items:[{
						fieldLabel:"欢迎页url",
						xtype:'textfield',
						anchor:"90%",
						name:'welcomeUrl'
					},{
						fieldLabel:"菜单配置",
						xtype:'textarea',
						name:'menuConfig',
						anchor:"90%",
						height:300
					},{
						fieldLabel:"head注入",
						xtype:'textarea',
						name:'headInject',
						anchor:"90%",
						height:100
					},{
						fieldLabel:"body注入",
						xtype:'textarea',
						name:'bodyInject',
						anchor:"90%",
						height:100
					}]
				}],
				buttons:[{
					text:'保存',
					scope:{nodeId:nodeId},
					handler:function(){
						var win = menuConfig.win[this.nodeId];
						win.getEl().mask('正在提交...');
						var form = win.items.first().form;
						var params = form.getValues();
						params.nodeId = this.nodeId;
						Ext.Ajax.request({  
							url:menuConfig.setUrl,
							method:'post',	
							params:params,
							options:{nodeId:this.nodeId},
							success:function(response,action){
								var win = menuConfig.win[action.options.nodeId];
								win.getEl().unmask();
								try{
									var ret = Ext.util.JSON.decode(response.responseText);
									if(ret.success){
										win.close();
									}else{
										Ext.Toast.show('保存出错',{
										   title:'提示',
										   buttons: Ext.Msg.OK,
										   animEl: 'elId',
										   minWidth:420,
										   icon: Ext.MessageBox.ERROR
										});
									}
									
								}catch(ex){
								
								}
							},
							failure:function(ret,action){
								menuConfig.win[action.options.nodeId].getEl().unmask();
								Ext.Msg.show({
									title:'错误提示',
									msg: decodeURIComponent(ret.message?ret.message:ret.statusText),
									buttons: Ext.Msg.OK,
									animEl: 'elId',
									minWidth:420,
									icon: Ext.MessageBox.ERROR 
								});
							}
						});
						
					}
				},{
					text:'预览',
					scope:{nodeId:nodeId},
					handler:function(){
						var win = menuConfig.win[this.nodeId];
						win.getEl().mask('正在提交预览...');
						
						var form = win.items.first().form;
						var params = form.getValues();
						
						var tempform = document.createElement('form');
						tempform.setAttribute('method', 'post');
						tempform.setAttribute('target', '_blank');
						tempform.setAttribute('name', 'tempform');
						tempform.setAttribute('action', menuConfig.previewUrl);
						
						newinput = document.createElement('input');
						newinput.setAttribute('type','hidden');
						newinput.setAttribute('name','menuConfig');
						newinput.setAttribute('value',params.menuConfig);
						tempform.appendChild(newinput);
						
						newinput = document.createElement('input');
						newinput.setAttribute('type','hidden');
						newinput.setAttribute('name','headInject');
						newinput.setAttribute('value',params.headInject);
						tempform.appendChild(newinput);
						
						newinput = document.createElement('input');
						newinput.setAttribute('type','hidden');
						newinput.setAttribute('name','bodyInject');
						newinput.setAttribute('value',params.bodyInject);
						tempform.appendChild(newinput);
						
						newinput = document.createElement('input');
						newinput.setAttribute('type','hidden');
						newinput.setAttribute('name','welcomeUrl');
						newinput.setAttribute('value',params.welcomeUrl);
						tempform.appendChild(newinput);
						
						newinput = document.createElement('input');
						newinput.setAttribute('type','hidden');
						newinput.setAttribute('name','nodeId');
						newinput.setAttribute('value',this.nodeId);
						
						tempform.appendChild(newinput);
						document.body.appendChild(tempform);
						tempform.submit();
						document.body.removeChild(tempform);
						
						win.getEl().unmask();
					}
				},{
					text:'关闭',
					scope:{nodeId:nodeId},
					handler:function(){
						menuConfig.win[this.nodeId].close();
						delete menuConfig.win[this.nodeId];
					}
				}]
			});
			menuConfig.win[nodeId] = win;
			win.show('btnAddNode',function(){
				var win = menuConfig.win[this.nodeId];
				win.getEl().mask('正在获取配置信息...');
				Ext.Ajax.request({  
					url:menuConfig.getUrl + '?nodeId=' + nodeId,
					method:'get',	
					options:{nodeId:this.nodeId},
					success:function(response,action){
						var win = menuConfig.win[action.options.nodeId];
						win.getEl().unmask();
						var ret=Ext.decode(response.responseText);
						if(ret.success!=false){
							if(!ret.menuConfig){
								ret.menuConfig = menuConfigTpl;
							}
							var form = win.items.first().form;
							form.setValues(ret);
						}else{
							Ext.Msg.show({
								title:'出错',
								msg: decodeURIComponent(ret.message),
								buttons: Ext.Msg.OK,
								animEl: 'elId',
								minWidth:420,
								icon: Ext.MessageBox.ERROR 
							});
						}
					},
					failure:function(ret,action){
						menuConfig.win[action.options.nodeId].getEl().unmask();
						Ext.Msg.show({
							title:'错误提示',
							msg: decodeURIComponent(ret.message?ret.message:ret.statusText),
							buttons: Ext.Msg.OK,
							animEl: 'elId',
							minWidth:420,
							icon: Ext.MessageBox.ERROR 
						});
					}
				});
			
			},{nodeId:nodeId});
		}
	}

	/*
	* 消息管理
	*/
	var notifyMgr ={
		win:{},
		setUrl:'runtime/message!send.jhtml',
		getUrl:'runtime/message!list.jhtml',
		init:function(nodeId,el){
			var win = new Ext.Window({
				title:'消息框',
				height:300,
				width:400, 
				buttonAlign: "center",
				closable:true ,
				closeAction:'close',
				maskDisabled:false,
				modal:true,
				border:false,
				layout:'fit',
				items:[{
					xtype:'form',
					layout:'form',
					border:false,
					itemCls:'itemStyle',
					labelAlign:'right',
					labelWidth:70,
					id:'msgForm' + nodeId ,
					items:[{
						xtype:'textfield',
						fieldLabel:'标题',
						width:270,
						name:'m.title'
					},{
						xtype:'textarea',
						fieldLabel:'内容',
						width:270,
						height:120,
						name:'m.content'
					},//{
						//xtype:'textfield',
						//fieldLabel:'发送者',
						//width:270,
						//name:'m.creator',
						//value:Cookies.get('cmpp_cn')
					//},
					{
						xtype:'hidden',
						name:'nodeId',
						value:nodeId,
					}],
					buttons:[{
						text:'发送',
						scope:{nodeId:nodeId},
						handler:function(){
							notifyMgr.win[this.nodeId].getEl().mask('正在发送...');
							var form = Ext.getCmp('msgForm' + this.nodeId);
							Ext.Ajax.request({  
								url:notifyMgr.setUrl,
								method:'post',	
								params:form.form.getValues(),
								options:{nodeId:this.nodeId},
								success:function(response,action){
									notifyMgr.win[action.options.nodeId].getEl().unmask();
									try{
										var ret = Ext.util.JSON.decode(response.responseText);
									}catch(ex){
									
									}	
									if(ret.success){
										notifyMgr.win[action.options.nodeId].close();
										delete notifyMgr.win[this.nodeId];
										Ext.Toast.show('消息已发送',{
										   title:'提示',
										   buttons: Ext.Msg.OK,
										   animEl: 'elId',
										   minWidth:420,
										   icon: Ext.MessageBox.INFO
										});
									}else{
										Ext.Msg.show({
											title:'提示',
											msg: ret.message,
											buttons: Ext.Msg.OK,
											animEl: 'elId',
											minWidth:420,
											icon: Ext.MessageBox.ERROR 
										});
									}
								},
								failure:function(ret,action){
									notifyMgr.win[action.options.nodeId].getEl().unmask();
									Ext.Msg.show({
										title:'错误提示',
										msg: decodeURIComponent(ret.message?ret.message:ret.statusText),
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
						scope:{nodeId:nodeId},
						handler:function(){
							notifyMgr.win[this.nodeId].close();
							delete notifyMgr.win[this.nodeId];
						}
					}]
				}]
			});
			notifyMgr.win[nodeId] = win;
			notifyMgr.win[nodeId].show(el);
		}
	}


	
</script>
<!-- 节点列表、节点查看修改、节点查询、节点列表初始化 end-->

<!-- 页面初始化 begin-->
<script type="text/javascript">
Ext.onReady(function(){
Ext.BLANK_IMAGE_URL = "./res/js/ext2/resources/images/default/s.gif";
Ext.lOADING_IMAGE_URL= "./res/js/ext2/resources/images/default/grid/wait.gif"; //resources\images\default\grid
Ext.QuickTips.init();
Ext.form.Field.prototype.msgTarget = 'qtip';

mainPanel = new Ext.Viewport({
	layout:"fit",
	items:[{
		xtype:'panel',
		region:"center",
		layout:"column",
		autoScroll:true,
		border:true,
		id:'nodeListBox',
		tbar:[{
			xtype:'label',
			text:'搜索节点：'
		},{
			xtype:'textfield',
			id:'txtSearch',
			width:200,
			emptyText:'输入节点名称全拼或全拼首字母',
			enableKeyEvents:true,
			style:'background:url(res/img/search.gif) 180px 1px no-repeat;margin-left:5px;padding-right:20px;border-radius:10px;'
			
		},{
			xtype:'button',
			iconCls:'add',
			text:'新建节点',
			id:'btnAddNode',
			handler:function(obj,e){
				createNode();
			}
		},{
			xtype:'tbfill'
		},{
			xtype:'label',
			id:'lbUserInfo',
			text:'您尚未登录系统'
		},{
			xtype:'tbseparator'
		}],
		items:[{
			xtype:'panel'
		}]
	}]
});
var txtSearch = Ext.getCmp('txtSearch');
addEventHandler(txtSearch.getEl().dom,'keyup',bindFunc(txtSearch.getEl().dom,nodeMgr.search));
nodeMgr.init();//初始化节点列表

});
</script>
<!-- 页面初始化 end-->

</body>
</html>
