 <!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title>列表</title>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />

	<link rel="stylesheet" type="text/css" href="./../res/js/ext2/resources/css/ext-all.css" />
	<link rel="stylesheet" type="text/css" href="./../res/js/ext2/resources/css/patch.css" />
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

		.x-toolbar .x-btn-menu-arrow-wrap .x-btn-center button {
			background: url("../res/js/ext2/resources/images/default/toolbar/btn-arrow.gif") no-repeat scroll 0 3px transparent;
			width: 12px;
			visibility:hidden;
		}
		.x-toolbar .x-btn-over .x-btn-menu-arrow-wrap .x-btn-center button {
			background-position: 0 -47px;
			visibility:visible;
		}	
		
		/***menu的样式***/
		.x-menu-list {
			background: none repeat scroll 0 0 transparent;
			border: 0 none;
			max-height:320px;overflow:auto;
		}
		
		/*******************************************/
		/******** css for Ext.ux.multiSelect *********/
		/********* by chengds 2012-05-02 **********/
		/*******************************************/
		checked{background-image:url(../res/js/ext2/resources/images/default/menu/checked.gif)}
		.unchecked{background-image:url(../res/js/ext2/resources/images/default/menu/unchecked.gif)}
		.ux-MultiSelect-icon { width:16px; height:16px; float:left; background-position: -1px -1px ! important; background-repeat:no-repeat ! important; }
		.ux-MultiSelect-icon-checked { background: transparent url(../res/js/ext2/resources/images/default/menu/checked.gif); }
		.ux-MultiSelect-icon-unchecked { background: transparent url(../res/js/ext2/resources/images/default/menu/unchecked.gif); } 
	</style>
 	<script type="text/javascript" src="../res/js/ext2/adapter/ext/ext-base.js"></script>
    <script type="text/javascript" src="../res/js/ext2/ext-all-debug.js?20120516"></script>
	<script type="text/javascript" src="../res/js/ext2/ext-lang-zh_CN.js"></script>
	<script type="text/javascript" src="../res/js/ext_base_extension.js?20120504"></script>
	<script type="text/javascript" src="../res/js/controls/Ext.ux.RadioGroup.js"></script>
	<script type="text/javascript" src="../res/js/controls/Ext.ux.DateFieldExtent.js"></script>
	<script type="text/javascript" src="../res/js/controls/Ext.ux.MultiSelect.js?20120516"></script>	
	<script type="text/javascript" src="../res/js/controls/Ext.ux.TreeComboBox.js"></script>	
	<script type="text/javascript" src="../res/js/CookiesHelper.js"></script>		
	<script type="text/javascript" src="xlist/list_rt_baseConfig.js?20120904"></script>	
<script type="text/javascript">
	var listConfig__= ${listConfig!""};
	
	var formId__ = #{formId!0};
	var listId__= #{listId!0};
	var nodeId__=#{nodeId!0};
	var viewId__ = listConfig__.viewId;
	
	var LPCFG = listConfig__;

	if(!LPCFG.searchSvr) LPCFG.searchSvr=[];
	LPCFG.pagesize = parseInt(LPCFG.pagesize);
	LPCFG.autoPagesize = LPCFG.autoPagesize==false?false:true;
	LPCFG.searchableFields.id = {"field":"id","f_type":"INT","viewInServiceSearch":true,"dataSource":"","title":"id","sortableSvr":-1,"html":"","viewInDbSearch":true,"ctrl":"","dataSourceType":"","sortableDb":-1};
	
	if(!LPCFG.buttons)LPCFG.buttons ={
		add:true,
		modify:true,
		"delete":true,
		ext:[]	
	};
	if(!LPCFG.menus)LPCFG.menus ={
		add:false,
		modify:true,
		"delete":true	
	};	
	if(!LPCFG.search)LPCFG.search =[];
	if(!LPCFG.columns)LPCFG.columns =[];
	if(!LPCFG.mustReturnFields) LPCFG.mustReturnFields=['id'];
		
	//通过url传入的参数
	var queryParams = {};
	var params__={};
	(function(){
		var params = Ext.parseQuery();
		params__ = params;
		for(var key in params){
			queryParams['query_' + key] = params[key];
		}
		if(!queryParams['query_viewId']){
			queryParams['query_viewId'] = viewId__;
		}
	})();


//预处理////
	var dbSearchableArr__=[];
	var svrSearchableArr__=[];
	var dbSortableArr__=[];
	var svrSortableArr__=[];
	for(var f in LPCFG.searchableFields){
		var item = LPCFG.searchableFields[f];
		if(item.viewInDbSearch==true) dbSearchableArr__.push(item);
		if(item.viewInServiceSearch==true) svrSearchableArr__.push(item);
		if(item.sortableDb==true) dbSortableArr__.push(item);
		if(item.sortableSvr==true) svrSortableArr__.push(item);
	}
	
</script>	

${headInject!""}
</head>
<body>	

<script type="text/javascript">

