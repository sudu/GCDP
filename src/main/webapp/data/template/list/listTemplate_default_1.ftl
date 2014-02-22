<!DOCTYPE html>
<html>
<head>
    <title>列表</title>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <link rel="stylesheet" type="text/css" href="../res/js/easyui/themes/bootstrap/easyui.css">
	<link rel="stylesheet" type="text/css" href="../res/css/runTime-easyui.css">
	<link rel="stylesheet" type="text/css" href="../res/js/easyui/themes/icon.css">
    <script type="text/javascript" src="../res/js/easyui/jquery.min.js"></script>
	<script type="text/javascript" src="../res/js/jquery/jquery.tmpl.min.js"></script>
	<script type="text/javascript" src="../res/js/jquery/jquery.extension.js"></script>
    <script type="text/javascript" src="../res/js/easyui/jquery.easyui.min.js"></script>
	<script type="text/javascript" src="../res/js/easyui/locale/easyui-lang-zh_CN.js"></script>
	<script type="text/javascript" src="../res/js/lib/juicer-min.js"></script>
	<script type="text/javascript" src="../res/js/easyui/src/jquery.textfield.js"></script>
	<script type="text/javascript" src="../res/js/easyui/src/jquery.radiogroup.js"></script>

	<style>
		.list-search-panel{
			padding:10px;
			height:auto;
		}
		.search-item{
			padding:7px 5px 7px 0;margin:0 5px;border:1px dotted
		}
	</style>
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
		var params = $.parseQuery();
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
<script>
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

var controlType__={
	textfield:'textfield',
	combo:'combobox',
	radiogroup:'radiogroup',
	treecombo:'combotree'
};
//搜索值控件的默认配置
var sValueControlsCfg__ = {
	combo:{
		inputTemplate:'<input type="text"/>',
		valueField:'value',   
		textField:'text',
		panelHeight:'auto'
		
	},	
	textfield:{
		width:100
	},
	radiogroup:{
		//vtype:'radiogroup',
		width:150
	},
	combotree:{
	
	}
}
</script>

