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
	<script type="text/javascript" src="../res/js/ext_base_extension.js"></script>
	<script type="text/javascript" src="../res/js/controls/Ext.ux.RadioGroup.js"></script>
	<script type="text/javascript" src="../res/js/controls/Ext.ux.DateFieldExtent.js"></script>
	<script type="text/javascript" src="../res/js/controls/Ext.ux.MultiSelect.js?20120516"></script>	
	<script type="text/javascript" src="../res/js/controls/Ext.ux.TreeComboBox.js"></script>
	<script type="text/javascript" src="../res/js/CookiesHelper.js"></script>		
	<script type="text/javascript" src="xlist/list_rt_baseConfig.js?20120516"></script>	
	<script>
		/*
		*grid宽度自适应插件
		*/
		Ext.ns("Ext.grid.plugins");

		Ext.grid.plugins.AutoResize = Ext.extend(Ext.util.Observable,{
			init:function(grid){
				grid.applyToMarkup = function(el){
					grid.render(el);
				}
				var containerId = Ext.get(grid.renderTo || grid.applyTo).id;
				if(Ext.isIE){
					Ext.get(containerId).on("resize",function(){
						grid.setWidth.defer(100,grid,[Ext.get(containerId).getWidth()]);
					});
				}else{
					window.onresize = function(){
						grid.setWidth(Ext.get(containerId).getWidth());
					}
				}
			}
		});
	</script>	
<script type="text/javascript">
	var listConfig__= ${listConfig!""};
	
	var formId__ = #{formId!0};
	var listId__= #{listId!0};
	var nodeId__=#{nodeId!0};
	
	var LPCFG = listConfig__;

	if(!LPCFG.searchSvr) LPCFG.searchSvr=[];
	LPCFG.pagesize = parseInt(LPCFG.pagesize);
	LPCFG.autoPagesize = LPCFG.autoPagesize==false?false:true;
	
	if(!LPCFG.buttons)LPCFG.buttons ={
		ext:[]	
	};
	if(!LPCFG.columns)LPCFG.columns =[];
	if(!LPCFG.search)LPCFG.search =[];
	if(!LPCFG.url)		 LPCFG.url="";
	//通过url传入的参数
	var queryParams = {};
	var params__={};
	(function(){
		var params = Ext.parseQuery();
		params__ = params;
		for(var key in params){
			queryParams['query_' + key] = params[key];
		}
	})();
	
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
	svrFilter:[],
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
		if(LPCFG.enableSearchSvr!=false){
			this.searchPanel["svr"] = this.createSvrSearchPanel();
		}
		
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
		this.initGrid();
		this.setPagesize();	
		this.loadData();
		
		this.listenerKeybord();//绑定键盘事件
	},
	initBase:function(){
		var urls = LPCFG.url.split('?');
		var url = urls[0];
		this.params = Ext.parseQuery(LPCFG.url);
		var sort = [{field:'id',order:'desc'}];
		var baseParams = {
			nodeId:nodeId__,
			sort:Ext.encode(sort),
			fd:LPCFG.neededFields//需要输出的字段
		};

		if(this.params ) Ext.apply(baseParams,this.params);
		this.store=new Ext.data.Store({ 
			url : url,
			baseParams:baseParams,
			reader : new Ext.data.JsonReader({
					autoLoad:true,
					root : "data",
					totalProperty : "totalCount",
					successProperty : 'success',
					fields:LPCFG.neededFields?LPCFG.neededFields.split(','):['id'],
			}),
			remoteSort: false,
			successProperty : 'success',
			listeners: { 
				"loadexception":function(obj, options, response) {
					console.info('store loadexception, arguments:', arguments);
					var msg = "浏览器错误";
					if(response.responseText) {
						try{
							var ret = Ext.decode(response.responseText);
							msg = ret.message;
						}catch(ex){}
						
					}
					Ext.Msg.show({
						   title:'错误提示',
						   msg:msg,
						   buttons: Ext.Msg.OK,
						   animEl: 'elId',
						   minWidth:420,
						   icon: Ext.MessageBox.ERROR 
					});
				}
			}
		});
	
		try{
			if(params__.q) {//来自url的参数
				//this.svrFilter = Ext.decode(params__.q);
				//q为json字符串，转换为数组。例如q={id:1}转化为"1:id"存进数组this.svrFilter
				var jsonObj = Ext.decode(params__.q);
				for(var k in jsonObj){
					this.svrFilter.push(jsonObj[k]+':'+k)
				}
				//console.log(this.svrFilter)
			}
			if(this.params.q) {//来自数据源url的配置
				this.svrFilter = this.svrFilter.concat(this.params.q.split(','));
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
	loadData:function(){
		/*
		if(this.svrFilter.length>0) this.store.baseParams.q = this.svrFilter.join(',');
		this.store.baseParams.limit = this.pagesize;
		this.store.load({
			params:{start:0,limit:this.pagesize},
			callback:listMgr.storeLoadCallback
		});//初次搜索
		this.grid.getSelectionModel().selectFirstRow();
		*/	
		this.doSearch_svr();
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
		if( this.searchPanel.svr){
			for(var i=0;i<this.seachSvrItemsArr.length;i++){
				if(!this.seachSvrItemsArr[i]) continue;
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

	createValueCtrl:function(sItem,groupId){//创建值控件
		var fldCtrlItem = sItem;
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
		var btnSelFld=new Ext.form.Label({
			text:btnText,
			fieldName:sItem.field
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

		sItems.push({
			xtype:'tbbutton',
			text:'搜索',
			iconCls  :'searchBtn',
			style:'padding:0px 5px 0px 5px;',
			handler:function(){
				listMgr.doSearch_svr();
			}
		});	
		//清空按钮
		sItems.push({
			xtype:'tbbutton',
			text:'清空',
			style:'padding:0px 5px 0px 5px;',
			scope:this,
			handler:function(){
				var items = this.seachSvrItemsArr;
				for(var i=0;i<items.length;i++){
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
			plugins:new Ext.grid.plugins.AutoResize(),
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
	　　　　	'render'　:　function()　{
					//listMgr.pagerBar.render(grid.bbar);
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
	doSearch_svr:function (){
		//搜集搜索条件,以json格式post到服务端解析
		var q=[];
		var localSettingWhere=[];
		for(var i=0;i<this.seachSvrItemsArr.length;i++){
			localSettingWhere[i]=0;
			var item = this.seachSvrItemsArr[i];
			if(!item) continue;
			var qText = item[1].getValue()+'';
			var field = item[0].fieldName;
			//qText = qText.replace(/,/g,'|');
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
				qText = encodeURIComponent(qText);//服务端需要反编码
				q.push(qText + ':' + field);	
				if(window.localStorage && this.enableRemenberSearch){//本地存储
					localSettingWhere[i]={
						title:text,
						field:field,
						value:qText
					};
				}
			}
		}
		//本地存储用户输入的搜索条件
		
		if(window.localStorage && this.enableRemenberSearch){
			localStorage.setItem(Cookies.get('cmpp_cn') + '_listPage_search_' + 'nodeId-' + nodeId__+ 'formId-' + formId__+ 'listId-' + listId__,Ext.encode({searchType:'service',searchSetting:localSettingWhere}))
		};
		
		q = q.concat(this.svrFilter);
		listMgr.store.baseParams.q =q.join(',');
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
	from:'service',
	q:	"1:status,标题1:title"
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
	Ext.BLANK_IMAGE_URL = "./../res/js/ext2/resources/images/default/s.gif";
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
