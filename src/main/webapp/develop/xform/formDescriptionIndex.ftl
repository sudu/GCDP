 <!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <title>CMPP表单数据接口说明</title>
	<link rel="shortcut icon" href="../res/img/favicon.ico" />
	<link rel="stylesheet" type="text/css" href="../res/js/ext2/resources/css/ext-all.css" />
	<link rel="stylesheet" type="text/css" href="../res/js/ext2/resources/css/patch.css" />
	<script type="text/javascript" src="../res/js/md5.js"></script>
 	<script type="text/javascript" src="../res/js/ext2/adapter/ext/ext-base.js"></script>
    <script type="text/javascript" src="../res/js/ext2/ext-all-debug.js"></script>	
	<script type="text/javascript" src="../res/js/ext2/ext-lang-zh_CN.js"></script>
	<script type="text/javascript" src="../res/js/ext_base_extension.js"></script>
	<script>
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
var nodeId__= #{nodeId!0};
allTabs={};
devMgr = {
	topToolbar:null,
	xformPanel:null,
	init:function(){
		this.initUI();	
		centerTabPanel = Ext.getCmp('centerTabPanel');
		centerTabPanel.body.setStyle({overflow:'hidden'});
		this.xformPanel = Ext.getCmp('xformPanel');
		this.initTabPanelEvent();
		this.initXFormPanel();
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
					text:'表单数据接口帮助'
				}]
			},{
				xtype: 'panel',
				title: '表单列表',
				id:'xformPanel',
				region: 'west',	
				split:true,
				collapsible: true,
				//collapseMode: 'mini',
				autoScroll:true,
				width:200,
				minWidth: 150,
				border: true,
				frame:false,
				style:'overflow-x:hidden;'
				//baseCls:'x-plain',
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
					title:"接口文档",
					html:'<iframe width="100%" height="100%" border="0" frameborder="0" scrolling="yes"  src="../doc/index.html?nodeId='+ nodeId__ +'"></iframe>'
				}],
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
						this.openPage(atrr.url?atrr.url:'../develop/xform!formDescription.jhtml?nodeId='+nodeId__+'&formId=' + atrr.formId,atrr.blank,atrr.text);
					},devMgr);
					return n;
				}
				
				var tree = new Ext.tree.TreePanel({
					containerScroll:true,
					rootVisible:false,
					useArrows:false,//用箭头还是加号
					border:false,
					lines :false,
					animate:true,
					frame:false,
					root:new Ext.tree.TreeNode({
						text: 'root',
						draggable:false,
						children:[]
					})
				});
				
				for(var i=ret.length-1;i>=0;i--){
					var item = ret[i];
					var n = createNode({
						text:item.title+'(' + item.tableName + ')',
						formId:item.id
					});
					tree.root.appendChild(n);
				}
				this.xformPanel.add(tree);
				this.xformPanel.doLayout();
			}
		});	
		
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
};

</script>

<script>
Ext.onReady(function(){
	Ext.BLANK_IMAGE_URL = "../res/js/ext2/resources/images/default/s.gif";
	Ext.lOADING_IMAGE_URL= "../res/js/ext2/resources/images/default/grid/wait.gif"; //resources\images\default\grid
	Ext.QuickTips.init();
	Ext.form.Field.prototype.msgTarget = 'qtip';

	devMgr.init();
});
</script>
	
</body>
</html>