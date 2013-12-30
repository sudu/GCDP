 <!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <title>CMPP</title>
	<link rel="shortcut icon" href="../res/img/favicon.ico" />
	<link rel="stylesheet" type="text/css" href="../res/js/ext2/resources/css/ext-all.css" />
	<link rel="stylesheet" type="text/css" href="../res/js/ext2/resources/css/patch.css" />
	<script type="text/javascript" src="../res/js/md5.js"></script>
 	<script type="text/javascript" src="../res/js/ext2/adapter/ext/ext-base.js"></script>
    <script type="text/javascript" src="../res/js/ext2/ext-all-debug.js"></script>	
	<script type="text/javascript" src="../res/js/ext2/ext-lang-zh_CN.js"></script>
	<script type="text/javascript" src="../res/js/ext_base_extension.js"></script>
	<script type="text/javascript" src="../res/js/CookiesHelper.js"></script>
	<script type="text/javascript" src="../res/js/userOnlineMgr.js"></script>		<!--在线用户管理 -->
	<style>
		/*按钮图标*/
		.extAdd{background:url("../res/js/ext2/resources/images/default/dd/drop-add.gif") left  no-repeat !important;}
		.extDelete{background:url("./../res/img/runTime/delete1.gif") left  no-repeat !important;}
		.extHelp{background:url("../res/img/help3.gif") left  no-repeat !important;}
		.extRefresh{background:url("../res/img/arrow_refresh.png") left  no-repeat !important;}
		.extMail{background:url("../res/img/mail.gif") left  no-repeat !important;}
		.extMail2{background:url("../res/img/mail2.gif") left  no-repeat !important;}
		
		.x-tool-refresh{position:absolute;right:2px;top:5px;z-index:9;}
		.x-tab-strip-active span.x-tab-strip-text {cursor:default;}

		.online_list_user ul li {
			margin: 2px 0 0;
			height: 28px;
			overflow: hidden;
			padding-left: 5px;
			clear: both;
		}
		
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
		.waiting7{background:url(../res/img/loading7.gif) no-repeat center 50px;}
		
		/****HTML5气泡**/