<script>
/*删除前导空格*/ 
function leftTrim(ui){ 
        var notValid=/^\s/; 
        while(notValid.test(ui)){ 
                ui=ui.replace(notValid,"");
        } 
        return ui;
}
function myZhuanyi(str){
	var ret = str;
	//ret = ret.replace(/&/g,'&amp;');
	ret = ret.replace(/"/g,'&quot;');
	ret = ret.replace(/'/g,'&quot;');
	ret = ret.replace(/>/g,'&gt;');
	ret = ret.replace(/</g,'&lt;');
	return ret;
}	
function myUnZhuanyi(str){
	if(str){
		var ret = str;
		ret = ret.replace(/&quot;/g,'"');
		ret = ret.replace(/&quot;/g,"'");
		ret = ret.replace(/&gt;/g,'>');
		ret = ret.replace(/&lt;/g,'<');
		//ret = ret.replace(/&amp;/g,'&');		
		return ret;
	}else{
		return str;
	}
}	

//根据模板渲染内容
function renderField(coLPCFG){
	var tpl = leftTrim(coLPCFG.tpl);
	var tplValue="";
	var compiled_tpl;
	try{
		var tplJson =  eval("(" + tpl + ")");//json格式
		
		for(var funcName in tplJson){
			if(funcName=="tpl"){
				tplValue = tplJson.tpl;
			}else{
				var func =  tplJson[funcName];
				if(typeof func == "function"){
					juicer.register(funcName,func);
				}
			}
		}
	}catch(ex){
		//alert('模板格式有误。格式:{"tpl":"{=title!func1}",func1:function(value,rowData,rowIndex){return value;}}')
		tplValue = tpl;
	}
	if(tplValue=="") 
		return null;
	else
		compiled_tpl = juicer(tplValue);
	
	var isShowTip = coLPCFG.isShowTip;
	var tipTplValue = "";
	var compiled_tipTpl = null;
	coLPCFG.tipTpl = myUnZhuanyi(coLPCFG.tipTpl);
	coLPCFG.tipTpl = leftTrim(coLPCFG.tipTpl);

	if(coLPCFG.tipTpl) {
		try{
			var tipTplJson =  eval("(" + coLPCFG.tipTpl + ")");//json格式
			for(var funcName in tipTplJson){
				if(funcName=="tpl"){
					tipTplValue = tipTplJson.tpl;
				}else{
					var func =  tipTplJson[funcName];
					if(typeof func == "function"){
						juicer.register(funcName,func);
					}
				}
			}
		}catch(ex){
			tipTplValue = coLPCFG.tipTpl;
		}
		compiled_tipTpl = juicer(tipTplValue);
	}
	return function(value,rowData,rowIndex){
		$.extend(rowData,queryParams);//合并注入url传入的参数
		var text = value;
		if(tplValue) text = compiled_tpl.render(rowData);
		
		if(isShowTip){
			var tip = value;
			if(compiled_tipTpl){
				tip = compiled_tipTpl.render(rowData);	
				//tip = myZhuanyi(tip);
			}
			text = '<div class="easyui-tooltip" title="'+ tip +'">'+ text +'</div>';
		}
		return text;	
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

<body class="easyui-layout">  

<div  id="searchContainer" region="north" style="padding:5px;height:auto;background:#fafafa;min-height:30px;" bodyCls="list-search-panel">
	<select id="searchFrom" style="width:150px"></select>

</div>


<div  id="gridContainer" region="center">
	<table id="grid" fit="true" data-options="rownumbers:false,singleSelect:true,checkOnSelect:true,selectOnCheck:false,method:'get',striped:true,remoteSort:false"></table>
</div>	

<script type="text/javascript">

listMgr = {
	rowHeight:22,//列表页行高
	url:"xlist!data.jhtml",
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
		this.initSearchPanel();
		
		this.setPagesize();
		this.columns = this.initColumns();
		this.toolbarArr = this.initToolbar();
		this.grid = this.initGrid();
		
	},
	initBase:function(){
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
			$.messager.alert('提示',"来自url的参数解析出错。请检查参数格式是否正确。",'error');
		}
		
	},
	initSearchPanel:function(){
		this.initSearchFrom();
		this.initSearchItem();
		this.initSearchButton("db");
		this.initSearchSvrItem();
		this.initSearchButton("service");
	},
	//初始化搜索方式选择
	initSearchFrom:function(){
		var searchFromItems=[];
		var dbChecked = LPCFG.defaultSearch!='service';
		if(LPCFG.enableSearchDb!=false && dbSearchableArr__.length>0){//数据库搜索启用
			searchFromItems.push({
				"text":"从数据库搜",
				"value":"db",
				checked:dbChecked?'checked':null
			});
			if(LPCFG.enableSearchHistory!=false){
				searchFromItems.push({
					text:'从历史库搜',
					value:'bak',
					checked:false
				});
			}
		}
		if(LPCFG.enableSearchSvr!=false && svrSearchableArr__.length>0){
			searchFromItems.push({
				text:'从搜索引擎搜',
				value:'service',
				checked:!dbChecked
			});
		}
		if(searchFromItems.length>1){
			var searchFrom = $("#searchFrom");
			this.searchFromSelect = searchFrom;
			searchFrom.combo({
                editable:false,
				width:100,
				panelWidth:120,
				panelHeight:95
            });
			if(dbChecked){
				searchFrom.combo('setValue', "db").combo('setText', "从数据库搜");
			}else{
				searchFrom.combo('setValue', "service").combo('setText', "从搜索引擎搜");
			}
			var searchPanelListTpl = [
				'<div>',
					'<div style="color:#99BBE8;background:#fafafa;padding:5px;">请选择数据来源</div>',
					'{@each items as item}',
						'<input type="radio" name="from" value="{=item.value}" {@if item.checked}checked="checked"{@/if}><span>{=item.text}</span><br/>',
					'{@/each}',
				'</div>'
			].join("");
			var searchPanelListHtml = juicer(searchPanelListTpl,{items:searchFromItems});
			var searchPanelList = $(searchPanelListHtml).after(searchFrom).appendTo(searchFrom.combo('panel'));
			searchPanelList.find('input[type="radio"]').click(function(){
                var v = $(this).val();
				var oldFrom = listMgr.from;
				listMgr.from = v;
                var s = $(this).next('span').text();
                listMgr.searchFromSelect.combo('setValue', v).combo('setText', s).combo('hidePanel');
				
				//显示或隐藏控件
				if(listMgr.from==oldFrom) return;
				if((oldFrom=="db" && listMgr.from=="bak") || (oldFrom=="bak" && listMgr.from=="db"))
					return;
				
				var ct = $("#searchContainer");
				if(listMgr.from=="service"){
					ct.find(".db").hide();	
					ct.find(".service").show();	
				}else{
					ct.find(".db").show();	
					ct.find(".service").hide();	
				}
            });
		}else{
			$("#searchFrom").hide();
		}
		
	},
	searchControls:[],
	svrSearchControls:[],
	//初始化搜索项
	initSearchItem:function(){
		var container = $("#searchContainer");
		var fieldMenuTpl=[
			'<div>',
				'{@each items as item}',
					'<div class="search-item-field-item" data-value="{=item.field}">{=item.title}</div>',
				'{@/each}',
			'</div>'
		].join("");
		var fieldMenuHtml = juicer(fieldMenuTpl,{items:dbSearchableArr__});

		for(var i=0;i<LPCFG.search.length;i++){
			var sItem = LPCFG.search[i];
			if(sItem.enable!=false){
				var ct = $('<span class="search-item db">').appendTo(container);
				var field = $('<a href="javascript:void(0)" data-value="'+ sItem.field +'" data-index="'+ i +'" class="search-item-field">'+ (sItem.title||'选择搜索项') +'</a>').appendTo(ct);
				var fieldMenu = $(fieldMenuHtml).appendTo(ct);
				field.splitbutton({
					menu: fieldMenu
				});
				fieldMenu.find(".search-item-field-item").click($.proxy(function(e){
					var oldFieldName = this.data().value;
					var index = this.data().index;
					listMgr._splitMenuHandler.call(this,e);
					
					//更新op控件
					var newFieldName = this.data().value;
					if(newFieldName==oldFieldName) return;
					var oldOps = listMgr.getOpsByFType(oldFieldName);
					var newOps = listMgr.getOpsByFType(newFieldName);
					
					var controlItem = listMgr.searchControls[index];
					var opCtrl = controlItem.op;
					if(oldOps!=newOps){
						opCtrl.splitbutton("destroy");
						opCtrl = listMgr.createOp(this.parent(),{field:newFieldName});
						this.after(opCtrl);
						listMgr.searchControls[index].op = opCtrl;
					}
					//更新valueCtrl
					var valueCtrl = controlItem.value;
					var ctrlClassName = listMgr.getCtrlClassName(listMgr.getCtrlName(oldFieldName));
					valueCtrl[ctrlClassName]("destroy");
					valueCtrl = listMgr.createValueCtrl(this.parent(),{field:newFieldName});
					opCtrl.after(valueCtrl);
					listMgr.searchControls[index].value = valueCtrl;
					
				},field));
				var opCtrl = this.createOp(ct,sItem);
				var valueCtrl = this.createValueCtrl(ct,sItem);	
				this.searchControls.push({
					field:field,
					op:opCtrl,
					value:valueCtrl
				});
			}
		}
	},
	initSearchSvrItem:function(){
		var container = $("#searchContainer");
		var fieldMenuTpl=[
			'<div>',
				'{@each items as item}',
					'<div class="search-item-field-item" data-value="{=item.field}">{=item.title}</div>',
				'{@/each}',
			'</div>'
		].join("");
		var fieldMenuHtml = juicer(fieldMenuTpl,{items:dbSearchableArr__});
		
		for(var i=0;i<LPCFG.searchSvr.length;i++){
			var sItem = LPCFG.searchSvr[i];
			if(sItem.enable!=false){
				var ct = $('<span class="search-item service">').appendTo(container);
				var field = $('<a href="javascript:void(0)" data-value="'+ sItem.field +'" data-index="'+ i +'" class="search-item-field">'+ (sItem.title||'选择搜索项') +'</a>').appendTo(ct);
				var fieldMenu = $(fieldMenuHtml).appendTo(ct);
				field.splitbutton({
					menu: fieldMenu
				});
				fieldMenu.find(".search-item-field-item").click($.proxy(function(e){
					var oldFieldName = this.data().value;
					var index = this.data().index;
					listMgr._splitMenuHandler.call(this,e);
					
					//更新op控件
					var newFieldName = this.data().value;
					if(newFieldName==oldFieldName) return;
					var controlItem = listMgr.svrSearchControls[index];

					//更新valueCtrl
					var valueCtrl = controlItem.value;
					var ctrlClassName = listMgr.getCtrlClassName(listMgr.getCtrlName(oldFieldName));
					valueCtrl[ctrlClassName]("destroy");
					valueCtrl = listMgr.createValueCtrl(this.parent(),{field:newFieldName});
					controlItem.field.after(valueCtrl);
					listMgr.svrSearchControls[index].value = valueCtrl;
					
				},field));
				var valueCtrl = this.createValueCtrl(ct,sItem);	
				this.svrSearchControls.push({
					field:field,
					value:valueCtrl
				});
			}
		}
	},
	
	createOp:function(ct,sItem){//创建操作符控件
		var opText = sItem.op?opEnZh__[sItem.op]:'=';
		var opValue = sItem.op?sItem.op:'=';
		var ops = this.getOpsByFType(sItem.field);
		var opMenus=[];
		for(var i=0;i<ops.length;i++){
			var op = ops[i];
			opMenus.push({
				text         : opEnZh__[op]||'从',
				value        : op
			});
		}
		var menuTpl=[
			'<div>',
				'{@each items as item}',
					'<div class="search-item-op-item" data-value="{=item.value}">{=item.text}</div>',
				'{@/each}',
			'</div>'
		].join("");
		
		var menuHtml = juicer(menuTpl,{items:opMenus});
		var opMenu = $(menuHtml).appendTo(ct);
		var opCtrl = $('<a href="javascript:void(0)" class="search-item-op" data-value="' + opValue +'">'+ opText +'</a>').appendTo(ct);
		opCtrl.splitbutton({
			menu: opMenu[0]
		});
				
		opMenu.find(".search-item-op-item").click($.proxy(this._splitMenuHandler,opCtrl));
		return opCtrl;
	},
	
	changeOp:function(opCtrl,field){
		var opMenu = opCtrl.splitbutton("options").menu;
		opMenu.remove();
		var ct = opCtrl.parent();
		opCtrl.replaceWith
		
		this.createOp(ct,{field:field,})
	},
	_splitMenuHandler:function(e){
		var target = $(e.currentTarget);
		var value = target.data().value;
		this.find('.l-btn-text')[0].firstChild.textContent=target.text();
		this.data("value",value);
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
	getCtrlName:function(field){
		var fldCtrlItem = LPCFG.searchableFields[field];
		var ctrl = fldCtrlItem && fldCtrlItem.ctrl?fldCtrlItem.ctrl:'textfield';
		return ctrl;
	},
	getCtrlClassName:function(ctrl){
		return controlType__[ctrl];
	},
	//创建值控件
	createValueCtrl:function(ct,sItem){//创建值控件
		var ctrl = this.getCtrlName(sItem.field);
		var ctrlClassName = this.getCtrlClassName(ctrl);
		var fldCtrlItem = LPCFG.searchableFields[sItem.field];
		var ctrlCfg = {};
		$.extend(ctrlCfg,sValueControlsCfg__[ctrl]);
		if(fldCtrlItem && fldCtrlItem.width) ctrlCfg.width=fldCtrlItem.width;
		
		if(fldCtrlItem && fldCtrlItem.dataSource){
			if(fldCtrlItem.dataSourceType=='url'){
				ctrlCfg.url = fldCtrlItem.dataSource;
			}else{//json sql
				var dataSource = fldCtrlItem.dataSource;
				try{
					if(dataSource!='') dataSource = eval("(" + (dataSource) + ")");
					ctrlCfg.data = dataSource;
				}catch(ex){
					console.log(ex);
					alert("数据源数据格式有误");
				}
			}
		}
		var inputTpl = ctrlCfg.inputTemplate || '<input type="text"/>';
		var valueCtrl = $.tmpl(inputTpl, ctrlCfg).appendTo(ct);
		valueCtrl.attr("name",sItem.field + "_value");
		valueCtrl[ctrlClassName](ctrlCfg);
		return valueCtrl;
	},
	
	//初始化搜索按钮
	initSearchButton:function(from){
		if(from=="service"){
			var orderMenuItems=[];
			for(var i=0;i<svrSortableArr__.length;i++){  
				var fldCfg = {
					text         : '按<b>' + svrSortableArr__[i].title + '</b>顺序查询',
					field		 : svrSortableArr__[i].field,
					order        : "asc"
				};
				orderMenuItems.push(fldCfg);
				var fldCfg = {
					text         : '按<b>' + svrSortableArr__[i].title + '</b>倒序查询',
					field		 : svrSortableArr__[i].field,
					order        : "desc"
				};
				orderMenuItems.push(fldCfg);			
			}
			
			var ct = $("#searchContainer");
			if(orderMenuItems.length==0){
				var btnSearch = $('<a href="javascript:void(0)" class="easyui-linkbutton service"  iconCls="icon-search">搜索</a>').appendTo(ct);
				btnSearch.linkbutton({   
					plain:false  
				}).click(function(){
					listMgr.doSearch_svr();
				});
			}else{
				var btnSearch = $('<a href="javascript:void(0)" class="easyui-splitbutton service"  iconCls="icon-search">搜索</a>').appendTo(ct);
				var menuTpl=[
					'<div>',
						'{@each items as item}',
							'<div class="search-order-item" data-field="{=item.field}" data-sort="{=item.sort}">{=item.text}</div>',
						'{@/each}',
					'</div>'
				].join("");
				var menuHtml = juicer(menuTpl,{items:orderMenuItems});
				
				var menu = $(menuHtml).appendTo(ct);
				btnSearch.splitbutton({
					menu: menu
				});	
				menu.find(".search-order-item").click(function(e){
					var data = $(this).data();
					var field = data.field;
					var sort = data.sort;
					var value = {field:field,sort:sort};
					listMgr.doSearch_svr(JSON.stringify(value));
				});
				
			}
		}else{
			var orderMenuItems=[];
			for(var i=0;i<dbSortableArr__.length;i++){  
				var fldCfg = {
					text         : '按<b>' + dbSortableArr__[i].title + '</b>顺序查询',
					field		 : dbSortableArr__[i].field,
					order        : "asc"
				};
				orderMenuItems.push(fldCfg);
				var fldCfg = {
					text         : '按<b>' + dbSortableArr__[i].title + '</b>倒序查询',
					field		 : dbSortableArr__[i].field,
					order        : "desc"
				};
				orderMenuItems.push(fldCfg);			
			}
			
			var ct = $("#searchContainer");
			if(orderMenuItems.length==0){
				var btnSearch = $('<a href="javascript:void(0)" class="easyui-linkbutton db"  iconCls="icon-search">搜索</a>').appendTo(ct);
				btnSearch.linkbutton({   
					plain:false  
				}).click(function(){
					listMgr.doSearch_db();
				});
			}else{
				var btnSearch = $('<a href="javascript:void(0)" class="easyui-splitbutton db"  iconCls="icon-search">搜索</a>').appendTo(ct);
				var menuTpl=[
					'<div>',
						'{@each items as item}',
							'<div class="search-order-item" data-field="{=item.field}" data-sort="{=item.sort}">{=item.text}</div>',
						'{@/each}',
					'</div>'
				].join("");
				var menuHtml = juicer(menuTpl,{items:orderMenuItems});
				
				var menu = $(menuHtml).appendTo(ct);
				btnSearch.splitbutton({
					menu: menu
				});	
				menu.find(".search-order-item").click(function(e){
					var data = $(this).data();
					var field = data.field;
					var sort = data.sort;
					var value = {field:field,sort:sort};
					listMgr.doSearch_db(JSON.stringify(value));
				});
				
			}
		}
	},
	
	//初始化列
	initColumns:function(){
		var colunms=[];
		if(LPCFG.checkbox!==false){
			colunms.push({
				field:"ck",
				checkbox:true
			});
		}
		for(var i=0;i<LPCFG.columns.length;i++){
			var col = LPCFG.columns[i];
			if(col.isView!=false){
				var o = {
					title : col.title, 
					sortable : true, 
					field: col.field?col.field:'',
					align:col.align,
					fixed:LPCFG.isWidthFreeRow==i,
					resizable:true,
					formatter:renderField(col)
				};
				if(col.width)o.width = col.width;
				colunms.push(o);
			}		
		}
		return colunms;
	},
	
	//根据grid高度计算pagesize
	setPagesize:function(){
		if(LPCFG.autoPagesize){
			var gridBody= $('#gridContainer');
			this.pageSize = Math.floor((gridBody.innerHeight()-60-24)/this.rowHeight);
		}else{
			this.pagesize = LPCFG.pagesize;
		}
		
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
	initToolbar:function(){
		//初始化按钮
		var toolbarArr = [];
		if(LPCFG.buttons.add){
			toolbarArr.push({ 
				text : "增加", 
				iconCls:'icon-add',
				handler : this.addRecord 

			});
		}
		if(LPCFG.buttons.modify){
			toolbarArr.push({ 
				text : "修改", 
				iconCls : 'icon-edit', 
				handler : this.updateRecord 
			});
		}
		if(LPCFG.buttons.delete){
			toolbarArr.push({ 
				text : "删除", 
				iconCls : 'icon-remove', 
				handler : this.deleteRecord 
			});
		}	
		for(var i=0;i<LPCFG.buttons.ext.length;i++){
			var btnItem = LPCFG.buttons.ext[i];
			toolbarArr.push({ 
				text : btnItem.text, 
				iconCls : btnItem.iconCls, 
				id:'extBtn_' + btnItem.id,
				scope:btnItem,
				handler : this.getCustomBtnHandler(btnItem)
			});
		}
		return toolbarArr;
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
				svrFilter = eval("(" + LPCFG.filter4Svr + ")" );
			}catch(ex){
				console.log(ex);
			}
		}else{
			svrFilter = LPCFG.filter4Svr;
		}
		if(this.svrFilter){
			this.svrFilter = $.extend(svrFilter,this.svrFilter);
		}
		var qArr = [];
		for(var field in this.svrFilter){
			var value = this.svrFilter[field];
			qArr.push(escape(value) + ':' + field);
		}
		if(qArr.length>0) this.q = qArr.join(',');
		
		var grid = $('#grid').datagrid({
			url:this.url,toolbar:this.toolbarArr,pagination:true,"pageSize":this.pageSize,pageList:[this.pageSize],pageNumber:1,
			fitColumns:LPCFG.isWidthFreeRow!=-1,
			columns:[this.columns],
			loader:function(param,success,error){
				var that = $(this);
				var opts = that.datagrid("options");
				var newParam = $.extend({},param);
				delete newParam.rows;
				delete newParam.page;
				newParam.start = (param.page-1) * param.rows;
				newParam.limit = param.page * param.rows;
				$.ajax({
					type : opts.method,
					url : opts.url,
					data : newParam,
					dataType : "text",
					success : function (data) {//解析成grid控件需要的数据格式
						data = eval("(" + data + ")");
						success({
							total:data.totalCount,
							rows:data.data
						});
					},
					error : function () {
						error.apply(this, arguments);
					}
				});
			},
			onLoadSuccess:function(data){
				//if(data.total == 0) 
				console.log("onLoadSuccess");//cds
			},
			queryParams:{
				from:"db",
				fd:LPCFG.mustReturnFields.join(','),//需要输出的字段
				where:JSON.stringify(this.where),
				sort:JSON.stringify(this.sort),
				formId:formId__,
				listId:listId__,
				start:0, 
				limit:this.pageSize
			}
		});
		grid.datagrid("getPager").pagination({
			showPageList:false
		});
		return grid;
	},
			
	/*添加 修改 删除*/
	addRecord:function(){
		setActiveTab('../runtime/xform!render.jhtml?viewId='+ viewId__ +'&formId=' + formId__ + '&id=0&nodeId=' + nodeId__,'list_add_' + formId__ + '_' + viewId__,'添加');
	},
	updateRecord:function(){
		var selItems = listMgr.grid.datagrid("getChecked");
		if(selItems.length==1){
			setActiveTab('../runtime/xform!render.jhtml?viewId='+ viewId__ +'&formId=' + formId__ + '&id=' + selItems[0].id + '&nodeId=' + nodeId__,'list_add_' + formId__ + '_' + viewId__ + '_' + selItems[0].id,'修改');
		}else{
			$.messager.show({
				title:'提示',
				msg:'请选择一条记录',
				timeout:1000,
				showType:'fade',
				style:{
					right:'',
					top:document.body.scrollTop+document.documentElement.scrollTop,
					bottom:''
				}
			}).delay(1000).queue(function(){
				$(this).panel("close");
			});
		}
	},
	deleteRecord:function(){
		var selItems = listMgr.grid.datagrid("getChecked");
		if(selItems.length>0){
			$.messager.confirm('提示', '确定删除吗？', function(r){
				if (r){
					var ids=[];
					for(var i=0;i<selItems.length;i++){
						ids.push(selItems[i].id);
					}
					$.ajax({  
						url:'xlist!delete.jhtml?nodeId=' + nodeId__ + '&viewId='+ viewId__ +'&formId=' + formId__ + '&ids=' + ids.join(','), 
						method:"get",
						dataType:"json",						
						success:function(response){
							var ret = eval("(" + response + ")");
							if(ret.success){
								$.messager.show({
									title:'提示',
									msg:'删除成功',
									timeout:1000,
									showType:'fade',
									style:{
										right:'',
										top:document.body.scrollTop+document.documentElement.scrollTop,
										bottom:''
									}
								}).delay(1000).queue(function(){
									$(this).panel("close");
									listMgr.doSearch();
								});
							}else{
								$.messager.alert('错误提示',decodeURIComponent(ret.message),'error');
							}
						},
						error:function(XMLHttpRequest, textStatus, errorThrown){
							$.messager.alert('错误提示',errorThrown.message,'error');
						}
					});
				}
			});
		}else{
			$.messager.show({
				title:'提示',
				msg:'请至少选择一条记录',
				timeout:1000,
				showType:'fade',
				style:{
					right:'',
					top:document.body.scrollTop+document.documentElement.scrollTop,
					bottom:''
				}
			}).delay(1000).queue(function(){
				$(this).panel("close");
			});
		}
	},

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
$(document).ready(function(){

	listMgr.init();	

	///////////注入的JS/////////////	
	${extOnReadyJs!""}	
	
});
</script>		
${bodyInject!""}	
</body>
</html>	
