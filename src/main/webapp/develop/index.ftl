 <!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <title>CMPP设计时</title>
	<link rel="shortcut icon" href="../res/img/favicon.ico" />
	<link rel="stylesheet" type="text/css" href="../res/js/ext2/resources/css/ext-all.css" />
	<link rel="stylesheet" type="text/css" href="../res/js/ext2/resources/css/patch.css" />
	<style>
		html{height:100%;}
		body {font-size:12px;height:100%;}
		.blank{clear:both;height:18px}
		.blank2{clear:both;height:10px;line-height:0;font-size:0}
		#header div{background:#46A3FF;}
		.userinfo {color:#D2E9FF;float:right;font-size:12px;font-weight:bold;width:300px;padding-top: 5px;}
		.userinfo a {color:#D2E9FF;}
		.topBanner {width:100%;height:0px; border-bottom:2px solid #46A3FF;border-radius: 2px;}
		#footer div{background:#46A3FF;}
		.copyright{float:right;color:#D2E9FF;padding-top: 3px;font-size:12px;}
		
		/*用户列表*/
		.online_list_user ul li {
			margin: 2px 0 0;
			height: 28px;
			overflow: hidden;
			padding-left: 5px;
			clear: both;
		}
		
		/*菜单图标样式*/
		.x-tree-node-leaf .menu-add{background-image:url(../res/js/ext2/resources/images/default/dd/drop-add.gif);}
		.x-tree-node-leaf .add-form {background-image:url(../res/js/ext2/resources/images/default/my/add-form.gif);}
		.x-tree-node-leaf .modify-form {background-image:url(../res/js/ext2/resources/images/default/my/modify-form.gif);}
		.x-tree-node-leaf .modify-script {background-image:url(../res/js/ext2/resources/images/default/my/modify-script.gif);}
		.x-tree-node-leaf .modify-list {background-image:url(../res/js/ext2/resources/images/default/my/list.gif);}
		.x-tree-node-leaf .modify-view {background-image:url(../res/js/ext2/resources/images/default/my/modify-view.gif);}
		.x-tree-node-leaf .task-node {background-image:url(../res/img/task.gif);}	
		.x-tree-node-leaf .source-node {background-image:url(../res/img/source.gif);}	
		.x-tree-node-leaf .allLeaf {background-image:url(../res/img/allLeaf.gif);}	
		.x-tree-node-leaf .extForm {background-image:url(../res/img/form.gif);}	
		.x-tree-node-leaf .user-node {background-image:url(../res/img/user.png);}	
		.x-tree-node-leaf .interface-node {background-image:url(../res/img/connect.png);}	
		.x-tree-node-leaf .resource-node {background-image:url(../res/img/resource.png);}
		.x-tree-node-leaf .script-node {background-image:url(../res/img/script.png);}	
		.x-tree-node-leaf .service-chart {background-image:url(../res/img/diagram_16.png);}	
		.x-tree-node-leaf .dynamic-web {background-image:url(../res/img/globe_16.png);}	
		.x-tree-node-leaf .workflow-icon {background-image:url(../res/img/workflow_16.png);}	
		
		.x-tree-node-collapsed .x-tree-node-icon-table {background-image:url(../res/js/ext2/resources/images/default/my/form.gif);}
		.x-tree-node-expanded  .x-tree-node-icon-table {background-image:url(../res/js/ext2/resources/images/default/my/form.gif);}
		.x-tree-node-collapsed .x-tree-node-icon-view {background-image:url(../res/js/ext2/resources/images/default/my/view.gif);}
		.x-tree-node-expanded  .x-tree-node-icon-view {background-image:url(../res/js/ext2/resources/images/default/my/view.gif);}
		.x-tree-node-collapsed .x-tree-node-icon-list {background-image:url(../res/js/ext2/resources/images/default/my/list.gif);}
		.x-tree-node-expanded  .x-tree-node-icon-list {background-image:url(../res/js/ext2/resources/images/default/my/list.gif);}	
		
		
		
		/*按钮图标*/
		.extAdd{background:url("../res/js/ext2/resources/images/default/dd/drop-add.gif") left  no-repeat !important;}
		.extDelete{background:url("../res/img/runTime/delete1.gif") left  no-repeat !important;}
		.extHelp{background:url("../res/img/help3.gif") left  no-repeat !important;}
		.extRefresh{background:url("../res/img/arrow_refresh.png") left  no-repeat !important;}
		
		/**折叠抽屉图标**/
		.x-panel-header.extForm {background-image:url(../res/img/form.gif);}	
		.x-panel-header.task-node {background-image:url(../res/img/task.gif);}	
		.x-panel-header.source-node {background-image:url(../res/img/source.gif);}	
		.x-panel-header.user-node {background-image:url(../res/img/user.png);}
		.x-panel-header.interface-node {background-image:url(../res/img/connect.png);}
		.x-panel-header.resource-node {background-image:url(../res/img/resource.png);}
		.x-panel-header.script-node {background-image:url(../res/img/script.png);}			
		.x-panel-header.service-chart {background-image:url(../res/img/diagram_16.png);}
		.x-panel-header.dynamic-web {background-image:url(../res/img/globe_16.png);}	
		.x-panel-header.workflow-icon {background-image:url(../res/img/workflow_16.png);}	
		
		
		.x-tool-refresh{position:absolute;right:2px;top:5px;z-index:9;}
		.x-tab-strip-active span.x-tab-strip-text {cursor:default;}

		#menusPanel .x-panel {
			margin-bottom:3px;
			margin-right:0;
		}
		#menusPanel .x-panel-body {
			border:0 none;
		}
		#menusPanel .x-panel-body li {
			margin:3px;	
		}
		#menusPanel .x-panel-body li img {
			width:16px;
			height:16px;
			vertical-align:middle;
			margin-right:2px;
			margin-bottom:2px;
		}
		#menusPanel .x-panel-body li a {
			text-decoration:none;
			color:#3764A0;
		}
		#menusPanel .x-plain-body {
			background-color:#cad9ec;
			padding:3px 0 0 5px;
		}
		.x-air #menusPanel .x-plain-body {
			padding-left:3px;
		}
		#menusPanel .x-panel-body li a:hover {
			text-decoration:underline;
			color:#15428b;
		}

		.x-panel-trans {
			background:transparent;
		}

		.x-layout-split-west {
			background-color:#cad9ec;
		}
		.x-panel-header-text {
			color:#3764A0;
		}
		.x-tab-strip-text{text-align: center;}
		
		
		#interfaceTree li{background:url("../res/js/ext2/resources/images/default/grid/page-next.gif") no-repeat;padding-left:20px;margin:5px 0 5px 10px;cursor:pointer;}
		#interfaceTree #addInterfaceBtn{background:url("../res/js/ext2/resources/images/default/dd/drop-add.gif") no-repeat;}
		.x-tool-maximize{position:absolute;right:2px;top:5px;z-index:9;}
		.x-tool-refresh{position:absolute;right:2px;top:5px;z-index:9;}
		.x-tab-strip-active span.x-tab-strip-text {cursor:default;}
		.x-tab-strip-text{text-align: center;}
		
		.waiting7{background:url(../res/img/loading7.gif) no-repeat center 50px;}
	</style>
	<script type="text/javascript" src="../res/js/md5.js"></script>
 	<script type="text/javascript" src="../res/js/ext2/adapter/ext/ext-base.js"></script>
    <script type="text/javascript" src="../res/js/ext2/ext-all-debug.js"></script>	
	<script type="text/javascript" src="../res/js/ext2/ext-lang-zh_CN.js"></script>
	<script type="text/javascript" src="../res/js/ext_base_extension.js"></script>
	<script type="text/javascript" src="../res/js/userOnlineMgr.js"></script>		<!--在线用户管理 -->	
	<script type="text/javascript" src="../res/js/CookiesHelper.js"></script>
	<script>
	
