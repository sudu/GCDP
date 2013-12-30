/*
*Ext.ux.grid.GridPanel:CMPP封装的统一样式的列表页
*author:chengds
*date:2012.5.25
*依赖 Ext.grid.plugins.AutoResize.js
*/
//判断是否支持 text-overflow
function checkChrome(){
	var browserName = navigator.userAgent.toLowerCase();
	if(/chrome/i.test(browserName) && /webkit/i.test(browserName) && /mozilla/i.test(browserName))
		return true;
	else
		return false;
}
var isChrome = checkChrome();
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
	ret = ret.replace(/>/g,'&gt;');
	ret = ret.replace(/</g,'&lt;');
	return ret;
}	
function myUnZhuanyi(str){
	if(str){
		var ret = str;
		ret = ret.replace(/&quot;/g,'"');
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
	var tpl = null;
	coLPCFG.tpl = leftTrim(coLPCFG.tpl);
	if(coLPCFG.tpl && coLPCFG.tpl.substring(0,1)!='\'' && coLPCFG.tpl.substring(0,1)!='"') {
		if(coLPCFG.tpl) tpl = new Ext.XTemplate(coLPCFG.tpl);
	}else{
		var tplStr = 'new Ext.XTemplate(' + coLPCFG.tpl + ')';
		if(coLPCFG.tpl) eval('tpl=' + tplStr);
	}
	var width = coLPCFG.width;
	var isShowTip = coLPCFG.isShowTip;
	var tipTpl = null;
	coLPCFG.tipTpl = myUnZhuanyi(coLPCFG.tipTpl);
	coLPCFG.tipTpl = leftTrim(coLPCFG.tipTpl);

	if(coLPCFG.tipTpl) {
		if(coLPCFG.tipTpl.substring(0,1)!='\'' && coLPCFG.tipTpl.substring(0,1)!='"'){
			if(coLPCFG.tipTpl) tipTpl = new Ext.XTemplate(coLPCFG.tipTpl);
		}else{
			var tplStr = 'new Ext.XTemplate(' + coLPCFG.tipTpl + ')';
			if(coLPCFG.tipTpl) eval('tipTpl=' + tplStr);
		}
	}
	return function(value, metadata, record){
		
		var text = value;
		if(tpl) text = tpl.applyTemplate(record.data);
		var displayHtml=text;
		var style = "";
		var divTitle='';
		
		if(isShowTip){
			var tip = value;
			if(tipTpl){
				tip = tipTpl.applyTemplate(record.data);	
				tip = myZhuanyi(tip);
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
	

Ext.namespace("Ext.ux.grid");

Ext.ux.grid.GridPanel = function (config) {	
	Ext.ux.grid.GridPanel.superclass.constructor.call(this, config);
}
Ext.extend(Ext.ux.grid.GridPanel, Ext.grid.GridPanel, {
	/****默认配置**/
	trackMouseOver:true,
	anchor:'100%',
	stripeRows: true,//行交替颜色显示 斑马线效果
	loadMask:{ 
		msg:"数据正在加载中...." 
	}, 
	autoSizeColumns : true,
	autoScroll:true, 
	enableColumnHide:false,
	enableHdMenu: false,
	hasPageBar:true,
	//autoExpandColumn:1,
	height:500,
	autoExpandColumn:null,
	store:null,
	plugins:Ext.grid.plugins.AutoResize?new Ext.grid.plugins.AutoResize():null,
	pagesize:10,
	viewConfig: null,
	view: null, 
	iconCls:'icon-grid',
	frame:false,
	storeConfig:null,
	columnConfig:null,
	sm:null,
	columnModel:null,
	cm:null,
	bbar:null,
	pagerBar:null,
	/****/
	initComponent: function () {
		this.storeConfig = {
			url:'',
			remoteSort: false,
			successProperty : 'success',
			root : "data",
			totalProperty : "totalCount",
			fields:['id']
		};
		this.columnConfig={
			hasRowNumber:false,
			hasSelectionModel:true,
			colunms:[{
				header: "ID",
				sortable: true,
				dataIndex: "id",
				renderer: renderField({
					tpl:"{id}"
				}),
				align:"left",
				width:60
			}]
		};
		this.viewConfig = {
			emptyText: "<div>无数据</div>", 
			enableRowBody:true,//可以用两行tr来表示一行数据  
			showPreview:true,//初始显示预览效果,这个是自定义的属性  
			getRowClass : function(record, rowIndex, p, store){//CSS class name to add to the row.获得一行的css样式  
				if(this.showPreview){  
					return 'x-grid3-row-expanded';  
				}  
				return 'x-grid3-row-collapsed';  
			}  
		};
		this.view = new Ext.grid.GridView({  
			forceFit:false,  
		})
		Ext.applyDeep(this,this.initialConfig);
		if(this.autoExpandColumn) this.autoExpandColumn = 'columns-' + this.autoExpandColumn;
		this.setStore();
		if(this.hasPageBar) this.setPageBar();
		this.setColumn();
		Ext.ux.grid.GridPanel.superclass.initComponent.call(this);
	},
	/*
	onRender:function(ct,pos){
		Ext.ux.grid.GridPanel.superclass.onRender.call(this,ct,pos);		
	},
	*/
	setColumn:function(){
		var cfg = this.columnConfig;
        var columnHeaders = new Array();
        if(cfg.hasRowNumber) columnHeaders.push(new Ext.grid.RowNumberer());
        if(cfg.hasSelectionModel) {
			if(cfg.sm){
				this.sm = cfg.sm;
			}else{	
				this.sm = new Ext.grid.CheckboxSelectionModel({width :25,singleSelect:false});
			}
			columnHeaders.push(this.sm);
		}

        for (var i = 0; i < cfg.colunms.length; i++) {
            var col = cfg.colunms[i];
            if (col.isView === false) {
                continue;
            }
            columnHeaders.push({
				id:'columns-' + i,
                header: col.header||"列"+(i+1),
                sortable: col.sortable==false?false:true,
                dataIndex: col.dataIndex||"",
                renderer: renderField(col),
                hidden:col.hidden,
				align:col.align,
                width:col.width
            });
        }
        this.cm = new Ext.grid.ColumnModel(columnHeaders);
        this.cm.defaultSortable = true;
        this.columnModel = this.cm;
	},
	setPageBar:function(){
		var pagerBar = new Ext.PagingToolbar({ 
			pageSize : this.pagesize, 
			store : this.store, 
			displayMsg : '{0}-{1}/{2}',
			emptyMsg:"没有查询到数据", 
			firstText : "首页",
			prevText : "前一页",
			nextText : "下一页",
			lastText : "尾页",
			refreshText : "刷新",
			displayInfo : true 
		});
		this.pagerBar = pagerBar;
		this.bbar = pagerBar;
	},
	setStore:function(){
		var cfg = this.storeConfig;
		this.store=new Ext.data.Store({ 
			url : cfg.url,
			reader : new Ext.data.JsonReader({
				autoLoad:true,
				root : cfg.root,
				totalProperty : cfg.totalProperty,
				successProperty : cfg.successProperty,
				fields: cfg.fields
			}),
			remoteSort: cfg.remoteSort,
			successProperty :  cfg.successProperty,
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
						msg:msg?msg:response.responseText,
						buttons: Ext.Msg.OK,
						minWidth:420,
						icon: Ext.MessageBox.ERROR 
					});
				}
			}
		});
	}
	
	
		
});
Ext.reg('ux-GridPanel', Ext.ux.grid.GridPanel);

/*
*CMPP通用列表页Panel
*
*/
Ext.ux.ListPanel = function (config) {	
	//config.autoHeight=config.height?null:true;
	Ext.ux.ListPanel.superclass.constructor.call(this, config);
}
Ext.extend(Ext.ux.ListPanel,Ext.Panel, {
	frame:false,
	header :false,
	gridRendered:null,
	grid:null,
	//html:'<div style="width:100%;height:100%;background:gray;"></div',
	gridConfig:null,
	initComponent: function () {
		this.gridConfig = {rowHeight:26};
		this.gridRendered=false;
		Ext.applyDeep(this,this.initialConfig);
		Ext.ux.ListPanel.superclass.initComponent.call(this);
	},
	onRender:function(ct,pos){
		Ext.ux.ListPanel.superclass.onRender.call(this,ct,pos);	
		this.addListener("afterlayout",this.renderGrid,this);	
		this.addListener("resize",function(obj,adjWidth,adjHeight,rawWidth,rawHeight ) {
			if(this.grid) {
				this.grid.setHeight(adjHeight);//确保表格自适应
				this.setPageSize();
			}
		},this);	
	},
	renderGrid:function(){
		if(!this.gridRendered){
			this.gridRendered=true;
			var panelHeight = this.body.getHeight();
			this.gridConfig.renderTo=this.body;
			if(panelHeight<10)
				this.gridConfig.autoHeight = true;
			else
				this.gridConfig.height = panelHeight;
			this.grid = new Ext.ux.grid.GridPanel(this.gridConfig);
			this.setPageSize();
		}
	},
	setPageSize:function(){
		if(!this.initialConfig.gridConfig.pagesize && this.grid && this.grid.hasPageBar){//用户没有配置pagesize，则自动计算出
			this.grid.pagesize = Math.floor((this.grid.body.getHeight()-26)/this.grid.rowHeight);	
			this.grid.pagerBar.pageSize=this.grid.pagesize;
		}
	}
});
Ext.reg('listpanel', Ext.ux.ListPanel);