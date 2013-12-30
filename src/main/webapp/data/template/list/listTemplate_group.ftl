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
		.addField{background:url("./../res/js/ext2/resources/images/default/dd/drop-add.gif") left  no-repeat !important;}
		.delField{background:url("./../res/js/ext2/resources/images/default/my/del-form.gif") left  no-repeat !important;}
		.modifyField{background:url("./../res/js/ext2/resources/images/default/my/modify-view.gif") left  no-repeat !important;}
		.searchBtn{background:url("./../res/img/search.gif") left  no-repeat !important;}
		
		.x-grid3-cell-inner, .x-grid3-hd-inner {
			overflow: hidden;
			padding: 5px 3px 5px 5px;
			text-overflow: ellipsis;/*ellipsis clip*/
			white-space: nowrap;
		}

	</style>
 	<script type="text/javascript" src="./../res/js/ext2/adapter/ext/ext-base.js"></script>
    <script type="text/javascript" src="./../res/js/ext2/ext-all-debug.js"></script>
	<script type="text/javascript" src="./../res/js/ext2/ext-lang-zh_CN.js"></script>
	<script type="text/javascript" src="./../res/js/ext_base_extension.js"></script>
	<script type="text/javascript" src="./../res/js/controls/Ext.ux.RadioGroup.js"></script>
<script type="text/javascript">
	var listConfig__= ${listConfig!""};
	var searchConfig__ = ${searchConfig!""};//全文检索的配置
	var formId__ = #{formId!0};
	var listId__= #{listId!0};
	var nodeId__=#{nodeId!0};
	var viewId__ = listConfig__.viewId;
	
	var LCFG = listConfig__;
	var SCFG = searchConfig__;
	//数据预处理	
	var isCustomSql=LCFG.isCustomSql;//是否定制sql
	try{
		if(isCustomSql &&  typeof(LCFG.customSearch)=='string')LCFG.customSearch = Ext.decode(LCFG.customSearch);
	}catch(ex){
		console.log('Ext.decode出错。json格式不合法');
	}
	if(!LCFG.searchSvr) LCFG.searchSvr=[];
	LCFG.pagesize = parseInt(LCFG.pagesize);
	LCFG.autoPagesize = LCFG.autoPagesize==false?false:true;
	
	if(!LCFG.buttons)LCFG.buttons ={
		add:true,
		modify:true,
		"delete":true,
		ext:[]	
	};
	if(!LCFG.menus)LCFG.menus ={
		add:false,
		modify:true,
		"delete":true	
	};	
	if(!LCFG.search)LCFG.search =[];
	if(!LCFG.columns)LCFG.columns =[];
	if(!LCFG.mustReturnFields) LCFG.mustReturnFields=[];
		
	var fields_EnZh__ ={};
	var fieldsAllArr__ = ['id'];
	var dbSearchableArr__=[];
	var dbSortableArr__,svrSearchableArr__,svrSortableArr__;
	if(!isCustomSql){
		dbSearchableArr__ = SCFG.fieldsConfig.searchable;
		dbSortableArr__ = SCFG.fieldsConfig.sortable;
		svrSearchableArr__ = SCFG.searchConfig.searchable;
		svrSortableArr__ = SCFG.searchConfig.sortable;//目前不支持全文检索的排序功能
		for(var i=0;i<SCFG.fieldsConfig.fields.length;i++){
			var fld = SCFG.fieldsConfig.fields[i];
			fields_EnZh__[fld[0]] = {title:fld[1]=='' ? fld[0]:fld[1],ftype:fld[2]?fld[2]:''};
			fieldsAllArr__.push(fld[0]);
		}

	}else{
		for(var i=0;LCFG.customSearch && i<LCFG.customSearch.searchableFields.length;i++){
			var sFld = LCFG.customSearch.searchableFields[i];
			fields_EnZh__[sFld.field] = {title:sFld.title,ftype:''};
			fieldsAllArr__.push(sFld.field);
			dbSearchableArr__.push(sFld.field);
			dbSortableArr__=[];
		}
	}
	if(LCFG.mustReturnFields.length>0){//如果没有设置了需要的字段则使用全部字段
		fieldsAllArr__ = LCFG.mustReturnFields;
	}
	
	//通过url传入的参数
	var queryParams = {};
	(function(){
		var params = Ext.parseQuery();
		for(var key in params){
			queryParams['query_' + key] = params[key];
		}
	})();
		
		