Ext.ux.RefreshTabTool = function(){
	this.init= function(ct) {
		var maximizeTool = {
			id: 'refresh', 
			handler: handleRefreshTab, 
			scope: ct, 
			qtip: '刷新'
		}; 
		ct.tools = ct.tools || [];
		var newTools = ct.tools.slice();
		ct.tools =newTools;
		for(var i=0, len=ct.tools.length;i<len;i++) {
			if (ct.tools[i].id=='refreshtab') return;
		}
		ct.tools[ct.tools.length] = maximizeTool;
	};
	function handleRefreshTab(event, toolEl, panel){
		var id = 'centerTabPanel__' + panel.getActiveTab().id;
		if(allTabs[id]){
			var tabPage = allTabs[id].tab;
			panel.addIframe(tabPage.id,tabPage.title,tabPage.url);
			
		}
	}
	
};

window.openTab = function(url,title){
	centerTabPanel.addIframe(null,title,url);
}
//在指定ID页签里加载新的url
window.reloadTab = function(id,newUrl,newTitle){
	centerTabPanel.reloadIframe(id,newUrl,newTitle);
}
	</script>
		
</head>
<body>	

<script type="text/javascript">
var nodeId__=Ext.parseQuery().nodeId;
allTabs={};
devMgr = {
	topToolbar:null,
	xformPanel:null,
	interfacePanel:null,
	
	resourcePanel:null,
	
	taskPanel:null,
	subscriblePanel:null,
	workflowPanel:null,
	userPanel:null,
	lblStatus:null,
	init:function(){
		this.initUI();	
		centerTabPanel = Ext.getCmp('centerTabPanel');
		centerTabPanel.body.setStyle({overflow:'hidden'});
		this.xformPanel = Ext.getCmp('xformPanel');
		this.scriptThreadPanel = Ext.getCmp('scriptThreadPanel');
		this.interfacePanel = Ext.getCmp('interfacePanel');
		
		this.resourcePanel = Ext.getCmp('resourcePanel');
		
		this.taskPanel = Ext.getCmp('taskPanel');
		this.subscriblePanel = Ext.getCmp('subscriblePanel');
		this.userPanel = Ext.getCmp('userPanel');
		this.lblStatus	= Ext.getCmp('lblStatus');
		this.serviceStatePanel = Ext.getCmp('serviceStatePanel');//状态监控
		this.dynamicWebPanel= Ext.getCmp('dynamicWebPanel');//动态前端
		this.workflowPanel = Ext.getCmp('workflowPanel');//脚本流程
		this.sysFormPanel = Ext.getCmp('sysFormPanel');//系统表单
		this.initTabPanelEvent();
		
		this.initUserInfo();
		
		this.initXFormPanel();
		this.initInterfacePanel();
		
		this.initResourcePanel();
		
		this.initTaskPanel();
		this.initSubscriblePanel();
		this.initServiceStatePanel();
		this.initDynamicWebPanel();
		this.initWorkflowPanel();
		this.initUserPanel();
	},
	initUI:function(){
		this.viewport = new Ext.Viewport({
			frame:true,
			layout:"border",
			margins:'5',
			items:[{
				xtype:'toolbar',
				region: 'north',	
				border:false,
				style:'border:none;',
				id:'topToolbar',
				items:[{
					xtype:'tbtext',
					text:'<img src="../res/img/LogoMaker.png" title="CMPP开发者" height="18"/>'
				},{
					xtype:'tbfill'
				},{
					xtype:'label',
					id:'lbUserInfo',
					text:'您尚未登录系统'
				},{
					xtype:'tbseparator'
				},{
					xtype:'button',
					text:'退出系统',
					style:'margin-right:5px',
					handler:function(){
					//todo
					}
				}]
			},{
				xtype: 'panel',
				title: '菜单栏',
				id:'menusPanel',
				region: 'west',	
				split:true,
				collapsible: true,
				//collapseMode: 'mini',
				autoScroll:true,
				width:200,
				minWidth: 150,
				border: true,
				frame:true,
				style:'overflow-x:hidden;',
				//baseCls:'x-plain',
				layoutConfig:{
					titleCollapse: true,   
					collapsible:true,
					collapseFirst : true,  				
					animate: true,   
					hideCollapseTool : false,   
					activeOnTop: false  	
				},
				items:[{
					xtype:'panel',
					frame:true,
					title:'表单管理',
					id:'xformPanel',
					iconCls :'extForm',
					autoScroll:true,
					collapsible:true,
					titleCollapse: true
				},{
					xtype:'panel',
					frame:true,
					title:'接口管理',
					id:'interfacePanel',
					iconCls:'interface-node',
					collapsible:true,
					collapsed:true ,	
					titleCollapse: true
				},{
					xtype:'panel',
					frame:true,
					title:'资源管理',
					id:'resourcePanel',
					iconCls:'resource-node',
					collapsible:true,
					collapsed:true ,	
					titleCollapse: true
				},{
					xtype:'panel',
					frame:true,
					title:'计划任务',
					id:'taskPanel',
					iconCls:'task-node',
					collapsible:true,
					collapsed:true ,
					titleCollapse: true
				},{
					xtype:'panel',
					frame:true,
					title:'数据源&订阅',
					id:'subscriblePanel',
					iconCls:'source-node',
					collapsible:true,
					collapsed:true ,	
					titleCollapse: true
				},{
					xtype:'panel',
					frame:true,
					title:'脚本线程管理',
					id:'scriptThreadPanel',
					iconCls :'script-node',
					collapsible:true,
					collapsed:true ,
					titleCollapse: true
				},{
					xtype:'panel',
					frame:true,
					title:'服务状态监控',
					id:'serviceStatePanel',
					iconCls :'service-chart',
					collapsible:true,
					collapsed:true ,
					titleCollapse: true
				},{
					xtype:'panel',
					frame:true,
					title:'动态前端',
					id:'dynamicWebPanel',
					iconCls :'dynamic-web',
					collapsible:true,
					collapsed:true ,
					titleCollapse: true
				},{
					xtype:'panel',
					frame:true,
					title:'脚本流程',
					id:'workflowPanel',
					iconCls :'workflow-icon',
					collapsible:true,
					collapsed:true ,
					titleCollapse: true
				},{
					xtype:'panel',
					frame:true,
					title:'用户 &组',
					id:'userPanel',
					iconCls:'user-node',
					collapsible:true,
					collapsed:true ,	
					titleCollapse: true
				},{
					xtype:'panel',
					frame:true,
					title:'系统表单',
					id:'sysFormPanel',
					//iconCls:'adapter-node',
					iconCls:'user-node',
					collapsible:true,
					collapsed:true ,	
					titleCollapse: true
				}]
			},{
				xtype:"tabpanel",
				id:"centerTabPanel",
				region:"center",
				//enableTabScroll :true,
				autoScroll:true, 
				layoutOnTabChange:true,
				resizeTabs:true,
				activeTab:0,
				frame:true,
				border:true,
				bodyStyle:'overflow:hidden;',	
				items:[{
					xtype:"panel",
					closable:false,
					title:"Welcome",
					html:'<iframe width="100%" height="100%" border="0" frameborder="0" scrolling="no"  src="../runtime/welcome/catch-the-coins/index.html"></iframe>'
				}],
				plugins: new Ext.ux.RefreshTabTool(),
				listeners : {  
					'beforeremove' : function(tabcontrol,tab) {  
						
						if(tab.closeTabPrepared){
							return true;
						}
						tab.el.fadeOut({
							endOpacity: 0, //can be any value between 0 and 1 (e.g. .5)
							easing: 'easeOut',
							duration: .3,
							useDisplay: false,
							scope:tab,
							callback:function(){
								this.closeTabPrepared = true;
								delete allTabs["centerTabPanel__" + tab.id];
								tabcontrol.remove(this);
							}
						});	
						return false;
					},
					'tabchange':function(p,tab){
						var tabObj = allTabs["centerTabPanel__" + tab.id];
						if(tabObj){
							tabObj.page.dom.focus();
						}
					}					
				}			

			},{
				xtype:'toolbar',
				region: 'south',	
				border:false,
				id:'footer',
				items:[{
					xtype:'textfield',
					style:'border:1px inset #A9BFD3;background:transparent;',
					readOnly:true,
					width:300,
					id:'lblStatus'
				},{
					xtype:'tbseparator'
				},{
					xtype:'label',
					style:'margin-left:5px;margin-right:5px',
					text:'技术部 Copyright @ 2011 Phoenix New Media Inc. All Rights Reserved.'
				},{
					xtype:'tbfill'
				},{
					text:'在线用户(0)',
					id:'btnOnline',
					style:'margin-right:5px',
					scope:userOnlineMgr,
					handler:userOnlineMgr.showUserList
				},]
			}]		

		});
 
	},
	initXFormPanel:function(){
		Ext.getBody().mask('正在请求数据');
		Ext.Ajax.request({  
			url:'xform!FormListConfig.jhtml?nodeId='+nodeId__,   
			method:"get",
			scope:this,
			success:function(response,opts){
				Ext.getBody().unmask();
				var ret = Ext.util.JSON.decode(response.responseText);
				
				while(this.xformPanel.items&&this.xformPanel.items.length>0){
					this.xformPanel.remove(this.xformPanel.items.get(0));
				}
				
				var createNode = function(cfg){
					var nCfg ={
						leaf:true,
						iconCls :'extForm',
						blank:false
					};
					Ext.apply(nCfg,cfg);
					var n = new Ext.tree.TreeNode(nCfg);
					n.on('click',function(node,event){
						var atrr = node.attributes;
						this.openPage(atrr.url?atrr.url:'formMgr.html?nodeId='+nodeId__+'&formId=' + atrr.formId,atrr.blank,atrr.text);
					},devMgr);
					return n;
				}
				
				var createScriptNode = function(cfg){
					var nCfg ={
						leaf:true,
						iconCls:'script-node',
						blank:false
					};
					Ext.apply(nCfg,cfg);
					var n = new Ext.tree.TreeNode(nCfg);
					n.on('click',function(node,event){
						var atrr = node.attributes;
						this.openPage(atrr.url?atrr.url:'scriptMgr.jhtml?nodeId='+nodeId__+'&formId=' + atrr.formId,atrr.blank,atrr.text);
					},devMgr);
					return n;
				}
				
				var tree = new Ext.tree.TreePanel({
					containerScroll:true,
					rootVisible:false,
					useArrows:false,//用箭头还是加号
					border:true,
					lines :false,
					animate:true,
					root:new Ext.tree.TreeNode({
						text: 'root',
						draggable:false,
						children:[]
					})
				});
				this.xformPanel.add(tree);
				this.xformPanel.doLayout();
				
				var root = tree.root;
				var n = createNode({
					text:'全部表单',
					formId:0,
					iconCls:'allLeaf'
				});
				root.appendChild(n);
				var script_node = createScriptNode({
					text:'全部脚本库',
					formId:'',
					iconCls:'allLeaf'
				});
				root.appendChild(script_node);
				var lst_node = createScriptNode({
					text:'碎片类型列表',
					url:"../runtime/xlist!render.jhtml?nodeId="+nodeId__+"&formId=76&listId=156&where=[{'field':'nodeId','op':'%3d','value':"+nodeId__+",'andor':'and'}]",
					iconCls:'allLeaf'
				});
				root.appendChild(lst_node);
				var bannerNode = createScriptNode({
					text:'通栏列表',
					url:"../runtime/xlist!render.jhtml?nodeId="+nodeId__+"&formId=158&listId=247&where=[{'field':'nodeId','op':'%3d','value':"+nodeId__+",'andor':'and'}]",
					iconCls:'allLeaf'
				});
				root.appendChild(bannerNode);
				for(var i=ret.length-1;i>=0;i--){
					var item = ret[i];
					//系统表单不显示
					if(item.isSysForm != 0){
						continue;
					}
					var n = createNode({
						text:item.title+'(' + item.tableName + ')',
						formId:item.id
					});
					root.appendChild(n);
				}
				var n = createNode({
					text:'新建表单',
					url:'xform!designer.jhtml?nodeId=' + nodeId__,
					iconCls:'menu-add'
				});
				root.appendChild(n);
				
				var n = createNode({
					text:'导入表单',
					url:'../develop/xform!importForm.jhtml?nodeId=' + nodeId__,
					iconCls:'menu-add'
				});
				root.appendChild(n);
				//初始化脚本库
				//this.initScriptLibPanel(ret);
				
				//初始化脚本线程管理panel
				this.initScriptThreadPanel(ret);
				
				//初始化系统表单panel
				this.initSysFormPanel(ret);
			}
		});	
		
	},
	initScriptThreadPanel:function(ret){
	
		var createNode = function(cfg){
			var nCfg ={
				leaf:true,
				iconCls:'script-node',
				blank:false
			};
			Ext.apply(nCfg,cfg);
			var n = new Ext.tree.TreeNode(nCfg);
			n.on('click',function(node,event){
				var atrr = node.attributes;
				this.openPage(atrr.url,atrr.blank,atrr.text);
			},devMgr);
			return n;
		}
		
		var tree = new Ext.tree.TreePanel({
			containerScroll:true,
			rootVisible:false,
			useArrows:false,//用箭头还是加号
			border:true,
			lines :false,
			animate:true,
			root:new Ext.tree.TreeNode({
				text: 'root',
				draggable:false,
				children:[]
			})
		});
		var root = tree.root;
		var n = createNode({
			text:'查看脚本线程',
			formId:'',
			iconCls:'check',
			url:'./script/scriptThreadList.html?nodeId=' + nodeId__
		});
		root.appendChild(n);
		this.scriptThreadPanel.add(tree);
		this.scriptThreadPanel.doLayout();
	},
	initInterfacePanel:function(){
		while(this.interfacePanel.items&&this.interfacePanel.items.length>0){
			this.interfacePanel.remove(this.interfacePanel.items.get(0));
		}
	
		var tree = new Ext.tree.TreePanel({
			containerScroll:true,
			rootVisible:false,
			useArrows:false,//用箭头还是加号
			border:true,
			lines :false,
			animate:true,
			root:new Ext.tree.TreeNode({
				text: 'root',
				draggable:false,
				children:[]
			})
		});
		this.interfacePanel.add(tree);
		this.interfacePanel.doLayout();
		
		var root = tree.root;
		root.appendChild(
			new Ext.tree.TreeNode({
				text:'查看接口列表',
				iconCls:'check',
				listeners:{
					scope:this,
					click:function(){
						this.openPage('interface/list.jhtml?nodeId=' + nodeId__,false,'接口列表');
					}
				}
			}),
			new Ext.tree.TreeNode({
				text:'新建接口',
				iconCls:'menu-add',
				listeners:{
					scope:this,
					click:function(){
						this.openPage('interface_mgr!view.jhtml?nodeId='+nodeId__,false,'新建接口');
					}
				}
			})
		);
	},
	
	initResourcePanel:function(){
		while(this.resourcePanel.items&&this.resourcePanel.items.length>0){
			this.resourcePanel.remove(this.resourcePanel.items.get(0));
		}
	
		var tree = new Ext.tree.TreePanel({
			containerScroll:true,
			rootVisible:false,
			useArrows:false,//用箭头还是加号
			border:true,
			lines :false,
			animate:true,
			root:new Ext.tree.TreeNode({
				text: 'root',
				draggable:false,
				children:[]
			})
		});
		this.resourcePanel.add(tree);
		this.resourcePanel.doLayout();
		
		var root = tree.root;
		root.appendChild(
			new Ext.tree.TreeNode({
				text:'文件夹列表',
				iconCls:'check',
				listeners:{
					scope:this,
					click:function(){
						this.openPage('../runtime/resource/list.jhtml?nodeId=' + nodeId__,false,'文件夹列表');
					}
				}
			}),
			
			new Ext.tree.TreeNode({
				text:'上传到默认目录',
				iconCls:'menu-add',
				listeners:{
					scope:this,
					click:function(){
						this.openPage('fileMgr!uploadpage.jhtml?mode=1&nodeId=' + nodeId__,false,'上传到默认目录');
					}
				}
			}),
			new Ext.tree.TreeNode({
				text:'自定义目录上传',
				iconCls:'menu-add',
				listeners:{
					scope:this,
					click:function(){
						this.openPage('fileMgr!uploadpage.jhtml?mode=2&nodeId='+nodeId__,false,'自定义目录上传');
					}
				}
			}),
			new Ext.tree.TreeNode({
				text:'自定义URL上传',
				iconCls:'menu-add',
				listeners:{
					scope:this,
					click:function(){
						this.openPage('fileMgr!uploadpage.jhtml?mode=3&nodeId='+nodeId__,false,'自定义URL上传');
					}
				}
			})
			
		);
	},
	
		
	initTaskPanel:function(){
			var createNode = function(cfg){
					var nCfg ={
						leaf:true,
						iconCls:'task-node',
						blank:false
					};
					Ext.apply(nCfg,cfg);
					var n = new Ext.tree.TreeNode(nCfg);
					n.on('click',function(node,event){
						var atrr = node.attributes;
						this.openPage(atrr.url?atrr.url:'task/taskMgr.html?nodeId='+nodeId__+'&taskId=' + atrr.taskId,atrr.blank,atrr.text);
					},devMgr);
					return n;
			}
			var tree = new Ext.tree.TreePanel({
					containerScroll:true,
					rootVisible:false,
					useArrows:false,//用箭头还是加号
					border:true,
					lines :false,
					animate:true,
					root:new Ext.tree.TreeNode({
						text: 'root',
						draggable:false,
						children:[]
					})
				});
				
				var root = tree.root;

				var n = createNode({
					text:'新建任务',
					url:'task/taskMgr.html?nodeId=' + nodeId__,
					iconCls:'menu-add'
				});
				
				var checkTask = createNode({
					iconCls:'check',
					text:'查看任务列表',
					url:'task!showCheckTaskPage.jhtml?nodeId=' + nodeId__
				});
				root.appendChild(checkTask);
				root.appendChild(n);
				this.taskPanel.add(tree);
				this.taskPanel.doLayout();
				
	},
	initSubscriblePanel:function(){
		Ext.getBody().mask('正在请求数据');
		Ext.Ajax.request({  
			url:'source.jhtml?nodeId='+nodeId__,   
			method:"get",
			scope:this,
			success:function(response,opts){
				Ext.getBody().unmask();
				var ret = Ext.util.JSON.decode(response.responseText);
				while(this.subscriblePanel.items&&this.subscriblePanel.items.length>0){
					this.subscriblePanel.remove(this.subscriblePanel.items.get(0));
				}						
				var createNode = function(cfg){
					var nCfg ={
						leaf:true,
						iconCls:'source-node',
						blank:false
					};
					Ext.apply(nCfg,cfg);
					var n = new Ext.tree.TreeNode(nCfg);
					n.on('click',function(node,event){
						var atrr = node.attributes;
						this.openPage(atrr.url?atrr.url:'source/index.jhtml?nodeId='+nodeId__+'&id=' + atrr.sId,atrr.blank,atrr.text);
					},devMgr);
					return n;
				}
				
				var tree = new Ext.tree.TreePanel({
					containerScroll:true,
					rootVisible:false,
					useArrows:false,//用箭头还是加号
					border:true,
					lines :false,
					animate:true,
					root:new Ext.tree.TreeNode({
						text: 'root',
						draggable:false,
						children:[]
					})
				});
				this.subscriblePanel.add(tree);
				this.subscriblePanel.doLayout();
				
				var root = tree.root;
				for(var i=ret.length-1;i>=0;i--){
					var item = ret[i];
					var n = createNode({
						text:item.title+ "("+ item.id +")",
						sId:item.id
					});
					root.appendChild(n);
				}
				var n = createNode({
					text:'发布表单',
					url:'source/index.jhtml?id=0&nodeId=' + nodeId__,
					iconCls:'menu-add'
				});
				root.appendChild(n);
				
			}
		});	
	},
	//状态 
	initServiceStatePanel:function(){	
		var tree = new Ext.tree.TreePanel({
			containerScroll:true,
			rootVisible:false,
			useArrows:false,//用箭头还是加号
			border:true,
			lines :false,
			animate:true,
			root:new Ext.tree.TreeNode({
				text: 'root',
				draggable:false,
				children:[]
			})
		});
		this.serviceStatePanel.add(tree);
		this.serviceStatePanel.doLayout();
		var root = tree.root;
		root.appendChild(
			new Ext.tree.TreeNode({
				text:'状态总览',
				iconCls:'check',
				listeners:{
					scope:this,
					click:function(){
						this.openPage('../monitor/service.jhtml?nodeID=' + nodeId__,false,'状态总览');
					}
				}
			}),
			new Ext.tree.TreeNode({
				text:'任务管理',
				iconCls:'check',
				listeners:{
					scope:this,
					click:function(){
						this.openPage('../monitor/service!showTask.jhtml?nodeID=' + nodeId__,false,'任务管理');
					}
				}
			}),
			new Ext.tree.TreeNode({
				text:'查看Log',
				iconCls:'check',
				listeners:{
					scope:this,
					click:function(){
						this.openPage('../monitor/service!showLog.jhtml',false,'日志查看');
					}
				}
			})
		);
	},
	//初始化动态前端抽屉
	initDynamicWebPanel:function(){
		while(this.dynamicWebPanel.items&&this.dynamicWebPanel.items.length>0){
			this.dynamicWebPanel.remove(this.dynamicWebPanel.items.get(0));
		}
	
		var tree = new Ext.tree.TreePanel({
			containerScroll:true,
			rootVisible:false,
			useArrows:false,//用箭头还是加号
			border:true,
			lines :false,
			animate:true,
			root:new Ext.tree.TreeNode({
				text: 'root',
				draggable:false,
				children:[]
			})
		});
		this.dynamicWebPanel.add(tree);
		this.dynamicWebPanel.doLayout();
		
		var root = tree.root;
		root.appendChild(
			new Ext.tree.TreeNode({
				text:'动态前端列表',
				iconCls:'check',
				listeners:{
					scope:this,
					click:function(){
						this.openPage('dynamic!index.jhtml?nodeId=' + nodeId__,false,'动态前端列表');
					}
				}
			}),
			new Ext.tree.TreeNode({
				text:'页面列表',
				iconCls:'check',
				listeners:{
					scope:this,
					click:function(){
						this.openPage('dynamic/index.jhtml?nodeId=' + nodeId__ + '&listId=1',false,'页面列表');
					}
				}
			}),
			new Ext.tree.TreeNode({
				text:'新建动态页面',
				iconCls:'menu-add',
				listeners:{
					scope:this,
					click:function(){
						this.openPage('dynamic/view.jhtml?nodeId=' + nodeId__ + '&id=0&formId=1',false,'新建页面');//固定formId=1
					}
				}
			})
		);
		
		
	},
	
	//初始化脚本流程图抽屉
	initWorkflowPanel:function(){
		while(this.workflowPanel.items&&this.workflowPanel.items.length>0){
			this.workflowPanel.remove(this.workflowPanel.items.get(0));
		}
	
		var tree = new Ext.tree.TreePanel({
			containerScroll:true,
			rootVisible:false,
			useArrows:false,//用箭头还是加号
			border:true,
			lines :false,
			animate:true,
			root:new Ext.tree.TreeNode({
				text: 'root',
				draggable:false,
				children:[]
			})
		});
		this.workflowPanel.add(tree);
		this.workflowPanel.doLayout();
		
		var root = tree.root;
		root.appendChild(
			new Ext.tree.TreeNode({
				text:'脚本流程列表',
				iconCls:'check',
				listeners:{
					scope:this,
					click:function(){
						this.openPage('workflowMgr!list.jhtml?nodeId=' + nodeId__,false,'脚本流程列表');
					}
				}
			}),
			new Ext.tree.TreeNode({
				text:'新建流程',
				iconCls:'menu-add',
				listeners:{
					scope:this,
					click:function(){
						this.openPage('workflowMgr.jhtml?nodeId=' + nodeId__ + '&processId=0',false,'新建流程');
					}
				}
			})
		);
		
		
	},
	
//用户和组管理
	initUserPanel:function(){
		while(this.userPanel.items&&this.userPanel.items.length>0){
			this.userPanel.remove(this.userPanel.items.get(0));
		}
	
		var tree = new Ext.tree.TreePanel({
			containerScroll:true,
			rootVisible:false,
			useArrows:false,//用箭头还是加号
			border:true,
			lines :false,
			animate:true,
			root:new Ext.tree.TreeNode({
				text: 'root',
				draggable:false,
				children:[]
			})
		});
		this.userPanel.add(tree);
		this.userPanel.doLayout();
		var root = tree.root;
		root.appendChild(
			new Ext.tree.TreeNode({
				text:'用户管理',
				iconCls:'user-node',
				listeners:{
					scope:this,
					click:function(){
						this.openPage('authority!getUserManagerPage.jhtml?page=1&nodeId=' + nodeId__,false,'用户管理');
					}
				}
			}),
			new Ext.tree.TreeNode({
				text:'组管理',
				iconCls:'user-node',
				listeners:{
					scope:this,
					click:function(){
						this.openPage('authority!getGroupManagerPage.jhtml?page=1&nodeId=' + nodeId__,false,'组管理');
					}
				}
			})
		);
		
	},	


//初始化系统表单
	initSysFormPanel:function(ret){
	
		while(this.sysFormPanel.items&&this.sysFormPanel.items.length>0){
			this.sysFormPanel.remove(this.sysFormPanel.items.get(0));
		}
					
		var tree = new Ext.tree.TreePanel({
			containerScroll:true,
			rootVisible:false,
			useArrows:false,// 用箭头还是加号
			border:true,
			lines :false,
			animate:true,
			root:new Ext.tree.TreeNode({
				text: 'root',
				draggable:false,
				children:[]
			})
		});
		this.sysFormPanel.add(tree);
		this.sysFormPanel.doLayout();
					
		var root = tree.root;
		var isExist = false;
		for(var i=ret.length-1;i>=0;i--){
			var item = ret[i];
			// 仅显示系统表单isSysForm=2
			if(item.isSysForm != 2){
				continue;
			}
			isExist = true;
			var node = new Ext.tree.TreeNode({
				text:item.title+'(' + item.tableName + ')',
				iconCls :'extForm',
				item :item,
				listeners:{
					scope:this,
					click:function(node,event){
						var item = node.attributes.item;
						this.openPage('formMgr.html?nodeId='+nodeId__+'&formId='+item.id,false,item.title+'(' + item.tableName + ')');
					}
				}
			});
			root.appendChild(node);
		}
		
		if(isExist){
			root.appendChild(
				new Ext.tree.TreeNode({
					text:'字段映射配置',
					iconCls :'extForm',
					listeners:{
						scope:this,
						click:function(){
							this.openPage('xformAdapter!fieldAdapterCfg.jhtml?nodeId='+nodeId__,false,'字段映射配置');
						}
					}
				}),
				new Ext.tree.TreeNode({
					text:'重置系统表单',
					iconCls :'extForm',
					listeners:{
						scope:this,
						click:function(){
							Ext.MessageBox.confirm("提示","确定要重置系统表单吗？",function(button,text){
				                if(button=="yes"){
				                   	Ext.getBody().mask('正在请求数据');
									Ext.Ajax.request({  
										url:'xformAdapter!initOrResetSysForm.jhtml?nodeId='+nodeId__,   
										method:"get",
										scope:this,
										success:function(response,opts){
											Ext.getBody().unmask();
											var ret = Ext.util.JSON.decode(response.responseText);
											if(ret.success){
												Ext.Toast.show('重置系统表单成功',{
												   title:'提示',
												   buttons: Ext.Msg.OK,
												   animEl: 'elId',
												   minWidth:420,
												   icon: Ext.MessageBox.INFO
											    });
											    devMgr.viewport.destroy();
												devMgr.init();
											}else{
												Ext.Msg.show({
													title:'重置系统表单异常',
													msg: ret.msg,
													buttons: Ext.Msg.OK,
													animEl: 'elId',
													minWidth:420,
													icon: Ext.MessageBox.ERROR 
												});
											}
										}
									});
				                }
							});
						}
					}
				})
			);
		}else{
			root.appendChild(
				new Ext.tree.TreeNode({
					text:'初始化系统表单',
					iconCls :'extForm',
					listeners:{
						scope:this,
						click:function(){
							Ext.getBody().mask('正在请求数据');
							Ext.Ajax.request({  
								url:'xformAdapter!initOrResetSysForm.jhtml?nodeId='+nodeId__,   
								method:"get",
								scope:this,
								success:function(response,opts){
									Ext.getBody().unmask();
									var ret = Ext.util.JSON.decode(response.responseText);
									if(ret.success){
										Ext.Toast.show('初始化系统表单成功',{
										   title:'提示',
										   buttons: Ext.Msg.OK,
										   animEl: 'elId',
										   minWidth:420,
										   icon: Ext.MessageBox.INFO
									    });
									    devMgr.viewport.destroy();
										devMgr.init();
									}else{
										Ext.Msg.show({
											title:'初始化系统表单异常',
											msg: ret.msg,
											buttons: Ext.Msg.OK,
											animEl: 'elId',
											minWidth:420,
											icon: Ext.MessageBox.ERROR 
										});
									}
								}
							});
						}
					}
				})
			);
		}
	},

	/****tabPanel功能**/
	//打开页面
	openPage:function(url,blank,title){
		if(blank){
			window.open(url);
		}else{
			centerTabPanel.addIframe(null,title,url);
		}
	},
	initTabPanelEvent:function(){
		centerTabPanel.addIframe=function(id,title,url){
			if(id==null) id = "tab_"+ hex_md5(url);
			var t=this;
			var menu=new Ext.menu.Menu({
				items:[
					{
						text:"关闭这个窗口"
						,id:"btnCloseThisTab"
						,iconCls:"iconDel"
						,handler:function(){
							var id=this.parentMenu.tab.id;
							if(allTabs[id] && allTabs[id].tab){
								t.remove(allTabs[id].tab);
								delete allTabs[id];
							}
						}
					}
					,{
						text:"关闭其它窗口"
						,id:"btnCloseOtherTabs"
						,iconCls:"iconDel"
						,handler:function(){
							var id=this.parentMenu.tab.id;
							var panel=allTabs[id];
							for(var i in allTabs){
								if(i!=id){
									t.remove(allTabs[i].tab);
									delete allTabs[id];
								}
							}
							allTabs[id] = panel;
						}
					}
					,{
						text:"关闭所有窗口"
						,id:"btnCloseAllTabs"
						,iconCls:"iconDel"
						,handler:function(){
							for(var i in allTabs){
								t.remove(allTabs[i].tab);
								delete allTabs[i];
							}
							allTabs={};
						}
					},'-',{
						text:"弹出打开"
						,handler:function(){
							var id=this.parentMenu.tab.id;
							var iframe = allTabs[id].page;
							window.open(iframe.dom.getAttribute("src").toString());
						}
					}	
					,{
						text:"重新载入"
						,id:"btnRefreshTab"
						,iconCls:"iconReresh"
						,handler:function(){
							var id=this.parentMenu.tab.id;
							var tabPage = allTabs[id].tab;
							centerTabPanel.addIframe(tabPage.id,tabPage.title,tabPage.url);
						}
					}		
				]
			});					
			var showMenu=function(e){
				menu.tab=e.browserEvent.currentTarget;
				menu.showAt(e.xy);
			}

			this.addIframe=function(id,title,url){
				if(id==null) id = "tab_"+ hex_md5(url);
				var n =Ext.getCmp(id);
				var tab = allTabs['centerTabPanel__' + id];
				if(n && tab) {//页签已打开
					var openedFrame = tab.page;
					openedFrame.remove();
					centerTabPanel.setActiveTab(n); 
				}else{	
					n = centerTabPanel.add({ 
						'id':id,
						"title":title,
						"url":url,
						closable:true
					}); 
					centerTabPanel.setActiveTab(n); 
				}
				n.body.addClass("waiting7");	
				var iframe=n.body.createChild({tag:"iframe",scrolling:"auto", frameborder:"0",style:'visibility:hidden', width:"100%", height:"100%"});
				iframe.dom.onload = function(){
					this.focus();
					devMgr.lblStatus.setValue(this.src);
					var frameEl =  Ext.fly(this);
					frameEl.parent().removeClass("waiting7");
					frameEl.fadeIn({
						endOpacity: 1, //can be any value between 0 and 1 (e.g. .5)
						easing: 'easeIn',
						duration: .3,
						useDisplay: false
					});	
				};	
				iframe.dom.src = url;
				iframe.focus();//让该页面获得焦点
				var tab=this.getTabEl(this.activeTab);
				allTabs[tab.id]={tab:n,page:iframe};
				tab.setAttribute("title",title);
				var el=Ext.fly(tab);
				el.on("dblclick",function(){
					//window.open(iframe.dom.getAttribute("src").toString());
					var id=tab.id;
					if(allTabs[id] && allTabs[id].tab){
						t.remove(allTabs[id].tab);
						delete allTabs[id];
					}
				});
				el.on("contextmenu",showMenu);
				return {iframe:iframe,panel:n};	
			}
			return this.addIframe(id,title,url);
		};
		//重新加载页签里的iframe
		centerTabPanel.reloadIframe=function(id,newUrl,title){
			var id = "centerTabPanel__"+ id;
			var tab = allTabs[id];
			if(tab){
				var iframe = tab.page;
				iframe.dom.src = newUrl;
				tab.tab.setTitle(title);
				var tabLiEl = Ext.get(id);
				tabLiEl.dom.setAttribute("title",title);
			}
		};

	},
	/****tabPanel功能 end**/
	
	initUserInfo:function(){
		var uid = Cookies.get('cmpp_cn');
		if(uid)
			Ext.getCmp('lbUserInfo').setText('欢迎您,<font color="blue">' + uid + '</font>');
	}
};
/*roney-->*/
window.initTaskTree=devMgr.initTaskPanel;
/*<---roney*/

</script>

<script>
Ext.onReady(function(){
	Ext.BLANK_IMAGE_URL = "../res/js/ext2/resources/images/default/s.gif";
	Ext.lOADING_IMAGE_URL= "../res/js/ext2/resources/images/default/grid/wait.gif"; //resources\images\default\grid
	Ext.QuickTips.init();
	Ext.form.Field.prototype.msgTarget = 'qtip';

	devMgr.init();
		
	//更新在线用户人数 //注册用户正处于使用状态
	userOnlineMgr.init('developIndex_' + 'nodeId' + nodeId__);
});
</script>
	
</body>
</html>