listMgr = {
	rowHeight:26,//列表页行高
	grid:null,
	column:null,
	mnuContext:null,//右键菜单
	dbFilter:[],
	svrFilter:{},
	seachItemsArr:[],
	seachSvrItemsArr:[],
	searchPanel:{},
	customBtnHandlerUrl:'xlist!runScript.jhtml',//自定义按钮脚本处理接口
	pagerBar:null,
	toolbarArr :null,
	enableRemenberSearch:false,
	localSettingSearchSetting:null,
	init:function(){
		this.initBase();
		
		//解析本地存储
		if(window.localStorage && this.enableRemenberSearch){
			var localSettingStr = localStorage.getItem(Cookies.get('cmpp_cn') + '_listPage_search_' + 'nodeId-' + nodeId__+ 'formId-' + formId__+ 'listId-' + listId__);
			if(localSettingStr!=''){
				try{
					this.localSettingSearchSetting = Ext.decode(localSettingStr);
				}catch(ex){
					if(console)console.error(ex);
				}
			}
		}
		
		var toolbarItemsCfgArr = [];
		var searchItems = [];
		
		if(this.localSettingSearchSetting){
			LPCFG.defaultSearch = this.localSettingSearchSetting.searchType;
		}
		var dbChecked = LPCFG.defaultSearch!='service';
		if(LPCFG.enableSearchDb!=false && dbSearchableArr__.length>0){//数据库搜索启用
			searchItems.push({
				text:'从数据库搜索',
				value:'db',
				checked:dbChecked
			});
			this.searchPanel["bak"] = this.searchPanel["db"] = this.createDbSearchPanel();
			
			if(LPCFG.enableSearchHistory!=false){
				searchItems.push({
					text:'从历史库搜索',
					value:'bak',
					checked:false
				});
			}
		}
		
		if(LPCFG.enableSearchSvr!=false && svrSearchableArr__.length>0){
			searchItems.push({
				text:'从搜索服务搜索',
				value:'service',
				checked:!dbChecked
			});
			this.searchPanel["svr"] = this.createSvrSearchPanel();
		}
		
		if(searchItems.length>1){
			var searchPanelTogle = {
				xtype:'cycle',
				id:'btnSearchPanelTogle',
				showText: true,
				prependText: '',
				value:(this.localSettingSearchSetting)?(this.localSettingSearchSetting.searchType):((LPCFG.defaultSearch)?LPCFG.defaultSearch:'db'),
				iconCls :'btnIconCls',
				style:'padding:0px 5px 0px 0px;',
				items: searchItems,
				changeHandler:function(btn, item){
					var togle = Ext.getCmp('btnSearchPanelTogle');
					if((item.value=='db' && togle.value=='bak') ||(togle.value=='db' && item.value=='bak')){
						togle.value = item.value;
						return;
					}
					togle.value = item.value;
					if(item.value=='db' || item.value=='bak'){
						listMgr.searchPanel.svr.el.fadeOut({
							useDisplay: true,
							callback:function(){
								listMgr.searchPanel.db.el.fadeIn({useDisplay: true}); 
							}
						}); 
					}else{
						listMgr.searchPanel.db.el.fadeOut({
							useDisplay: true,
							callback:function(){
								listMgr.searchPanel.svr.el.fadeIn({useDisplay: true}); 
							}
						}); 
					}
					if(listMgr.morePanelDb)listMgr.morePanelDb.getEl().setDisplayed(false);
					if(listMgr.morePanelSvr)listMgr.morePanelSvr.getEl().setDisplayed(false);
				},
				listeners :{
					render:function(obj){
						var btns = obj.el.query('button');
						if(btns.length>0)
							btns[0].style.width = '90px';
					}
				}


			};
			toolbarItemsCfgArr.push(searchPanelTogle);
		}
		
		if(this.searchPanel["db"]) toolbarItemsCfgArr.push(this.searchPanel["db"]);
		if(this.searchPanel["svr"])toolbarItemsCfgArr.push(this.searchPanel["svr"]);
	
		
		var viewPortItemsArr = [];
		if(toolbarItemsCfgArr.length>0){
			viewPortItemsArr.push({
				xtype:'toolbar',
				region:'north',
				id:'toolbarBox',
				style:'border:none;',
				//style:'padding:1px 5px 0px 5px;',
				autoHeight:true,
				//height:38,
				border:false,
				frame:false,
				items:toolbarItemsCfgArr
			});
		}
		viewPortItemsArr.push({
			xtype:'panel',
			region:'center',
			layout:'anchor',
			//style:'padding:1px 5px 0px 5px;',
			frame:false,
			header :false,
			id:'datagridPanel',
			items:[{
				xtype:'panel',
				anchor:'100%',
				autoHeight:true,
				frame:false,
				border:false,
				header :false,	
				layout:'anchor',	
				id:'placeholder'
			}],
			listeners:{
				resize:function(obj,adjWidth,adjHeight,rawWidth,rawHeight ) {
					if(listMgr.grid) listMgr.grid.setHeight(adjHeight);//确保表格自适应
					if(listMgr.datagridPanel) listMgr.setPagesize();
				}
			}
		});
		
		//主界面
		new Ext.Viewport({
			layout:"border",
			border:false,
			frame:false,
			items:viewPortItemsArr
		});
		
		this.datagridPanel = Ext.getCmp('datagridPanel');	
		
		this.initToolbar();
		this.initPageBar();
		this.initColumn();
		this.initMnuContext();//初始化右键菜单
		this.initGrid();
		this.setPagesize();	
		this.loadData();
		
		if(LPCFG.defaultSearch=="service"){
			this.searchPanel["svr"].el.setVisible(true);
			this.searchPanel["svr"].el.setDisplayed(true);	
			this.searchPanel["db"].el.setDisplayed(false);				
		}else{
			if(this.searchPanel["svr"] && this.searchPanel["db"]) this.searchPanel["svr"].el.setVisible(false);
		}
		this.listenerKeybord();//绑定键盘事件
	},
	initBase:function(){
		this.store=new Ext.data.Store({ 
			url : 'xlist!data.jhtml',
			reader : new Ext.data.JsonReader({
				autoLoad:true,
				root : "data",
				totalProperty : "totalCount",
				successProperty : 'success',
				fields: LPCFG.mustReturnFields
			}),
			remoteSort: false,
			successProperty : 'success',
			listeners: { 
				"loadexception":function(obj, options, response) {
					try{
						var ret = Ext.util.JSON.decode(response.responseText);
					}catch(ex){
						
					}					
					console.info('store loadexception, arguments:', arguments);
					if(ret) var msg = ret.message;
					Ext.Msg.show({
						   title:'错误提示',
						   msg:msg?msg:response.statusText,
						   buttons: Ext.Msg.OK,
						   animEl: 'elId',
						   minWidth:420,
						   icon: Ext.MessageBox.ERROR 
					});
				}
			}
		});
	
		try{
			if(params__.where) {
				this.dbFilter = Ext.decode(params__.where);
			}
			if(params__.q) {
				this.svrFilter = Ext.decode(params__.q);
			}
			if(params__.sort) {
				this.sort = Ext.decode(params__.sort);
			}
		}catch(ex){
			Ext.Msg.show({
			   title:'错误提示',
			   msg: "来自url的参数解析出错。请检查参数格式是否正确。",
			   buttons: Ext.Msg.OK,
			   animEl: 'elId',
			   minWidth:420,
			   icon: Ext.MessageBox.ERROR  
			});
		}
		
	},
	//根据grid高度计算pagesize
	setPagesize:function(){
		if(LPCFG.autoPagesize){
			this.pagesize = Math.floor((listMgr.grid.body.getHeight()-26)/listMgr.rowHeight);
		}else{
			this.pagesize =LPCFG.pagesize;
		}
		listMgr.pagerBar.store.baseParams.limit	= this.pagesize;
		listMgr.pagerBar.pageSize = this.pagesize;
	},
	//绑定键盘事件
	listenerKeybord:function(){
		if( this.searchPanel.db){
			for(var i=0;i<this.seachItemsArr.length;i++){
				if(this.seachItemsArr[i]){
					new Ext.KeyMap(this.seachItemsArr[i][2].el, {
						key: Ext.EventObject.ENTER,
						fn: function(){
							this.doSearch_db();
						},
						scope: this
					});
				}
			}
		}
		if( this.searchPanel.svr){
			for(var i=0;i<this.seachSvrItemsArr.length;i++){
				new Ext.KeyMap(this.seachSvrItemsArr[i][1].el, {
					key: Ext.EventObject.ENTER,
					fn: function(){
						this.doSearch_svr();
					},
					scope: this
				});
			}
		}		
	},
	createDbSearchPanel:function(){//创建DB搜索panel
		var panel= new Ext.Panel({
			layout:'table',
			id:'searchPanel_db',
			autoHeight:true,
			frame:true,
			border:false,
			autoScroll:true,
			items:this.initDbSearchPanel()
		});
		return panel;
	},
	createSearchItem:function(sItem,groupId){
		var lcsss =this.localSettingSearchSetting ;
		if(lcsss && 'service'!=lcsss.searchType){
			var searchSets = lcsss.searchSetting[groupId];
			Ext.apply(sItem,searchSets);
		}
		var btnText = sItem.title?sItem.title:'选择搜索项';
		var btnSelFld=new Ext.Toolbar.SplitButton({
			text:btnText,
			fieldName:sItem.field
		});
		var menuItems=[];
		for(var i=0;i<dbSearchableArr__.length;i++){
			var item = dbSearchableArr__[i];
			menuItems.push({
				text         : item.title,
				value        : item.field,
				checked      : sItem.field==item.field,
				group		 : 'sitem_' + groupId
			});
		}
		
		btnSelFld.menu = new Ext.menu.Menu({
			items :menuItems,
			listeners:{
				scope:{groupId:groupId},
				'itemclick':function(item,e){					
					var button = listMgr.seachItemsArr[this.groupId][0];
					var oldFieldName = button.fieldName;
					var btnOp=listMgr.seachItemsArr[this.groupId][1];
					button.setText(item.text);
					button.fieldName = item.value;
					
					//更新op和valueCtrl控件
					var oldOps = listMgr.getOpsByFType(oldFieldName);
					var newOps = listMgr.getOpsByFType(item.value);
					if(oldOps!=newOps){
						listMgr.changeOp(btnOp,{field:item.value},this.groupId);
					}
					
					var oldValueCtrl = listMgr.seachItemsArr[this.groupId][2];				
					var dbPanel = oldValueCtrl.ownerCt;
					var newValueCtrl = listMgr.createValueCtrl({field:item.value},groupId);

					oldValueCtrl.el.fadeOut({
						endOpacity: 0, 
						easing: 'easeOut',
						duration: .5,
						scope:this,
						callback:function(){
							dbPanel.remove(oldValueCtrl,true);	
							dbPanel.body.update('');					
							dbPanel.add(newValueCtrl);	
							listMgr.seachItemsArr[this.groupId][2] = newValueCtrl;					
							dbPanel.doLayout();
							//绑定回车事件									
							new Ext.KeyMap(newValueCtrl.el, {
								key: Ext.EventObject.ENTER,
								fn: function(){
									this.doSearch_db();
								},
								scope: listMgr
							});
		
						}
					});

				}
			}
		});	
		
		///初始操作符///
		var btnOp=this.createOp(sItem,groupId);
		
		/////初始化值控件////
		var valueCtrl=this.createValueCtrl(sItem,groupId);
		var valueCtrlContainer = new Ext.Panel({
			layout:'fit',
			autoHeight:true,
			style:'margin-right:5px;',
			items:[valueCtrl]
		})
		
		this.seachItemsArr[groupId] = [btnSelFld,btnOp,valueCtrl];
		return [btnSelFld,btnOp,valueCtrlContainer] ;
	},
	createValueCtrl:function(sItem,groupId){//创建值控件
		var fldCtrlItem = LPCFG.searchableFields[sItem.field];
		var ctrl =  sItem.field=='' || !fldCtrlItem.ctrl?'textfield':fldCtrlItem.ctrl;
		var ctrlClassName = controlType__[ctrl];
		var ctrlCfg = {};
		Ext.applyDeep(ctrlCfg,sValueControlsCfg__[ctrl]);
		if(fldCtrlItem && fldCtrlItem.width) ctrlCfg.width=fldCtrlItem.width;
		if(this.localSettingSearchSetting && this.localSettingSearchSetting.searchSetting[groupId] && this.localSettingSearchSetting.searchSetting[groupId].field==sItem.field){//设置本地配置值
			ctrlCfg.value = this.localSettingSearchSetting.searchSetting[groupId].value;
		}
		
		if(fldCtrlItem && fldCtrlItem.dataSource){
			if(fldCtrlItem.dataSourceType=='url'){
				if(ctrlClassName==="Ext.ux.TreeComboBox"){
					ctrlCfg.dataUrl = fldCtrlItem.dataSource;
				}else{
					ctrlCfg.mode="remote";
					ctrlCfg.store=new Ext.data.SimpleStore({　
						fields:['value','text'],
						url:fldCtrlItem.dataSource
					});
				}
			}else{//json sql
				var dataSource = fldCtrlItem.dataSource;
				try{
					if(dataSource!='') dataSource = Ext.decode(dataSource);
				}catch(ex){
					console.log(ex);
				}
				Ext.applyDeep(ctrlCfg,{dataSource:dataSource,data:dataSource});
			}
		}
		eval('var valueCtrl = new ' + ctrlClassName + '(ctrlCfg)');

		return valueCtrl;
	},
	createOp:function(sItem,groupId){//创建操作符控件
		var fldCtrlItem = LPCFG.searchableFields[sItem.field];
		var ctrl =  sItem.field=='' || !fldCtrlItem.ctrl?'textfield':fldCtrlItem.ctrl;
		var xtype = sValueControlsCfg__[ctrl].xtype;
		var opText = sItem.op?opEnZh__[sItem.op]:'=';
		var opValue = sItem.op?sItem.op:'=';
		
		if(this.localSettingSearchSetting && 'service'!=this.localSettingSearchSetting.searchType && this.localSettingSearchSetting.searchSetting[groupId]&&this.localSettingSearchSetting.searchSetting[groupId].field==sItem.field){//设置本地配置值
			opValue = this.localSettingSearchSetting.searchSetting[groupId].op;
		}
		
		if(xtype=='datefieldextent') {
			opText="从";
			opValue="from";
		}
		var btnOp= new Ext.Toolbar.SplitButton({
			text:opText,
			value:opValue
		});
		var ops = this.getOpsByFType(sItem.field);
		if(xtype=='datefieldextent'){
			ops=['from'];
		}
		
		var group ='createMBtnOp' + groupId;
		var opMenus=[];
		for(var i=0;i<ops.length;i++){
			var op = ops[i];
			opMenus.push({
				text         : opEnZh__[op]||'从',
				value        : op,
				checked      : i==0,
				group		 :group
			});
		}
		btnOp.menu = new Ext.menu.Menu({
			items :opMenus,
			listeners:{
				scope:{groupId:groupId},
				'itemclick':function(item,e){
					var button = listMgr.seachItemsArr[this.groupId][1];
					button.setText(item.text);
					button.value = item.value;
				}
			}
		});
		return btnOp;
	},
	getOpsByFType:function(field){
		var ops= ftype_op__.all;
		if(field){
			var ftype = LPCFG.searchableFields[field].f_type;
			if(ftype) ftype=ftype.toLowerCase();
			if(ftype && ftype_op__[ftype]) 
				ops = ftype_op__[ftype];
		}
		return ops;
	},
	changeOp:function(btnOp,sItem,groupId){
		var fldCtrlItem = LPCFG.searchableFields[sItem.field];
		var ctrl =  sItem.field=='' || !fldCtrlItem.ctrl?'textfield':fldCtrlItem.ctrl;
		var xtype = sValueControlsCfg__[ctrl].xtype;
		var ops = listMgr.getOpsByFType(sItem.field);
		if(xtype=='datefieldextent') {
			ops=['from'];
		}
		
		
		btnOp.menu.removeAll();
		var group ='createMBtnOp' + groupId;
		for(var i=0;i<ops.length;i++){
			var op = ops[i];
			var menuCfg = {
				text         : opEnZh__[op]||'从',
				value        : op,
				checked      : i==0,
				group		 :group
			};
			btnOp.menu.add(menuCfg);
		}
		
		
		if(this.localSettingSearchSetting && 'service'!=this.localSettingSearchSetting.searchType && this.localSettingSearchSetting.searchSetting[groupId]&&this.localSettingSearchSetting.searchSetting[groupId].field==sItem.field){//设置本地配置值
			btnOp.value = this.localSettingSearchSetting.searchSetting[groupId].op;
			btnOp.setText(opEnZh__[btnOp.value]||'从');
		}else{
			btnOp.value=ops[0]||'=';
			btnOp.setText(opEnZh__[ops[0]]||'从');
		}
		return btnOp;
	},
	moreSearchItem:function(sItemsMore){//创建更多搜索项
		var sItems = sItemsMore;
		return function(obj,e){		
			if(!listMgr.morePanelDb){
				var dbPanel = listMgr.searchPanel['db'];
				var position = dbPanel.getPosition();
				var size = dbPanel.getSize();
				//var right = Ext.getBody().getWidth()-(obj.el.getLeft()+ obj.el.getWidth() );	
				var left = position[0];
				var style = 'position:absolute;left:' + left + 'px;top:' + (position[1]+size.height) + 'px';
				
				var sItemsArr =[];
				for(var i=0;i<sItems.length;i++){
					var sItem = sItems[i];
					var sItemCtrls = listMgr.createSearchItem(sItem,listMgr.seachItemsArr.length);
					sItemsArr.push(sItemCtrls[0]);
					sItemsArr.push(sItemCtrls[1]);
					sItemsArr.push(sItemCtrls[2]);
				}
								
				//收起按钮
				var btnMin = new Ext.Button({
					text:'收起︽',//︽
					scope:obj,
					handler:function(){
						listMgr.morePanelDb.getEl().fadeOut({useDisplay:true});
					}
				});
				sItemsArr.push(btnMin);
				
				var panel = new Ext.Toolbar({
					autoHeight:true,
					border:false,
					frame:false,
					items:[{
						xtype:'panel',
						layout:'table',
						autoHeight:true,
						frame:true,
						border:false,
						autoScroll:true,
						style:style,
						items:sItemsArr
					}]
				});

				panel.render(Ext.getBody());
				panel.getEl().setVisible(true);
				panel.getEl().setDisplayed(false);
				panel.getEl().fadeIn({useDisplay:true});
				listMgr.morePanelDb = panel;
			}else{
				var panel = listMgr.morePanelDb;
				var el = panel.getEl();
				if(!el.isDisplayed()){
					el.fadeIn({	
						endOpacity: 1, 
						easing: 'easeIn',
						duration: .5,
						useDisplay:true
					});
				}else{
					el.fadeOut({useDisplay:true});
				}
			}
		}
	},	
	moreSearchItemSvr:function(sItemsMore){//创建更多搜索项-for-搜索服务
		var sItems = sItemsMore;
		return function(obj,e){		
			if(!listMgr.morePanelSvr){
				var dbPanel = listMgr.searchPanel['svr'];
				var position = dbPanel.getPosition();
				var size = dbPanel.getSize();
				//var right = Ext.getBody().getWidth()-(obj.el.getLeft()+ obj.el.getWidth() );	
				var left = position[0];
				var style = 'position:absolute;left:' + left + 'px;top:' + (position[1]+size.height) + 'px';
				var sItemsArr =[];
				
				for(var i=0;i<sItems.length;i++){
					var sItem = sItems[i];
					var sItemCtrls = listMgr.createSearchItemSvr(sItem,listMgr.seachSvrItemsArr.length);
					sItemsArr.push(sItemCtrls[0]);
					sItemsArr.push(sItemCtrls[1]);
				}
				
				//收起按钮
				var btnMin = new Ext.Button({
					text:'收起︽',//︽
					scope:obj,
					handler:function(){
						listMgr.morePanelSvr.getEl().fadeOut({useDisplay:true});
					}
				});
				sItemsArr.push(btnMin);
				
				var panel = new Ext.Toolbar({
					autoHeight:true,
					border:false,
					frame:false,
					items:[{
						xtype:'panel',
						layout:'table',
						autoHeight:true,
						frame:true,
						border:false,
						autoScroll:true,
						style:style,
						items:sItemsArr
					}]
				});

				panel.render(Ext.getBody());
				listMgr.morePanelSvr = panel;
			}else{
				var panel = listMgr.morePanelSvr;
				var el = panel.getEl();
				if(!el.isDisplayed()){
					el.fadeIn({	
						endOpacity: 1, 
						easing: 'easeIn',
						duration: .5,
						useDisplay:true
					});
				}
			}
		}
	},	
	initDbSearchPanel:function(){
		var sItems = [];
		var sItemsMore = [];
		for(var i=0;i<LPCFG.search.length;i++){
			var sItem = LPCFG.search[i];
			if(sItem.enable!=false){
				if(sItems.length/3>=3) {
					 sItemsMore.push(sItem);	
				}else{
					var sItemCtrls = this.createSearchItem(sItem,this.seachItemsArr.length);
					sItems.push(sItemCtrls[0]);
					sItems.push(sItemCtrls[1]);
					sItems.push(sItemCtrls[2]);
				}
			}
		}
		if(sItemsMore.length>0){
			var btnMore = new Ext.Button({
				text:'更多︾',//︽
				scope:this,
				handler:this.moreSearchItem(sItemsMore)
			});
			sItems.push(btnMore);
		}
		//////////////////搜索按钮///////////////////////
		if(dbSortableArr__.length>0){
			var mbtnSearch = new Ext.Toolbar.MenuButton({
				text     : '搜索',
				iconCls  :'searchBtn',
				value 	 :'',
				style:'padding:0px 0px 0px 5px;',
				handler:function(){
					listMgr.doSearch_db();
				}
				
			});
			
			var orderMenuItems=[];
			for(var i=0;i<dbSortableArr__.length;i++){  
				var fldCfg = {
					text         : '按<font color="green">' + dbSortableArr__[i].title + '</font>顺序查询',
					value        : {"field":dbSortableArr__[i].field,"order":"asc"},
					checked      : false,
					group		 :'mbtnSearch'
				};
				orderMenuItems.push(fldCfg);
				var fldCfg = {
					text         : '按<font color="green">' + dbSortableArr__[i].title + '</font>倒序查询',
					value        : {"field":dbSortableArr__[i].field,"order":"desc"},
					checked      : false,
					group		 :'mbtnSearch'
				};
				orderMenuItems.push(fldCfg);			
			}
			mbtnSearch.menu = new Ext.menu.Menu({
				items :orderMenuItems,
				listeners:{
					scope:this,
					'itemclick':function(item,e){
						this.doSearch_db(item.value);
					}	
				}
			});
			
			sItems.push(mbtnSearch);
		}else{
			sItems.push({
				xtype:'tbbutton',
				text:'搜索',
				iconCls  :'searchBtn',
				style:'padding:0px 0px 0px 5px;',
				handler:function(){
					listMgr.doSearch_db();
				}
			});	
		}
		//清空按钮
		sItems.push({
			xtype:'tbbutton',
			text:'清空',
			style:'padding:0px 5px 0px 5px;',
			scope:this,
			handler:function(){
				var items = this.seachItemsArr;
				for(var i=0;i<items.length;i++){
					if(!items[i]) continue;
					var vCtrl = items[i][2];
					vCtrl.setValue('');
				}
			}
		});
		return sItems;
	},
	createSvrSearchPanel:function(){//创建Svr搜索panel
		var panel= new Ext.Panel({
			layout:'table',
			id:'searchPanel_Svr',
			autoHeight:true,
			frame:true,
			border:false,
			autoScroll:true,
			items:this.initSvrSearchPanel()
		});
		return panel;
	},
	createSearchItemSvr:function(sItem,groupId){
		var lcsss =this.localSettingSearchSetting ;
		if(lcsss && 'service'==lcsss.searchType){
			var searchSets = lcsss.searchSetting[groupId];
			Ext.apply(sItem,searchSets);
		}
	
		var btnText = sItem.title?sItem.title + ":":'选择搜索项';
		var btnSelFld=new Ext.Toolbar.SplitButton({
			text:btnText,
			fieldName:sItem.field
		});
		
		var menuItems=[];
		for(var i=0;i<svrSearchableArr__.length;i++){
			var item = svrSearchableArr__[i];
			menuItems.push({
				text         : item.title,
				value        : item.field,
				checked      : sItem.field==item.field,
				group		 : 'sitem_svr_' + groupId
			});
		}
		btnSelFld.menu = new Ext.menu.Menu({
			items :menuItems,
			listeners:{
				scope:{groupId:groupId},
				'itemclick':function(item,e){					
					var button = listMgr.seachSvrItemsArr[this.groupId][0];
					var oldFieldName = button.fieldName;
					button.setText(item.text + ":");
					button.fieldName = item.value;
					
					var oldField = LPCFG.searchableFields[oldFieldName].field;
					var newField = LPCFG.searchableFields[item.value].field;
					if(newField!=oldField){
						var oldValueCtrl = listMgr.seachSvrItemsArr[this.groupId][1];
											
						var dbPanel = oldValueCtrl.ownerCt;
						var newValueCtrl = listMgr.createValueCtrl({field:item.value},groupId);
						oldValueCtrl.el.fadeOut({
							endOpacity: 0, 
							easing: 'easeOut',
							duration: .5,
							scope:this,
							callback:function(){
								dbPanel.remove(oldValueCtrl,true);	
								dbPanel.body.update('');									
								dbPanel.add(newValueCtrl);	
								listMgr.seachSvrItemsArr[this.groupId][1] = newValueCtrl;					
								dbPanel.doLayout();
								//绑定回车事件									
								new Ext.KeyMap(newValueCtrl.el, {
									key: Ext.EventObject.ENTER,
									fn: function(){
										this.doSearch_svr();
									},
									scope: listMgr
								});
							}
						});

					}
				}
			}
		});	
		
		/////初始化值控件////
		var valueCtrl=this.createValueCtrl(sItem,groupId);
		var valueCtrlContainer = new Ext.Panel({
			layout:'fit',
			autoHeight:true,
			autoWidth:true,
			style:'margin-right:10px;',
			items:[valueCtrl]
		});
		
		this.seachSvrItemsArr[groupId] = [btnSelFld,valueCtrl];
		return [btnSelFld,valueCtrlContainer] ;
	},
	
	//初始化服务搜索panel
	initSvrSearchPanel:function(){
		var sItems = [];
		var sItemsMore = [];
		for(var i=0;i<LPCFG.searchSvr.length;i++){
			var sItem = LPCFG.searchSvr[i];
			if(sItem.enable!=false){
				if(sItems.length/2>=3) {
					 sItemsMore.push(sItem);	
				}else{
					var sItemCtrls = this.createSearchItemSvr(sItem,this.seachSvrItemsArr.length);
					sItems.push(sItemCtrls[0]);
					sItems.push(sItemCtrls[1]);
				}
			}
		}
		if(sItemsMore.length>0){
			var btnMore = new Ext.Button({
				text:'更多︾',//︽
				scope:this,
				handler:this.moreSearchItemSvr(sItemsMore)
			});
			sItems.push(btnMore);
		}
		//////////////////搜索按钮///////////////////////
		if(svrSortableArr__.length>0){
			var mbtnSearch = new Ext.Toolbar.MenuButton({
				text     : '搜索',
				iconCls  :'searchBtn',
				value 	 :'',
				//tooltip	 :'',
				style:'padding:0px 5px 0px 5px;',
				menu     : [],
				//minWidth :110,
				handler:function(){
					listMgr.doSearch_svr();
				}
				
			});

			for(var i=0;i<svrSortableArr__.length;i++){  
				var fldCfg = {
					text         : '按<font color="green">' + svrSortableArr__[i].title + '</font>顺序查询',
					value        : {"field":svrSortableArr__[i].field,"order":"asc"},
					checked      : false,
					group		 :'mbtnSearchSvr',
					handler:function(obj,e){
						listMgr.doSearch_svr(this.value);
					}
				};
				mbtnSearch.menu.add(fldCfg);
				var fldCfg = {
					text         : '按<font color="green">' + svrSortableArr__[i].title + '</font>倒序查询',
					value        : {"field":svrSortableArr__[i].field,"order":"desc"},
					checked      : false,
					group		 :'mbtnSearchSvr',
					handler:function(obj,e){
						listMgr.doSearch_svr(this.value);
					}
				};
				mbtnSearch.menu.add(fldCfg);			
			}
			sItems.push(mbtnSearch);
		}else{
			sItems.push({
				xtype:'tbbutton',
				text:'搜索',
				iconCls  :'searchBtn',
				style:'padding:0px 5px 0px 5px;',
				handler:function(){
					listMgr.doSearch_svr();
				}
			});	
		}
		//清空按钮
		sItems.push({
			xtype:'tbbutton',
			text:'清空',
			style:'padding:0px 5px 0px 5px;',
			scope:this,
			handler:function(){
				var items = this.seachSvrItemsArr;
				for(var i=0;i<items.length;i++){
					if(!items[i]) continue;
					var vCtrl = items[i][1];
					vCtrl.setValue('');
				}
			}
		});
		
		return sItems;
	},	
	initToolbar:function(){
		//初始化按钮
		this.toolbarArr = [];
		if(LPCFG.buttons.add){
			this.toolbarArr.push({ 
				text : "增加", 
				iconCls : 'addField', 
				handler : this.addRecord 

			});
		}
		if(LPCFG.buttons.modify){
			this.toolbarArr.push({ 
				text : "修改", 
				iconCls : 'modifyField', 
				handler : this.updateRecord 
			});
		}
		if(LPCFG.buttons.delete){
			this.toolbarArr.push({ 
				text : "删除", 
				iconCls : 'delField', 
				handler : this.deleteRecord 
			});
		}	
		for(var i=0;i<LPCFG.buttons.ext.length;i++){
			var btnItem = LPCFG.buttons.ext[i];
			this.toolbarArr.push({ 
				text : btnItem.text, 
				iconCls : btnItem.iconCls, 
				id:'extBtn_' + btnItem.id,
				scope:btnItem,
				handler : this.getCustomBtnHandler(btnItem)
			});
		}

	},
	initPageBar:function(){
		this.pagerBar = new Ext.PagingToolbar({ 
			pageSize : this.pagesize, 
			store : this.store, 
			displayMsg : '显示第 {0} 条到 {1} 条记录,共 {2} 条记录',
			emptyMsg:"对不起，没有查询到数据", 
			firstText : "首页",
			prevText : "前一页",
			nextText : "下一页",
			lastText : "尾页",
			refreshText : "刷新",
			displayInfo : true 
		});
	},
	initMnuContext:function(){
		var menuItemsArr=[];
		if(LPCFG.menus.add){
			menuItemsArr.push({
				text:'添加',
				iconCls:'addField',
				handler:listMgr.addRecord
			});
		}
		if(LPCFG.menus.modify){
			menuItemsArr.push({
				text:'修改',
				iconCls:'modifyField',
				handler:listMgr.updateRecord
			});
		}	 
		if(LPCFG.menus.delete){
			menuItemsArr.push({
				text:'删除',
				iconCls:'delField',
				handler:listMgr.deleteRecord
			});
		}

		var menuItemsArr2=[];
		if(LPCFG.buttons.ext){
			for(var i=0;i<LPCFG.buttons.ext.length;i++){
				var btn= LPCFG.buttons.ext[i];
				if(btn.isMenuItem){
					menuItemsArr2.push({
						text:btn.text,
						iconCls:btn.iconCls,
						handler:listMgr.getCustomBtnHandler(btn)
					});
				}
			}
		}
		if(menuItemsArr.length>0 && menuItemsArr2.length>0){
			menuItemsArr.push('-');
		}
		menuItemsArr= menuItemsArr.concat(menuItemsArr2);
		if(menuItemsArr.length>0){	
			this.mnuContext = new Ext.menu.Menu({
				items: menuItemsArr
			});
		}
	},
	getCustomBtnHandler:function(btn){
		var myBtn = btn;
		//todo 执行客户端脚本
		var beforeJs;
		try{
			if(btn.js) beforeJs = new Function(btn.js);
		}catch(ex){
			console.info(ex);
		}
		return function(){
			if((beforeJs && beforeJs.call(listMgr)!=false) || !beforeJs){
				listMgr.customBtnHandler(myBtn);
			}
		}
	},
	initGrid:function(){
		var gridCfg = {
			renderTo:'placeholder',
			store: this.store,
			cm: this.column,
			trackMouseOver:true,
			anchor:'100%',
			stripeRows: true,//行交替颜色显示 斑马线效果
			sm: new Ext.grid.CheckboxSelectionModel(),
			loadMask:{ 
				msg:"数据正在加载中...." 
			}, 
			autoSizeColumns : true,
			autoScroll:true, 
			enableColumnHide:false,
			enableHdMenu: false,
			emptyText: "<div>无数据</div>", 
			autoExpandColumn:LPCFG.isWidthFreeRow!=-1?'columns-'+LPCFG.isWidthFreeRow:null,
			//autoExpandColumn:LPCFG.isWidthFreeRow!=-1?LPCFG.isWidthFreeRow:null,
			height:this.datagridPanel.body.getHeight(),
			plugins:Ext.grid.plugins.AutoResize?new Ext.grid.plugins.AutoResize():null,
			viewConfig: {
				emptyText: "<div>无数据</div>", 
				enableRowBody:true,//可以用两行tr来表示一行数据  
				showPreview:true,//初始显示预览效果,这个是自定义的属性  
				getRowClass : function(record, rowIndex, p, store){//CSS class name to add to the row.获得一行的css样式  
					if(this.showPreview){  
						return 'x-grid3-row-expanded';  
					}  
					return 'x-grid3-row-collapsed';  
				}  
			},
			view: new Ext.grid.GridView({  
                forceFit:false,  
            }), 
			iconCls:'icon-grid',
			frame:false,
			bbar : listMgr.pagerBar, 
			listeners　:　{
	　　　　	'render'　:　function(grid)　{
					//listMgr.pagerBar.render(grid.bbar);
					grid.body.setStyle({width:'100%',height:'100%'});
				},
				"rowcontextmenu":function(grid, rowIndex, event){
					/*右键菜单暂时不启用
					 event.stopEvent();
					 var selModel = grid.getSelectionModel();
					 if(!selModel.isSelected(rowIndex)){
						selModel.selectRow(rowIndex);
					 }
					 listMgr.mnuContext.showAt(event.xy);
					 */
				}
			}
		};
		if(this.toolbarArr.length>0){
			gridCfg.tbar =this.toolbarArr;
		}
		
		var grid = new Ext.grid.GridPanel(gridCfg); 
		this.grid = grid;
		grid.render();
	},
	getSearchFrom:function(){
		var from="db";
		if(!this.searchPanel["db"] && this.searchPanel["svr"] ){
			from="service";
		}
		// 如果存在q参数，则从搜索服务搜索
		if(params__.q){
			from = "service";
		}
		var togle = Ext.getCmp('btnSearchPanelTogle');
		if(togle) from = togle.value;	
		return from;
	},
	loadData:function(){
		var from = this.getSearchFrom();
		//设置url传递的filter
		var sort;
		var where = [];
		for(var i=0;i<this.dbFilter.length;i++){
			var item = this.dbFilter[i];
			if(item.field!="" && item.value!=""){
				where.push({
					field:item.field,op:item.op,value:item.value,andor:item.andor
				})
			}
		}
		//init sort
		sort = this.sort;
		if(!sort || sort.length==0){//启动默认排序
			sort=[];
			if(LPCFG.defaultSort){
				sort.push(LPCFG.defaultSort);
			}else{
				sort.push({"field":"id","order":"desc"});
			}
		}
		this.sort = sort;
		this.where = where.slice(0);
		
		var svrFilter={};
		if(typeof LPCFG.filter4Svr == "string"){
			try{
				svrFilter = Ext.decode(LPCFG.filter4Svr)
			}catch(ex){
				console.log(ex);
			}
		}else{
			svrFilter = LPCFG.filter4Svr;
		}
		if(this.svrFilter){
			Ext.applyIf(this.svrFilter,svrFilter);
		}
		var qArr = [];
		for(var field in this.svrFilter){
			var value = this.svrFilter[field];
			qArr.push(escape(value) + ':' + field);
		}
		if(qArr.length>0) this.q = qArr.join(',');
		
		/*
		this.store.baseParams.from = from;
		this.store.baseParams.formId =formId__;
		this.store.baseParams.listId =listId__;
		this.store.baseParams.limit = this.pagesize;
		this.store.baseParams.sort = Ext.encode(this.sort);
		if(where.length>0) this.store.baseParams.where = Ext.encode(where);	
		if(qArr.length>0) this.store.baseParams.q = this.q ;
		
		this.store.load({
			params:{start:0,limit:this.pagesize},
			callback:listMgr.storeLoadCallback
		});//初次搜索,默认从DB搜索
		this.grid.getSelectionModel().selectFirstRow();
		*/
		
		
		if('service'==from){
			this.doSearch_svr();
		}else{	
			this.doSearch_db();
		}
		
	},
	initColumn:function(){
		var colModelArr = [new Ext.grid.RowNumberer(),new Ext.grid.CheckboxSelectionModel({width :25,singleSelect:false})]; //new Ext.grid.RowNumberer(),//序号
		for(var i=0;i<LPCFG.columns.length;i++){
			var col = LPCFG.columns[i];
			if(col.isView!=false){
				var o = {
					id:'columns-' + i,
					header : col.title, 
					sortable : true, 
					dataIndex : col.field?col.field:'',
					align:col.align,
					renderer:renderField(col)
				};
				if(col.width)o.width = col.width+8;
				colModelArr[colModelArr.length]=o;
			}
			
		}
		var column = new Ext.grid.ColumnModel(colModelArr);
		this.column = column;
	},
	/*添加 修改 删除*/
	addRecord:function(){
		setActiveTab('../runtime/xform!render.jhtml?viewId='+ viewId__ +'&formId=' + formId__ + '&id=0&nodeId=' + nodeId__,'list_add_' + formId__ + '_' + viewId__,'添加');
	},
	updateRecord:function(){
		var selItems = listMgr.grid.getSelectionModel().selections.items;
		if(selItems.length==1){
			setActiveTab('../runtime/xform!render.jhtml?viewId='+ viewId__ +'&formId=' + formId__ + '&id=' + selItems[0].data.id + '&nodeId=' + nodeId__,'list_add_' + formId__ + '_' + viewId__ + '_' + selItems[0].data.id,'修改');
		}else{
			Ext.Msg.show({
			   title:'操作提示',
			   msg: "请选择一条记录",
			   buttons: Ext.Msg.OK,
			   animEl: 'elId',
			   minWidth:420,
			   icon: Ext.MessageBox.INFO  
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
							url:'xlist!delete.jhtml?nodeId=' + nodeId__ + '&viewId='+ viewId__ +'&formId=' + formId__ + '&ids=' + ids.join(','), 
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
									listMgr.doSearch();
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
								   msg: response.statusText,
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
			Ext.Msg.show({
			   title:'操作提示',
			   msg: "请选择记录",
			   buttons: Ext.Msg.OK,
			   animEl: 'elId',
			   minWidth:420,
			   icon: Ext.MessageBox.INFO  
			});
		}
	},
	//自定义按钮发起的请求
	customBtnHandler:function(btn){
		var btnId=btn.id;
		var selItems = listMgr.grid.getSelectionModel().selections.items;
		//if(selItems.length>0){
			var ids=[];
			for(var i=0;i<selItems.length;i++){
				ids.push(selItems[i].data.id);
			}
			Ext.getBody().mask("正在处理中，请稍候...");
			Ext.Ajax.request({  
				url:listMgr.customBtnHandlerUrl,
				method:"post",
				params:{nodeId:nodeId__,formId:formId__,listId:listId__,ids:ids.join(','),scriptKey:btnId},
				waitTitle : "请稍候",  
				waitMsg : "正在处理中，请稍候......",  
				scope:btn,
				success:function(response,opts){
					Ext.getBody().unmask();
					var ret={success:false};
					try{
						ret = Ext.util.JSON.decode(response.responseText);
					}catch(ex){
						console.log(ex);
					}
					if(ret.success){
						Ext.Toast.show(ret.message?ret.message:'成功',{
							title:'提示',
							buttons: Ext.Msg.OK,
							animEl: 'elId',
							icon: Ext.MessageBox.INFO,  
							time:1000,
							minWidth:420
						});
						
						//提交后脚本
						try{
							if(this.afterjs) {
								var afterjs = eval('0,'+this.afterjs);
								afterjs.call(listMgr,ret);
							}	
						}catch(ex){
							console.info(ex);
						}
						if(ret.refresh){	//需要刷新列表
							listMgr.doSearch();
						}
					}else{
						Ext.Msg.show({
						   title:"出错",
						   msg: ret.errorMessage?decodeURIComponent(ret.errorMessage):'出现异常',
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
					   msg: decodeURIComponet(response.responseText),
					   buttons: Ext.Msg.OK,
					   animEl: 'elId',
					   minWidth:420,
					   icon: Ext.MessageBox.ERROR  
					});
				}
			});
		
		//}else{
			/*
			Ext.Msg.show({
			   title:'操作提示',
			   msg: "请选择记录",
			   buttons: Ext.Msg.OK,
			   animEl: 'elId',
			   minWidth:420,
			   icon: Ext.MessageBox.INFO  
			});
			*/
		//}
		
	},
	//搜索
	doSearch_db:function (sortCfg){
		//搜集搜索条件,以json格式post到服务端解析
		var filter={where:[],sort:[]};
		
		var localSettingWhere=[];
		
		for(var i=0;i<this.seachItemsArr.length;i++){
			localSettingWhere[i]=0;
			var item = this.seachItemsArr[i];
			if(!item) continue;
			var qText = item[2].getValue()+'';
			var field = item[0].fieldName;
			var text = item[0].text;
			var xtype =item[2].xtype; 
			var op = item[1].value?item[1].value:'=';
			if(qText!=="" && field!==""){
				if(window.localStorage && this.enableRemenberSearch){//本地存储
					localSettingWhere[i]={
						title:text,
						field:field,
						op:op,
						value:qText
					};
				}
				
				if(xtype=="datefieldextent"){//日期区间控件
					var dates = qText.split(',');
					filter.where.push({
						field:field,
						op:'>=',
						value:dates.length>0?dates[0]:qText,
						andor:'and'
					});
					if(dates.length>1){
						filter.where.push({
							field:field,
							op:'<=',
							value:dates[1] + " 23:59:59",
							andor:'and'
						});
					}
				}else{
					var separator = sValueControlsCfg__.multiselect.separator;
					if(qText.indexOf(separator)!=-1){//关键词里含有,字符表示需要分割成多个或条件查询
						var qArr = qText.split(separator);
						for(var j=0;j<qArr.length;j++){
							if(qArr[j]!=""){
								filter.where.push({
									field:field,
									op:op,
									value:qArr[j],
									andor:'or'
								});	
							}
						}
					}else{
						filter.where.push({
							field:field,
							op:op,
							value:qText,
							andor:'and'
						});	
					}
				}
			}
		}
		var from = this.getSearchFrom();
		from = from?from:'db';
		//本地存储用户输入的搜索条件
		
		if(window.localStorage && this.enableRemenberSearch){
			localStorage.setItem(Cookies.get('cmpp_cn') + '_listPage_search_' + 'nodeId-' + nodeId__+ 'formId-' + formId__+ 'listId-' + listId__,Ext.encode({searchType:from,searchSetting:localSettingWhere}))
		};
		
		filter.where = filter.where.concat(this.where );
		if(this.sort){
			filter.sort = this.sort.slice(0);
		}
		if(sortCfg){
			filter.sort.unshift({field:sortCfg.field,order:sortCfg.order});
		}
		
		listMgr.store.baseParams ={
			from:from,
			where:Ext.util.JSON.encode(filter.where),
			sort:Ext.util.JSON.encode(filter.sort),
			formId:formId__,
			listId:listId__
		};
		listMgr.store.load({
			params:{
				start:0, 
				limit:listMgr.pagesize
			},
			callback:listMgr.storeLoadCallback
			
		});
	},
	doSearch_svr:function (sortCfg){
		//搜集搜索条件,以json格式post到服务端解析
		var q=[];
		var localSettingWhere=[];
		for(var i=0;i<this.seachSvrItemsArr.length;i++){
			localSettingWhere[i]=0;
			var item = this.seachSvrItemsArr[i];
			var qText = item[1].getValue();
			var field = item[0].fieldName;
			var text = item[0].text.replace(/:/g,'');

			if(qText!=="" && field!==""){
				if(item[1].xtype=="datefieldextent"){//日期区间控件
					var dateExtent = qText.split(',');
					qText="<[" + dateExtent[0] + "T00:00:00Z TO " + dateExtent[1] + "T23:59:59Z]>"; 
				}else{
					//转义qText里的特殊字符(搜索引擎需要)
					//特殊符号 \ + - && || ! ( ) { } [ ] ^ ” ~ * ? : 
					var speReg = [/\\/g,/\+/g,/-/g,/&&/g,/\|\|/g,/!/g,/\(/g,/\)/g,/\{/g,/\}/g,/\[/g,/\]/g,/\^/g,/~/g,/\*/g,/\?/g,/:/g];
					var speChar = ['\\','+','-','&&','||','!','(',')','{','}','[',']','^','~','*','?',':'];
					for(var c=0;c<speChar.length;c++){
						qText = qText.replace(speReg[c],'\\' + speChar[c]);
					}
					//对*和-的处理：当搜索关键词中*和-用<>包起来时，* 和 -将作为搜索引擎的运算符
					var exceptionReg = [/<\\\*>/g,/<\\\->/g];
					var exceptionChar = ['*','-'];
					for(var c=0;c<exceptionReg.length;c++){
						qText = qText.replace(exceptionReg[c],exceptionChar[c]);
					}
				}
				qText = escape(qText);//服务端需要反编码
				q.push(qText + ':' + field);	
				if(window.localStorage){//本地存储
					localSettingWhere[i]={
						title:text,
						field:field,
						value:qText
					};
				}
			}
		}
		//本地存储用户输入的搜索条件
		/*
		if(window.localStorage){
			localStorage.setItem(Cookies.get('cmpp_cn') + '_listPage_search_' + 'nodeId-' + nodeId__+ 'formId-' + formId__+ 'listId-' + listId__,Ext.encode({searchType:'service',searchSetting:localSettingWhere}))
		};
		*/
		q = q.concat(this.q);
		var sort=[];
		
		if(sortCfg){
			sort.push({field:sortCfg.field,order:sortCfg.order});
		}
		if(this.sort && this.sort.length>0){
			sort = this.sort;
		}else{
			sort.push({field:'id',order:'desc'});
		}
		
		//http://localhost/search/do.action?type=select&start=0&len=10&fl=id,name&q=10:id,1280335088000-1280421494000:time,ifeng*:name&sort=id asc,date desc&wt=xml
		listMgr.store.baseParams ={
			from:'service',
			fd:LPCFG.mustReturnFields.join(','),//需要输出的字段
			q:q.join(','),
			sort:Ext.encode(sort),
			formId:formId__,
			listId:listId__
		};
		listMgr.store.load({
			params:{
				start:0, 
				limit:listMgr.pagesize
			},
			callback:listMgr.storeLoadCallback
		});
		
	},
	//查询结果处理
	storeLoadCallback:function(r,options,success){
		if(success && r.length==0){
			var gridbody = listMgr.grid.body.child('.x-grid3-body');
			gridbody.setStyle("height","100%");
			gridbody.createChild({
				tag:'div',
				style:'width:100%;height:100%;background:url(../res/img/nodata.gif) no-repeat center center;'
			});
		}
		
	},
	//被外部调用的搜索
	doSearch:function(baseParams){
		if(!baseParams){//当前页刷新
			listMgr.store.load({
				params:{
					start:listMgr.pagerBar.cursor, 
					limit:listMgr.pagesize
				},
				callback:listMgr.storeLoadCallback
			});
		}else{//按新条件重新查询
			listMgr.store.baseParams ={
				from:'db',
				sort:Ext.util.JSON.encode([{field:'id',order:'desc'}]),
				formId:formId__,
				listId:listId__
			};
			var where = baseParams.where;
			if(typeof(where)=='string'){
				where = Ext.decode(where);
				where = where.concat(listMgr.where); 
				baseParams.where = Ext.encode(where);
			}
			if(typeof(where)=='object'){
				where = where.concat(listMgr.where); 
				baseParams.where = Ext.encode(where);
			}
			
			var q = baseParams.q;
			if(typeof(q)=='string'){
				q = Ext.decode(q);
				if(listMgr.q) q = q.concat(listMgr.q); 
				baseParams.q = q.join(',');
			}
			if(typeof(q)=='object'){
				if(listMgr.q) q = q.concat(listMgr.q); 
				baseParams.q =  q.join(',');
			}			
			Ext.apply(listMgr.store.baseParams,baseParams);
			listMgr.store.load({
				params:{
					start:0, 
					limit:listMgr.pagesize
				},
				callback:listMgr.storeLoadCallback
				
			});
		}
	}
		

}
/*
*共有方法
*二次开发帮助类
*用法:
helper.search({
	from:'db',
	where:	[{"field":"status","op":"<>","value":1,"andor":"and"}]
});
*/
helper = {
	search:function(baseParams){
		listMgr.doSearch(baseParams);
	},
	doSearch:function(baseParams){
		listMgr.doSearch(baseParams);
	}
}
</script>	
	
<script type="text/javascript">
Ext.onReady(function(){
	Ext.BLANK_IMAGE_URL = "../res/js/ext2/resources/images/default/s.gif";
	Ext.QuickTips.init();
	Ext.form.Field.prototype.msgTarget = 'qtip';
	
	listMgr.init();	

	///////////注入的JS/////////////	
	${extOnReadyJs!""}	
	
});
</script>		
${bodyInject!""}	
</body>
</html>	