//搜索区操作符列表框数据
var opStore = new Ext.data.SimpleStore({　
　　fields:['value','text'],　
　　data:[['=','等于(=)'],['like','匹配(like)'],['>','大于(>)'],['>=','大于或等于(>=)'],['<','小于(<)'],['<=','小于或等于(<=)'],['<>','不等于(<>)']]
});
var opEnZh__={
	'=':'=',
	'like':'包含',
	'not like':'不包含',
	'>':'>',
	'>=':'≥',
	'<':'<',
	'<=':'≤ ',
	'<>':'≠'
};
////数据库字段类型与操作符对应表///////////////
var ftype_op__ = {//[['INT','INT'],['FLOAT','FLOAT'],['DOUBLE','DOUBLE'],['CHAR','CHAR'],['VARCHAR','VARCHAR'],['TEXT','TEXT'],['mediumtext','mediumtext'],['DATETIME','DATETIME']],
	'int':['=','>','<','>=','<=','<>'],	
	'varchar':['like','not like','=','<>'],
	'text':['like','not like'],
	'char':[],
	'float':[],
	'double':[],
	'datetime':[],
	'mediumtext':[],
	'all':['=','like','not like','>','>=','<','<=','<>']
}
ftype_op__.float =ftype_op__.double =ftype_op__.datetime= ftype_op__.int;
ftype_op__.mediumtext = ftype_op__.text;
ftype_op__.char = ftype_op__.varchar;
/////////////////////////////

var andorEnZH__ ={
	'and':'并且','or':'或者'
}
//搜索值控件的默认配置
var sValueControlsCfg__ = {
	combo:{
		triggerAction:"all",
		valueField : 'value',
		displayField : 'text',
		editable  :false,
		mode:'local',
		width:120
	},
	textfield:{
		width:100
	},
	datefield:{
		width:100,
		format :'Y-m-d' 
	}
}
//判断是否支持 text-overflow
function checkChrome(){
	var browserName = navigator.userAgent.toLowerCase();
	if(/chrome/i.test(browserName) && /webkit/i.test(browserName) && /mozilla/i.test(browserName))
		return true;
	else
		return false;
}
var isChrome = checkChrome();
/*删除前导空格*/ 
function leftTrim(ui){ 
        var notValid=/^\s/; 
        while(notValid.test(ui)){ 
                ui=ui.replace(notValid,"");
        } 
        return ui;
}
//根据模板渲染内容
function renderField(colCfg){
	var tpl = null;
	colCfg.tpl = leftTrim(colCfg.tpl);
	if(colCfg.tpl && colCfg.tpl.substring(0,1)!='\'' && colCfg.tpl.substring(0,1)!='"') {
		if(colCfg.tpl) tpl = new Ext.XTemplate(colCfg.tpl);
	}else{
		var tplStr = 'new Ext.XTemplate(' + colCfg.tpl + ')';
		if(colCfg.tpl) eval('tpl=' + tplStr);
	}
	var width = colCfg.width;
	var isShowTip = colCfg.isShowTip;
	var tipTpl = null;
	colCfg.tipTpl = leftTrim(colCfg.tipTpl);
	
	
	if(colCfg.tipTpl && colCfg.tipTpl.substring(0,1)!='\'' && colCfg.tipTpl.substring(0,1)!='"') {
		if(colCfg.tipTpl) tipTpl = new Ext.XTemplate(colCfg.tipTpl);
	}else{
		var tplStr = 'new Ext.XTemplate(' + colCfg.tipTpl + ')';
		if(colCfg.tipTpl) eval('tipTpl=' + tplStr);
	}
	return function(value, metadata, record){
		
		Ext.apply(record.data,queryParams);//注入url传入的参数
		var text = value;
		if(tpl) text = tpl.applyTemplate(record.data);
		var displayHtml=text;
		var style = "";
		var divTitle='';
		
		if(isShowTip){
			var tip = value;
			if(tipTpl){
				tip = tipTpl.applyTemplate(record.data);	
			}
			metadata.attr = 'ext:qtitle=""' + ' ext:qtip="' + tip + '"'; 
			//divTitle=' title="' + value + '" ';
		}
		if(width) {
			if(!isChrome)
				displayHtml= '<div style="width:'+ width +'px;overflow:hidden;" ' + divTitle + '>'+ text +'</div>';
			else
				displayHtml=text;	
		}
		return displayHtml;
		
	}
}
	

function setActiveTab(url,tabId,title){
	if(top&& top.centerTabPanel){
		top.centerTabPanel.addIframe('tab_' + tabId,title ,url,window);
	}else{
		window.open(url);	
	}
}

openTab = function(url,title){
	if(!title) title='';
	if(top&& top.centerTabPanel){
		var _url = url;
		top.centerTabPanel.addIframe('tab_' + (new Date()).valueOf(),title ,_url);
	}else{
		window.open(url);	
	}
}

</script>	

${headInject!""}
</head>
<body>	

<script type="text/javascript">