.example-right {
	position:relative;
	padding:15px 30px;
	margin:0;
	width:150px;
	height:80px;
	height: 100px;
	text-align: center;
	color:#fff;
	background:#5a8f00; /* default background for browsers without gradient support */
	/* css3 */
	background:-webkit-gradient(linear, 0 0, 0 100%, from(#b8db29), to(#5a8f00));
	background:-moz-linear-gradient(#b8db29, #5a8f00);
	background:-o-linear-gradient(#b8db29, #5a8f00);
	background:linear-gradient(#b8db29, #5a8f00);
	-webkit-border-radius:10px;
	-moz-border-radius:10px;
	border-radius:10px;
}

/* creates the triangle */
.example-right:after {
	content:"";
	position:absolute;
	bottom:-50px;
	left:160px;
	border-width:0 20px 50px 0px;
	border-style:solid;
	border-color:transparent #5a8f00;
    /* reduce the damage in FF3.0 */
    display:block; 
    width:0;
}
/****HTML5气泡 结束**/
		
/****按钮****/		
/* blue pill
*******************************************************************************/
button.blue-pill {
  background-color: #a5b8da;
  background-image: -webkit-gradient(linear, left top, left bottom, from(#a5b8da), to(#7089b3));
  /* Saf4+, Chrome */
  background-image: -webkit-linear-gradient(top, #a5b8da, #7089b3);
  background-image: -moz-linear-gradient(top, #a5b8da, #7089b3);
  background-image: -ms-linear-gradient(top, #a5b8da, #7089b3);
  background-image: -o-linear-gradient(top, #a5b8da, #7089b3);
  background-image: linear-gradient(top, #a5b8da, #7089b3);
  border-top: 1px solid #758fba;
  border-right: 1px solid #6c84ab;
  border-bottom: 1px solid #5c6f91;
  border-left: 1px solid #6c84ab;
  -webkit-border-radius: 18px;
  -moz-border-radius: 18px;
  -ms-border-radius: 18px;
  -o-border-radius: 18px;
  border-radius: 18px;
  -webkit-box-shadow: inset 0 1px 0 0 #aec3e5;
  -moz-box-shadow: inset 0 1px 0 0 #aec3e5;
  -ms-box-shadow: inset 0 1px 0 0 #aec3e5;
  -o-box-shadow: inset 0 1px 0 0 #aec3e5;
  box-shadow: inset 0 1px 0 0 #aec3e5;
  color: #fff;
  font: bold 11px "Lucida Grande", "Lucida Sans Unicode", "Lucida Sans", Geneva, Verdana, sans-serif;
  line-height: 1;
  padding: 8px 0;
  text-align: center;
  text-shadow: 0 -1px 1px #64799e;
  text-transform: uppercase;
  width: 74px; 
}
button.blue-pill:hover {
    background-color: #9badcc;
    background-image: -webkit-gradient(linear, left top, left bottom, from(#9badcc), to(#687fa6));
    /* Saf4+, Chrome */
    background-image: -webkit-linear-gradient(top, #9badcc, #687fa6);
    background-image: -moz-linear-gradient(top, #9badcc, #687fa6);
    background-image: -ms-linear-gradient(top, #9badcc, #687fa6);
    background-image: -o-linear-gradient(top, #9badcc, #687fa6);
    background-image: linear-gradient(top, #9badcc, #687fa6);
    border-top: 1px solid #6d86ad;
    border-right: 1px solid #647a9e;
    border-bottom: 1px solid #546685;
    border-left: 1px solid #647a9e;
    -webkit-box-shadow: inset 0 1px 0 0 #a5b9d9;
    -moz-box-shadow: inset 0 1px 0 0 #a5b9d9;
    -ms-box-shadow: inset 0 1px 0 0 #a5b9d9;
    -o-box-shadow: inset 0 1px 0 0 #a5b9d9;
    box-shadow: inset 0 1px 0 0 #a5b9d9;
    cursor: pointer; }
  button.blue-pill:active {
    border: 1px solid #546685;
    -webkit-box-shadow: inset 0 0 8px 2px #7e8da6, 0 1px 0 0 #eeeeee;
    -moz-box-shadow: inset 0 0 8px 2px #7e8da6, 0 1px 0 0 #eeeeee;
    -ms-box-shadow: inset 0 0 8px 2px #7e8da6, 0 1px 0 0 #eeeeee;
    -o-box-shadow: inset 0 0 8px 2px #7e8da6, 0 1px 0 0 #eeeeee;
    box-shadow: inset 0 0 8px 2px #7e8da6, 0 1px 0 0 #eeeeee; }
	
.close{ 
	width:26px;
 	height:26px;
	background:url(../res/img/close.png) no-repeat;
	-webkit-transition:all 1s ease-in-out;/*过渡的性质，时间，类型(ease-in-out由快到慢到快)*/
	-moz-transition:all 1s ease-in-out;
	-o-transition:all 1s ease-in-out;
	transition:all 1s ease-in-out;
}

.close:hover{
	-webkit-transform:rotate(360deg) scale(1.2);/*旋转360度，放大1.2倍*/
	-moz-transform:rotate(360deg) scale(1.2);
	-o-transform:rotate(360deg) scale(1.2);
	transform:rotate(360deg) scale(1.2);
}

	</style>
	
<script>
	var nodeId__ = "#{nodeId!0}";
	var sysMenu = "${(menuConfig!"")?js_string}";
	var globalVars = {
		nodeId : nodeId__,
		welcomeUrl:"${welcomeUrl!""}"
	};
	
	//*******全局变量********//
	window.centerTabPanel = null;
		
	Ext.ux.RefreshTabTool = function(){
		this.init= function(ct) {
			var maximizeTool = {
				id: 'refresh', 
				iconCls:'extRefresh',
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
	centerTabPanel.addIframe(null,title ,url);
}
//在指定ID页签里加载新的url
window.reloadTab = function(id,newUrl,newTitle){
	centerTabPanel.reloadIframe(id,newUrl,newTitle);
}

</script>

<!--head inject region -->
${headInject!""}

</head>
<body>	


<script type="text/javascript">
allTabs={};
runtimeMgr = {
	localMenuKey:'CMPP_MENUS_LOCAL_DATA_' + nodeId__,
	systemMenuKey:'CMPP_MENUS_SYS_DATA_' + nodeId__,
	topToolbar:null,
	menusPanel:null,
	init:function(){
		this.initUI();	
		centerTabPanel = Ext.getCmp('centerTabPanel');
		centerTabPanel.body.setStyle({overflow:'hidden'});
		this.initTabPanelEvent();
		this.topToolbar = Ext.getCmp('topToolbar');
		this.initDragDrop();
		this.menusPanel = Ext.getCmp('menusPanel');
		this.lblStatus	= Ext.getCmp('lblStatus');
		this.tbtnshowTopMenuWin = Ext.getCmp('tbtnshowTopMenuWin');
		this.tbtnshowTopMenuWin.setVisible(false);
		this.menusPanel.bbar.setVisible(false);
		
		this.initUserInfo();
		this.initLocalMenus();
		this.getMenus();
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
					text:'添加菜单',
					id:'tbtnshowTopMenuWin',
					style:'margin-left:5px',
					iconCls:'extAdd',
					scope:this,
					handler:this.showTopMenuWin
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
					text:'配置菜单',
					enableToggle :true,
					scope:this,
					handler:function(obj,event){
						this.isMenuEditState = obj.pressed ;
						this.tbtnshowTopMenuWin.setVisible(obj.pressed);
						this.menusPanel.bbar.setVisible(obj.pressed);
						//highlight
						if(obj.pressed){
							this.tbtnshowTopMenuWin.el.highlight();
							this.menusPanel.bbar.first().first().highlight();
						}else{
							this.menusPanel.bbar.first().first().hide();
						}
						
						this.lblStatus.setValue(obj.pressed?'系统正处于菜单配置状态中...':'');
						obj.setText(obj.pressed?'保存配置':'配置菜单');
						if(!obj.pressed){
							this.lblStatus.setValue('正在保存自定义菜单配置...');
							this.saveCustomMenu();
							this.lblStatus.setValue('');
							//this.dropTarget.lock();
						}else{
							//this.dropTarget.unlock();
						}
						this.lockSysMenu(obj.pressed);
					}
				},{
					xtype:'tbseparator'
				}/*,{
					xtype:'button',
					text:'退出系统',
					style:'margin-right:5px',
					handler:function(){
					
					}
				}*/]
			},{
				xtype: 'panel',
				title: 'CMPP',
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
				bbar:[{
					text:'帮助',
					style:'display:none',
					iconCls:'extHelp'
				},{
					text:'新建',
					iconCls:'extAdd',
					tooltip:'新建菜单栏',
					id:"tbtnshowAccMenuWin",
					scope:this,
					handler:function(obj,event){
						this.showAccMenuWin(obj,event);
					}
				},{
					xtype:'tbfill'
				},{
					text:'刷新',
					iconCls:'extRefresh',
					//style:'display:none',
					handler:function(){
						runtimeMgr.getMenus(true);
					}
				}],
				layoutConfig:{
					titleCollapse: true,   
					collapseFirst : true,   
					animate: true,   
					hideCollapseTool : false,   
					activeOnTop: false  	
				}
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
				bodyStyle:'overflow:hidden',		
				items:[{
					xtype:"panel",
					closable:false,
					title:"Welcome",
					autoScroll:true,
					html:'<iframe width="100%" height="100%" border="0" frameborder="0" scrolling="auto"  src="'+ (globalVars.welcomeUrl || 'welcome/catch-the-coins/index.html') +'"></iframe>'
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
				},{
					text:'(0)',
					id:'btnNotify',
					style:'margin-right:5px',
					iconCls:'extMail'
				},{
					xtype:'textfield',
					style:'display:none',
					id:'txtMsg'
				}]
			}]		

		});
 
	},
	initDragDrop:function(){
		this.dropTarget = new Ext.dd.DropTarget(this.topToolbar.getEl().dom,{
			ddGroup: 'TreePanelDDGroup',
			notifyDrop : function(ddSource, e, data) {
				//if(runtimeMgr.isMenuEditState){
					runtimeMgr.addTopMenu(data.node.attributes);
				/*}else{
					//todo
					alert('系统不处于菜单配置状态,该操作无效！');
				}*/
			}
		});
		//this.dropTarget.lock();
	},
	initUserInfo:function(){
		var uid = Cookies.get('cmpp_cn');
		if(uid)
			Ext.getCmp('lbUserInfo').setText('欢迎您,<font color="blue">' + uid + '</font>');
		else{
			Ext.Msg.show({
				title:'您尚未登录',
				msg: '确保您的电脑已经加入域,点刷新按钮将会自动登录系统.',
				buttons: Ext.Msg.OK,
				buttonText:'刷新',
				icon: Ext.MessageBox.INFO ,
				fn: function(btn, text){
					location.reload();
				}
			});
		}		
	},
	getMenus:function(refresh){
		/* cds modify at 2012/2/16 不再从本地存储获取，每次都去服务器请求最新的菜单配置
		if(refresh){
			this.ajaxRequest('rtMgr!readSysMenu.jhtml?nodeId=' + nodeId__,this.initMenus,{refresh:true});
		}else{
			var menuStr = localStorage.getItem(this.systemMenuKey);//读缓存
			if(menuStr){
				try{
					var data = Ext.decode(menuStr);
				}catch(ex){
					console.info('菜单缓存不存在或解析出错.ERROR:' + ex); 
				}
				if(data && ((data.topBar && data.topBar.children) || (data.accordion && data.accordion.length>0))){
					this.initMenus(data,{});
					return;
				}
			}
			this.ajaxRequest('rtMgr!readSysMenu.jhtml?nodeId=' + nodeId__,this.initMenus,{mustCache:true});
		}
		*/
		//this.ajaxRequest('rtMgr!readSysMenu.jhtml?nodeId=' + nodeId__,this.initMenus,{refresh:true});
		
		try{
			var sysMenuJSON = Ext.decode(sysMenu);
			
		}catch(ex){
			Ext.CMPP.warn("错误","配置不是一个合法的JSON");
		}
		this.initMenus(sysMenuJSON,{refresh:true});		
		
	},
	initMenus:function(data,options){
		if(!options) options={};
		if(options.mustCache || options.refresh) 	localStorage.setItem(runtimeMgr.systemMenuKey,Ext.encode(data));
		if(options.refresh){
			//重刷,清除已加载的
			var btns = runtimeMgr.topToolbar.items;
			var len = btns.length;
			for(var i=0;i<len;i++){
				var btn = btns.get(i);
				if(btn.isCustom || btn.id=="tbtnshowTopMenuWin")break;
				btn.getEl().parent().remove();  
				runtimeMgr.topToolbar.items.removeAt(i--);
				len--;
				btn.destroy();
				
			}
			var menus = runtimeMgr.menusPanel.items;
			if(menus){
				len = menus.length;
				for(var i=0;i<len;i++){
					var accPanel = menus.get(i);
					if(accPanel.isCustom)break;
					runtimeMgr.menusPanel.remove(accPanel);
					i--;len--;
					
				}
			}

		}
				
		if(data.topBar && data.topBar.children){
			for(var i=data.topBar.children.length-1;i>=0;i--){
				var tb = data.topBar.children[i];
				var cfg = {
					cfgData:tb,
					scope:tb,
					disabled:runtimeMgr.isMenuEditState,
					handler:function(obj,event){
						if(!runtimeMgr.isMenuEditState){
							runtimeMgr.openPage(this.url,this.blank,this.text);
						}
					}
				};
				Ext.applyDeep(cfg,tb);
				runtimeMgr.topToolbar.insertButton(0,new Ext.Toolbar.Button(cfg));
			}
		}	
		
		//添加非叶子节点
		var addSubRoot = function(root,cfg){
			var subRoot = new Ext.tree.TreeNode(cfg);
			root.appendChild(subRoot);
			
			if(cfg.url){
				subRoot.on('click',function(node,event){
					if(!runtimeMgr.isMenuEditState){
						this.openPage(node.attributes.url,node.attributes.blank,node.attributes.text);
					}
				},runtimeMgr);
			}
			
			return subRoot;
		}
		
		//递归加载树
		var addNode = function(root,nodeCfg){
			if(nodeCfg.dataUrl){
				if(nodeCfg.asyn){//异步
					nodeCfg.loader = new Ext.tree.TreeLoader({
						preloadChildren:true,
						dataUrl: nodeCfg.dataUrl
					});
					nodeCfg.listeners={
						"beforeappend":function(tree,thisNode,node){
							node.on('click',function(node,event){
								if(!this.isMenuEditState){
									this.openPage(node.attributes.url,node.attributes.blank,node.attributes.text);
								}
							},runtimeMgr);
							if(node.attributes.dataUrl){
								node.attributes.loader.dataUrl = node.attributes.dataUrl;
							}
							node.on("beforeappend",arguments.callee);
						}
					}
					var n = new Ext.tree.AsyncTreeNode(nodeCfg);
					n.cfgData=nodeCfg;
					n.on('click',function(node,event){
						if(!this.isMenuEditState){
							this.openPage(node.attributes.url,node.attributes.blank,node.attributes.text);
						}
					},runtimeMgr);
					root.appendChild(n);
				}else{
					var subRoot = addSubRoot(root,nodeCfg);
					//从缓存读取
					var localData = localStorage.getItem(nodeCfg.dataUrl);
					if(localData && !options.refresh){
						try{localData = Ext.decode(localData);}catch(ex){}
						if(typeof(localData)=='object'){
							for(var j=0;j<localData.length;j++){
								var cfg = localData[j];
								addNode(subRoot,cfg);
							}
						}
					}else{
						//从接口请求
						runtimeMgr.ajaxRequest(nodeCfg.dataUrl,function(data,options){
							localStorage.setItem(options.cacheKey,Ext.encode(data));
							if(data){
								for(var j=0;j<data.length;j++){
									var cfg = data[j];
									addNode(options.root,cfg);
								}
							}
						},{root:subRoot,cacheKey:nodeCfg.dataUrl});
					}
				}
			}else{
				if(nodeCfg.children){
					var subRoot = addSubRoot(root,nodeCfg);
					for(var i=0;i<nodeCfg.children.length;i++){
						addNode(subRoot,nodeCfg.children[i]);
					}
				}else{
					var n = runtimeMgr.createLeafTreeNode(nodeCfg);
					n.cfgData=nodeCfg;
					n.on('click',function(node,event){
						if(!runtimeMgr.isMenuEditState){
							this.openPage(node.attributes.url,node.attributes.blank,node.attributes.text);
						}/*else{
							var qtip = new Ext.QuickTip({
								title:'提示',
								autoHide:true,
								target:node.getUI().getEl(),
								html:'系统处于菜单配置状态,点击保存配置后功能才能重新起效。'
							});

							qtip.showBy(node.getUI().getEl());
							
						}*/
					},runtimeMgr);
					root.appendChild(n);
				}
			}
		}
		
		if(data.accordion){
			for(var i=data.accordion.length-1;i>=0;i--){
				var accordion = data.accordion[i];
				var cfg = {
					frame:true,
					collapsible:true,
					titleCollapse: true
				};
				Ext.apply(cfg,accordion);
				delete cfg.children;
				var newAccPanel = runtimeMgr.menusPanel.insert(0,cfg);
				var tree = new Ext.tree.TreePanel({
					containerScroll:true,
					rootVisible:false,
					useArrows:false,//用箭头还是加号
					border:true,
					lines :false,
					animate:true,
					enableDrop:false,
					enableDrag:true,
					ddGroup: 'TreePanelDDGroup',
					root:new Ext.tree.TreeNode({
						text: 'root',
						draggable:false,
						children:[]
					}),
					listeners:{
						"startdrag":function(tree,node,e){
							if(!runtimeMgr.isMenuEditState){
								//e.stopEvent();
								return false;
							}
						},
						"beforeload":function(node){
							node.attributes.loader.dataUrl =node.attributes.dataUrl; 
						}
					}
				});
				newAccPanel.add(tree);
				runtimeMgr.menusPanel.doLayout();
				if(runtimeMgr.isMenuEditState) tree.disable();//若处于菜单编辑状态则锁定
				
				if(accordion.dataUrl){
					//从缓存读取	
					var localData = localStorage.getItem(accordion.dataUrl);
					if(localData && !options.refresh){
						try{localData = Ext.decode(localData);}catch(ex){}
						if(typeof(localData)=='object'){
							for(var j=0;j<localData.length;j++){
								var cfg = localData[j];
								addNode(tree.root,cfg);
							}
						}
					}else{
						//从接口请求
						runtimeMgr.ajaxRequest(accordion.dataUrl,function(data,options){
							localStorage.setItem(options.cacheKey,Ext.encode(data));
							if(data){
								for(var j=0;j<data.length;j++){
									var cfg = data[j];
									addNode(options.tree.root,cfg);
								}
							}
						},{tree:tree,cacheKey:accordion.dataUrl});
					}
				}else{
					if(accordion.children){
						for(var j=0;j<accordion.children.length;j++){
							var cfg = accordion.children[j];
							addNode(tree.root,cfg);
						}
					}
				}
			}
			if(data.accordion.length>0) runtimeMgr.menusPanel.doLayout();
		}
		
	},
	//初始化本地菜单
	initLocalMenus:function(){
		try{
			var data = Ext.decode(localStorage.getItem(this.localMenuKey));
			if(data.topBar && data.topBar.children){
				for(var i=data.topBar.children.length-1;i>=0;i--){
					var tb = data.topBar.children[i];
					var cfg = {
						isCustom:true,
						cfgData:tb,
						scope:tb,
						handler:function(obj,event){
							if(!runtimeMgr.isMenuEditState){
								runtimeMgr.openPage(this.url,this.blank,this.text);
							}else{
								runtimeMgr.editTopMenu(obj,event,this);
							}				
						}
					};
					Ext.applyDeep(cfg,tb);
					var btn = new Ext.Toolbar.Button(cfg);
					runtimeMgr.topToolbar.insertButton(0,btn);
					
					btn.getEl().on('contextmenu', function(e) {  
						if(runtimeMgr.isMenuEditState){	
							e.preventDefault();       
							runtimeMgr.contextMenu_topBtn.btn = this;
							runtimeMgr.contextMenu_topBtn.show(this.getEl());
						}
					},btn);
				}
			}
			
			if(data.accordion && data.accordion.length){
				for(var i=0;i<data.accordion.length;i++){
					var accordion = data.accordion[i];
					var accCfg = {};
					Ext.apply(accCfg,accordion);
					delete accCfg.children;
					var newAccPanel = runtimeMgr.addAccMenu(accCfg);
					var root = newAccPanel.items.get(0).root;
					for(var j=0;accordion.children&&j<accordion.children.length;j++){
						var subMenu = accordion.children[j];
						var n = runtimeMgr.createNewSubMenu(subMenu);
						n.isCustom=true;
						delete subMenu.children;
						n.cfgData=subMenu;
						root.insertBefore(n,root.lastChild);
					}
					newAccPanel.doLayout();	
				}	
			}
			
			
		}catch(ex){
			console.info(ex);
		}
		
	},
	createLeafTreeNode:function(nodeCfg){
		var cfg = {
			leaf:true
		}
		Ext.apply(cfg,nodeCfg);
		var n = new Ext.tree.TreeNode(cfg);
		return n;
	},
	ajaxRequest:function(url,callback,options){
		if(url.indexOf('?')==-1){
			url+='?t=' + (new Date()).valueOf()
		}else{
			url+='&t=' + (new Date()).valueOf()
		}
		Ext.getBody().mask('正在初始化系统菜单...');
		Ext.Ajax.request({  
			url:url,
			method:"post",
			waitTitle : "请稍候",  
			options:{callback:callback,options:options},
			success:function(response,opts){
				Ext.getBody().unmask();
				var ret=null;
				try{
					ret = Ext.util.JSON.decode(response.responseText);
				}catch(ex){
					console.log(ex);
				}
				if(!ret){
					Ext.Msg.show({
					   title:"出错",
					   msg: '获取菜单配置出现异常',
					   buttons: Ext.Msg.OK,
					   animEl: 'elId',
					   minWidth:420,
					   icon: Ext.MessageBox.ERROR  
					});
				}else{
					if(typeof(opts.options.callback)=='function') opts.options.callback(ret,opts.options.options);
				}
			},
			failure:function(response,opts){
				Ext.getBody().unmask();
				Ext.Msg.show({
				   title:'错误提示',
				   msg: decodeURIComponent(response.statusText),
				   buttons: Ext.Msg.OK,
				   animEl: 'elId',
				   minWidth:420,
				   icon: Ext.MessageBox.ERROR  
				});
			}
		});
	},
	/***定制菜单功能 开始**/
	menuWinCfg:{
		title:"菜单配置"
		,closeAction:"hide"
		,callback:null
		,inputs:null
		,form:null
		,buttonAlign:'center'
		,setValues:function(values){
			var inputs=this.inputs||(this.inputs=this.body.dom.getElementsByTagName("input"));
			var len=inputs.length;
			while(len--){
				var ipt=inputs[len];
				if(ipt.type=="checkbox"){
					ipt.checked=values[ipt.name]?values[ipt.name]:false;
				}
				else
					ipt.value=values[ipt.name]?values[ipt.name]:'';			
			}		
		},
		confirm:function(){
			var f=this.form||(this.form=this.items.items[0]);
			if(!f.form.isValid())
				return;
			var inputs=this.inputs||(this.inputs=this.body.dom.getElementsByTagName("input"));
			var len=inputs.length;
			var values={};
			while(len--){
				var ipt=inputs[len];
				if(ipt.type=="checkbox")
					values[ipt.name]=ipt.checked;
				else
					values[ipt.name]=ipt.value;
			}
			this.callback(values);	
			this.hide();
		},
		items:{
			xtype:"form"
			,bodyStyle:"padding:2em"	
			,items:[{
				fieldLabel:"文字"
				,xtype:"textfield"
				,width:"300"
				,name:"text"
				,allowBlank:false
			},{
				fieldLabel:"链接"
				,xtype:"textfield"
				,name:"url"
				,width:"300"
				,allowBlank:false
			},{
				fieldLabel:"说明"
				,xtype:"textfield"
				,name:"tooltip"
				,width:"300"
			},{
				fieldLabel:"在新窗口打开"
				,name:"blank"
				,xtype:"checkbox"
			}]
		},
		buttons:[{
			text:"确定"
			,handler:function(){
				this.ownerCt.confirm();
			}
		},{
			text:"取消"
			,handler:function(){
				this.ownerCt.hide();
			}
		}]
	},
	showTopMenuWin:function(obj,event,values){
		if(!this.topMenuWin){
			this.topMenuWin = new Ext.Window(this.menuWinCfg);
		}
		var x = event.xy[0];
		x = Ext.getBody().getWidth()-477<x?Ext.getBody().getWidth()-477:x;
		this.topMenuWin.setPagePosition(x,event.xy[1]+25);
		this.topMenuWin.show(obj.el);
		this.topMenuWin.setValues(values?values:{});
		if(obj.id=='tbtnshowTopMenuWin'){
			this.topMenuWin.callback = function( values ){
				runtimeMgr.addTopMenu(values);
			}
		}else{
			this.topMenuWin.callback = (function(_target){
				return function(vals){
					runtimeMgr.updateTopMenu(vals,_target)
				};
			})(obj);
		}
	},
	addTopMenu:function(values){
		var cfg = {
			isCustom:true,
			cfgData:values,//用户自定义的
			scope:values,
			handler:function(obj,event){
				if(!runtimeMgr.isMenuEditState){
					runtimeMgr.openPage(this.url,this.blank,this.text);
				}else{
					runtimeMgr.editTopMenu(obj,event,this);
				}				
			}
		};
		Ext.applyDeep(cfg,values);
		var btn = new Ext.Toolbar.Button(cfg);
		//var insertPos = runtimeMgr.topToolbar.items.keys.indexOf('tbtnshowTopMenuWin');
		var insertPos = runtimeMgr.topToolbar.items.findIndex('id','tbtnshowTopMenuWin');
		if(insertPos==-1) insertPos=0;
		runtimeMgr.topToolbar.insertButton(insertPos,btn);
		
		btn.getEl().on('contextmenu', function(e) {  
			if(runtimeMgr.isMenuEditState){	
				e.preventDefault();       
				runtimeMgr.contextMenu_topBtn.btn = this;
				runtimeMgr.contextMenu_topBtn.show(this.getEl());
			}
		},btn);
	},
	updateTopMenu:function(values,target){
		target.setText(values.text);
		target.cfgData = values;
		target.setHandler(function(obj,event){
			if(!runtimeMgr.isMenuEditState){
				runtimeMgr.openPage(this.url,this.blank,this.text);
			}else{
				runtimeMgr.editTopMenu(obj,event,values);
			}
		},values);
	},
	editTopMenu:function(obj,event,values){
		runtimeMgr.showTopMenuWin(obj,event,values);
	},
	contextMenu_topBtn:new Ext.menu.Menu({    
		items: [{      
			text: '删除',      
			iconCls: 'extDelete', 
			handler:function(){
				var btn = runtimeMgr.contextMenu_topBtn.btn;
				var btnIndex = runtimeMgr.topToolbar.items.indexOf(btn);
				btn.getEl().parent().remove();  
				runtimeMgr.topToolbar.items.removeAt(btnIndex);
				btn.destroy();
			}
		}]  
	}),
	/***定制acc菜单功能 开始**/
	showAccMenuWin:function(obj,event,values){
		if(!this.accMenuWin){
			this.accMenuWin = new Ext.Window({
				title:"菜单栏配置"
				,width:500
				,closeAction:"hide"
				,callback:null
				,inputs:null
				,form:null
				,setValues:function(values){
					var inputs=this.inputs||(this.inputs=this.body.dom.getElementsByTagName("input"));
					var len=inputs.length;
					while(len--){
						var ipt=inputs[len];
						ipt.value=values[ipt.name]?values[ipt.name]:'';			
					}		
				}
				,confirm:function(){
					var f=this.form||(this.form=this.items.items[0]);
					if(!f.form.isValid())
						return;
					var inputs=this.inputs||(this.inputs=this.body.dom.getElementsByTagName("input"));
					var len=inputs.length;
					var values={};
					while(len--){
						var ipt=inputs[len];
						values[ipt.name]=ipt.value;
					}
					this.callback(values);	
					this.hide();
				}
				,items:{
					xtype:"form"
					,bodyStyle:"padding:2em"
					,items:[{
						fieldLabel:"名称"
						,xtype:"textfield"
						,width:"300"
						,name:"title"
						,allowBlank:false
					},{
						fieldLabel:"说明"
						,width:"300"
						,xtype:"textfield"
						,name:"tooltip"
					}]
				}
				,buttons:[{
						text:"确定"
						,handler:function(){
							this.ownerCt.confirm();
						}
					},{
						text:"取消"
						,handler:function(){
							this.ownerCt.hide();
						}
				}]
			});
		}
		var x = event.xy[0];
		var y = event.xy[1];
		x = Ext.getBody().getWidth()-477<x?Ext.getBody().getWidth()-477:x;
		y = Ext.getBody().getHeight()-172<y?Ext.getBody().getHeight()-172:y;
		this.accMenuWin.setPagePosition(x+50,y-50);
		this.accMenuWin.show(obj.el);
		this.accMenuWin.setValues(values?values:{});
		if(obj.id=='tbtnshowAccMenuWin'){
			this.accMenuWin.callback = function( values ){
				runtimeMgr.addAccMenu(values);
			}
		}else{
			this.accMenuWin.callback = (function(_target){
				return function(vals){
					runtimeMgr.updateAccMenu(vals,_target)
				};
			})(runtimeMgr.contextMenu_acc.panel);
		}
	},
	createNewAccPanel:function(values){
		var cfg = {
			isCustom:true,
			cfgData:values,//用户自定义的
			title:values.title,
			tooltip:values.tooltip,
			frame:true,
			collapsible:true,
			titleCollapse: true
		};
		var newAccPanel = new Ext.Panel(cfg);
		var tree = new Ext.tree.TreePanel({
			containerScroll:true,
			rootVisible:false,
			border:true,
			lines :false,
			animate:true,
			enableDrop:true,
			enableDrag:true,
			ddGroup: 'TreePanelDDGroup',
			root:new Ext.tree.TreeNode({
				text: 'root',
				draggable:false,
				children:[]
			}),
			listeners : {
				'contextmenu' :function(node,e){
					if(runtimeMgr.isMenuEditState){	
						e.preventDefault();
						node.select();
						try{
							if(node.isLeaf()){
								runtimeMgr.contextMenu_subMenu.node = node;
								runtimeMgr.contextMenu_subMenu.showAt(e.getXY());
							}else{
								//contextmenu.showAt(e.getXY());
							}
						}catch(ex){
						}
					}
				}
			}
		});
		newAccPanel.add(tree);	
		return newAccPanel;
	},
	addAccMenu:function(values){
		var newAccPanel = runtimeMgr.createNewAccPanel(values);
		var n = runtimeMgr.createLeafTreeNode({
			text:'添加子菜单',
			iconCls:'extAdd'
		});
		n.name = 'addSubMenuItem';//标识
		n.on('click',function(obj,event,options){
			runtimeMgr.contextMenu_acc.panel = this;
			runtimeMgr.showSubMenuWin(obj,event);
		},newAccPanel);
		newAccPanel.items.get(0).root.appendChild(n);
		runtimeMgr.menusPanel.add(newAccPanel);	
		runtimeMgr.menusPanel.doLayout();
		if(!runtimeMgr.isMenuEditState)n.getUI().hide();
		
		newAccPanel.header.on('contextmenu', function(e) {  
			if(runtimeMgr.isMenuEditState){	
				e.preventDefault();       
				runtimeMgr.contextMenu_acc.panel = this;
				runtimeMgr.contextMenu_acc.showAt(e.getXY());
			}
		},newAccPanel);		
		return newAccPanel;
	},
	updateAccMenu:function(values,target){
		target.setTitle(values.title);
		target.tooltip = values.tooltip;
		target.cfgData = values;
	},
	contextMenu_acc:new Ext.menu.Menu({    
		items: [{
			text:'添加子菜单',
			iconCls: 'extAdd', 
			id:'addSubMenuItem',
			handler:function(obj,event){
				runtimeMgr.showSubMenuWin(obj,event);
			}
		},'-',{
			text:'修改菜单栏名称',
			iconCls: 'extModify',
			handler:function(obj,event){
				var panel = runtimeMgr.contextMenu_acc.panel;
				runtimeMgr.showAccMenuWin(obj,event,{title:panel.title,tooltip:panel.tooltip});
			}			
		},{      
			text: '删除菜单栏',      
			iconCls: 'extDelete', 
			handler:function(){
				var panel = runtimeMgr.contextMenu_acc.panel;
				runtimeMgr.menusPanel.remove(panel);
			}
		}]  
	}),
	/***定制acc菜单功能 结束**/
	/***定制acc子菜单功能 开始**/
	showSubMenuWin:function(obj,event,values){
		var _this = runtimeMgr;
		if(!_this.subMenuWin){
			_this.subMenuWin = new Ext.Window(_this.menuWinCfg);
		}
		var x = event.xy[0];
		var y = event.xy[1];
		x = Ext.getBody().getWidth()-477<x?Ext.getBody().getWidth()-477:x;
		y = Ext.getBody().getHeight()-224<y?Ext.getBody().getHeight()-224:y;
		_this.subMenuWin.setPagePosition(x+50,y-25);
		_this.subMenuWin.show(obj.el);
		_this.subMenuWin.setValues(values?values:{});
		if(obj.id=='addSubMenuItem' || obj.name=='addSubMenuItem'){
			_this.subMenuWin.callback = function( values ){
				runtimeMgr.addSubMenu(values);
			}
		}else{
			_this.subMenuWin.callback = (function(_target){
				return function(vals){
					runtimeMgr.updateSubMenu(vals,_target)
				};
			})(obj);
		}
	},
	createNewSubMenu:function(values){
		var n = runtimeMgr.createLeafTreeNode(values);
		
		n.on('click',function(node,event){
			if(!runtimeMgr.isMenuEditState){
				runtimeMgr.openPage(node.attributes.url,node.attributes.blank,node.attributes.text);
			}else{
				runtimeMgr.editSubMenu(node,event,node.attributes);
			}
		},values);
		return n;
	},
	addSubMenu:function(values){
		var panel = runtimeMgr.contextMenu_acc.panel;
		var n = runtimeMgr.createNewSubMenu(values);
		n.cfgData = values;
		n.isCustom = true;
		var root = panel.items.get(0).root;
		root.insertBefore(n,root.lastChild);
		panel.doLayout();		
	},
	updateSubMenu:function(values,node){
		node.setText(values.text);
		node.cfgData = values;
		Ext.apply(node.attributes,values);
		
	},
	editSubMenu:function(obj,event,values){
		runtimeMgr.showSubMenuWin(obj,event,values);
	},
	contextMenu_subMenu:new Ext.menu.Menu({    
		items: [{
			text:'修改',
			iconCls: 'extModify',
			handler:function(obj,event){
				var node = runtimeMgr.contextMenu_subMenu.node;
				runtimeMgr.showSubMenuWin(node,event,node.attributes);
			}			
		},{      
			text: '删除',      
			iconCls: 'extDelete', 
			handler:function(){
				var node = runtimeMgr.contextMenu_subMenu.node;
				node.remove();
			}
		}]  
	}),
	/***定制acc子菜单功能 结束**/
	/***定制菜单功能 结束**/
	
	/***定制菜单保存 开始**/
	saveCustomMenu:function(){
		var topBarCfg = [];
		var accMenuCfg=[];
		
		var btns = this.topToolbar.items;
		for(var i=0;i<btns.length;i++){
			var btn = btns.get(i);
			if(btn.isCustom){
				topBarCfg.push(btn.cfgData)
			}
		}

		var menus = this.menusPanel.items;
		for(var i=0;i<menus.length;i++){
			var accPanel = menus.get(i);
			if(accPanel.isCustom){
				var cfgData = Ext.applyDeep({},accPanel.cfgData);
				var root = accPanel.items.get(0).root;
				for(var j=0;j<root.childNodes.length;j++){
					var nodeCfg = root.childNodes[j].cfgData;
					if(nodeCfg){
						if(!cfgData.children) {
							cfgData.children=[root.childNodes[j].cfgData];
						}else{	
							cfgData.children.push(root.childNodes[j].cfgData);
						}
					}
				}
				accMenuCfg.push(cfgData);
			}
		}
		
		var cfg = {
			topBar:{
				children:topBarCfg
			},
			accordion:accMenuCfg
		};
		if(localStorage) {
			localStorage.setItem(this.localMenuKey,Ext.encode(cfg));
		}else
			alert('该浏览器不支持自定义菜单的存储');
		
	},
	/***定制菜单保存 结束**/
	//锁定系统菜单
	lockSysMenu:function(lock){
		var btns = this.topToolbar.items;
		for(var i=0;i<btns.length;i++){
			var btn = btns.get(i);
			if(btn.isCustom || btn.id=='tbtnshowTopMenuWin'){
				break;
			}
			if(lock) btn.disable();
			else btn.enable();
		}

		var menus = this.menusPanel.items;
		for(var i=0;i<menus.length;i++){
			var accPanel = menus.get(i);
			var root = accPanel.items.get(0).root;
			if(accPanel.isCustom){
				if(root.lastChild){
					if(lock) root.lastChild.getUI().show();
					else root.lastChild.getUI().hide();//设置添加子菜单按钮显示状态
				}
			}else{
				if(lock) accPanel.items.get(0).disable();
				else accPanel.items.get(0).enable();
			}
		}
		
		if(lock) centerTabPanel.disable();
		else centerTabPanel.enable();
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
					runtimeMgr.lblStatus.setValue(this.src);
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
	
};

var notifyMgr = {
	localMsgKey:'CMPP_NOTIFY_LOCAL_DATA_' + nodeId__,
	notifyUrl:'message!get.jhtml',
	localMsgData:{},
	localMsgArr:[],
	remoteUnReadMsgData:[],
	unReadMsgArr:[],
	btnNotify:null,
	hasNewMsg:false,
	tpl:new Ext.XTemplate('\
		<p><h2>{title}</h2><p>\
		<p>{creator}</p>\
		<p>{content}</p>\
		<p>{datetime}</p>\
	'),
	init:function(){
		this.btnNotify = Ext.getCmp('btnNotify');
		this.btnNotify.on('click',this.btnNotifyHandler,this);
		
		this.initLocalMsg();
		this.setLocalMsgShow();//设置本地未读消息
		
		var mgr = new Ext.Updater("txtMsg");
		mgr.startAutoRefresh(60, 'message!list.jhtml?nodeId=' + nodeId__,null,this.handleNewNotify,true);

	},
	handleNewNotify:function(el,success,response,options){
		if(success){
			var ret = Ext.decode(response.responseText);
			var unreadCount = notifyMgr.getUnReadCount(ret);
			//获取详细通知,存储到本地
			if(notifyMgr.remoteUnReadMsgData.length>0) {
				notifyMgr.getRemoteNotifyDetail(notifyMgr.updateNotifyBtn);
			}
		}
	},
	updateNotifyBtn:function(remote){
		var unread=this.getLocalUnRead();
		this.unReadMsgArr = unread;
		if(unread.length>0){
			this.btnNotify.setText('(<font color="red">'+ unread.length +'</font>)');
			this.ShowBubble(unread.length);
			
			if(this.intervalId) clearInterval(this.intervalId);
			this.intervalId = setInterval(function(){
				var iconCls = notifyMgr.btnNotify.iconCls ;
				notifyMgr.btnNotify.setIconClass(iconCls=='extMail'?'extMail2':'extMail');
			},500);
			
		}else{		
			this.btnNotify.setText('(0)');
			if(this.intervalId) clearInterval(this.intervalId);
		}
	},
	//读取本地所有消息
	initLocalMsg:function(){
		var localMsg = localStorage.getItem(this.localMsgKey);
		if(localMsg){
			this.localMsgArr = Ext.decode(localMsg);
			for(var i = 0;i<this.localMsgArr.length;i++){
				var key = 'msg_' + nodeId__ + '_' + this.localMsgArr[i].id;	
				this.localMsgData[key] = this.localMsgArr[i];
			}
			
		}
	},
	getLocalUnRead:function(){
		var unRead = [];
		for(var i = 0;i<this.localMsgArr.length;i++){
			var key = 'msg_' + nodeId__ + '_' + this.localMsgArr[i].id;	
			var msg = this.localMsgData[key];
			if(!msg.read) unRead.push(msg);
		}
		return unRead;
	},
	setLocalMsgShow:function(){
		this.updateNotifyBtn();
	},
	getRemoteNotifyDetail:function(callback){
		var keys = [];
		for(var i=0;i<this.remoteUnReadMsgData.length;i++){
			var id = this.remoteUnReadMsgData[i];
			var key = 'msg_' + nodeId__ + '_' + id;	
			if(!this.localMsgData[key]){
				keys.push(id);
			}
		}
		if(keys.length==0) {
			callback.call(this);
			return;	
		}
		Ext.Ajax.request({  
			url:this.notifyUrl + '?ids=' + keys.join(',') + '&nodeId=' + nodeId__,
			method:"get",
			options:{callback:callback},
			scope:this,
			success:function(response,opts){
				try{
					var mustSave=false;
					var ret = Ext.util.JSON.decode(response.responseText);
					for(var i=ret.length-1;i>=0;i--){
						var item = ret[i];
						var key = 'msg_' + nodeId__ + '_' + item.id;
						if(!this.localMsgData[key]){
							this.localMsgData[key] = item;
							this.localMsgArr.push(item);
							mustSave=true;
						}
					}
					if(mustSave){	
						this.saveToLocal();	
					}
					opts.options.callback.call(this,'remote');
				}catch(ex){
					console.log(ex);
				}
			}
		});
		
	},
	getUnReadCount:function (msgData){
		var len=0;
		this.remoteUnReadMsgData=[];
		for(var i=0;i<msgData.length;i++){
			var key = 'msg_' + nodeId__ + '_' + msgData[i];
			var msg = this.localMsgData[key];
			if(!msg) {
				len++;
				this.remoteUnReadMsgData.push(msgData[i]);
			}
		}
		return len;
	},
	btnNotifyHandler:function(obj,e){
		var unread = this.unReadMsgArr;
		if(unread.length==0) return;
		var navHandler = function(direction){
			var wizard = this.card.layout;  
			var prev = Ext.getCmp('move-prev');  
			var next = Ext.getCmp('move-next');  
			var label = Ext.getCmp('move-label');  
			var activeId = wizard.activeItem.id;  
			var current = parseInt(activeId.split('-')[1]);
			current+=direction;
			
			prev.setDisabled(false); 
			next.setDisabled(false);
			if(current==0){
				prev.setDisabled(true); 
			}
			if(current==this.card.items.length-1){
				next.setDisabled(true);  
			}
			
			wizard.setActiveItem(current); 		
			label.setText((current+1) + '/' + this.card.items.length);
			
			var key = 'msg_' + nodeId__ + '_' + this.unReadMsgArr[this.unReadMsgArr.length-current-1].id;
			if(this.localMsgData[key] && !this.localMsgData[key].read){
				this.localMsgData[key].read = true;
			}
			
		};
		
		var itemsCfg=[];
		for(var i=unread.length-1;i>=0;i--){
			itemsCfg.push({
				id: 'card-' + (unread.length-i-1),
				autoScroll:true,
				html:notifyMgr.tpl.applyTemplate(unread[i])
			});
		}
		this.card = new Ext.Panel({
			layout:'card',
			activeItem: 0, 
			bodyStyle: 'padding:10px',
			defaults: {
				border:false
			},
			bbar: [
				{
					id: 'move-prev',
					text: '上一条',
					handler: navHandler.createDelegate(this, [-1]),
					disabled: true
				},'->',
				{
					xtype:'label',
					id:'move-label',
					text:'1/' + unread.length
				},
				' ',
				{
					id: 'move-next',
					text: '下一条',
					disabled: unread.length<=1,
					handler: navHandler.createDelegate(this, [1])
				}
			],
			items: itemsCfg
		});	
		if(!this.notifyWin || !this.notifyWin.isVisible()){
			this.notifyWin  = new Ext.Window({
				title:'消息盒',
				width:250,
				height:200,
				closeAction:'close',
				layout:'fit',
				items:[this.card],
				listeners:{
					scope:this,
					beforeclose :function(){
						var unread=this.getLocalUnRead();
						this.unReadMsgArr = unread;
						var count = unread.length;
						this.btnNotify.setText(count>0?'(<font color="red">'+ count +'</font>)':'(0)');
						this.saveToLocal();
						if(count==0) clearInterval(this.intervalId);
					},
					beforeshow :function(){
						if(this.bubble) this.bubble.hide(true);
					}
				}
			});
			this.notifyWin.setPosition(Ext.getBody().getWidth()-250,Ext.getBody().getHeight()-200-28);
			this.notifyWin.show(obj.getEl());
			
			var key = 'msg_' + nodeId__ + '_' + this.unReadMsgArr[this.unReadMsgArr.length-1].id;
			if(this.localMsgData[key] && !this.localMsgData[key].read){
				this.localMsgData[key].read = true;
			}
			
		}else{
			this.notifyWin.hide(obj.getEl());
			this.notifyWin.close();
		}
	},
	saveToLocal:function(){
		//控制数量
		while(this.localMsgArr.length>10){
			this.localMsgArr.shift();
		}
		for(var i = 0;i<this.localMsgArr.length;i++){
			var key = 'msg_' + nodeId__ + '_' + this.localMsgArr[i].id;
			 this.localMsgArr[i] = this.localMsgData[key];
		}
		
		localStorage.setItem(this.localMsgKey,Ext.encode(this.localMsgArr));	
	},
	ShowBubble:function(){
		if(!this.bubble ){
			this.bubble = Ext.getBody().createChild({
				tag:'div',
				cls:'example-right',
				style:'display:none;position:absolute;',
				html:'<p style="line-height:70px">您有新的消息</p><p><button class="blue-pill">现在就看</button><button class="blue-pill">以后再说</button></p>'
			});
			var btns = this.bubble.select('.blue-pill');
			Ext.fly(btns.elements[0]).on('click',function(obj,e){
				this.bubble.hide(true);
				this.btnNotifyHandler(this.btnNotify,e)
			},this);
			Ext.fly(btns.elements[1]).on('click',function(obj,e){
				this.bubble.hide(true);
			},this);
		}
		this.bubble.show(true);
		var xy = this.btnNotify.el.getXY();
		this.bubble.setLocation(xy[0]-this.bubble.getWidth()+40,xy[1]-this.bubble.getHeight()-40);
		
	}
	/*
	fadeIn:function(){
		var el = notifyMgr.btnNotify.el;
		if(!notifyMgr.hasNewMsg) return;
		el.fadeIn({ endOpacity: 1, duration: 2,callback:function(){
			notifyMgr.fadeOut();
		}});
	},
	fadeOut:function(){
		var el = notifyMgr.btnNotify.el;
		if(!notifyMgr.hasNewMsg) return;
		el.fadeOut({ endOpacity: 0, duration: 2,callback:function(){
			notifyMgr.fadeIn();
		}});
	}
	*/
}

</script>
<script>
Ext.onReady(function(){
	Ext.BLANK_IMAGE_URL = "../res/js/ext2/resources/images/default/s.gif";
	Ext.lOADING_IMAGE_URL= "../res/js/ext2/resources/images/default/grid/wait.gif"; //resources\images\default\grid
	Ext.QuickTips.init();
	Ext.form.Field.prototype.msgTarget = 'qtip';

	runtimeMgr.init();
	notifyMgr.init();
		
	//更新在线用户人数 //注册用户正处于使用状态
	userOnlineMgr.init('runtimeIndex_' + 'nodeId' + nodeId__);

});
</script>


<!--body inject region -->
${bodyInject!""}
	
</body>
</html>