listMgr = {
	grid:null,
	column:null,
	pagerBar:null,
	mnuContext:null,//右键菜单
	customBtnHandlerUrl:'xlist!runScript.jhtml',//自定义按钮脚本处理接口
	store:new Ext.data.GroupingStore({ 
		proxy : new Ext.data.HttpProxy({
			url : 'xlist!data.jhtml',
			method:'POST'
		}), 
		reader : new Ext.data.JsonReader({
				autoLoad:true,
				root : "data",
				totalProperty : "totalCount",
				successProperty : 'success',
				fields: fieldsAllArr__
		}),
		groupField:"author",
		sortInfo:{field:"author",direction:"DESC"},
		remoteSort: false,
		successProperty : 'success',
		listeners: { 
			"loadexception":function(obj, options, response) {
				console.info('store loadexception, arguments:', arguments);
				var ret = Ext.decode(response.responseText);  
				if(!ret.success){ 
					Ext.Msg.show({
						   title:'错误提示',
						   msg: ret.message,
						   buttons: Ext.Msg.OK,
						   animEl: 'elId',
						   minWidth:420,
						   icon: Ext.MessageBox.ERROR 
					});
				}
			}
		}
	}),
	pagerBar:null,
	toolbarArr :null,
	init:function(){
		var toolbarItemsCfgArr = [];
		if(LCFG.enableSearchDb!=false && LCFG.enableSearchSvr!=false  && !isCustomSql && dbSearchableArr__.length>0 && svrSearchableArr__.length>0){
			var searchPanelTogle = {
				xtype:'cycle',
				id:'btnSearchPanelTogle',
				showText: true,
				prependText: '',
				value:LCFG.defaultSearch=='service'?'service':'db',
				iconCls :'btnIconCls',
				style:'padding:0px 5px 0px 0px;',
				items: [{
					text:'从数据库搜索',
					value:'db',
					checked:LCFG.defaultSearch!='service'
				},{
					text:'从搜索服务搜索',
					value:'service',
					checked:LCFG.defaultSearch=='service'
				}],
				changeHandler:function(btn, item){
					if(item.value=='db'){
						listMgr.searchPanel_svr.el.fadeOut({
							useDisplay: true,
							callback:function(){
								listMgr.searchPanel_db.el.fadeIn({useDisplay: true}); 
							}
						}); 
					}else{
						listMgr.searchPanel_db.el.fadeOut({
							useDisplay: true,
							callback:function(){
								listMgr.searchPanel_svr.el.fadeIn({useDisplay: true}); 
							}
						}); 
					}
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
		if(LCFG.enableSearchDb!=false && dbSearchableArr__.length>0) {
			var	searchPanel_db_cfg ={
				xtype:'panel',
				layout:'table',
				id:'searchPanel_db',
				//height:30,
				//style:'display:block;',
				//style:'padding:0px 0px 0px 5px;',
				autoHeight:true,
				frame:true,
				border:false,
				//width:825,
				autoScroll:true,
				items:this.initDbSearchPanel()
			}; 	
			toolbarItemsCfgArr.push(searchPanel_db_cfg);
		}
		if(LCFG.enableSearchSvr!=false  && !isCustomSql && svrSearchableArr__.length>0){
			var searchPanel_svr_cfg = {
				xtype:'panel',
				layout:'table',
				id:'searchPanel_svr',
				//style:'padding:0px 0px 0px 5px;',
				autoHeight:true,
				frame:true,
				border:false,
				//width:825,
				autoScroll:true,
				items:this.initSvrSearchPanel()
			};		
			toolbarItemsCfgArr.push(searchPanel_svr_cfg);
		}
		
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
		
		this.searchPanel_db = Ext.getCmp('searchPanel_db');
		this.searchPanel_svr = Ext.getCmp('searchPanel_svr');
		if(this.searchPanel_svr && this.searchPanel_db) this.searchPanel_svr.el.setVisible(false);
		this.listenerKeybord();//绑定键盘事件
	},
	//根据grid高度计算pagesize
	setPagesize:function(){
		if(LCFG.autoPagesize){
			this.pagesize = Math.floor((listMgr.grid.body.getHeight()-26)/25);
		}else{
			this.pagesize =LCFG.pagesize;
		}
		listMgr.pagerBar.store.baseParams.limit	= this.pagesize;
		listMgr.pagerBar.pageSize = this.pagesize;
	},
	//绑定键盘事件
	listenerKeybord:function(){
		if( this.searchPanel_db){
			var searchPanel = this.searchPanel_db;
			var searchKeyObjs = searchPanel.find('name','searchKey');
			for(var i=0;i<searchKeyObjs.length;i++){
				new Ext.KeyMap(searchKeyObjs[i].el, {
					key: Ext.EventObject.ENTER,
					fn: function(){
						this.doSearch_db();
					},
					scope: this
				});
			}
			if( this.searchPanel_svr){
				searchPanel = this.searchPanel_svr;
				searchKeyObjs = searchPanel.find('name','searchKey');
				for(var i=0;i<searchKeyObjs.length;i++){
					new Ext.KeyMap(searchKeyObjs[i].el, {
						key: Ext.EventObject.ENTER,
						fn: function(){
							this.doSearch_svr();	
						},
						scope: this
					});
				}
			}
		}
	},
	initDbSearchPanel:function(){
		if(isCustomSql){
			return this.initCustomSearchPanel();
		}else{
			var searchPanel_db = [];
			for(var i=0;i<LCFG.search.length;i++){
				var sItem = LCFG.search[i];
				if(sItem.field!="" && !sItem.isHide){
					var ftitle = fields_EnZh__[sItem.field].title;
					searchPanel_db.push({
						xtype:'label',
						style:'margin:0 3px 0 5px',
						text:ftitle + ":"
					});

					var dataSource = sItem.dataSource;
					
					var sValueCfg = {
						xtype:sItem.value,
						name:'searchKey'
					};
					try{
						if(sItem.dataSource!='')dataSource = Ext.decode(sItem.dataSource);
					}catch(ex){
						console.log(ex);
					}
					if(dataSource){
						sValueCfg.dataSource = dataSource;
					}
					
					Ext.applyDeep(sValueCfg,sValueControlsCfg__[sItem.value]);
					searchPanel_db.push(sValueCfg);	
				}else if(!sItem.isHide){
					if(searchPanel_db.length>0){
						searchPanel_db.push({
							xtype:'cycle',
							showText: true,
							prependText: '',
							id:'cBtnAndor' + i,
							value:'and',
							items: [{
								text:'并且',
								value:'and',
								checked:true
							},{
								text:'或者',
								value:'or'
							}],
							changeHandler:function(btn, item){
								btn.value = item.value;
							}

						});
					}
					searchPanel_db.push(this.createMBtnField(dbSearchableArr__,i));
					searchPanel_db.push(this.createMBtnOp(i));
					searchPanel_db.push({
						xtype:'textfield',
						name:'searchKey',
						width:100
					});
				}			
			}
			if(LCFG.search.length==0){
				for(var i=0;i<3;i++){
					if(searchPanel_db.length>0){
						searchPanel_db.push({
							xtype:'cycle',
							showText: true,
							prependText: '',
							id:'cBtnAndor' + i,
							value:'and',
							items: [{
								text:'并且',
								value:'and',
								checked:true
							},{
								text:'或者',
								value:'or'
							}],
							changeHandler:function(btn, item){
								btn.value = item.value;
							}

						});
					}
					searchPanel_db.push(this.createMBtnField(dbSearchableArr__,i));
					searchPanel_db.push(this.createMBtnOp(i));
					searchPanel_db.push({
						xtype:'textfield',
						name:'searchKey',
						width:100
					});			
				}
			}
			//////////////////搜索按钮///////////////////////
			if(dbSortableArr__.length>0){
				var mbtnSearch = new Ext.Toolbar.MenuButton({
					text     : '搜索',
					iconCls  :'searchBtn',
					value 	 :'',
					//tooltip	 :'',
					style:'padding:0px 5px 0px 5px;',
					menu     : [],
					//minWidth :110,
					handler:function(){
						listMgr.doSearch_db();
					}
					
				});

				for(var i=0;i<dbSortableArr__.length;i++){  
					var fldCfg = {
						text         : '按<font color="green">' + fields_EnZh__[dbSortableArr__[i]].title + '</font>顺序查询',
						value        : {"field":dbSortableArr__[i],"order":"asc"},
						checked      : false,
						group		 :'mbtnSearch',
						handler:function(obj,e){
							listMgr.doSearch_db(this.value);
						}
					};
					mbtnSearch.menu.add(fldCfg);
					var fldCfg = {
						text         : '按<font color="green">' + fields_EnZh__[dbSortableArr__[i]].title + '</font>倒序查询',
						value        : {"field":dbSortableArr__[i],"order":"desc"},
						checked      : false,
						group		 :'mbtnSearch',
						handler:function(obj,e){
							listMgr.doSearch_db(this.value);
						}
					};
					mbtnSearch.menu.add(fldCfg);			
				}
				searchPanel_db.push(mbtnSearch);
			}else{
				searchPanel_db.push({
					xtype:'tbbutton',
					text:'搜索',
					iconCls  :'searchBtn',
					style:'padding:0px 5px 0px 5px;',
					handler:function(){
						listMgr.doSearch_db();
					}
				});	
			}
			
			return searchPanel_db;
		}
	},
	createMBtnField:function(searchableArr,index){
		var group='filter' + index;
		var id = index;
		var btn = new Ext.Toolbar.MenuButton({
			text     : '选择搜索项',
			tooltip  : "选择搜索项",
			id	 :'mBtnField' + index,
			menu     : []
		});
		for(var i=0;i<searchableArr.length;i++){  
			var fldCfg = {
				text         : fields_EnZh__[searchableArr[i]].title,
				value        : searchableArr[i],
				checked      : false,
				group		 :group,
				checkHandler :function(item, checked){
					if(checked){
						btn.setText(item.text);
						btn.value = item.value;
						var ftype = fields_EnZh__[item.value].ftype;
						listMgr.changeMBtnOP(index,ftype);
					}
				}
			};
			btn.menu.add(fldCfg);
		}
		return btn;
	},
	changeMBtnOP:function(index,ftype){
		var btn = Ext.getCmp('mBtnOp' + index);
		if(!btn) return;
		ftype = ftype.toLowerCase();
		var ops ;
		if(ftype && ftype_op__[ftype] ) 
			ops = ftype_op__[ftype];
		else 
			ops= ftype_op__.all;
		btn.menu.removeAll();	
		var group ='createMBtnOp' + index;
		for(var i=0;i<ops.length;i++){
			var op = ops[i];
			var menuCfg = {
				text         : opEnZh__[op],
				value        : op,
				checked      : i==0,
				group		 :group,
				checkHandler :function(item, checked){
					if(checked){
						btn.setText(item.text);
						btn.value = item.value;
					}
				}
			};
			btn.menu.add(menuCfg);
		}
		btn.setText(opEnZh__[ops[0]]);

	},
	
	createMBtnOp:function (index){
			var group ='createMBtnOp' + index;
			var btn = new Ext.Toolbar.MenuButton({
				text     : '包含',
				value 	 :'like',
				id:'mBtnOp' + index,
				tooltip  : "选择匹配方式",
				menu     : [],
				handler:function(){
					
				}
			});
			for(var op in opEnZh__){
				var menuCfg = {
					text         : opEnZh__[op],
					value        : op,
					checked      : op=='like',
					group		 :group,
					checkHandler :function(item, checked){
						if(checked){
							btn.setText(item.text);
							btn.value = item.value;
						}
					}
				};
				btn.menu.add(menuCfg);
			}
			
			return btn;
		},
	//初始化服务搜索panel
	initSvrSearchPanel:function(){
		if(isCustomSql) return [];
		var searchPanel = [];
		//定制的搜索
		if(!LCFG.searchSvr) LCFG.searchSvr=[];
		for(var i=0;i<LCFG.searchSvr.length;i++){
			var sItem = LCFG.searchSvr[i];
			if(sItem.field!="" && !sItem.isHide){
				var ftitle = fields_EnZh__[sItem.field].title;
				searchPanel.push({
					xtype:'label',
					style:'margin:0 3px 0 3px',
					text:ftitle + ":"
				});
							
				var dataSource = sItem.dataSource;
				var sValueCfg = {
					xtype:sItem.value,
					style:'margin:0 3px 0 0px',
					name:'searchKey'
				};
				try{
					if(sItem.dataSource!='') dataSource = Ext.decode(sItem.dataSource);
				}catch(ex){
					console.log(ex);
				}
				if(dataSource){
					sValueCfg.dataSource = dataSource;
				}
				Ext.applyDeep(sValueCfg,sValueControlsCfg__[sItem.value]);
				searchPanel.push(sValueCfg);	
				
			}else if(!sItem.isHide){
				//高级搜索
				searchPanel.push(this.createMBtnField(svrSearchableArr__,'_svr_'+ i));
				searchPanel.push({
					xtype:'label',
					style:'margin:0 3px 0 0px',
					text:':'
				});
				
				searchPanel.push({
					xtype:'textfield',
					width:120,
					name:'searchKey'
				});
			}
		}
		if(LCFG.searchSvr.length==0){
			for(var i=0;i<3;i++){
				searchPanel.push(this.createMBtnField(svrSearchableArr__,'_svr_'+ i));
				searchPanel.push({
					xtype:'label',
					style:'margin:0 3px 0 0px',
					text:':'
				});
				
				searchPanel.push({
					xtype:'textfield',
					width:120,
					name:'searchKey'
				});			
			}
		}

//////////////////搜索按钮///////////////////////
		if(svrSortableArr__.length>0){
			var mbtnSearch = new Ext.Toolbar.MenuButton({
				text     : '搜索',
				iconCls  :'searchBtn',
				value 	 :'db',
				//tooltip	 :'',
				style:'padding:0px 5px 0px 5px;',
				menu     : [],
				handler:function(){
					listMgr.doSearch_svr();
				}
				
			});

			for(var i=0;i<svrSortableArr__.length;i++){  
				var fldCfg = {
					text         : '按<font color="green">' + fields_EnZh__[svrSortableArr__[i]].title + '</font>顺序查询',
					value        : {"field":svrSortableArr__[i],"order":"asc"},
					checked      : false,
					group		 :'mbtnSearch',
					handler:function(obj,e){
						listMgr.doSearch_svr(this.value);
					}
				};
				mbtnSearch.menu.add(fldCfg);
				var fldCfg = {
					text         : '按<font color="green">' + fields_EnZh__[svrSortableArr__[i]].title + '</font>倒序查询',
					value        : {"field":svrSortableArr__[i],"order":"desc"},
					checked      : false,
					group		 :'mbtnSearch',
					handler:function(obj,e){
						listMgr.doSearch_svr(this.value);
					}
				};
				mbtnSearch.menu.add(fldCfg);			
			}	
			searchPanel.push(mbtnSearch);	
		}else{
			searchPanel.push({
				xtype:'tbbutton',
				text:'搜索',
				style:'padding:0px 5px 0px 5px;',
				iconCls  :'searchBtn',
				handler:function(){
					listMgr.doSearch_svr();
				}
			});	
		}
		return searchPanel;
	},	
	initCustomSearchPanel:function(){
		var searchPanel_db = [];

		var sItems = LCFG.customSearch.searchItem;
		if(sItems && sItems.length>0){
			for(var i=0;i<sItems.length;i++){
				var sItem = sItems[i];
				if(sItem.field!="" && !sItem.isHide){
					var ftitle = sItem.fieldTitle;
					searchPanel_db.push({
						xtype:'label',
						style:'margin:0 3px 0 5px',
						text:ftitle + ":"
					});

					var dataSource = sItem.dataSource;
					var sValueCfg = {
						xtype:sItem.value,
						name:'searchKey'
					};
					try{
						if(sItem.dataSource)dataSource = Ext.decode(sItem.dataSource);
					}catch(ex){
						console.log(ex);
					}
					if(dataSource){
						sValueCfg.dataSource = dataSource;
					}
					
					Ext.applyDeep(sValueCfg,sValueControlsCfg__[sItem.value]);
					searchPanel_db.push(sValueCfg);	
				}else if(!sItem.isHide){
					if(searchPanel_db.length>0){
						searchPanel_db.push({
							xtype:'cycle',
							showText: true,
							prependText: '',
							id:'cBtnAndor' + i,
							value:'and',
							items: [{
								text:'并且',
								value:'and',
								checked:true
							},{
								text:'或者',
								value:'or'
							}],
							changeHandler:function(btn, item){
								btn.value = item.value;
							}

						});
					}
					searchPanel_db.push(this.createMBtnField(dbSearchableArr__,i));
					searchPanel_db.push(this.createMBtnOp(i));
					searchPanel_db.push({
						xtype:'textfield',
						name:'searchKey',
						width:100
					});
				}			
			}
		}else{
			for(var i=0;i<3;i++){
				if(searchPanel_db.length>0){
					searchPanel_db.push({
						xtype:'cycle',
						showText: true,
						prependText: '',
						id:'cBtnAndor' + i,
						value:'and',
						items: [{
							text:'并且',
							value:'and',
							checked:true
						},{
							text:'或者',
							value:'or'
						}],
						changeHandler:function(btn, item){
							btn.value = item.value;
						}

					});
				}
				searchPanel_db.push(this.createMBtnField(dbSearchableArr__,i));
				searchPanel_db.push(this.createMBtnOp(i));
				searchPanel_db.push({
					xtype:'textfield',
					name:'searchKey',
					width:100
				});			
			}
		}
		//////////////////搜索按钮///////////////////////
		if(dbSortableArr__.length>0){
			var mbtnSearch = new Ext.Toolbar.MenuButton({
				text     : '搜索',
				iconCls  :'searchBtn',
				value 	 :'',
				//tooltip	 :'',
				style:'padding:0px 5px 0px 5px;',
				menu     : [],
				//minWidth :110,
				handler:function(){
					listMgr.doSearch_db();
				}
				
			});

			for(var i=0;i<dbSortableArr__.length;i++){  
				var fldCfg = {
					text         : '按<font color="green">' + fields_EnZh__[dbSortableArr__[i]].title + '</font>顺序查询',
					value        : {"field":dbSortableArr__[i],"order":"asc"},
					checked      : false,
					group		 :'mbtnSearch',
					handler:function(obj,e){
						listMgr.doSearch_db(this.value);
					}
				};
				mbtnSearch.menu.add(fldCfg);
				var fldCfg = {
					text         : '按<font color="green">' + fields_EnZh__[dbSortableArr__[i]].title + '</font>倒序查询',
					value        : {"field":dbSortableArr__[i],"order":"desc"},
					checked      : false,
					group		 :'mbtnSearch',
					handler:function(obj,e){
						listMgr.doSearch_db(this.value);
					}
				};
				mbtnSearch.menu.add(fldCfg);			
			}
			searchPanel_db.push(mbtnSearch);
		}else{
			searchPanel_db.push({
				xtype:'tbbutton',
				text:'搜索',
				iconCls  :'searchBtn',
				style:'padding:0px 5px 0px 5px;',
				handler:function(){
					listMgr.doSearch_db();
				}
			});	
		}
		
		return searchPanel_db;
	},
	initToolbar:function(){
		//初始化按钮
		this.toolbarArr = [];
		if(LCFG.buttons.add){
			this.toolbarArr.push({ 
				text : "增加", 
				iconCls : 'addField', 
				handler : this.addRecord 

			});
		}
		if(LCFG.buttons.modify){
			this.toolbarArr.push({ 
				text : "修改", 
				iconCls : 'modifyField', 
				handler : this.updateRecord 
			});
		}
		if(LCFG.buttons.delete){
			this.toolbarArr.push({ 
				text : "删除", 
				iconCls : 'delField', 
				handler : this.deleteRecord 
			});
		}	
		for(var i=0;i<LCFG.buttons.ext.length;i++){
			var btnItem = LCFG.buttons.ext[i];
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
		if(LCFG.menus.add){
			menuItemsArr.push({
				text:'添加',
				iconCls:'addField',
				handler:listMgr.addRecord
			});
		}
		if(LCFG.menus.modify){
			menuItemsArr.push({
				text:'修改',
				iconCls:'modifyField',
				handler:listMgr.updateRecord
			});
		}	
		if(LCFG.menus.delete){
			menuItemsArr.push({
				text:'删除',
				iconCls:'delField',
				handler:listMgr.deleteRecord
			});
		}

		var menuItemsArr2=[];
		if(LCFG.buttons.ext){
			for(var i=0;i<LCFG.buttons.ext.length;i++){
				var btn= LCFG.buttons.ext[i];
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
		var myBtnId = btn.id;
		//todo 执行客户端脚本
		var beforeJs;
		try{
			if(btn.js) beforeJs = new Function(btn.js);
		}catch(ex){
			console.info(ex);
		}
		return function(){
			if((beforeJs && beforeJs.call(listMgr)!=false) || !beforeJs){
				listMgr.customBtnHandler(myBtnId);
			}
		}
	},
	initGrid:function(){
		var gridCfg = {
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
			//autoHeight:true,
			height:this.datagridPanel.body.getHeight(),
			viewConfig: {
				emptyText: "<div>无数据</div>", 
				forceFit:true,//当行大小变化时始终填充满  
				enableRowBody:true,//可以用两行tr来表示一行数据  
				showPreview:true,//初始显示预览效果,这个是自定义的属性  
				getRowClass : function(record, rowIndex, p, store){//CSS class name to add to the row.获得一行的css样式  
					if(this.showPreview){  
						return 'x-grid3-row-expanded';  
					}  
					return 'x-grid3-row-collapsed';  
				}  
			},
			view: new Ext.grid.GroupingView({  
                forceFit:true  
            }), 
			iconCls:'icon-grid',
			frame:false,
			bbar : listMgr.pagerBar, 
			listeners　:　{
	　　　　	'render'　:　function()　{
					//listMgr.pagerBar.render(grid.bbar);
				},
				"rowcontextmenu":function(grid, rowIndex, event){
					 //event.stopEvent();
					 var selModel = grid.getSelectionModel();
					 if(!selModel.isSelected(rowIndex)){
						selModel.selectRow(rowIndex);
					 }
										 
					 //listMgr.mnuContext.showAt(event.xy);//暂时不启用
				}
			}
		};
		if(this.toolbarArr.length>0){
			gridCfg.tbar =this.toolbarArr;
		}
		
		var grid = new Ext.grid.GridPanel(gridCfg); 
		this.grid = grid;
		grid.render('placeholder');
	},
	loadData:function(){
		this.store.baseParams.formId =formId__;
		this.store.baseParams.listId =listId__;
		this.store.baseParams.limit = this.pagesize;
		this.store.baseParams.sort = '[{"field":"id","order":"desc"}]';
		this.store.load({
			params:{start:0,limit:this.pagesize},
			callback:listMgr.storeLoadCallback
		});//初次搜索,默认从DB搜索
		this.grid.getSelectionModel().selectFirstRow();
	},
	initColumn:function(){
		var colModelArr = [new Ext.grid.CheckboxSelectionModel()]; //new Ext.grid.RowNumberer(),//序号
		for(var i=0;i<LCFG.columns.length;i++){
			var col = LCFG.columns[i];
			if(col.isView==true){
				var o = {
					header : col.title, 
					sortable : true, 
					dataIndex : col.field,
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
		setActiveTab('./../runtime/xform!render.jhtml?viewId='+ viewId__ +'&formId=' + formId__ + '&id=0&nodeId=' + nodeId__,'list_add_' + formId__ + '_' + viewId__,'添加');
	},
	updateRecord:function(){
		var selItems = listMgr.grid.getSelectionModel().selections.items;
		if(selItems.length==1){
			setActiveTab('./../runtime/xform!render.jhtml?viewId='+ viewId__ +'&formId=' + formId__ + '&id=' + selItems[0].data.id + '&nodeId=' + nodeId__,'list_add_' + formId__ + '_' + viewId__ + '_' + selItems[0].data.id,'修改');
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
							url:'xlist!delete.jhtml?viewId='+ viewId__ +'&formId=' + formId__ + '&ids=' + ids.join(','), 
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
	customBtnHandler:function(btnId){
		var selItems = listMgr.grid.getSelectionModel().selections.items;
		if(selItems.length>0){
			var ids=[];
			for(var i=0;i<selItems.length;i++){
				ids.push(selItems[i].data.id);
			}
			
			Ext.Ajax.request({  
				url:listMgr.customBtnHandlerUrl,
				method:"post",
				params:{nodeId:nodeId__,formId:formId__,listId:listId__,ids:ids.join(','),scriptKey:btnId},
				waitTitle : "请稍候",  
				waitMsg : "正在处理中，请稍候......",  
				success:function(response,opts){
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
	//搜索
	doSearch_db:function (sortCfg){
		//搜集搜索条件,以json格式post到服务端解析
		var searchPanel = this.searchPanel_db;
		if(!searchPanel) return;
		var searchKeyObjs = searchPanel.find('name','searchKey');
		var filter={where:[],sort:[]};
		for(var i=0;i<searchKeyObjs.length;i++){
			var sItem = LCFG.search[i];
			var fld = sItem.field;
			if(fld!=''){
				var op = sItem.op;
				var andor = 'and';
				if(i!=0) andor = LCFG.search[i-1].andor;
				var value = '';
				if(searchKeyObjs[i]) value = searchKeyObjs[i].getValue();
				if(value!=""){
					filter.where.push({field:fld,op:op,value:value,andor:andor});
				}
			}else{
				var mBtnField = searchPanel.findById('mBtnField' + i);
				var cBtnAndor = searchPanel.findById('cBtnAndor' + i);
				var mBtnOp = searchPanel.findById('mBtnOp' + i);
				var fname = mBtnField.value;
				var op = mBtnOp.value;
				var andor = 'and';
				if(cBtnAndor) andor = cBtnAndor.value;
				var value = '';
				if(searchKeyObjs[i]) value = searchKeyObjs[i].getValue();
				if(fname && op && value!=""){
					filter.where.push({field:fname,op:op,value:value,andor:andor});
				}
			}
		}
		if(sortCfg){
			filter.sort.push({field:sortCfg.field,order:sortCfg.order});//todo
		}
		filter.sort.push({field:'id',order:'desc'});
		
		listMgr.store.baseParams ={
			from:'db',
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
		var searchPanel = this.searchPanel_svr;
		if(!searchPanel) return;
		var searchKeyObjs = searchPanel.find('name','searchKey');
		var q=[];
		for(var i=0;i<searchKeyObjs.length;i++){
			var sItem = LCFG.searchSvr[i];
			var fld = sItem.field;
			if(fld!=''){
				var value = '';
				if(searchKeyObjs[i])value = searchKeyObjs[i].getValue();
				
				if(fld && value!=""){
					q.push(value + ':' + fld);
				}
			}else{
				var mBtnField = searchPanel.findById('mBtnField_svr_' + i);
				var fname = mBtnField.value;
				var value = '';
				if(searchKeyObjs[i])value = searchKeyObjs[i].getValue();
				
				if(fname && value!=""){
					q.push(value + ':' + fname);
				}		
			}
		}

		var sort=[];
		
		if(sortCfg){
			sort.push({field:sortCfg.field,order:sortCfg.order});
		}
		sort.push({field:'id',order:'desc'});
		
		//http://localhost/search/do.action?type=select&start=0&len=10&fl=id,name&q=10:id,1280335088000-1280421494000:time,ifeng*:name&sort=id asc,date desc&wt=xml
		listMgr.store.baseParams ={
			from:'service',
			fd:fieldsAllArr__.join(','),//需要输出的字段
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
	storeLoadCallback:function(r,options,success){
		if(success && r.length==0){
			var gridbody = listMgr.grid.body.child('.x-grid3-body');
			var gridbodyParent = gridbody.findParentNode('.x-grid3-scroller');
			gridbodyParent = Ext.get(gridbodyParent);
			var tip = gridbody.createChild({
				tag:'img',
				src:'../res/img/nodata.gif'
			});
			var width = gridbodyParent.getWidth()-318;
			var height = gridbodyParent.getHeight()-73;
			tip.setStyle('margin',height/2 + 'px  ' + width/2 + 'px ' + height/2 + 'px  ' + width/2 + 'px ');
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

</script>	
	
<script type="text/javascript">
Ext.onReady(function(){
	Ext.BLANK_IMAGE_URL = "./../res/js/ext2/resources/images/default/s.gif";
	Ext.QuickTips.init();
	Ext.form.Field.prototype.msgTarget = 'qtip';
	
	listMgr.init();		
});
</script>		
${bodyInject!""}	
</body>
</html>	
